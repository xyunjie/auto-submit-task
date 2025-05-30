package com.xyj.autosubmittask.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xuyunjie
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    private String gitToken;
    private String gitUserId;
    private String taskToken;
    private String taskUrl;
    private String getTaskUrl;
    private String taskUserId;
    private boolean isPush;
    private String pushUrl;
}
