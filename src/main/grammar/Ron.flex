package com.github.unclepomedev.ronassist.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.github.unclepomedev.ronassist.psi.RonTypes;
import com.intellij.psi.TokenType;

%%

%public
%class RonLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
%eof}

WHITE_SPACE=[\ \t\n\r\f]+
LINE_COMMENT="//"[^\r\n]*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*\*+"/"

IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
RAW_IDENTIFIER=r#[a-zA-Z0-9_.+-]+

// Intentionally permissive: closing delimiters are optional so the lexer produces a token while the user is still typing the literal.
// Required by SimpleTokenSetQuoteHandler auto-pairing, brace-matcher context detection, and parser error recovery.
// STRING and CHAR are kept single-line to prevent the lexer from greedily merging with quotes on later lines.
// Validation of malformed literals belongs in inspections, not here.
STRING_CONTENT=([^\"\\\n\r]|\\.)*
STRING=\"{STRING_CONTENT}\"?
BYTE_STRING=b\"{STRING_CONTENT}\"?

RAW_STRING_HASHED=r#+\"([^\"]|\"[^#])*(\"#+)?
RAW_STRING_SIMPLE=r\"[^\"]*\"?
RAW_STRING=({RAW_STRING_HASHED}|{RAW_STRING_SIMPLE})

RAW_BYTE_STRING_HASHED=b{RAW_STRING_HASHED}
RAW_BYTE_STRING_SIMPLE=b{RAW_STRING_SIMPLE}
RAW_BYTE_STRING=({RAW_BYTE_STRING_HASHED}|{RAW_BYTE_STRING_SIMPLE})

CHAR_CONTENT=([^'\\\n\r]|\\.)
CHAR='({CHAR_CONTENT})?'?

DIGIT=[0-9]
HEX_DIGIT=[0-9a-fA-F]
OCT_DIGIT=[0-7]
BIN_DIGIT=[01]

DEC_DIGITS={DIGIT}+(_{DIGIT}+)*
HEX_DIGITS={HEX_DIGIT}+(_{HEX_DIGIT}+)*
OCT_DIGITS={OCT_DIGIT}+(_{OCT_DIGIT}+)*
BIN_DIGITS={BIN_DIGIT}+(_{BIN_DIGIT}+)*

EXPONENT_ANY=[eE][+-]?{DEC_DIGITS}
FLOAT_SUFFIX=f32|f64
INT_SUFFIX=u8|u16|u32|u64|u128|i8|i16|i32|i64|i128|usize|isize

FLOAT_WITH_DOT=-?{DEC_DIGITS}\.({DEC_DIGITS})?({EXPONENT_ANY})?
FLOAT_EXP_ONLY=-?{DEC_DIGITS}{EXPONENT_ANY}
FLOAT_LEADING_DOT=-?\.{DEC_DIGITS}({EXPONENT_ANY})?
FLOAT_NEG_INF=-inf
FLOAT=({FLOAT_WITH_DOT}|{FLOAT_EXP_ONLY}|{FLOAT_LEADING_DOT})({FLOAT_SUFFIX})?

INT_HEX=0x{HEX_DIGITS}
INT_BIN=0b{BIN_DIGITS}
INT_OCT=0o{OCT_DIGITS}
INT_DEC={DEC_DIGITS}
INTEGER=-?({INT_HEX}|{INT_BIN}|{INT_OCT}|{INT_DEC})({INT_SUFFIX})?

%%

<YYINITIAL> {
  {WHITE_SPACE}       { return TokenType.WHITE_SPACE; }
  {LINE_COMMENT}      { return RonTypes.LINE_COMMENT; }
  {BLOCK_COMMENT}     { return RonTypes.BLOCK_COMMENT; }

  "{"                 { return RonTypes.LBRACE; }
  "}"                 { return RonTypes.RBRACE; }
  "("                 { return RonTypes.LPAREN; }
  ")"                 { return RonTypes.RPAREN; }
  "["                 { return RonTypes.LBRACK; }
  "]"                 { return RonTypes.RBRACK; }
  ":"                 { return RonTypes.COLON; }
  ","                 { return RonTypes.COMMA; }
  "#"                 { return RonTypes.HASH; }
  "!"                 { return RonTypes.EXCL; }

  "true"              { return RonTypes.TRUE; }
  "false"             { return RonTypes.FALSE; }
  "Some"              { return RonTypes.SOME; }
  "None"              { return RonTypes.NONE; }

  {FLOAT_NEG_INF}     { return RonTypes.FLOAT; }
  {FLOAT}             { return RonTypes.FLOAT; }
  {INTEGER}           { return RonTypes.INTEGER; }
  {STRING}            { return RonTypes.STRING; }
  {BYTE_STRING}       { return RonTypes.BYTE_STRING; }
  {RAW_STRING}        { return RonTypes.RAW_STRING; }
  {RAW_BYTE_STRING}   { return RonTypes.RAW_BYTE_STRING; }
  {CHAR}              { return RonTypes.CHAR; }
  {RAW_IDENTIFIER}    { return RonTypes.RAW_IDENTIFIER; }
  {IDENTIFIER}        { return RonTypes.IDENTIFIER; }

  [^]                 { return TokenType.BAD_CHARACTER; }
}
