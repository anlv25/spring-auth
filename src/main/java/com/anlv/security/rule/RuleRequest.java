package com.anlv.security.rule;

import lombok.Data;

@Data
public class RuleRequest {
    private String exp_path;
    private String exp_path_fe;
    private Long roleId;
    private boolean createRequest;
    private boolean updateRequest;
    private boolean readRequest;
    private boolean deleteRequest;
}
