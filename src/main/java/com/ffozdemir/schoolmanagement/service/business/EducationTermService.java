package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.exception.ResourceNotFoundException;
import com.ffozdemir.schoolmanagement.payload.mappers.EducationTermMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermRequest;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermUpdateRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.EducationTermResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.EducationTermRepository;
import com.ffozdemir.schoolmanagement.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

	private final EducationTermRepository educationTermRepository;
	private final EducationTermMapper educationTermMapper;
	private final PageableHelper pageableHelper;

	public ResponseMessage<EducationTermResponse> save(
				@Valid EducationTermRequest educationTermRequest) {
		//validation
		validateEducationTermDates(educationTermRequest);
		//save
		EducationTerm educationTerm = educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest);
		EducationTerm savedEducationTerm = educationTermRepository.save(educationTerm);
		return ResponseMessage.<EducationTermResponse>builder()
					       .message(SuccessMessages.EDUCATION_TERM_SAVE)
					       .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
					       .httpStatus(HttpStatus.CREATED)
					       .build();
	}

	private void validateEducationTermDates(
				EducationTermRequest educationTermRequest) {
		//validate request by reg/start/end dates
		validateEducationTermDatesForRequest(educationTermRequest);
		//only one education term can exist in a year
		if (educationTermRepository.existByTermAndYear(educationTermRequest.getTerm(), educationTermRequest.getStartDate()
					                                                                               .getYear())) {
			throw new ConflictException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
		}
		//validate not to have any conflict with other education terms
		educationTermRepository.findByYear(educationTermRequest.getStartDate()
					                                   .getYear())
					.forEach(educationTerm->{
						if (educationTerm.getStartDate()
									     .isAfter((educationTermRequest.getEndDate())) || !educationTerm.getEndDate()
												                                                      .isBefore(educationTermRequest.getStartDate())) {
							throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
						}
					});
		//2025-06-01 - 2025-07-30
	}

	private void validateEducationTermDatesForRequest(
				EducationTermRequest educationTermRequest) {
		//reg < start < end

		if (educationTermRequest.getLastRegistrationDate()
					    .isAfter(educationTermRequest.getStartDate())) {
			throw new ConflictException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
		}

		if (educationTermRequest.getEndDate()
					    .isBefore(educationTermRequest.getStartDate())) {
			throw new ConflictException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
		}
	}


	public List<EducationTermResponse> getAllEducationTerms() {
		List<EducationTerm> educationTerms = educationTermRepository.findAll();
		return educationTerms.stream()
					       .map(educationTermMapper::mapEducationTermToEducationTermResponse)
					       .collect(Collectors.toList());
	}

	public ResponseMessage<EducationTermResponse> updateEducationTerm(
				@Valid EducationTermUpdateRequest educationTermRequest,
				Long educationTermId) {
		//check if education term exist
		EducationTerm foundEducationTerm = isEducationTermExist(educationTermId);
		//update entity with partial/whole new data
		EducationTerm updatedEducationTerm = educationTermMapper.updateEducationTermWithEducationTermUpdateRequest(educationTermRequest, foundEducationTerm);
		//validate dates
		validateEducationTermDatesForPartialUpdate(updatedEducationTerm);
		//since it has passed the validation, we can now save it into DB
		// and return by mapping it to responseDTO
		return ResponseMessage.<EducationTermResponse>builder()
					       .message(SuccessMessages.EDUCATION_TERM_UPDATE)
					       .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(educationTermRepository.save(updatedEducationTerm)))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}

	/**
	 * For partial update, we need the whole entity to be able to validate dates.
	 * For example; if the user wants to update only the startDate, then we need to acquire the endDate from the existing entity.
	 * That's why, we map the updateDTO to updatedEntity first, then validate its dates with this method.
	 *
	 * @param educationTerm updated entity object
	 */
	private void validateEducationTermDatesForPartialUpdate(
				EducationTerm educationTerm) {
		//reg<start
		if (educationTerm.getLastRegistrationDate()
					    .isAfter(educationTerm.getStartDate())) {
			throw new ConflictException(ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
		}
		//end>start
		if (educationTerm.getEndDate()
					    .isBefore(educationTerm.getStartDate())) {
			throw new ConflictException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
		}
	}

	public EducationTerm isEducationTermExist(
				Long educationTermId) {
		return educationTermRepository.findById(educationTermId)
					       .orElseThrow(()->new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE));
	}

	public EducationTermResponse getEducationTermById(
				Long educationTermId) {
		EducationTerm educationTerm = isEducationTermExist(educationTermId);
		return educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm);
	}

	public Page<EducationTermResponse> getByPage(
				int page,
				int size,
				String sort,
				String type) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		//fetch paginated and sorted data from DB
		Page<EducationTerm> educationTerms = educationTermRepository.findAll(pageable);
		//use mapper
		return educationTerms.map(educationTermMapper::mapEducationTermToEducationTermResponse);
	}

	public ResponseMessage<EducationTermResponse> deleteById(
				Long educationTermId) {
		EducationTerm educationTerm = isEducationTermExist(educationTermId);
		educationTermRepository.deleteById(educationTermId);
		return ResponseMessage.<EducationTermResponse>builder()
					       .message(SuccessMessages.EDUCATION_TERM_DELETE)
					       .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm))
					       .httpStatus(HttpStatus.OK)
					       .build();
	}
}
