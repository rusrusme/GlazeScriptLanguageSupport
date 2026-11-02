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

package net.rusrusme.glaze.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import net.rusrusme.glaze.core.GIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class GColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keywords", GSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Types", GSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Intrinsics", GSyntaxHighlighter.INTRINSIC),
            new AttributesDescriptor("Numbers", GSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Strings", GSyntaxHighlighter.STRING),
            new AttributesDescriptor("Operators", GSyntaxHighlighter.OPERATION_SIGN),
            new AttributesDescriptor("Comments", GSyntaxHighlighter.COMMENT),
    };

    @Override public @Nullable Icon getIcon() { return GIcons.FILE; }
    @Override public @NotNull SyntaxHighlighter getHighlighter() { return new GSyntaxHighlighter(); }
    @Override public AttributesDescriptor @NotNull [] getAttributeDescriptors() { return DESCRIPTORS; }
    @Override public ColorDescriptor @NotNull [] getColorDescriptors() { return ColorDescriptor.EMPTY_ARRAY; }
    @Override public @NotNull String getDisplayName() { return "GlazeScript"; }
    @Override public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() { return null; }

    @Override
    public @NotNull String getDemoText() {
        return """
                // GlazeScript Example
                component Velocity {
                    vec3 direction;
                    float speed = 5.0;
                }

                system MovementSystem {
                    on_update(float dt) {
                        for (entity e, Velocity v) {
                            v.speed += 1.0;
                        }
                    }
                }
                """;
    }
}