package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.payload.request.business.LessonRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonResponse;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

	public Lesson mapLessonRequestToLesson(
				LessonRequest lessonRequest) {
		return Lesson.builder()
					       .lessonName(lessonRequest.getLessonName())
					       .creditScore(lessonRequest.getCreditScore())
					       .isCompulsory(lessonRequest.getIsCompulsory())
					       .build();
	}

	public LessonResponse mapLessonToLessonRespone(
				Lesson lesson) {
		return LessonResponse.builder()
					       .lessonId(lesson.getId())
					       .lessonName(lesson.getLessonName())
					       .creditScore(lesson.getCreditScore())
					       .isCompulsory(lesson.getIsCompulsory())
					       .build();
	}
}
