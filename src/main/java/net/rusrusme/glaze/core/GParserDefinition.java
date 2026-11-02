package net.rusrusme.glaze.core;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import net.rusrusme.glaze.lexer.GLexer;
import net.rusrusme.glaze.parser.GParser;
import net.rusrusme.glaze.psi.GTypes;
import org.jetbrains.annotations.NotNull;

public class GParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(GLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FlexAdapter(new GLexer(null));
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new GParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(GTypes.COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(GTypes.STRING_LITERAL);
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return GTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new GFile(viewProvider);
    }
}