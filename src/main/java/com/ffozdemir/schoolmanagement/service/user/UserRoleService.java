package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.user.UserRole;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

	private final UserRoleRepository userRoleRepository;

	public UserRole getUserRole(
				RoleType roleType) {
		return userRoleRepository.findByUserRoleType(roleType)
					       .orElseThrow(()->new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND));
	}

	public List<UserRole> getAllUserRoles() {
		return userRoleRepository.findAll();
	}


}
