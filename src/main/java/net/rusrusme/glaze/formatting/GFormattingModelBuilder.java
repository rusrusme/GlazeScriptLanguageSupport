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

import com.intellij.formatting.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import net.rusrusme.glaze.core.GLanguage;
import net.rusrusme.glaze.psi.GTypes;
import org.jetbrains.annotations.NotNull;

public class GFormattingModelBuilder implements FormattingModelBuilder {
    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, GLanguage.INSTANCE)
                .after(GTypes.KEYWORD_IF).spaces(1)
                .after(GTypes.KEYWORD_WHILE).spaces(1)
                .after(GTypes.KEYWORD_FOR).spaces(1)
                .after(GTypes.KEYWORD_SWITCH).spaces(1)
                .after(GTypes.KEYWORD_ELSE).spaces(1)
                .around(GTypes.ARROW).spaces(1)
                .around(GTypes.ASSIGN).spaces(1)
                .around(GTypes.ADD_ASSIGN).spaces(1)
                .around(GTypes.SUB_ASSIGN).spaces(1)
                .before(GTypes.LBRACE).spaces(1)
                .after(GTypes.COMMA).spaces(1);
    }

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        GBlock block = new GBlock(
                formattingContext.getNode(),
                Wrap.createWrap(WrapType.NONE, false),
                null,
                createSpaceBuilder(settings)
        );
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.getContainingFile(), block, settings);
    }
}