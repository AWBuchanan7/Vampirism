package mca.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.*;

import java.util.Arrays;
import java.util.Optional;

import de.teamlapen.vampirism.VampirismMod;

@AllArgsConstructor
public enum EnumChore {
    NONE(0, "none", null),
    PROSPECT(1, "gui.label.prospecting", ItemPickaxe.class),
    HARVEST(2, "gui.label.harvesting", ItemHoe.class),
    CHOP(3, "gui.label.chopping", ItemAxe.class),
    HUNT(4, "gui.label.hunting", ItemSword.class),
    FISH(5, "gui.label.fishing", ItemFishingRod.class);

    private EnumChore(int id, String friendlyName, Class toolType) {
		this.id = id;
		this.friendlyName = friendlyName;
		this.toolType = toolType;
	}

	public Class getToolType() {
		return toolType;
	}

	public void setToolType(Class toolType) {
		this.toolType = toolType;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	@Getter int id;
    String friendlyName;
    @Getter Class toolType;

    public static EnumChore byId(int id) {
        Optional<EnumChore> state = Arrays.stream(values()).filter((e) -> e.id == id).findFirst();
        return state.orElse(NONE);
    }

    public String getFriendlyName() {
        return VampirismMod.getLocalizer().localize(this.friendlyName);
    }
    
    public int getId() {
    	return id;
    }
}

