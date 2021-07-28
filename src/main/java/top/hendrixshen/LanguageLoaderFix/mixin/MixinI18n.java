package top.hendrixshen.LanguageLoaderFix.mixin;

import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(I18n.class)
public class MixinI18n {
    // from Language.class
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    @Redirect(method = "translate", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static String myFormat(String format, Object[] args) {
        String newFormat = TOKEN_PATTERN.matcher(format).replaceAll("%s");
        Matcher matcher = TOKEN_PATTERN.matcher(format);
        int matcher_start = 0;
        for (int i = 0; i < args.length && matcher.find(matcher_start); ++i) {
            args[i] = String.format(TOKEN_PATTERN.matcher(format).group(), args[0]);
            matcher_start = matcher.end();
        }
        return String.format(newFormat, args);
    }
}
