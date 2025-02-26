package com.ffozdemir.schoolmanagement.repository.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.concretes.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(
				String username);

	boolean existsByEmail(
				String email);

	boolean existsBySsn(
				String ssn);

	boolean existsByPhoneNumber(
				String phone);

	@Query("select u from User u where u.userRole.roleName = :roleName")
	Page<User> findUserByUserRoleQuery(
				String roleName,
				Pageable pageable);

	User findByUsername(
				String username);

	@Query("select (count(u) > 0) from User u where u.userRole.roleType = 'STUDENT'")
	boolean findStudent();

	@Query("select max(u.studentNumber) from User u where u.userRole.roleType = 'STUDENT'")
	int getMaxStudentNumber();

	List<User> findByAdvisorTeacherId(
				Long advisorTeacherId);

	List<User> findByUserRole(
				UserRole userRole);

	@Query("select u from User u where u.userRole.roleType = 'TEACHER'")
	List<User> getAllTeachers();

	@Query("select u from User u where u.userRole.roleType = 'TEACHER'")
	Page<User> findAllTeacherByPage(
				String name,
				Pageable pageable);

	@Transactional
	@Modifying
	@Query("UPDATE User u SET u.advisorTeacherId = NULL WHERE u.advisorTeacherId = :teacherId")
	void removeAdvisorFromStudents(
				@Param("teacherId") Long teacherId);

	@Query("select u from User u where u.id in :userIdList")
	List<User> findByUserIdList(
				List<Long> userIdList);

}
