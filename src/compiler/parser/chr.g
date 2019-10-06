// TODO deze commentaar stamt nog van een zeeeeeeer oude versie ;-)
/*
 * @author Peter Van Weert
 *
 * Wijzigingen / verbeteringen tov JCHR:
 *  o Gegenereerde klassen zullen in het juiste pakket zitten,
 *		niet langer in default (dit is niet echt concrete 
 *		syntax-verbetering) 
 *
 *	o Door gebruik te maken van een aantal lexer-constructies 
 *		uit de nieuwste antlr-java-language-definitie zijn een
 *		aantal dingen verbeterd ten opzichte van JCHR:
 *			- string- en character-literals kunnen 
 *				escaped characters bevatten (ook \" of \' dus!)
 *			- herkennen van numerische literals is er 
 *				sterk op vooruitgegaan
 *
 *	o Door het gebruiken van een grotere charVocabulary kunnen
 *		strings, commentaar, etc speciale tekens bevatten 
 *		zoals |, letters met accenten, etc
 *
 *	o Door de herdefinitie van de syntax van een regel is deze
 *		nu veel sterker gelijkend op die van de gebruikelijke.
 *		De reden dat dit niet aanvankelijk zo werd gedaan zou
 *		iets te maken kunnen hebben met het feit dat dit de 
 *		syntax-definitie wel wat bemoeilijkt, omdat een aantal
 *		syntactic predicates moeten ingevoerd om ambiguiteiten
 *		weg te werken. De "offici?le" reden om het meer java-
 *		like te maken lijkt ons niet overtuigend:
 *			- bemoeilijkt sterk het lezen van de regels
 *				als men de normale syntax gewoon is
 *			- bemoeilijkt het porten van CHR-programma's
 *
 * o Regels zonder naam krijgen in parsing-fase een naam
 *		rule_<teller>, zodat deze achteraf op net dezelfde
 *		wijze kunnen worden behandeld.
 */
header {
package compiler.parser;
}

{
import compiler.CHRIntermediateForm.builder.ICHRIntermediateFormBuilder;
import compiler.CHRIntermediateForm.builder.ICHRIntermediateFormDirector;

import static compiler.CHRIntermediateForm.rulez.RuleType.PROPAGATION;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPAGATION;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPLIFICATION;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.UNNAMED_PACKAGE_ID;

import util.builder.BuilderException;

import compiler.options.Options;
import compiler.options.OptionsException;
}

class CHRParser extends Parser;

options {
	k=4;
	exportVocab=CHR;
	
	noConstructors=true;
	classHeaderPrefix="@SuppressWarnings(CHRParser.ALL) public final";
	classHeaderSuffix="ICHRIntermediateFormDirector";
}

tokens {
	TYPECAST;
	METHODORCONSTRAINT;	
	EMPTY;
}

{
	final static String ALL = "all";
	
	public CHRParser(TokenBuffer tokenBuf, ICHRIntermediateFormBuilder<?> builder, Options opts) {
		super(tokenBuf, 1);
		tokenNames = _tokenNames;
		init(builder, opts);
	}
	
	public CHRParser(TokenStream tokenStr, ICHRIntermediateFormBuilder<?> builder, Options opts) {
		super(tokenStr, 1);
		tokenNames = _tokenNames;
		init(builder, opts);
	}
	
	public void init(ICHRIntermediateFormBuilder<?> builder, Options opts) {
		setBuilder(builder);
		setOptions(opts);
	}
	
	private Options opts;
	
	protected void setOptions(Options opts) {
		this.opts = opts;	
	}
	
	protected Options getOptions() {
		return opts;	
	}
	
	private ICHRIntermediateFormBuilder<?> builder;
	
	public void setBuilder(ICHRIntermediateFormBuilder<?> builder) {
		this.builder = builder;
	}
	
	public ICHRIntermediateFormBuilder<?> getBuilder() {
		return builder;	
	}	
	
	public final void construct() throws BuilderException {
		if (getBuilder() == null) {
			throw new BuilderException("Builder not set");
		}
		
		getBuilder().init();
		
		try {
			compilationUnit();
		} catch (RecognitionException re) {
			abort(re);
		} catch (TokenStreamException tse) {
		 	abort(tse);
		} catch (OptionsException oe) {
			abort(oe);
		}
				
		getBuilder().finish();
	}
	
	protected void abort(Exception reason) throws BuilderException {
		getBuilder().abort();
		throw new BuilderException(reason);
	}
}

