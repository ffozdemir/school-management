package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.request.user.UserRequest;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
				UserRequest userRequest,
				String userRole) {
		User user = User.builder()
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
					            .isAdvisor(false)
					            .build();

		//rol ile user one to one relationship'e sahip oldugu icin
		//bunu DB'den fetch edip requeste eklememiz gerekir.

		if (userRole.equalsIgnoreCase(RoleType.ADMIN.getName())) {
			//eger username ismi Admin ise datalar degistirilemez
			if (Objects.equals(userRequest.getUsername(), "Admin")) {
				user.setBuildIn(true);
			}
			user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
		} else if (userRole.equalsIgnoreCase(RoleType.MANAGER.getName())) {
			user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
		} else if (userRole.equalsIgnoreCase(RoleType.ASSISTANT_MANAGER.getName())) {
			user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
		} else if (userRole.equalsIgnoreCase(RoleType.STUDENT.getName())) {
			user.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
		} else if (userRole.equalsIgnoreCase(RoleType.TEACHER.getName())) {
			user.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
		} else {
			throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USER_ROLE_MESSAGE, userRole));
		}
		return user;
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
}
