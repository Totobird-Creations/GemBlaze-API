package net.totobirdcreations.gemblazeapi.api.hypercube

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import java.util.*


class InstructionInfo(
    /**
     * The instruction block type.
     */
    val type        : InstructionType,
    /**
     * The name of the instruction block, including formatting.
     */
    val name        : Text,
    /**
     * The description of the instruction block.
     */
    val description : Collection<Text>,
    stack : ItemStack
) {
    /**
     * The instruction block's default stack.
     */
    val stack       : ItemStack = stack
        get() = field.copy();

    override fun toString() : String {
        return "${this.type}(`${this.name.string}`)";
    }

    companion object {
        private val stored : EnumMap<InstructionType, InstructionInfo> = EnumMap(InstructionType::class.java);

        internal fun clear() {
            this.stored.clear();
        }

        internal fun tryPut(stack : ItemStack) : Boolean {
            val type = try {
                InstructionType.values().first { type -> stack.isOf(type.item) };
            } catch (_ : NoSuchElementException) {return false;}
            val stack = stack.copy();
            this.stored[type] = InstructionInfo(
                type,
                stack.name,
                stack.getSubNbt(ItemStack.DISPLAY_KEY)
                    ?.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE.toInt())
                    ?.mapNotNull{element -> Text.Serializer.fromJson((element as NbtString).asString())}
                    ?: listOf(),
                stack
            );
            return true;
        }

        /**
         * Returns information about an instruction block.
         */
        @JvmStatic fun get(type : InstructionType) : InstructionInfo? {
            return this.stored[type];
        }

        override fun toString() : String {
            return this.stored.entries.joinToString("\n"){ (_, value) -> value.toString()};
        }

    }

}


enum class InstructionType(
    val block : Block,
    val item  : Item
) {

    PLAYER_EVENT  (Blocks.DIAMOND_BLOCK     , Items.DIAMOND_BLOCK     ),
    PLAYER_ACTION (Blocks.COBBLESTONE       , Items.COBBLESTONE       ),
    IF_PLAYER     (Blocks.OAK_PLANKS        , Items.OAK_PLANKS        ),
    ENTITY_EVENT  (Blocks.GOLD_BLOCK        , Items.GOLD_BLOCK        ),
    ENTITY_ACTION (Blocks.MOSSY_COBBLESTONE , Items.MOSSY_COBBLESTONE ),
    IF_ENTITY     (Blocks.BRICKS            , Items.BRICKS            ),
    SET_VARIABLE  (Blocks.IRON_BLOCK        , Items.IRON_BLOCK        ),
    IF_VARIABLE   (Blocks.OBSIDIAN          , Items.OBSIDIAN          ),
    GAME_ACTION   (Blocks.NETHERRACK        , Items.NETHERRACK        ),
    IF_GAME       (Blocks.RED_NETHER_BRICKS , Items.RED_NETHER_BRICKS ),
    SELECT_OBJECT (Blocks.PURPUR_BLOCK      , Items.PURPUR_BLOCK      ),
    ELSE          (Blocks.END_STONE         , Items.END_STONE         ),
    FUNCTION      (Blocks.LAPIS_BLOCK       , Items.LAPIS_BLOCK       ),
    CALL_FUNCTION (Blocks.LAPIS_ORE         , Items.LAPIS_ORE         ),
    PROCESS       (Blocks.EMERALD_BLOCK     , Items.EMERALD_BLOCK     ),
    CALL_PROCESS  (Blocks.EMERALD_ORE       , Items.EMERALD_ORE       ),
    CONTROL       (Blocks.COAL_BLOCK        , Items.COAL_BLOCK        ),
    REPEAT        (Blocks.PRISMARINE        , Items.PRISMARINE        );

}
