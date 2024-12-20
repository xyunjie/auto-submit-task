package com.xyj.autosubmittask.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GitEvent {
    private int id;
    private int projectId;
    private String actionName;
    private int authorId;
    private String createdAt;
    private Author author;
    private boolean imported;
    private String importedFrom;
    private PushData pushData;
    private String authorUsername;

    // Getters and Setters

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Author {
        private int id;
        private String username;
        private String name;
        private String state;
        private boolean locked;
        private String avatarUrl;
        private String webUrl;

        // Getters and Setters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PushData {
        private int commitCount;
        private String action;
        private String refType;
        private String commitFrom;
        private String commitTo;
        private String ref;
        private String commitTitle;

        // Getters and Setters
    }
}
