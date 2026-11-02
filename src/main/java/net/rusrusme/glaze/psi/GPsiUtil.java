package net.rusrusme.glaze.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class GPsiUtil {
    private GPsiUtil() {}

    public record VisibleSymbol(@NotNull String name, @NotNull String type, @NotNull PsiElement element) {}

    @NotNull
    public static List<VisibleSymbol> findVisibleSymbols(@Nullable PsiElement element) {
        if (element == null) return Collections.emptyList();
        PsiFile file = element.getContainingFile();
        if (file == null) return Collections.emptyList();

        List<VisibleSymbol> symbols = new ArrayList<>();
        int caretOffset = element.getTextOffset();

        // 1. Local Variables (let / var) in current scope before caret
        Collection<GLetDeclaration> letDecls = PsiTreeUtil.findChildrenOfType(file, GLetDeclaration.class);
        for (GLetDeclaration decl : letDecls) {
            if (decl.getTextOffset() < caretOffset && decl.getNameIdentifier() != null) {
                symbols.add(new VisibleSymbol(decl.getNameIdentifier().getText(), decl.getType().getText(), decl.getNameIdentifier()));
            }
        }
        Collection<GVarDeclaration> varDecls = PsiTreeUtil.findChildrenOfType(file, GVarDeclaration.class);
        for (GVarDeclaration decl : varDecls) {
            if (decl.getTextOffset() < caretOffset && decl.getNameIdentifier() != null) {
                symbols.add(new VisibleSymbol(decl.getNameIdentifier().getText(), decl.getType().getText(), decl.getNameIdentifier()));
            }
        }

        // 2. Enclosing Function / Callback Parameters
        GFunctionDeclaration func = PsiTreeUtil.getParentOfType(element, GFunctionDeclaration.class);
        if (func != null) {
            for (GParameter param : PsiTreeUtil.findChildrenOfType(func, GParameter.class)) {
                if (param.getNameIdentifier() != null) {
                    symbols.add(new VisibleSymbol(param.getNameIdentifier().getText(), param.getType().getText(), param.getNameIdentifier()));
                }
            }
        }
        GOnUpdateDeclaration updateDecl = PsiTreeUtil.getParentOfType(element, GOnUpdateDeclaration.class);
        if (updateDecl != null && updateDecl.getNameIdentifier() != null) {
            symbols.add(new VisibleSymbol(updateDecl.getNameIdentifier().getText(), "float", updateDecl.getNameIdentifier()));
        }
        GOnEventDeclaration eventDecl = PsiTreeUtil.getParentOfType(element, GOnEventDeclaration.class);
        if (eventDecl != null && eventDecl.getNameIdentifier() != null) {
            symbols.add(new VisibleSymbol(eventDecl.getNameIdentifier().getText(), "event", eventDecl.getNameIdentifier()));
        }

        // 3. ECS Query Loop Bindings: for (entity e, Velocity v, Position p)
        GForEcsStmt ecsStmt = PsiTreeUtil.getParentOfType(element, GForEcsStmt.class);
        if (ecsStmt != null) {
            for (GComponentBinding binding : PsiTreeUtil.findChildrenOfType(ecsStmt, GComponentBinding.class)) {
                PsiElement id = binding.getNameIdentifier();
                if (id != null) {
                    List<PsiElement> idNodes = PsiTreeUtil.getChildrenOfTypeAsList(binding, PsiElement.class);
                    String compType = idNodes.isEmpty() ? "component" : idNodes.get(0).getText();
                    symbols.add(new VisibleSymbol(id.getText(), compType, id));
                }
            }
        }

        return symbols;
    }

    @NotNull
    public static List<GStructDeclaration> findStructs(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) return Collections.emptyList();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GStructDeclaration.class));
    }

    @NotNull
    public static List<GComponentDeclaration> findComponents(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) return Collections.emptyList();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GComponentDeclaration.class));
    }

    @NotNull
    public static List<GFunctionDeclaration> findFunctions(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) return Collections.emptyList();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GFunctionDeclaration.class));
    }

    @NotNull
    public static List<GEnumDeclaration> findEnums(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) return Collections.emptyList();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GEnumDeclaration.class));
    }

    @NotNull
    public static List<GSystemDeclaration> findSystems(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) return Collections.emptyList();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GSystemDeclaration.class));
    }
}