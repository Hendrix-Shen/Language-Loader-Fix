package top.hendrixshen.LanguageLoaderFix.mixin;

import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(Language.class)
public class MixinLanguage {
    private static Pattern FUCK_SYNTAX;

    @Inject(
            method = "<clinit>",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void onClInit(CallbackInfo ci) {
        FUCK_SYNTAX = Pattern.compile("[^\\W\\w]");
    }

    @Redirect(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/regex/Pattern;matcher(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;"
            )
    )
    private static Matcher onMatcher(Pattern pattern, CharSequence input) {
        return FUCK_SYNTAX.matcher(input);
    }
}
