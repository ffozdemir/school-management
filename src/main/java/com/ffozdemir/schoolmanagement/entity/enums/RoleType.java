package com.ffozdemir.schoolmanagement.entity.enums;

import lombok.Getter;

@Getter
public enum RoleType {
	ADMIN("Admin"), TEACHER("Teacher"), STUDENT("Student"), MANAGER("Manager"), ASSISTANT_MANAGER("ViceDean");

	RoleType(String name) {
		this.name = name;
	}

	public final String name;
}
