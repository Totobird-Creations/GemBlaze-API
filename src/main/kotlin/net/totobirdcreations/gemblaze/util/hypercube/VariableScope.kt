package net.totobirdcreations.gemblaze.util.hypercube

import net.minecraft.util.Identifier
import net.totobirdcreations.gemblaze.util.CommandFlag


enum class VariableScope(
    override val flag : String
) : CommandFlag<VariableScope> {
    LINE    ("-i"),
    LOCAL   ("-l"),
    UNSAVED ("-g"),
    SAVED   ("-s");

    override val isDef get() = this == UNSAVED;

    val model = Identifier("hypercube", "item/hypercube/variable/${this.name.lowercase()}");
}
