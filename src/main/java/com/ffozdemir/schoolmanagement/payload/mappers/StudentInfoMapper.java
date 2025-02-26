package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.business.StudentInfo;
import com.ffozdemir.schoolmanagement.entity.enums.Note;
import com.ffozdemir.schoolmanagement.payload.request.business.StudentInfoRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.StudentInfoResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class StudentInfoMapper {

	private final UserMapper userMapper;

	public StudentInfo mapStudentInfoRequestToStudentInfo(
				StudentInfoRequest studentInfoRequest,
				Note note,
				Double average) {
		return StudentInfo.builder()
					       .infoNote(studentInfoRequest.getInfoNote())
					       .absentee(studentInfoRequest.getAbsentee())
					       .midtermExam(studentInfoRequest.getMidtermExam())
					       .finalExam(studentInfoRequest.getFinalExam())
					       .examAverage(average)
					       .letterGrade(note)
					       .build();

	}

	public StudentInfoResponse mapStudentInfoToStudentInfoResponse(
				StudentInfo studentInfo) {
		return StudentInfoResponse.builder()
					       .lessonName(studentInfo.getLesson()
								                   .getLessonName())
					       .creditScore(studentInfo.getLesson()
								                    .getCreditScore())
					       .isCompulsory(studentInfo.getLesson()
								                     .getIsCompulsory())
					       .educationTerm(studentInfo.getEducationTerm()
								                      .getTerm())
					       .id(studentInfo.getId())
					       .absentee(studentInfo.getAbsentee())
					       .midtermExam(studentInfo.getMidtermExam())
					       .finalExam(studentInfo.getFinalExam())
					       .infoNote(studentInfo.getInfoNote())
					       .note(studentInfo.getLetterGrade())
					       .average(studentInfo.getExamAverage())
					       .studentResponse(userMapper.mapUserToStudentResponse(studentInfo.getStudent()))
					       .build();
	}

}
