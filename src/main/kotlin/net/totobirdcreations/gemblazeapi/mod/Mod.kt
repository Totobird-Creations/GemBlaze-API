package net.totobirdcreations.gemblazeapi.mod

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.util.Identifier
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.Messages
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.mod.command.IgnoreMenuCommand
import net.totobirdcreations.gemblazeapi.mod.command.SearchCommand
import net.totobirdcreations.gemblazeapi.mod.config.Config
import net.totobirdcreations.gemblazeapi.mod.render.HUDRenderer
import net.totobirdcreations.gemblazeapi.mod.render.ItemRenderer
import net.totobirdcreations.gemblazeapi.mod.render.SearchRenderer
import java.io.BufferedReader
import java.io.InputStreamReader


internal object Mod {

    private val CONFIG_ID : Identifier = Identifier(Main.ID, "config");
    val CONFIG_HANDLER : ConfigClassHandler<Config> = ConfigClassHandler.createBuilder(Config::class.java)
        .id(CONFIG_ID)
        .serializer{config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("${Main.ID}.json5"))
            .setJson5(true)
            .build()
        }
        .build();
    val CONFIG : Config get() = CONFIG_HANDLER.instance();

    private var hasEnabledLagslayer : Boolean = false;

    private var stdin = BufferedReader(InputStreamReader(System.`in`));


    fun init() {

        CONFIG_HANDLER.load();

        State.ENTER_PLOT.register{_ -> run {
            val id = CONFIG.autoCommandChatMode.id;
            if (id != null) {
                Messages.sendCommand("chat ${id}");
            }
        }};

        State.EXIT_PLOT.register {_ -> run {
            hasEnabledLagslayer = false;
        }};

        State.ENTER_BUILD.register{_ -> run {
            if (CONFIG.autoCommandWorldeditWand) {
                Messages.sendCommand("/wand");
            }
        }};

        State.ENTER_DEV.register{_ -> run {
            if (CONFIG.autoCommandCompactInventory) {
                Messages.sendCommand("rc");
            }
            if (State.state?.plot?.permissions?.hasBuild == true && CONFIG.autoCommandWorldeditWand) {
                Messages.sendCommand("/wand");
            }
            if (CONFIG.autoCommandCpuOverlay) {
                if (! hasEnabledLagslayer) {
                    hasEnabledLagslayer = true;
                    Messages.sendCommand("lagslayer");
                }
            }
        }};

        Inventory.DEV_ITEMS.register{builder -> run {
            if (CONFIG.interfaceInventoryAutoGlitchStick) {
                builder.putWherePossible(Inventory.glitchStick.getOrRequest());
            }
            if (CONFIG.interfaceInventoryAutoCancelWand) {
                builder.putWherePossible(Inventory.cancelWand.getOrRequest());
            }
        }};


        HudRenderCallback.EVENT.register(HUDRenderer);
        ModelLoadingPlugin.register(ItemRenderer);


        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            SearchCommand.register(dispatcher);
            IgnoreMenuCommand.register(dispatcher);
        };
        ClientChunkEvents.CHUNK_LOAD   .register{ _, chunk -> SearchRenderer.getChunks { chunks -> chunks.add    (chunk.pos) } };
        ClientChunkEvents.CHUNK_UNLOAD .register{ _, chunk -> SearchRenderer.getChunks { chunks -> chunks.remove (chunk.pos) } };

        ClientTickEvents.START_CLIENT_TICK.register{ _ -> try {
            if (this.stdin.ready()) {
                Main.CLIENT.networkHandler?.sendCommand(this.stdin.readLine());
            }
        } catch (_ : Exception) {}};

    }

}