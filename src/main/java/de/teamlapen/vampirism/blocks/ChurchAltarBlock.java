package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Placed in some churches
 */
public class ChurchAltarBlock extends VampirismBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final static String regName = "church_altar";
    private static final VoxelShape SHAPEX = makeShape();
    private static final VoxelShape SHAPEZ = UtilLib.rotateShape(SHAPEX, UtilLib.RotationAmount.NINETY);

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(1, 0, 5, 15, 1, 12);
        VoxelShape b = Block.makeCuboidShape(7, 1, 7, 9, 12, 11);
        VoxelShape c = Block.makeCuboidShape(1, 9, 3, 15, 14, 13);
        VoxelShape r = VoxelShapes.or(a, b);
        VoxelShape d = VoxelShapes.or(r, c);
        return d;
    }


    public ChurchAltarBlock() {
        super(regName, Properties.create(Material.WOOD).hardnessAndResistance(0.5f));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));

    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        Direction dir = blockState.get(FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) return SHAPEX;
        return SHAPEZ;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).with(FACING, ctx.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
        ItemStack heldItem = player.getHeldItem(hand);
        if (handler.isInFaction(VReference.VAMPIRE_FACTION)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return true;
        } else if (!heldItem.isEmpty()) {
            if (ModItems.holy_salt_water.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                boolean enhanced = handler.isInFaction(VReference.HUNTER_FACTION) && ((IHunterPlayer) handler.getCurrentFactionPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.holy_water_enhanced);
                ItemStack newStack = new ItemStack(enhanced ? ModItems.holy_water_bottle_enhanced : ModItems.holy_water_bottle_normal, heldItem.getCount());
                player.setHeldItem(hand, newStack);
                return true;
            } else if (ModItems.pure_salt.equals(heldItem.getItem())) {
                if (world.isRemote) return true;
                player.setHeldItem(hand, new ItemStack(ModItems.holy_salt, heldItem.getCount()));
            }
        }
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
