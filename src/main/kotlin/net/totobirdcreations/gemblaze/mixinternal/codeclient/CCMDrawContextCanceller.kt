package net.totobirdcreations.gemblaze.mixinternal.codeclient

import com.bawnorton.mixinsquared.api.MixinCanceller


class CCMDrawContextCanceller : MixinCanceller {

    // ResourcePackModels
    override fun shouldCancel(targetClassNames : MutableList<String>, mixinClassName : String) : Boolean {
        return mixinClassName == "dev.dfonline.codeclient.mixin.render.hud.MDrawContext";
    }

}