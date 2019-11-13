package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.forest.EntityLizardfolk;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.lubriciouskin.iymts_mod.renderer.mobs.RenderIYLizardMan;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLizardfolk extends RenderIYLizardMan {

	public ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/lizardfolk.png");

    public RenderLizardfolk(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    protected ResourceLocation getEntityTexture(EntityLizardfolk entity) {
        return texture;
    }
    
}
