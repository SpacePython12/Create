package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class StockKeeperOpenRequestScreenPacket extends SimplePacketBase {

	private BlockPos pos;
	private boolean isAdmin;
	private boolean isLocked;

	public StockKeeperOpenRequestScreenPacket(BlockPos pos, boolean isAdmin, boolean isLocked) {
		this.pos = pos;
		this.isAdmin = isAdmin;
		this.isLocked = isLocked;
	}

	public StockKeeperOpenRequestScreenPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		isAdmin = buffer.readBoolean();
		isLocked = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(isAdmin);
		buffer.writeBoolean(isLocked);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null)
				return;
			ItemStack mainHandItem = player.getMainHandItem();
			final boolean encodeMode =
				AllItemTags.TABLE_CLOTHS.matches(mainHandItem) || AllBlocks.REDSTONE_REQUESTER.isIn(mainHandItem);
			if (player.level()
				.getBlockEntity(pos) instanceof StockTickerBlockEntity be)
				ScreenOpener.open(new StockTickerRequestScreen(be, isAdmin, isLocked, encodeMode));
		});
		return true;
	}

}
