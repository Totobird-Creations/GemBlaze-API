package net.totobirdcreations.gemblaze.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.render.SearchRenderer
import net.totobirdcreations.gemblaze.util.item.InstructionBlock


internal typealias S = FabricClientCommandSource;


internal object SearchCommand {

    fun register(d : CommandDispatcher<S>) {
        val term = argument<S, String>("term", StringArgumentType.greedyString());

        d.register(literal<S>("search")
            .executes(this::clear)

            .then(argument<S, _>("type", InstructionTypeArgument)
                .executes { ctx -> this.search(ctx,
                    InstructionTypeArgument.get(ctx, "type"),
                    null
                ) }

                .then(term.executes { ctx -> this.search(ctx,
                    InstructionTypeArgument.get(ctx, "type"),
                    StringArgumentType.getString(ctx, "term")
                ) } )

            )

            .then(literal<S>("*").then(term.executes { ctx -> this.search(ctx,
                null,
                StringArgumentType.getString(ctx, "term")
            ) } ))

        );
    }

    private fun clear(ctx : CommandContext<S>) : Int {
        ctx.source.sendFeedback(Text.translatable("command.${Main.ID}.search.success.clear"));
        SearchRenderer.type = null;
        SearchRenderer.term = null;
        return 1;
    }

    private fun search(ctx : CommandContext<S>, type : InstructionBlock?, term : String?) : Int {
        ctx.source.sendFeedback(Text.translatable("command.${Main.ID}.search.success.set"));
        SearchRenderer.type = type;
        SearchRenderer.term = term?.lowercase();
        return 1;
    }

}
