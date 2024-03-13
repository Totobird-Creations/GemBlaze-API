package net.totobirdcreations.gemblaze.util


interface CommandFlag<T : CommandFlag<T>> {
    val flag  : String;
    val isDef : Boolean;
}
internal inline fun <reified U: Enum<U>, T : CommandFlag<U>> T.applyCommandFlags(args : MutableList<String>) {
    var final : T? = null;
    for (flag in enumValues<U>()) {
        @Suppress("UNCHECKED_CAST")
        if ((flag as T).flag in args) {
        //        ----Guaranteed.
            final = flag;
            break;
        }
    }
    if (final?.isDef == true) {
        args.remove(final.flag);
    }
    if (final == null && ! this.isDef) {
        args.add(this.flag);
    }
}


enum class CodespaceSpacing(override val flag : String) : CommandFlag<CodespaceSpacing> {
    COMPACT ("-c"),
    SPARSE  ("-s");

    override val isDef get() = this == SPARSE;
}


enum class CodespaceStyle(override val flag : String) : CommandFlag<CodespaceStyle> {
    LINE   ("-l"),
    DOUBLE ("-d"),
    FULL   ("-f");

    override val isDef get() = this == FULL;
}


enum class CodespaceColour(override val flag : String) : CommandFlag<CodespaceColour> {
    CLEAR      ("clear"      ),
    WHITE      ("white"      ),
    ORANGE     ("orange"     ),
    MAGENTA    ("magenta"    ),
    LIGHT_BLUE ("light_blue" ),
    YELLOW     ("yellow"     ),
    LIME       ("lime"       ),
    PINK       ("pink"       ),
    GREY       ("gray"       ),
    LIGHT_GREY ("light_gray" ),
    CYAN       ("cyan"       ),
    PURPLE     ("purple"     ),
    BLUE       ("blue"       ),
    BROWN      ("brown"      ),
    GREEN      ("green"      ),
    RED        ("red"        ),
    BLACK      ("black"      ),
    TINTED     ("tinted"     );

    override val isDef get() = this == CLEAR;
}
