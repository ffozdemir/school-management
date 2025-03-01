package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.Meet;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.payload.mappers.MeetingMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.MeetingRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.MeetingResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.MeetingRepository;
import com.ffozdemir.schoolmanagement.service.helper.MeetingHelper;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import com.ffozdemir.schoolmanagement.service.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MeetingService {

	private final MeetingRepository meetingRepository;
	private final MethodHelper methodHelper;
	private final TimeValidator timeValidator;
	private final MeetingMapper meetingMapper;
	private final PageableHelper pageableHelper;
	private final MeetingHelper meetingHelper;

	public ResponseMessage<MeetingResponse> save(
				HttpServletRequest httpServletRequest,
				@Valid MeetingRequest meetingRequest) {

		String username = (String) httpServletRequest.getAttribute("username");
		User teacher = methodHelper.loadByUsername(username);
		methodHelper.checkIsAdvisor(teacher);
		timeValidator.checkStartIsBeforeStop(meetingRequest.getStartTime(), meetingRequest.getStopTime());

		meetingHelper.checkMeetingConflicts(meetingRequest.getStudentIds(), teacher.getId(), meetingRequest.getDate(), meetingRequest.getStartTime(), meetingRequest.getStopTime());

		List<User> students = methodHelper.getUserList(meetingRequest.getStudentIds());
		Meet meetToSave = meetingMapper.mapMeetingRequestToMeet(meetingRequest);
		meetToSave.setStudentList(students);
		meetToSave.setAdvisoryTeacher(teacher);
		Meet savedMeeting = meetingRepository.save(meetToSave);
		return ResponseMessage.<MeetingResponse>builder()
					       .message(SuccessMessages.MEET_SAVE)
					       .returnBody(meetingMapper.mapMeetingToMeetingResponse(savedMeeting))
					       .httpStatus(HttpStatus.CREATED)
					       .build();


	}

	public ResponseMessage<MeetingResponse> update(
				@Valid MeetingRequest meetingRequest,
				Long meetingId,
				HttpServletRequest httpServletRequest) {
		Meet existingMeeting = meetingHelper.isMeetingExistById(meetingId);
		//validate is logged in teacher owner of the meeting
		meetingHelper.isMeetingMatchedWithTeacher(existingMeeting, httpServletRequest);
		//validate start + end time
		timeValidator.checkStartIsBeforeStop(meetingRequest.getStartTime(), meetingRequest.getStopTime());
		//validate meeting conflicts
		meetingHelper.checkMeetingConflicts(meetingRequest.getStudentIds(), existingMeeting.getAdvisoryTeacher()
					                                                                    .getId(), meetingRequest.getDate(), meetingRequest.getStartTime(), meetingRequest.getStopTime());
		List<User> students = methodHelper.getUserList(meetingRequest.getStudentIds());
		Meet meetingToUpdate = meetingMapper.mapMeetingRequestToMeet(meetingRequest);
		meetingToUpdate.setStudentList(students);
		meetingToUpdate.setAdvisoryTeacher(existingMeeting.getAdvisoryTeacher());
		meetingToUpdate.setId(existingMeeting.getId());
		Meet updatedMeeting = meetingRepository.save(meetingToUpdate);
		return ResponseMessage.<MeetingResponse>builder()
					       .message(SuccessMessages.MEET_UPDATE)
					       .returnBody(meetingMapper.mapMeetingToMeetingResponse(updatedMeeting))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public ResponseMessage<Page<MeetingResponse>> getAllByPageTeacher(
				int page,
				int size,
				HttpServletRequest httpServletRequest) {
		String username = (String) httpServletRequest.getAttribute("username");
		User teacher = methodHelper.loadByUsername(username);
		methodHelper.checkIsAdvisor(teacher);
		Pageable pageable = pageableHelper.getPageableByPageAndSize(page, size);
		Page<Meet> meetings = meetingRepository.findByAdvisoryTeacher_Id(teacher.getId(), pageable);
		Page<MeetingResponse> meetingResponses = meetings.map(meetingMapper::mapMeetingToMeetingResponse);
		return ResponseMessage.<Page<MeetingResponse>>builder()
					       .message(meetings.isEmpty() ? ErrorMessages.MEET_NOT_FOUND_MESSAGE : SuccessMessages.MEET_FOUND)
					       .returnBody(meetingResponses)
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public String deleteById(
				Long meetingId,
				HttpServletRequest httpServletRequest) {
		//validations
		Meet existingMeeting = meetingHelper.isMeetingExistById(meetingId);
		String username = (String) httpServletRequest.getAttribute("username");
		User user = methodHelper.loadByUsername(username);
		if (!user.getUserRole()
					     .getRoleName()
					     .equalsIgnoreCase("Admin")) {
			meetingHelper.isMeetingMatchedWithTeacher(existingMeeting, httpServletRequest);
		}
		//delete
		meetingRepository.deleteById(meetingId);
		return SuccessMessages.MEET_DELETE;
	}

	public List<MeetingResponse> getAll(
				HttpServletRequest httpServletRequest) {
		String username = (String) httpServletRequest.getAttribute("username");
		User user = methodHelper.loadByUsername(username);
		if (user.getUserRole()
					    .getRoleName()
					    .equalsIgnoreCase("Teacher")) {
			methodHelper.checkIsAdvisor(user);
			return meetingRepository.findByAdvisoryTeacher_Id((user.getId()))
						       .stream()
						       .map(meetingMapper::mapMeetingToMeetingResponse)
						       .collect(Collectors.toList());
		}
		return meetingRepository.findByStudentList_IdEquals(user.getId())
					       .stream()
					       .map(meetingMapper::mapMeetingToMeetingResponse)
					       .collect(Collectors.toList());
	}

	public Page<MeetingResponse> getAllByPage(
				int page,
				int size) {
		Pageable pageable = pageableHelper.getPageableByPageAndSize(page, size);
		Page<Meet> meetings = meetingRepository.findAll(pageable);
		return meetings.map(meetingMapper::mapMeetingToMeetingResponse);
	}
}
