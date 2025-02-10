package com.ffozdemir.schoolmanagement.controller.user;

import com.ffozdemir.schoolmanagement.payload.request.user.UserRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@PostMapping("/save/{userRole}")
	public ResponseEntity<ResponseMessage<UserResponse>> saveUser(
				@RequestBody @Valid UserRequest userRequest, @PathVariable String userRole) {
		return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
	}

}
