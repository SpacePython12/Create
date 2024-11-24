package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;

import net.createmod.catnip.utility.lang.Components;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;

public class RedstoneRequesterBlockEntity extends StockCheckingBlockEntity implements MenuProvider {

	public boolean allowPartialRequests;
	public PackageOrder encodedRequest = PackageOrder.empty();
	public String encodedTargetAdress = "";

	public boolean lastRequestSucceeded;

	protected boolean redstonePowered;

	public RedstoneRequesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		allowPartialRequests = false;
	}

	protected void onRedstonePowerChanged() {
		boolean hasNeighborSignal = level.hasNeighborSignal(worldPosition);
		if (redstonePowered == hasNeighborSignal)
			return;

		lastRequestSucceeded = false;
		if (hasNeighborSignal)
			triggerRequest();

		redstonePowered = hasNeighborSignal;
		notifyUpdate();
	}

	public void triggerRequest() {
		if (encodedRequest.isEmpty())
			return;

		if (!allowPartialRequests) {
			InventorySummary summaryOfOrder = new InventorySummary();
			encodedRequest.stacks()
				.forEach(summaryOfOrder::add);

			InventorySummary summary = getAccurateSummary();
			for (BigItemStack entry : summaryOfOrder.getStacks())
				if (summary.getCountOf(entry.stack) < entry.count)
					return;
		}

		broadcastPackageRequest(RequestType.REDSTONE, encodedRequest, null, encodedTargetAdress);
		lastRequestSucceeded = true;
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		redstonePowered = tag.getBoolean("Powered");
		lastRequestSucceeded = tag.getBoolean("Success");
		allowPartialRequests = tag.getBoolean("AllowPartial");
		encodedRequest = PackageOrder.read(tag.getCompound("EncodedRequest"));
		encodedTargetAdress = tag.getString("EncodedAddress");
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putBoolean("Powered", redstonePowered);
		tag.putBoolean("Success", lastRequestSucceeded);
		tag.putBoolean("AllowPartial", allowPartialRequests);
		tag.putString("EncodedAddress", encodedTargetAdress);
		tag.put("EncodedRequest", encodedRequest.write());
	}

	public InteractionResult use(Player player) {
		if (player == null || player.isCrouching())
			return InteractionResult.PASS;
		if (player instanceof FakePlayer)
			return InteractionResult.PASS;
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		if (!behaviour.mayInteractMessage(player))
			return InteractionResult.SUCCESS;

		NetworkHooks.openScreen((ServerPlayer) player, this, worldPosition);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Component getDisplayName() {
		return Components.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return RedstoneRequesterMenu.create(pContainerId, pPlayerInventory, this);
	}

}
