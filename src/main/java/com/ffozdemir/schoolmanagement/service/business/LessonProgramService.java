package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.mappers.LessonProgramMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.LessonProgramRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonProgramResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.LessonProgramRepository;
import com.ffozdemir.schoolmanagement.service.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

	private final LessonProgramRepository lessonProgramRepository;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final TimeValidator timeValidator;
	private final LessonProgramMapper lessonProgramMapper;

	public ResponseMessage<LessonProgramResponse> saveLessonProgram(
				@Valid LessonProgramRequest lessonProgramRequest) {
		//get lessons from DB
		List<Lesson> lessons = lessonService.getAllByIdSet(lessonProgramRequest.getLessonIdList());
		//get education term from DB
		EducationTerm educationTerm = educationTermService.isEducationTermExist(lessonProgramRequest.getEducationTermId());
		//validate start + end time
		timeValidator.checkStartIsBeforeStop(lessonProgramRequest.getStartTime(), lessonProgramRequest.getStopTime());
		//mapping
		LessonProgram lessonProgramToSave = lessonProgramMapper.mapLessonProgramRequestToLessonProgram(lessonProgramRequest, lessons, educationTerm);
		LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgramToSave);
		return ResponseMessage.<LessonProgramResponse>builder()
					       .returnBody(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
					       .message(SuccessMessages.LESSON_PROGRAM_SAVE)
					       .build();
	}

	//database view and index will be researched
	public List<LessonProgramResponse> getAllUnassigned() {
		return lessonProgramRepository.findByUsers_IdNull()
					       .stream()
					       .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
					       .collect(Collectors.toList());
	}

	public List<LessonProgramResponse> getAllAssigned() {
		return lessonProgramRepository.findByUsers_IdNotNull()
					       .stream()
					       .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
					       .collect(Collectors.toList());
	}

	public List<LessonProgramResponse> getAllLessonPrograms() {
		return lessonProgramRepository.findAll()
					       .stream()
					       .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
					       .collect(Collectors.toList());
	}

	public LessonProgramResponse findById(
				Long id) {
		return lessonProgramMapper.mapLessonProgramToLessonProgramResponse(ifExistById(id));
	}

	public LessonProgram ifExistById(
				Long id) {
		return lessonProgramRepository.findById(id)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE, id)));
	}

	public ResponseMessage<LessonProgramResponse> deleteLessonProgramById(
				Long id) {
		LessonProgram foundLesson = ifExistById(id);
		lessonProgramRepository.delete(foundLesson);
		return ResponseMessage.<LessonProgramResponse>builder()
					       .message(SuccessMessages.LESSON_PROGRAM_DELETE)
					       .returnBody(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(foundLesson))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	public List<LessonProgram> getLessonProgramById(
				List<Long> lessonIdList) {
		List<LessonProgram> lessonProgramList = lessonProgramRepository.findAllById(lessonIdList);
		if (lessonProgramList.isEmpty()) {
			throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE_WITHOUT_ID_INFO);
		}
		return lessonProgramList;
	}
}
