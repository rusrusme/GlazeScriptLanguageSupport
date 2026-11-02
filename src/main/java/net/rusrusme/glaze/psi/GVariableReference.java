package net.rusrusme.glaze.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    public GVariableReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        this.key = element.getText();
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        // 1. Visible local & scoped symbols
        for (GPsiUtil.VisibleSymbol sym : GPsiUtil.findVisibleSymbols(getElement())) {
            if (key.equals(sym.name())) {
                return new ResolveResult[]{ new PsiElementResolveResult(sym.element()) };
            }
        }
        // 2. Components
        for (GComponentDeclaration comp : GPsiUtil.findComponents(getElement())) {
            if (comp.getNameIdentifier() != null && key.equals(comp.getNameIdentifier().getText())) {
                return new ResolveResult[]{ new PsiElementResolveResult(comp.getNameIdentifier()) };
            }
        }
        // 3. Structs
        for (GStructDeclaration struct : GPsiUtil.findStructs(getElement())) {
            if (struct.getNameIdentifier() != null && key.equals(struct.getNameIdentifier().getText())) {
                return new ResolveResult[]{ new PsiElementResolveResult(struct.getNameIdentifier()) };
            }
        }
        // 4. Functions
        for (GFunctionDeclaration func : GPsiUtil.findFunctions(getElement())) {
            if (func.getNameIdentifier() != null && key.equals(func.getNameIdentifier().getText())) {
                return new ResolveResult[]{ new PsiElementResolveResult(func.getNameIdentifier()) };
            }
        }
        // 5. Enums
        for (GEnumDeclaration enumDecl : GPsiUtil.findEnums(getElement())) {
            if (enumDecl.getNameIdentifier() != null && key.equals(enumDecl.getNameIdentifier().getText())) {
                return new ResolveResult[]{ new PsiElementResolveResult(enumDecl.getNameIdentifier()) };
            }
        }
        // 6. Systems
        for (GSystemDeclaration sys : GPsiUtil.findSystems(getElement())) {
            if (sys.getNameIdentifier() != null && key.equals(sys.getNameIdentifier().getText())) {
                return new ResolveResult[]{ new PsiElementResolveResult(sys.getNameIdentifier()) };
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] results = multiResolve(false);
        return results.length == 1 ? results[0].getElement() : null;
    }
}