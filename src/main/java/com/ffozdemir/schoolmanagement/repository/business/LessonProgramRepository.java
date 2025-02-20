package com.ffozdemir.schoolmanagement.repository.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
}
