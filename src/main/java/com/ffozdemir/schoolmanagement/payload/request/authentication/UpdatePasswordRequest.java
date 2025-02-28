package com.ffozdemir.schoolmanagement.payload.request.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

	@NotBlank(message = "Please provide your old password")
	private String oldPassword;

	@NotBlank(message = "Please provide your new password")
	private String newPassword;

}
