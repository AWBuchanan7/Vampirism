package mca.api.types;

import de.teamlapen.vampirism.VampirismMod;
import lombok.AllArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

@AllArgsConstructor
public class Gift {
    private String type;
    private String name;
    private int value;

    /**
     * Used for verifying if a given gift exists in the game's registries.
     * @return True if the item/block exists.
     */
    public boolean exists() {
        if (getType().equals("block")) {
            return Block.getBlockFromName(getName()) != null;
        } else if (getType().equals("item")) {
            return Item.getByNameOrId(getName()) != null;
        } else {
            VampirismMod.getLog().warn("Could not process gift '" + getName() + "'- bad type name of '" + getType() + "'. Must be 'item' or 'block'");
            return false;
        }
    }
    
    public String getName() {
    	return name;
    }
    
    public String getType() {
    	return type;
    }
    
    public int getValue() {
    	return value;
    }
}
