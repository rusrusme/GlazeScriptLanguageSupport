package net.rusrusme.glaze.formatting;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import net.rusrusme.glaze.core.GLanguage;
import org.jetbrains.annotations.NotNull;

public class GLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @Override
    public @NotNull Language getLanguage() {
        return GLanguage.INSTANCE;
    }

    @Override
    public void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                  @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 4;
        indentOptions.TAB_SIZE = 4;
        indentOptions.CONTINUATION_INDENT_SIZE = 4;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return """
                system MovementSystem {
                    on_update(float dt) {
                        for (entity e, Transform t, Velocity v) exclude Frozen {
                            t.position += v.direction * dt;
                        }
                    }
                }
                """;
    }
}