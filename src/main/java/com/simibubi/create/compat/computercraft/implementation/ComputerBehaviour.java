package com.simibubi.create.compat.computercraft.implementation;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageOrderLuaObject;
import com.simibubi.create.compat.computercraft.implementation.peripherals.DisplayLinkPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.FrogportPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.PostboxPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.RepackagerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SequencedGearshiftPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedControllerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedGaugePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StationPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StressGaugePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StockTickerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.PackagerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.TableClothShopPeripheral;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.compat.computercraft.implementation.peripherals.RedstoneRequesterPeripheral;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;

public class ComputerBehaviour extends AbstractComputerBehaviour {

	@Nullable
	public static IPeripheral peripheralProvider(Level level, BlockPos blockPos) {
		AbstractComputerBehaviour behavior = BlockEntityBehaviour.get(level, blockPos, AbstractComputerBehaviour.TYPE);
		if (behavior instanceof ComputerBehaviour real)
			return real.getPeripheral();
		return null;
	}

	IPeripheral peripheral;

	public ComputerBehaviour(SmartBlockEntity te) {
		super(te);
		this.peripheral = getPeripheralFor(te);
	}

	public static IPeripheral getPeripheralFor(SmartBlockEntity be) {
		if (be instanceof SpeedControllerBlockEntity scbe)
			return new SpeedControllerPeripheral(scbe, scbe.targetSpeed);
		if (be instanceof DisplayLinkBlockEntity dlbe)
			return new DisplayLinkPeripheral(dlbe);
		if (be instanceof FrogportBlockEntity fpbe)
			return new FrogportPeripheral(fpbe);
		if (be instanceof PostboxBlockEntity pbbe)
			return new PostboxPeripheral(pbbe);
		if (be instanceof SequencedGearshiftBlockEntity sgbe)
			return new SequencedGearshiftPeripheral(sgbe);
		if (be instanceof SpeedGaugeBlockEntity sgbe)
			return new SpeedGaugePeripheral(sgbe);
		if (be instanceof StressGaugeBlockEntity sgbe)
			return new StressGaugePeripheral(sgbe);
		if (be instanceof StockTickerBlockEntity sgbe)
			return new StockTickerPeripheral(sgbe);
		// Has to be before PackagerBlockEntity as it's a subclass
		if (be instanceof RepackagerBlockEntity rpbe)
			return new RepackagerPeripheral(rpbe);
		if (be instanceof PackagerBlockEntity pgbe)
			return new PackagerPeripheral(pgbe);
		if (be instanceof RedstoneRequesterBlockEntity rrbe)
			return new RedstoneRequesterPeripheral(rrbe);
		if (be instanceof StationBlockEntity sbe)
			return new StationPeripheral(sbe);
		if (be instanceof TableClothBlockEntity tcbe)
			return new TableClothShopPeripheral(tcbe);

		throw new IllegalArgumentException(
			"No peripheral available for " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()));
	}

	public static void registerItemDetailProviders() {
		VanillaDetailRegistries.ITEM_STACK.addProvider((out, stack) -> {
			if (PackageItem.isPackage(stack))
			{
				PackageLuaObject packageLuaObject = new PackageLuaObject(null, stack);
				out.put("package", packageLuaObject);
			}
		});
	}

	@Override
	public <T> T getPeripheral() {
		//noinspection unchecked
		return (T) peripheral;
	}
}
