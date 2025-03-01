package com.ffozdemir.schoolmanagement.controller.business;

import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermRequest;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.EducationTermResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/educationTerm")
@RequiredArgsConstructor
public class EducationTermController {

	private final EducationTermService educationTermService;

	@PreAuthorize("hasAnyAuthority('Admin' , 'Dean')")
	@PostMapping("/save")
	public ResponseMessage<EducationTermResponse> save(
				@Valid @RequestBody EducationTermRequest educationTermRequest) {
		return educationTermService.save(educationTermRequest);
	}

	@PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher')")
	@PutMapping("/update/{educationTermId}")
	public ResponseMessage<EducationTermResponse> updateEducationTerm(
				@Valid @RequestBody EducationTermUpdateRequest educationTermUpdateRequest,
				@PathVariable Long educationTermId) {
		return educationTermService.updateEducationTerm(educationTermUpdateRequest, educationTermId);
	}


	@PreAuthorize("hasAnyAuthority('Admin' , 'Dean', 'ViceDean', 'Teacher')")
	@GetMapping("/getAll")
	public List<EducationTermResponse> getAllEducationTerms() {
		return educationTermService.getAllEducationTerms();
	}

	@PreAuthorize("hasAnyAuthority('Admin' , 'Dean', 'ViceDean', 'Teacher')")
	@GetMapping("/{educationTermId}")
	public EducationTermResponse getEducationTerm(
				@PathVariable Long educationTermId) {
		return educationTermService.getEducationTermById(educationTermId);
	}

	@PreAuthorize("hasAnyAuthority('Admin' , 'Dean', 'ViceDean', 'Teacher')")
	@GetMapping("/getByPage")
	public Page<EducationTermResponse> getByPage(
				@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "size", defaultValue = "10") int size,
				@RequestParam(value = "sort", defaultValue = "term") String sort,
				@RequestParam(value = "type", defaultValue = "desc") String type) {
		return educationTermService.getByPage(page, size, sort, type);
	}

	@PreAuthorize("hasAnyAuthority('Admin' , 'Dean', 'ViceDean', 'Teacher')")
	@DeleteMapping("/delete/{educationTermId}")
	public ResponseMessage<EducationTermResponse> deleteEducationTerm(
				@PathVariable Long educationTermId) {
		return educationTermService.deleteById(educationTermId);
	}


}
