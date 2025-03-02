package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.mappers.UserMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.AddLessonProgram;
import com.ffozdemir.schoolmanagement.payload.request.user.TeacherRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.StudentResponse;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.service.business.LessonProgramService;
import com.ffozdemir.schoolmanagement.service.helper.LessonProgramDuplicationHelper;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import com.ffozdemir.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final MethodHelper methodHelper;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final LessonProgramService lessonProgramService;
	private final PageableHelper pageableHelper;
	private final LessonProgramDuplicationHelper lessonProgramDuplicationHelper;

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

	public ResponseMessage<UserResponse> updateTeacherById(
				@Valid TeacherRequest teacherRequest,
				Long userId) {
		//validate if teacher exist
		User teacher = methodHelper.isUserExist(userId);
		//validate if user is a teacher
		methodHelper.checkUserRole(teacher, RoleType.TEACHER);
		//validate unique property
		uniquePropertyValidator.checkUniqueProperty(teacher, teacherRequest);
		List<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonProgramList());
		User teacherToUpdate = userMapper.mapUserRequestToUser(teacherRequest, RoleType.TEACHER.getName());
		//map missing props
		teacherToUpdate.setId(userId);
		teacherToUpdate.setLessonProgramList(lessonPrograms);
		teacherToUpdate.setIsAdvisor(teacherRequest.getIsAdvisoryTeacher());
		User savedTeacher = userRepository.save(teacherToUpdate);
		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.TEACHER_UPDATE)
					       .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public List<StudentResponse> getAllStudentByAdvisorTeacher(
				HttpServletRequest httpServletRequest) {
		String username = (String) httpServletRequest.getAttribute("username");
		User teacher = methodHelper.loadByUsername(username);
		methodHelper.checkIsAdvisor(teacher);
		return userRepository.findByAdvisorTeacherId(teacher.getId())
					       .stream()
					       .map(userMapper::mapUserToStudentResponse)
					       .collect(Collectors.toList());
	}

	public ResponseMessage<UserResponse> addLessonProgram(
				@Valid AddLessonProgram lessonProgram) {
		User teacher = methodHelper.isUserExist(lessonProgram.getTeacherId());
		methodHelper.checkUserRole(teacher, RoleType.TEACHER);
		List<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(lessonProgram.getLessonProgramId());


		// checking and removing duplicates
		List<LessonProgram> existingLessonPrograms = teacher.getLessonProgramList();
		List<LessonProgram> newLessonProgram = lessonProgramDuplicationHelper.removeDuplicates(existingLessonPrograms, lessonPrograms);

		teacher.getLessonProgramList()
					.addAll(newLessonProgram);
		User savedTeacher = userRepository.save(teacher);
		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
					       .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public ResponseMessage<List<UserResponse>> getAllByTeacher() {
		List<User> teacherList = userRepository.getAllTeachers();
		return ResponseMessage.<List<UserResponse>>builder()
					       .message(SuccessMessages.TEACHER_LIST)
					       .returnBody(teacherList.stream()
								                   .map(userMapper::mapUserToUserResponse)
								                   .collect(Collectors.toList()))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public Page<UserResponse> getAllTeacherByPage(
				int page,
				int size,
				String sort,
				String type) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		Page<User> teacherPage = userRepository.findAllTeacherByPage(RoleType.TEACHER.getName(), pageable);
		return teacherPage.map(userMapper::mapUserToUserResponse);
	}

	/*@Transactional
	public String deleteTeacherById(
				Long teacherId) {
		User teacher = methodHelper.isUserExist(teacherId);
		methodHelper.checkUserRole(teacher, RoleType.TEACHER);
		List<User> students = userRepository.findByAdvisorTeacherId(teacherId);
		if (!students.isEmpty()) {
			students.forEach(student->student.setAdvisorTeacherId(null));
			userRepository.saveAll(students);
		}
		userRepository.delete(teacher);
		return SuccessMessages.TEACHER_DELETE;
	}*/

	@Transactional
	public ResponseMessage<UserResponse> deleteTeacherById(Long teacherId) {
		User teacher = methodHelper.isUserExist(teacherId);
		methodHelper.checkUserRole(teacher,RoleType.TEACHER);

		userRepository.removeAdvisorFromStudents(teacherId);
		userRepository.delete(teacher);

		return ResponseMessage.<UserResponse>builder()
					       .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

}
