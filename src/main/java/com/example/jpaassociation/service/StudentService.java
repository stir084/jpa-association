package com.example.jpaassociation.service;

import com.example.jpaassociation.domain.Student;
import com.example.jpaassociation.domain.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentService {
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager em;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public void findAllSchoolNames(){
        System.out.println(em.getDelegate());
        List<Student> students = studentRepository.findAll();
        getSchoolName(students);
    }

    private List<String> getSchoolName(List<Student> students){
        System.out.println(em.getDelegate());
        return students.stream()
                .map(a -> a.getSchool().getName())
                .collect(Collectors.toList());
    }
//    /**
//     * Lazy Load를 수행하기 위해 메소드를 별도로 생성
//     */
//    private List<String> extractSubjectNames(List<Student> students){
//
//        return students.stream()
//                .map(a -> a.getSchool().getName())
//                .collect(Collectors.toList());
//    }
}
