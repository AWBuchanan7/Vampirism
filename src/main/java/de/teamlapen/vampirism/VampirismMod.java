package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.Logger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.converted.BiteableRegistry;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.factions.HunterFaction;
import de.teamlapen.vampirism.entity.factions.VampireFaction;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.SkillHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.skills.SkillRegistry;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

/**
 * Main class for Vampirism
 * TODO readd "required-after:teamlapen-lib;"
 */
@Mod(modid = REFERENCE.MODID, name = REFERENCE.NAME, version = REFERENCE.VERSION, acceptedMinecraftVersions = "[1.8.9]", dependencies = "required-after:Forge@[" + REFERENCE.FORGE_VERSION_MIN + ",);", guiFactory = "de.teamlapen.vampirism.client.core.ModGuiFactory")
public class VampirismMod {

    public final static Logger log = new Logger(REFERENCE.MODID, "de.teamlapen.vampirism");
    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;
    @SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.proxy.ServerProxy")
    public static IProxy proxy;
    public static boolean inDev = false;
    public static AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static CreativeTabs creativeTab = new CreativeTabs(REFERENCE.MODID) {
        @Override
        public Item getTabIconItem() {
            return ModItems.vampireFang;
        }
    };

    public static boolean isRealism() {
        return Configs.realism_mode;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        log.t("Test balance value %s", Balance.leveling.TEST_VALUE);


        MinecraftForge.EVENT_BUS.register(new ModEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());

        HelperRegistry.registerPlayerEventReceivingProperty(VampireFaction.instance().prop);
        HelperRegistry.registerPlayerEventReceivingProperty(HunterFaction.instance().prop);
        HelperRegistry.registerSyncablePlayerProperty(VampireFaction.instance().prop, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerProperty(HunterFaction.instance().prop, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerProperty(VReference.FACTION_PLAYER_HANDLER_PROP, FactionPlayerHandler.class);
        Achievements.registerAchievement();
        proxy.onInitStep(IInitListener.Step.INIT, event);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new VampirismCommand());
        event.registerServerCommand(new TestCommand());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((BiteableRegistry) VampirismAPI.biteableRegistry()).finishRegistration(Balance.mobProps.CONVERTED_MOB_DEFAULT_DMG);
        proxy.onInitStep(IInitListener.Step.POST_INIT, event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        checkDevEnv();
        setupAPI1();
        Configs.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        Balance.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        setupAPI2();

        dispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
        proxy.onInitStep(IInitListener.Step.PRE_INIT, event);
        SkillHandler.registerDefaultSkills();


    }

    private void checkDevEnv() {
        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            inDev = true;
            log.inDev = true;
        }
    }

    /**
     * Setup API during pre-init before configs are loaded
     */
    private void setupAPI1() {
        FactionRegistry factionRegistry = new FactionRegistry();
        SundamageRegistry sundamageRegistry = new SundamageRegistry();
        BiteableRegistry biteableRegistry = new BiteableRegistry();
        SkillRegistry skillRegistry = new SkillRegistry();
        VampirismAPI.setUp(factionRegistry, sundamageRegistry, biteableRegistry, skillRegistry);
        VReference.VAMPIRE_FACTION = VampireFaction.instance();
        VReference.HUNTER_FACTION = HunterFaction.instance();
        factionRegistry.addFaction(VReference.VAMPIRE_FACTION);
        factionRegistry.addFaction(VReference.HUNTER_FACTION);
        biteableRegistry.setDefaultConvertingHandlerCreator(new BiteableRegistry.ICreateDefaultConvertingHandler() {
            @Override
            public IConvertingHandler create(IConvertingHandler.IDefaultHelper helper) {
                return new DefaultConvertingHandler(helper);
            }
        });//DefaultConvertingHandler::new

        VReference.hunterCreatureType = EnumHelper.addCreatureType("vampirism:hunter", IHunter.class, 30, Material.air, false, false);
        VReference.vampireCreatureType = EnumHelper.addCreatureType("vampirism:vampire", IVampire.class, 30, Material.air, false, false);
    }

    /**
     * Setup API during pre-init after configs are loaded
     */
    private void setupAPI2() {

    }

}
