package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.payload.mappers.UserMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.user.UserRequest;
import com.ffozdemir.schoolmanagement.payload.request.user.UserRequestWithoutPassword;
import com.ffozdemir.schoolmanagement.payload.response.abstracts.BaseUserResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import com.ffozdemir.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;
	private final MethodHelper methodHelper;
	private final PageableHelper pageableHelper;

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

	public ResponseMessage<BaseUserResponse> findUserById(
				Long userId) {
		//validate if user exists in DB
		User user = methodHelper.isUserExist(userId);
		return ResponseMessage.<BaseUserResponse>builder()
					       .message(SuccessMessages.USER_FOUND)
					       //map entity to DTO
					       .returnBody(userMapper.mapUserToUserResponse(user))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public String deleteUserById(
				Long userId) {
		//validate if user exists in DB
		methodHelper.isUserExist(userId);
		//delete user from DB
		userRepository.deleteById(userId);
		return SuccessMessages.USER_DELETE;
	}

	public Page<UserResponse> getUserByPage(
				int page,
				int size,
				String sort,
				String type,
				String userRole) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		return userRepository.findUserByUserRoleQuery(userRole, pageable)
					       .map(userMapper::mapUserToUserResponse);
	}

	public ResponseMessage<UserResponse> updateUserById(
				@Valid UserRequest userRequest,
				Long userId) {
		//validate if user exists in DB
		User userFromDb = methodHelper.isUserExist(userId);

		//build in user cannot be updated
		methodHelper.checkBuildIn(userFromDb);

		//validate unique properties
		uniquePropertyValidator.checkUniqueProperty(userFromDb, userRequest);

		//mapping
		User userToSave = userMapper.mapUserRequestToUser(userRequest, userFromDb.getUserRole()
					                                                               .getRoleName());
		userToSave.setId(userId);
		User savedUser = userRepository.save(userToSave);
		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.USER_UPDATE)
					       .httpStatus(HttpStatus.OK)
					       .returnBody(userMapper.mapUserToUserResponse(savedUser))
					       .build();

	}

	public String updateLoggedInUser(
				@Valid UserRequestWithoutPassword userRequestWithoutPassword,
				HttpServletRequest httpServletRequest) {
		String username = (String) httpServletRequest.getAttribute("username");
		User user = userRepository.findByUsername(username);
		methodHelper.checkBuildIn(user);
		uniquePropertyValidator.checkUniqueProperty(user, userRequestWithoutPassword);
		user.setName(userRequestWithoutPassword.getName());
		user.setSurname(userRequestWithoutPassword.getSurname());
		user.setSsn(userRequestWithoutPassword.getSsn());
		user.setUsername(userRequestWithoutPassword.getUsername());
		user.setBirthday(userRequestWithoutPassword.getBirthDay());
		user.setBirthplace(userRequestWithoutPassword.getBirthPlace());
		user.setPhoneNumber(userRequestWithoutPassword.getPhoneNumber());
		user.setEmail(userRequestWithoutPassword.getEmail());
		user.setGender(userRequestWithoutPassword.getGender());
		userRepository.save(user);
		return SuccessMessages.USER_UPDATE;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

}
