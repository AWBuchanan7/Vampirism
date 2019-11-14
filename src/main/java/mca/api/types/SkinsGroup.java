package mca.api.types;

import lombok.AllArgsConstructor;
import mca.enums.EnumGender;

@AllArgsConstructor
public class SkinsGroup {
    private String gender;
    private String profession;
    private String[] paths;

    public EnumGender getGender() {
        return EnumGender.byName(gender);
    }
    
    public String getProfession() {
    	return profession;
    }
}
