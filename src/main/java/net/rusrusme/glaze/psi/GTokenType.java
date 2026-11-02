package net.rusrusme.glaze.psi;

import com.intellij.psi.tree.IElementType;
import net.rusrusme.glaze.core.GLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GTokenType extends IElementType {
    public GTokenType(@NonNls @NotNull String debugName) {
        super(debugName, GLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "GTokenType." + super.toString();
    }
}