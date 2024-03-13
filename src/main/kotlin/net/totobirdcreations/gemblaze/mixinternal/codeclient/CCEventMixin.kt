package net.totobirdcreations.gemblaze.mixinternal.codeclient

import dev.dfonline.codeclient.location.*
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.gui.screen.ProgressScreen
import net.minecraft.client.gui.screen.ReconfiguringScreen
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.mixinternal.LightmapTextureManagerMixin
import net.totobirdcreations.gemblaze.render.DocsRenderer
import net.totobirdcreations.gemblaze.util.item.UtilityItems


internal object CCEventMixin {

    private var chatModeSet      : Boolean = false;
    private var hasBuild         : Boolean = false;
            var lagslayerEnabled : Boolean = false;


    fun updateLocation(location : Location?) {
        Main.isOnDF = location != null;
        if (location !is Dev) {
            UtilityItems.invalidateDev();
            DocsRenderer.clear();
        }
        if (location == null || location is Spawn) {
            this.chatModeSet      = false;
            this.hasBuild         = false;
            this.lagslayerEnabled = false;
        } else {
            if (location is Build) {
                this.hasBuild = true;
            }

            Thread { ->
                while (true) {
                    if (   Main.CLIENT.currentScreen !is ReconfiguringScreen
                        && Main.CLIENT.currentScreen !is ProgressScreen
                        && Main.CLIENT.currentScreen !is DownloadingTerrainScreen
                    ) {
                        break;
                    }
                    Thread.sleep(1);
                }

                if (location is Plot) {

                    // NightVision
                    LightmapTextureManagerMixin.overrideNightVision = null;

                    // ChatMode
                    if (! this.chatModeSet) {
                        val chatMode = Main.CONFIG.chatAutoSetMode.id;
                        if (chatMode != null) { Main.CLIENT.networkHandler?.sendCommand("c ${chatMode}"); }
                        this.chatModeSet = true;
                    }

                    if (location is Dev) {
                        // CpuOverlay
                        if (! this.lagslayerEnabled) {
                            if (Main.CONFIG.interfaceCpuOverlay) {
                                Main.CLIENT.networkHandler?.sendCommand("lagslayer");
                            }
                        }
                        // CompactCoder
                        if (Main.CONFIG.developmentCompactCoder) {
                            Thread.sleep(1000);
                            UtilityItems.glitchStick.getOrRequest();
                        }
                    }

                    UtilityItems.resetInventory(location);

                }

            }.start();

        }
    }

}