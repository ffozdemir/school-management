package com.ffozdemir.schoolmanagement.service.validator;

import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.request.abstracts.AbstractUserRequest;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

	private final UserRepository userRepository;

	public void checkUniqueProperty(
				User user,
				AbstractUserRequest userRequest) {
		String updatedUsername = user.getUsername()
					                         .equals(userRequest.getUsername()) ? "" : userRequest.getUsername();
		String updatedSsn = user.getSsn()
					                    .equals(userRequest.getSsn()) ? "" : userRequest.getSsn();
		String updatedEmail = user.getEmail()
					                      .equals(userRequest.getEmail()) ? "" : userRequest.getEmail();
		String updatedPhone = user.getPhoneNumber()
					                      .equals(userRequest.getPhoneNumber()) ? "" : userRequest.getPhoneNumber();

		//check if there is any change
		boolean isChanged = !updatedUsername.isEmpty() || !updatedSsn.isEmpty() || !updatedEmail.isEmpty() || !updatedPhone.isEmpty();

		//if there is any change, check if the new values are unique
		if (isChanged) {
			checkDuplication(updatedUsername, updatedSsn, updatedPhone, updatedEmail);
		}
	}

	public void checkDuplication(
				String username,
				String ssn,
				String phone,
				String email) {
		if (userRepository.existsByUsername(username)) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
		}
		if (userRepository.existsByEmail(email)) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL, email));
		}
		if (userRepository.existsByPhoneNumber(phone)) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER, phone));
		}
		if (userRepository.existsBySsn(ssn)) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN, ssn));
		}
	}
}
