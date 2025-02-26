package com.ffozdemir.schoolmanagement.service.helper;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MethodHelper {

	private final UserRepository userRepository;

	public User isUserExist(
				Long id) {
		return userRepository.findById(id)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, id)));
	}

	public void checkBuildIn(
				User user) {
		if (user.getBuildIn()) {
			throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
		}
	}

	public void checkUserRole(
				User user,
				RoleType roleType) {
		if (!user.getUserRole()
					     .getRoleType()
					     .equals(roleType)) {
			throw new BadRequestException(ErrorMessages.NOT_HAVE_EXPECTED_ROLE_USER);
		}
	}

	public User loadByUsername(
				String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE_USERNAME, username));
		}
		return user;
	}

	public void checkIsAdvisor(
				User user) {
		if (!user.getIsAdvisor()) {
			throw new BadRequestException(String.format(ErrorMessages.NOT_ADVISOR_TEACHER_MESSAGE, user.getId()));
		}
	}


	public List<User> getUserList(
				List<Long> userIdList) {
		return userRepository.findByUserIdList(userIdList);
	}

}
