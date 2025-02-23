package com.ffozdemir.schoolmanagement.repository.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
	List<LessonProgram> findByUsers_IdNull();

	List<LessonProgram> findByUsers_IdNotNull();
}
