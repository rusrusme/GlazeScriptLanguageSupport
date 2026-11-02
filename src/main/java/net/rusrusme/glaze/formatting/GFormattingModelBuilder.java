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