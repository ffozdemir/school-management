package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.payload.request.authentication.LoginRequest;
import com.ffozdemir.schoolmanagement.payload.response.authentication.AuthenticationResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.security.jwt.JwtUtils;
import com.ffozdemir.schoolmanagement.security.service.UserDetailsImpl;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtUtils jwtUtils;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final MethodHelper methodHelper;


	public AuthenticationResponse authenticate(
				@Valid LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		//injection of security in service
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		//security authentication bu satirda yapiliyor
		SecurityContextHolder.getContext()
					.setAuthentication(authentication);

		String token = jwtUtils.generateToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String userRole = userDetails.getAuthorities()
					                  .iterator()
					                  .next()
					                  .getAuthority();

		return AuthenticationResponse.builder()
					       .token(token)
					       .role(userRole)
					       .username(userDetails.getUsername())
					       .build();


	}
}
