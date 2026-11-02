package net.rusrusme.glaze.highlight;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import net.rusrusme.glaze.lexer.GLexer;
import net.rusrusme.glaze.psi.GTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class GSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("GLAZE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey TYPE =
            createTextAttributesKey("GLAZE_TYPE", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("GLAZE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("GLAZE_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey OPERATION_SIGN =
            createTextAttributesKey("GLAZE_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("GLAZE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey INTRINSIC =
            createTextAttributesKey("GLAZE_INTRINSIC", DefaultLanguageHighlighterColors.STATIC_METHOD);

    private static final TokenSet KEYWORDS = TokenSet.create(
            GTypes.KEYWORD_STRUCT, GTypes.KEYWORD_COMPONENT, GTypes.KEYWORD_ENUM,
            GTypes.KEYWORD_SYSTEM, GTypes.KEYWORD_FN, GTypes.KEYWORD_ON_UPDATE,
            GTypes.KEYWORD_ON_EVENT, GTypes.KEYWORD_LET, GTypes.KEYWORD_VAR,
            GTypes.KEYWORD_IF, GTypes.KEYWORD_ELSE, GTypes.KEYWORD_SWITCH,
            GTypes.KEYWORD_CASE, GTypes.KEYWORD_DEFAULT, GTypes.KEYWORD_FOR,
            GTypes.KEYWORD_WHILE, GTypes.KEYWORD_DO, GTypes.KEYWORD_RETURN,
            GTypes.KEYWORD_BREAK, GTypes.KEYWORD_CONTINUE, GTypes.KEYWORD_EXCLUDE,
            GTypes.BOOL_TRUE, GTypes.BOOL_FALSE
    );

    private static final TokenSet TYPES = TokenSet.create(
            GTypes.TYPE_VOID, GTypes.TYPE_UINT8, GTypes.TYPE_INT8, GTypes.TYPE_UINT16,
            GTypes.TYPE_INT16, GTypes.TYPE_UINT32, GTypes.TYPE_INT32, GTypes.TYPE_UINT64,
            GTypes.TYPE_INT64, GTypes.TYPE_FLOAT, GTypes.TYPE_DOUBLE, GTypes.TYPE_CHAR,
            GTypes.TYPE_STRING, GTypes.TYPE_BOOL, GTypes.TYPE_VEC2, GTypes.TYPE_VEC3,
            GTypes.TYPE_VEC4, GTypes.TYPE_MAT2, GTypes.TYPE_MAT3, GTypes.TYPE_MAT4,
            GTypes.TYPE_QUAT, GTypes.TYPE_ENTITY, GTypes.TYPE_EVENT, GTypes.KEYWORD_LIST
    );

    private static final TokenSet INTRINSICS = TokenSet.create(
            GTypes.INTRINSIC_GET, GTypes.INTRINSIC_ADD, GTypes.INTRINSIC_HAS,
            GTypes.INTRINSIC_REMOVE, GTypes.INTRINSIC_REMOVE_AT, GTypes.INTRINSIC_CLEAR
    );

    private static final TokenSet OPERATORS = TokenSet.create(
            GTypes.ARROW, GTypes.SCOPE, GTypes.EQ, GTypes.NEQ, GTypes.LTE, GTypes.GTE,
            GTypes.LT, GTypes.GT, GTypes.AND, GTypes.OR, GTypes.NOT, GTypes.PLUS,
            GTypes.MINUS, GTypes.MUL, GTypes.DIV, GTypes.MOD, GTypes.INC, GTypes.DEC,
            GTypes.ASSIGN, GTypes.ADD_ASSIGN, GTypes.SUB_ASSIGN, GTypes.MUL_ASSIGN,
            GTypes.DIV_ASSIGN, GTypes.MOD_ASSIGN
    );

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FlexAdapter(new GLexer(null));
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (KEYWORDS.contains(tokenType)) return pack(KEYWORD);
        if (TYPES.contains(tokenType)) return pack(TYPE);
        if (INTRINSICS.contains(tokenType)) return pack(INTRINSIC);
        if (OPERATORS.contains(tokenType)) return pack(OPERATION_SIGN);
        if (tokenType.equals(GTypes.INT_LITERAL) || tokenType.equals(GTypes.FLOAT_LITERAL)) return pack(NUMBER);
        if (tokenType.equals(GTypes.STRING_LITERAL) || tokenType.equals(GTypes.CHAR_LITERAL)) return pack(STRING);
        if (tokenType.equals(GTypes.COMMENT)) return pack(COMMENT);
        return TextAttributesKey.EMPTY_ARRAY;
    }
}