package net.rusrusme.glaze.core;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import net.rusrusme.glaze.psi.GTypes;

public class GQuoteHandler extends SimpleTokenSetQuoteHandler {
    public GQuoteHandler() {
        super(GTypes.STRING_LITERAL, GTypes.CHAR_LITERAL);
    }
}