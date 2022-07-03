package com.example.jpaassociation.service;

import com.example.jpaassociation.domain.School;
import com.example.jpaassociation.domain.SchoolRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SchoolService {
    private SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Transactional(readOnly = true)
    public List<String> findAllSubjectNames(){
        List<School> academies = schoolRepository.findAll();

        return academies.stream()
                .map(a -> a.getStudents().get(0).getName())
                .collect(Collectors.toList());
    }

    /**
     * Lazy Load를 수행하기 위해 메소드를 별도로 생성
     */
    private List<String> extractSubjectNames(List<School> academies){

        return academies.stream()
                .map(a -> a.getStudents().get(0).getName())
                .collect(Collectors.toList());
    }
}
