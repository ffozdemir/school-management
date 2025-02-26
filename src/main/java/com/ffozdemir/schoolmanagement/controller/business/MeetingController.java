package com.ffozdemir.schoolmanagement.controller.business;

import com.ffozdemir.schoolmanagement.payload.request.business.MeetingRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.MeetingResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.service.business.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/meeting")
public class MeetingController {

	private final MeetingService meetingService;

	//response status annotation
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyAuthority('Teacher')")
	@PostMapping("/save")
	public ResponseMessage<MeetingResponse> saveMeeting(
				HttpServletRequest httpServletRequest,
				@RequestBody @Valid MeetingRequest meetingRequest) {
		return meetingService.save(httpServletRequest, meetingRequest);
	}

	@PreAuthorize("hasAnyAuthority('Teacher')")
	@PutMapping("/update/{meetingId}")
	public ResponseMessage<MeetingResponse> updateMeeting(
				@RequestBody @Valid MeetingRequest meetingRequest,
				@PathVariable Long meetingId,
				HttpServletRequest httpServletRequest) {
		return meetingService.update(meetingRequest, meetingId, httpServletRequest);
	}


	//TODO furkan
	@PreAuthorize("hasAnyAuthority('Admin','Teacher')")
	@DeleteMapping("/delete/{meetingId}")
	public ResponseMessage deleteById(@PathVariable Long meetingId){
		//return meetingService.deleteById(meetingId);
		return null;
	}

	//TODO neslihan
	@PreAuthorize("hasAnyAuthority('Teacher')")
	@GetMapping("/getAllByPageTeacher")
	public Page<MeetingResponse> getAllByPageTeacher(
				HttpServletRequest httpServletRequest,
				@RequestParam(value = "page",defaultValue = "0") int page,
				@RequestParam(value = "size",defaultValue = "10") int size){
		//return meetingService.getAllByPageTeacher(page,size,httpServletRequest);
		return null;
	}

	//TODO edip
	@PreAuthorize("hasAnyAuthority('Teacher','Student')")
	@GetMapping("/getAll")
	public List<MeetingResponse> getAllMeetings(HttpServletRequest httpServletRequest){
		//return meetingService.getAll(httpServletRequest);
		return null;
	}

	//TODO ismail
	@PreAuthorize("hasAnyAuthority('Admin')")
	@GetMapping("/getAllByPage")
	public Page<MeetingResponse>getAllByPage(
				@RequestParam(value = "page") int page,
				@RequestParam(value = "size") int size){
		//return meetingService.getAllByPage(page, size);
		return null;
	}

}
