package net.rusrusme.glaze.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;

public class GAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // 1. Semantic Rule: system must contain at least one on_update or on_event
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

        // 2. Semantic Rule: ECS View Query Loop (for_ecs_stmt) is valid only inside on_update and on_event
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

        // 3. Unresolved Symbol Resolution
        if (element.getNode().getElementType() != GTypes.IDENTIFIER) return;

        PsiElement parent = element.getParent();
        if (parent instanceof GStructDeclaration || parent instanceof GComponentDeclaration
                || parent instanceof GSystemDeclaration || parent instanceof GFunctionDeclaration
                || parent instanceof GFieldDeclaration || parent instanceof GParameter
                || parent instanceof GLetDeclaration || parent instanceof GVarDeclaration
                || parent instanceof GMemberAccess || parent instanceof GScopedIdentifier) {
            return;
        }

        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() == null) {
            String name = element.getText();
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot resolve symbol '" + name + "'")
                    .range(element)
                    .create();
        }
    }
}