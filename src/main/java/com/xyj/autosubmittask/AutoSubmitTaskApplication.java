package com.xyj.autosubmittask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author xuyunjie
 */
@SpringBootApplication
@EnableScheduling
public class AutoSubmitTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoSubmitTaskApplication.class, args);
    }

}
