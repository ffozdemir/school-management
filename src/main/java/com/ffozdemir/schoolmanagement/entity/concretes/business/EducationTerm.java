package com.ffozdemir.schoolmanagement.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ffozdemir.schoolmanagement.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationTerm {

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Long id;

	@Enumerated(javax.persistence.EnumType.STRING)
	private Term term;

	@Column(name = "start_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@Column(name = "end_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	@Column(name = "last_registration_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate lastRegistrationDate;

	@OneToMany(mappedBy = "educationTerm", cascade = CascadeType.ALL)
	private List<LessonProgram> lessonProgram;

}
