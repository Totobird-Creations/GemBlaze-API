package net.totobirdcreations.gemblazeapi.api.hypercube

import net.minecraft.util.Identifier


const val HYPERCUBE_PREFIX : String = "hypercube";


enum class ChatMode(
    val id        : String?,
    val isDefault : Boolean
) {
    DONT_CHANGE (null     , false ),
    DND         ("dnd"    , false ),
    NONE        ("none"   , false ),
    LOCAL       ("local"  , false ),
    GLOBAL      ("global" , true  )
}


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

    val model = Identifier(HYPERCUBE_PREFIX, "item/${HYPERCUBE_PREFIX}/${this.name.lowercase()}");

}


enum class VariableScope(
    val id        : String,
    val flag      : String,
    val isDefault : Boolean,
    val colour    : Int
) {
    LINE  ("line"    , "-i", false , 0x55AAFF),
    LOCAL ("local"   , "-l", false , 0x55FF55),
    GAME  ("unsaved" , "-g", true  , 0xAAAAAA),
    SAVE  ("saved"   , "-s", false , 0xFFFF55);

    val model = Identifier(HYPERCUBE_PREFIX, "item/${HYPERCUBE_PREFIX}/variable/${this.name.lowercase()}");
}


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

    val model = Identifier(HYPERCUBE_PREFIX, "item/${HYPERCUBE_PREFIX}/parameter/${this.name.lowercase()}");

}


enum class CodespaceStyle(
    val flag      : String,
    val isDefault : Boolean
) {
    LINE   ("-l", false ),
    DOUBLE ("-d", false ),
    FULL   ("-f", true  )
}
