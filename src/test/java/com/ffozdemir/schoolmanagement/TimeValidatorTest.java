package com.ffozdemir.schoolmanagement;

import com.ffozdemir.schoolmanagement.entity.concretes.business.LessonProgram;
import com.ffozdemir.schoolmanagement.entity.enums.Day;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.service.validator.TimeValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeValidatorTest {

	public final TimeValidator timeValidator = new TimeValidator();


	@Test
	void testNoDuplicateLessonProgram() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0)));
		timeValidator.checkDuplicateLessonProgram(lessonPrograms);
	}

	@Test
	void testDuplicateStartTimeThrowsException() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
		assertThrows(BadRequestException.class, ()->timeValidator.checkDuplicateLessonProgram(lessonPrograms));
	}

	@Test
	void testOverlappingTimesThrowsException() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)));
		assertThrows(BadRequestException.class, ()->timeValidator.checkDuplicateLessonProgram(lessonPrograms));
	}

	@Test
	void testOverlappingStopTimesThrowsException() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
		assertThrows(BadRequestException.class, ()->timeValidator.checkDuplicateLessonProgram(lessonPrograms));
	}

	@Test
	void testAdjacentLessonProgramsNoException() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
		timeValidator.checkDuplicateLessonProgram(lessonPrograms);
	}

	@Test
	void testAdjacentLessonProgramsWithDifferentDaysNoException() {
		List<LessonProgram> lessonPrograms = new ArrayList<>();
		lessonPrograms.add(new LessonProgram(Day.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		lessonPrograms.add(new LessonProgram(Day.THURSDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
		timeValidator.checkDuplicateLessonProgram(lessonPrograms);
	}


}
