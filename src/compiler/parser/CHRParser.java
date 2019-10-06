// $ANTLR : "chr.g" -> "CHRParser.java"$

package compiler.parser;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

import compiler.CHRIntermediateForm.builder.ICHRIntermediateFormBuilder;
import compiler.CHRIntermediateForm.builder.ICHRIntermediateFormDirector;

import static compiler.CHRIntermediateForm.rulez.RuleType.PROPAGATION;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPAGATION;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPLIFICATION;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.UNNAMED_PACKAGE_ID;

import util.builder.BuilderException;

import compiler.options.Options;
import compiler.options.OptionsException;

@SuppressWarnings(CHRParser.ALL) public final class CHRParser extends antlr.LLkParser       implements CHRTokenTypes
, ICHRIntermediateFormDirector {

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

protected CHRParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public CHRParser(TokenBuffer tokenBuf) {
  this(tokenBuf,4);
}

protected CHRParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public CHRParser(TokenStream lexer) {
  this(lexer,4);
}

public CHRParser(ParserSharedInputState state) {
  super(state,4);
  tokenNames = _tokenNames;
}

	protected final void option() throws RecognitionException, TokenStreamException, OptionsException {
		
		String optionName = null;
		
		try {      // for error handling
			match(OPTION);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case SIMPLE_ID:
			{
				optionName=simpleID();
				break;
			}
			case LITERAL_debug:
			{
				match(LITERAL_debug);
				if ( inputState.guessing==0 ) {
					optionName="debug";
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COMMA:
			{
				binaryOption(optionName);
				break;
			}
			case RPAREN:
			{
				if ( inputState.guessing==0 ) {
					getOptions().processOptions('-' + optionName);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			match(SEMICOLON);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final String  simpleID() throws RecognitionException, TokenStreamException {
		String result;
		
		Token  id = null;
		result = null;
		
		try {      // for error handling
			id = LT(1);
			match(SIMPLE_ID);
			if ( inputState.guessing==0 ) {
				result = id.getText();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final void binaryOption(
		String optionName
	) throws RecognitionException, TokenStreamException, OptionsException {
		
		String value;
		
		try {      // for error handling
			match(COMMA);
			value=optionValue();
			if ( inputState.guessing==0 ) {
				getOptions().processOptions('-' + optionName, value);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
	}
	
	private final String  optionValue() throws RecognitionException, TokenStreamException {
		String result;
		
		Token  s = null;
		Token  n = null;
		Token  f = null;
		Token  d = null;
		result = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING_LITERAL:
			{
				s = LT(1);
				match(STRING_LITERAL);
				if ( inputState.guessing==0 ) {
					result = s.getText();
				}
				break;
			}
			case NUM_INT:
			{
				n = LT(1);
				match(NUM_INT);
				if ( inputState.guessing==0 ) {
					result = n.getText();
				}
				break;
			}
			case NUM_FLOAT:
			{
				f = LT(1);
				match(NUM_FLOAT);
				if ( inputState.guessing==0 ) {
					result = f.getText();
				}
				break;
			}
			case NUM_DOUBLE:
			{
				d = LT(1);
				match(NUM_DOUBLE);
				if ( inputState.guessing==0 ) {
					result = d.getText();
				}
				break;
			}
			case SIMPLE_ID:
			case ID:
			{
				result=anID();
				break;
			}
			case TRUE:
			case ON:
			{
				{
				switch ( LA(1)) {
				case TRUE:
				{
					match(TRUE);
					break;
				}
				case ON:
				{
					match(ON);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					result = "true";
				}
				break;
			}
			case FALSE:
			case OFF:
			{
				{
				switch ( LA(1)) {
				case FALSE:
				{
					match(FALSE);
					break;
				}
				case OFF:
				{
					match(OFF);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					result = "false";
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final String  anID() throws RecognitionException, TokenStreamException {
		String result;
		
		result = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				result=composedID();
				break;
			}
			case SIMPLE_ID:
			{
				result=simpleID();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final void compilationUnit() throws RecognitionException, TokenStreamException, OptionsException,BuilderException {
		
		
		try {      // for error handling
			packageDeclaration();
			imports();
			handler();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void packageDeclaration() throws RecognitionException, TokenStreamException, BuilderException {
		
		String packageId = UNNAMED_PACKAGE_ID;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case PACKAGE:
			{
				match(PACKAGE);
				packageId=anID();
				match(SEMICOLON);
				break;
			}
			case IMPORT:
			case HANDLER:
			case PUBLIC:
			case PROTECTED:
			case PRIVATE:
			case LOCAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().buildPackageDeclaration(packageId);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void imports() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginImports();
			}
			{
			_loop642:
			do {
				if ((LA(1)==IMPORT)) {
					match(IMPORT);
					{
					switch ( LA(1)) {
					case SIMPLE_ID:
					case ID:
					{
						importSingleType();
						break;
					}
					case ON_DEMAND_ID:
					{
						importOnDemand();
						break;
					}
					default:
						if ((LA(1)==STATIC) && (LA(2)==SIMPLE_ID||LA(2)==ID)) {
							importSingleStatic();
						}
						else if ((LA(1)==STATIC) && (LA(2)==ON_DEMAND_ID)) {
							importStaticOnDemand();
						}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop642;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endImports();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void handler() throws RecognitionException, TokenStreamException, BuilderException,OptionsException {
		
		Token  handler = null;
		String access;
		
		try {      // for error handling
			access=accessModifier();
			match(HANDLER);
			handler = LT(1);
			match(SIMPLE_ID);
			if ( inputState.guessing==0 ) {
				getBuilder().beginHandler(handler.getText(), access);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeParameters();
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LCURLY);
			declarations();
			ruleDefinitions();
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				getBuilder().endHandler();
			}
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void importSingleType() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=anID();
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().importSingleType(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void importOnDemand() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			id = LT(1);
			match(ON_DEMAND_ID);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().importOnDemand(id.getText());
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void importSingleStatic() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			match(STATIC);
			id=anID();
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().importSingleStatic(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void importStaticOnDemand() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			match(STATIC);
			id = LT(1);
			match(ON_DEMAND_ID);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().importStaticOnDemand(id.getText());
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final String  accessModifier() throws RecognitionException, TokenStreamException {
		String result;
		
		result = "default";
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case PUBLIC:
			{
				match(PUBLIC);
				if ( inputState.guessing==0 ) {
					result = "public";
				}
				break;
			}
			case PROTECTED:
			{
				match(PROTECTED);
				if ( inputState.guessing==0 ) {
					result = "protected";
				}
				break;
			}
			case PRIVATE:
			{
				match(PRIVATE);
				if ( inputState.guessing==0 ) {
					result = "private";
				}
				break;
			}
			case LOCAL:
			{
				match(LOCAL);
				if ( inputState.guessing==0 ) {
					result = "local";
				}
				break;
			}
			case HANDLER:
			case SOLVER:
			case CONSTRAINT:
			case LITERAL_constraints:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final void typeParameters() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(LT);
			if ( inputState.guessing==0 ) {
				getBuilder().beginTypeParameters();
			}
			{
			switch ( LA(1)) {
			case SIMPLE_ID:
			{
				typeParameter();
				{
				_loop659:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						typeParameter();
					}
					else {
						break _loop659;
					}
					
				} while (true);
				}
				break;
			}
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(GT);
			if ( inputState.guessing==0 ) {
				getBuilder().endTypeParameters();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void declarations() throws RecognitionException, TokenStreamException, BuilderException,OptionsException {
		
		String access;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginDeclarations();
			}
			{
			int _cnt653=0;
			_loop653:
			do {
				switch ( LA(1)) {
				case PUBLIC:
				case PROTECTED:
				case PRIVATE:
				case LOCAL:
				case SOLVER:
				case CONSTRAINT:
				case LITERAL_constraints:
				{
					{
					access=accessModifier();
					{
					switch ( LA(1)) {
					case SOLVER:
					{
						solverDeclaration(access);
						break;
					}
					case CONSTRAINT:
					case LITERAL_constraints:
					{
						constraintDeclaration(access);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					}
					break;
				}
				case OPTION:
				{
					option();
					break;
				}
				default:
				{
					if ( _cnt653>=1 ) { break _loop653; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				}
				_cnt653++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endDeclarations();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void ruleDefinitions() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(RULES);
			match(LCURLY);
			if ( inputState.guessing==0 ) {
				getBuilder().beginRules();
			}
			{
			_loop698:
			do {
				switch ( LA(1)) {
				case VARIABLE:
				{
					variableDeclarations();
					break;
				}
				case LOCAL:
				case LITERAL_variable:
				{
					localVariableDeclarations();
					break;
				}
				case STRING_LITERAL:
				case NUM_INT:
				case NUM_FLOAT:
				case NUM_DOUBLE:
				case TRUE:
				case FALSE:
				case SIMPLE_ID:
				case ID:
				case SINGLE_CHAR_LITERAL:
				case MULTI_CHAR_LITERAL:
				case NEW:
				case NUM_LONG:
				case NULL:
				{
					rule();
					break;
				}
				default:
				{
					break _loop698;
				}
				}
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				getBuilder().endRules();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void typeParameter() throws RecognitionException, TokenStreamException, BuilderException {
		
		String sid;
		
		try {      // for error handling
			sid=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginTypeParameter(sid);
			}
			{
			switch ( LA(1)) {
			case EXTENDS:
			{
				match(EXTENDS);
				upperBound();
				{
				_loop648:
				do {
					if ((LA(1)==AMPERCENT)) {
						match(AMPERCENT);
						upperBound();
					}
					else {
						break _loop648;
					}
					
				} while (true);
				}
				break;
			}
			case COMMA:
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endTypeParameter();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void upperBound() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginUpperBound(id);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeArguments();
				break;
			}
			case COMMA:
			case AMPERCENT:
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endUpperBound();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void solverDeclaration(
		String access
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		String interf, id = null;
		
		try {      // for error handling
			match(SOLVER);
			interf=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginSolverDeclaration(interf, access);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeArguments();
				break;
			}
			case SEMICOLON:
			case SIMPLE_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case SIMPLE_ID:
			{
				id=simpleID();
				{
				switch ( LA(1)) {
				case EQ:
				{
					match(EQ);
					if ( inputState.guessing==0 ) {
						getBuilder().beginSolverDefault();
					}
					argument();
					if ( inputState.guessing==0 ) {
						getBuilder().endSolverDefault();
					}
					break;
				}
				case SEMICOLON:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().buildSolverName(id);
			}
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().endSolverDeclaration();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constraintDeclaration(
		String access
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CONSTRAINT:
			{
				match(CONSTRAINT);
				break;
			}
			case LITERAL_constraints:
			{
				match(LITERAL_constraints);
				if ( inputState.guessing==0 ) {
					System.err.println(
							" --> warning: deprecated syntax 'constraints' (replace with 'constraint')"
						);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			constraintDeclarationBody(access);
			{
			_loop687:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					constraintDeclarationBody(access);
				}
				else {
					break _loop687;
				}
				
			} while (true);
			}
			match(SEMICOLON);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void typeArguments() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginTypeArguments();
			}
			match(LT);
			typeArgument();
			{
			_loop675:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					typeArgument();
				}
				else {
					break _loop675;
				}
				
			} while (true);
			}
			match(GT);
			if ( inputState.guessing==0 ) {
				getBuilder().endTypeArguments();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final String  composedID() throws RecognitionException, TokenStreamException {
		String result;
		
		Token  id = null;
		result = null;
		
		try {      // for error handling
			id = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				result = id.getText();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final void argument() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING_LITERAL:
			case NUM_INT:
			case NUM_FLOAT:
			case NUM_DOUBLE:
			case TRUE:
			case FALSE:
			case SINGLE_CHAR_LITERAL:
			case MULTI_CHAR_LITERAL:
			case NUM_LONG:
			{
				constantArgument();
				break;
			}
			case NULL:
			{
				nullArgument();
				break;
			}
			case NEW:
			{
				constructorInvocationArgument();
				break;
			}
			default:
				if ((LA(1)==SIMPLE_ID) && (_tokenSet_15.member(LA(2)))) {
					identifiedArgument();
				}
				else if ((LA(1)==SIMPLE_ID||LA(1)==ID) && (LA(2)==LPAREN)) {
					methodInvocationArgument();
				}
				else if ((LA(1)==ID) && (_tokenSet_15.member(LA(2)))) {
					fieldAccessArgument();
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void typeArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginTypeArgument(id);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeParameters();
				break;
			}
			case COMMA:
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endTypeArgument();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constraintDeclarationBody(
		String access
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			id = LT(1);
			match(SIMPLE_ID);
			if ( inputState.guessing==0 ) {
				getBuilder().beginConstraintDeclaration(id.getText(), access);
			}
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				match(LPAREN);
				constraintArgumentList();
				match(RPAREN);
				break;
			}
			case COMMA:
			case SEMICOLON:
			case INFIX:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case INFIX:
			{
				match(INFIX);
				infixDeclaration();
				{
				_loop682:
				do {
					if ((LA(1)==COMMA) && (_tokenSet_16.member(LA(2)))) {
						match(COMMA);
						infixDeclaration();
					}
					else {
						break _loop682;
					}
					
				} while (true);
				}
				break;
			}
			case COMMA:
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endConstraintDeclaration();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constraintArgumentList() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case SIMPLE_ID:
			case ID:
			case FIXED:
			{
				constraintArgument();
				break;
			}
			case COMMA:
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop691:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					constraintArgument();
				}
				else {
					break _loop691;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void infixDeclaration() throws RecognitionException, TokenStreamException, BuilderException {
		
		String infix;
		
		try {      // for error handling
			infix=infixId();
			if ( inputState.guessing==0 ) {
				getBuilder().buildInfixIdentifier(infix);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final String  infixId() throws RecognitionException, TokenStreamException, BuilderException {
		String result;
		
		Token  uds = null;
		Token  udm = null;
		result = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case EQ:
			{
				match(EQ);
				if ( inputState.guessing==0 ) {
					result = "=" ;
				}
				break;
			}
			case EQEQ:
			{
				match(EQEQ);
				if ( inputState.guessing==0 ) {
					result = "==";
				}
				break;
			}
			case EQEQEQ:
			{
				match(EQEQEQ);
				if ( inputState.guessing==0 ) {
					result = "===";
				}
				break;
			}
			case LT:
			{
				match(LT);
				if ( inputState.guessing==0 ) {
					result = "<" ;
				}
				break;
			}
			case GT:
			{
				match(GT);
				if ( inputState.guessing==0 ) {
					result = ">" ;
				}
				break;
			}
			case LEQ:
			{
				match(LEQ);
				if ( inputState.guessing==0 ) {
					result = "<=";
				}
				break;
			}
			case QEL:
			{
				match(QEL);
				if ( inputState.guessing==0 ) {
					result = "=<";
				}
				break;
			}
			case GEQ:
			{
				match(GEQ);
				if ( inputState.guessing==0 ) {
					result = ">=";
				}
				break;
			}
			case NEQ:
			{
				match(NEQ);
				if ( inputState.guessing==0 ) {
					result = "!=";
				}
				break;
			}
			case NEQEQ:
			{
				match(NEQEQ);
				if ( inputState.guessing==0 ) {
					result = "!==";
				}
				break;
			}
			case SINGLE_CHAR_LITERAL:
			{
				uds = LT(1);
				match(SINGLE_CHAR_LITERAL);
				if ( inputState.guessing==0 ) {
					result = uds.getText();
				}
				break;
			}
			case MULTI_CHAR_LITERAL:
			{
				udm = LT(1);
				match(MULTI_CHAR_LITERAL);
				if ( inputState.guessing==0 ) {
					result = udm.getText();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_18);
			} else {
			  throw ex;
			}
		}
		return result;
	}
	
	protected final void constraintArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		
			String type, name = null; 
			boolean fixed = false;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case FIXED:
			{
				match(FIXED);
				if ( inputState.guessing==0 ) {
					fixed = true;
				}
				break;
			}
			case SIMPLE_ID:
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			type=anID();
			if ( inputState.guessing==0 ) {
				
						getBuilder().beginConstraintArgument(); 
						getBuilder().beginConstraintArgumentType(type, fixed);
					
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeArguments();
				break;
			}
			case COMMA:
			case RPAREN:
			case SIMPLE_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endConstraintArgumentType();
			}
			{
			switch ( LA(1)) {
			case SIMPLE_ID:
			{
				name=simpleID();
				break;
			}
			case COMMA:
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
						getBuilder().buildConstraintArgumentName(name);
						getBuilder().endConstraintArgument();
					
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void variableDeclarations() throws RecognitionException, TokenStreamException, BuilderException {
		
		boolean fixed = false;
		
		try {      // for error handling
			match(VARIABLE);
			{
			switch ( LA(1)) {
			case FIXED:
			{
				match(FIXED);
				if ( inputState.guessing==0 ) {
					fixed = true;
				}
				break;
			}
			case SIMPLE_ID:
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginVariableDeclarations();
			}
			variableType(fixed);
			variableDeclaration();
			{
			_loop709:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					variableDeclaration();
				}
				else {
					break _loop709;
				}
				
			} while (true);
			}
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().endVariableDeclarations();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void localVariableDeclarations() throws RecognitionException, TokenStreamException, BuilderException {
		
		boolean fixed = false;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LOCAL:
			{
				match(LOCAL);
				break;
			}
			case LITERAL_variable:
			{
				{
				match(LITERAL_variable);
				if ( inputState.guessing==0 ) {
					System.err.println(
							" --> warning: deprecated syntax 'variable' (replace with 'local')"
						);
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case FIXED:
			{
				match(FIXED);
				if ( inputState.guessing==0 ) {
					fixed = true;
				}
				break;
			}
			case SIMPLE_ID:
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginLocalVariableDeclarations();
			}
			variableType(fixed);
			localVariableDeclaration();
			{
			_loop704:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					localVariableDeclaration();
				}
				else {
					break _loop704;
				}
				
			} while (true);
			}
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				getBuilder().endLocalVariableDeclarations();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void rule() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginRule();
			}
			{
			boolean synPredMatched716 = false;
			if (((LA(1)==SIMPLE_ID) && (LA(2)==AT))) {
				int _m716 = mark();
				synPredMatched716 = true;
				inputState.guessing++;
				try {
					{
					match(SIMPLE_ID);
					match(AT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched716 = false;
				}
				rewind(_m716);
inputState.guessing--;
			}
			if ( synPredMatched716 ) {
				id = LT(1);
				match(SIMPLE_ID);
				match(AT);
				ruleDefinition(id.getText());
			}
			else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2)))) {
				ruleDefinition(null);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			switch ( LA(1)) {
			case PRAGMA:
			{
				pragmas();
				break;
			}
			case SEMICOLON:
			case DOT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			ruleEnd();
			if ( inputState.guessing==0 ) {
				getBuilder().endRule();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void variableType(
		boolean fixed
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		String type;
		
		try {      // for error handling
			type=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginVariableType(type, fixed);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeArguments();
				break;
			}
			case SIMPLE_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endVariableType();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_23);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void localVariableDeclaration() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().declareLocalVariable(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void variableDeclaration() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().declareVariable(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void ruleDefinition(
		String name
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			boolean synPredMatched720 = false;
			if (((_tokenSet_21.member(LA(1))) && (_tokenSet_24.member(LA(2))) && (_tokenSet_25.member(LA(3))) && (_tokenSet_26.member(LA(4))))) {
				int _m720 = mark();
				synPredMatched720 = true;
				inputState.guessing++;
				try {
					{
					occurrenceList();
					match(MINUS);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched720 = false;
				}
				rewind(_m720);
inputState.guessing--;
			}
			if ( synPredMatched720 ) {
				simpagationDef(name);
			}
			else {
				boolean synPredMatched724 = false;
				if (((_tokenSet_21.member(LA(1))) && (_tokenSet_27.member(LA(2))) && (_tokenSet_28.member(LA(3))) && (_tokenSet_29.member(LA(4))))) {
					int _m724 = mark();
					synPredMatched724 = true;
					inputState.guessing++;
					try {
						{
						occurrenceList();
						{
						_loop723:
						do {
							if ((LA(1)==DOUBLE_MINUS)) {
								negativeHead();
							}
							else {
								break _loop723;
							}
							
						} while (true);
						}
						match(SIMP);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched724 = false;
					}
					rewind(_m724);
inputState.guessing--;
				}
				if ( synPredMatched724 ) {
					simplificationDef(name);
				}
				else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_30.member(LA(2))) && (_tokenSet_31.member(LA(3))) && (_tokenSet_32.member(LA(4)))) {
					propagationDef(name);
					if ( inputState.guessing==0 ) {
						getBuilder().endRuleDefinition();
					}
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_33);
				} else {
				  throw ex;
				}
			}
		}
		
	protected final void pragmas() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(PRAGMA);
			if ( inputState.guessing==0 ) {
				getBuilder().beginPragmas();
			}
			pragma();
			{
			_loop727:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					pragma();
				}
				else {
					break _loop727;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endPragmas();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_34);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void ruleEnd() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case SEMICOLON:
			{
				match(SEMICOLON);
				break;
			}
			case DOT:
			{
				match(DOT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void occurrenceList() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			occurrence();
			{
			_loop761:
			do {
				if ((LA(1)==COMMA||LA(1)==AND)) {
					and();
					occurrence();
				}
				else {
					break _loop761;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_35);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void simpagationDef(
		String name
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginRuleDefinition(name, SIMPAGATION);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginPositiveHead();
			}
			keptOccurrences();
			match(MINUS);
			removedOccurrences();
			if ( inputState.guessing==0 ) {
				getBuilder().endPositiveHead();
			}
			negativeHeads();
			match(SIMP);
			guardNbody();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void negativeHead() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(DOUBLE_MINUS);
			if ( inputState.guessing==0 ) {
				getBuilder().beginNegativeHead();	
			}
			negativeOccurrences();
			{
			switch ( LA(1)) {
			case VERTLINE:
			{
				negativeGuard();
				break;
			}
			case SIMP:
			case PROP:
			case DOUBLE_MINUS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endNegativeHead();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_36);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void simplificationDef(
		String name
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginRuleDefinition(name, SIMPLIFICATION);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginPositiveHead();
			}
			removedOccurrences();
			if ( inputState.guessing==0 ) {
				getBuilder().endPositiveHead();
			}
			negativeHeads();
			match(SIMP);
			guardNbody();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void propagationDef(
		String name
	) throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginRuleDefinition(name, PROPAGATION);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginPositiveHead();
			}
			keptOccurrences();
			if ( inputState.guessing==0 ) {
				getBuilder().endPositiveHead();
			}
			negativeHeads();
			match(PROP);
			guardNbody();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void pragma() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_passive:
			{
				passivePragma();
				break;
			}
			case LITERAL_no_history:
			{
				no_historyPragma();
				break;
			}
			case LITERAL_debug:
			{
				debugPragma();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void passivePragma() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(LITERAL_passive);
			match(LPAREN);
			passivePragmaId();
			{
			_loop732:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					passivePragmaId();
				}
				else {
					break _loop732;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void no_historyPragma() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(LITERAL_no_history);
			if ( inputState.guessing==0 ) {
				getBuilder().addNoHistoryPragma();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void debugPragma() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(LITERAL_debug);
			if ( inputState.guessing==0 ) {
				getBuilder().addDebugPragma();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void passivePragmaId() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().addPassivePragma(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void keptOccurrences() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginKeptOccurrences();
			}
			occurrenceList();
			if ( inputState.guessing==0 ) {
				getBuilder().endKeptOccurrences();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_38);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void removedOccurrences() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginRemovedOccurrences();
			}
			occurrenceList();
			if ( inputState.guessing==0 ) {
				getBuilder().endRemovedOccurrences();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_39);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void negativeHeads() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginNegativeHeads();	
			}
			{
			_loop741:
			do {
				if ((LA(1)==DOUBLE_MINUS)) {
					negativeHead();
				}
				else {
					break _loop741;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endNegativeHeads();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_40);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void guardNbody() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			{
			boolean synPredMatched755 = false;
			if (((_tokenSet_41.member(LA(1))) && (_tokenSet_42.member(LA(2))) && (_tokenSet_43.member(LA(3))) && (_tokenSet_44.member(LA(4))))) {
				int _m755 = mark();
				synPredMatched755 = true;
				inputState.guessing++;
				try {
					{
					conjunctList();
					match(VERTLINE);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched755 = false;
				}
				rewind(_m755);
inputState.guessing--;
			}
			if ( synPredMatched755 ) {
				guard();
			}
			else if ((_tokenSet_41.member(LA(1))) && (_tokenSet_45.member(LA(2))) && (_tokenSet_46.member(LA(3))) && (_tokenSet_47.member(LA(4)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			body();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void negativeOccurrences() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginNegativeOccurrences();
			}
			occurrenceList();
			if ( inputState.guessing==0 ) {
				getBuilder().endNegativeOccurrences();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_48);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void negativeGuard() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginNegativeGuard();
			}
			match(VERTLINE);
			conjunctList();
			if ( inputState.guessing==0 ) {
				getBuilder().endNegativeGuard();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_36);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void fieldAccessConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=composedID();
			if ( inputState.guessing==0 ) {
				getBuilder().addFieldAccessConjunct(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void simpleIdConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().addSimpleIdConjunct(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void argumentedConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginArgumentedConjunct(id);
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endArgumentedConjunct();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void arglist() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginArguments();
			}
			{
			switch ( LA(1)) {
			case STRING_LITERAL:
			case NUM_INT:
			case NUM_FLOAT:
			case NUM_DOUBLE:
			case TRUE:
			case FALSE:
			case SIMPLE_ID:
			case ID:
			case SINGLE_CHAR_LITERAL:
			case MULTI_CHAR_LITERAL:
			case NEW:
			case NUM_LONG:
			case NULL:
			{
				argument();
				break;
			}
			case COMMA:
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop793:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					argument();
				}
				else {
					break _loop793;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endArguments();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void flagOccurrence() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().addFlagConjunct(id);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void userDefinedConstraint() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginUserDefinedConstraint(id);
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endUserDefinedConstraint();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void conjunctList() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			conjunct();
			{
			_loop773:
			do {
				if ((LA(1)==COMMA||LA(1)==AND)) {
					and();
					conjunct();
				}
				else {
					break _loop773;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_51);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void guard() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginGuard();
			}
			conjunctList();
			match(VERTLINE);
			if ( inputState.guessing==0 ) {
				getBuilder().endGuard();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_41);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void body() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginBody();
			}
			conjunctList();
			if ( inputState.guessing==0 ) {
				getBuilder().endBody();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void occurrence() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			{
			boolean synPredMatched765 = false;
			if (((_tokenSet_21.member(LA(1))) && (_tokenSet_52.member(LA(2))) && (_tokenSet_53.member(LA(3))) && (_tokenSet_54.member(LA(4))))) {
				int _m765 = mark();
				synPredMatched765 = true;
				inputState.guessing++;
				try {
					{
					argument();
					infixId();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched765 = false;
				}
				rewind(_m765);
inputState.guessing--;
			}
			if ( synPredMatched765 ) {
				infixConstraint();
			}
			else if ((LA(1)==SIMPLE_ID) && (_tokenSet_55.member(LA(2))) && (_tokenSet_56.member(LA(3))) && (_tokenSet_57.member(LA(4)))) {
				{
				if ((LA(1)==SIMPLE_ID) && (_tokenSet_50.member(LA(2)))) {
					flagOccurrence();
				}
				else if ((LA(1)==SIMPLE_ID) && (LA(2)==LPAREN)) {
					userDefinedConstraint();
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			if ((LA(1)==NUMBER_SIGN) && (LA(2)==SIMPLE_ID)) {
				{
				match(NUMBER_SIGN);
				id=simpleID();
				if ( inputState.guessing==0 ) {
					getBuilder().buildOccurrenceId(id);
				}
				}
			}
			else if ((LA(1)==NUMBER_SIGN) && (_tokenSet_58.member(LA(2)))) {
				{
				match(NUMBER_SIGN);
				{
				switch ( LA(1)) {
				case LITERAL_passive:
				{
					match(LITERAL_passive);
					break;
				}
				case COMMA:
				case MINUS:
				case SIMP:
				case PROP:
				case DOUBLE_MINUS:
				case VERTLINE:
				case AND:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					getBuilder().setPassive();
				}
				}
			}
			else if ((_tokenSet_59.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_59);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void and() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case AND:
			{
				match(AND);
				break;
			}
			case COMMA:
			{
				match(COMMA);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_41);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void infixConstraint() throws RecognitionException, TokenStreamException, BuilderException {
		
		String infix;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				getBuilder().beginInfixConstraint();
			}
			argument();
			infix=infixId();
			{
			}
			if ( inputState.guessing==0 ) {
				getBuilder().buildInfix(infix);
			}
			argument();
			if ( inputState.guessing==0 ) {
				getBuilder().endInfixConstraint();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_60);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void conjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  lit = null;
		String message=null;
		
		try {      // for error handling
			boolean synPredMatched776 = false;
			if (((_tokenSet_21.member(LA(1))) && (_tokenSet_52.member(LA(2))) && (_tokenSet_53.member(LA(3))) && (_tokenSet_61.member(LA(4))))) {
				int _m776 = mark();
				synPredMatched776 = true;
				inputState.guessing++;
				try {
					{
					argument();
					infixId();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched776 = false;
				}
				rewind(_m776);
inputState.guessing--;
			}
			if ( synPredMatched776 ) {
				{
				infixConstraint();
				}
			}
			else if ((_tokenSet_62.member(LA(1))) && (_tokenSet_63.member(LA(2))) && (_tokenSet_64.member(LA(3))) && (_tokenSet_65.member(LA(4)))) {
				{
				switch ( LA(1)) {
				case NEW:
				{
					constructorInvocationConjunct();
					break;
				}
				case TRUE:
				{
					match(TRUE);
					break;
				}
				case FALSE:
				{
					match(FALSE);
					if ( inputState.guessing==0 ) {
						getBuilder().addFailureConjunct();
					}
					break;
				}
				case FAIL:
				{
					match(FAIL);
					{
					switch ( LA(1)) {
					case LPAREN:
					{
						match(LPAREN);
						lit = LT(1);
						match(STRING_LITERAL);
						match(RPAREN);
						if ( inputState.guessing==0 ) {
							message=lit.getText();
						}
						break;
					}
					case COMMA:
					case SEMICOLON:
					case SIMP:
					case PRAGMA:
					case PROP:
					case DOUBLE_MINUS:
					case VERTLINE:
					case AND:
					case DOT:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					if ( inputState.guessing==0 ) {
						
											if (message == null) 
												getBuilder().addFailureConjunct();
											else 
												getBuilder().addFailureConjunct(message);
										
					}
					break;
				}
				default:
					if ((LA(1)==SIMPLE_ID||LA(1)==ID||LA(1)==FIXED) && (LA(2)==SIMPLE_ID||LA(2)==LT||LA(2)==ID)) {
						declarationConjunct();
					}
					else if ((LA(1)==SIMPLE_ID||LA(1)==ID) && (LA(2)==LPAREN)) {
						argumentedConjunct();
					}
					else if ((LA(1)==ID) && (_tokenSet_49.member(LA(2)))) {
						fieldAccessConjunct();
					}
					else if ((LA(1)==SIMPLE_ID) && (_tokenSet_49.member(LA(2)))) {
						simpleIdConjunct();
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void declarationConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		boolean fixed = false; String id;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case FIXED:
			{
				match(FIXED);
				if ( inputState.guessing==0 ) {
					fixed = true;
				}
				break;
			}
			case SIMPLE_ID:
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().beginDeclarationConjunct();
			}
			variableType(fixed);
			id=simpleID();
			if ( inputState.guessing==0 ) {
				getBuilder().buildDeclaredVariable(id);
			}
			{
			switch ( LA(1)) {
			case EQ:
			{
				match(EQ);
				argument();
				break;
			}
			case COMMA:
			case SEMICOLON:
			case SIMP:
			case PRAGMA:
			case PROP:
			case DOUBLE_MINUS:
			case VERTLINE:
			case AND:
			case DOT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				getBuilder().endDeclarationConjunct();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constructorInvocationConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			match(NEW);
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginConstructorInvocationConjunct(id);
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endConstructorInvocationConjunct();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_49);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void methodInvocationConjunct() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			id = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				getBuilder().beginMethodInvocationConjunct(id.getText());
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endMethodInvocationConjunct();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void identifiedArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			id = LT(1);
			match(SIMPLE_ID);
			if ( inputState.guessing==0 ) {
				getBuilder().addIdentifiedArgument(id.getText());
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constantArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  c = null;
		Token  s = null;
		Token  i = null;
		Token  fl = null;
		Token  l = null;
		Token  d = null;
		Token  illegal = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SINGLE_CHAR_LITERAL:
			{
				c = LT(1);
				match(SINGLE_CHAR_LITERAL);
				if ( inputState.guessing==0 ) {
					getBuilder().addCharLiteralArgument(c.getText());
				}
				break;
			}
			case STRING_LITERAL:
			{
				s = LT(1);
				match(STRING_LITERAL);
				if ( inputState.guessing==0 ) {
					getBuilder().addStringLiteralArgument(s.getText());
				}
				break;
			}
			case NUM_INT:
			{
				i = LT(1);
				match(NUM_INT);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(Integer.valueOf(i.getText()).intValue());
				}
				break;
			}
			case NUM_FLOAT:
			{
				fl = LT(1);
				match(NUM_FLOAT);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(Float.valueOf(fl.getText()).floatValue());
				}
				break;
			}
			case NUM_LONG:
			{
				l = LT(1);
				match(NUM_LONG);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(Long.valueOf(l.getText()).longValue());
				}
				break;
			}
			case NUM_DOUBLE:
			{
				d = LT(1);
				match(NUM_DOUBLE);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(Double.valueOf(d.getText()).doubleValue());
				}
				break;
			}
			case TRUE:
			{
				match(TRUE);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(true);
				}
				break;
			}
			case FALSE:
			{
				match(FALSE);
				if ( inputState.guessing==0 ) {
					getBuilder().addPrimitiveArgument(false);
				}
				break;
			}
			case MULTI_CHAR_LITERAL:
			{
				illegal = LT(1);
				match(MULTI_CHAR_LITERAL);
				if ( inputState.guessing==0 ) {
					
								throw new BuilderException(
									"Illegal character literal: '" + illegal.getText() + "'"
								);
							
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void nullArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		
		try {      // for error handling
			match(NULL);
			if ( inputState.guessing==0 ) {
				getBuilder().addNullArgument();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void constructorInvocationArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			match(NEW);
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginConstructorInvocationArgument(id);
			}
			{
			switch ( LA(1)) {
			case LT:
			{
				typeArguments();
				break;
			}
			case LPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endConstructorInvocationArgument();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void methodInvocationArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		String id;
		
		try {      // for error handling
			id=anID();
			if ( inputState.guessing==0 ) {
				getBuilder().beginMethodInvocationArgument(id);
			}
			match(LPAREN);
			arglist();
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				getBuilder().endMethodInvocationArgument();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	protected final void fieldAccessArgument() throws RecognitionException, TokenStreamException, BuilderException {
		
		Token  id = null;
		
		try {      // for error handling
			id = LT(1);
			match(ID);
			if ( inputState.guessing==0 ) {
				getBuilder().addFieldAccessArgument(id.getText());
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"TYPECAST",
		"METHODORCONSTRAINT",
		"EMPTY",
		"\"option\"",
		"LPAREN",
		"\"debug\"",
		"COMMA",
		"RPAREN",
		"SEMICOLON",
		"a string",
		"a numerical",
		"NUM_FLOAT",
		"NUM_DOUBLE",
		"\"true\"",
		"\"on\"",
		"\"false\"",
		"\"off\"",
		"\"package\"",
		"\"import\"",
		"\"handler\"",
		"an identifer",
		"LCURLY",
		"RCURLY",
		"\"extends\"",
		"AMPERCENT",
		"LT",
		"GT",
		"an identifer",
		"\"public\"",
		"\"protected\"",
		"\"private\"",
		"\"local\"",
		"ON_DEMAND_ID",
		"\"static\"",
		"\"solver\"",
		"EQ",
		"\"infix\"",
		"\"constraint\"",
		"\"constraints\"",
		"FIXED",
		"\"rules\"",
		"\"variable\"",
		"\"pragma_var\"",
		"AT",
		"MINUS",
		"SIMP",
		"\"pragma\"",
		"\"passive\"",
		"\"no_history\"",
		"PROP",
		"DOUBLE_MINUS",
		"VERTLINE",
		"NUMBER_SIGN",
		"\"fail\"",
		"EQEQ",
		"EQEQEQ",
		"LEQ",
		"QEL",
		"GEQ",
		"NEQ",
		"NEQEQ",
		"SINGLE_CHAR_LITERAL",
		"MULTI_CHAR_LITERAL",
		"\"new\"",
		"AND",
		"DOT",
		"NUM_LONG",
		"\"null\"",
		"ELLIPSIS",
		"STAR",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"ESC",
		"HEX_DIGIT",
		"EXPONENT",
		"FLOAT_SUFFIX"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 24528558227584L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 137078865443953920L, 48L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1895832832L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 64437092352L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 64432898048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 6871956062208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 1107297280L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 17592186044416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 67108864L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 1073742848L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1342178304L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 1358961920L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 64739246539283712L, 48L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { -151151511127188480L, 55L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { -288229824785285120L, 7L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 5120L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 2165044224L, 206L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 3072L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 105589708152832L, 206L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 2165039104L, 206L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { -188306205888740096L, 23L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 16777216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { -215890753606384384L, 23L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 2533277492309248L, 222L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { -197313405142700800L, 223L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { -197594880120191744L, 23L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 164953135147379968L, 222L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { -16316199024722688L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { -189150630818872064L, 23L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 173397384448699648L, 222L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { -7871949723403008L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { 1125899906846720L, 32L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { 4096L, 32L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 63894819713318912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = { 27584547717644288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = { 5120L, 32L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = { 27303072740933632L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = { 18577348462903296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = { 9570149208162304L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = { 144123986333917184L, 206L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = { -252201025602059008L, 23L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = { 180153333645569280L, 222L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = { -106951141525553920L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = { -287103922714176256L, 55L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = { 152111423517622016L, 254L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = { -36054597339709696L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = { 63613344736608256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = { 64739244643456000L, 48L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	private static final long[] mk_tokenSet_50() {
		long[] data = { 135952413751247872L, 16L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	private static final long[] mk_tokenSet_51() {
		long[] data = { 64739244643454976L, 32L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_51 = new BitSet(mk_tokenSet_51());
	private static final long[] mk_tokenSet_52() {
		long[] data = { -288229822621024000L, 7L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_52 = new BitSet(mk_tokenSet_52());
	private static final long[] mk_tokenSet_53() {
		long[] data = { 2701913344L, 206L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_53 = new BitSet(mk_tokenSet_53());
	private static final long[] mk_tokenSet_54() {
		long[] data = { -152277408868995840L, 223L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_54 = new BitSet(mk_tokenSet_54());
	private static final long[] mk_tokenSet_55() {
		long[] data = { 135952413751248128L, 16L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_55 = new BitSet(mk_tokenSet_55());
	private static final long[] mk_tokenSet_56() {
		long[] data = { 210270605860924416L, 222L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_56 = new BitSet(mk_tokenSet_56());
	private static final long[] mk_tokenSet_57() {
		long[] data = { -7027524793271040L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_57 = new BitSet(mk_tokenSet_57());
	private static final long[] mk_tokenSet_58() {
		long[] data = { 66146619527005184L, 16L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_58 = new BitSet(mk_tokenSet_58());
	private static final long[] mk_tokenSet_59() {
		long[] data = { 63894819713319936L, 16L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_59 = new BitSet(mk_tokenSet_59());
	private static final long[] mk_tokenSet_60() {
		long[] data = { 137078313658094592L, 48L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_60 = new BitSet(mk_tokenSet_60());
	private static final long[] mk_tokenSet_61() {
		long[] data = { -223490577976787712L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_61 = new BitSet(mk_tokenSet_61());
	private static final long[] mk_tokenSet_62() {
		long[] data = { 144123986333794304L, 8L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_62 = new BitSet(mk_tokenSet_62());
	private static final long[] mk_tokenSet_63() {
		long[] data = { 64739247344588032L, 48L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_63 = new BitSet(mk_tokenSet_63());
	private static final long[] mk_tokenSet_64() {
		long[] data = { 215724768254230272L, 254L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_64 = new BitSet(mk_tokenSet_64());
	private static final long[] mk_tokenSet_65() {
		long[] data = { -25800320745728L, 255L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_65 = new BitSet(mk_tokenSet_65());
	
	}
