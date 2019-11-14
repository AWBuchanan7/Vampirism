package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import mca.entity.EntityVillagerMCA;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModEventFactory {
	
    public static boolean fireVillagerCaptureEvent(@Nonnull IVampirismVillage village, @Nonnull List<EntityVillagerMCA> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn, @Nonnull AxisAlignedBB affectedArea) {
        VampirismVillageEvent.VillagerCaptureFinish event = new VampirismVillageEvent.VillagerCaptureFinish(village, villagerIn, controllingFactionIn, capturingFactionIn, affectedArea);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult().equals(Result.DENY);
    }

    public static ResourceLocation fireSpawnCaptureEntityEvent(@Nullable IVampirismVillage village, @Nonnull IFaction<?> faction) {
        VampirismVillageEvent.SpawnCaptureEntity event = new VampirismVillageEvent.SpawnCaptureEntity(village, faction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEntity();
    }

    public static VampirismVillageEvent.SpawnNewVillager fireSpawnNewVillagerEvent(@Nonnull IVampirismVillage village, @Nonnull EntityVillagerMCA seed, boolean converted, IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(village, seed, converted, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireReplaceVillageBlockEvent(@Nullable IVampirismVillage village, @Nonnull World world, @Nonnull IBlockState b, @Nonnull BlockPos pos, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.ReplaceBlock event = new VampirismVillageEvent.ReplaceBlock(village, world, b, pos, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean fireInitiateCaptureEvent(@Nonnull IVampirismVillage village, @Nonnull World world, @Nullable IPlayableFaction<?> controllingFaction, @Nonnull IPlayableFaction<?> capturingFaction) {
        VampirismVillageEvent.InitiateCapture event = new VampirismVillageEvent.InitiateCapture(village, world, controllingFaction, capturingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getResult().equals(Result.DENY);
    }

    public static VampirismVillageEvent.SpawnFactionVillager fireSpawnFactionVillagerEvent(@Nullable IVampirismVillage village, @Nonnull EntityVillagerMCA seed, @Nonnull IPlayableFaction<?> controllingFaction) {
        VampirismVillageEvent.SpawnFactionVillager event = new VampirismVillageEvent.SpawnFactionVillager(village, seed, controllingFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void fireUpdateBoundingBoxEvent(@Nullable IVampirismVillage village, @Nonnull StructureBoundingBox bb) {
        VampirismVillageEvent.UpdateBoundingBox event = new VampirismVillageEvent.UpdateBoundingBox(village, bb);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static Result fireCanJoinFactionEvent(@Nonnull IFactionPlayerHandler playerHandler, @Nullable IPlayableFaction<?> currentFaction, IPlayableFaction<?> newFaction){
        FactionEvent.CanJoinFaction event = new FactionEvent.CanJoinFaction(playerHandler, currentFaction, newFaction);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean fireChangeLevelOrFactionEvent(@Nonnull IFactionPlayerHandler player, @Nullable IPlayableFaction currentFaction, int currentLevel, @Nullable IPlayableFaction newFaction, int newLevel){
        FactionEvent.ChangeLevelOrFaction event = new FactionEvent.ChangeLevelOrFaction(player, currentFaction, currentLevel, newFaction, newLevel);
        return MinecraftForge.EVENT_BUS.post(event);
    }
}
