package net.totobirdcreations.gemblaze.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.item.InstructionBlock
import net.totobirdcreations.gemblaze.util.item.InstructionBlocks
import java.util.concurrent.CompletableFuture


internal object InstructionTypeArgument : ArgumentType<InstructionTypeArgument.Result> {

    private val INVALID_TYPE = DynamicCommandExceptionType { string -> Text.translatable("command.${Main.ID}.search.invalid_type", string) }

    override fun parse(reader : StringReader) : Result {
        val start = reader.cursor;
        val string = reader.readUnquotedString();
        try {
            return Result(InstructionBlocks.instructionBlocks.values.first { type -> type.name_cmd == string });
        } catch (_ : NoSuchElementException) {
            reader.cursor = start;
            throw INVALID_TYPE.createWithContext(reader, Text.translatable("command.${Main.ID}.search.invalid_type.detailed", string));
        }
    }

    override fun <S> listSuggestions(
        context : CommandContext<S>,
        builder : SuggestionsBuilder
    ) : CompletableFuture<Suggestions> {
        for (type in InstructionBlocks.instructionBlocks.values) {
            builder.suggest(type.name_cmd);
        }
        return builder.buildFuture();
    }


    fun register() {
        ArgumentTypeRegistry.registerArgumentType(
            Identifier(Main.ID, "instructiontype"),
            InstructionTypeArgument::class.java, ConstantArgumentSerializer.of { -> InstructionTypeArgument }
        )
    }

    fun get(ctx : CommandContext<S>, name : String) : InstructionBlock? {
        return ctx.getArgument(name, Result::class.java).result;
    }


    internal data class Result(val result : InstructionBlock?);

}