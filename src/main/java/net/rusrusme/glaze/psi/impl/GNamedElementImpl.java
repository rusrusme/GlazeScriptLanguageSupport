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

package net.rusrusme.glaze.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import net.rusrusme.glaze.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GNamedElementImpl extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    public GNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (this instanceof GComponentBinding) {
            ASTNode[] ids = getNode().getChildren(TokenSet.create(GTypes.IDENTIFIER));
            if (ids.length >= 2) {
                return ids[1].getPsi();
            }
        }
        return findChildByType(GTypes.IDENTIFIER);
    }

    @Override
    public String getName() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement id = getNameIdentifier();
        if (id != null) {
            PsiElement newId = GElementFactory.createIdentifier(getProject(), newName);
            if (newId != null) {
                id.replace(newId);
            }
        }
        return this;
    }
}