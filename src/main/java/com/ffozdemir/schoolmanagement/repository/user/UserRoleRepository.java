package com.ffozdemir.schoolmanagement.repository.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.UserRole;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query("select r from UserRole r WHERE r.roleType = ?1")
	Optional<UserRole> findByUserRoleType(
				RoleType roleType);
}
