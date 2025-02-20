package com.ffozdemir.schoolmanagement.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.entity.concretes.business.Lesson;
import com.ffozdemir.schoolmanagement.entity.enums.Day;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonProgramResponse {

	private Long lessonProgramId;
	private Day day;
	private LocalTime startTime;
	private LocalTime stopTime;
	private Set<Lesson> lessonName;
	private EducationTerm educationTerm;

}
