
grammar Minitestlang;

@header {
import org.minitestlang.listener.minitestlang.result.ResultExpr;
import org.minitestlang.listener.minitestlang.result.ResultDeclVar;
import org.minitestlang.listener.minitestlang.result.ResultClass;
import org.minitestlang.listener.minitestlang.result.ResultMethod;
import org.minitestlang.listener.minitestlang.result.ResultInstr;
}

classDef returns [ResultClass result] :
    CLASS Identifier LBRACE 
      (method )*
    RBRACE
    ;

method returns [ResultMethod result] : typeMethod Identifier LPAREN RPAREN
    LBRACE
    ( instr  )*
    RBRACE
    ;

instr returns [ResultInstr result] :
    type decl_var ( ',' decl_var )* SEMI # DeclareVariable
    | Identifier ASSIGN expression SEMI # Affect
    | IF parExpression instr (ELSE instr)?  # IfInstr
    | WHILE parExpression instr  # WhileInstr
    | LBRACE (instr)* RBRACE  # BlockInstr
    | Identifier LPAREN ( expression ( COMMA expression )* )? RPAREN SEMI # AppelInstr
    ;

parExpression returns [ResultExpr expr]:
    LPAREN expression RPAREN
    ;

decl_var returns [ResultDeclVar expr] :
    Identifier (ASSIGN expression)?
    ;


expression returns [ResultExpr expr] :
    LPAREN expression RPAREN # ParentExpr
    | expression op=(MUL|DIV) expression  # OpMultDiv
    | expression op=(ADD|SUB|MOD) expression  # OpPlusMinus
    | expression op=(AND|OR) expression  # OpAndOr
    | expression op=(EQUAL|NOTEQUAL|LE|GE|GT|LT) expression  # OpEquals
    | Number                         # Number
    | (TRUE|FALSE)                       # BooleanValue
    | Identifier                         # Ident
    | StringLiteral          # Str
    ;

StringLiteral
	:	'"' StringCharacters? '"'
	;

fragment
StringCharacters
	:	StringCharacter+
	;

fragment
StringCharacter
	:	~["\\]
	|	EscapeSequence
	;

fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	|	OctalEscape
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
	;

fragment
OctalEscape
	:	'\\' OctalDigit
	|	'\\' OctalDigit OctalDigit
	|	'\\' ZeroToThree OctalDigit OctalDigit
	;

fragment
ZeroToThree
	:	[0-3]
	;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;


fragment
OctalDigits
	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
	;

fragment
OctalDigit
	:	[0-7]
	;


fragment
OctalDigitsAndUnderscores
	:	OctalDigitOrUnderscore+
	;

fragment
OctalDigitOrUnderscore
	:	OctalDigit
	|	'_'
	;

fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;

fragment
HexDigit
	:	[0-9a-fA-F]
	;

fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;


type: BOOLEAN | INT ;

typeMethod: type | VOID ;

main: classDef;

IF : 'if';
BOOLEAN : 'boolean';
CLASS : 'class';
INT : 'int';
VOID : 'void';
TRUE : 'true';
FALSE : 'false';
WHILE        : 'while';
ELSE         : 'else';

Number: [0-9]+ ;

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMI : ';';
COMMA : ',';
DOT : '.';


ASSIGN : '=';
GT : '>';
LT : '<';
BANG : '!';
EQUAL : '==';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';
INC : '++';
DEC : '--';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
MOD : '%';

Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment
JavaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;


WS  :  [ \t\r\n\u000C]+ -> channel(HIDDEN)
    ;

COMMENT
    :   '/*' .*? '*/' -> channel(HIDDEN)
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> channel(HIDDEN)
    ;


