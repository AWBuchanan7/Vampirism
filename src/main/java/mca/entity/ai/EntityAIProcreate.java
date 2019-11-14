package mca.entity.ai;

import de.teamlapen.vampirism.VampirismMod;
import mca.core.Constants;
import mca.core.minecraft.ItemsMCA;
import mca.entity.EntityVillagerMCA;
import mca.entity.data.PlayerSaveData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;

public class EntityAIProcreate extends EntityAIBase {
    private final EntityVillagerMCA villager;
    public int procreateTimer;

    public EntityAIProcreate(EntityVillagerMCA _villager) {
    	villager = _villager;
    }
    
    @Override
    public boolean shouldExecute() {
        return villager.get(EntityVillagerMCA.IS_PROCREATING);
    }

    @Override
    public void updateTask() {
        if (procreateTimer % 5 == 0) villager.spawnParticles(EnumParticleTypes.HEART);

        if (--procreateTimer <= 0) {
            villager.set(EntityVillagerMCA.IS_PROCREATING, false);

            EntityPlayer spousePlayer = villager.world.getPlayerEntityByUUID(villager.get(EntityVillagerMCA.SPOUSE_UUID).or(Constants.ZERO_UUID));
            if (spousePlayer != null) {
                villager.world.playSound(null, villager.posX, villager.posY, villager.posZ, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                spousePlayer.inventory.addItemStackToInventory(new ItemStack(villager.getRNG().nextBoolean() ? ItemsMCA.BABY_BOY : ItemsMCA.BABY_GIRL));
                PlayerSaveData.get(spousePlayer).setBabyPresent(true);

                if (villager.getRNG().nextFloat() < VampirismMod.getConfig().chanceToHaveTwins / 100)
                    spousePlayer.inventory.addItemStackToInventory(new ItemStack(villager.getRNG().nextBoolean() ? ItemsMCA.BABY_BOY : ItemsMCA.BABY_GIRL));
            }
        }
    }
}
