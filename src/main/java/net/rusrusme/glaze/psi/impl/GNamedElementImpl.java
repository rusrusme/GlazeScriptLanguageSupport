package net.rusrusme.glaze.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
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