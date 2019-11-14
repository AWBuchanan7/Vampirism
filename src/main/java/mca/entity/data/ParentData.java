package mca.entity.data;

import lombok.Getter;
import mca.core.Constants;
import mca.entity.EntityVillagerMCA;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

import static mca.entity.EntityVillagerMCA.*;

@Getter
public class ParentData {
    private UUID parent1UUID = Constants.ZERO_UUID;
    private UUID parent2UUID = Constants.ZERO_UUID;
    private String parent1Name = "";
    private String parent2Name = "";

    public static ParentData fromNBT(NBTTagCompound nbt) {
        ParentData data = new ParentData();
        data.parent1UUID = nbt.getUniqueId("parent1UUID");
        data.parent2UUID = nbt.getUniqueId("parent2UUID");
        data.parent1Name = nbt.getString("parent1Name");
        data.parent2Name = nbt.getString("parent2Name");
        return data;
    }

    public UUID getParent1UUID() {
		return parent1UUID;
	}

	public void setParent1UUID(UUID parent1uuid) {
		parent1UUID = parent1uuid;
	}

	public UUID getParent2UUID() {
		return parent2UUID;
	}

	public void setParent2UUID(UUID parent2uuid) {
		parent2UUID = parent2uuid;
	}

	public String getParent1Name() {
		return parent1Name;
	}

	public void setParent1Name(String parent1Name) {
		this.parent1Name = parent1Name;
	}

	public String getParent2Name() {
		return parent2Name;
	}

	public void setParent2Name(String parent2Name) {
		this.parent2Name = parent2Name;
	}

	public static ParentData create(UUID parent1UUID, UUID parent2UUID, String parent1Name, String parent2Name) {
        ParentData data = new ParentData();
        data.parent1UUID = parent1UUID;
        data.parent2UUID = parent2UUID;
        data.parent1Name = parent1Name;
        data.parent2Name = parent2Name;
        return data;
    }

    public static ParentData fromVillager(EntityVillagerMCA villager) {
        ParentData data = new ParentData();
        data.parent1Name = villager.get(VILLAGER_NAME);
        data.parent1UUID = villager.getUniqueID();
        data.parent2Name = villager.get(SPOUSE_NAME);
        data.parent2UUID = villager.get(SPOUSE_UUID).or(Constants.ZERO_UUID);
        return data;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("parent1UUID", parent1UUID);
        nbt.setUniqueId("parent2UUID", parent2UUID);
        nbt.setString("parent1Name", parent1Name);
        nbt.setString("parent2Name", parent2Name);
        return nbt;
    }

    public ParentData setParents(UUID parent1UUID, String parent1Name, UUID parent2UUID, String parent2Name) {
        this.parent1UUID = parent1UUID;
        this.parent2UUID = parent2UUID;
        this.parent1Name = parent1Name;
        this.parent2Name = parent2Name;
        return this;
    }

    public Entity getParentEntity(World world, UUID uuid) {
        return world.loadedEntityList.stream().filter(e -> e.getUniqueID().equals(uuid)).findFirst().orElse(null);  // TODO: This should definitely be changed to an optional
    }

    public Entity[] getParentEntities(World world) {
        return new Entity[]{
                getParentEntity(world, getParent1UUID()),
                getParentEntity(world, getParent2UUID())
        };
    }
}
