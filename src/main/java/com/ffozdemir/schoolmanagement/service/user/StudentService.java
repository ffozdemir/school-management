package com.ffozdemir.schoolmanagement.service.user;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.RoleType;
import com.ffozdemir.schoolmanagement.payload.mappers.UserMapper;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.AddLessonProgramForStudent;
import com.ffozdemir.schoolmanagement.payload.request.user.StudentRequest;
import com.ffozdemir.schoolmanagement.payload.request.user.StudentUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.StudentResponse;
import com.ffozdemir.schoolmanagement.repository.user.UserRepository;
import com.ffozdemir.schoolmanagement.service.business.LessonProgramService;
import com.ffozdemir.schoolmanagement.service.helper.MethodHelper;
import com.ffozdemir.schoolmanagement.service.validator.TimeValidator;
import com.ffozdemir.schoolmanagement.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final MethodHelper methodHelper;
  private final UniquePropertyValidator uniquePropertyValidator;
  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final LessonProgramService lessonProgramService;
  private final TimeValidator timeValidator;

  public ResponseMessage<StudentResponse> save(
      StudentRequest studentRequest) {
    //does advisor teacher exist in DB
    User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
    //is he/she really advisor
    methodHelper.checkIsAdvisor(advisorTeacher);
    //validate unique properties
    uniquePropertyValidator.checkDuplication(studentRequest.getUsername(), studentRequest.getSsn(),
        studentRequest.getPhoneNumber(), studentRequest.getEmail());
    //map DTO to Entity
    User student = userMapper.mapStudentRequestToUser(studentRequest);
    //set missing props
    student.setAdvisorTeacherId(advisorTeacher.getId());
    student.setActive(true);
    //every student will have a number strating from 1000
    student.setStudentNumber(getLastStudentNumber());
    //save student
    User savedStudent = userRepository.save(student);
    return ResponseMessage.<StudentResponse>builder()
        .returnBody(userMapper.mapUserToStudentResponse(savedStudent))
        .message(SuccessMessages.STUDENT_SAVE)
        .httpStatus(HttpStatus.CREATED)
        .build();
  }

  private int getLastStudentNumber() {
    if (!userRepository.findStudent()) {
      return 1000;
    }
    return userRepository.getMaxStudentNumber() + 1;
  }


  public String updateStudent(
      HttpServletRequest httpServletRequest,
      @Valid StudentUpdateRequest studentUpdateRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    User student = methodHelper.loadByUsername(username);
    uniquePropertyValidator.checkUniqueProperty(student, studentUpdateRequest);
    User userToUpdate = userMapper.mapStudentUpdateRequestToUser(studentUpdateRequest);
    userToUpdate.setId(student.getId());
    userToUpdate.setPassword(student.getPassword());
    userToUpdate.setBuildIn(student.getBuildIn());
    userToUpdate.setAdvisorTeacherId(student.getAdvisorTeacherId());
    userRepository.save(userToUpdate);
    return SuccessMessages.STUDENT_UPDATE;
  }

  //TODO FIXlenecek
  public ResponseMessage<StudentResponse> updateStudentByManager(
      Long studentId,
      StudentRequest studentRequest) {
    //validate user existence
    User student = methodHelper.isUserExist(studentId);
    methodHelper.checkUserRole(student, RoleType.STUDENT);
    uniquePropertyValidator.checkUniqueProperty(student, studentRequest);

    //validate advisor teacher
    User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
    methodHelper.checkIsAdvisor(advisorTeacher);

    //only necessary fields are updated
    User userToUpdate = userMapper.mapStudentRequestToUser(studentRequest);
    userToUpdate.setId(student.getId());
    userToUpdate.setActive(student.isActive());
    userToUpdate.setStudentNumber(student.getStudentNumber());
    userToUpdate.setLessonProgramList(student.getLessonProgramList());

    return ResponseMessage.<StudentResponse>builder()
        .message(SuccessMessages.STUDENT_UPDATE)
        .returnBody(userMapper.mapUserToStudentResponse(userRepository.save(userToUpdate)))
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public ResponseMessage<StudentResponse> addLessonProgram(
      HttpServletRequest httpServletRequest,
      AddLessonProgramForStudent addLessonProgramForStudent) {
    String username = (String) httpServletRequest.getAttribute("username");
    User loggedInUser = methodHelper.loadByUsername(username);
    //new lesson program from request
    List<LessonProgram> lessonProgramFromDb = lessonProgramService.getLessonProgramById(
        addLessonProgramForStudent.getLessonProgramId());
    //existing lesson programs of student
    List<LessonProgram> studentLessonProgram = loggedInUser.getLessonProgramList();
    //TODO user LessonProgramDuplicationHelper here
    studentLessonProgram.addAll(lessonProgramFromDb);
    return null;
  }
}
