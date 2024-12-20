package com.xyj.autosubmittask.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuyunjie
 */
@Data
@Component
@ConfigurationProperties(prefix = "task")
public class TaskConfigEntity {
    @Value("${task.git-token}")
    private String gitToken;
    @Value("${task.git-user-id}")
    private String gitUserId;
    @Value("${task.task-token}")
    private String taskToken;
    @Value("${task.task-url}")
    private String taskUrl;
    @Value("${task.get-task-url}")
    private String getTaskUrl;
    @Value("${task.task-user-id}")
    private String taskUserId;
    @Value("${task.push-url}")
    private String pushUrl;
    @Value("${task.is-push}")
    private Boolean isPush;

}
