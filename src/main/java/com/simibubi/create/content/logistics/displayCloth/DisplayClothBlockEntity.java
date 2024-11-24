package com.simibubi.create.content.logistics.displayCloth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.displayCloth.ShoppingListItem.ShoppingList;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.RemoveBlockEntityPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import io.github.fabricators_of_create.porting_lib.util.NetworkHooks;
import net.createmod.catnip.utility.NBTHelper;
import net.createmod.catnip.utility.lang.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayClothBlockEntity extends SmartBlockEntity implements MenuProvider {

	public AutoRequestData requestData;
	public List<ItemStack> manuallyAddedItems;
	public ItemStack paymentItem;
	public int paymentAmount;
	public UUID owner;

	private List<ItemStack> renderedItemsForShop;

	public DisplayClothBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		manuallyAddedItems = new ArrayList<>();
		requestData = new AutoRequestData();
		paymentItem = ItemStack.EMPTY;
		paymentAmount = 1;
		owner = null;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	public List<ItemStack> getItemsForRender() {
		if (isShop()) {
			if (renderedItemsForShop == null)
				renderedItemsForShop = requestData.encodedRequest.stacks()
					.stream()
					.map(b -> b.stack)
					.limit(4)
					.toList();
			return renderedItemsForShop;
		}

		return manuallyAddedItems;
	}

	public boolean isShop() {
		return !requestData.encodedRequest.isEmpty();
	}

	public InteractionResult use(Player player) {
		if (isShop())
			return useShop(player);

		ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

		if (heldItem.isEmpty()) {
			if (manuallyAddedItems.isEmpty())
				return InteractionResult.SUCCESS;
			player.setItemInHand(InteractionHand.MAIN_HAND, manuallyAddedItems.remove(manuallyAddedItems.size() - 1));

			if (manuallyAddedItems.isEmpty()) {
				level.setBlock(worldPosition, getBlockState().setValue(DisplayClothBlock.HAS_BE, false), 3);
				AllPackets.getChannel()
					.send(packetTarget(), new RemoveBlockEntityPacket(worldPosition));
			} else
				notifyUpdate();

			return InteractionResult.SUCCESS;
		}

		if (manuallyAddedItems.size() >= 4)
			return InteractionResult.SUCCESS;

		manuallyAddedItems.add(heldItem.copyWithCount(1));
		heldItem.shrink(1);
		if (heldItem.isEmpty())
			player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		notifyUpdate();
		return InteractionResult.SUCCESS;
	}

	public InteractionResult useShop(Player player) {
		if (level.isClientSide())
			return InteractionResult.SUCCESS;
		if (!owner.equals(player.getUUID()) || (!paymentItem.isEmpty() && !player.isShiftKeyDown()))
			return interactAsCustomer(player);

		return interactAsOwner(player);
	}

	public InteractionResult interactAsOwner(Player player) {
		if (player instanceof ServerPlayer sp)
			NetworkHooks.openScreen(sp, this, worldPosition);
		return InteractionResult.SUCCESS;
	}

	public InteractionResult interactAsCustomer(Player player) {
		ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack prevListItem = ItemStack.EMPTY;
		boolean addOntoList = false;

		// Remove other lists from inventory
		for (int i = 0; i < 9; i++) {
			ItemStack item = player.getInventory()
				.getItem(i);
			if (!AllItems.SHOPPING_LIST.isIn(item))
				continue;
			prevListItem = item;
			addOntoList = true;
			player.getInventory()
				.setItem(i, ItemStack.EMPTY);
		}

		// add onto existing list if in hand
		if (AllItems.SHOPPING_LIST.isIn(itemInHand)) {
			prevListItem = itemInHand;
			addOntoList = true;
		}

		if (!itemInHand.isEmpty() && !addOntoList) {
			CreateLang.translate("stock_keeper.shopping_list_empty_hand")
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition);
			return InteractionResult.SUCCESS;
		}

		if (paymentItem.isEmpty()) {
			CreateLang.translate("stock_keeper.no_price_set")
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition);
			return InteractionResult.SUCCESS;
		}

		UUID tickerID = null;
		BlockPos tickerPos = requestData.targetOffset.offset(worldPosition);
		if (level.getBlockEntity(tickerPos) instanceof StockTickerBlockEntity stbe && stbe.isKeeperPresent())
			tickerID = stbe.behaviour.freqId;

		int stockLevel = getStockLevelForTrade(ShoppingListItem.getList(prevListItem));

		if (tickerID == null) {
			CreateLang.translate("stock_keeper.keeper_missing")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition);
			return InteractionResult.SUCCESS;
		}

		if (stockLevel == 0) {
			CreateLang.translate("stock_keeper.out_of_stock")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition);

			if (!prevListItem.isEmpty()) {
				if (player.getItemInHand(InteractionHand.MAIN_HAND)
					.isEmpty())
					player.setItemInHand(InteractionHand.MAIN_HAND, prevListItem);
				else
					player.getInventory()
						.placeItemBackInInventory(prevListItem);
			}

			return InteractionResult.SUCCESS;
		}

		ShoppingList list = new ShoppingList(new ArrayList<>(), owner, tickerID);

		if (addOntoList) {
			ShoppingList prevList = ShoppingListItem.getList(prevListItem);
			if (owner.equals(prevList.shopOwner()) && tickerID.equals(prevList.shopNetwork()))
				list = prevList;
			else
				addOntoList = false;
		}

		if (list.getPurchases(worldPosition) == stockLevel) {
			CreateLang.translate("stock_keeper.limited_stock")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition);

		} else {
			list.addPurchases(worldPosition, 1);
			if (!addOntoList)
				CreateLang.translate("stock_keeper.use_list_to_add_purchases")
					.color(0xeeeeee)
					.sendStatus(player);
			if (!addOntoList)
				level.playSound(null, worldPosition, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1, 1.5f);
			AllSoundEvents.CONFIRM.playOnServer(level, worldPosition);
		}

		ItemStack newListItem =
			ShoppingListItem.saveList(AllItems.SHOPPING_LIST.asStack(), list, requestData.encodedTargetAdress);

		if (player.getItemInHand(InteractionHand.MAIN_HAND)
			.isEmpty())
			player.setItemInHand(InteractionHand.MAIN_HAND, newListItem);
		else
			player.getInventory()
				.placeItemBackInInventory(newListItem);

		return InteractionResult.SUCCESS;
	}

	public int getStockLevelForTrade(@Nullable ShoppingList otherPurchases) {
		BlockPos tickerPos = requestData.targetOffset.offset(worldPosition);
		if (!(level.getBlockEntity(tickerPos) instanceof StockTickerBlockEntity stbe))
			return 0;

		InventorySummary recentSummary = null;

		if (level.isClientSide()) {
			if (stbe.getTicksSinceLastUpdate() > 15)
				stbe.refreshClientStockSnapshot();
			recentSummary = stbe.getLastClientsideStockSnapshotAsSummary();
		} else
			recentSummary = stbe.getRecentSummary();

		if (recentSummary == null)
			return 0;

		InventorySummary modifierSummary = new InventorySummary();
		if (otherPurchases != null)
			modifierSummary = otherPurchases.bakeEntries(level, worldPosition)
				.getFirst();

		int smallestQuotient = Integer.MAX_VALUE;
		for (BigItemStack entry : requestData.encodedRequest.stacks())
			if (entry.count > 0)
				smallestQuotient = Math.min(smallestQuotient,
					(recentSummary.getCountOf(entry.stack) - modifierSummary.getCountOf(entry.stack)) / entry.count);

		return smallestQuotient;
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("Items", NBTHelper.writeItemList(manuallyAddedItems));
		tag.put("Payment", paymentItem.serializeNBT());
		tag.putInt("PaymentAmount", paymentAmount);
		requestData.write(tag);
		if (owner != null)
			tag.putUUID("OwnerUUID", owner);
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		manuallyAddedItems = NBTHelper.readItemList(tag.getList("Items", Tag.TAG_COMPOUND));
		requestData = AutoRequestData.read(tag);
		paymentItem = ItemStack.of(tag.getCompound("Payment"));
		paymentAmount = tag.getInt("PaymentAmount");
		owner = tag.contains("OwnerUUID") ? tag.getUUID("OwnerUUID") : null;
	}

	@Override
	public void destroy() {
		super.destroy();
		manuallyAddedItems.forEach(stack -> Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
			worldPosition.getZ(), stack));
		manuallyAddedItems.clear();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return DisplayClothPricingMenu.create(pContainerId, pPlayerInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Components.empty();
	}

}
