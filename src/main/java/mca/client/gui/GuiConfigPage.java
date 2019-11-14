package mca.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public class GuiConfigPage extends GuiConfig {
    public GuiConfigPage(GuiScreen parent) {
        this(parent,
        		VampirismMod.getConfig().getCategories(),
                REFERENCE.MODID, false, false, GuiConfig.getAbridgedConfigPath(VampirismMod.getConfig().getInstance().toString()));
    }

    public GuiConfigPage(GuiScreen parentScreen, List<IConfigElement> configElements, String modID, boolean allRequireWorldRestart, boolean allRequireMcRestart, String title) {
        super(parentScreen, configElements, modID, allRequireWorldRestart, allRequireMcRestart, title);
    }
}