package com.ffozdemir.schoolmanagement.service.helper;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LessonProgramDuplicationHelper {
	public List<LessonProgram> removeDuplicates(
				List<LessonProgram> existingLessonPrograms,
				List<LessonProgram> newLessonPrograms) {
		Set<Long> existingLessonProgramIds = existingLessonPrograms.stream()
					                                      .map(LessonProgram::getId)
					                                      .collect(Collectors.toSet());

		return newLessonPrograms.stream()
					       .filter(lessonProgram->!existingLessonProgramIds.contains(lessonProgram.getId()))
					       .collect(Collectors.toList());
	}
}
