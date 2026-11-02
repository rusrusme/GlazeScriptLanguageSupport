/*
 * Copyright 2026 rusrusme
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.rusrusme.glaze.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
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

        // 1. Lokale Variablen (let & var) vor der aktuellen Position
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

        // 2. Funktionsparameter & Callback-Parameter (on_update, on_event)
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

        // 3. Hierarchie nach oben durchlaufen (fuer ECS-Loops & Range-Loops)
        PsiElement cur = element;
        while (cur != null && !(cur instanceof PsiFile)) {
            // ECS-View Query: for (entity e, TransformComponent t)
            if (cur instanceof GForEcsStmt ecsStmt) {
                // A. Entity-Variable ermitteln (z.B. e)
                ASTNode entityNode = ecsStmt.getNode().findChildByType(GTypes.TYPE_ENTITY);
                if (entityNode != null) {
                    ASTNode next = entityNode.getTreeNext();
                    while (next != null && (next.getElementType() == TokenType.WHITE_SPACE || next.getElementType() == GTypes.COMMENT)) {
                        next = next.getTreeNext();
                    }
                    if (next != null && next.getElementType() == GTypes.IDENTIFIER) {
                        symbols.add(new VisibleSymbol(next.getText(), "entity", next.getPsi()));
                    }
                }

                // B. Component Bindings ermitteln (z.B. TransformComponent t)
                for (GComponentBinding binding : PsiTreeUtil.findChildrenOfType(ecsStmt, GComponentBinding.class)) {
                    ASTNode[] ids = binding.getNode().getChildren(TokenSet.create(GTypes.IDENTIFIER));
                    if (ids.length >= 2) {
                        String compType = ids[0].getText(); // TransformComponent
                        ASTNode varId = ids[1];             // t
                        symbols.add(new VisibleSymbol(varId.getText(), compType, varId.getPsi()));
                    } else if (ids.length == 1) {
                        GType typeNode = PsiTreeUtil.findChildOfType(binding, GType.class);
                        String compType = typeNode != null ? typeNode.getText() : "component";
                        symbols.add(new VisibleSymbol(ids[0].getText(), compType, ids[0].getPsi()));
                    }
                }
            }

            // Range Loop: for (type item : list)
            if (cur instanceof GForRangeStmt forRange && forRange.getNameIdentifier() != null) {
                symbols.add(new VisibleSymbol(forRange.getNameIdentifier().getText(), forRange.getType().getText(), forRange.getNameIdentifier()));
            }

            cur = cur.getParent();
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