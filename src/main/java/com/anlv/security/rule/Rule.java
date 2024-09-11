package com.anlv.security.rule;

import com.anlv.security.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Rule {
    @Id
    @GeneratedValue
    private Long id;

    private String exp_path;
    private String exp_path_fe;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private boolean createRequest;

    private boolean updateRequest;

    private boolean ReadRequest;

    private boolean DeleteRequest;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long lastModifiedBy;


}
