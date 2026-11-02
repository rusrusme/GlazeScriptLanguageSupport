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
                || type == GTypes.ENUM_DECLARATION || type == GTypes.SWITCH_STMT) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public @Nullable Indent getIndent() {
        ASTNode parent = myNode.getTreeParent();
        if (parent == null) return Indent.getNoneIndent();

        IElementType parentType = parent.getElementType();
        IElementType myType = myNode.getElementType();

        if (parentType == GTypes.BLOCK || parentType == GTypes.STRUCT_DECLARATION
                || parentType == GTypes.COMPONENT_DECLARATION || parentType == GTypes.SYSTEM_DECLARATION
                || parentType == GTypes.ENUM_DECLARATION) {
            if (myType == GTypes.LBRACE || myType == GTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }
        return Indent.getNoneIndent();
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