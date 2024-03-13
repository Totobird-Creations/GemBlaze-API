package net.totobirdcreations.gemblaze.util


enum class CpuOverlayPosition(
    val right  : Boolean = false,
    val bottom : Boolean = false
) {
    ACTIONBAR    (false , false ),
    BOTTOM_LEFT  (false , true  ),
    BOTTOM_RIGHT (true  , true  ),
    TOP_LEFT     (false , false ),
    TOP_RIGHT    (true  , false)
}