package com.xyj.autosubmittask.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xyj.autosubmittask.entity.GitEvent;
import com.xyj.autosubmittask.entity.Project;
import com.xyj.autosubmittask.entity.TaskConfigEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xuyunjie
 */
@Service
public class AutoSubmitService {

    @Resource
    private TaskConfigEntity taskConfigEntity;

    private static final Map<String, String> SUBMIT_TAG_MAP = new HashMap<>();

    static {
        SUBMIT_TAG_MAP.put("feat", "添加🚀");
        SUBMIT_TAG_MAP.put("fix", "修复🚨");
        SUBMIT_TAG_MAP.put("refactor", "重构");
        SUBMIT_TAG_MAP.put("update", "更新💓");
        SUBMIT_TAG_MAP.put("perf", "优化");
        SUBMIT_TAG_MAP.put("test", "测试");
        SUBMIT_TAG_MAP.put("chore", "构建");
        SUBMIT_TAG_MAP.put("commit", "提交🚀");
        SUBMIT_TAG_MAP.put("release", "发布🍁");
    }

    public void submitTask() {
        // 获取配置信息
        String format = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String url = String.format("https://git.lingman.tech:8888/api/v4/users/%s/events?after=%s&per_page=100&sort=asc", taskConfigEntity.getGitUserId(), format);
        String body = HttpUtil.createGet(url).header("PRIVATE-TOKEN", taskConfigEntity.getGitToken()).execute().body();
        List<GitEvent> gitEvents = JSONUtil.toList(body, GitEvent.class);

        if (CollUtil.isEmpty(gitEvents)) {
            return; // 无提交记录
        }

        // 获取项目ID和项目名称映射
        List<Integer> projectIds = gitEvents.stream().map(GitEvent::getProjectId).distinct().toList();
        Map<Integer, String> projectMap = fetchProjectNames(projectIds);

        // 生成日报内容
        String reportContent = generateReport(gitEvents, projectMap);
        if (StrUtil.isBlank(reportContent)) {
            return; // 无内容生成
        }

        // 提交日报
        submitDailyReport(reportContent);
        System.out.println(reportContent);
    }

    private Map<Integer, String> fetchProjectNames(List<Integer> projectIds) {
        Map<Integer, String> projectMap = new HashMap<>();
        for (Integer projectId : projectIds) {
            String projectUrl = String.format("https://git.lingman.tech:8888/api/v4/projects/%d", projectId);
            String projectBody = HttpUtil.createGet(projectUrl).header("PRIVATE-TOKEN", taskConfigEntity.getGitToken()).execute().body();
            Project project = JSONUtil.toBean(projectBody, Project.class);
            projectMap.put(projectId, project.getName());
        }
        return projectMap;
    }

    private String generateReport(List<GitEvent> gitEvents, Map<Integer, String> projectMap) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, List<GitEvent>> eventsByProject = gitEvents.stream().collect(Collectors.groupingBy(GitEvent::getProjectId));

        Set<String> globalContentSet = new HashSet<>();
        int projectCount = 1;
        for (Map.Entry<Integer, List<GitEvent>> entry : eventsByProject.entrySet()) {
            Integer projectId = entry.getKey();
            List<GitEvent> events = entry.getValue();

            if (CollUtil.isEmpty(events)) {
                continue;
            }

            List<GitEvent.PushData> pushDataList = events.stream()
                    .map(GitEvent::getPushData)
                    .filter(data -> data != null && data.getCommitTitle() != null && !data.getCommitTitle().contains("Merge remote-tracking"))
                    .toList();

            if (CollUtil.isEmpty(pushDataList)) {
                continue;
            }

            sb.append(projectCount++).append(". ").append(projectMap.get(projectId)).append(":\n");
            Map<String, List<String>> tagMap = extractCommitMessages(events);

            // 生成标签信息
            for (Map.Entry<String, List<String>> tagEntry : tagMap.entrySet()) {
                String tag = SUBMIT_TAG_MAP.getOrDefault(tagEntry.getKey(), tagEntry.getKey());
                sb.append("\t").append(tag).append(": \n");

                // 子项序号
                int subItemCount = 1;
                for (String content : tagEntry.getValue()) {
                    // 全局去重检查
                    if (globalContentSet.contains(content)) {
                        continue;
                    }
                    globalContentSet.add(content);
                    sb.append("\t\t").append(subItemCount++).append(". ")
                            .append(StrUtil.isBlank(content) ? "修改BUG" : content).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private Map<String, List<String>> extractCommitMessages(List<GitEvent> events) {
        Map<String, List<String>> tagMap = new HashMap<>();

        for (GitEvent event : events) {
            GitEvent.PushData pushData = event.getPushData();
            if (pushData == null || pushData.getCommitTitle() == null || pushData.getCommitTitle().contains("Merge remote-tracking")) {
                continue;
            }

            String cleanedTitle = pushData.getCommitTitle().replace(" ", "");
            String[] split = cleanedTitle.split("[:：]");
            String tag = split[0];
            String content = split.length > 1 ? split[1] : "";

            tagMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(content);
        }
        return tagMap;
    }

    private void submitDailyReport(String reportContent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("contentToday", reportContent);
        jsonObject.set("contentProblem", "");
        jsonObject.set("contentTommorow", "");
        jsonObject.set("day", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        jsonObject.set("createdBy", taskConfigEntity.getTaskUserId());
        jsonObject.set("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String taskInfo = HttpUtil.createGet(taskConfigEntity.getGetTaskUrl()).header("token", taskConfigEntity.getTaskToken()).execute().body();
        String taskId = Optional.ofNullable(JSONUtil.parseObj(taskInfo).getJSONObject("data")).map(obj -> obj.getStr("id")).orElse("");
        jsonObject.set("id", taskId);
        HttpResponse response = HttpUtil.createPost(taskConfigEntity.getTaskUrl()).header("token", taskConfigEntity.getTaskToken()).body(JSONUtil.toJsonStr(jsonObject)).execute();
        handlePushNotification(response, reportContent);
    }

    private void handlePushNotification(HttpResponse response, String reportContent) {
        if (taskConfigEntity.getIsPush()) {
            JSONObject pushJson = new JSONObject();
            pushJson.set("title", response.isOk() ? "日报提交成功" : "日报提交失败");
            pushJson.set("desp", response.isOk() ? reportContent.replace("\n", "\n\n") : response.body());
            pushJson.set("tags", "日报");
            pushJson.set("short", response.isOk() ? "日报提交成功！" : "日报提交失败！");
            HttpUtil.post(taskConfigEntity.getPushUrl(), pushJson);
        }
    }


}
