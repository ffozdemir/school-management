package com.ffozdemir.schoolmanagement.service.helper;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

	private final UserRepository userRepository;

	public User isUserExist(
				Long id) {
		return userRepository.findById(id)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, id)));
	}

	public void checkBuildIn(User user) {
		if(user.getBuildIn()) {
			throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
		}
	}

}
