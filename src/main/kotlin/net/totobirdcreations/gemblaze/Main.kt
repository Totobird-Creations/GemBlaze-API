package net.totobirdcreations.gemblaze

import dev.dfonline.codeclient.CodeClient
import dev.dfonline.codeclient.location.Location
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.totobirdcreations.gemblaze.command.InstructionTypeArgument
import net.totobirdcreations.gemblaze.command.SearchCommand
import net.totobirdcreations.gemblaze.config.Config
import net.totobirdcreations.gemblaze.mixinternal.codeclient.CCEventMixin
import net.totobirdcreations.gemblaze.render.HudRenderer
import net.totobirdcreations.gemblaze.render.ItemRenderer
import net.totobirdcreations.gemblaze.render.SearchRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory


internal object Main : ClientModInitializer {
	const val ID     : String          = "gemblaze";
    val 	  LOGGER : Logger          = LoggerFactory.getLogger(ID);
	val 	  CLIENT : MinecraftClient = MinecraftClient.getInstance();

	private val CONFIG_ID : Identifier = Identifier(Main.ID, "config");
	val CONFIG_HANDLER : ConfigClassHandler<Config> = ConfigClassHandler.createBuilder(Config::class.java)
		.id(CONFIG_ID)
		.serializer{config -> GsonConfigSerializerBuilder.create(config)
			.setPath(YACLPlatform.getConfigDir().resolve("${Main.ID}.json5"))
			.setJson5(true)
			.build()
		}
		.build();
	@JvmStatic
	val CONFIG : Config get() = CONFIG_HANDLER.instance();


	internal var isOnDF : Boolean = false;
	@JvmStatic
	val location : Location? get() = if (this.isOnDF) { CodeClient.location } else { null };


	override fun onInitializeClient() {

		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> CCEventMixin.updateLocation(null); };

		HudRenderCallback.EVENT.register(HudRenderer);
		ModelLoadingPlugin.register(ItemRenderer);

		// CodeSearch
		InstructionTypeArgument.register();
		ClientCommandRegistrationCallback.EVENT.register { d, _ -> SearchCommand.register(d); };
		ClientChunkEvents.CHUNK_LOAD   .register { _, c -> SearchRenderer.getChunks { chunks -> chunks.add    (c.pos); }; };
		ClientChunkEvents.CHUNK_UNLOAD .register { _, c -> SearchRenderer.getChunks { chunks -> chunks.remove (c.pos); }; };

		CONFIG_HANDLER.load();

	}

}