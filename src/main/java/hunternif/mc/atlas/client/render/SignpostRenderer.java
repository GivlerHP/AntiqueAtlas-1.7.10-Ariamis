package hunternif.mc.atlas.client.render;

import hunternif.mc.atlas.signpost.SignpostTileEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Renders a signpost as a vanilla sign, reusing {@link ModelSign} and the
 * vanilla sign texture. The label is written on the board, like sign text.
 */
public class SignpostRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation signTexture = new ResourceLocation("textures/entity/sign.png");
	/** Max width of a line of text on the board, in font pixels. */
	private static final int MAX_TEXT_WIDTH = 90;
	private static final int MAX_LINES = 4;

	private final ModelSign model = new ModelSign();

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
		if (!(te instanceof SignpostTileEntity)) {
			return;
		}
		SignpostTileEntity signpost = (SignpostTileEntity) te;

		// The model part is a verbatim copy of the standing sign branch of
		// TileEntitySignRenderer:
		GL11.glPushMatrix();
		float f1 = 0.6666667F;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * f1, (float)z + 0.5F);
		float f2 = (float)(signpost.getBlockMetadata() * 360) / 16.0F;
		GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
		bindTexture(signTexture);
		GL11.glPushMatrix();
		GL11.glScalef(f1, -f1, -f1);
		model.renderSign();
		GL11.glPopMatrix();

		String label = signpost.getLabel();
		if (label != null && !label.isEmpty()) {
			FontRenderer font = this.func_147498_b();
			List<String> lines = wrapLabel(font, label);
			renderBoardText(font, lines, f1);
			GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
			renderBoardText(font, lines, f1);
		}

		GL11.glPopMatrix();
	}

	/** Draws the given lines centered on the board face the current
	 * transformation is positioned at. */
	private void renderBoardText(FontRenderer font, List<String> lines, float f1) {
		float f3 = 0.016666668F * f1;
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
		GL11.glScalef(f3, -f3, f3);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		GL11.glDepthMask(false);
		for (int i = 0; i < lines.size(); ++i) {
			String line = lines.get(i);
			font.drawString(line, -font.getStringWidth(line) / 2, i * 10 - lines.size() * 5, 0);
		}
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	private List<String> wrapLabel(FontRenderer font, String label) {
		List<String> lines = new ArrayList<String>();
		for (String word : label.split(" +")) {
			if (word.isEmpty()) continue;
			int start = 0;
			while (start < word.length()) {
				int end = word.length();
				while (end > start + 1 && font.getStringWidth(word.substring(start, end)) > MAX_TEXT_WIDTH) {
					end--;
				}
				appendPiece(font, lines, word.substring(start, end), start == 0);
				if (lines.size() == MAX_LINES) {
					return lines;
				}
				start = end;
			}
		}
		return lines;
	}

	/** Appends a piece of a word to the last line, if it fits, otherwise
	 * starts a new line. Pieces continuing a split word are joined without
	 * a space.
	 * @param newWord whether this piece starts a new word. */
	private void appendPiece(FontRenderer font, List<String> lines, String piece, boolean newWord) {
		if (!lines.isEmpty()) {
			int i = lines.size() - 1;
			String candidate = lines.get(i) + (newWord ? " " : "") + piece;
			if (font.getStringWidth(candidate) <= MAX_TEXT_WIDTH) {
				lines.set(i, candidate);
				return;
			}
		}
		lines.add(piece);
	}
}
