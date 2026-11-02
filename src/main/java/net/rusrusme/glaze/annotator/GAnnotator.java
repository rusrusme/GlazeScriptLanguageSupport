package net.rusrusme.glaze.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.glaze.highlight.GSyntaxHighlighter;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;

public class GAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof GUserType || element instanceof GComponentDeclaration || element instanceof GStructDeclaration) {
            PsiElement id = element instanceof GUserType ? element : ((PsiNameIdentifierOwner) element).getNameIdentifier();
            if (id != null) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(id)
                        .textAttributes(GSyntaxHighlighter.TYPE)
                        .create();
            }
        }

        if (element instanceof GComponentBinding binding) {
            PsiElement firstChild = binding.getFirstChild();
            if (firstChild != null) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(firstChild)
                        .textAttributes(GSyntaxHighlighter.TYPE)
                        .create();
            }
        }

        if (element instanceof GSystemDeclaration sysDecl) {
            boolean hasUpdate = !PsiTreeUtil.findChildrenOfType(sysDecl, GOnUpdateDeclaration.class).isEmpty();
            boolean hasEvent = !PsiTreeUtil.findChildrenOfType(sysDecl, GOnEventDeclaration.class).isEmpty();
            if (!hasUpdate && !hasEvent && sysDecl.getNameIdentifier() != null) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                "System '" + sysDecl.getNameIdentifier().getText() + "' must contain at least one on_update or on_event block")
                        .range(sysDecl.getNameIdentifier())
                        .create();
            }
            return;
        }

        if (element instanceof GForEcsStmt ecsStmt) {
            boolean insideUpdate = PsiTreeUtil.getParentOfType(ecsStmt, GOnUpdateDeclaration.class) != null;
            boolean insideEvent = PsiTreeUtil.getParentOfType(ecsStmt, GOnEventDeclaration.class) != null;
            if (!insideUpdate && !insideEvent) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                "ECS view queries are only valid inside 'on_update' or 'on_event' blocks")
                        .range(ecsStmt)
                        .create();
            }
            return;
        }

        if (element.getNode().getElementType() != GTypes.IDENTIFIER) return;

        PsiElement parent = element.getParent();
        if (parent instanceof GStructDeclaration || parent instanceof GComponentDeclaration
                || parent instanceof GSystemDeclaration || parent instanceof GFunctionDeclaration
                || parent instanceof GFieldDeclaration || parent instanceof GParameter
                || parent instanceof GLetDeclaration || parent instanceof GVarDeclaration
                || parent instanceof GForRangeStmt
                || parent instanceof GForEcsStmt
                || parent instanceof GMemberAccess || parent instanceof GScopedIdentifier
                || parent instanceof GComponentBinding || parent instanceof GUserType) {
            return;
        }

        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() == null) {
            String name = element.getText();
            if (isBuiltinMathFunction(name)) {
                return;
            }
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot resolve symbol '" + name + "'")
                    .range(element)
                    .create();
        }

        String name = element.getText();
        if (isBuiltinMathFunction(name)) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(GSyntaxHighlighter.INTRINSIC)
                    .create();
            return;
        }
    }

    private boolean isBuiltinMathFunction(String name) {
        return switch (name) {
            case "length", "distance", "dot", "cross", "normalize", "reflect", "refract",
                 "inverse", "transpose", "determinant",
                 "translate", "rotate", "scale", "lookAt", "perspective",
                 "slerp", "conjugate", "mat4_cast", "angleAxis",
                 "clamp", "mix", "step", "smoothstep", "min", "max", "abs",
                 "sin", "cos", "tan", "asin", "acos", "atan", "radians", "degrees", "sqrt", "pow" -> true;
            default -> false;
        };
    }
}