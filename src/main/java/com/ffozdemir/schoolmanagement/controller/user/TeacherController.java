package com.ffozdemir.schoolmanagement.controller.user;

import com.ffozdemir.schoolmanagement.payload.request.business.AddLessonProgram;
import com.ffozdemir.schoolmanagement.payload.request.user.TeacherRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.payload.response.user.StudentResponse;
import com.ffozdemir.schoolmanagement.payload.response.user.UserResponse;
import com.ffozdemir.schoolmanagement.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

	private final TeacherService teacherService;

	@PreAuthorize("hasAnyAuthority('Admin')")
	@PostMapping("/save")
	public ResponseMessage<UserResponse> saveTeacher(
				@RequestBody @Valid TeacherRequest teacherRequest) {
		return teacherService.saveTeacher(teacherRequest);
	}

	@PreAuthorize("hasAnyAuthority('Admin')")
	@PutMapping("/update/{userId}")
	public ResponseMessage<UserResponse> updateTeacher(
				@PathVariable Long userId,
				@RequestBody @Valid TeacherRequest teacherRequest) {
		return teacherService.updateTeacherById(teacherRequest, userId);
	}

	//teacher will log in and see his/her students via lesson programs
	@PreAuthorize("hasAnyAuthority('Teacher')")
	@GetMapping("/getByAdvisorTeacher")
	public List<StudentResponse> getAllStudentByAdvisorTeacher(
				HttpServletRequest httpServletRequest) {
		return teacherService.getAllStudentByAdvisorTeacher(httpServletRequest);
	}

	//managers can add lesson programs to teacher
	@PreAuthorize("hasAnyAuthority('Admin', 'Dean', 'ViceDean')")
	@PostMapping("/addLessonProgram")
	public ResponseMessage<UserResponse> addLessonProgramToTeacher(
				@RequestBody @Valid AddLessonProgram addLessonProgram) {
		return teacherService.addLessonProgram(addLessonProgram);
	}


	@PreAuthorize("hasAnyAuthority('Admin')")
	@DeleteMapping("/deleteTeacherById/{teacherId}")
	public ResponseEntity<String> deleteTeacherById(
				@PathVariable Long teacherId) {
		return ResponseEntity.ok(teacherService.deleteTeacherById(teacherId));
	}

	@PreAuthorize("hasAnyAuthority('Admin', 'Dean', 'ViceDean')")
	@GetMapping("/getAllByTeacher")
	public ResponseMessage<List<UserResponse>> getAllByTeacher() {
		return teacherService.getAllByTeacher();
	}

	@PreAuthorize("hasAnyAuthority('Admin', 'Dean', 'ViceDean')")
	@GetMapping("/getAllTeacherByPage")
	public ResponseEntity<Page<UserResponse>> getAllTeacherByPage(
				@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "size", defaultValue = "10") int size,
				@RequestParam(value = "sort", defaultValue = "name") String sort,
				@RequestParam(value = "type", defaultValue = "desc") String type) {
		Page<UserResponse> userResponses = teacherService.getAllTeacherByPage(page, size, sort, type);
		return ResponseEntity.ok(userResponses);
	}


}
