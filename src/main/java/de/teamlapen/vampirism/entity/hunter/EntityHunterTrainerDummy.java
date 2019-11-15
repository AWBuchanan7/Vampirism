package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.entity.ai.HunterAILookAtTrainee;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class EntityHunterTrainerDummy extends EntityVampirism {
    private final int MOVE_TO_RESTRICT_PRIO = 3;

    public EntityHunterTrainerDummy(World world) {
        super(world);
        saveHome = true;
        hasArms = true;
        ((PathNavigateGround) this.getNavigator()).setEnterDoors(true);

        this.setSize(0.6F, 1.95F);
        this.setDontDropEquipment();
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public void setHome(AxisAlignedBB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(19);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(5);
    }

    @Override
    protected boolean canDespawn() {
        return !hasHome() && super.canDespawn();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0, false));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 13F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityVampirism.class, 17F));
        this.tasks.addTask(10, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }


    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() == Items.SPAWN_EGG;

        if (!flag && this.isEntityAlive() && !player.isSneaking()) {
            if (!this.world.isRemote) {
            	if(Helper.isHunter(player)) {
            		player.sendMessage(new TextComponentTranslation("text.vampirism.trainer_disabled_hunter"));
            	}else {
            		player.sendMessage(new TextComponentTranslation("text.vampirism.trainer_disabled"));
            	}
            }
            return true;
        }


        return super.processInteract(player, hand);
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
