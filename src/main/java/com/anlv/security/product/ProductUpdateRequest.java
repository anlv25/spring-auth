package com.anlv.security.product;

import com.anlv.security.category.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Category category;
    private List<MultipartFile> newImages;
    private List<Long> imagesToRemove;
}