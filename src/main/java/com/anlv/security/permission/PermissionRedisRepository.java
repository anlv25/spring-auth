package com.anlv.security.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRedisRepository extends CrudRepository<PermissionRedis, Long> {
}
