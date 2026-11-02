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

package net.rusrusme.glaze.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.glaze.psi.GTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GBlock extends AbstractBlock {
    private final SpacingBuilder spacingBuilder;

    protected GBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE && child.getTextLength() > 0) {
                blocks.add(new GBlock(child, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType type = myNode.getElementType();
        if (type == GTypes.BLOCK || type == GTypes.STRUCT_DECLARATION
                || type == GTypes.COMPONENT_DECLARATION || type == GTypes.SYSTEM_DECLARATION
                || type == GTypes.ENUM_DECLARATION || type == GTypes.SWITCH_STMT
                || type == GTypes.FUNCTION_DECLARATION) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public @Nullable Indent getIndent() {
        ASTNode parentNode = myNode.getTreeParent();
        if (parentNode == null) {
            return Indent.getNoneIndent();
        }

        IElementType parentType = parentNode.getElementType();
        IElementType elementType = myNode.getElementType();

        // 1. Statements inside blocks { ... }
        if (parentType == GTypes.BLOCK) {
            if (elementType == GTypes.LBRACE || elementType == GTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        // 2. Members inside struct, component, system, enum declarations
        if (parentType == GTypes.STRUCT_DECLARATION
                || parentType == GTypes.COMPONENT_DECLARATION
                || parentType == GTypes.SYSTEM_DECLARATION
                || parentType == GTypes.ENUM_DECLARATION) {
            if (isInsideBraces(parentNode, myNode)) {
                return Indent.getNormalIndent();
            }
            return Indent.getNoneIndent();
        }

        return Indent.getNoneIndent();
    }

    /**
     * Ensures only elements strictly between { and } get indented.
     * Prevents the declaration headers ('system Foo', 'struct Bar') from shifting.
     */
    private boolean isInsideBraces(@NotNull ASTNode parentNode, @NotNull ASTNode childNode) {
        IElementType type = childNode.getElementType();
        if (type == GTypes.LBRACE || type == GTypes.RBRACE) {
            return false;
        }
        ASTNode lBrace = parentNode.findChildByType(GTypes.LBRACE);
        if (lBrace == null || childNode.getStartOffset() <= lBrace.getStartOffset()) {
            return false;
        }
        ASTNode rBrace = parentNode.findChildByType(GTypes.RBRACE);
        return rBrace == null || childNode.getStartOffset() < rBrace.getStartOffset();
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}