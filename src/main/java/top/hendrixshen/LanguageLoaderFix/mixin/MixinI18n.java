package top.hendrixshen.LanguageLoaderFix.mixin;

import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IllegalFormatException;
import java.util.regex.Pattern;

@Mixin(I18n.class)
public class MixinI18n {
    // from Language.class
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    @Redirect(method = "translate", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static String myFormat(String format, Object[] args) {
        try {
            return String.format(format, args);
        } catch (IllegalFormatException ignored) {
            return String.format(TOKEN_PATTERN.matcher(format).replaceAll("%$1s"), args);
        }
    }

}
