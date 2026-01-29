package com.broadridge.brx.matmapparser.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class SpecFile {

    private String filename;
    private String lastCommitter;
    private Date lastCommittedAt;
    private Content content;

}
