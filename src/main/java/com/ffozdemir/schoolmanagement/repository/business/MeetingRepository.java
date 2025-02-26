package com.ffozdemir.schoolmanagement.repository.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meet, Long> {

	List<Meet> findByStudentList_IdEquals(
				Long id);

	List<Meet> getByAdvisoryTeacher_IdEquals(
				Long teacherId);
}
