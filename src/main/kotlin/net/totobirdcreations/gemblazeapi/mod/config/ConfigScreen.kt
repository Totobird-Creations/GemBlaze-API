package net.totobirdcreations.gemblazeapi.mod.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen
import net.totobirdcreations.gemblazeapi.mod.Mod


@Suppress("unused")
internal object ConfigScreen : ModMenuApi {

    override fun getModConfigScreenFactory() : ConfigScreenFactory<*>? {
        return ConfigScreenFactory<Screen>{ parent -> createConfigScreen(parent) }
    }

    private fun createConfigScreen(parent: Screen?): Screen? {
        return Mod.CONFIG_HANDLER.generateGui().generateScreen(parent);
    }

}