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

// Intentionally permissive: closing delimiters are optional so the lexer produces a token while the user is still typing the literal.
// Required by SimpleTokenSetQuoteHandler auto-pairing, brace-matcher context detection, and parser error recovery.
// STRING and CHAR are kept single-line to prevent the lexer from greedily merging with quotes on later lines.
// Validation of malformed literals belongs in inspections, not here.
STRING=\"([^\"\\\n\r]|\\.)*\"?
RAW_STRING=r#+\"([^\"]|\"[^#])*(\"#+)? | r\"[^\"]*\"?
CHAR='([^'\\\n\r]|\\.)?'?

FLOAT=-?[0-9]+(_[0-9]+)*\.([0-9]+(_[0-9]+)*)?(e[+-]?[0-9]+(_[0-9]+)*)?(f32|f64)?
INTEGER=-?(0x[0-9a-fA-F]+(_[0-9a-fA-F]+)*|0b[01]+(_[01]+)*|0o[0-7]+(_[0-7]+)*|[0-9]+(_[0-9]+)*)(u8|u16|u32|u64|u128|i8|i16|i32|i64|i128|usize|isize)?

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

  "true"              { return RonTypes.TRUE; }
  "false"             { return RonTypes.FALSE; }
  "Some"              { return RonTypes.SOME; }
  "None"              { return RonTypes.NONE; }

  {FLOAT}             { return RonTypes.FLOAT; }
  {INTEGER}           { return RonTypes.INTEGER; }
  {STRING}            { return RonTypes.STRING; }
  {RAW_STRING}        { return RonTypes.RAW_STRING; }
  {CHAR}              { return RonTypes.CHAR; }
  {IDENTIFIER}        { return RonTypes.IDENTIFIER; }

  [^]                 { return TokenType.BAD_CHARACTER; }
}
