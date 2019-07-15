package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModVillages;
import de.teamlapen.vampirism.entity.FactionVillagerEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;


public class HunterFactionVillagerEntity extends FactionVillagerEntity implements IHunter {

    private final static VillagerEntity.ITradeList[][] TRADES = {
            {
                    new ItemsForSouls(new PriceInfo(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_normal), new ItemStack(ModItems.obsidian_armor_feet_normal), new ItemStack(ModItems.armor_of_swiftness_feet_normal)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_normal), new ItemStack(ModItems.obsidian_armor_legs_normal), new ItemStack(ModItems.armor_of_swiftness_legs_normal)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_normal), new ItemStack(ModItems.obsidian_armor_chest_normal), new ItemStack(ModItems.armor_of_swiftness_chest_normal)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_normal), new ItemStack(ModItems.obsidian_armor_head_normal), new ItemStack(ModItems.armor_of_swiftness_head_normal)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(10, 20), ModItems.item_garlic, new PriceInfo(2, 5)),
                    new ItemsForSouls(new PriceInfo(50, 100), Items.DIAMOND, new PriceInfo(1, 1))
            },
            {
                    new ItemsForSouls(new PriceInfo(10, 15), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_enhanced), new ItemStack(ModItems.obsidian_armor_feet_enhanced), new ItemStack(ModItems.armor_of_swiftness_feet_enhanced)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(25, 35), new ItemStack[]{new ItemStack(ModItems.hunter_coat_legs_enhanced), new ItemStack(ModItems.obsidian_armor_legs_enhanced), new ItemStack(ModItems.armor_of_swiftness_legs_enhanced)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(30, 40), new ItemStack[]{new ItemStack(ModItems.hunter_coat_chest_enhanced), new ItemStack(ModItems.obsidian_armor_chest_enhanced), new ItemStack(ModItems.armor_of_swiftness_chest_enhanced)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(20, 30), new ItemStack[]{new ItemStack(ModItems.hunter_coat_head_enhanced), new ItemStack(ModItems.obsidian_armor_head_enhanced), new ItemStack(ModItems.armor_of_swiftness_head_enhanced)}, new PriceInfo(1, 1)),
                    new ItemsForSouls(new PriceInfo(40, 90), Items.DIAMOND, new PriceInfo(1, 2))
            },
            {
                    new ItemsForSouls(new PriceInfo(100, 200), new ItemStack[]{new ItemStack(ModItems.hunter_coat_feet_ultimate), new ItemStack(ModItems.obsidian_armor_feet_ultimate), new ItemStack(ModItems.armor_of_swiftness_feet_ultimate), new ItemStack(ModItems.hunter_coat_legs_ultimate), new ItemStack(ModItems.obsidian_armor_legs_ultimate), new ItemStack(ModItems.armor_of_swiftness_legs_ultimate), new ItemStack(ModItems.hunter_coat_chest_ultimate), new ItemStack(ModItems.obsidian_armor_chest_ultimate), new ItemStack(ModItems.armor_of_swiftness_chest_ultimate), new ItemStack(ModItems.hunter_coat_head_ultimate), new ItemStack(ModItems.obsidian_armor_head_ultimate), new ItemStack(ModItems.armor_of_swiftness_head_ultimate)}, new PriceInfo(1, 1))
            }
    };

    public HunterFactionVillagerEntity(EntityType<? extends HunterFactionVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(DifficultyInstance difficulty, @Nullable ILivingEntityData livingdata, @Nullable CompoundNBT itemNbt) {
        this.setProfession(ModVillages.profession_hunter_expert);
        return this.finalizeMobSpawn(difficulty, livingdata, itemNbt, false);
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        if (player.isCreative() || Helper.isHunter(player)) {
            return super.processInteract(player, hand);
        } else {
            if (player.world.isRemote) {
                player.sendStatusMessage(new TranslationTextComponent(Helper.isVampire(player) ? "text.vampirism.hunter_villager.decline_trade_vampire" : "text.vampirism.hunter_villager.decline_trade_normal"), false);
            }
            return true;
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        PlayerEntity player = getCustomer();
        if (player != null) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.hunter_villager.trade_successful"), false);
        }
    }

    @Nonnull
    @Override
    protected ITradeList[] getTrades(int level) {
        if (level >= 2) {
            return ArrayUtils.addAll(TRADES[1], TRADES[2]);
        }
        return TRADES[level];//Must not be >2
    }

    private static class ItemsForSouls implements ITradeList {
        private ItemStack[] sellingStacks;
        private PriceInfo buying;
        private PriceInfo selling;

        ItemsForSouls(PriceInfo price, Item sell, PriceInfo amount) {
            this(price, new ItemStack[]{new ItemStack(sell)}, amount);
        }

        ItemsForSouls(PriceInfo price, ItemStack sell, PriceInfo amount) {
            this(price, new ItemStack[]{sell}, amount);

        }

        ItemsForSouls(PriceInfo price, ItemStack[] sellingStacks, PriceInfo amount) {
            this.sellingStacks = sellingStacks;
            this.buying = price;
            this.selling = amount;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            int price = this.buying.getPrice(random);
            int count = this.selling.getPrice(random);
            ItemStack selling = sellingStacks[random.nextInt(sellingStacks.length)].copy();
            selling.setCount(count);
            ItemStack first = new ItemStack(ModItems.soul_orb_vampire, price);
            ItemStack second = ItemStack.EMPTY;
            if (price > 64) {
                second = new ItemStack(ModItems.soul_orb_vampire, price - 64);
                first.setCount(price - second.getCount());
            }
            recipeList.add(new MerchantRecipe(first, second, selling));
        }
    }
}