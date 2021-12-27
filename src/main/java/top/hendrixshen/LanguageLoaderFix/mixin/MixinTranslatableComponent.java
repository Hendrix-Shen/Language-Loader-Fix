package top.hendrixshen.LanguageLoaderFix.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TranslatableFormatException;
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

@Mixin(TranslatableComponent.class)
public class MixinTranslatableComponent {
    private final ThreadLocal<List<String>> llf_threadFmtList = ThreadLocal.withInitial(ArrayList::new);
    private static final Pattern LLF_TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    private static final Pattern LLF_ALL_TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[dfs]");

    @Final
    @Shadow
    private Object[] args;

    @Shadow @Final private static FormattedText TEXT_NULL;

    @ModifyVariable(
            method = "decomposeTemplate",
            at = @At(
                    value = "HEAD"
            ),
            ordinal = 0,
            argsOnly = true
    )
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

    @Inject(
            method = "getArgument",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void myGetArg(int i, CallbackInfoReturnable<FormattedText> cir) {
        List<String> fmtList = llf_threadFmtList.get();
        if (i >= this.args.length) {
            throw new TranslatableFormatException((TranslatableComponent) (Object) this, i);
        } else {
            Object object = this.args[i];
            if (object instanceof Component) {
                cir.setReturnValue((Component) object);
            } else {
                if (object == null) {
                    cir.setReturnValue(TEXT_NULL);
                } else {
                    String retString = object.toString();
                    if (i < fmtList.size()) {
                        try {
                            retString = String.format(fmtList.get(i), object);
                        } catch (IllegalFormatException ignored) {
                        }
                    }
                    cir.setReturnValue(FormattedText.of(retString));
                }
            }
        }
    }
}
