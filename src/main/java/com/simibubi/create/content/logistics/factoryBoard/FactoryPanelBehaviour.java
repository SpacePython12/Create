package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;

import org.joml.Math;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.utility.NBTHelper;
import net.createmod.catnip.utility.animation.LerpedFloat;
import net.createmod.catnip.utility.animation.LerpedFloat.Chaser;
import net.createmod.catnip.utility.lang.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class FactoryPanelBehaviour extends FilteringBehaviour {

	public static final BehaviourType<FactoryPanelBehaviour> TOP_LEFT = new BehaviourType<>();
	public static final BehaviourType<FactoryPanelBehaviour> TOP_RIGHT = new BehaviourType<>();
	public static final BehaviourType<FactoryPanelBehaviour> BOTTOM_LEFT = new BehaviourType<>();
	public static final BehaviourType<FactoryPanelBehaviour> BOTTOM_RIGHT = new BehaviourType<>();
	public static final int REQUEST_INTERVAL = 100;

	public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
	public Set<FactoryPanelPosition> targeting;
	public List<ItemStack> activeCraftingArrangement;

	public boolean satisfied;
	public boolean promisedSatisfied;
	public boolean waitingForNetwork;
	public String recipeAddress;
	public int recipeOutput;
	public LerpedFloat bulb;
	public PanelSlot slot;
	public int promiseClearingInterval;
	public boolean forceClearPromises;
	public UUID network;

	public RequestPromiseQueue restockerPromises;
	private boolean promisePrimedForMarkDirty;

	private boolean active;
	private int lastReportedUnloadedLinks;
	private int lastReportedLevelInStorage;
	private int lastReportedPromises;
	private int timer;

	public FactoryPanelBehaviour(FactoryPanelBlockEntity be, PanelSlot slot) {
		super(be, new FactoryPanelSlotPositioning(slot));
		this.slot = slot;
		this.targetedBy = new HashMap<>();
		this.targeting = new HashSet<>();
		this.count = 0;
		this.satisfied = false;
		this.promisedSatisfied = false;
		this.waitingForNetwork = false;
		this.activeCraftingArrangement = List.of();
		this.recipeAddress = "";
		this.recipeOutput = 1;
		this.active = false;
		this.forceClearPromises = false;
		this.promiseClearingInterval = -1;
		this.bulb = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.45, Chaser.EXP);
		this.restockerPromises = new RequestPromiseQueue(be::setChanged);
		this.promisePrimedForMarkDirty = true;
		this.network = UUID.randomUUID();
	}

	public void setNetwork(UUID network) {
		this.network = network;
	}

	@Nullable
	public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelPosition pos) {
		if (world instanceof Level l && !l.isLoaded(pos.pos()))
			return null;
		if (!(world.getBlockEntity(pos.pos()) instanceof FactoryPanelBlockEntity fpbe))
			return null;
		FactoryPanelBehaviour behaviour = fpbe.panels.get(pos.slot());
		if (!behaviour.active)
			return null;
		return behaviour;
	}

	@Override
	public void tick() {
		super.tick();
		if (getWorld().isClientSide()) {
			bulb.updateChaseTarget(satisfied ? 1 : 0);
			bulb.tickChaser();
			return;
		}

		if (!promisePrimedForMarkDirty) {
			restockerPromises.setOnChanged(blockEntity::setChanged);
			promisePrimedForMarkDirty = true;
		}

		tickStorageMonitor();
		tickRequests();
	}

	private void tickStorageMonitor() {
		ItemStack filter = getFilter();
		int inStorage = getLevelInStorage();
		int promised = getPromised();
		int demand = getAmount() * (upTo ? 1 : filter.getMaxStackSize());
		int unloadedLinkCount = getUnloadedLinks();
		boolean shouldSatisfy = filter.isEmpty() || inStorage >= demand;
		boolean shouldPromiseSatisfy = filter.isEmpty() || inStorage + promised >= demand;
		boolean shouldWait = unloadedLinkCount > 0;

		if (lastReportedLevelInStorage == inStorage && lastReportedPromises == promised
			&& lastReportedUnloadedLinks == unloadedLinkCount && satisfied == shouldSatisfy
			&& promisedSatisfied == shouldPromiseSatisfy && waitingForNetwork == shouldWait)
			return;

		lastReportedLevelInStorage = inStorage;
		satisfied = shouldSatisfy;
		lastReportedPromises = promised;
		promisedSatisfied = shouldPromiseSatisfy;
		lastReportedUnloadedLinks = unloadedLinkCount;
		waitingForNetwork = shouldWait;
		blockEntity.sendData();
	}

	private void tickRequests() {
		FactoryPanelBlockEntity panelBE = panelBE();
		if (targetedBy.isEmpty() && !panelBE.restocker)
			return;
		if (satisfied || promisedSatisfied || waitingForNetwork)
			return;
		if (timer > 0) {
			timer = Math.min(timer, REQUEST_INTERVAL);
			timer--;
			return;
		}

		timer = REQUEST_INTERVAL;

		if (panelBE.restocker) {
			tryRestock();
			return;
		}

		if (recipeAddress.isBlank())
			return;

		boolean failed = false;

		Multimap<UUID, BigItemStack> toRequest = HashMultimap.create();
		List<BigItemStack> toRequestAsList = new ArrayList<>();

		for (FactoryPanelConnection connection : targetedBy.values()) {
			FactoryPanelBehaviour source = at(getWorld(), connection.from);
			if (source == null)
				return;

			ItemStack item = source.getFilter();
			int amount = connection.amount;
			InventorySummary summary = LogisticsManager.getSummaryOfNetwork(source.network, true);
			if (amount == 0 || item.isEmpty() || summary.getCountOf(item) < amount) {
				sendEffect(connection.from, false);
				failed = true;
				continue;
			}

			BigItemStack stack = new BigItemStack(item, amount);
			toRequest.put(source.network, stack);
			toRequestAsList.add(stack);
			sendEffect(connection.from, true);
		}

		if (failed)
			return;

		// Input items may come from differing networks
		Map<UUID, Collection<BigItemStack>> asMap = toRequest.asMap();
		PackageOrder requestContext = new PackageOrder(toRequestAsList);
		List<Multimap<PackagerBlockEntity, PackagingRequest>> requests = new ArrayList<>();

		// Panel may enforce item arrangement
		if (!activeCraftingArrangement.isEmpty())
			requestContext = new PackageOrder(activeCraftingArrangement.stream()
				.map(BigItemStack::new)
				.toList());

		// Collect request distributions
		for (Entry<UUID, Collection<BigItemStack>> entry : asMap.entrySet()) {
			PackageOrder order = new PackageOrder(new ArrayList<>(entry.getValue()));
			Multimap<PackagerBlockEntity, PackagingRequest> request =
				LogisticsManager.findPackagersForRequest(entry.getKey(), order, requestContext, null, recipeAddress);
			requests.add(request);
		}

		// Check if any packager is busy - cancel all
		for (Multimap<PackagerBlockEntity, PackagingRequest> entry : requests)
			for (PackagerBlockEntity packager : entry.keySet())
				if (packager.isTooBusyFor(RequestType.RESTOCK))
					return;

		// Send it
		for (Multimap<PackagerBlockEntity, PackagingRequest> entry : requests)
			LogisticsManager.performPackageRequests(entry);

		// Keep the output promise
		RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(network);
		if (promises != null)
			promises.add(new RequestPromise(new BigItemStack(getFilter(), recipeOutput)));
	}

	private void tryRestock() {
		ItemStack item = getFilter();
		if (item.isEmpty())
			return;

		FactoryPanelBlockEntity panelBE = panelBE();
		PackagerBlockEntity packager = panelBE.getRestockedPackager();
		if (packager == null || !packager.targetInventory.hasInventory())
			return;

		int availableOnNetwork = LogisticsManager.getStockOf(network, item, packager.targetInventory.getInventory());
		if (availableOnNetwork == 0) {
			sendEffect(getPanelPosition(), false);
			return;
		}

		int inStorage = getLevelInStorage();
		int promised = getPromised();
		int maxStackSize = item.getMaxStackSize();
		int demand = getAmount() * (upTo ? 1 : maxStackSize);
		int amountToOrder = Math.clamp(demand - promised - inStorage, 0, maxStackSize * 9);

		BigItemStack orderedItem = new BigItemStack(item, Math.min(amountToOrder, availableOnNetwork));
		PackageOrder order = new PackageOrder(List.of(orderedItem));

		sendEffect(getPanelPosition(), true);

		if (!LogisticsManager.broadcastPackageRequest(network, RequestType.RESTOCK, order,
			packager.targetInventory.getInventory(), recipeAddress))
			return;

		restockerPromises.add(new RequestPromise(orderedItem));
	}

	private void sendEffect(FactoryPanelPosition fromPos, boolean success) {
		AllPackets.sendToNear(getWorld(), getPos(), 64,
			new FactoryPanelEffectPacket(fromPos, getPanelPosition(), success));
	}

	public void addConnection(FactoryPanelPosition fromPos) {
		if (targetedBy.size() >= 9)
			return;
		FactoryPanelBehaviour source = at(getWorld(), fromPos);
		if (source == null)
			return;
		source.targeting.add(getPanelPosition());
		targetedBy.put(fromPos, new FactoryPanelConnection(fromPos, 1));
		blockEntity.notifyUpdate();
	}

	public FactoryPanelPosition getPanelPosition() {
		return new FactoryPanelPosition(getPos(), slot);
	}

	public FactoryPanelBlockEntity panelBE() {
		return (FactoryPanelBlockEntity) blockEntity;
	}

	@Override
	public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
		if (!Create.LOGISTICS.mayInteract(network, player)) {
			player.displayClientMessage(CreateLang.translate("logistically_linked.protected")
				.style(ChatFormatting.RED)
				.component(), true);
			return;
		}

		if (AllItemTags.WRENCH.matches(player.getItemInHand(hand))) {
			int sharedMode = -1;
			for (FactoryPanelPosition target : targeting) {
				FactoryPanelBehaviour at = at(getWorld(), target);
				if (at == null)
					continue;
				FactoryPanelConnection connection = at.targetedBy.get(getPanelPosition());
				if (connection == null)
					continue;
				if (sharedMode == -1)
					sharedMode = (connection.arrowBendMode + 1) % 4;
				connection.arrowBendMode = sharedMode;
				if (!player.level().isClientSide)
					at.blockEntity.notifyUpdate();
			}
			if (sharedMode == -1)
				return;

			char[] boxes = "\u25a1\u25a1\u25a1\u25a1".toCharArray();
			boxes[sharedMode] = '\u25a0';
			player.displayClientMessage(CreateLang.translate("factory_panel.cycled_arrow_path", new String(boxes))
				.component(), true);

			return;
		}

		if (player.level().isClientSide)
			if (FactoryPanelConnectionHandler.panelClicked(getWorld(), player, this))
				return;

		if (getFilter().isEmpty()) {
			if (AllBlocks.FACTORY_GAUGE.isIn(player.getItemInHand(hand)))
				return;

			super.onShortInteract(player, hand, side, hitResult);
			return;
		}

		if (player.level().isClientSide)
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> displayScreen(player));
	}

	public void enable() {
		active = true;
		blockEntity.notifyUpdate();
	}

	public void disable() {
		destroy();
		active = false;
		targetedBy = new HashMap<>();
		targeting = new HashSet<>();
		count = 0;
		satisfied = false;
		promisedSatisfied = false;
		recipeAddress = "";
		recipeOutput = 1;
		setFilter(ItemStack.EMPTY);
		blockEntity.notifyUpdate();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void destroy() {
		FactoryPanelPosition panelPosition = getPanelPosition();
		for (FactoryPanelPosition position : targetedBy.keySet()) {
			FactoryPanelBehaviour source = at(getWorld(), position);
			if (source != null) {
				source.targeting.remove(panelPosition);
				source.blockEntity.sendData();
			}
		}
		for (FactoryPanelPosition position : targeting) {
			FactoryPanelBehaviour target = at(getWorld(), position);
			if (target != null) {
				target.targetedBy.remove(panelPosition);
				target.blockEntity.sendData();
			}
		}

		super.destroy();
	}

	public int getUnloadedLinks() {
		if (getWorld().isClientSide())
			return lastReportedUnloadedLinks;
		if (panelBE().restocker)
			return panelBE().getRestockedPackager() == null ? 1 : 0;
		return Create.LOGISTICS.getUnloadedLinkCount(network);
	}

	public int getLevelInStorage() {
		if (getWorld().isClientSide())
			return lastReportedLevelInStorage;
		if (getFilter().isEmpty())
			return 0;

		InventorySummary summary = getRelevantSummary();
		return summary.getCountOf(getFilter());
	}

	private InventorySummary getRelevantSummary() {
		FactoryPanelBlockEntity panelBE = panelBE();
		if (!panelBE.restocker)
			return LogisticsManager.getSummaryOfNetwork(network, false);
		PackagerBlockEntity packager = panelBE.getRestockedPackager();
		if (packager == null)
			return InventorySummary.EMPTY;
		return packager.getAvailableItems();
	}

	public int getPromised() {
		if (getWorld().isClientSide())
			return lastReportedPromises;
		ItemStack item = getFilter();
		if (item.isEmpty())
			return 0;

		if (panelBE().restocker) {
			if (forceClearPromises) {
				restockerPromises.forceClear(item);
				timer = 0;
			}
			forceClearPromises = false;
			return restockerPromises.getTotalPromisedAndRemoveExpired(item, getPromiseExpiryTimeInTicks());
		}

		RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(network);
		if (forceClearPromises) {
			promises.forceClear(item);
			timer = 0;
		}
		forceClearPromises = false;

		return promises == null ? 0 : promises.getTotalPromisedAndRemoveExpired(item, getPromiseExpiryTimeInTicks());
	}

	private int getPromiseExpiryTimeInTicks() {
		if (promiseClearingInterval == -1)
			return -1;
		if (promiseClearingInterval == 0)
			return 20 * 30;

		return promiseClearingInterval * 20 * 60;
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		if (!active)
			return;

		CompoundTag panelTag = new CompoundTag();
		super.write(panelTag, clientPacket);

		panelTag.putInt("LastLevel", lastReportedLevelInStorage);
		panelTag.putInt("LastPromised", lastReportedPromises);
		panelTag.putInt("LastUnloadedLinks", lastReportedUnloadedLinks);
		panelTag.putBoolean("Satisfied", satisfied);
		panelTag.putBoolean("PromisedSatisfied", promisedSatisfied);
		panelTag.putBoolean("Waiting", waitingForNetwork);
		panelTag.put("Targeting", NBTHelper.writeCompoundList(targeting, FactoryPanelPosition::write));
		panelTag.put("TargetedBy", NBTHelper.writeCompoundList(targetedBy.values(), FactoryPanelConnection::write));
		panelTag.putString("RecipeAddress", recipeAddress);
		panelTag.putInt("RecipeOutput", recipeOutput);
		panelTag.putInt("PromiseClearingInterval", promiseClearingInterval);
		panelTag.putUUID("Freq", network);
		panelTag.put("Craft", NBTHelper.writeItemList(activeCraftingArrangement));

		if (panelBE().restocker && !clientPacket)
			panelTag.put("Promises", restockerPromises.write());

		nbt.put(CreateLang.asId(slot.name()), panelTag);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		CompoundTag panelTag = nbt.getCompound(CreateLang.asId(slot.name()));
		if (panelTag.isEmpty()) {
			active = false;
			return;
		}

		active = true;
		filter = FilterItemStack.of(panelTag.getCompound("Filter"));
		count = panelTag.getInt("FilterAmount");
		upTo = panelTag.getBoolean("UpTo");
		lastReportedLevelInStorage = panelTag.getInt("LastLevel");
		lastReportedPromises = panelTag.getInt("LastPromised");
		lastReportedUnloadedLinks = panelTag.getInt("LastUnloadedLinks");
		satisfied = panelTag.getBoolean("Satisfied");
		promisedSatisfied = panelTag.getBoolean("PromisedSatisfied");
		waitingForNetwork = panelTag.getBoolean("Waiting");
		promiseClearingInterval = panelTag.getInt("PromiseClearingInterval");
		if (panelTag.hasUUID("Freq"))
			network = panelTag.getUUID("Freq");

		targeting.clear();
		NBTHelper.iterateCompoundList(panelTag.getList("Targeting", Tag.TAG_COMPOUND),
			c -> targeting.add(FactoryPanelPosition.read(c)));

		targetedBy.clear();
		NBTHelper.iterateCompoundList(panelTag.getList("TargetedBy", Tag.TAG_COMPOUND),
			c -> targetedBy.put(FactoryPanelPosition.read(c), FactoryPanelConnection.read(c)));

		activeCraftingArrangement = NBTHelper.readItemList(panelTag.getList("Craft", Tag.TAG_COMPOUND));
		recipeAddress = panelTag.getString("RecipeAddress");
		recipeOutput = panelTag.getInt("RecipeOutput");

		if (nbt.getBoolean("Restocker") && !clientPacket) {
			restockerPromises = RequestPromiseQueue.read(panelTag.getCompound("Promises"), () -> {
			});
			promisePrimedForMarkDirty = false;
		}
	}

	@Override
	public float getRenderDistance() {
		return 64;
	}

	@Override
	public MutableComponent formatValue(ValueSettings value) {
		return value.value() == 0 ? Components.literal("*")
			: Components.literal(Math.max(0, value.value()) + ((value.row() == 0) ? "" : "\u25A4"));
	}

	@Override
	public boolean setFilter(ItemStack stack) {
		ItemStack filter = stack.copy();
		if (stack.getItem() instanceof FilterItem)
			return false;
		this.filter = FilterItemStack.of(filter);
		blockEntity.setChanged();
		blockEntity.sendData();
		return true;
	}

	@Override
	public void setValueSettings(Player player, ValueSettings settings, boolean ctrlDown) {
		if (getValueSettings().equals(settings))
			return;
		count = Math.max(0, settings.value());
		upTo = settings.row() == 0;
		panelBE().redraw = true;
		blockEntity.setChanged();
		blockEntity.sendData();
		playFeedbackSound(this);
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		int maxAmount = 100;
		return new ValueSettingsBoard(CreateLang.translate("factory_panel.target_amount")
			.component(), maxAmount, 10,
			List.of(CreateLang.translate("schedule.condition.threshold.items")
				.component(),
				CreateLang.translate("schedule.condition.threshold.stacks")
					.component()),
			new ValueSettingsFormatter(this::formatValue));
	}

	@Override
	public MutableComponent getLabel() {
		String key = "";

		if (getFilter().isEmpty())
			key = "factory_panel.new_factory_task";
		else if (waitingForNetwork)
			key = "factory_panel.some_links_unloaded";
		else if (getAmount() == 0 || targetedBy.isEmpty())
			return getFilter().getHoverName()
				.plainCopy();
		else {
			String stacks = upTo ? "" : "\u25A4";
			key = getFilter().getHoverName()
				.getString() + " -> " + getAmount() + stacks;
			if (!satisfied)
				key += " " + CreateLang.translate("factory_panel.in_progress")
					.string();
			return CreateLang.text(key)
				.component();
		}

		return CreateLang.translate(key)
			.component();
	}

	@Override
	public ValueSettings getValueSettings() {
		return new ValueSettings(upTo ? 0 : 1, count);
	}

	@Override
	public MutableComponent getTip() {
		return CreateLang
			.translateDirect(filter.isEmpty() ? "logistics.filter.click_to_set" : "factory_panel.click_to_configure");
	}

	@Override
	public MutableComponent getCountLabelForValueBox() {
		if (filter.isEmpty())
			return Components.empty();
		if (waitingForNetwork)
			return Components.literal("?");

		int inStorage = getLevelInStorage() / (upTo ? 1 : getFilter().getMaxStackSize());
		int promised = getPromised();
		String stacks = upTo ? "" : "\u25A4";

		if (count == 0) {
			return CreateLang.text(inStorage + stacks)
				.color(0xF1EFE8)
				.component();
		}

		return CreateLang.text("   " + inStorage + stacks)
			.color(satisfied ? 0xD7FFA8 : promisedSatisfied ? 0xffcd75 : 0xFFBFA8)
			.add(CreateLang.text(promised == 0 ? "" : "\u23F6"))
			.add(CreateLang.text("/")
				.style(ChatFormatting.WHITE))
			.add(CreateLang.text(count + stacks + "  ")
				.color(0xF1EFE8))
			.component();
	}

	@Override
	public int netId() {
		return 2 + slot.ordinal();
	}

	@Override
	public boolean isCountVisible() {
		return !getFilter().isEmpty();
	}

	@Override
	public BehaviourType<?> getType() {
		return getTypeForSlot(slot);
	}

	public static BehaviourType<?> getTypeForSlot(PanelSlot slot) {
		return switch (slot) {
		case BOTTOM_LEFT -> BOTTOM_LEFT;
		case TOP_LEFT -> TOP_LEFT;
		case TOP_RIGHT -> TOP_RIGHT;
		case BOTTOM_RIGHT -> BOTTOM_RIGHT;
		};
	}

	@Environment(value = EnvType.CLIENT)
	public void displayScreen(Player player) {
		if (player instanceof LocalPlayer)
			ScreenOpener.open(new FactoryPanelScreen(this));
	}

}
