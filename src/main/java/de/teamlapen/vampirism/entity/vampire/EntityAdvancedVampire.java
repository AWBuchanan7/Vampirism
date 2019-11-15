package de.teamlapen.vampirism.entity.vampire;

import com.mojang.authlib.GameProfile;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.vampire.IAdvancedVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.entity.action.EntityActionHandler;
import de.teamlapen.vampirism.entity.ai.*;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.util.IPlayerFace;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import de.teamlapen.vampirism.util.SupporterManager;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * Advanced vampire. Is strong. Represents supporters
 */
public class EntityAdvancedVampire extends EntityVampirism implements IAdvancedVampire, IPlayerFace, IEntityActionUser {
    private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.VARINT);
    private static final DataParameter<String> NAME = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.STRING);
    private static final DataParameter<String> TEXTURE = EntityDataManager.createKey(EntityAdvancedVampire.class, DataSerializers.STRING);

    private final int MAX_LEVEL = 1;
    /**
     * Store the approximate count of entities that are following this advanced vampire.
     * Not guaranteed to be exact and not saved to nbt
     */
    private int followingEntities = 0;

    @SideOnly(Side.CLIENT)
    @Nullable
    private GameProfile facePlayerProfile;

    public EntityAdvancedVampire(World world) {
        super(world);
        super.setProfession(ModVillages.profession_vampire_expert);
        this.setSize(0.6F, 1.95F);
        this.canSuckBloodFromPlayer = true;
        this.setSpawnRestriction(SpawnRestriction.SPECIAL);
        this.setDontDropEquipment();
        this.entitytier = EntityActionTier.High;
        this.entityActionHandler = new EntityActionHandler<>(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        boolean flag = super.attackEntityFrom(damageSource, amount);
        if (flag && damageSource.getTrueSource() instanceof EntityPlayer && this.rand.nextInt(4) == 0) {
            this.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 150, 2));
        }
        return flag;
    }

    @Override
    public void decreaseFollowerCount() {
        followingEntities = Math.max(0, followingEntities - 1);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }


    @Override
    public int getEyeType() {
        return getDataManager().get(TYPE);
    }

    @Override
    public int getFollowingCount() {
        return followingEntities;
    }

    @Override
    public int getLevel() {
        return getDataManager().get(LEVEL);
    }

    @Override
    public void setLevel(int level) {
        if (level >= 0) {
            getDataManager().set(LEVEL, level);
            this.updateEntityAttributes();
            if (level == 1) {
                this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000000, 0));
            }
        }
    }

    @Override
    public int getMaxFollowerCount() {
        return Balance.mobProps.ADVANCED_VAMPIRE_MAX_FOLLOWER;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public String getName() {
        String senderName = this.getDataManager().get(NAME);
        return "none".equals(senderName) ? super.getName() : senderName;
    }


    @SideOnly(Side.CLIENT)
    @Nullable
    @Override
    public GameProfile getPlayerFaceProfile() {
        if (this.facePlayerProfile == null) {
            String name = getTextureName();
            if (name == null) return null;
            facePlayerProfile = new GameProfile(null, name);
            PlayerSkinHelper.updateGameProfileAsync(facePlayerProfile, (profile) -> Minecraft.getMinecraft().addScheduledTask(() -> EntityAdvancedVampire.this.facePlayerProfile = profile));
        }
        return facePlayerProfile;
    }


    @Nullable
    public String getTextureName() {
        String texture = this.getDataManager().get(TEXTURE);
        return "none".equals(texture) ? null : texture;
    }

    @Override
    public boolean increaseFollowerCount() {
        if (followingEntities < getMaxFollowerCount()) {
            followingEntities++;
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        if (tagCompund.hasKey("level")) {
            setLevel(tagCompund.getInteger("level"));
        }
        if (tagCompund.hasKey("type")) {
            getDataManager().set(TYPE, tagCompund.getInteger("type"));
            getDataManager().set(NAME, tagCompund.getString("name"));
            getDataManager().set(TEXTURE, tagCompund.getString("texture"));
        }

    }

    @Override
    public int suggestLevel(Difficulty d) {
        if (rand.nextBoolean()) {
            return (int) (d.avgPercLevel * MAX_LEVEL / 100F);
        }
        return rand.nextInt(MAX_LEVEL + 1);

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("level", getLevel());
        nbt.setInteger("type", getEyeType());
        nbt.setString("texture", getDataManager().get(TEXTURE));
        nbt.setString("name", getDataManager().get(NAME));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.updateEntityAttributes();

    }

    @Override
    protected float calculateFireDamage(float amount) {
        return (float) (amount * Balance.mobProps.ADVANCED_VAMPIRE_FIRE_VULNERABILITY);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        SupporterManager.Supporter supporter = SupporterManager.getInstance().getRandomVampire(rand);
        this.getDataManager().register(LEVEL, -1);
        this.getDataManager().register(TYPE, supporter.typeId);
        this.getDataManager().register(NAME, supporter.senderName == null ? "none" : supporter.senderName);
        this.getDataManager().register(TEXTURE, supporter.textureName == null ? "none" : supporter.textureName);

    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 10 * (1 + getLevel());
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootHandler.ADVANCED_VAMPIRE;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        removeCertainTasks(EntityAIAvoidEntity.class);
        if (world.getDifficulty() == EnumDifficulty.HARD) {
            //Only break doors on hard difficulty
            this.tasks.addTask(1, new EntityAIBreakDoor(this));
            ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
        }
        this.tasks.addTask(2, new VampireAIRestrictSun(this));
        this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
        this.tasks.addTask(3, new VampireAIFleeGarlic(this, 0.9, false));
        this.tasks.addTask(4, new EntityAIAttackMeleeNoSun(this, 1.0, false));
        this.tasks.addTask(8, new EntityAIWander(this, 0.9, 25));
        this.tasks.addTask(9, new EntityAIWatchClosestVisible(this, EntityPlayer.class, 10F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, true, true, false, null)));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));
    }
    
    private void removeCertainTasks(Class typ) {
        Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.tasks.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

            if (entityaibase.getClass().equals(typ)) {
                iterator.remove();
            }
        }
    }

    protected void updateEntityAttributes() {
        int l = Math.max(getLevel(), 0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH + Balance.mobProps.ADVANCED_VAMPIRE_MAX_HEALTH_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE + Balance.mobProps.ADVANCED_VAMPIRE_ATTACK_DAMAGE_PL * l);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Balance.mobProps.ADVANCED_VAMPIRE_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13);
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
