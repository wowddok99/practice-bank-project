package com.example.bank.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    // save - 이미 만들어져 있음. 누가? -> jpaRepository // 즉, test 할 이유가 없음

    //select*from user where username = ?
    Optional<User> findByUsername(String username); //Jpa NameQuery 작동

}