protected option throws OptionsException
{String optionName = null; }
:
	OPTION LPAREN ( optionName=simpleID | "debug" { optionName="debug"; } )
		( ( COMMA ) => binaryOption[optionName]
				|  { getOptions().processOptions('-' + optionName); } )
	RPAREN SEMICOLON
;

protected binaryOption[String optionName] throws OptionsException
{ String value; }
:
	COMMA value=optionValue
	{ getOptions().processOptions('-' + optionName, value); }
;

private optionValue returns [String result]
{ result = null; }
:
	s:STRING_LITERAL { result = s.getText(); }
	 | n:NUM_INT { result = n.getText(); }
	 | f:NUM_FLOAT { result = f.getText(); }
	 | d:NUM_DOUBLE { result = d.getText(); }
	 | result = anID
	 | ( TRUE | ON) { result = "true"; }
	 | ( FALSE | OFF) { result = "false"; }
;

protected compilationUnit throws OptionsException, BuilderException
:
	packageDeclaration
	imports
	handler
;

protected packageDeclaration throws BuilderException
{ String packageId = UNNAMED_PACKAGE_ID; }
:
	( PACKAGE packageId=anID SEMICOLON )?
	{ getBuilder().buildPackageDeclaration(packageId); }
;

protected imports throws BuilderException
:
	{ getBuilder().beginImports(); }
	(IMPORT (
		  importSingleType 
		| importOnDemand
		| importSingleStatic
		| importStaticOnDemand
	) )*
	{ getBuilder().endImports(); }
;


protected handler throws BuilderException, OptionsException
{ String access; }
:	
	access = accessModifier
	HANDLER handler:SIMPLE_ID 
			{ getBuilder().beginHandler(handler.getText(), access); }

			(typeParameters)?
				
		LCURLY
			declarations
			
			ruleDefinitions
		RCURLY
		
		{ getBuilder().endHandler(); }
		EOF
;

protected typeParameter throws BuilderException
{String sid; }
:
	 sid=simpleID 
	 { getBuilder().beginTypeParameter(sid); }
	 (EXTENDS upperBound (AMPERCENT upperBound)*)?
	 { getBuilder().endTypeParameter(); }
;

protected declarations throws BuilderException, OptionsException
{ String access; }
:
	{ getBuilder().beginDeclarations(); }
		(
			( 
				access=accessModifier
				( solverDeclaration[access] | constraintDeclaration[access] )
			)
		 |	option
		)+
	{ getBuilder().endDeclarations(); }
;

protected upperBound throws BuilderException
{ String id; }
:
	id=anID
	{ getBuilder().beginUpperBound(id); }
		( typeArguments )?
	{ getBuilder().endUpperBound(); }
;

protected typeParameters throws BuilderException:
	LT { getBuilder().beginTypeParameters(); }
		(typeParameter (COMMA typeParameter)*)?
	GT  { getBuilder().endTypeParameters(); }
;

protected simpleID returns [String result]
{ result = null; }
:
	id:SIMPLE_ID { result = id.getText(); }
;

protected composedID returns [String result]
{ result = null; }
:
	id:ID { result = id.getText(); }
;

protected anID returns [String result]
{ result = null; }
:
	result = composedID	| result = simpleID
;

protected accessModifier returns [String result]
{ result = "default"; }
:
	( PUBLIC { result = "public"; } 
	| PROTECTED { result = "protected"; } 
	| PRIVATE { result = "private"; }
	| LOCAL { result = "local"; }
	)?
;

protected importSingleType throws BuilderException
{ String id; }
:
	id=anID SEMICOLON
	{ getBuilder().importSingleType(id); }
;

protected importOnDemand throws BuilderException
:
	id:ON_DEMAND_ID SEMICOLON
	{ getBuilder().importOnDemand(id.getText()); }
;

protected importSingleStatic throws BuilderException
{ String id; }
:
	STATIC id=anID SEMICOLON
	{ getBuilder().importSingleStatic(id); }
