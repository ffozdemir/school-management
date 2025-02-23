package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.payload.request.business.LessonProgramRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonProgramResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LessonProgramMapper {

	public LessonProgram mapLessonProgramRequestToLessonProgram(
				LessonProgramRequest lessonProgramRequest,
				List<Lesson> lessonSet,
				EducationTerm educationTerm) {
		return LessonProgram.builder()
					       .startTime(lessonProgramRequest.getStartTime())
					       .stopTime(lessonProgramRequest.getStopTime())
					       .day(lessonProgramRequest.getDay())
					       .lessons(lessonSet)
					       .educationTerm(educationTerm)
					       .build();
	}

	public LessonProgramResponse mapLessonProgramToLessonProgramResponse(
				LessonProgram lessonProgram) {
		return LessonProgramResponse.builder()
					       .day(lessonProgram.getDay())
					       .educationTerm(lessonProgram.getEducationTerm())
					       .startTime(lessonProgram.getStartTime())
					       .stopTime(lessonProgram.getStopTime())
					       .lessonName(lessonProgram.getLessons())
					       .lessonProgramId(lessonProgram.getId())
					       .build();
	}

}
