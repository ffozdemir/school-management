package com.ffozdemir.schoolmanagement.service.validator;

import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class TimeValidator {

	//validate if start time is before stop time
	public void checkStartIsBeforeStop(
				LocalTime start,
				LocalTime stop) {
		if (start.isAfter(stop) || start.equals(stop)) {
			throw new BadRequestException(ErrorMessages.TIME_NOT_VALID_MESSAGE);
		}
	}
}
