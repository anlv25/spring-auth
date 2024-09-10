package com.anlv.security.rule;

import com.anlv.security.role.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Data
@Entity
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

}
