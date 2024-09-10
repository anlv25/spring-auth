package com.anlv.security.rule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRedisRepository extends CrudRepository<RuleRedis, Long> {
}
