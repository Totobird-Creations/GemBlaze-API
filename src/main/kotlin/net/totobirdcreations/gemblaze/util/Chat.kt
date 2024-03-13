package net.totobirdcreations.gemblaze.util

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.text.MutableText
import net.minecraft.text.Text


enum class ChatMode(
    val id        : String?,
    val isDefault : Boolean
) {
    DONT_CHANGE (null  , false ),
    DND         ("dnd" , false ),
    NONE        ("n"   , false ),
    LOCAL       ("l"   , false ),
    GLOBAL      ("g"   , true  )
}


internal fun textToMm(text : Text) : String {
    return MiniMessage.miniMessage().serialize(JSONComponentSerializer.json().deserialize(Text.Serialization.toJsonString(text)));
}


internal fun mmToText(mm : String) : MutableText? {
    return Text.Serialization.fromJson(JSONComponentSerializer.json().serialize(MiniMessage.miniMessage().deserialize(mm)));
}


internal val PLAYER_CHAT : Regex = Regex("^(?<vip><gold>\\[<\\/gold><#ffd47f><click:suggest_command:'\\/vip'><hover:show_text:'<#ffd47f>VIP'>⭐<\\/hover><\\/click><\\/#ffd47f><gold>]<\\/gold>)? ?(?<ranks>(?:(?:<[^>]+>)*\\[[^\\]]+\\](?:<[^>]+>)*)*)(?<username>[A-Za-z0-9_]{1,16}): (?<message>.*)\$");
internal val PLOT_AD     : Regex = Regex("^<click:suggest_command:'\\/join (?<plotId>\\d+)'><hover:show_text:'<#2ad4d4>→ Click to join!<\\/#2ad4d4><br>(?<plotName>.+)<br><gray>ID:<\\/gray> <white>\\1<\\/white><br><blue>(?<node>.+)<\\/blue><br><br><gray>Ad by (?<plotOwner>[A-Za-z0-9_]{1,16})'><strikethrough><dark_gray> *<\\/dark_gray><\\/strikethrough><dark_gray>\\[</dark_gray> <yellow>Plot Ad</yellow> <dark_gray>\\]<\\/dark_gray><strikethrough><dark_gray> *<\\/dark_gray><\\/strikethrough><br>.*<gray>by \\4:<\\/gray> .*<br><strikethrough><dark_gray> *\$");
internal val PLOT_BOOST  : Regex = Regex("^<click:suggest_command:'/join (?<plotId>[0-9]+)'><hover:show_text:'(?<plotName>.+)<br><gray>ID:</gray> <white>\\1</white><br><blue>(?<node>.+)</blue><br><br><gray>Boosted by (?<booster>[A-Za-z0-9_]{1,16})'><strikethrough><dark_gray>\\n? *</dark_gray></strikethrough><br> *<bold>\u200C?</bold>.+<gray> by (?<plotOwner>[A-Za-z0-9_]{1,16}).*<br> *<bold>\u200C?</bold><#ffd47f>→ <#ffffaa>Click to join!</#ffffaa></#ffd47f><strikethrough><dark_gray>\\n? *\$");
internal val EDIT_VALUE  : Regex = Regex("^<click:suggest_command:'(?<command>.+)'><hover:show_text:'<#808080>Click here.'><#2ad4d4>⏵</#2ad4d4> Click to edit variable: \\1\$");

internal val CPU_OVERLAY : Regex = Regex("^<gold>CPU Usage: </gold><gray>\\[</gray>(?:<(\\w+)>▮*</\\1>)+<gray>] </gray><white>\\((?<percent>[0-9\\.]+)</white><white>%\\)\$");

internal val CODESPACE_COMMAND : Regex = Regex("^(?:p|plot) codespace add(?:\$| )");
internal val VARIABLE_COMMAND  : Regex = Regex("^var(?:\$| )");
