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

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class GReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(GTypes.IDENTIFIER),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        if (isDeclarationIdentifier(element)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[]{ new GVariableReference(element, new TextRange(0, element.getTextLength())) };
                    }
                }
        );
    }

    private static boolean isDeclarationIdentifier(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        return (parent instanceof GStructDeclaration structDecl && structDecl.getNameIdentifier() == element)
                || (parent instanceof GComponentDeclaration compDecl && compDecl.getNameIdentifier() == element)
                || (parent instanceof GSystemDeclaration sysDecl && sysDecl.getNameIdentifier() == element)
                || (parent instanceof GEnumDeclaration enumDecl && enumDecl.getNameIdentifier() == element)
                || (parent instanceof GEnumMember enumMember && enumMember.getNameIdentifier() == element)
                || (parent instanceof GFunctionDeclaration funcDecl && funcDecl.getNameIdentifier() == element)
                || (parent instanceof GFieldDeclaration fieldDecl && fieldDecl.getNameIdentifier() == element)
                || (parent instanceof GParameter param && param.getNameIdentifier() == element)
                || (parent instanceof GLetDeclaration letDecl && letDecl.getNameIdentifier() == element)
                || (parent instanceof GVarDeclaration varDecl && varDecl.getNameIdentifier() == element)
                || (parent instanceof GForRangeStmt forRange && forRange.getNameIdentifier() == element)
                || (parent instanceof GForEcsStmt)
                || (parent instanceof GComponentBinding compBinding && compBinding.getNameIdentifier() == element)
                || (parent instanceof GOnUpdateDeclaration update && update.getNameIdentifier() == element)
                || (parent instanceof GOnEventDeclaration event && event.getNameIdentifier() == element);
    }
}