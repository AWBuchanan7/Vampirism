package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Entity Ghost
 */
public class EntityGhost extends EntityVampirism implements IMob {
    public EntityGhost(World p_i1595_1_) {
        super(p_i1595_1_);
        ((PathNavigateGround) getNavigator()).setCanSwim(true);
        this.setSize(0.8F, 1.95F);
        this.experienceValue = 8;

    }

    /**
     * Entity becomes invisible (5 sec) after being damaged.
     */
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2) {
        if (!super.attackEntityFrom(damageSource, par2)) {
            return false;
        } else {
            if (damageSource.getTrueSource() != null && !this.equals(damageSource.getTrueSource())) {
                addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20 * 5, 1));
            }
        }
        return true;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.1F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.GHOST_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(Balance.mobProps.GHOST_FOLLOW_RANGE);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.GHOST_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.GHOST_HEALTH);
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.GHOST;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(7, new EntityAIWander(this, 0.9F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 16));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true, false, true, false, null)));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
    }

    /**
     * Ghost do not make any step sounds
     */
    @Override
    protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_) {
        return;
    }

	@Override
	public boolean doesResistGarlic(EnumStrength strength) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EnumStrength isGettingGarlicDamage(boolean forceRefresh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGettingSundamage(boolean forceRefresh) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIgnoringSundamage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useBlood(int amt, boolean allowPartial) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean wantsBlood() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EntityLivingBase getRepresentingEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLevel(int level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int suggestLevel(Difficulty d) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void attackVillage(AxisAlignedBB area) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defendVillage(AxisAlignedBB area) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AxisAlignedBB getTargetVillageArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttackingVillage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopVillageAttackDefense() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IVampirismVillage getCurrentFriendlyVillage() {
		// TODO Auto-generated method stub
		return null;
	}

}
