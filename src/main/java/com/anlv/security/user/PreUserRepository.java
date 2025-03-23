package com.anlv.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreUserRepository extends JpaRepository<PreUser, Long> {

  Optional<PreUser> findByEmail(String email);
  void deleteAllByEmail(String email);
}
