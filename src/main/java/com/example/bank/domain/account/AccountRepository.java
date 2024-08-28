package com.example.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    // jpa query method
    // findByNumber -> select * from account where number = :number
    
    // account.getUser().getId() <-- Lazy로딩으로 들고오는게 더 좋음 그러므로 join fetch 사용 x 
    // id를 제외한 나머지를 조회할때는 쿼리가 두번 나가므로 아래의 join fetch 사용 권장
    
    //  join fetch를 하면 조인해서 객체에 값을 미리 가져올수 있다.
    // @Query("SELECT ac FROM Account ac JOIN FETCH ac.user u WHERE ac.number = :number")
    Optional<Account> findByNumber(Long number);

    // jpa query method
    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
