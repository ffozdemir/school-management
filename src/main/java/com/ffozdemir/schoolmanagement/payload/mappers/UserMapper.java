package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import com.ffozdemir.schoolmanagement.payload.request.user.StudentRequest;
import com.ffozdemir.schoolmanagement.payload.request.user.StudentUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.user.StudentResponse;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final UserRoleService userRoleService;
	private final PasswordEncoder passwordEncoder;

	/**
	 * @param userRequest DTO from postman or FE.
	 * @param userRole    role of user to be created or updated
	 * @return User entity
	 */

	public User mapUserRequestToUser(
				BaseUserRequest userRequest,
				String userRole) {
		User.UserBuilder userBuilder = User.builder()
					                               .username(userRequest.getUsername())
					                               .name(userRequest.getName())
					                               .surname(userRequest.getSurname())
					                               .password(passwordEncoder.encode(userRequest.getPassword()))
					                               .ssn(userRequest.getSsn())
					                               .birthday(userRequest.getBirthDay())
					                               .birthplace(userRequest.getBirthPlace())
					                               .phoneNumber(userRequest.getPhoneNumber())
					                               .gender(userRequest.getGender())
					                               .email(userRequest.getEmail())
					                               .buildIn(userRequest.getBuildIn())
					                               .isAdvisor(false);

		RoleType roleType = RoleType.valueOf(userRole.toUpperCase());
		if (roleType == RoleType.ADMIN && "Admin".equals(userRequest.getUsername())) {
			userBuilder.buildIn(true);
		}
		userBuilder.userRole(userRoleService.getUserRole(roleType));

		return userBuilder.build();
	}

	public UserResponse mapUserToUserResponse(
				User user) {
		return UserResponse.builder()
					       .id(user.getId())
					       .username(user.getUsername())
					       .name(user.getName())
					       .surname(user.getSurname())
					       .phoneNumber(user.getPhoneNumber())
					       .gender(user.getGender())
					       .birthDay(user.getBirthday())
					       .birthPlace(user.getBirthplace())
					       .ssn(user.getSsn())
					       .email(user.getEmail())
					       .userRole(user.getUserRole()
								                 .getRoleName())
					       .build();
	}

	public StudentResponse mapUserToStudentResponse(
				User student) {
		return StudentResponse.builder()
					       .id(student.getId())
					       .username(student.getUsername())
					       .name(student.getName())
					       .surname(student.getSurname())
					       .birthDay(student.getBirthday())
					       .ssn(student.getSsn())
					       .birthPlace(student.getBirthplace())
					       .phoneNumber(student.getPhoneNumber())
					       .gender(student.getGender())
					       .email(student.getEmail())
					       .studentNumber(student.getStudentNumber())
					       .motherName(student.getMotherName())
					       .fatherName(student.getFatherName())
					       .lessonProgramList(student.getLessonProgramList())
					       .build();
	}

	public User mapStudentUpdateRequestToUser(
				StudentUpdateRequest studentUpdateRequest) {
		return User.builder()
					       .username(studentUpdateRequest.getUsername())
					       .name(studentUpdateRequest.getName())
					       .ssn(studentUpdateRequest.getSsn())
					       .surname(studentUpdateRequest.getSurname())
					       .birthday(studentUpdateRequest.getBirthDay())
					       .birthplace(studentUpdateRequest.getBirthPlace())
					       .phoneNumber(studentUpdateRequest.getPhoneNumber())
					       .gender(studentUpdateRequest.getGender())
					       .email(studentUpdateRequest.getEmail())
					       .fatherName(studentUpdateRequest.getFatherName())
					       .motherName(studentUpdateRequest.getMotherName())
					       .build();
	}

	public User mapStudentRequestToUser(
				StudentRequest studentRequest) {
		return User.builder()
					       .username(studentRequest.getUsername())
					       .name(studentRequest.getName())
					       .surname(studentRequest.getSurname())
					       .password(passwordEncoder.encode(studentRequest.getPassword()))
					       .birthday(studentRequest.getBirthDay())
					       .ssn(studentRequest.getSsn())
					       .birthplace(studentRequest.getBirthPlace())
					       .phoneNumber(studentRequest.getPhoneNumber())
					       .gender(studentRequest.getGender())
					       .email(studentRequest.getEmail())
					       .buildIn(false)
					       .motherName(studentRequest.getMotherName())
					       .fatherName(studentRequest.getFatherName())
					       .advisorTeacherId(studentRequest.getAdvisorTeacherId())
					       .userRole(userRoleService.getUserRole(RoleType.STUDENT))
					       .build();
	}
}