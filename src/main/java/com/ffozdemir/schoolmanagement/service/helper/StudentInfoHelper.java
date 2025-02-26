package com.ffozdemir.schoolmanagement.service.helper;

import com.ffozdemir.schoolmanagement.entity.enums.Note;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.repository.business.StudentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentInfoHelper {

	private final StudentInfoRepository studentInfoRepository;

	@Value("${final.exam.impact.percentage}")
	private Double finalExamPercentage;

	@Value("${midterm.exam.impact.percentage}")
	private Double midTermExamPercentage;


	public void validateLessonDuplication(
				Long studentId,
				String lessonName) {
		if (studentInfoRepository.isStudentInfoExistForLesson(studentId, lessonName)) {
			throw new ConflictException(String.format(ErrorMessages.ALREADY_CREATED_STUDENT_INFO_FOR_LESSON, lessonName));
		}
	}

	public Double calculateAverageScore(
				Double midTermExam,
				Double finalExam) {
		return ((midTermExam * midTermExamPercentage) + (finalExam * finalExamPercentage));
	}

	public Note checkLetterGrade(
				Double average) {
		if (average < 50.0) {
			return Note.FF;
		} else if (average < 60) {
			return Note.DD;
		} else if (average < 65) {
			return Note.CC;
		} else if (average < 70) {
			return Note.CB;
		} else if (average < 75) {
			return Note.BB;
		} else if (average < 80) {
			return Note.BA;
		} else {
			return Note.AA;
		}
	}


}
