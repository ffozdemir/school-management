package com.ffozdemir.schoolmanagement.payload.mappers;

import com.ffozdemir.schoolmanagement.entity.concretes.business.EducationTerm;
import com.ffozdemir.schoolmanagement.payload.request.business.EducationTermRequest;
import com.ffozdemir.schoolmanagement.payload.response.business.EducationTermResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
			//with this parameter, MapStruct will always check source properties if they have null value or not.
			nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
			//If a source bean property equals null, the target bean property will be ignored and retain its existing value. So, we will be able to perform partial update.
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationTermMapper {

	EducationTerm mapEducationTermRequestToEducationTerm(
				EducationTermRequest educationTermRequest);

	//TODO check the usage
	EducationTerm updateEducationTermWithEducationTermRequest(
				EducationTermRequest educationTermRequest,
				@MappingTarget EducationTerm educationTerm);

	EducationTermResponse mapEducationTermToEducationTermResponse(
				EducationTerm educationTerm);
}
