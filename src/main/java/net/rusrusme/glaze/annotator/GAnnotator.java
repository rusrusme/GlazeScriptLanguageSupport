package net.rusrusme.glaze.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.glaze.core.GBuiltinRegistry;
import net.rusrusme.glaze.highlight.GSyntaxHighlighter;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // 1. User Types & Component Declarations einfärben
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

        // 2. Semantische System-Prüfung
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

        // 3. ECS Query-Kontext
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

        // Deklarations-Bezeichner überspringen
        PsiElement parent = element.getParent();
        if (parent instanceof GStructDeclaration || parent instanceof GComponentDeclaration
                || parent instanceof GSystemDeclaration || parent instanceof GFunctionDeclaration
                || parent instanceof GFieldDeclaration || parent instanceof GParameter
                || parent instanceof GLetDeclaration || parent instanceof GVarDeclaration
                || parent instanceof GForRangeStmt || parent instanceof GForEcsStmt
                || parent instanceof GMemberAccess || parent instanceof GScopedIdentifier
                || parent instanceof GComponentBinding || parent instanceof GUserType) {
            return;
        }

        String name = element.getText();

        // 4. Built-in Math Functions prüfen & validieren
        GBuiltinRegistry.BuiltinSignature sig = GBuiltinRegistry.getSignature(name);
        if (sig != null) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(GSyntaxHighlighter.INTRINSIC)
                    .create();

            validateBuiltinFunction(element, sig, holder);
            return;
        }

        // 5. Normale Symbole auflösen
        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot resolve symbol '" + name + "'")
                    .range(element)
                    .create();
        }
    }

    private void validateBuiltinFunction(@NotNull PsiElement element, @NotNull GBuiltinRegistry.BuiltinSignature sig, @NotNull AnnotationHolder holder) {
        PsiElement postfix = PsiTreeUtil.getParentOfType(element, GPostfixExpr.class);
        if (postfix == null) return;

        GCallExpression call = PsiTreeUtil.getChildOfType(postfix, GCallExpression.class);
        if (call == null) return;

        List<GExpression> args = PsiTreeUtil.getChildrenOfTypeAsList(call, GExpression.class);
        GBuiltinRegistry.TypeKind[] expected = sig.expectedParams();

        // Parameter-Anzahl prüfen
        if (args.size() != expected.length) {
            holder.newAnnotation(HighlightSeverity.ERROR,
                            "'" + sig.name() + "' expects " + expected.length + " argument" + (expected.length == 1 ? "" : "s") + ", but got " + args.size())
                    .range(element)
                    .create();
            return;
        }

        // Typen prüfen
        for (int i = 0; i < args.size(); i++) {
            GExpression arg = args.get(i);
            String actualType = inferExpressionType(arg);

            if (actualType.isEmpty()) continue;

            GBuiltinRegistry.TypeKind expKind = expected[i];
            if (!matchesKind(actualType, expKind)) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                "Type mismatch on argument " + (i + 1) + " of '" + sig.name() + "': expected " + formatKind(expKind) + ", got '" + actualType + "'")
                        .range(element)
                        .create();
                break;
            }
        }
    }

    private boolean matchesKind(@NotNull String type, @NotNull GBuiltinRegistry.TypeKind kind) {
        return switch (kind) {
            case VEC -> type.equals("vec2") || type.equals("vec3") || type.equals("vec4");
            case VEC3 -> type.equals("vec3");
            case MAT -> type.equals("mat2") || type.equals("mat3") || type.equals("mat4");
            case QUAT -> type.equals("quat");
            case FLOAT -> type.equals("float") || type.equals("double");
            case ANY_NUMERIC -> type.equals("float") || type.equals("double")
                    || type.startsWith("int") || type.startsWith("uint")
                    || type.startsWith("vec");
        };
    }

    private String formatKind(@NotNull GBuiltinRegistry.TypeKind kind) {
        return switch (kind) {
            case VEC -> "vector ('vec2', 'vec3', 'vec4')";
            case VEC3 -> "'vec3'";
            case MAT -> "matrix ('mat2', 'mat3', 'mat4')";
            case QUAT -> "'quat'";
            case FLOAT -> "'float'";
            case ANY_NUMERIC -> "numeric scalar or vector";
        };
    }

    private String inferExpressionType(@NotNull GExpression expr) {
        // A. Literale über getNode().findChildByType ermitteln
        if (expr.getNode().findChildByType(GTypes.FLOAT_LITERAL) != null) return "float";
        if (expr.getNode().findChildByType(GTypes.INT_LITERAL) != null) return "int32";
        if (expr.getNode().findChildByType(GTypes.STRING_LITERAL) != null) return "string";
        if (expr.getNode().findChildByType(GTypes.CHAR_LITERAL) != null) return "char";
        if (expr.getNode().findChildByType(GTypes.BOOL_TRUE) != null || expr.getNode().findChildByType(GTypes.BOOL_FALSE) != null) {
            return "bool";
        }

        // B. Konstruktoren wie vec3(...) oder mat4(...)
        GMathConstructor mathCons = PsiTreeUtil.findChildOfType(expr, GMathConstructor.class);
        if (mathCons != null) {
            return mathCons.getMathType().getText();
        }

        // C. Variablen
        for (PsiElement child : PsiTreeUtil.findChildrenOfType(expr, PsiElement.class)) {
            if (child.getNode().getElementType() == GTypes.IDENTIFIER) {
                String varName = child.getText();
                for (GPsiUtil.VisibleSymbol sym : GPsiUtil.findVisibleSymbols(expr)) {
                    if (varName.equals(sym.name())) {
                        return sym.type();
                    }
                }
            }
        }

        return "";
    }
}