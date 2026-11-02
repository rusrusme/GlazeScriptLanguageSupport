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