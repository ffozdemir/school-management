package com.ffozdemir.schoolmanagement;

import com.ffozdemir.schoolmanagement.entity.concretes.user.UserRole;
import com.ffozdemir.schoolmanagement.entity.enums.Gender;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.request.user.UserRequest;
import com.ffozdemir.schoolmanagement.repository.user.UserRoleRepository;
import com.ffozdemir.schoolmanagement.service.user.UserRoleService;
import com.ffozdemir.schoolmanagement.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SchoolManagementApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserRoleRepository userRoleRepository;
	private final UserService userService;

	public SchoolManagementApplication(
				UserRoleService userRoleService,
				UserRoleRepository userRoleRepository,
				UserService userService) {
		this.userRoleService = userRoleService;
		this.userRoleRepository = userRoleRepository;
		this.userService = userService;
	}

	public static void main(
				String[] args) {
		SpringApplication.run(SchoolManagementApplication.class, args);
	}

	@Override
	public void run(
				String... args) throws Exception {
		if (userRoleService.getAllUserRoles()
					    .isEmpty()) {
			List<UserRole> userRoles = Arrays.asList(createUserRole(RoleType.ADMIN), createUserRole(RoleType.MANAGER), createUserRole(RoleType.ASSISTANT_MANAGER), createUserRole(RoleType.STUDENT), createUserRole(RoleType.TEACHER));
			userRoleRepository.saveAll(userRoles);
		}
		if (userService.getAllUsers()
					    .isEmpty()) {
			userService.saveUser(getUserRequest(), RoleType.ADMIN.getName());
		}

	}

	private UserRole createUserRole(
				RoleType roleType) {
		UserRole userRole = new UserRole();
		userRole.setRoleType(roleType);
		userRole.setRoleName(roleType.getName());
		return userRole;
	}

	private static UserRequest getUserRequest() {
		UserRequest userRequest = new UserRequest();
		userRequest.setUsername("admin");
		userRequest.setEmail("admin@admin.com");
		userRequest.setSsn("111-11-1111");
		userRequest.setPassword("Ankara06*");
		userRequest.setBuildIn(true);
		userRequest.setName("adminName");
		userRequest.setSurname("adminSurname");
		userRequest.setPhoneNumber("111-111-1111");
		userRequest.setGender(Gender.FEMALE);
		userRequest.setBirthDay(LocalDate.of(1980, 1, 1));
		userRequest.setBirthPlace("Texas");
		return userRequest;
	}

}
