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

package net.rusrusme.glaze.core;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GFileType extends LanguageFileType {
    public static final GFileType INSTANCE = new GFileType();

    protected GFileType() {
        super(GLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Glaze Script File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Glaze Script File";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "gscript";
    }

    @Override
    public @Nullable Icon getIcon() {
        return GIcons.FILE;
    }
}
