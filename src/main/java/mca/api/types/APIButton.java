package mca.api.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mca.entity.EntityVillagerMCA;
import mca.enums.EnumConstraint;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * APIButton is a button defined in assets/mca/api/gui/*
 * <p>
 * These buttons are dynamically attached to a GuiScreen and include additional instruction/constraints for building
 * and processing interactions.
 */

public class APIButton {
     private int id;             // numeric id
     private String identifier;  // string identifier for the button in the .lang file
     private int x;              // x position
     private int y;              // y position
     private int width;          // button width
     private int height;         // button height
     private boolean notifyServer;   // whether the button press is sent to the server for processing
     private boolean targetServer;   // whether the button is processed by the villager or the server itself
    private String constraints;     // list of EnumConstraints separated by a pipe character |
     private boolean isInteraction;  // whether the button is an interaction that generates a response and boosts/decreases hearts

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isNotifyServer() {
		return notifyServer;
	}

	public void setNotifyServer(boolean notifyServer) {
		this.notifyServer = notifyServer;
	}

	public boolean isTargetServer() {
		return targetServer;
	}

	public void setTargetServer(boolean targetServer) {
		this.targetServer = targetServer;
	}

	public boolean isInteraction() {
		return isInteraction;
	}

	public void setInteraction(boolean isInteraction) {
		this.isInteraction = isInteraction;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public List<EnumConstraint> getConstraints() {
        return EnumConstraint.fromStringList(constraints);
    }

    /**
     * Determines if the given villager and player match the constraints for this button, allowing the action to be performed
     *
     * @param villager Instance of the EntityVillagerMCA the button would perform the action on
     * @param player   Instance of the EntityPlayer performing the action
     * @return boolean whether the button is valid for a constraint
     */
    public boolean isValidForConstraint(EntityVillagerMCA villager, EntityPlayer player) {
        List<EnumConstraint> constraints = getConstraints();

        if (constraints.contains(EnumConstraint.ADULTS) && !villager.isChild()) {
            return true;
        } else if (constraints.contains(EnumConstraint.SPOUSE) && villager.isMarriedTo(player.getUniqueID())) {
            return true;
        } else if (constraints.contains(EnumConstraint.NOT_SPOUSE) && !villager.isMarriedTo(player.getUniqueID())) {
            return true;
        } else if (constraints.contains(EnumConstraint.FAMILY) && (villager.playerIsParent(player) || villager.isMarriedTo(player.getUniqueID()))){
            return true;
        } else if (constraints.contains(EnumConstraint.NOT_FAMILY) && !(villager.playerIsParent(player) || villager.isMarriedTo(player.getUniqueID()))) {
            return true;
        } else if (constraints.isEmpty()) {
            return true;
        }
        return false;
    }
    
    public String getIdentifier() {
    	return identifier;
    }
}
