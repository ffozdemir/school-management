package com.ffozdemir.schoolmanagement.service.business;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.exception.BadRequestException;
import com.ffozdemir.schoolmanagement.exception.ConflictException;
import com.ffozdemir.schoolmanagement.payload.mappers.EducationTermMapper;
import com.ffozdemir.schoolmanagement.payload.messages.ErrorMessages;
import com.ffozdemir.schoolmanagement.payload.messages.SuccessMessages;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.EducationTermResponse;
import com.ffozdemir.schoolmanagement.payload.response.business.ResponseMessage;
import com.ffozdemir.schoolmanagement.repository.business.EducationTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class EducationTermService {

	private final EducationTermRepository educationTermRepository;
	private final EducationTermMapper educationTermMapper;

	public ResponseMessage<EducationTermResponse> save(
				@Valid EducationTermRequest educationTermRequest) {
		//validation
		validateEducationTermDates(educationTermRequest);
		//save
		EducationTerm educationTerm = educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest);
		educationTermRepository.save(educationTerm);
		return ResponseMessage.<EducationTermResponse>builder()
					       .message(SuccessMessages.EDUCATION_TERM_SAVE)
					       .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm))
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
						if (!educationTerm.getStartDate()
									     .isAfter((educationTermRequest.getEndDate())) || educationTerm.getEndDate()
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

}
