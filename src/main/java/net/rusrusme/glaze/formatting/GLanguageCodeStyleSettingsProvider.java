/*
 * Copyright 2026 rusrusme
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        indentOptions.USE_TAB_CHARACTER = false;
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