package com.ffozdemir.schoolmanagement.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingResponse {

	private Long id;
	private String description;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime stopTime;
	private Long advisorTeacherId;
	private List<User> students;

}
