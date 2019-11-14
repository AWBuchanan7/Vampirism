package mca.entity.data;

import lombok.Getter;
import mca.core.Constants;
import mca.entity.EntityVillagerMCA;
import mca.enums.EnumDialogueType;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class PlayerHistory {
    @Getter private int hearts;
    @Getter private int interactionFatigue;
    @Getter private boolean giftPresent;
    @Getter private int greetTimer;
    @Getter private EnumDialogueType dialogueType;

    @Getter private UUID playerUUID;
    private EntityVillagerMCA villager;

    public boolean isGiftPresent() {
		return giftPresent;
	}

	public void setGiftPresent(boolean giftPresent) {
		this.giftPresent = giftPresent;
	}

	public int getGreetTimer() {
		return greetTimer;
	}

	public void setGreetTimer(int greetTimer) {
		this.greetTimer = greetTimer;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	public EntityVillagerMCA getVillager() {
		return villager;
	}

	public void setVillager(EntityVillagerMCA villager) {
		this.villager = villager;
	}

	public int getHearts() {
		return hearts;
	}

	public EnumDialogueType getDialogueType() {
		return dialogueType;
	}

	public void setInteractionFatigue(int interactionFatigue) {
		this.interactionFatigue = interactionFatigue;
	}

	private PlayerHistory() {
        hearts = 0;
        interactionFatigue = 0;
        giftPresent = false;
        greetTimer = 0;
        playerUUID = Constants.ZERO_UUID;
        dialogueType = EnumDialogueType.ADULT;
    }

    public static PlayerHistory getNew(EntityVillagerMCA villager, UUID uuid) {
        PlayerHistory history = new PlayerHistory();
        history.villager = villager;
        history.playerUUID = uuid;

        if (villager.isChild()) {
            history.setDialogueType(EnumDialogueType.CHILD);
        } else {
            history.setDialogueType(EnumDialogueType.ADULT);
        }
        return history;
    }

    public static PlayerHistory fromNBT(EntityVillagerMCA villager, UUID uuid, NBTTagCompound nbt) {
        PlayerHistory history = new PlayerHistory();
        history.villager = villager;
        history.playerUUID = uuid;

        history.hearts = nbt.getInteger("hearts");
        history.interactionFatigue = nbt.getInteger("interactionFatigue");
        history.giftPresent = nbt.getBoolean("giftPresent");
        history.greetTimer = nbt.getInteger("greetTimer");
        history.dialogueType = EnumDialogueType.byValue(nbt.getString("dialogueType"));

        return history;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("hearts", hearts);
        nbt.setInteger("interactionFatigue", interactionFatigue);
        nbt.setBoolean("giftPresent", giftPresent);
        nbt.setInteger("greetTimer", greetTimer);
        nbt.setString("dialogueType", dialogueType.getId());

        return nbt;
    }

    public void setHearts(int value) {
        hearts = value;
        villager.updatePlayerHistoryMap(this);
    }

    public void changeHearts(int value) {
        hearts += value;
        villager.updatePlayerHistoryMap(this);
    }

    public void changeInteractionFatigue(int value) {
        interactionFatigue += value;
        villager.updatePlayerHistoryMap(this);
    }

    public int getInteractionFatigue() {
    	return interactionFatigue;
    }
    
    public void update() {
        // every 5 minutes reduce interaction fatigues
        if (villager.ticksExisted % 6000 == 0) changeInteractionFatigue(-1);
    }

    public void setDialogueType(EnumDialogueType type) {
        this.dialogueType = type;
        villager.updatePlayerHistoryMap(this);
    }
}
