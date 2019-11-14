package mca.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;



public enum EnumDialogueType {
    CHILDP("childp"),
    CHILD("child"),
    ADULT("adult"),
    SPOUSE("spouse");

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private EnumDialogueType(String id) {
		this.id = id;
	}

	String id;

    public static EnumDialogueType byValue(String value) {
        return Arrays.stream(values()).filter(c -> c.getId().equals(value)).findFirst().orElse(null);
    }
}

