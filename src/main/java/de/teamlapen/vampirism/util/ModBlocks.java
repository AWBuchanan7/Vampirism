package de.teamlapen.vampirism.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import de.teamlapen.vampirism.block.BlockBloodAltar;
import de.teamlapen.vampirism.block.BlockVampirism;

public class ModBlocks {

public static BlockVampirism bloodAltar = new BlockBloodAltar();
	
	public static void init() {
		GameRegistry.registerBlock(bloodAltar, "bloodAltar");
	}
}
