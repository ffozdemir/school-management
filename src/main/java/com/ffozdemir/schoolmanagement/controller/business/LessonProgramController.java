package com.ffozdemir.schoolmanagement.controller.business;

import com.ffozdemir.schoolmanagement.payload.request.business.LessonProgramRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.LessonProgramResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

	private final LessonProgramService lessonProgramService;

	@PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
	@PostMapping("/save")
	public ResponseMessage<LessonProgramResponse> saveLessonProgram(
				@RequestBody @Valid LessonProgramRequest lessonProgramRequest) {
		return lessonProgramService.saveLessonProgram(lessonProgramRequest);
	}

}
