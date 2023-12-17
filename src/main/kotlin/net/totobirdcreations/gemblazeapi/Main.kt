package net.totobirdcreations.gemblazeapi

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.totobirdcreations.gemblazeapi.api.Packets
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.InstructionInfo
import net.totobirdcreations.gemblazeapi.api.hypercube.Utilities
import net.totobirdcreations.gemblazeapi.detect.Connection
import net.totobirdcreations.gemblazeapi.util.InventoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


internal object Main : ClientModInitializer {
	const val ID     : String          = "gemblazeapi";
    val 	  LOGGER : Logger          = LoggerFactory.getLogger(ID);
	val 	  CLIENT : MinecraftClient = MinecraftClient.getInstance();

	override fun onInitializeClient() {

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

		State.ENTER_DEV.register{_ -> run {
			Utilities.isCompactInventory = false;
			if (CLIENT.player!!.inventory.isEmpty) {
				Packets.waitForPacket(ScreenHandlerSlotUpdateS2CPacket::class.java, 250);
				Thread.sleep(10);
			};
			val inventory = CLIENT.player!!.inventory.main;
			for (stack in inventory) {
				InstructionInfo.tryPut(stack);
				Utilities.tryPut(stack);
			};
			val builder = InventoryBuilder(false);
			Utilities.DEV_ITEMS.trigger(builder);
			builder.push();
		}};
		State.EXIT_DEV.register{_ -> run {
			InstructionInfo.clear();
			Utilities.clear();
		}};

		Connection.init();

	}
}