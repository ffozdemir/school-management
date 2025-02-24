package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.mappers.UserMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.user.TeacherRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.service.business.LessonProgramService;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final MethodHelper methodHelper;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final LessonProgramService lessonProgramService;

	public ResponseMessage<UserResponse> saveTeacher(
				@Valid TeacherRequest teacherRequest) {
		List<LessonProgram> lessonProgramList = lessonProgramService.getLessonProgramById(teacherRequest.getLessonProgramList());
		//validate unique property
		uniquePropertyValidator.checkDuplication(teacherRequest.getUsername(), teacherRequest.getSsn(), teacherRequest.getPhoneNumber(), teacherRequest.getEmail());
		User teacher = userMapper.mapUserRequestToUser(teacherRequest, RoleType.TEACHER.getName());
		//set additional props.
		teacher.setIsAdvisor(teacherRequest.getIsAdvisoryTeacher());
		teacher.setLessonProgramList(lessonProgramList);
		User savedTeacher = userRepository.save(teacher);
		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.TEACHER_SAVE)
					       .httpStatus(HttpStatus.CREATED)
					       .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
					       .build();
	}
}
