package com.ffozdemir.schoolmanagement.security.jwt;

import com.ffozdemir.schoolmanagement.security.service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
// for extra information please read
//https://www.baeldung.com/spring-onceperrequestfilter
public class AuthTokenFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

	private final JwtUtils jwtUtils;

	private final UserDetailServiceImpl userDetailService;


	@Override
	protected void doFilterInternal(
				@NonNull HttpServletRequest request,
				@NonNull HttpServletResponse response,
				@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			//1-from every request, we will get JWT
			String jwt = parseJwt(request);
			//validate JWT
			if (jwt != null && jwtUtils.validateToken(jwt)) {
				//3- we need username to get user information
				String username = jwtUtils.getUsernameFromToken(jwt);
				//4- check DB and fetch user and upgrade it to userDetails
				UserDetails userDetails = userDetailService.loadUserByUsername(username);
				//5- set attribute with username
				request.setAttribute("username", username);
				//6- we load user details information to security context
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext()
							.setAuthentication(authentication);
				LOGGER.debug("User '{}' authenticated successfully", username);
			}
		} catch (UsernameNotFoundException e) {
			LOGGER.error("Can not set user authentication", e);
		} catch (Exception e) {
			LOGGER.error("Authentication process failed: {}", e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Extracts JWT token from the Authorization header.
	 *
	 * @param request HTTP request containing the Authorization header
	 * @return JWT token if present and valid format, null otherwise
	 */
	private String parseJwt(
				HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
}
