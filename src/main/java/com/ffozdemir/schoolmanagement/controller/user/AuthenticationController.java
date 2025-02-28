package com.ffozdemir.schoolmanagement.controller.user;

import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.authentication.LoginRequest;
import com.ffozdemir.schoolmanagement.payload.request.authentication.UpdatePasswordRequest;
import com.ffozdemir.schoolmanagement.payload.response.authentication.AuthenticationResponse;
import com.ffozdemir.schoolmanagement.service.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

	@PreAuthorize("hasAnyAuthority('Admin', 'ViceDean', 'Dean', 'Teacher', 'Student')")
	@PutMapping("/changePassword")
	public ResponseEntity<String> updatePassword(
				@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
				HttpServletRequest httpServletRequest) {
		authenticationService.changePassword(updatePasswordRequest, httpServletRequest);
		return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE);
	}


}
