package top.hendrixshen.LanguageLoaderFix;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageLoaderFix implements ModInitializer {
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    @Override
    public void onInitialize() {
        logger.info(String.format("[%s]: Mod initialized - Version: %s (%s)", Reference.MOD_NAME, Reference.MOD_VERSION, Reference.MOD_VERSION_TYPE));
    }
}
