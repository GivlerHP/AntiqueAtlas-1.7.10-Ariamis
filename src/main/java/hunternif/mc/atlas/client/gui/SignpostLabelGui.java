package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.server.SignpostLabelPacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

/**
 * A small dialog for entering the label of a signpost. The label is sent to
 * the server, which creates a global marker visible in all atlases.
 */
public class SignpostLabelGui extends GuiComponent {
	private static final int MAX_LABEL_LENGTH = 32;

	private GuiButton btnDone;
	private GuiButton btnCancel;
	private GuiTextField textField;
	private int signX, signY, signZ;

	public SignpostLabelGui setSignpost(int x, int y, int z) {
		this.signX = x;
		this.signY = y;
		this.signZ = z;
		return this;
	}

	@Override
	public void initGui() {
		btnDone = new GuiButton(0, width / 2 - 100 - 2, height / 2 + 40, 100, 20, I18n.format("gui.done"));
		buttonList.add(btnDone);
		btnCancel = new GuiButton(1, width / 2 + 2, height / 2 + 40, 100, 20, I18n.format("gui.cancel"));
		buttonList.add(btnCancel);
		textField = new GuiTextField(fontRendererObj, (width - 200) / 2, height / 2 - 81, 200, 20);
		textField.setFocused(true);
		textField.setText("");
		textField.setMaxStringLength(MAX_LABEL_LENGTH);
	}

	@Override
	public void updateScreen() {
		textField.updateCursorCounter();
	}

	@Override
	protected void mouseClicked(int x, int y, int code) {
		super.mouseClicked(x, y, code);
		textField.mouseClicked(x, y, code);
	}

	@Override
	protected void keyTyped(char c, int code) {
		super.keyTyped(c, code);
		textField.textboxKeyTyped(c, code);
		if (code == Keyboard.KEY_RETURN && !textField.getText().trim().isEmpty()) {
			done();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		drawCenteredString(I18n.format("gui.antiqueatlas.signpost.label"), height / 2 - 97, 0xFFFFFF, true);
		textField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == btnDone) {
			done();
		} else if (button == btnCancel) {
			close();
		}
	}

	private void done() {
		String label = textField.getText().trim();
		if (!label.isEmpty()) {
			PacketDispatcher.sendToServer(new SignpostLabelPacket(signX, signY, signZ, label));
		}
		close();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
