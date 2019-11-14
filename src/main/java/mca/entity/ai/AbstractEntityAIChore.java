package mca.entity.ai;

import com.google.common.base.Optional;

import de.teamlapen.vampirism.VampirismMod;
import mca.core.Constants;
import mca.entity.EntityVillagerMCA;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractEntityAIChore extends EntityAIBase {
    protected final EntityVillagerMCA villager;

    public AbstractEntityAIChore(EntityVillagerMCA entityIn) {
        this.villager = entityIn;
        this.setMutexBits(4);
    }

    @Override
    public void updateTask() {
        super.updateTask();

        if (!getAssigningPlayer().isPresent()) {
            VampirismMod.getLog().warn("Force-stopped chore because assigning player was not present.");
            villager.stopChore();
        }
    }

    Optional<EntityPlayer> getAssigningPlayer() {
        EntityPlayer player = villager.world.getPlayerEntityByUUID(villager.get(EntityVillagerMCA.CHORE_ASSIGNING_PLAYER).or(Constants.ZERO_UUID));
        return Optional.fromNullable(player);
    }
}