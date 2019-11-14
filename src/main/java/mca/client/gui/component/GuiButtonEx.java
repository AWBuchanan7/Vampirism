package mca.client.gui.component;

import de.teamlapen.vampirism.VampirismMod;
import lombok.Getter;
import mca.api.types.APIButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiButtonEx extends GuiButton {
    private APIButton apiButton;

    public APIButton getApiButton() {
		return apiButton;
	}

	public void setApiButton(APIButton apiButton) {
		this.apiButton = apiButton;
	}

	public GuiButtonEx(GuiScreen gui, APIButton apiButton) {
        super(apiButton.getId(), (gui.width / 2) + apiButton.getX(), (gui.height / 2) + apiButton.getY(), apiButton.getWidth(), apiButton.getHeight(), VampirismMod.getLocalizer().localize(apiButton.getIdentifier()));
        this.apiButton = apiButton;
    }
}
