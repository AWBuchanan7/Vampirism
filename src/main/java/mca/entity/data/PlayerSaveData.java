package mca.entity.data;

import lombok.Getter;
import mca.core.Constants;
import mca.enums.EnumMarriageState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.teamlapen.vampirism.VampirismMod;

public class PlayerSaveData extends WorldSavedData {
    private static final String PREFIX = "MCA-Player-V1-";

    private UUID spouseUUID = Constants.ZERO_UUID;
    private EnumMarriageState marriageState = EnumMarriageState.NOT_MARRIED;
    private String spouseName = "";
    private boolean babyPresent = false;

    public PlayerSaveData(String id) {
        super(id);
    }

    public UUID getSpouseUUID() {
		return spouseUUID;
	}

	public void setSpouseUUID(UUID spouseUUID) {
		this.spouseUUID = spouseUUID;
	}

	public EnumMarriageState getMarriageState() {
		return marriageState;
	}

	public void setMarriageState(EnumMarriageState marriageState) {
		this.marriageState = marriageState;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}

	public static String getPrefix() {
		return PREFIX;
	}

	public static PlayerSaveData get(EntityPlayer player) {
        String dataId = PREFIX + player.getUniqueID().toString();
        PlayerSaveData data = (PlayerSaveData) player.world.loadData(PlayerSaveData.class, dataId);

        if (data == null) {
            data = new PlayerSaveData(dataId);
            player.world.setData(dataId, data);
        }

        return data;
    }

    public static PlayerSaveData getExisting(World world, UUID uuid) {
        return (PlayerSaveData) world.loadData(PlayerSaveData.class, PREFIX + uuid.toString());
    }
    
    public boolean isBabyPresent() {
    	return babyPresent;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setUniqueId("spouseUUID", spouseUUID);
        nbt.setInteger("marriageState", marriageState.getId());
        nbt.setString("spouseName", spouseName);
        nbt.setBoolean("babyPresent", babyPresent);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        spouseUUID = nbt.getUniqueId("spouseUUID");
        marriageState = EnumMarriageState.byId(nbt.getInteger("marriageState"));
        spouseName = nbt.getString("spouseName");
        babyPresent = nbt.getBoolean("babyPresent");
    }

    public boolean isMarriedOrEngaged() {
        return marriageState != EnumMarriageState.NOT_MARRIED;
    }

    public void marry(UUID uuid, String name) {
        spouseUUID = uuid;
        marriageState = EnumMarriageState.MARRIED;
        spouseName = name;
        markDirty();
    }

    public void endMarriage() {
        spouseUUID = Constants.ZERO_UUID;
        spouseName = "";
        marriageState = EnumMarriageState.NOT_MARRIED;
        markDirty();
    }

    public void setBabyPresent(boolean value) {
        this.babyPresent = value;
        markDirty();
    }

    public void reset() {
        endMarriage();
        setBabyPresent(false);
        markDirty();
    }

    public List<Field> getDataFields() {
        return Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> !Modifier.isFinal(f.getModifiers())).collect(Collectors.toList());
    }

    public void dump(EntityPlayer player) {
        for (Field f : getDataFields()) {
            try {
                player.sendMessage(new TextComponentString(f.getName() + " = " + f.get(this).toString()));
            } catch (Exception e) {
            	VampirismMod.getLog().error("Error dumping player data!");
            	VampirismMod.getLog().error(e);
            }
        }
    }
}
