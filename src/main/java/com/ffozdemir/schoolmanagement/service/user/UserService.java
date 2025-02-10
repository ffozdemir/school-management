package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.payload.mappers.UserMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.user.UserRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;

	public ResponseMessage<UserResponse> saveUser(
				@Valid UserRequest userRequest,
				String userRole) {
		//validate unique properties
		uniquePropertyValidator.checkDuplication(userRequest.getUsername(), userRequest.getSsn(), userRequest.getPhoneNumber(), userRequest.getEmail());

		//DTO->entity mapping
		User userToSave = userMapper.mapUserRequestToUser(userRequest, userRole);

		//save operation
		User savedUser = userRepository.save(userToSave);

		//entity ->DTO mapping
		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.USER_CREATE)
					       .returnBody(userMapper.mapUserToUserResponse(savedUser))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}
}
