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