;

protected importStaticOnDemand throws BuilderException
:
	STATIC id:ON_DEMAND_ID SEMICOLON
	{ getBuilder().importStaticOnDemand(id.getText()); }
;

protected solverDeclaration[String access] throws BuilderException
{ String interf, id = null; }
:	
	SOLVER interf=anID
		{ getBuilder().beginSolverDeclaration(interf, access); }
	( typeArguments )?
	(id=simpleID 
		(EQ 
		{ getBuilder().beginSolverDefault(); }
		argument
		{ getBuilder().endSolverDefault(); }
		)?
	)?
		{ getBuilder().buildSolverName(id); }
	SEMICOLON
		{ getBuilder().endSolverDeclaration(); }
;

protected typeArguments throws BuilderException
:
	{ getBuilder().beginTypeArguments(); }
	LT typeArgument (COMMA typeArgument)* GT
	{ getBuilder().endTypeArguments(); }
;

protected typeArgument throws BuilderException
{ String id; }
:	
	id=anID 
	{ getBuilder().beginTypeArgument(id); }
	( typeParameters )?
	{ getBuilder().endTypeArgument(); }
;


protected constraintDeclarationBody[String access] throws BuilderException
:
	id:SIMPLE_ID { getBuilder().beginConstraintDeclaration(id.getText(), access); }
	( LPAREN constraintArgumentList RPAREN )?
	( INFIX infixDeclaration (COMMA infixDeclaration)* )?
	{ getBuilder().endConstraintDeclaration(); }
;

protected infixDeclaration throws BuilderException
{ String infix; }
:
	infix=infixId { getBuilder().buildInfixIdentifier(infix); }
;

protected constraintDeclaration[String access] throws BuilderException
:
	(CONSTRAINT | "constraints" { System.err.println(
		" --> warning: deprecated syntax 'constraints' (replace with 'constraint')"
	); })
		constraintDeclarationBody[access] (COMMA constraintDeclarationBody[access])*
	SEMICOLON
;

protected constraintArgumentList throws BuilderException:
	(constraintArgument)? (COMMA constraintArgument)*
;

protected constraintArgument throws BuilderException
{ 
	String type, name = null; 
	boolean fixed = false;
}
:
	(FIXED { fixed = true; })?
	type=anID 
	{
		getBuilder().beginConstraintArgument(); 
		getBuilder().beginConstraintArgumentType(type, fixed);
	}
	( typeArguments )?
		{ getBuilder().endConstraintArgumentType(); }
	( name=simpleID )?	
	{ 
		getBuilder().buildConstraintArgumentName(name);
		getBuilder().endConstraintArgument();
	}
;

protected ruleDefinitions throws BuilderException:
	RULES 
		LCURLY { getBuilder().beginRules(); }
			( variableDeclarations |  // XXX only temporary
			 	localVariableDeclarations | rule )*
		RCURLY	{ getBuilder().endRules(); }
;

protected localVariableDeclarations throws BuilderException
{ boolean fixed = false; }
:	
	( LOCAL | ( "variable" { System.err.println(
		" --> warning: deprecated syntax 'variable' (replace with 'local')"
	); }))
	(FIXED { fixed = true; })?
	{ getBuilder().beginLocalVariableDeclarations(); }
    	variableType[fixed]
    	localVariableDeclaration (COMMA localVariableDeclaration)*
    	SEMICOLON
	{ getBuilder().endLocalVariableDeclarations(); }

;

protected localVariableDeclaration throws BuilderException
{ String id; }
:
	id = simpleID
	{ getBuilder().declareLocalVariable(id); }
;


protected variableDeclarations throws BuilderException
{ boolean fixed = false; }
:	
	VARIABLE (FIXED { fixed = true; })?
	{ getBuilder().beginVariableDeclarations(); }
    	variableType[fixed]
    	variableDeclaration (COMMA variableDeclaration)*
    	SEMICOLON
	{ getBuilder().endVariableDeclarations(); }
;

protected variableDeclaration throws BuilderException
{ String id; }
:
	id = simpleID
	{ getBuilder().declareVariable(id); }
;

