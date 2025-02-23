package com.ffozdemir.schoolmanagement.repository.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.concretes.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
