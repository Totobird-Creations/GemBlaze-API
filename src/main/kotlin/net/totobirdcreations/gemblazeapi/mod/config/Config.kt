package net.totobirdcreations.gemblazeapi.mod.config

import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen
import dev.isxander.yacl3.config.v2.api.autogen.Boolean as BooleanAG
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler as EnumAG
import net.totobirdcreations.gemblazeapi.api.hypercube.ChatMode
import net.totobirdcreations.gemblazeapi.api.hypercube.CodespaceStyle
import net.totobirdcreations.gemblazeapi.api.hypercube.VariableScope


@Suppress("RedundantVisibilityModifier")
class Config {

    @AutoGen(category = "autoCommand")
    @EnumAG
    @SerialEntry
    @JvmField public var autoCommandChatMode : ChatMode = ChatMode.LOCAL;

    @AutoGen(category = "autoCommand")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var autoCommandCompactInventory : Boolean = true;

    @AutoGen(category = "autoCommand")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var autoCommandWorldeditWand : Boolean = true;

    @AutoGen(category = "autoCommand")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var autoCommandCpuOverlay : Boolean = true;

    @AutoGen(category = "autoCommand", group = "defaults")
    @EnumAG
    @SerialEntry
    @JvmField public var autoCommandDefaultsVarCreateScope : VariableScope = VariableScope.LINE;

    @AutoGen(category = "autoCommand", group = "defaults")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var autoCommandDefaultsCodespaceAddCompact : Boolean = true;

    @AutoGen(category = "autoCommand", group = "defaults")
    @EnumAG
    @SerialEntry
    @JvmField public var autoCommandDefaultsCodespaceAddStyle : CodespaceStyle = CodespaceStyle.LINE;


    @AutoGen(category = "interface")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField public var interfaceHideSidebar : Boolean = true;

    @AutoGen(category = "interface")
    @EnumAG
    @SerialEntry
    @JvmField public var interfaceCpuOverlayPosition : CpuOverlayPosition = CpuOverlayPosition.TOP_RIGHT;
    @Suppress("unused")
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

    @AutoGen(category = "interface")
    @BooleanAG
    @SerialEntry
    @JvmField public var interfaceInstructionBlockDocs : Boolean = true;


    @AutoGen(category = "interface", group = "inventory")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var interfaceInventoryAutoGlitchStick : Boolean = false;

    @AutoGen(category = "interface", group = "inventory")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField public var interfaceInventoryAutoCancelWand : Boolean = false;

}