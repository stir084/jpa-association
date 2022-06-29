package com.example.jpaassociation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.security.auth.Subject;

/**
 * Created by jojoldu@gmail.com on 2017. 7. 21.
 * Blog : http://jojoldu.tistory.com
 * Github : https://github.com/jojoldu
 */

public interface StudentRepository extends JpaRepository<Student, Long> {
}
