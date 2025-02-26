package com.ffozdemir.schoolmanagement.controller.business;

import com.ffozdemir.schoolmanagement.payload.request.business.StudentInfoRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.business.StudentInfoResponse;
import com.ffozdemir.schoolmanagement.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {
	private final StudentInfoService studentInfoService;

	@PreAuthorize("hasAnyAuthority('Teacher')")
	@PostMapping("/save")
	public ResponseMessage<StudentInfoResponse> saveStudentInfo(
				HttpServletRequest httpServletRequest,
				@RequestBody @Valid StudentInfoRequest studentInfoRequest) {
		return studentInfoService.saveStudentInfo(httpServletRequest, studentInfoRequest);
	}
}
