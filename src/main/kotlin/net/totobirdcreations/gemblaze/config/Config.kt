package net.totobirdcreations.gemblaze.config

import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen
import net.totobirdcreations.gemblaze.util.*
import net.totobirdcreations.gemblaze.util.hypercube.VariableScope
import java.awt.Color
import dev.isxander.yacl3.config.v2.api.autogen.Boolean as BooleanAG
import dev.isxander.yacl3.config.v2.api.autogen.ColorField as ColourAG
import dev.isxander.yacl3.config.v2.api.autogen.DoubleField as DoubleAG
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler as EnumAG
import dev.isxander.yacl3.config.v2.api.autogen.IntField as IntAG


class Config {

    @AutoGen(category = "chat")
    @EnumAG
    @SerialEntry
    @JvmField var chatAutoSetMode : ChatMode = ChatMode.LOCAL;

    @AutoGen(category = "chat", group = "colours")
    @ColourAG(allowAlpha = false)
    @SerialEntry
    @JvmField var chatColoursNameDefault : Color = Color(0.875f, 0.875f, 0.875f);

    @AutoGen(category = "chat", group = "colours")
    @ColourAG(allowAlpha = false)
    @SerialEntry
    @JvmField var chatColoursNameInrange : Color = Color(0.75f, 0.875f, 1.0f);

    @AutoGen(category = "chat", group = "hideTags")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField var chatHideTagsVIP : Boolean = true;

    @AutoGen(category = "chat", group = "hideTags")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField var chatHideTagsRanks : Boolean = false;

    @AutoGen(category = "chat", group = "suppress")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField var chatSuppressPlotAds : Boolean = false;

    @AutoGen(category = "chat", group = "suppress")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField var chatSuppressPlotBoosts : Boolean = false;


    @AutoGen(category = "interface")
    @BooleanAG(formatter = BooleanAG.Formatter.YES_NO, colored = true)
    @SerialEntry
    @JvmField var interfaceHideSidebar : Boolean = true;

    @AutoGen(category = "interface")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var interfaceCpuOverlay : Boolean = true;

    @AutoGen(category = "interface")
    @EnumAG
    @SerialEntry
    @JvmField var interfaceCpuOverlayPosition : CpuOverlayPosition = CpuOverlayPosition.TOP_RIGHT;

    @AutoGen(category = "interface")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var interfaceInstructionDocs : Boolean = true;


    @AutoGen(category = "development")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentCompactInventory : Boolean = true;

    @AutoGen(category = "development")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentAutoWorldeditWandBuild : Boolean = true;

    @AutoGen(category = "development")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentAutoWorldeditWandDev : Boolean = true;

    @AutoGen(category = "development")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentAutoEditValues : Boolean = true;

    @AutoGen(category = "development")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentNightVision : Boolean = true;

    @AutoGen(category = "development")
    @ColourAG(allowAlpha = true)
    @SerialEntry
    @JvmField var developmentSearchColour : Color = Color(1.0f, 1.0f, 1.0f, 1.0f);

    @AutoGen(category = "development", group = "autoFlags")
    @EnumAG
    @SerialEntry
    @JvmField var developmentAutoFlagCodespaceCompact : CodespaceSpacing = CodespaceSpacing.COMPACT;

    @AutoGen(category = "development", group = "autoFlags")
    @EnumAG
    @SerialEntry
    @JvmField var developmentAutoFlagCodespaceStyle : CodespaceStyle = CodespaceStyle.LINE;

    @AutoGen(category = "development", group = "autoFlags")
    @EnumAG
    @SerialEntry
    @JvmField var developmentAutoFlagCodespaceColour : CodespaceColour = CodespaceColour.CLEAR;

    @AutoGen(category = "development", group = "autoFlags")
    @EnumAG
    @SerialEntry
    @JvmField var developmentAutoFlagVariableScope : VariableScope = VariableScope.LINE;

    @AutoGen(category = "development", group = "beta")
    @BooleanAG(formatter = BooleanAG.Formatter.ON_OFF, colored = true)
    @SerialEntry
    @JvmField var developmentCompactCoder : Boolean = false;


    @AutoGen(category = "optimisation")
    @DoubleAG(min = 8.0, max = 128.0)
    @SerialEntry
    @JvmField var optimisationChesthideDistance : Double = 32.0;

    @AutoGen(category = "optimisation")
    @IntAG(min = 0, max = 8)
    @SerialEntry
    @JvmField var optimisationChesthideCodespaces : Int = 2;

}
