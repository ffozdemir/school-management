package com.ffozdemir.schoolmanagement.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ffozdemir.schoolmanagement.entity.concretes.user.User;
import com.ffozdemir.schoolmanagement.entity.enums.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentInfo {

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Long id;

	private Integer absentee;

	private Double midtermExam;

	private Double finalExam;

	private String infoNote;

	private Double examAverage;

	@Enumerated(EnumType.STRING)
	private Note letterGrade;

	@ManyToOne
	@JsonIgnore
	private User teacher;

	@ManyToOne
	@JsonIgnore
	private User student;

	@ManyToOne
	private Lesson lesson;

	@OneToOne
	private EducationTerm educationTerm;

}
