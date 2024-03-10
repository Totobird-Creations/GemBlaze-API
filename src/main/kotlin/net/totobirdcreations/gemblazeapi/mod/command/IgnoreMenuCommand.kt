package net.totobirdcreations.gemblazeapi.mod.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.text.Text
import net.totobirdcreations.gemblazeapi.Main


internal object IgnoreMenuCommand {

    var ignoreMenu : Boolean = false
            private set;

    fun register(dispatcher : CommandDispatcher<S>) {
        dispatcher.register(
            LiteralArgumentBuilder.literal<S>("ignoremenu")
            .executes { ctx ->
                this.ignoreMenu = ! this.ignoreMenu;
                ctx.source.sendFeedback(Text.translatable("command.${Main.ID}.ignoremenu.${this.ignoreMenu}"));
                1
            }
        );
    }

}