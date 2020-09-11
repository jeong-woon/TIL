package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

// 트랜잭션을 readonly로 설정해서 라이트 락을 쓰지 않아서 성능의 이점을 가져오자??
@Transactional(readOnly=true) // 이거 안해주면 우리가 만든 것은 트랜잭션 처리가 안됨.
public interface AccountRepository extends JpaRepository<Account, Long>  {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String nickname);
}
