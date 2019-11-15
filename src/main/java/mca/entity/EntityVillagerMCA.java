package mca.entity;

import static net.minecraft.block.BlockBed.OCCUPIED;
import static net.minecraft.block.BlockBed.PART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.api.items.IItemWithTier.TIER;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntitySoulOrb;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.entity.ai.EntityAIAttackMeleeNoSun;
import de.teamlapen.vampirism.entity.ai.EntityAIAttackVillage;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendVillage;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveThroughVillageCustom;
import de.teamlapen.vampirism.entity.ai.EntityAIWatchClosestVisible;
import de.teamlapen.vampirism.entity.ai.VampireAIBiteNearbyEntity;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeGarlic;
import de.teamlapen.vampirism.entity.ai.VampireAIFleeSun;
import de.teamlapen.vampirism.entity.ai.VampireAIFollowAdvanced;
import de.teamlapen.vampirism.entity.ai.VampireAIMoveToBiteable;
import de.teamlapen.vampirism.entity.ai.VampireAIRestrictSun;
import de.teamlapen.vampirism.entity.hunter.EntityHunterBase;
import de.teamlapen.vampirism.items.ItemHunterCoat;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import mca.api.API;
import mca.api.types.APIButton;
import mca.core.Constants;
import mca.core.forge.NetMCA;
import mca.core.minecraft.ItemsMCA;
import mca.core.minecraft.ProfessionsMCA;
import mca.entity.ai.EntityAIAgeBaby;
import mca.entity.ai.EntityAIChopping;
import mca.entity.ai.EntityAIDefendFromTarget;
import mca.entity.ai.EntityAIFishing;
import mca.entity.ai.EntityAIGoHangout;
import mca.entity.ai.EntityAIGoWorkplace;
import mca.entity.ai.EntityAIHarvesting;
import mca.entity.ai.EntityAIHunting;
import mca.entity.ai.EntityAIMoveState;
import mca.entity.ai.EntityAIProcreate;
import mca.entity.ai.EntityAIProspecting;
import mca.entity.ai.EntityAISleeping;
import mca.entity.data.ParentData;
import mca.entity.data.PlayerHistory;
import mca.entity.data.PlayerSaveData;
import mca.entity.data.SavedVillagers;
import mca.entity.inventory.InventoryMCA;
import mca.enums.EnumAgeState;
import mca.enums.EnumChore;
import mca.enums.EnumConstraint;
import mca.enums.EnumDialogueType;
import mca.enums.EnumGender;
import mca.enums.EnumMarriageState;
import mca.enums.EnumMoveState;
import mca.items.ItemSpecialCaseGift;
import mca.util.ItemStackCache;
import mca.util.ResourceLocationCache;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityVillagerMCA extends EntityVillager implements IFactionEntity {
    public static final int VANILLA_CAREER_ID_FIELD_INDEX = 13;
    public static final int VANILLA_CAREER_LEVEL_FIELD_INDEX = 14;

    public static final DataParameter<String> VILLAGER_NAME = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.STRING);
    public static final DataParameter<String> TEXTURE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.STRING);
    public static final DataParameter<Integer> GENDER = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<Float> GIRTH = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> TALLNESS = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.FLOAT);
    public static final DataParameter<NBTTagCompound> PLAYER_HISTORY_MAP = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.COMPOUND_TAG);
    public static final DataParameter<Integer> MOVE_STATE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<String> SPOUSE_NAME = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.STRING);
    public static final DataParameter<Optional<UUID>> SPOUSE_UUID = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public static final DataParameter<Integer> MARRIAGE_STATE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<Boolean> IS_PROCREATING = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
    public static final DataParameter<NBTTagCompound> PARENTS = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.COMPOUND_TAG);
    public static final DataParameter<Boolean> IS_INFECTED = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> AGE_STATE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> ACTIVE_CHORE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<Boolean> IS_SWINGING = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> HAS_BABY = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> BABY_IS_MALE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> BABY_AGE = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<UUID>> CHORE_ASSIGNING_PLAYER = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public static final DataParameter<BlockPos> BED_POS = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<BlockPos> WORKPLACE_POS = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<BlockPos> HANGOUT_POS = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);

    private static final Predicate<EntityVillagerMCA> BANDIT_TARGET_SELECTOR = (v) -> v.getProfessionForge() != ProfessionsMCA.bandit && v.getProfessionForge() != ProfessionsMCA.child;
    private static final Predicate<EntityVillagerMCA> GUARD_TARGET_SELECTOR = (v) -> v.getProfessionForge() == ProfessionsMCA.bandit;

    public final InventoryMCA inventory;
    public int babyAge = 0;
    public UUID playerToFollowUUID = Constants.ZERO_UUID;
    private EntityAIBase tasks_avoidHunter;
    private BlockPos home = BlockPos.ORIGIN;
    private int startingAge = 0;
    private float swingProgressTicks;

    public float renderOffsetX;
    public float renderOffsetY;
    public float renderOffsetZ;

    public EntityVillagerMCA() {
    	super(null);
        this.countAsMonsterForSpawn = false;
        inventory = null;
    }

    public EntityVillagerMCA(World worldIn) {
		super(worldIn);
        this.countAsMonsterForSpawn = false;
        inventory = new InventoryMCA(this);
    }

    public EntityVillagerMCA(World worldIn, Optional<VillagerRegistry.VillagerProfession> profession, Optional<EnumGender> gender) {
		this(worldIn);
		this.countAsMonsterForSpawn = false;
        if (!worldIn.isRemote) {
            EnumGender eGender = gender.isPresent() ? gender.get() : EnumGender.getRandom();
            set(GENDER, eGender.getId());
            set(VILLAGER_NAME, API.getRandomName(eGender));
            setProfession(ProfessionsMCA.randomProfession());
            setVanillaCareer(getProfessionForge().getRandomCareer(worldIn.rand));
            set(TEXTURE, API.getRandomSkin(this));

            applySpecialAI();
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(VILLAGER_NAME, "");
        this.dataManager.register(TEXTURE, "");
        this.dataManager.register(GENDER, EnumGender.MALE.getId());
        this.dataManager.register(GIRTH, 0.0F);
        this.dataManager.register(TALLNESS, 0.0F);
        this.dataManager.register(PLAYER_HISTORY_MAP, new NBTTagCompound());
        this.dataManager.register(MOVE_STATE, EnumMoveState.MOVE.getId());
        this.dataManager.register(SPOUSE_NAME, "");
        this.dataManager.register(SPOUSE_UUID, Optional.of(Constants.ZERO_UUID));
        this.dataManager.register(MARRIAGE_STATE, EnumMarriageState.NOT_MARRIED.getId());
        this.dataManager.register(IS_PROCREATING, false);
        this.dataManager.register(PARENTS, new NBTTagCompound());
        this.dataManager.register(IS_INFECTED, false);
        this.dataManager.register(AGE_STATE, EnumAgeState.ADULT.getId());
        this.dataManager.register(ACTIVE_CHORE, EnumChore.NONE.getId());
        this.dataManager.register(IS_SWINGING, false);
        this.dataManager.register(HAS_BABY, false);
        this.dataManager.register(BABY_IS_MALE, false);
        this.dataManager.register(BABY_AGE, 0);
        this.dataManager.register(CHORE_ASSIGNING_PLAYER, Optional.of(Constants.ZERO_UUID));
        this.dataManager.register(BED_POS, BlockPos.ORIGIN);
        this.dataManager.register(WORKPLACE_POS, BlockPos.ORIGIN);
        this.dataManager.register(HANGOUT_POS, BlockPos.ORIGIN);
        this.dataManager.register(SLEEPING, false);
        this.setSilent(false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(VampirismMod.getConfig().villagerMaxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);

        if (this.getHealth() <= VampirismMod.getConfig().villagerMaxHealth) {
            this.setHealth(VampirismMod.getConfig().villagerMaxHealth);
        }
        getAttributeMap().registerAttribute(VReference.sunDamage).setBaseValue(Balance.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    public <T> T get(DataParameter<T> key) {
        return this.dataManager.get(key);
    }

    public <T> void set(DataParameter<T> key, T value) {
        this.dataManager.set(key, value);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        set(VILLAGER_NAME, nbt.getString("name"));
        set(GENDER, nbt.getInteger("gender"));
        set(TEXTURE, nbt.getString("texture"));
        set(GIRTH, nbt.getFloat("girth"));
        set(TALLNESS, nbt.getFloat("tallness"));
        set(PLAYER_HISTORY_MAP, nbt.getCompoundTag("playerHistoryMap"));
        set(MOVE_STATE, nbt.getInteger("moveState"));
        set(MARRIAGE_STATE, nbt.getInteger("marriageState"));
        set(SPOUSE_UUID, Optional.of(nbt.getUniqueId("spouseUUID")));
        set(SPOUSE_NAME, nbt.getString("spouseName"));
        set(IS_PROCREATING, nbt.getBoolean("isProcreating"));
        set(IS_INFECTED, nbt.getBoolean("infected"));
        set(AGE_STATE, nbt.getInteger("ageState"));
        set(ACTIVE_CHORE, nbt.getInteger("activeChore"));
        set(CHORE_ASSIGNING_PLAYER, Optional.of(nbt.getUniqueId("choreAssigningPlayer")));
        set(HAS_BABY, nbt.getBoolean("hasBaby"));
        set(BABY_IS_MALE, nbt.getBoolean("babyIsMale"));
        set(PARENTS, nbt.getCompoundTag("parents"));
        set(BED_POS, new BlockPos(nbt.getInteger("bedX"), nbt.getInteger("bedY"), nbt.getInteger("bedZ")));
        set(HANGOUT_POS, new BlockPos(nbt.getInteger("hangoutX"), nbt.getInteger("hangoutY"), nbt.getInteger("hangoutZ")));
        set(WORKPLACE_POS, new BlockPos(nbt.getInteger("workplaceX"), nbt.getInteger("workplaceY"), nbt.getInteger("workplaceZ")));
        set(SLEEPING, nbt.getBoolean("sleeping"));
        inventory.readInventoryFromNBT(nbt.getTagList("inventory", 10));

        // Vanilla Age doesn't apply from the superclass call. Causes children to revert to the starting age on world reload.
        this.startingAge = nbt.getInteger("startingAge");
        setGrowingAge(nbt.getInteger("Age"));

        this.home = new BlockPos(nbt.getDouble("homePositionX"), nbt.getDouble("homePositionY"), nbt.getDouble("homePositionZ"));
        this.playerToFollowUUID = nbt.getUniqueId("playerToFollowUUID");
        this.babyAge = nbt.getInteger("babyAge");

        applySpecialAI();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setUniqueId("uuid", this.getUniqueID()); // for SavedVillagers
        nbt.setString("name", get(VILLAGER_NAME));
        nbt.setString("texture", get(TEXTURE));
        nbt.setInteger("gender", get(GENDER));
        nbt.setFloat("girth", get(GIRTH));
        nbt.setFloat("tallness", get(TALLNESS));
        nbt.setTag("playerHistoryMap", get(PLAYER_HISTORY_MAP));
        nbt.setInteger("moveState", get(MOVE_STATE));
        nbt.setInteger("marriageState", get(MARRIAGE_STATE));
        nbt.setDouble("homePositionX", home.getX());
        nbt.setDouble("homePositionY", home.getY());
        nbt.setDouble("homePositionZ", home.getZ());
        nbt.setUniqueId("playerToFollowUUID", playerToFollowUUID);
        nbt.setUniqueId("spouseUUID", get(SPOUSE_UUID).or(Constants.ZERO_UUID));
        nbt.setString("spouseName", get(SPOUSE_NAME));
        nbt.setBoolean("isProcreating", get(IS_PROCREATING));
        nbt.setBoolean("infected", get(IS_INFECTED));
        nbt.setInteger("ageState", get(AGE_STATE));
        nbt.setInteger("startingAge", startingAge);
        nbt.setInteger("activeChore", get(ACTIVE_CHORE));
        nbt.setUniqueId("choreAssigningPlayer", get(CHORE_ASSIGNING_PLAYER).or(Constants.ZERO_UUID));
        nbt.setTag("inventory", inventory.writeInventoryToNBT());
        nbt.setInteger("babyAge", babyAge);
        nbt.setTag("parents", get(PARENTS));
        nbt.setInteger("bedX", get(BED_POS).getX());
        nbt.setInteger("bedY", get(BED_POS).getY());
        nbt.setInteger("bedZ", get(BED_POS).getZ());
        nbt.setInteger("workplaceX", get(WORKPLACE_POS).getX());
        nbt.setInteger("workplaceY", get(WORKPLACE_POS).getY());
        nbt.setInteger("workplaceZ", get(WORKPLACE_POS).getZ());
        nbt.setInteger("hangoutX", get(HANGOUT_POS).getX());
        nbt.setInteger("hangoutY", get(HANGOUT_POS).getY());
        nbt.setInteger("hangoutZ", get(HANGOUT_POS).getZ());
        nbt.setBoolean("sleeping", get(SLEEPING));
    }

    @Override
    protected void damageEntity(@Nonnull DamageSource damageSource, float damageAmount) {
        // Guards take 50% less damage
        if (getProfessionForge() == ProfessionsMCA.guard) {
            damageAmount *= 0.50;
        }
        super.damageEntity(damageSource, damageAmount);

        // Check for infection to apply. Does not affect guards.
        if (VampirismMod.getConfig().enableInfection && getProfessionForge() != ProfessionsMCA.guard && damageSource.getImmediateSource() instanceof EntityZombie && getRNG().nextFloat() < VampirismMod.getConfig().infectionChance / 100) {
            set(IS_INFECTED, true);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateSwinging();
        updateSleeping();

        if (this.isServerWorld()) {
            onEachServerUpdate();
        } else {
            onEachClientUpdate();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return get(IS_INFECTED) ? SoundEvents.ENTITY_ZOMBIE_DEATH : null;
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
        // No-op, handled by EventHooks
        return true;
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        if (cause.getImmediateSource() instanceof EntityPlayer && Helper.isHunter(cause.getImmediateSource())) {
            ItemStack weapon = ((EntityPlayer) cause.getImmediateSource()).getHeldItemMainhand();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
        if (!world.isRemote) {
            if (VampirismMod.getConfig().logVillagerDeaths) {
                String causeName = cause.getImmediateSource() == null ? "Unknown" : cause.getImmediateSource().getName();
                VampirismMod.getLog().info("Villager death: " + get(VILLAGER_NAME) + ". Caused by: " + causeName + ". UUID: " + this.getUniqueID().toString());
            }

            //TODO: player history gets lsot on revive
            //TODO: childp becomes to child on revive (needs verification)

            inventory.dropAllItems();
            inventory.clear(); //fixes issue #1227, dropAllItems() should clear, but it does not work

            if (isMarried()) {
                UUID spouseUUID = get(SPOUSE_UUID).or(Constants.ZERO_UUID);
                Optional<EntityVillagerMCA> spouse = mca.util.Util.getEntityByUUID(world, spouseUUID, EntityVillagerMCA.class);
                PlayerSaveData playerSaveData = PlayerSaveData.getExisting(world, spouseUUID);

                // Notify spouse of the death
                if (spouse.isPresent()) {
                    spouse.get().endMarriage();
                } else if (playerSaveData != null) {
                    playerSaveData.endMarriage();
                    EntityPlayer player = world.getPlayerEntityByUUID(spouseUUID);
                    if (player != null) {
                        player.sendMessage(new TextComponentString(Constants.Color.RED + VampirismMod.getLocalizer().localize("notify.spousedied", get(VILLAGER_NAME), cause.getImmediateSource().getName())));
                    }
                }
            }

            // Notify all parents of the death
            ParentData parents = ParentData.fromNBT(get(PARENTS));
            Arrays.stream(parents.getParentEntities(world))
                    .filter(e -> e instanceof EntityPlayer)
                    .forEach(e -> {
                        EntityPlayer player = (EntityPlayer) e;
                        player.sendMessage(new TextComponentString(Constants.Color.RED + VampirismMod.getLocalizer().localize("notify.childdied", get(VILLAGER_NAME), cause.getImmediateSource().getName())));
                    });

            SavedVillagers.get(world).save(this);
        }
    }

    @Override
    protected void onGrowingAdult() {
        Entity[] parents = ParentData.fromNBT(get(PARENTS)).getParentEntities(world);
        set(AGE_STATE, EnumAgeState.ADULT.getId());
        Arrays.stream(parents).filter((e) -> e instanceof EntityPlayer).forEach((e) -> {
            PlayerHistory history = getPlayerHistoryFor(e.getUniqueID());
            history.setDialogueType(EnumDialogueType.ADULT);
            e.sendMessage(new TextComponentString(VampirismMod.getLocalizer().localize("notify.child.grownup", this.get(VILLAGER_NAME))));
        });

        // set profession away from child for villager children
        if (getProfessionForge() == ProfessionsMCA.child) {
            setProfession(ProfessionsMCA.randomProfession());
            setVanillaCareer(getProfessionForge().getRandomCareer(world.rand));
        }
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        // translate profession name
        ITextComponent careerName = new TextComponentTranslation("entity.Villager." + getVanillaCareer().getName());
        EnumAgeState age = EnumAgeState.byId(get(AGE_STATE));
        String professionName = age != EnumAgeState.ADULT ? age.localizedName() : careerName.getUnformattedText();
        String color = this.getProfessionForge() == ProfessionsMCA.bandit ? Constants.Color.RED : this.getProfessionForge() == ProfessionsMCA.guard ? Constants.Color.GREEN : "";

        return new TextComponentString(String.format("%1$s%2$s%3$s (%4$s)", color, VampirismMod.getConfig().villagerChatPrefix, get(VILLAGER_NAME), professionName));
    }

    @Override
    @Nonnull
    public String getCustomNameTag() {
        return get(VILLAGER_NAME);
    }

    @Override
    @Nonnull
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public void swingArm(EnumHand hand) {
        this.setActiveHand(EnumHand.MAIN_HAND);
        super.swingArm(EnumHand.MAIN_HAND);

        if (!get(IS_SWINGING) || swingProgressTicks >= 8 / 2 || swingProgressTicks < 0) {
            swingProgressTicks = -1;
            set(IS_SWINGING, true);
        }
    }

    private void updateSwinging() {
        if (get(IS_SWINGING)) {
            swingProgressTicks++;

            if (swingProgressTicks >= 8) {
                swingProgressTicks = 0;
                set(IS_SWINGING, false);
            }
        } else {
            swingProgressTicks = 0;
        }
        swingProgress = swingProgressTicks / (float) 8;
    }

    @Override
    @Nonnull
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        if (slotIn == EntityEquipmentSlot.MAINHAND) {
            VillagerRegistry.VillagerProfession profession = getProfessionForge();
            EnumChore chore = EnumChore.byId(get(ACTIVE_CHORE));
            if (get(HAS_BABY)) {
                return ItemStackCache.get(get(BABY_IS_MALE) ? ItemsMCA.BABY_BOY : ItemsMCA.BABY_GIRL);
            } else if (chore != EnumChore.NONE) {
                return inventory.getBestItemOfType(chore.getToolType());
            } else {
                return ProfessionsMCA.getDefaultHeldItem(profession, getVanillaCareer());
            }
        } else {
            return inventory.getBestArmorOfType(slotIn);
        }
    }

    public void setStartingAge(int value) {
        this.startingAge = value;
        setGrowingAge(value);
    }

    public PlayerHistory getPlayerHistoryFor(UUID uuid) {
        if (!get(PLAYER_HISTORY_MAP).hasKey(uuid.toString())) {
            updatePlayerHistoryMap(PlayerHistory.getNew(this, uuid));
        }
        return PlayerHistory.fromNBT(this, uuid, get(PLAYER_HISTORY_MAP).getCompoundTag(uuid.toString()));
    }

    public void updatePlayerHistoryMap(PlayerHistory history) {
        NBTTagCompound nbt = get(PLAYER_HISTORY_MAP);
        nbt.setTag(history.getPlayerUUID().toString(), history.toNBT());
        set(PLAYER_HISTORY_MAP, nbt);
        this.dataManager.setDirty(PLAYER_HISTORY_MAP);
    }

    public void reset() {
        set(PLAYER_HISTORY_MAP, new NBTTagCompound());
        dataManager.setDirty(PLAYER_HISTORY_MAP);

        setHealth(20.0F);

        set(SPOUSE_NAME, "");
        set(SPOUSE_UUID, Optional.of(Constants.ZERO_UUID));
        set(MARRIAGE_STATE, EnumMarriageState.NOT_MARRIED.getId());
        set(HAS_BABY, false);
    }

    public VillagerRegistry.VillagerCareer getVanillaCareer() {
        return this.getProfessionForge().getCareer(ObfuscationReflectionHelper.getPrivateValue(EntityVillager.class, this, VANILLA_CAREER_ID_FIELD_INDEX));
    }

    public void setVanillaCareer(int careerId) {
        ObfuscationReflectionHelper.setPrivateValue(EntityVillager.class, this, careerId, VANILLA_CAREER_ID_FIELD_INDEX);
    }

    private void setSizeForAge() {
        EnumAgeState age = EnumAgeState.byId(get(AGE_STATE));
        this.setSize(age.getWidth(), age.getHeight());
        this.setScale(1.0F); // trigger rebuild of the bounding box
    }

    private void toggleMount(EntityPlayerMP player) {
        if (getRidingEntity() != null) {
            dismountRidingEntity();
        } else {
            try {
                List<EntityHorse> horses = world.getEntities(EntityHorse.class, h -> (h.isHorseSaddled() && !h.isBeingRidden() && h.getDistance(this) < 3.0D));
                startRiding(horses.stream().min(Comparator.comparingDouble(this::getDistance)).get(), true);
                getNavigator().clearPath();
            } catch (NoSuchElementException e) {
                say(Optional.of(player), "interaction.ridehorse.fail.notnearby");
            }
        }
    }

    private void goHome(EntityPlayerMP player) {
        if (home.equals(Vec3d.ZERO)) {
            say(Optional.of(player), "interaction.gohome.fail");
        } else {
            say(Optional.of(player), "interaction.gohome.success");
            if (!getNavigator().setPath(getNavigator().getPathToXYZ(home.getX(), home.getY(), home.getZ()), 1.0D)) {
                attemptTeleport(home.getX(), home.getY(), home.getZ());
            }
        }
    }

    public BlockPos getWorkplace() {
        return get(WORKPLACE_POS);
    }

    public BlockPos getHangout() {
        return get(HANGOUT_POS);
    }

    /**
     * Forces the villager's home to be set to their position. No checks for safety are made.
     * This is used on overwriting the original villager.
     */
    public void forcePositionAsHome() {
        this.home = this.getPosition();
    }

    private void setHome(EntityPlayerMP player) {
        if (attemptTeleport(player.posX, player.posY, player.posZ)) {
            say(Optional.of(player), "interaction.sethome.success");
            this.home = player.getPosition();
            this.setHomePosAndDistance(this.home, 32);
            BlockPos bed = searchBed();
            if (bed != null) {
                set(BED_POS, bed);
            }
        } else {
            say(Optional.of(player), "interaction.sethome.fail");
        }
    }

    public void setWorkplace(EntityPlayerMP player) {
        say(Optional.of(player), "interaction.setworkplace.success");
        set(WORKPLACE_POS, player.getPosition());
    }

    public void setHangout(EntityPlayerMP player) {
        say(Optional.of(player), "interaction.sethangout.success");
        set(HANGOUT_POS, player.getPosition());
    }

	public void setBabyAge(int babyAge) {
		this.babyAge = babyAge;
	}

	public UUID getPlayerToFollowUUID() {
		return playerToFollowUUID;
	}

	public void setPlayerToFollowUUID(UUID playerToFollowUUID) {
		this.playerToFollowUUID = playerToFollowUUID;
	}

	public AxisAlignedBB getHome() {
		return new AxisAlignedBB(home.ORIGIN);
	}

	public void setHome(BlockPos home) {
		this.home = home;
	}

	public float getSwingProgressTicks() {
		return swingProgressTicks;
	}

	public void setSwingProgressTicks(float swingProgressTicks) {
		this.swingProgressTicks = swingProgressTicks;
	}

	public float getRenderOffsetX() {
		return renderOffsetX;
	}

	public void setRenderOffsetX(float renderOffsetX) {
		this.renderOffsetX = renderOffsetX;
	}

	public float getRenderOffsetY() {
		return renderOffsetY;
	}

	public void setRenderOffsetY(float renderOffsetY) {
		this.renderOffsetY = renderOffsetY;
	}

	public float getRenderOffsetZ() {
		return renderOffsetZ;
	}

	public void setRenderOffsetZ(float renderOffsetZ) {
		this.renderOffsetZ = renderOffsetZ;
	}

	public static int getVanillaCareerIdFieldIndex() {
		return VANILLA_CAREER_ID_FIELD_INDEX;
	}

	public static int getVanillaCareerLevelFieldIndex() {
		return VANILLA_CAREER_LEVEL_FIELD_INDEX;
	}

	public static DataParameter<String> getVillagerName() {
		return VILLAGER_NAME;
	}

	public static DataParameter<String> getTexture() {
		return TEXTURE;
	}

	public static DataParameter<Integer> getGender() {
		return GENDER;
	}

	public static DataParameter<Float> getGirth() {
		return GIRTH;
	}

	public static DataParameter<Float> getTallness() {
		return TALLNESS;
	}

	public static DataParameter<NBTTagCompound> getPlayerHistoryMap() {
		return PLAYER_HISTORY_MAP;
	}

	public static DataParameter<Integer> getMoveState() {
		return MOVE_STATE;
	}

	public static DataParameter<String> getSpouseName() {
		return SPOUSE_NAME;
	}

	public static DataParameter<Optional<UUID>> getSpouseUuid() {
		return SPOUSE_UUID;
	}

	public static DataParameter<Integer> getMarriageState() {
		return MARRIAGE_STATE;
	}

	public static DataParameter<Boolean> getIsProcreating() {
		return IS_PROCREATING;
	}

	public static DataParameter<NBTTagCompound> getParents() {
		return PARENTS;
	}

	public static DataParameter<Boolean> getIsInfected() {
		return IS_INFECTED;
	}

	public static DataParameter<Integer> getAgeState() {
		return AGE_STATE;
	}

	public static DataParameter<Integer> getActiveChore() {
		return ACTIVE_CHORE;
	}

	public static DataParameter<Boolean> getIsSwinging() {
		return IS_SWINGING;
	}

	public static DataParameter<Boolean> getHasBaby() {
		return HAS_BABY;
	}

	public static DataParameter<Boolean> getBabyIsMale() {
		return BABY_IS_MALE;
	}

	public static DataParameter<Integer> getBabyAge() {
		return BABY_AGE;
	}

	public static DataParameter<Optional<UUID>> getChoreAssigningPlayer() {
		return CHORE_ASSIGNING_PLAYER;
	}

	public static DataParameter<BlockPos> getBedPos() {
		return BED_POS;
	}

	public static DataParameter<BlockPos> getWorkplacePos() {
		return WORKPLACE_POS;
	}

	public static DataParameter<BlockPos> getHangoutPos() {
		return HANGOUT_POS;
	}

	public static DataParameter<Boolean> getSleeping() {
		return SLEEPING;
	}

	public static Predicate<EntityVillagerMCA> getBanditTargetSelector() {
		return BANDIT_TARGET_SELECTOR;
	}

	public static Predicate<EntityVillagerMCA> getGuardTargetSelector() {
		return GUARD_TARGET_SELECTOR;
	}

	public InventoryMCA getInventory() {
		return inventory;
	}

	public int getStartingAge() {
		return startingAge;
	}

	public void say(Optional<EntityPlayer> player, String phraseId, @Nullable String... params) {
        ArrayList<String> paramsList = new ArrayList<>();
        if (params != null) Collections.addAll(paramsList, params);

        if (player.isPresent()) {
            EntityPlayer thePlayer = player.get();

            // Provide player as the first param, always
            paramsList.add(0, thePlayer.getName());

            // Infected villagers do not speak.
            if (get(IS_INFECTED)) {
                thePlayer.sendMessage(new TextComponentString(getDisplayName().getFormattedText() + ": " + "???"));
                this.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, 0.5F, rand.nextFloat() + 0.5F);
            } else {
                String dialogueType = getPlayerHistoryFor(player.get().getUniqueID()).getDialogueType().getId();
                String phrase = VampirismMod.getLocalizer().localize(dialogueType + "." + phraseId, paramsList);
                thePlayer.sendMessage(new TextComponentString(String.format("%1$s: %2$s", getDisplayName().getFormattedText(), phrase)));
            }
        } else {
            VampirismMod.getLog().warn(new Throwable("Say called on player that is not present!"));
        }
    }

    public boolean isMarried() {
        return !get(SPOUSE_UUID).or(Constants.ZERO_UUID).equals(Constants.ZERO_UUID);
    }

    public boolean isMarriedTo(UUID uuid) {
        return get(SPOUSE_UUID).or(Constants.ZERO_UUID).equals(uuid);
    }

    public void marry(EntityPlayer player) {
        set(SPOUSE_UUID, Optional.of(player.getUniqueID()));
        set(SPOUSE_NAME, player.getName());
        set(MARRIAGE_STATE, EnumMarriageState.MARRIED.getId());
    }

    private void endMarriage() {
        set(SPOUSE_UUID, Optional.of(Constants.ZERO_UUID));
        set(SPOUSE_NAME, "");
        set(MARRIAGE_STATE, EnumMarriageState.NOT_MARRIED.getId());
    }

    private void handleInteraction(EntityPlayerMP player, PlayerHistory history, APIButton button) {
        float successChance = 0.85F;
        int heartsBoost = button.getConstraints().contains(EnumConstraint.ADULTS) ? 15 : 5;

        String interactionName = button.getIdentifier().replace("gui.button.", "");

        successChance -= button.getConstraints().contains(EnumConstraint.ADULTS) ? 0.25F : 0.0F;
        successChance += (history.getHearts() / 10.0D) * 0.025F;

        if (VampirismMod.getConfig().enableDiminishingReturns) successChance -= history.getInteractionFatigue() * 0.05F;

        boolean succeeded = rand.nextFloat() < successChance;
        if (VampirismMod.getConfig().enableDiminishingReturns && succeeded)
            heartsBoost -= history.getInteractionFatigue() * 0.05F;

        history.changeInteractionFatigue(1);
        history.changeHearts(succeeded ? heartsBoost : (heartsBoost * -1));
        String responseId = String.format("%s.%s", interactionName, succeeded ? "success" : "fail");
        say(Optional.of(player), responseId);
    }

    public void handleButtonClick(EntityPlayerMP player, String guiKey, String buttonId) {
        PlayerHistory history = getPlayerHistoryFor(player.getUniqueID());
        java.util.Optional<APIButton> button = API.getButtonById(guiKey, buttonId);
        if (!button.isPresent()) {
            VampirismMod.getLog().warn("Button not found for key and ID: " + guiKey + ", " + buttonId);
        } else if (button.get().isInteraction()) handleInteraction(player, history, button.get());

        switch (buttonId) {
            case "gui.button.move":
                set(MOVE_STATE, EnumMoveState.MOVE.getId());
                this.playerToFollowUUID = Constants.ZERO_UUID;
                break;
            case "gui.button.stay":
                set(MOVE_STATE, EnumMoveState.STAY.getId());
                break;
            case "gui.button.follow":
                set(MOVE_STATE, EnumMoveState.FOLLOW.getId());
                this.playerToFollowUUID = player.getUniqueID();
                stopChore();
                break;
            case "gui.button.ridehorse":
                toggleMount(player);
                break;
            case "gui.button.sethome":
                setHome(player);
                break;
            case "gui.button.gohome":
                goHome(player);
                break;
            case "gui.button.setworkplace":
                setWorkplace(player);
                break;
            case "gui.button.sethangout":
                setHangout(player);
                break;
            case "gui.button.trade":
                if (VampirismMod.getConfig().allowTrading) {
                    setCustomer(player);
                    player.displayVillagerTradeGui(this);
                } else {
                    player.sendMessage(new TextComponentString(VampirismMod.getLocalizer().localize("info.trading.disabled")));
                }
                break;
            case "gui.button.inventory":
                player.openGui(VampirismMod.getInstance(), Constants.GUI_ID_INVENTORY, player.world, this.getEntityId(), 0, 0);
                break;
            case "gui.button.gift":
                ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
                int giftValue = API.getGiftValueFromStack(stack);
                if (!handleSpecialCaseGift(player, stack)) {
                    if (stack.getItem() == Items.GOLDEN_APPLE) set(IS_INFECTED, false);
                    else {
                        history.changeHearts(giftValue);
                        say(Optional.of(player), API.getResponseForGift(stack));
                    }
                }
                if (giftValue > 0) {
                    player.inventory.decrStackSize(player.inventory.currentItem, 1);
                }
                break;
            case "gui.button.procreate":
                if (PlayerSaveData.get(player).isBabyPresent())
                    say(Optional.of(player), "interaction.procreate.fail.hasbaby");
                else if (history.getHearts() < 100) say(Optional.of(player), "interaction.procreate.fail.lowhearts");
                else {
                    EntityAITasks.EntityAITaskEntry task = tasks.taskEntries.stream().filter((ai) -> ai.action instanceof EntityAIProcreate).findFirst().orElse(null);
                    if (task != null) {
                        ((EntityAIProcreate) task.action).procreateTimer = 20 * 3; // 3 seconds
                        set(IS_PROCREATING, true);
                    }
                }
                break;
            case "gui.button.infected":
                set(IS_INFECTED, !get(IS_INFECTED));
                break;
            case "gui.button.texture.randomize":
                set(TEXTURE, API.getRandomSkin(this));
                break;
            case "gui.button.profession.randomize":
                setProfession(ProfessionsMCA.randomProfession());
                setVanillaCareer(getProfessionForge().getRandomCareer(world.rand));
                break;
            case "gui.button.gender":
                EnumGender gender = EnumGender.byId(get(GENDER));
                if (gender == EnumGender.MALE) {
                    set(GENDER, EnumGender.FEMALE.getId());
                } else {
                    set(GENDER, EnumGender.MALE.getId());
                }
                // intentional fall-through here
            case "gui.button.texture":
                set(TEXTURE, API.getRandomSkin(this));
                break;
            case "gui.button.random":
                set(VILLAGER_NAME, API.getRandomName(EnumGender.byId(get(GENDER))));
                break;
            case "gui.button.profession":
                RegistryNamespaced<ResourceLocation, VillagerRegistry.VillagerProfession> registry = ObfuscationReflectionHelper.getPrivateValue(VillagerRegistry.class, VillagerRegistry.instance(), "REGISTRY");
                setProfession(ProfessionsMCA.randomProfession());
                setVanillaCareer(getProfessionForge().getRandomCareer(world.rand));
                applySpecialAI();
                break;
            case "gui.button.prospecting":
                startChore(EnumChore.PROSPECT, player);
                break;
            case "gui.button.hunting":
                startChore(EnumChore.HUNT, player);
                break;
            case "gui.button.fishing":
                startChore(EnumChore.FISH, player);
                break;
            case "gui.button.chopping":
                startChore(EnumChore.CHOP, player);
                break;
            case "gui.button.harvesting":
                startChore(EnumChore.HARVEST, player);
                break;
            case "gui.button.stopworking":
                stopChore();
                break;
        }
    }

    private boolean handleSpecialCaseGift(EntityPlayer player, ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof ItemSpecialCaseGift && !this.isChild()) { // special case gifts are rings so far so prevent giving them to children
            boolean decStackSize = ((ItemSpecialCaseGift) item).handle(player, this);
            if (decStackSize) player.inventory.decrStackSize(player.inventory.currentItem, -1);
            return true;
        } else if (item == Items.CAKE) {
            Optional<Entity> spouse = mca.util.Util.getEntityByUUID(world, get(SPOUSE_UUID).or(Constants.ZERO_UUID));
            if (spouse.isPresent()) {
                EntityVillagerMCA progressor = this.get(GENDER) == EnumGender.FEMALE.getId() ? this : (EntityVillagerMCA) spouse.get();
                progressor.set(HAS_BABY, true);
                progressor.set(BABY_IS_MALE, rand.nextBoolean());
                progressor.spawnParticles(EnumParticleTypes.HEART);
            } else say(Optional.of(player), "gift.cake.fail");
        } else if (item == Items.GOLDEN_APPLE && this.isChild()) {
            this.addGrowth(((startingAge / 4) / 20 * -1));
            return true;
        }

        return false;
    }

    private void onEachClientUpdate() {
        if (get(IS_PROCREATING)) {
            this.rotationYawHead += 50.0F;
        }

        if (this.ticksExisted % 20 == 0) {
            onEachClientSecond();
        }
    }

    private void onEachClientSecond() {
        this.setSizeForAge();
    }

    private void onEachServerUpdate() {
        if (this.ticksExisted % 20 == 0) { // Every second
            onEachServerSecond();
        }

        if (this.ticksExisted % 200 == 0 && this.getHealth() > 0.0F) { // Every 10 seconds and when we're not already dead
            if (this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F); // heal
            }
        }

        if (isChild()) {
            EnumAgeState current = EnumAgeState.byId(get(AGE_STATE));
            EnumAgeState target = EnumAgeState.byCurrentAge(startingAge, getGrowingAge());
            if (current != target) {
                set(AGE_STATE, target.getId());
            }
        }
    }

    private void onEachServerSecond() {
        NBTTagCompound memories = get(PLAYER_HISTORY_MAP);
        memories.getKeySet().forEach((key) -> PlayerHistory.fromNBT(this, UUID.fromString(key), memories.getCompoundTag(key)).update());

        if (get(HAS_BABY)) {
            set(BABY_AGE, get(BABY_AGE) + 1);

            if (get(BABY_AGE) >= VampirismMod.getConfig().babyGrowUpTime * 60) { // grow up time is in minutes and we measure age in seconds
                EntityVillagerMCA child = new EntityVillagerMCA(world, Optional.absent(), Optional.of(get(BABY_IS_MALE) ? EnumGender.MALE : EnumGender.FEMALE));
                child.set(EntityVillagerMCA.AGE_STATE, EnumAgeState.BABY.getId());
                child.setStartingAge(VampirismMod.getConfig().childGrowUpTime * 60 * 20 * -1);
                child.setScaleForAge(true);
                child.setPosition(this.posX, this.posY, this.posZ);
                child.set(EntityVillagerMCA.PARENTS, ParentData.create(this.getUniqueID(), this.get(SPOUSE_UUID).get(), this.get(VILLAGER_NAME), this.get(SPOUSE_NAME)).toNBT());
                world.spawnEntity(child);

                set(HAS_BABY, false);
                set(BABY_AGE, 0);
            }
        }
    }

    public ResourceLocation getTextureResourceLocation() {
        if (get(IS_INFECTED)) {
            return ResourceLocationCache.getResourceLocationFor(String.format("mca:skins/%s/zombievillager.png", get(GENDER) == EnumGender.MALE.getId() ? "male" : "female"));
        } else {
            return ResourceLocationCache.getResourceLocationFor(get(TEXTURE));
        }
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAIProspecting(this));
        this.tasks.addTask(0, new EntityAIHunting(this));
        this.tasks.addTask(0, new EntityAIChopping(this));
        this.tasks.addTask(0, new EntityAIHarvesting(this));
        this.tasks.addTask(0, new EntityAIFishing(this));
        this.tasks.addTask(0, new EntityAIMoveState(this));
        this.tasks.addTask(0, new EntityAIAgeBaby(this));
        this.tasks.addTask(0, new EntityAIProcreate(this));
        this.tasks.addTask(5, new EntityAIGoWorkplace(this));
        this.tasks.addTask(5, new EntityAIGoHangout(this));
        this.tasks.addTask(1, new EntityAISleeping(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.tasks.addTask(0, new EntityAISwimming(this));
    }

    private void applySpecialAI() {
        if (getProfessionForge() == ProfessionsMCA.bandit) {
            this.tasks.taskEntries.clear();
            this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.8D, false));
            this.tasks.addTask(2, new EntityAIMoveThroughVillage(this, 0.6D, false));

            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityVillagerMCA.class, 100, false, false, BANDIT_TARGET_SELECTOR));
            this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        } else if (getProfessionForge() == ModVillages.profession_vampire_rogue || getProfessionForge() == ModVillages.profession_vampire_expert) {
            removeCertainTasks(EntityAIAvoidEntity.class);
            removeCertainTasks(EntityAIWatchClosest.class);
            removeCertainTasks(EntityAIGoHangout.class);
            removeCertainTasks(EntityAISleeping.class);
            removeCertainTasks(EntityAIGoWorkplace.class);
            
            if (world.getDifficulty() == EnumDifficulty.HARD) {
                //Only break doors on hard difficulty
                this.tasks.addTask(1, new EntityAIBreakDoor(this));
                ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
            }
            
            this.tasks_avoidHunter = new EntityAIAvoidEntity<>(this, EntityCreature.class, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION), 10, 1.0, 1.1);
            this.tasks.addTask(2, this.tasks_avoidHunter);
            this.tasks.addTask(2, new VampireAIRestrictSun((EntityVampirism)this));
            this.tasks.addTask(3, new VampireAIFleeSun(this, 0.9, false));
            this.tasks.addTask(3, new VampireAIFleeGarlic((EntityVampirism)this, 0.9, false));
            this.tasks.addTask(4, new EntityAIAttackMeleeNoSun(this, 1.0, false));
            this.tasks.addTask(5, new VampireAIBiteNearbyEntity((EntityVampirism)this));
            this.tasks.addTask(6, new VampireAIFollowAdvanced((EntityVampirism)this, 1.0));
            this.tasks.addTask(7, new VampireAIMoveToBiteable((EntityVampirism)this, 0.75));
            this.tasks.addTask(8, new EntityAIMoveThroughVillageCustom(this, 0.6, true, 600));
            this.tasks.addTask(9, new EntityAIWander(this, 0.7));
            this.tasks.addTask(10, new EntityAIWatchClosestVisible(this, EntityPlayer.class, 8F));
            this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityHunterBase.class, 17F));
            this.tasks.addTask(10, new EntityAILookIdle(this));

            this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
            this.targetTasks.addTask(4, new EntityAIAttackVillage(this));
            this.targetTasks.addTask(4, new EntityAIDefendVillage(this));//Should automatically be mutually exclusive with  attack village
            this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, true, false, false, null)));
            this.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, null)));//TODO maybe make them not attack hunters, although it looks interesting

        } else if (getProfessionForge() == ProfessionsMCA.guard) {
            removeCertainTasks(EntityAIAvoidEntity.class);

            this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.8D, false));
            this.tasks.addTask(2, new EntityAIMoveThroughVillage(this, 0.6D, false));

            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityVillagerMCA.class, 100, false, false, GUARD_TARGET_SELECTOR));
            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 100, false, false, null));
            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityVex.class, 100, false, false, null));
            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityVindicator.class, 100, false, false, null));
        } else {
            //every other villager is allowed to defend itself from zombies while fleeing
            this.tasks.addTask(0, new EntityAIDefendFromTarget(this));

            this.targetTasks.taskEntries.clear();
            this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 100, false, false, null));
        }
    }

    //guards should not run away from zombies
    //TODO: should only avoid zombies when low on health
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

    public void spawnParticles(EnumParticleTypes particleType) {
        if (this.world.isRemote) {
            for (int i = 0; i < 5; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(particleType, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 1.0D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }
        } else {
            NetMCA.INSTANCE.sendToAll(new NetMCA.SpawnParticles(this.getUniqueID(), particleType));
        }
    }

    public void stopChore() {
        set(ACTIVE_CHORE, EnumChore.NONE.getId());
        set(CHORE_ASSIGNING_PLAYER, Optional.of(Constants.ZERO_UUID));
    }

    public void startChore(EnumChore chore, EntityPlayer player) {
        set(ACTIVE_CHORE, chore.getId());
        set(CHORE_ASSIGNING_PLAYER, Optional.of(player.getUniqueID()));
    }

    public boolean playerIsParent(EntityPlayer player) {
        ParentData data = ParentData.fromNBT(get(PARENTS));
        return data.getParent1UUID().equals(player.getUniqueID()) || data.getParent2UUID().equals(player.getUniqueID());
    }

    @Override
    public BlockPos getHomePosition() {
        return home;
    }

    @Override
    public void detachHome() {
        // no-op, skip EntityVillager's detaching homes which messes up MoveTowardsRestriction.
    }

    public String getCurrentActivity() {
        EnumMoveState moveState = EnumMoveState.byId(get(MOVE_STATE));
        if (moveState != EnumMoveState.MOVE) {
            return moveState.getFriendlyName();
        }

        EnumChore chore = EnumChore.byId(get(ACTIVE_CHORE));
        if (chore != EnumChore.NONE) {
            return chore.getFriendlyName();
        }

        return null;
    }

    public void moveTowardsBlock(BlockPos target) {
        moveTowardsBlock(target, 0.5D);
    }

    public void moveTowardsBlock(BlockPos target, double speed) {
        double range = getNavigator().getPathSearchRange() - 6.0D;

        if (getDistanceSq(target) > Math.pow(range, 2.0)) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this, (int) range, 8, new Vec3d(target.getX(), target.getY(), target.getZ()));
            if (vec3d != null && !getNavigator().setPath(getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z), speed)) {
                attemptTeleport(vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            if (!getNavigator().setPath(getNavigator().getPathToPos(target), speed)) {
                attemptTeleport(target.getX(), target.getY(), target.getZ());
            }
        }
    }

    //searches for the nearest bed
    public BlockPos searchBed() {
        List<BlockPos> nearbyBeds = mca.util.Util.getNearbyBlocks(getPos(), world, BlockBed.class, 8, 8);
        List<BlockPos> valid = new ArrayList<>();
        for (BlockPos pos : nearbyBeds) {
            IBlockState state = world.getBlockState(pos);
            if (!(state.getValue(OCCUPIED)) && state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
                valid.add(pos);
            }
        }
        return mca.util.Util.getNearestPoint(getPos(), valid);
    }

    /**
     * Returns the orientation of the bed in degrees.
     */
    @SideOnly(Side.CLIENT)
    public float getBedOrientationInDegrees() {
        BlockPos bedLocation = get(EntityVillagerMCA.BED_POS);
        IBlockState state = bedLocation == BlockPos.ORIGIN ? null : this.world.getBlockState(bedLocation);
        if (state != null && state.getBlock().isBed(state, world, bedLocation, this)) {
            EnumFacing enumfacing = state.getBlock().getBedDirection(state, world, bedLocation);

            switch (enumfacing) {
                case SOUTH:
                    return 90.0F;
                case WEST:
                    return 0.0F;
                case NORTH:
                    return 270.0F;
                case EAST:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    public boolean isSleeping() {
        return get(SLEEPING);
    }

    private void updateSleeping() {
        if (isSleeping()) {
            BlockPos bedLocation = get(EntityVillagerMCA.BED_POS);

            final IBlockState state = this.world.isBlockLoaded(bedLocation) ? this.world.getBlockState(bedLocation) : null;
            final boolean isBed = state != null && state.getBlock().isBed(state, this.world, bedLocation, this);

            if (isBed) {
                final EnumFacing enumfacing = state.getBlock() instanceof BlockHorizontal ? state.getValue(BlockHorizontal.FACING) : null;

                if (enumfacing != null) {
                    float f1 = 0.5F + (float) enumfacing.getXOffset() * 0.4F;
                    float f = 0.5F + (float) enumfacing.getZOffset() * 0.4F;
                    this.setRenderOffsetForSleep(enumfacing);
                    this.setPosition((double) ((float) bedLocation.getX() + f1), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + f));
                } else {
                    this.setPosition((double) ((float) bedLocation.getX() + 0.5F), (double) ((float) bedLocation.getY() + 0.6875F), (double) ((float) bedLocation.getZ() + 0.5F));
                }

                this.setSize(0.2F, 0.2F);

                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            } else {
                set(EntityVillagerMCA.BED_POS, BlockPos.ORIGIN);
                stopSleeping();
            }
        } else {
            this.setSize(0.6F, 1.8F);
        }
    }

    private void setRenderOffsetForSleep(EnumFacing bedDirection) {
        this.renderOffsetX = -1.0F * (float) bedDirection.getXOffset();
        this.renderOffsetZ = -1.0F * (float) bedDirection.getZOffset();
    }

    public void startSleeping() {
        if (this.isRiding()) {
            this.dismountRidingEntity();
        }

        set(SLEEPING, true);

        BlockPos bedLocation = get(EntityVillagerMCA.BED_POS);
        IBlockState blockstate = this.world.getBlockState(bedLocation);
        if (blockstate.getBlock() == Blocks.BED) {
            blockstate.getBlock().setBedOccupied(world, bedLocation, null, true);
        }
    }

    public void stopSleeping() {
        BlockPos bedLocation = get(EntityVillagerMCA.BED_POS);
        if (bedLocation != BlockPos.ORIGIN) {
            IBlockState blockstate = this.world.getBlockState(bedLocation);

            if (blockstate.getBlock().isBed(blockstate, world, bedLocation, this)) {
                blockstate.getBlock().setBedOccupied(world, bedLocation, null, false);
                BlockPos blockpos = blockstate.getBlock().getBedSpawnPosition(blockstate, world, bedLocation, null);

                if (blockpos == null) {
                    blockpos = bedLocation.up();
                }

                this.setPosition((double) ((float) blockpos.getX() + 0.5F), (double) ((float) blockpos.getY() + 0.1F), (double) ((float) blockpos.getZ() + 0.5F));
            }
        }

        set(SLEEPING, false);
    }

    /**
     * Rules to consider for {@link #getCanSpawnHere()}
     */
    protected SpawnRestriction spawnRestriction = SpawnRestriction.NORMAL;
    private boolean countAsMonsterForSpawn;

    protected EnumStrength garlicResist = EnumStrength.NONE;
    protected boolean canSuckBloodFromPlayer = false;
    protected boolean vulnerableToFire = true;
    private boolean sundamageCache;
    private EnumStrength garlicCache = EnumStrength.NONE;
    /**
     * If the vampire should spawn a vampire soul at the end of its death animation.
     * No need to store this in NBT as it is only set during onDeath() so basically 20 ticks beforehand.
     */
    private boolean dropSoul = false;

    /**
     * Select rules to consider for {@link #getCanSpawnHere()}
     */
    public void setSpawnRestriction(SpawnRestriction spawnRules) {
        this.spawnRestriction = spawnRules;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        for (ItemStack e : entity.getArmorInventoryList()) {
            if (e != null && e.getItem() instanceof ItemHunterCoat) {
                int j = 1;
                if (((ItemHunterCoat) e.getItem()).getTier(e).equals(TIER.ENHANCED))
                    j = 2;
                else if (((ItemHunterCoat) e.getItem()).getTier(e).equals(TIER.ULTIMATE))
                    j = 3;
                if (getRNG().nextInt((4 - j) * 2) == 0)
                    addPotionEffect(new PotionEffect(ModPotions.poison, (int) (20 * Math.sqrt(j)), j));
            }
        }

        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), this.getProfessionForge() == ProfessionsMCA.guard ? 9.0F : 2.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return super.attackEntityFrom(damageSource, amount);
    }

    public enum SpawnRestriction {
        /**
         * Only entity spawn checks
         */
        NONE(0),
        /**
         * +No direct sunlight or garlic
         */
        SIMPLE(1),
        /**
         * +Avoid villages and daytime (random chance)
         */
        NORMAL(2),
        /**
         * +Only at low light level or in vampire biome on cursed earth
         */
        SPECIAL(3);

        int level;

        SpawnRestriction(int level) {
            this.level = level;
        }
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public float getEyeHeight() {
        return height * 0.875f;
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        return super.isCreatureType(type, forSpawnCount);
    }



    @Override
    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            if (isEntityAlive() && isInWater()) {
                setAir(300);
                if (ticksExisted % 16 == 4) {
                    addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
        super.onLivingUpdate();
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     *
     * @param amount
     * @return
     */
    protected float calculateFireDamage(float amount) {
        return amount;
    }

    @Override
    protected void onDeathUpdate() {
        if (this.deathTime == 19) {
            if (!this.world.isRemote && (dropSoul && this.world.getGameRules().getBoolean("doMobLoot"))) {
                this.world.spawnEntity(new EntitySoulOrb(this.world, this.posX, this.posY, this.posZ, EntitySoulOrb.TYPE.VAMPIRE));
            }
        }
        super.onDeathUpdate();
    }

    private boolean isCursedTerrain(IBlockState iBlockState) {
    	return ModBlocks.cursed_earth.equals(iBlockState.getBlock());
    }

	@Override
	public IFaction getFaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityLivingBase getRepresentingEntity() {
		// TODO Auto-generated method stub
		return null;
	}
}