protected variableType[boolean fixed] throws BuilderException
{String type; }:
	type=anID
	{ getBuilder().beginVariableType(type, fixed); }
		(typeArguments)?
	{ getBuilder().endVariableType(); }
;

protected rule throws BuilderException
:
	{ getBuilder().beginRule(); }
	( 
	(SIMPLE_ID AT)	/* ruleDeclaration */
		=> 	id:SIMPLE_ID AT ruleDefinition[id.getText()]
		|	ruleDefinition[null]	// builder will generate name
	)
	( pragmas )?
	ruleEnd
	{ getBuilder().endRule(); }
;

protected ruleDefinition[String name] throws BuilderException:
	(occurrenceList MINUS) => simpagationDef[name]
		| (occurrenceList ( negativeHead )* SIMP) => simplificationDef[name]
		| propagationDef[name]
		
	{ getBuilder().endRuleDefinition(); }
;

protected pragmas throws BuilderException:
	PRAGMA 
	{ getBuilder().beginPragmas(); }
	pragma (COMMA pragma)*
	{ getBuilder().endPragmas(); }
;

protected pragma throws BuilderException:
	( passivePragma | no_historyPragma | debugPragma )
;

protected passivePragma throws BuilderException:
	"passive" LPAREN passivePragmaId ( COMMA passivePragmaId )* RPAREN	
;

protected passivePragmaId throws BuilderException
{ String id; }
:
	id=simpleID { getBuilder().addPassivePragma(id); }
;

protected no_historyPragma throws BuilderException:
	"no_history" { getBuilder().addNoHistoryPragma(); }
;

protected debugPragma throws BuilderException:
	"debug" { getBuilder().addDebugPragma(); }
;

protected simpagationDef[String name] throws BuilderException:	
	{ getBuilder().beginRuleDefinition(name, SIMPAGATION); }
		
	{ getBuilder().beginPositiveHead(); }
		keptOccurrences MINUS removedOccurrences
	{ getBuilder().endPositiveHead(); }
	
	negativeHeads
	
	SIMP guardNbody
;

protected simplificationDef[String name] throws BuilderException:
	{ getBuilder().beginRuleDefinition(name, SIMPLIFICATION); }
		
	{ getBuilder().beginPositiveHead(); }
		removedOccurrences
	{ getBuilder().endPositiveHead(); }
	
	negativeHeads
	
	SIMP guardNbody
;

protected propagationDef[String name] throws BuilderException:
	{ getBuilder().beginRuleDefinition(name, PROPAGATION); }
	
	{ getBuilder().beginPositiveHead(); }
		keptOccurrences
	{ getBuilder().endPositiveHead(); }
	
	negativeHeads
	
	PROP guardNbody
;

protected negativeHeads throws BuilderException:
	{ getBuilder().beginNegativeHeads();	}
		( negativeHead )*
	{ getBuilder().endNegativeHeads(); }
;

protected negativeHead throws BuilderException:
	DOUBLE_MINUS 
	{ getBuilder().beginNegativeHead();	}
		negativeOccurrences	( negativeGuard )?
	{ getBuilder().endNegativeHead(); }
;

protected negativeOccurrences throws BuilderException:
	{ getBuilder().beginNegativeOccurrences(); }
		occurrenceList
	{ getBuilder().endNegativeOccurrences(); }
;

protected keptOccurrences throws BuilderException:
	{ getBuilder().beginKeptOccurrences(); }
		occurrenceList
	{ getBuilder().endKeptOccurrences(); }
;

protected removedOccurrences throws BuilderException:
	{ getBuilder().beginRemovedOccurrences(); }
		occurrenceList
	{ getBuilder().endRemovedOccurrences(); }
;

protected fieldAccessConjunct throws BuilderException
{ String id; }
:
	id=composedID { getBuilder().addFieldAccessConjunct(id); }
;

protected simpleIdConjunct throws BuilderException
{ String id; }
:
	id=simpleID { getBuilder().addSimpleIdConjunct(id); }
;

protected argumentedConjunct throws BuilderException
{ String id; }
:
	id=anID { getBuilder().beginArgumentedConjunct(id); }
		LPAREN arglist RPAREN
	{ getBuilder().endArgumentedConjunct(); }
;

