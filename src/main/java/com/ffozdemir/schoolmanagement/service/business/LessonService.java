package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.mappers.LessonMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.LessonRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.LessonRepository;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final LessonMapper lessonMapper;
	private final PageableHelper pageableHelper;

	public ResponseMessage<LessonResponse> saveLesson(
				@Valid LessonRequest lessonRequest) {
		//validate lesson name must be unique
		isLessonExistByName(lessonRequest.getLessonName());
		//save lesson
		Lesson lesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));
		return ResponseMessage.<LessonResponse>builder()
					       .returnBody(lessonMapper.mapLessonToLessonRespone(lesson))
					       .httpStatus(HttpStatus.CREATED)
					       .message(SuccessMessages.LESSON_SAVE)
					       .build();
	}

	private void isLessonExistByName(
				String lessonName) {
		if (lessonRepository.findByLessonNameEqualsIgnoreCase(lessonName)
					    .isPresent()) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_CREATED_LESSON_MESSAGE, lessonName));
		}
	}

	public ResponseMessage<LessonResponse> getLessonByName(
				String lessonName) {
		Lesson lesson = lessonRepository.findByLessonNameEqualsIgnoreCase(lessonName)
					                .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, lessonName)));
		return ResponseMessage.<LessonResponse>builder()
					       .returnBody(lessonMapper.mapLessonToLessonRespone(lesson))
					       .httpStatus(HttpStatus.OK)
					       .message(SuccessMessages.LESSON_FOUND)
					       .build();
	}

	public Page<LessonResponse> findLessonByPage(
				int page,
				int size,
				String sort,
				String type) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		return lessonRepository.findAll(pageable)
					       .map(lessonMapper::mapLessonToLessonRespone);
	}

	public ResponseMessage<LessonResponse> deleteLesson(
				Long lessonId) {
		Lesson lesson = isLessonExistbyId(lessonId);
		lessonRepository.delete(lesson);
		return ResponseMessage.<LessonResponse>builder()
					       .httpStatus(HttpStatus.OK)
					       .message(SuccessMessages.LESSON_DELETE)
					       .build();
	}

	public Lesson isLessonExistbyId(
				Long lessonId) {
		return lessonRepository.findById(lessonId)
					       .orElseThrow(()->new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_MESSAGE));
	}

	public LessonResponse updateLesson(
				@Valid LessonRequest lessonRequest,
				Long lessonId) {
		Lesson lessonFromDb = isLessonExistbyId(lessonId);
		if (!lessonRequest.getLessonName()
					     .equals(lessonFromDb.getLessonName())) {
			isLessonExistByName(lessonRequest.getLessonName());
		}
		Lesson lessonToUpdate = lessonMapper.mapLessonRequestToLesson(lessonRequest);
		lessonToUpdate.setId(lessonId);
		Lesson savedLesson = lessonRepository.save(lessonToUpdate);
		return lessonMapper.mapLessonToLessonRespone(savedLesson);
	}

	public Set<Lesson> getAllByIdSet(
				Set<Long> idSet) {
		return idSet.stream()
					       .map(this::isLessonExistbyId)
					       .collect(Collectors.toSet());
	}
}
