package net.rusrusme.glaze.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.glaze.psi.GTypes;

%%

%public
%class GLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  return;
%eof}

WHITE_SPACE=[\ \t\r\n]+
COMMENT="#"[^\r\n]*

IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
INT_LITERAL=[0-9]+
FLOAT_LITERAL=[0-9]+\.[0-9]+([fF])?
STRING_LITERAL=\"([^\\\"\r\n]|\\.)*\"?
CHAR_LITERAL='([^\\'\r\n]|\\.)'?

%%

<YYINITIAL> {
  {WHITE_SPACE}         { return TokenType.WHITE_SPACE; }
  {COMMENT}             { return GTypes.COMMENT; }

  // Declarations & ECS Keywords
  "struct"              { return GTypes.KEYWORD_STRUCT; }
  "component"           { return GTypes.KEYWORD_COMPONENT; }
  "enum"                { return GTypes.KEYWORD_ENUM; }
  "system"              { return GTypes.KEYWORD_SYSTEM; }
  "fn"                  { return GTypes.KEYWORD_FN; }
  "on_update"           { return GTypes.KEYWORD_ON_UPDATE; }
  "on_event"            { return GTypes.KEYWORD_ON_EVENT; }
  "let"                 { return GTypes.KEYWORD_LET; }
  "var"                 { return GTypes.KEYWORD_VAR; }

  // Control Flow
  "if"                  { return GTypes.KEYWORD_IF; }
  "else"                { return GTypes.KEYWORD_ELSE; }
  "switch"              { return GTypes.KEYWORD_SWITCH; }
  "case"                { return GTypes.KEYWORD_CASE; }
  "default"             { return GTypes.KEYWORD_DEFAULT; }
  "for"                 { return GTypes.KEYWORD_FOR; }
  "while"               { return GTypes.KEYWORD_WHILE; }
  "do"                  { return GTypes.KEYWORD_DO; }
  "return"              { return GTypes.KEYWORD_RETURN; }
  "break"               { return GTypes.KEYWORD_BREAK; }
  "continue"            { return GTypes.KEYWORD_CONTINUE; }
  "exclude"             { return GTypes.KEYWORD_EXCLUDE; }

  // Primitives
  "void"                { return GTypes.TYPE_VOID; }
  "uint8"               { return GTypes.TYPE_UINT8; }
  "int8"                { return GTypes.TYPE_INT8; }
  "uint16"              { return GTypes.TYPE_UINT16; }
  "int16"               { return GTypes.TYPE_INT16; }
  "uint32"              { return GTypes.TYPE_UINT32; }
  "int32"               { return GTypes.TYPE_INT32; }
  "uint64"              { return GTypes.TYPE_UINT64; }
  "int64"               { return GTypes.TYPE_INT64; }
  "float"               { return GTypes.TYPE_FLOAT; }
  "double"              { return GTypes.TYPE_DOUBLE; }
  "char"                { return GTypes.TYPE_CHAR; }
  "string"              { return GTypes.TYPE_STRING; }
  "bool"                { return GTypes.TYPE_BOOL; }

  // Math Types
  "vec2"                { return GTypes.TYPE_VEC2; }
  "vec3"                { return GTypes.TYPE_VEC3; }
  "vec4"                { return GTypes.TYPE_VEC4; }
  "mat2"                { return GTypes.TYPE_MAT2; }
  "mat3"                { return GTypes.TYPE_MAT3; }
  "mat4"                { return GTypes.TYPE_MAT4; }
  "quat"                { return GTypes.TYPE_QUAT; }

  // Builtin ECS & Containers
  "entity"              { return GTypes.TYPE_ENTITY; }
  "event"               { return GTypes.TYPE_EVENT; }
  "list"                { return GTypes.KEYWORD_LIST; }

  // Intrinsics
  "get"                 { return GTypes.INTRINSIC_GET; }
  "add"                 { return GTypes.INTRINSIC_ADD; }
  "has"                 { return GTypes.INTRINSIC_HAS; }
  "remove_at"           { return GTypes.INTRINSIC_REMOVE_AT; }
  "remove"              { return GTypes.INTRINSIC_REMOVE; }
  "clear"               { return GTypes.INTRINSIC_CLEAR; }

  // Literals
  "true"                { return GTypes.BOOL_TRUE; }
  "false"               { return GTypes.BOOL_FALSE; }
  {FLOAT_LITERAL}       { return GTypes.FLOAT_LITERAL; }
  {INT_LITERAL}         { return GTypes.INT_LITERAL; }
  {STRING_LITERAL}      { return GTypes.STRING_LITERAL; }
  {CHAR_LITERAL}        { return GTypes.CHAR_LITERAL; }

  // Multi-char Operators
  "->"                  { return GTypes.ARROW; }
  "::"                  { return GTypes.SCOPE; }
  "=="                  { return GTypes.EQ; }
  "!="                  { return GTypes.NEQ; }
  "<="                  { return GTypes.LTE; }
  ">="                  { return GTypes.GTE; }
  "&&"                  { return GTypes.AND; }
  "||"                  { return GTypes.OR; }
  "++"                  { return GTypes.INC; }
  "--"                  { return GTypes.DEC; }
  "+="                  { return GTypes.ADD_ASSIGN; }
  "-="                  { return GTypes.SUB_ASSIGN; }
  "*="                  { return GTypes.MUL_ASSIGN; }
  "/="                  { return GTypes.DIV_ASSIGN; }
  "%="                  { return GTypes.MOD_ASSIGN; }

  // Single-char Operators & Punctuation
  "="                   { return GTypes.ASSIGN; }
  "<"                   { return GTypes.LT; }
  ">"                   { return GTypes.GT; }
  "!"                   { return GTypes.NOT; }
  "+"                   { return GTypes.PLUS; }
  "-"                   { return GTypes.MINUS; }
  "*"                   { return GTypes.MUL; }
  "/"                   { return GTypes.DIV; }
  "%"                   { return GTypes.MOD; }
  ":"                   { return GTypes.COLON; }
  ";"                   { return GTypes.SEMICOLON; }
  ","                   { return GTypes.COMMA; }
  "."                   { return GTypes.DOT; }
  "{"                   { return GTypes.LBRACE; }
  "}"                   { return GTypes.RBRACE; }
  "("                   { return GTypes.LPAREN; }
  ")"                   { return GTypes.RPAREN; }
  "["                   { return GTypes.LBRACK; }
  "]"                   { return GTypes.RBRACK; }

  {IDENTIFIER}          { return GTypes.IDENTIFIER; }
}

[^] { return TokenType.BAD_CHARACTER; }