protected flagOccurrence throws BuilderException
{ String id; }
:
	id=simpleID { getBuilder().addFlagConjunct(id); }
;

protected userDefinedConstraint throws BuilderException
{ String id; }
:
	id=simpleID { getBuilder().beginUserDefinedConstraint(id); }
		LPAREN arglist RPAREN
	{ getBuilder().endUserDefinedConstraint(); }
;

protected guardNbody throws BuilderException:
	( (conjunctList VERTLINE) => guard | ) body
;

protected guard throws BuilderException:
	{ getBuilder().beginGuard(); }
		conjunctList VERTLINE 
	{ getBuilder().endGuard(); }
;

protected negativeGuard throws BuilderException:
	{ getBuilder().beginNegativeGuard(); }
		VERTLINE conjunctList  
	{ getBuilder().endNegativeGuard(); }
;

protected body throws BuilderException:
	{ getBuilder().beginBody(); }
		conjunctList
	{ getBuilder().endBody(); }
;

protected occurrenceList throws BuilderException:
	occurrence (and occurrence)*
;

protected occurrence throws BuilderException
{String id; }
:
	(
		( argument infixId )
			=> infixConstraint
			|  ( flagOccurrence | userDefinedConstraint )
	)
	(
		( NUMBER_SIGN id=simpleID { getBuilder().buildOccurrenceId(id); } )
		// Syntactic sugar for pragma passives
		| ( NUMBER_SIGN ( "passive" )? { getBuilder().setPassive(); } )
	)?
;

protected conjunctList throws BuilderException:
	conjunct (and conjunct)*
;

protected conjunct throws BuilderException
{ String message=null; }
:
	( argument infixId )
		=> ( infixConstraint )
		| (
			declarationConjunct
			| argumentedConjunct
			| fieldAccessConjunct
			| simpleIdConjunct
			| constructorInvocationConjunct
			| TRUE
			| FALSE { getBuilder().addFailureConjunct(); }
			| FAIL 
				( LPAREN lit:STRING_LITERAL RPAREN { message=lit.getText(); })? 
				{ 
					if (message == null) 
						getBuilder().addFailureConjunct();
					else 
						getBuilder().addFailureConjunct(message);
				}
		)
;

protected infixId returns [String result] throws BuilderException
{ result = null; }
:
	EQ 		{ result = "=" ; }
	| EQEQ 	{ result = "=="; }
	| EQEQEQ{ result = "===";}
	| LT  	{ result = "<" ; }
	| GT  	{ result = ">" ; }
	| LEQ 	{ result = "<="; }
	| QEL 	{ result = "=<"; }
	| GEQ 	{ result = ">="; }
	| NEQ 	{ result = "!="; }
	| NEQEQ	{ result = "!==";}
	| uds:SINGLE_CHAR_LITERAL { result = uds.getText(); }
	| udm:MULTI_CHAR_LITERAL { result = udm.getText(); }
;

protected infixConstraint throws BuilderException
{ String infix; }
:	
	{ getBuilder().beginInfixConstraint(); }
		argument
		infix=infixId() { getBuilder().buildInfix(infix); }
		argument
	{ getBuilder().endInfixConstraint(); }
;

protected declarationConjunct throws BuilderException
{ boolean fixed = false; String id; }
:
	(FIXED { fixed = true; })?
	{ getBuilder().beginDeclarationConjunct(); }
	variableType[fixed]
	id = simpleID	{ getBuilder().buildDeclaredVariable(id); }
	(
		EQ argument
	)?
	{ getBuilder().endDeclarationConjunct(); }
;

protected methodInvocationConjunct throws BuilderException:
	id:ID { getBuilder().beginMethodInvocationConjunct(id.getText()); }
		LPAREN arglist RPAREN
	{ getBuilder().endMethodInvocationConjunct(); }
;

protected constructorInvocationConjunct throws BuilderException
{ String id; }
:
	NEW id=anID 
	{ getBuilder().beginConstructorInvocationConjunct(id); }
		LPAREN arglist RPAREN
	{ getBuilder().endConstructorInvocationConjunct(); }
;

