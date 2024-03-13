package net.totobirdcreations.gemblaze.util.hypercube

import net.minecraft.util.Identifier


enum class ParameterType(val type : String) {

    ANY      ("any"  ),
    STRING   ("txt"  ),
    TEXT     ("comp" ),
    NUMBER   ("num"  ),
    LOCATION ("loc"  ),
    VECTOR   ("vec"  ),
    SOUND    ("snd"  ),
    PARTICLE ("part" ),
    POTION   ("pot"  ),
    ITEM     ("item" ),

    VARIABLE   ("var"  ),
    LIST       ("list" ),
    DICTIONARY ("dict" );

    val model = Identifier("hypercube", "item/hypercube/parameter/${this.name.lowercase()}");

}
