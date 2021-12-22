package top.hendrixshen.LanguageLoaderFix.mixin;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.TranslationException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TranslatableText.class)
public class MixinTranslatableText {
    private final ThreadLocal<List<String>> llf_threadFmtList = ThreadLocal.withInitial(ArrayList::new);
    private static final Pattern LLF_TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static final Pattern LLF_ALL_TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[dfs]");

    @Final
    @Shadow
    private static StringVisitable NULL_ARGUMENT;

    @Final
    @Shadow
    private Object[] args;


    @ModifyVariable(method = "setTranslation", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private String modifyTranslation(String translation) {
        List<String> fmtList = llf_threadFmtList.get();
        fmtList.clear();
        Matcher matcher = LLF_ALL_TOKEN_PATTERN.matcher(translation);
        int matcher_start = 0;
        while (matcher.find(matcher_start)) {
            fmtList.add(matcher.group());
            matcher_start = matcher.end();
        }
        return LLF_TOKEN_PATTERN.matcher(translation).replaceAll("%$1s");
    }

    @Inject(method = "getArg", at = @At(value = "HEAD"), cancellable = true)
    private void myGetArg(int index, CallbackInfoReturnable<StringVisitable> cir) {
        List<String> fmtList = llf_threadFmtList.get();
        if (index >= this.args.length) {
            throw new TranslationException((TranslatableText) (Object) this, index);
        } else {
            Object object = this.args[index];
            if (object instanceof Text) {
                cir.setReturnValue((Text) object);
            } else {
                if (object == null) {
                    cir.setReturnValue(NULL_ARGUMENT);
                } else {
                    String retString = object.toString();
                    if (index < fmtList.size()) {
                        try {
                            retString = String.format(fmtList.get(index), object);
                        } catch (IllegalFormatException ignored) {
                        }
                    }
                    cir.setReturnValue(StringVisitable.plain(retString));
                }
            }
        }
    }
}
