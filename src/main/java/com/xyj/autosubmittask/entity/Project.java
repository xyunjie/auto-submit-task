package com.xyj.autosubmittask.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Project {
    private int id;
    private String name;
    private String nameWithNamespace;
    private String path;
    private String pathWithNamespace;
    private String createdAt;
    private String defaultBranch;
    private String sshUrlToRepo;
    private String httpUrlToRepo;
    private String webUrl;
    private String readmeUrl;
    private int forksCount;
    private int starCount;
    private String lastActivityAt;
    private String containerRegistryImagePrefix;
    private boolean packagesEnabled;
    private boolean emptyRepo;
    private boolean archived;
    private String visibility;
    private boolean issuesEnabled;
    private boolean mergeRequestsEnabled;
    private boolean wikiEnabled;
    private boolean jobsEnabled;
    private boolean snippetsEnabled;
    private boolean containerRegistryEnabled;
}
