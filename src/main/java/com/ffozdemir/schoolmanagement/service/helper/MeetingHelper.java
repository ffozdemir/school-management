package com.ffozdemir.schoolmanagement.service.helper;

import com.ffozdemir.schoolmanagement.entity.concretes.business.Meet;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.repository.business.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MeetingHelper {

	private final MethodHelper methodHelper;
	private final MeetingRepository meetingRepository;

	// Ö1 > m1 , m4
	// Ö2 > m1 , m3
	// Ö3 > m2 , m5 , m6
	// m1, m2, m4, m5, m6
	// Ögretmen1 > m5, m2
	// m1, m2, m4, m5, m6 ---- MONDAY , 08:00-11:00

	public void checkMeetingConflicts(
				List<Long> studentIdList,
				Long teacherId,
				LocalDate meetingDate,
				LocalTime startTime,
				LocalTime stopTime) {
		Set<Meet> existingMeetings = new HashSet<>();
		studentIdList.forEach(id -> {
			// check if the user exists and is a student
			methodHelper.checkUserRole(methodHelper.isUserExist(id), RoleType.STUDENT);
			// add all student's meetings to the collection
			existingMeetings.addAll(meetingRepository.findByStudentList_IdEquals(id));
		});
		// add all teacher meetings to the collection
		existingMeetings.addAll(meetingRepository.getByAdvisoryTeacher_IdEquals(teacherId));
		// compare each meeting with the DTO
		existingMeetings.stream()
					.filter(meet -> existingMeetings.size() > 1)
					.forEach(meet -> {
						LocalTime existingStartTime = meet.getStartTime();
						LocalTime existingStopTime = meet.getStopTime();
						if (meet.getDate().equals(meetingDate) &&
									    ((startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
												     (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
												     (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
												     (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime)))) {
							throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
						}
					});
	}

	public Meet isMeetingExistById(
				Long id) {
		return meetingRepository.findById(id)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE, id)));
	}

	public void isMeetingMatchedWithTeacher(
				Meet meeting,
				HttpServletRequest httpServletRequest) {
		String username = (String) httpServletRequest.getAttribute("username");
		User teacher = methodHelper.loadByUsername(username);
		methodHelper.checkIsAdvisor(teacher);
		if (!meeting.getAdvisoryTeacher()
					     .getId()
					     .equals(teacher.getId())) {
			throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
		}
	}


}
