package net.totobirdcreations.gemblaze.render

import dev.dfonline.codeclient.location.Dev
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.totobirdcreations.gemblaze.Main
import net.totobirdcreations.gemblaze.util.ColourUtil
import net.totobirdcreations.gemblaze.util.item.InstructionBlocks
import net.totobirdcreations.gemblaze.util.item.UtilityItems
import net.totobirdcreations.gemblaze.util.value.ExpirableValue
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList


internal object DocsRenderer {

    private var active : Boolean         = false;
    private var lines  : ArrayList<Text> = arrayListOf();
    fun clear() {
        this.active = false;
        this.lines.clear();

        this.lastOpened = null;
        this.refSlot    = null;
        this.refUuid    = null;
        this.refRevert  = null;
    }

    fun render(context : DrawContext) {
        if (Main.CONFIG.interfaceInstructionDocs && Main.location is Dev && this.active) {
            val tr = Main.CLIENT.textRenderer;
            var y  = 10;
            for (line in this.lines) {
                context.drawText(tr, line, 10, y, 0xffffff, true);
                y += tr.fontHeight;
            }
        }
    }


    private var lastOpened : BlockPos?  = null;
    private var refSlot    : Int?       = null; // The slot where the reference book is expected to be.
    private var refUuid    : String?    = null; // Used to track if the reference book was changed.
    private var refRevert  : ItemStack? = null; // The stack to revert to after getting the needed information.

    fun onOpenChest(pos : BlockPos) {
        this.clear();
        val loc = Main.location;
        if (loc is Dev && loc.isInDev(pos)) {

            // Find a reference book in the inventory.
            val inv = Main.CLIENT.player?.inventory?.main ?: return;
            val ref = UtilityItems.referenceBook ?: return;
            for (i in inv.indices) {
                val stack = inv[i];
                try {
                    if (stack.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                        == ref.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
                    ) {
                        this.refSlot = i;
                        break;
                    }
                } catch (_ : NullPointerException) {}
            }
            // If no reference book was found, put one in the inventory.
            if (this.refSlot == null) {
                this.refSlot   = 17;
                this.refRevert = inv[this.refSlot!!];
            }
            // Add extra data to track if the server changed the book.
            this.refUuid = UUID.randomUUID().toString();
            ref.orCreateNbt.put(this.refUuid!!, NbtString.of(""));
            // Put the new book.
            UtilityItems.putInventory(this.refSlot!!, ref, updateClient = false);

            this.lastOpened = pos.add(-1, -1, 0);

        }
    }


    fun onSlotUpdate(slot : Int, stack : ItemStack) : Boolean {
        if (slot == this.refSlot && stack.getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
            == (UtilityItems.referenceBook ?: return false).getSubNbt("PublicBukkitValues")?.getString("hypercube:item_instance")
        ) {
            val refSlot   = this.refSlot!!;
            val refRevert = this.refRevert;
            if (refRevert != null) {
                UtilityItems.putInventory(refSlot, refRevert, updateClient = false);
            }
            if (this.refUuid != null && stack.nbt?.contains(this.refUuid) != true) {
                this.active = false;
                this.lines.clear();
                val lore = stack
                    .getSubNbt(ItemStack.DISPLAY_KEY)
                    ?.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE.toInt())
                    ?.mapNotNull { line -> Text.Serialization.fromJson((line as NbtString).asString()) };
                if (lore != null) {
                    this.lines.addAll(lore.toTypedArray());
                }
            }
            return refRevert != null;
        }
        return false;
    }


    fun clearScreen() {
        this.active    = false;
        this.refSlot   = null;
        this.refUuid   = null;
        this.refRevert = null;
    }


    fun onOpenScreen() {
        val world   = Main.CLIENT.world;
        val signPos = this.lastOpened;
        if (world != null && signPos != null) {
            val entity = world.getBlockEntity(signPos);
            if (entity is SignBlockEntity) {try {
                val text  = entity.frontText;
                val type = InstructionBlocks.instructionBlocks.values.first { type -> type.name_sign == text.getMessage(0, false).string };

                val lines = mutableListOf<Text>();
                val name  = type.stack.name;
                val style = name.style;
                val header = Text.literal(name.string.uppercase()).setStyle(style.withBold(true));
                if (text.getMessage(3, false).string == "NOT") {
                    header.append(Text.literal(" ɴᴏᴛ").setStyle(Style.EMPTY.withColor(0xff0000).withBold(false)));
                }
                lines.add(header);
                val subColour = ColourUtil.intToHsv(name.style.color?.rgb ?: 0);
                val subStyle  = style.withColor(ColourUtil.hsvToInt(subColour.first, subColour.second * 0.5f, subColour.third * 0.875f));
                for (i in 1..<3) {
                    val line = text.getMessage(i, false);
                    if (line.string.replace(" ", "").isNotEmpty()) {
                        lines.add(Text.literal("  ").append(line.copy().setStyle(subStyle)));
                    }
                }

                lines.add(Text.empty());
                this.lines.addAll(0, lines);
                this.active = true;

            } catch (_ : NoSuchElementException) {
                this.clear();
            }}
        }

        this.lastOpened = null;
    }

}