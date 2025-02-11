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
				User user, AbstractUserRequest userRequest){
		String updatedUsername = "";
		String updatedSsn = "";
		String updatedEmail = "";
		String updatedPhone = "";
		boolean isChanged = false;
		//we re checking if the user updated the unique properties
		if (!user.getUsername().equals(userRequest.getUsername())) {
			updatedUsername = userRequest.getUsername();
			isChanged = true;
		}
		if (!user.getSsn().equals(userRequest.getSsn())) {
			updatedSsn = userRequest.getSsn();
			isChanged = true;
		}

		if (!user.getEmail().equals(userRequest.getEmail())) {
			updatedEmail = userRequest.getEmail();
			isChanged = true;
		}

		if (!user.getPhoneNumber().equals(userRequest.getPhoneNumber())) {
			updatedPhone = userRequest.getPhoneNumber();
			isChanged = true;
		}
		//if the user updated the unique properties, we re checking if the updated values are already exist in the DB
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
