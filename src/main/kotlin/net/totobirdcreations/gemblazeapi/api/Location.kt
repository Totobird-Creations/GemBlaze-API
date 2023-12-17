package net.totobirdcreations.gemblazeapi.api

import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.totobirdcreations.gemblazeapi.util.RequestableValue


class DiamondFire internal constructor() {
    /**
     * The current node that the players is on.
     *
     * *May be unknown.*
     */
    val node : RequestableValue<DiamondFireNode> = RequestableValue{-> TODO()};
    /**
     * The current plot that the players is on.
     *
     * *Is null if the player is at spawn.*
     */
    var plot : DiamondFirePlot?                  = null
            internal set;

    override fun toString() : String {
        return "NODE( ${this.node} ) PLOT( ${this.plot} )";
    }

}


sealed class DiamondFireNode private constructor(
    /**
     * The argument used in the `/server [id]` command.
     */
    val id   : String?,
    /**
     * The node's display name.
     */
    val name : String
) {

    class Indexed(
        val index : UInt
    ) : DiamondFireNode("node${index}", "Node ${index}");

    class Event : DiamondFireNode("event", "Event");

    class Beta : DiamondFireNode("beta", "Beta");

    class Other(
        name : String
    ) : DiamondFireNode(null, name);

    override fun toString() : String {
        return "ID( ${this.id}) NAME( ${this.name.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")} )";
    }

    override fun equals(other : Any?) : Boolean {
        return other is DiamondFireNode && this::class == other::class && this.id == other.id;
    }

    override fun hashCode() : Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result;
    }

}


class DiamondFirePlot internal constructor(mode : DiamondFireMode) {
    /**
     * The current mode the player is in.
     */
    var mode        : DiamondFireMode = mode
        internal set;
    /**
     * The id of the current plot.
     *
     * *May be unknown.*
     */
    val id          : RequestableValue<UInt>    = RequestableValue{-> TODO()};
    /**
     * The name of the current plot.
     *
     * *May be unknown.*
     */
    val name        : RequestableValue<Text>    = RequestableValue{-> TODO()};
    /**
     * The username of the owner of the current plot.
     *
     * *May be unknown.*
     */
    val owner       : RequestableValue<String>  = RequestableValue{-> TODO()};
    /**
     * Whether the current plot is whitelisted.
     *
     * *May be unknown.*
     */
    val whitelisted : RequestableValue<Boolean> = RequestableValue{-> TODO()};
    /**
     * Information about the plot in the world.
     * Includes details like coordinates and size.
     */
    val area        : DiamondFirePlotArea       = DiamondFirePlotArea();

    override fun toString() : String {
        return "MODE( ${this.mode} ) ID( ${this.id} ) NAME( `${this.name.getOrNull()?.string?.replace("\\", "\\\\")?.replace("`", "\\`")}` ) OWNER( ${this.owner} ) WHITELISTED( ${this.whitelisted} ) AREA( ${this.area} )"
    }

};


class DiamondFirePlotArea internal constructor() {
    /**
     * The North-West corner of the build area of the current plot.
     *
     * *May be unknown.*
     */
    val buildCorner : BlockPos?                = null;
    /**
     * The North-West corner of the dev/code area of the current plot.
     *
     * *May be unknown.*
     */
    val devCorner   : BlockPos?                = null;
    /**
     * The size of the current plot.
     *
     * *May be unknown.*
     */
    val size        : DiamondFirePlotAreaSize? = null;

    override fun toString() : String {
        return "BUILDC( ${this.buildCorner} ) DEVC( ${this.devCorner} ) SIZE( ${this.size} )";
    }

}


enum class DiamondFirePlotAreaSize(
    val size : UInt
) {
    SMALL   (51u),
    LARGE   (101u),
    MASSIVE (301u)
}


enum class DiamondFireMode {
    PLAY,
    BUILD,
    DEV
}
