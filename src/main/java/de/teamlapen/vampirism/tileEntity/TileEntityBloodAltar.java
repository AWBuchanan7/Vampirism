package de.teamlapen.vampirism.tileEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.BloodAltarPacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class TileEntityBloodAltar extends TileEntity {
	private boolean occupied = false;
	public final String BLOODALTAR_OCCUPIED_NBTKEY = "bloodaltaroccupied";
	private final double DISTANCE_AROUND_ALTAR = 10.0;
	private final int LIGHTNINGBOLT_AMOUNT = 10;
	
	public TileEntityBloodAltar() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.occupied = nbt.getBoolean(BLOODALTAR_OCCUPIED_NBTKEY);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(BLOODALTAR_OCCUPIED_NBTKEY, occupied);
	}
	

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean flag, EntityPlayer player) {
		if (flag != occupied && player != null)
			VampirismMod.modChannel.sendToAll(new BloodAltarPacket(flag,
					this.xCoord, this.yCoord, this.zCoord));
		occupied = flag;
		if (occupied && player != null) {
			startVampirismRitual(player);
		}
	}

	private void startVampirismRitual(EntityPlayer player) {
		Logger.i("TEBloodAltar", "Starting Vampirism-Ritual");
		List entityList = getWorldObj().loadedEntityList;
		ArrayList<EntityVillager> list = getVillagersInRadius(entityList,
				DISTANCE_AROUND_ALTAR);
		for (EntityVillager v : list) {
			for (int i = 0; i < LIGHTNINGBOLT_AMOUNT; i++)
				getWorldObj().addWeatherEffect(
						new EntityLightningBolt(getWorldObj(), v.posX, v.posY,
								v.posZ));
		}
		VampirePlayer.get(player).setLevel(list.size());
		Logger.i("TEBloodAltar", "Ritual ended, level: " + list.size());
	}

	private ArrayList<EntityVillager> getVillagersInRadius(List entityList,
			double distance) {
		ArrayList<EntityVillager> list = new ArrayList<EntityVillager>();
		for (Object entity : entityList) {
			if (EntityVillager.class.isInstance(entity)) {
				EntityVillager v = (EntityVillager) entity;
				if (Math.sqrt(Math.pow(v.posX - xCoord, 2)
						+ Math.pow(v.posY - yCoord, 2)
						+ Math.pow(v.posZ - zCoord, 2)) <= distance)
					list.add((EntityVillager) entity);
			}
		}
		return list;
	}
}
