package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.payload.request.business.LessonProgramRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonProgramResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.LessonProgramRepository;
import com.ffozdemir.schoolmanagement.service.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

	private final LessonProgramRepository lessonProgramRepository;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final TimeValidator timeValidator;

	public ResponseMessage<LessonProgramResponse> saveLessonProgram(
				@Valid LessonProgramRequest lessonProgramRequest) {
		//get lessons from DB
		Set<Lesson> lessons = lessonService.getAllByIdSet(lessonProgramRequest.getLessonIdList());
		//get education term from DB
		EducationTerm educationTerm = educationTermService.isEducationTermExist(lessonProgramRequest.getEducationTermId());
		//validate start + end time
		timeValidator.checkStartIsBeforeStop(lessonProgramRequest.getStartTime(), lessonProgramRequest.getStopTime());
		//mapping
		return null;
	}
}
