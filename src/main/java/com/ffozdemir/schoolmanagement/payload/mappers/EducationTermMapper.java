package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.EducationTermResponse;
import org.springframework.stereotype.Component;

@Component
public class EducationTermMapper {

	public EducationTerm mapEducationTermRequestToEducationTerm(
				EducationTermRequest educationTermRequest) {
		return EducationTerm.builder()
					       .term(educationTermRequest.getTerm())
					       .startDate(educationTermRequest.getStartDate())
					       .endDate(educationTermRequest.getEndDate())
					       .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
					       .build();
	}

	public EducationTermResponse mapEducationTermToEducationTermResponse(
				EducationTerm educationTerm) {
		return EducationTermResponse.builder()
					       .id(educationTerm.getId())
					       .term(educationTerm.getTerm())
					       .startDate(educationTerm.getStartDate())
					       .endDate(educationTerm.getEndDate())
					       .lastRegistrationDate(educationTerm.getLastRegistrationDate())
					       .build();
	}


}
