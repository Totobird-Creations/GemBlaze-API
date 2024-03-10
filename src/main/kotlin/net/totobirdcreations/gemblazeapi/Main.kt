package net.totobirdcreations.gemblazeapi

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.totobirdcreations.gemblazeapi.api.DiamondFirePlotAreaSize
import net.totobirdcreations.gemblazeapi.api.Packets
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.detect.Connection
import net.totobirdcreations.gemblazeapi.detect.InboundPackets
import net.totobirdcreations.gemblazeapi.mod.Mod
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max


internal object Main : ClientModInitializer {
	const val ID     : String          = "gemblazeapi";
    val 	  LOGGER : Logger          = LoggerFactory.getLogger(ID);
	val 	  CLIENT : MinecraftClient = MinecraftClient.getInstance();

	override fun onInitializeClient() {

		// Yes, the internals of this library use its own API.
		// To the people who don't like that, deal with it.

		State.ENTER_PLAY.register{_ -> run {
			State.state?.plot?.permissions?.hasPlay = true;
		}};

		State.ENTER_BUILD.register{_ -> run {
			State.state?.plot?.permissions?.hasBuild = true;
		}};

		State.ENTER_DEV.register{_ -> run {
			State.state?.plot?.permissions?.hasDev = true;

			Inventory.isCompactInventory = false;

			Thread{-> run {
				Thread.sleep(500);
				val origin = InboundPackets.ModeSwitch.origin;
				if (origin != null) {
					val devCorner = BlockPos.ofFloored(Vec3d(origin.first, 0.0, origin.second)).add(-10, 0, -10);
					State.state?.plot?.area?.buildCorner = BlockPos.ofFloored(Vec3d(origin.first, 0.0, origin.second)).add(10, 0, -10);
					State.state?.plot?.area?.devCorner   = devCorner;
					State.state?.plot?.area?.size        = null;
					var devEdge : BlockPos = devCorner.mutableCopy().setY(49);
					while (CLIENT.world?.getBlockState(devEdge)?.isOf(Blocks.STONE) == true) {
						devEdge = devEdge.add(0, 0, 1);
					}
					val expectedLength = max(devEdge.z - devCorner.z, 0);
					try {
						val size = DiamondFirePlotAreaSize.entries
							.sortedBy { size -> size.size }
							.first { length -> expectedLength <= length.size };
						State.state?.plot?.area?.size = size;
					} catch (_ : NoSuchElementException) {};
				}
			}}.start();

			Thread{-> run {
				val builder = InventoryBuilder(false);
				Inventory.DEV_ITEMS.trigger(builder);
				builder.push();
			}};

			if (CLIENT.player!!.inventory.isEmpty) {
				Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 500);
				Thread.sleep(10);
			};
			val inventory = CLIENT.player!!.inventory.main;
			for (stack in inventory) {
				InstructionInfo.tryPut(stack);
				Inventory.tryPut(stack);
			};

		}};
		State.EXIT_DEV.register{_ -> run {
			InstructionInfo.clear();
			Inventory.clear();
		}};

		// Set up debug.
		if (FabricLoader.getInstance().isDevelopmentEnvironment) {

			State.ENTER_DF    .register{  -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_DF    - ${State.state}");}}};
			State.EXIT_DF     .register{  -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_DF     - ${State.state}");}}};

			State.ENTER_NODE  .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_NODE  - ${State.state}");}}};
			State.EXIT_NODE   .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_NODE   - ${State.state}");}}};

			State.ENTER_PLOT  .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_PLOT  - ${State.state}");}}};
			State.EXIT_PLOT   .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_PLOT   - ${State.state}");}}};

			State.ENTER_PLAY  .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_PLAY  - ${State.state}");}}};
			State.EXIT_PLAY   .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_PLAY   - ${State.state}");}}};
			State.ENTER_BUILD .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_BUILD - ${State.state}");}}};
			State.EXIT_BUILD  .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_BUILD  - ${State.state}");}}};
			State.ENTER_DEV   .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE ENTER_DEV   - ${State.state}");}}};
			State.EXIT_DEV    .register{_ -> run {if (State.DEBUG_IN_DEVENV) {LOGGER.error("STATE EXIT_DEV    - ${State.state}");}}};

		}


		Connection.init();

		Mod.init();

	}
}