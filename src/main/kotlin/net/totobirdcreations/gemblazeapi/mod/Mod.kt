package net.totobirdcreations.gemblazeapi.mod

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.api.Messages
import net.totobirdcreations.gemblazeapi.api.State
import net.totobirdcreations.gemblazeapi.api.hypercube.Inventory
import net.totobirdcreations.gemblazeapi.mod.config.Config
import net.totobirdcreations.gemblazeapi.mod.render.HUDRenderer
import net.totobirdcreations.gemblazeapi.mod.render.ItemRenderer


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


    fun init() {

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

    }

}