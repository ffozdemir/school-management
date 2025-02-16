package com.ffozdemir.schoolmanagement.controller.user;

import com.ffozdemir.schoolmanagement.payload.request.authentication.LoginRequest;
import com.ffozdemir.schoolmanagement.payload.response.authentication.AuthenticationResponse;
import com.ffozdemir.schoolmanagement.service.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> authenticate(
				@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
	}


}
