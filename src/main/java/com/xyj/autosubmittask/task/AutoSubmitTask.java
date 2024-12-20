package com.xyj.autosubmittask.task;

import com.xyj.autosubmittask.service.AutoSubmitService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author xuyunjie
 */
@Component
public class AutoSubmitTask {

    @Resource
    private AutoSubmitService autoSubmitService;

    // 每天下午17:50执行
    @Scheduled(cron = "0 0 18 * * ?")
    public void submitTask() {
        System.out.println("start auto submit task");
        autoSubmitService.submitTask();
    }


}
