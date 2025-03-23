package com.anlv.security.token;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Long> {

  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(Long id);

  @Modifying
  @Query(value = """
      delete from Token t\s
      where t.user.id = :id and (t.expired = true or t.revoked = true)\s
      """)
  void deleteAllInValidTokenByUser(Long id);

  Optional<Token> findByToken(String token);
}
