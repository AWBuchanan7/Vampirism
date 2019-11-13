package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.enchantment.EnchantmentVampireSlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModEnchantments {

    public static final EnchantmentVampireSlayer vampireslayer = getNull();


    static void registerEnchantments(IForgeRegistry<Enchantment> registry) {
        registry.register(new EnchantmentVampireSlayer(Enchantment.Rarity.UNCOMMON));
    }

}
