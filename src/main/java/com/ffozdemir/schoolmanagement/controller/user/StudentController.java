package com.ffozdemir.schoolmanagement.controller.user;

import com.ffozdemir.schoolmanagement.payload.request.user.StudentRequest;
import com.ffozdemir.schoolmanagement.payload.request.user.StudentUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.StudentResponse;
import com.ffozdemir.schoolmanagement.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@PreAuthorize("hasAnyAuthority('Admin')")
	@PostMapping("/save")
	public ResponseMessage<StudentResponse> save(
				@RequestBody @Valid StudentRequest studentRequest) {
		return studentService.save(studentRequest);
	}

	@PreAuthorize("hasAnyAuthority('Student')")
	@PutMapping("/update")
	public ResponseEntity<String> updateStudent(
				HttpServletRequest httpServletRequest,
				@RequestBody @Valid StudentUpdateRequest studentUpdateRequest) {
		return ResponseEntity.ok(studentService.updateStudent(httpServletRequest, studentUpdateRequest));
	}


}
