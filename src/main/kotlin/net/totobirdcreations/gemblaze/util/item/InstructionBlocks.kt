package net.totobirdcreations.gemblaze.util.item

import net.minecraft.block.Block
import net.minecraft.item.ItemStack


internal object InstructionBlocks {

    internal var instructionBlocks : MutableMap<Block, InstructionBlock> = mutableMapOf();

}


internal data class InstructionBlock(
    val stack     : ItemStack,
    val name_sign : String,
    val name_cmd  : String
);
