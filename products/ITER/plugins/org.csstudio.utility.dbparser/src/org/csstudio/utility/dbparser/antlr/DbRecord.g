grammar DbRecord;

options {
  language = Java;
  output = AST;
}

tokens {
  RECORD_INSTANCE = 'record_instance';
  RECORD = 'record';
  RECORD_BODY = 'record_body';
  FIELD = 'field';
  INFO = 'info';
  ALIAS = 'alias';
  TYPE = 'type';
  VALUE = 'value';
}

@header {
package org.csstudio.utility.dbparser.antlr;
}

@lexer::header {
package org.csstudio.utility.dbparser.antlr;
}

top : program;

program : record*;

record  : record_head record_block -> ^('record_instance' record_head record_block);

record_head : 'record' '(' record_name ')' -> ^(RECORD record_name);

record_block : '{' record_body* '}'  -> ^('record_body' record_body*);

record_body : field | info | alias;

field : 'field' '(' key_value ')' -> ^(FIELD key_value);

info : 'info' '(' key_value ')' -> ^(INFO key_value);

alias : 'alias' '(' type ')' -> ^(ALIAS type);

key_value : type ',' value -> ^(TYPE type) ^(VALUE value);

record_name : type ',' name -> ^(TYPE type) ^(VALUE name);

type : ID;

value : String;

name : String | NonQuotedString;

OCTAL_ESC
  :
  '\\' ('0'..'3') ('0'..'7') ('0'..'7')
  | '\\' ('0'..'7') ('0'..'7')
  | '\\' ('0'..'7')
  ;
  
  HEX_DIGIT
  :
  (
    '0'..'9'
    | 'a'..'f'
    | 'A'..'F'
  )
  ;
  
  UNICODE_ESC
  :
  '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
  ;
  
WHITESPACE
  :
  (
    '\t'
    | ' '
    | '\r'
    | '\n'
    | '\u000C'
  )+
  
  {
   $channel = HIDDEN;
  }
  ;

ID
  :
  (
    'a'..'z'
    | 'A'..'Z'
    | '_'
  )
  (
    'a'..'z'
    | 'A'..'Z'
    | '0'..'9'
    | '_'
    | '-'
  )*
  ;

INT
  :
  '0'..'9'+
  ;

FLOAT
  :
  ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
  | '.' ('0'..'9')+ EXPONENT?
  | ('0'..'9')+ EXPONENT
  ;

COMMENT
  :
  '#'
  ~(
    '\n'
    | '\r'
   )*
  '\r'? '\n' 
            {
             $channel = HIDDEN;
            }
  ;

String
  :
  '"'
  (
    ESC_SEQ
    |
    ~(
      '\\'
      | '"'
     )
  )*
  '"'
  ;
  
NonQuotedString
  :
  (   
    ('0'..'9')
    |
    ('A'..'Z')
    |
    ('a'..'z')
    |
    ':'
    |
    '-'
  )+
  ;
  
  ESC_SEQ
  :
  '\\'
  (
    'b'
    | 't'
    | 'n'
    | 'f'
    | 'r'
    | '\"'
    | '\''
    | '\\'
  )
  | UNICODE_ESC
  | OCTAL_ESC
  ;
  
  

EXPONENT
  :
  (
    'e'
    | 'E'
  )
  (
    '+'
    | '-'
  )?
  ('0'..'9')+
  ;