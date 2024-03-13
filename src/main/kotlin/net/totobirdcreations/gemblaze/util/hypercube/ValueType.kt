package net.totobirdcreations.gemblaze.util.hypercube

import net.minecraft.util.Identifier


enum class ValueType(val type : String) {

    STRING     ( "txt"   ),
    TEXT       ( "comp"  ),
    NUMBER     ( "num"   ),
    LOCATION   ( "loc"   ),
    VECTOR     ( "vec"   ),
    SOUND      ( "snd"   ),
    PARTICLE   ( "part"  ),
    POTION     ( "pot"   ),
    GAME_VALUE ( "g_val" );

    val model = Identifier("hypercube", "item/hypercube/${this.name.lowercase()}");

}
