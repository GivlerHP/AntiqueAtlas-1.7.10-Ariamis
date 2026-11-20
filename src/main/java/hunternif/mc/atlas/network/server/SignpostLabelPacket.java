package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractServerMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.signpost.SignpostBlock;
import hunternif.mc.atlas.signpost.SignpostMarkerItem;
import hunternif.mc.atlas.signpost.SignpostTileEntity;
import hunternif.mc.atlas.util.Log;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;

/**
 * A request from a client to name a signpost, creating a global marker
 * visible in all atlases.
 */
public class SignpostLabelPacket extends AbstractServerMessage<SignpostLabelPacket> {
	private static final int MAX_LABEL_LENGTH = 32;
	/** Max squared distance from the player to the signpost. */
	private static final double MAX_DISTANCE_SQ = 64;

	private int x, y, z;
	private String label;

	public SignpostLabelPacket() {}

	public SignpostLabelPacket(int x, int y, int z, String label) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.label = label;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		label = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		ByteBufUtils.writeUTF8String(buffer, label);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if (!(te instanceof SignpostTileEntity)) {
			return;
		}
		SignpostTileEntity signpost = (SignpostTileEntity) te;
		if (signpost.hasMarker()) {
			return; // Already named.
		}
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack == null || !(stack.getItem() instanceof SignpostMarkerItem)) {
			Log.warn("Player %s attempted to name a signpost without the marker item",
					player.getGameProfile().getName());
			return;
		}
		if (player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) > MAX_DISTANCE_SQ) {
			return;
		}
		label = label == null ? "" : label.trim();
		if (label.isEmpty()) {
			return;
		}
		if (label.length() > MAX_LABEL_LENGTH) {
			label = label.substring(0, MAX_LABEL_LENGTH);
		}
		int dimension = player.worldObj.provider.dimensionId;
		MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
		Marker marker = data.createAndSaveMarker(SignpostBlock.MARKER_TYPE, label, dimension, x, z, true);
		// Forward the new global marker to everyone, including the sender:
		PacketDispatcher.sendToAll(new MarkersPacket(dimension, marker));
		signpost.setMarker(marker.getId(), label);
	}
}