// We laten zowel prolog- als java-like syntax toe bij de
// and en bij het einde van een regel. Dit laat de gebruiker
// toe om zelf te kiezen wat hem/haar het beste ligt.
// Ook hybride dingen zijn toegelaten, waar ik persoonlijk
// wel een fan van ben, bvb:
//		leq(X, Y), leq(X1, Y1) <=> X == X1 && Y == Y1 | true.
// ...
protected and:	AND | COMMA	;
protected ruleEnd:	SEMICOLON | DOT ;

protected arglist throws BuilderException:
	{ getBuilder().beginArguments(); }
	(argument)? (COMMA! argument)*
	{ getBuilder().endArguments(); }
;

protected argument throws BuilderException:
	identifiedArgument 	
		| constantArgument 
		| nullArgument
		| constructorInvocationArgument
		| methodInvocationArgument 
		| fieldAccessArgument
;


protected fieldAccessArgument throws BuilderException:
	id:ID { getBuilder().addFieldAccessArgument(id.getText()); }
;


protected identifiedArgument throws BuilderException:
	id:SIMPLE_ID	{ getBuilder().addIdentifiedArgument(id.getText()); }
;

protected constantArgument throws BuilderException:
	c:SINGLE_CHAR_LITERAL { getBuilder().addCharLiteralArgument(c.getText()); }
		| s:STRING_LITERAL{ getBuilder().addStringLiteralArgument(s.getText()); }
		| i:NUM_INT 	  { getBuilder().addPrimitiveArgument(Integer.valueOf(i.getText()).intValue()); }
		| fl:NUM_FLOAT 	  { getBuilder().addPrimitiveArgument(Float.valueOf(fl.getText()).floatValue()); }
		| l:NUM_LONG 	  { getBuilder().addPrimitiveArgument(Long.valueOf(l.getText()).longValue()); }
		| d:NUM_DOUBLE 	  { getBuilder().addPrimitiveArgument(Double.valueOf(d.getText()).doubleValue()); }
		| TRUE	 		  { getBuilder().addPrimitiveArgument(true); }
		| FALSE			  { getBuilder().addPrimitiveArgument(false); }
		| illegal:MULTI_CHAR_LITERAL { 
			throw new BuilderException(
				"Illegal character literal: '" + illegal.getText() + "'"
			);
		}
;

protected nullArgument throws BuilderException:
	NULL			{ getBuilder().addNullArgument(); }
;

protected methodInvocationArgument throws BuilderException
{ String id; }
:
	id=anID { getBuilder().beginMethodInvocationArgument(id); }
		LPAREN arglist RPAREN
	{ getBuilder().endMethodInvocationArgument(); }
;

protected constructorInvocationArgument throws BuilderException
{ String id; }
:
	NEW id=anID { getBuilder().beginConstructorInvocationArgument(id); }
		( typeArguments )?
		LPAREN arglist RPAREN
	{ getBuilder().endConstructorInvocationArgument(); }
;


class CHRLexer extends Lexer;

options {
	testLiterals=false;
	k=3;
	exportVocab=CHR;	
	charVocabulary='\u0003'..'\u7FFE';
	classHeaderPrefix="@SuppressWarnings(CHRParser.ALL) public final";
}

tokens {
	PACKAGE		= "package";
	HANDLER		= "handler";
	SOLVER		= "solver";
	RULES		= "rules";
	VARIABLE	= "pragma_var";
	LOCAL       = "local";
	CONSTRAINT	= "constraint";		// cfr JaCK
	IMPORT		= "import";
	TRUE		= "true";
	FALSE		= "false";
	NULL		= "null";
	FAIL		= "fail";
	EXTENDS		= "extends";
	STATIC		= "static";
	INFIX		= "infix";
	OPTION		= "option";
	ON			= "on";
	OFF			= "off";
	NEW			= "new";
	PRAGMA		= "pragma";
	
	PUBLIC		= "public";
	PRIVATE		= "private";
	PROTECTED	= "protected";
	
	ELLIPSIS;
//	MULTI_CHAR_LITERAL;
}

SIMP 		:	"<=>";
PROP 		:	"==>";
FIXED		:	"+";
MINUS 		:	"\\";
DOUBLE_MINUS:	"\\\\";
AND 		:	"&&";
SEMICOLON 	:	';';
COMMA		:	',';
LPAREN		:	'(';
RPAREN		:	')';
LCURLY		:	'{';
RCURLY		:	'}';
AT			:	'@';
VERTLINE	:	'|';
AMPERCENT	:	'&';
NUMBER_SIGN	:	'#';

