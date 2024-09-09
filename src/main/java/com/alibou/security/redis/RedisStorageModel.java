package com.alibou.security.redis;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RedisStorageModel {
    private String tblName;
    private String key;
    private Object value;
}
