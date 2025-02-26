package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.entity.concretes.business.StudentInfo;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.Note;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.mappers.StudentInfoMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.StudentInfoRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.business.StudentInfoResponse;
import com.ffozdemir.schoolmanagement.repository.business.StudentInfoRepository;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.helper.StudentInfoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

	private final StudentInfoRepository studentInfoRepository;
	private final MethodHelper methodHelper;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final StudentInfoHelper studentInfoHelper;
	private final StudentInfoMapper studentInfoMapper;

	public ResponseMessage<StudentInfoResponse> saveStudentInfo(
				HttpServletRequest httpServletRequest,
				@Valid StudentInfoRequest studentInfoRequest) {
		String teacherUsername = (String) httpServletRequest.getAttribute("username");
		User teacher = methodHelper.loadByUsername(teacherUsername);
		//validate student id
		User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
		//validate user is really a student
		methodHelper.checkUserRole(student, RoleType.STUDENT);
		//validate and fetch lesson
		Lesson lesson = lessonService.isLessonExistbyId(studentInfoRequest.getLessonId());
		//validate and fetch education term
		EducationTerm educationTerm = educationTermService.isEducationTermExist(studentInfoRequest.getEducationTermId());
		//student should have only one student info for a lesson
		studentInfoHelper.validateLessonDuplication(student.getId(), lesson.getLessonName());
		Note note = studentInfoHelper.checkLetterGrade(studentInfoHelper.calculateAverageScore(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));
		//mapping
		StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(studentInfoRequest, note, studentInfoHelper.calculateAverageScore(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));
		studentInfo.setStudent(student);
		studentInfo.setLesson(lesson);
		studentInfo.setEducationTerm(educationTerm);
		studentInfo.setTeacher(teacher);
		StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);
		return ResponseMessage.<StudentInfoResponse>builder()
					       .message(SuccessMessages.STUDENT_INFO_SAVE)
					       .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
					       .build();
	}
}