//DOT : '.';
STAR: '*';

// operatoren:
EQ:		 	"=";
EQEQ:		"==";
EQEQEQ:		"===";
LT:			"<";
LEQ:		"<=";
QEL:		"=<";
GT:			">";
GEQ:		">=";
NEQ:		"!=";
NEQEQ:		"!==";

// Whitespace -- ignored
WS
:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
	;

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)?
		{$setType(Token.SKIP); newline();}
	;

// multiple-line comments
ML_COMMENT
	:	"/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another.  I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous.  Consequently, the resulting grammar
				must be ambiguous.  I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{$setType(Token.SKIP);}
	;

// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
ID
	options {
		testLiterals=true;
		paraphrase = "an identifer";
	}
	:	(SIMPLE_ID '.' (SIMPLE_ID | '*'))
			=> SIMPLE_ID ('.' (SIMPLE_ID | ('*' { _ttype = ON_DEMAND_ID; })) )+
			| SIMPLE_ID { _ttype = SIMPLE_ID; }
	;
	
protected SIMPLE_ID
	options {
		testLiterals=true;
		paraphrase = "an identifer";
	}

	:	( 'a'..'z' | 'A'..'Z' | '_' | '$' ) 
		( 'a'..'z' | 'A'..'Z' | '_' | '$' | '0'..'9')*
	;
	
// character literals
SINGLE_CHAR_LITERAL
	// I'm not very creative with variable names now am I?
	{ boolean a = false, b = false, c = false; }
	:	'\''!
	 ( (ESC { c = true; } | ~('\''|'\n'|'\r'|'\\')) 
	 	{ 
	 		if (!a) {
	 			if (b) {a = true; _ttype = MULTI_CHAR_LITERAL;}
		 		else b = true;
	 		}
	 	}  )* 
	 '\''!
	 
	 { 
	 	if (a && c) 
	 		throw new antlr.SemanticException("Illegal literal/identifier: '" + getText() + "'");
	 }
	;

// string literals
STRING_LITERAL
	options { paraphrase = "a string"; }
	: '"'! (ESC|~('"'|'\\'|'\n'|'\r'))* '"'!
	;
	
// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected ESC:
	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)
	;
	
// hexadecimal digit (again, note it's protected!)
protected HEX_DIGIT:
	('0'..'9'|'A'..'F'|'a'..'f')
;

// a numeric literal
NUM_INT
	options {
		paraphrase = "a numerical";	
	}
	{boolean isDecimal=false; Token t=null;}
	:	'.' {_ttype = DOT;}
		(	'.' '.' {_ttype = ELLIPSIS;}	/* Dit laten we maar staan, hoewel nergens gebruikt */
		|	(	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
				{
				if (t != null && t.getText().toUpperCase().indexOf('F')>=0) {
					_ttype = NUM_FLOAT;
				}
				else {
					_ttype = NUM_DOUBLE; // assume double
				}
				}
			)?
		)

	|	(	'0' {isDecimal = true;} // special case for just '0'
			(	('x'|'X')
				(											// hex
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options {
						warnWhenFollowAmbig=false;
					}
				:	HEX_DIGIT
				)+

			|	//float or double with leading zero
				(('0'..'9')+ ('.'|EXPONENT|FLOAT_SUFFIX)) => ('0'..'9')+

			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
		)
		(	('l'|'L')! { _ttype = NUM_LONG; }

		// only check to see if it's a float if looks like decimal so far
		|	{isDecimal}?
			(	'.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
			|	EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
			|	f4:FLOAT_SUFFIX {t=f4;}
			)
			{
			if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0) {
				_ttype = NUM_FLOAT;
			}
			else {
				_ttype = NUM_DOUBLE; // assume double
			}
			}
		)?
	;


// a couple protected methods to assist in matching floating point numbers
protected EXPONENT:
	('e'|'E') ('+'|'-')? ('0'..'9')+
;
	
protected FLOAT_SUFFIX:
	'f'|'F'|'d'|'D'
;
