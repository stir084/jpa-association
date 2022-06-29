package com.example.jpaassociation.service;

import com.example.jpaassociation.domain.School;
import com.example.jpaassociation.domain.SchoolRepository;
import com.example.jpaassociation.domain.Student;
import com.example.jpaassociation.domain.StudentRepository;
import org.hibernate.TransientPropertyValueException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.security.auth.Subject;
import javax.transaction.Transactional;
import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SchoolServiceTest {
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EntityManager em;

    @After
    public void cleanAll() {
        schoolRepository.deleteAll();
        studentRepository.deleteAll();
    }
    @Before
    public void setup() {
        List<School> schools = new ArrayList<>();

        for(int i=0; i<5; i++){
            School school = new School("school"+i);

            school.addStudent(new Student("Lee"+i));
            school.addStudent(new Student("Kim"+i));

            schools.add(school);
        }
        schoolRepository.saveAll(schools);
    }

    @Test
    public void School조회() throws Exception {
        School school = schoolRepository.findById(1L).get();
        System.out.println(school.getName());
    }

    @Test
    public void School과Student조회() throws Exception {
        School school = schoolRepository.findById(1L).get();
        Student student = studentRepository.findById(1L).get();
        System.out.println(school.getName());
        System.out.println(student.getName());
    }

    @Test
    public void School로부터Student조회() throws Exception {
        School school = schoolRepository.findById(1L).get();

        List<Student> studentList = school.getStudents();
        for(Student student: studentList){
            System.out.println(student.getName());
        }
    }

    @Test
    public void Student로부터School조회() throws Exception {
        Student student = studentRepository.findById(1L).get();
        School school = student.getSchool();
        System.out.println(school.getName());
    }

    @Test
    public void TransientPropertyValueException발생() throws Exception {

        School school = new School();
        school.setName("MySchool");

        Student student = new Student();
        student.setName("stir");
        student.setSchool(school);

        schoolRepository.save(school);
        //TransientPropertyValueException(임시 프로퍼티 값 예외 발생)
        //Student 객체 저장 시 객체 안에 저장되지 않은 객체(Student 내 School 객체)가 저장되려고 할 때 발생한다.
    }
    @Test
    public void ConstraintViolationException발생() throws Exception {

        School school = new School();
        school.setName("MySchool");


        Student student = new Student();
        student.setName("stir");
        student.setSchool(school);
        //student.getSchool().getStudents().add(student);

        schoolRepository.save(school);
        studentRepository.save(student);
        //DataIntegrityViolationException의 ConstraintViolationException 발생
        //제약 조건 위배 시 발생하는 예외
        //save까진 잘돌아가지만 @After에서 school 삭제 시 발생한다.
        //@After 까지도 @Transactional의 영향을 받기 때문에 @After가 종료되어야만 비로소 @Transactional이 종료되는데
        //종료될 시점 전에는 영속성 컨텍스트 내에서만 판단한다.
        //School 부모를 삭제했을 때 cascade로 Student 자식들을 먼저 모두 삭제하고 부모를 삭제하게 되는데,
        //school입장에선 student에 대한 정보가 존재하지 않기 때문에 school부터 삭제하려 드는데, 이때 이미 영속성 컨텍스트 내에
        //외래키로 student가 존재하다보니 외래키만 냅두고 기본키를 삭제할 수가 없어서 발생하는 에러.

        //삭제를 트랜잭션 내에서 하지 않고 save만 일단 해두고 후에 다른 transaction에서 deleteAll을 하면 정상적으로 지워질 것이다.
        //바로 삭제하고 싶다면 아래처럼 사용 권장.
    }
    @Test
    public void ConstraintViolationException제거() throws Exception {

        School school = new School();
        school.setName("MySchool");


        Student student = new Student();
        student.setName("stir");
        student.setSchool(school);
        student.getSchool().getStudents().add(student); //School에도 student에 대한 정보 추가

        schoolRepository.save(school);
        studentRepository.save(student);

    }

    @Test
    public void n더하기1예제() throws Exception {

        System.out.println("== start ==");
        List<School> schoolList = schoolRepository.findAll();
        System.out.println("== find all ==");
        for(School school: schoolList){
            System.out.println(school.getStudents().get(0).getName());
        }
        //@Transactional이 붙은 테스트 코드에서는 N+1이 발생하지 않는다..?
        //안가져왔따는건 1차캐시에서 가져왔다는건데...
        //1차캐시에 들어갈만한 사건이 있었나? 조회, 저장 할 때 들어갈텐데..
        //그리고 EAGER로 했는데 왜 school만 검색하나?
        //이것도 @Transactional 때문인가?

    }
}
