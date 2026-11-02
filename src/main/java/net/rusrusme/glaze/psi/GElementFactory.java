package net.rusrusme.glaze.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.glaze.core.GFile;
import net.rusrusme.glaze.core.GFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GElementFactory {
    private GElementFactory() {}

    @Nullable
    public static PsiElement createIdentifier(@NotNull Project project, @NotNull String name) {
        GVarDeclaration varDecl = createVarDeclaration(project, name);
        return varDecl != null ? varDecl.getNameIdentifier() : null;
    }

    @Nullable
    public static GVarDeclaration createVarDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "var int32 " + name + " = 0;";
        GFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, GVarDeclaration.class);
    }

    @Nullable
    public static GStructDeclaration createStructDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "struct " + name + " { int32 dummy; }";
        GFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, GStructDeclaration.class);
    }

    @Nullable
    public static GComponentDeclaration createComponentDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "component " + name + " { int32 dummy; }";
        GFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, GComponentDeclaration.class);
    }

    @NotNull
    public static GFile createFile(@NotNull Project project, @NotNull String text) {
        return (GFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.gscript", GFileType.INSTANCE, text);
    }
}