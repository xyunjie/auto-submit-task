package com.xyj.autosubmittask.controller;

import com.xyj.autosubmittask.service.AutoSubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class AutoSubmitController {

    @Resource
    private AutoSubmitService autoSubmitService;

    @GetMapping("submit")
    public String submitTask() {
        autoSubmitService.submitTask();
        return "success";
    }

}
