package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.entity.concretes.business.StudentInfo;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.Note;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.mappers.StudentInfoMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.StudentInfoRequest;
import com.ffozdemir.schoolmanagement.payload.request.business.StudentInfoUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.business.StudentInfoResponse;
import com.ffozdemir.schoolmanagement.repository.business.StudentInfoRepository;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import com.ffozdemir.schoolmanagement.service.helper.StudentInfoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

	private final StudentInfoRepository studentInfoRepository;
	private final MethodHelper methodHelper;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final StudentInfoHelper studentInfoHelper;
	private final StudentInfoMapper studentInfoMapper;
	private final PageableHelper pageableHelper;

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


	public List<StudentInfoResponse> findStudentInfoByStudentId(
				Long studentId) {
		User student = methodHelper.isUserExist(studentId);
		methodHelper.checkUserRole(student, RoleType.STUDENT);
		List<StudentInfo> studentInfoList = studentInfoRepository.findByStudent_Id(studentId);
		return studentInfoList.stream()
					       .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse)
					       .collect(Collectors.toList());
	}


	public String deleteStudentInfoById(
				Long id) {
		StudentInfo studentInfo = isStudentInfoExist(id);
		studentInfoRepository.delete(studentInfo);
		return SuccessMessages.STUDENT_INFO_DELETE;
	}

	public StudentInfo isStudentInfoExist(
				Long id) {
		return studentInfoRepository.findById(id)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND, id)));
	}


	public StudentInfoResponse findById(
				Long studentInfoId) {
		StudentInfo studentInfo = isStudentInfoExist(studentInfoId);
		return studentInfoMapper.mapStudentInfoToStudentInfoResponse(studentInfo);
	}

	public ResponseMessage<StudentInfoResponse> updateStudentInfo(
				@Valid StudentInfoUpdateRequest studentInfoUpdateRequest,
				Long id) {
		StudentInfo studentInfo = isStudentInfoExist(id);

		// Validate the student
		User student = methodHelper.isUserExist(studentInfoUpdateRequest.getStudentId());
		methodHelper.checkUserRole(student, RoleType.STUDENT);

		// Validate the lesson and education term
		Lesson lesson = lessonService.isLessonExistbyId(studentInfoUpdateRequest.getLessonId());
		EducationTerm educationTerm = educationTermService.isEducationTermExist(studentInfoUpdateRequest.getEducationTermId());

		// Calculate the average score and check the letter grade
		double averageScore = studentInfoHelper.calculateAverageScore(studentInfoUpdateRequest.getMidtermExam(), studentInfoUpdateRequest.getFinalExam());
		Note updatedNote = studentInfoHelper.checkLetterGrade(averageScore);

		// Update using the mapper
		StudentInfo updatedStudentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(studentInfoUpdateRequest, updatedNote, averageScore);

		// Set missing properties
		updatedStudentInfo.setId(studentInfo.getId());
		updatedStudentInfo.setStudent(student);
		updatedStudentInfo.setLesson(lesson);
		updatedStudentInfo.setEducationTerm(educationTerm);
		updatedStudentInfo.setTeacher(studentInfo.getTeacher());

		StudentInfo savedStudentInfo = studentInfoRepository.save(updatedStudentInfo);
		return ResponseMessage.<StudentInfoResponse>builder()
					       .message(SuccessMessages.STUDENT_INFO_UPDATE)
					       .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
					       .build();
	}

	public Page<StudentInfoResponse> findStundentInfoByPage(
				int page,
				int size,
				String sort,
				String type) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		Page<StudentInfo> studentInfoPage = studentInfoRepository.findAll(pageable);
		return studentInfoPage.map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
	}

	public Page<StudentInfoResponse> findByTeacherOrStudentByPage(
				HttpServletRequest httpServletRequest,
				int page,
				int size) {
		String username = (String) httpServletRequest.getAttribute("username");
		User loggedInUser = methodHelper.loadByUsername(username);
		Pageable pageable = pageableHelper.getPageableByPageAndSize(page, size);
		Page<StudentInfo> studentInfoPage = (loggedInUser.getUserRole()
					                                     .getRoleType()
					                                     .getName()
					                                     .equals(RoleType.TEACHER.getName())) ? studentInfoRepository.findByTeacher_Id((loggedInUser.getId()), pageable) : studentInfoRepository.findByStudent_Id(loggedInUser.getId(), pageable);
		//SECOND OPTION
		// Teachers already has its own StudentInfo list within the object.
		// If loggedIn user is a teacher, we can have and turn that StudentInfo list into a Page directly.*/
    /*Page<StudentInfo> studentInfoPage =
        (loggedInUser.getUserRole().getRoleType().getName().equals(RoleType.TEACHER.getName())) ?
            new PageImpl<>(loggedInUser.getStudentInfos(), pageable,
                loggedInUser.getStudentInfos().size())
            : studentInfoRepository.findAllByStudent_Id(loggedInUser.getId(), pageable);*/
		return studentInfoPage.map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
	}
}
