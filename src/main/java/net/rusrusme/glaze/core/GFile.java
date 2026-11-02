package net.rusrusme.glaze.core;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class GFile extends PsiFileBase {
    protected GFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, GLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return GFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Glaze Script File";
    }
}
