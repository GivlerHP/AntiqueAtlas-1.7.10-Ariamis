package hunternif.mc.atlas.signpost;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Stores the global marker created by clicking this signpost with the marker
 * item, so that the marker is removed from all atlases when the block is
 * destroyed. The label is synced to the client for rendering on the board.
 */
public class SignpostTileEntity extends TileEntity {
	private int markerId = 0;
	private String label = "";

	public boolean hasMarker() {
		return markerId != 0;
	}

	public int getMarkerId() {
		return markerId;
	}

	public String getLabel() {
		return label;
	}

	public void setMarker(int markerId, String label) {
		this.markerId = markerId;
		this.label = label == null ? "" : label;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("markerId", markerId);
		nbt.setString("label", label);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		markerId = nbt.getInteger("markerId");
		label = nbt.getString("label");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}
}
