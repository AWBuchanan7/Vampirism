package mca.enums;

import java.util.Arrays;

import de.teamlapen.vampirism.VampirismMod;

public enum EnumMoveState {
    MOVE(0, ""),
    STAY(1, "gui.label.staying"),
    FOLLOW(2, "gui.label.following");

    private EnumMoveState(int id, String friendlyName) {
		this.id = id;
		this.friendlyName = friendlyName;
	}

	int id;
    String friendlyName;

    public static EnumMoveState byId(int id) {
        return Arrays.stream(values()).filter(s -> s.id == id).findFirst().orElse(MOVE);
    }
    
    public int getId() {
    	return id;
    }

    public String getFriendlyName() {
        return VampirismMod.getLocalizer().localize(friendlyName);
    }
}

