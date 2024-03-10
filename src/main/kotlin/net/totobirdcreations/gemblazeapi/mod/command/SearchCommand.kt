package net.totobirdcreations.gemblazeapi.mod.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.totobirdcreations.gemblazeapi.Main
import net.totobirdcreations.gemblazeapi.mod.render.SearchRenderer

internal typealias S = FabricClientCommandSource;


internal object SearchCommand {

    fun register(dispatcher : CommandDispatcher<S>) {
        dispatcher.register(literal<S>("search")
            .then(argument<S, String>("term", StringArgumentType.string())
                .executes{ ctx ->
                    SearchRenderer.term = StringArgumentType.getString(ctx, "term");
                    ctx.source.sendFeedback(Text.translatable("command.${Main.ID}.search.set"));
                    1
                }
            )
            .executes { ctx ->
                SearchRenderer.term = null;
                ctx.source.sendFeedback(Text.translatable("command.${Main.ID}.search.reset"));
                1
            }
        );
    }

}