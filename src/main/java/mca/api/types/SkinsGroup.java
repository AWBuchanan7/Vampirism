package mca.api.types;

import lombok.AllArgsConstructor;
import mca.enums.EnumGender;

@AllArgsConstructor
public class SkinsGroup {
    private String gender;
    private String profession;
    private String[] paths;

    public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public EnumGender getGender() {
        return EnumGender.byName(gender);
    }
    
    public String getProfession() {
    	return profession;
    }
}
