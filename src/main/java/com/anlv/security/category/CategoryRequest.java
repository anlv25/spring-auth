package com.anlv.security.category;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private Long parentId;
}