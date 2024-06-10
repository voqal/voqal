// Generated from PLSQLParser/PLSQL.g4 by ANTLR 4.2
package large;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PLSQLParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AND=1, ARRAY=2, AS=3, AUTHID=4, BETWEEN=5, BODY=6, BULK=7, BULK_ROWCOUNT=8, 
		BY=9, CASE=10, CREATE=11, COLLECT=12, COMMIT=13, CURRENT_USER=14, DEFAULT=15, 
		DEFINER=16, DELETE=17, ELSE=18, ELSIF=19, EXTERNAL=20, FALSE=21, FETCH=22, 
		FOR=23, FORALL=24, GOTO=25, IF=26, IN=27, INDEX=28, INSERT=29, INTO=30, 
		IS=31, LANGUAGE=32, LIKE=33, LIMIT=34, LOCK=35, NOT=36, NOTFOUND=37, NULL=38, 
		OPEN=39, OR=40, PACKAGE=41, RAISE=42, ROLLBACK=43, SAVEPOINT=44, SELECT=45, 
		SET=46, SQL=47, TABLE=48, TRANSACTION=49, TRUE=50, THEN=51, UPDATE=52, 
		WHILE=53, INSERTING=54, UPDATING=55, DELETING=56, ISOPEN=57, EXISTS=58, 
		BEGIN=59, CLOSE=60, CONSTANT=61, CONTINUE=62, CURSOR=63, DECLARE=64, DETERMINISTIC=65, 
		END=66, EXCEPTION=67, EXECUTE=68, EXIT=69, FUNCTION=70, IMMEDIATE=71, 
		LOOP=72, NOCOPY=73, OTHERS=74, OUT=75, PARALLEL_ENABLE=76, PIPELINED=77, 
		PRAGMA=78, PROCEDURE=79, RECORD=80, REF=81, RESULT_CACHE=82, RETURN=83, 
		RETURNING=84, ROWTYPE=85, SUBTYPE=86, USING=87, VARRAY=88, VARYING=89, 
		WHEN=90, QUOTED_STRING=91, ID=92, SEMI=93, COLON=94, DOUBLEDOT=95, DOT=96, 
		COMMA=97, EXPONENT=98, ASTERISK=99, AT_SIGN=100, RPAREN=101, LPAREN=102, 
		RBRACK=103, LBRACK=104, PLUS=105, MINUS=106, DIVIDE=107, EQ=108, PERCENT=109, 
		LLABEL=110, RLABEL=111, ASSIGN=112, ARROW=113, VERTBAR=114, DOUBLEVERTBAR=115, 
		NOT_EQ=116, LTH=117, LEQ=118, GTH=119, GEQ=120, INTEGER=121, REAL_NUMBER=122, 
		WS=123, SL_COMMENT=124, ML_COMMENT=125;
	public static final String[] tokenNames = {
		"<INVALID>", "'and'", "'array'", "'as'", "'authid'", "'between'", "'body'", 
		"'bulk'", "'bulk_rowcount'", "'by'", "'case'", "'create'", "'collect'", 
		"'commit'", "'current_user'", "'default'", "'definer'", "'delete'", "'else'", 
		"'elsif'", "'external'", "'false'", "'fetch'", "'for'", "'forall'", "'goto'", 
		"'if'", "'in'", "'index'", "'insert'", "'into'", "'is'", "'language'", 
		"'like'", "'limit'", "'lock'", "'not'", "'notfound'", "'null'", "'open'", 
		"'or'", "'package'", "'raise'", "'rollback'", "'savepoint'", "'select'", 
		"'set'", "'sql'", "'table'", "'transaction'", "'true'", "'then'", "'update'", 
		"'while'", "'inserting'", "'updating'", "'deleting'", "'isopen'", "'exists'", 
		"'begin'", "'close'", "'constant'", "'continue'", "'cursor'", "'declare'", 
		"'deterministic'", "'end'", "'exception'", "'execute'", "'exit'", "'function'", 
		"'immediate'", "'loop'", "'nocopy'", "'others'", "'out'", "'parallel_enable'", 
		"'pipelined'", "'pragma'", "'procedure'", "'record'", "'ref'", "'result_cache'", 
		"'return'", "'returning'", "'rowtype'", "'subtype'", "'using'", "'varray'", 
		"'varying'", "'when'", "QUOTED_STRING", "ID", "';'", "':'", "DOUBLEDOT", 
		"DOT", "','", "'**'", "'*'", "'@'", "')'", "'('", "']'", "'['", "'+'", 
		"'-'", "'/'", "'='", "'%'", "'<<'", "'>>'", "':='", "'=>'", "'|'", "'||'", 
		"NOT_EQ", "'<'", "'<='", "'>'", "'>='", "INTEGER", "REAL_NUMBER", "WS", 
		"SL_COMMENT", "ML_COMMENT"
	};
	public static final int
		RULE_file = 0, RULE_show_errors = 1, RULE_create_object = 2, RULE_procedure_heading = 3, 
		RULE_function_heading = 4, RULE_parameter_declarations = 5, RULE_parameter_declaration = 6, 
		RULE_declare_section = 7, RULE_cursor_definition = 8, RULE_item_declaration = 9, 
		RULE_variable_declaration = 10, RULE_constant_declaration = 11, RULE_exception_declaration = 12, 
		RULE_type_definition = 13, RULE_subtype_definition = 14, RULE_record_type_definition = 15, 
		RULE_record_field_declaration = 16, RULE_collection_type_definition = 17, 
		RULE_varray_type_definition = 18, RULE_nested_table_type_definition = 19, 
		RULE_associative_index_type = 20, RULE_ref_cursor_type_definition = 21, 
		RULE_datatype = 22, RULE_function_declaration_or_definition = 23, RULE_function_declaration = 24, 
		RULE_function_definition = 25, RULE_procedure_declaration_or_definition = 26, 
		RULE_procedure_declaration = 27, RULE_procedure_definition = 28, RULE_body = 29, 
		RULE_exception_handler = 30, RULE_statement = 31, RULE_lvalue = 32, RULE_assign_or_call_statement = 33, 
		RULE_call = 34, RULE_delete_call = 35, RULE_basic_loop_statement = 36, 
		RULE_case_statement = 37, RULE_close_statement = 38, RULE_continue_statement = 39, 
		RULE_execute_immediate_statement = 40, RULE_exit_statement = 41, RULE_fetch_statement = 42, 
		RULE_into_clause = 43, RULE_bulk_collect_into_clause = 44, RULE_using_clause = 45, 
		RULE_param_modifiers = 46, RULE_dynamic_returning_clause = 47, RULE_for_loop_statement = 48, 
		RULE_forall_statement = 49, RULE_bounds_clause = 50, RULE_goto_statement = 51, 
		RULE_if_statement = 52, RULE_null_statement = 53, RULE_open_statement = 54, 
		RULE_pragma = 55, RULE_raise_statement = 56, RULE_return_statement = 57, 
		RULE_plsql_block = 58, RULE_label = 59, RULE_qual_id = 60, RULE_sql_statement = 61, 
		RULE_commit_statement = 62, RULE_delete_statement = 63, RULE_insert_statement = 64, 
		RULE_lock_table_statement = 65, RULE_rollback_statement = 66, RULE_savepoint_statement = 67, 
		RULE_select_statement = 68, RULE_set_transaction_statement = 69, RULE_update_statement = 70, 
		RULE_swallow_to_semi = 71, RULE_while_loop_statement = 72, RULE_match_parens = 73, 
		RULE_label_name = 74, RULE_expression = 75, RULE_or_expr = 76, RULE_and_expr = 77, 
		RULE_not_expr = 78, RULE_compare_expr = 79, RULE_is_null_expr = 80, RULE_like_expr = 81, 
		RULE_between_expr = 82, RULE_in_expr = 83, RULE_numeric_expression = 84, 
		RULE_add_expr = 85, RULE_mul_expr = 86, RULE_unary_sign_expr = 87, RULE_exponent_expr = 88, 
		RULE_atom = 89, RULE_variable_or_function_call = 90, RULE_attribute = 91, 
		RULE_call_args = 92, RULE_boolean_atom = 93, RULE_numeric_atom = 94, RULE_numeric_literal = 95, 
		RULE_boolean_literal = 96, RULE_string_literal = 97, RULE_collection_exists = 98, 
		RULE_conditional_predicate = 99, RULE_parameter = 100, RULE_index = 101, 
		RULE_create_package = 102, RULE_create_package_body = 103, RULE_create_procedure = 104, 
		RULE_create_function = 105, RULE_invoker_rights_clause = 106, RULE_call_spec = 107, 
		RULE_kERRORS = 108, RULE_kEXCEPTIONS = 109, RULE_kFOUND = 110, RULE_kINDICES = 111, 
		RULE_kMOD = 112, RULE_kNAME = 113, RULE_kOF = 114, RULE_kREPLACE = 115, 
		RULE_kROWCOUNT = 116, RULE_kSAVE = 117, RULE_kSHOW = 118, RULE_kTYPE = 119, 
		RULE_kVALUES = 120;
	public static final String[] ruleNames = {
		"file", "show_errors", "create_object", "procedure_heading", "function_heading", 
		"parameter_declarations", "parameter_declaration", "declare_section", 
		"cursor_definition", "item_declaration", "variable_declaration", "constant_declaration", 
		"exception_declaration", "type_definition", "subtype_definition", "record_type_definition", 
		"record_field_declaration", "collection_type_definition", "varray_type_definition", 
		"nested_table_type_definition", "associative_index_type", "ref_cursor_type_definition", 
		"datatype", "function_declaration_or_definition", "function_declaration", 
		"function_definition", "procedure_declaration_or_definition", "procedure_declaration", 
		"procedure_definition", "body", "exception_handler", "statement", "lvalue", 
		"assign_or_call_statement", "call", "delete_call", "basic_loop_statement", 
		"case_statement", "close_statement", "continue_statement", "execute_immediate_statement", 
		"exit_statement", "fetch_statement", "into_clause", "bulk_collect_into_clause", 
		"using_clause", "param_modifiers", "dynamic_returning_clause", "for_loop_statement", 
		"forall_statement", "bounds_clause", "goto_statement", "if_statement", 
		"null_statement", "open_statement", "pragma", "raise_statement", "return_statement", 
		"plsql_block", "label", "qual_id", "sql_statement", "commit_statement", 
		"delete_statement", "insert_statement", "lock_table_statement", "rollback_statement", 
		"savepoint_statement", "select_statement", "set_transaction_statement", 
		"update_statement", "swallow_to_semi", "while_loop_statement", "match_parens", 
		"label_name", "expression", "or_expr", "and_expr", "not_expr", "compare_expr", 
		"is_null_expr", "like_expr", "between_expr", "in_expr", "numeric_expression", 
		"add_expr", "mul_expr", "unary_sign_expr", "exponent_expr", "atom", "variable_or_function_call", 
		"attribute", "call_args", "boolean_atom", "numeric_atom", "numeric_literal", 
		"boolean_literal", "string_literal", "collection_exists", "conditional_predicate", 
		"parameter", "index", "create_package", "create_package_body", "create_procedure", 
		"create_function", "invoker_rights_clause", "call_spec", "kERRORS", "kEXCEPTIONS", 
		"kFOUND", "kINDICES", "kMOD", "kNAME", "kOF", "kREPLACE", "kROWCOUNT", 
		"kSAVE", "kSHOW", "kTYPE", "kVALUES"
	};

	@Override
	public String getGrammarFileName() { return "PLSQL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PLSQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FileContext extends ParserRuleContext {
		public TerminalNode DIVIDE(int i) {
			return getToken(PLSQLParser.DIVIDE, i);
		}
		public List<Show_errorsContext> show_errors() {
			return getRuleContexts(Show_errorsContext.class);
		}
		public TerminalNode EOF() { return getToken(PLSQLParser.EOF, 0); }
		public Create_objectContext create_object(int i) {
			return getRuleContext(Create_objectContext.class,i);
		}
		public List<TerminalNode> DIVIDE() { return getTokens(PLSQLParser.DIVIDE); }
		public List<Create_objectContext> create_object() {
			return getRuleContexts(Create_objectContext.class);
		}
		public Show_errorsContext show_errors(int i) {
			return getRuleContext(Show_errorsContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFile(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(242); create_object();
				setState(245);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(243); match(DIVIDE);
					setState(244); show_errors();
					}
					break;
				}
				setState(248);
				_la = _input.LA(1);
				if (_la==DIVIDE) {
					{
					setState(247); match(DIVIDE);
					}
				}

				}
				}
				setState(252); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CREATE );
			setState(254); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Show_errorsContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(PLSQLParser.SEMI, 0); }
		public KSHOWContext kSHOW() {
			return getRuleContext(KSHOWContext.class,0);
		}
		public KERRORSContext kERRORS() {
			return getRuleContext(KERRORSContext.class,0);
		}
		public Show_errorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_show_errors; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterShow_errors(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitShow_errors(this);
		}
	}

	public final Show_errorsContext show_errors() throws RecognitionException {
		Show_errorsContext _localctx = new Show_errorsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_show_errors);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256); kSHOW();
			setState(257); kERRORS();
			setState(259);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(258); match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_objectContext extends ParserRuleContext {
		public Create_functionContext create_function() {
			return getRuleContext(Create_functionContext.class,0);
		}
		public Create_package_bodyContext create_package_body() {
			return getRuleContext(Create_package_bodyContext.class,0);
		}
		public Create_procedureContext create_procedure() {
			return getRuleContext(Create_procedureContext.class,0);
		}
		public Create_objectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCreate_object(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCreate_object(this);
		}
	}

	public final Create_objectContext create_object() throws RecognitionException {
		Create_objectContext _localctx = new Create_objectContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_create_object);
		try {
			setState(265);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(261); create_package_body();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(262); create_package_body();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(263); create_function();
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(264); create_procedure();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Procedure_headingContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public Parameter_declarationsContext parameter_declarations() {
			return getRuleContext(Parameter_declarationsContext.class,0);
		}
		public TerminalNode PROCEDURE() { return getToken(PLSQLParser.PROCEDURE, 0); }
		public Procedure_headingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procedure_heading; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterProcedure_heading(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitProcedure_heading(this);
		}
	}

	public final Procedure_headingContext procedure_heading() throws RecognitionException {
		Procedure_headingContext _localctx = new Procedure_headingContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_procedure_heading);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267); match(PROCEDURE);
			setState(268); match(ID);
			setState(270);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(269); parameter_declarations();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_headingContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode FUNCTION() { return getToken(PLSQLParser.FUNCTION, 0); }
		public TerminalNode RETURN() { return getToken(PLSQLParser.RETURN, 0); }
		public Parameter_declarationsContext parameter_declarations() {
			return getRuleContext(Parameter_declarationsContext.class,0);
		}
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public Function_headingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_heading; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFunction_heading(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFunction_heading(this);
		}
	}

	public final Function_headingContext function_heading() throws RecognitionException {
		Function_headingContext _localctx = new Function_headingContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_function_heading);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272); match(FUNCTION);
			setState(273); match(ID);
			setState(275);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(274); parameter_declarations();
				}
			}

			setState(277); match(RETURN);
			setState(278); datatype();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Parameter_declarationsContext extends ParserRuleContext {
		public List<Parameter_declarationContext> parameter_declaration() {
			return getRuleContexts(Parameter_declarationContext.class);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public Parameter_declarationContext parameter_declaration(int i) {
			return getRuleContext(Parameter_declarationContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public Parameter_declarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter_declarations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterParameter_declarations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitParameter_declarations(this);
		}
	}

	public final Parameter_declarationsContext parameter_declarations() throws RecognitionException {
		Parameter_declarationsContext _localctx = new Parameter_declarationsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parameter_declarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(280); match(LPAREN);
			setState(281); parameter_declaration();
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(282); match(COMMA);
				setState(283); parameter_declaration();
				}
				}
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(289); match(RPAREN);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Parameter_declarationContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(PLSQLParser.ASSIGN, 0); }
		public TerminalNode NOCOPY() { return getToken(PLSQLParser.NOCOPY, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(PLSQLParser.DEFAULT, 0); }
		public TerminalNode IN() { return getToken(PLSQLParser.IN, 0); }
		public TerminalNode OUT() { return getToken(PLSQLParser.OUT, 0); }
		public Parameter_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterParameter_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitParameter_declaration(this);
		}
	}

	public final Parameter_declarationContext parameter_declaration() throws RecognitionException {
		Parameter_declarationContext _localctx = new Parameter_declarationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_parameter_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291); match(ID);
			setState(301);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(292); match(IN);
				}
				break;

			case 2:
				{
				{
				setState(296);
				switch (_input.LA(1)) {
				case OUT:
					{
					setState(293); match(OUT);
					}
					break;
				case IN:
					{
					setState(294); match(IN);
					setState(295); match(OUT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(299);
				_la = _input.LA(1);
				if (_la==NOCOPY) {
					{
					setState(298); match(NOCOPY);
					}
				}

				}
				}
				break;
			}
			setState(303); datatype();
			setState(306);
			_la = _input.LA(1);
			if (_la==DEFAULT || _la==ASSIGN) {
				{
				setState(304);
				_la = _input.LA(1);
				if ( !(_la==DEFAULT || _la==ASSIGN) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(305); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Declare_sectionContext extends ParserRuleContext {
		public List<Item_declarationContext> item_declaration() {
			return getRuleContexts(Item_declarationContext.class);
		}
		public List<PragmaContext> pragma() {
			return getRuleContexts(PragmaContext.class);
		}
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public Subtype_definitionContext subtype_definition(int i) {
			return getRuleContext(Subtype_definitionContext.class,i);
		}
		public Cursor_definitionContext cursor_definition(int i) {
			return getRuleContext(Cursor_definitionContext.class,i);
		}
		public Type_definitionContext type_definition(int i) {
			return getRuleContext(Type_definitionContext.class,i);
		}
		public PragmaContext pragma(int i) {
			return getRuleContext(PragmaContext.class,i);
		}
		public List<Subtype_definitionContext> subtype_definition() {
			return getRuleContexts(Subtype_definitionContext.class);
		}
		public List<Cursor_definitionContext> cursor_definition() {
			return getRuleContexts(Cursor_definitionContext.class);
		}
		public Procedure_declaration_or_definitionContext procedure_declaration_or_definition(int i) {
			return getRuleContext(Procedure_declaration_or_definitionContext.class,i);
		}
		public Item_declarationContext item_declaration(int i) {
			return getRuleContext(Item_declarationContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public List<Function_declaration_or_definitionContext> function_declaration_or_definition() {
			return getRuleContexts(Function_declaration_or_definitionContext.class);
		}
		public Function_declaration_or_definitionContext function_declaration_or_definition(int i) {
			return getRuleContext(Function_declaration_or_definitionContext.class,i);
		}
		public List<Procedure_declaration_or_definitionContext> procedure_declaration_or_definition() {
			return getRuleContexts(Procedure_declaration_or_definitionContext.class);
		}
		public List<Type_definitionContext> type_definition() {
			return getRuleContexts(Type_definitionContext.class);
		}
		public Declare_sectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declare_section; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterDeclare_section(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitDeclare_section(this);
		}
	}

	public final Declare_sectionContext declare_section() throws RecognitionException {
		Declare_sectionContext _localctx = new Declare_sectionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_declare_section);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(329); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					setState(329);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						setState(308); type_definition();
						setState(309); match(SEMI);
						}
						break;

					case 2:
						{
						setState(311); subtype_definition();
						setState(312); match(SEMI);
						}
						break;

					case 3:
						{
						setState(314); cursor_definition();
						setState(315); match(SEMI);
						}
						break;

					case 4:
						{
						setState(317); item_declaration();
						setState(318); match(SEMI);
						}
						break;

					case 5:
						{
						setState(320); function_declaration_or_definition();
						setState(321); match(SEMI);
						}
						break;

					case 6:
						{
						setState(323); procedure_declaration_or_definition();
						setState(324); match(SEMI);
						}
						break;

					case 7:
						{
						setState(326); pragma();
						setState(327); match(SEMI);
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(331); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cursor_definitionContext extends ParserRuleContext {
		public TerminalNode CURSOR() { return getToken(PLSQLParser.CURSOR, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public Select_statementContext select_statement() {
			return getRuleContext(Select_statementContext.class,0);
		}
		public Parameter_declarationsContext parameter_declarations() {
			return getRuleContext(Parameter_declarationsContext.class,0);
		}
		public Cursor_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cursor_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCursor_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCursor_definition(this);
		}
	}

	public final Cursor_definitionContext cursor_definition() throws RecognitionException {
		Cursor_definitionContext _localctx = new Cursor_definitionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_cursor_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333); match(CURSOR);
			setState(334); match(ID);
			setState(336);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(335); parameter_declarations();
				}
			}

			setState(338); match(IS);
			setState(339); select_statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Item_declarationContext extends ParserRuleContext {
		public Constant_declarationContext constant_declaration() {
			return getRuleContext(Constant_declarationContext.class,0);
		}
		public Variable_declarationContext variable_declaration() {
			return getRuleContext(Variable_declarationContext.class,0);
		}
		public Exception_declarationContext exception_declaration() {
			return getRuleContext(Exception_declarationContext.class,0);
		}
		public Item_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterItem_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitItem_declaration(this);
		}
	}

	public final Item_declarationContext item_declaration() throws RecognitionException {
		Item_declarationContext _localctx = new Item_declarationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_item_declaration);
		try {
			setState(344);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(341); variable_declaration();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(342); constant_declaration();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(343); exception_declaration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Variable_declarationContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(PLSQLParser.ASSIGN, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(PLSQLParser.DEFAULT, 0); }
		public Variable_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterVariable_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitVariable_declaration(this);
		}
	}

	public final Variable_declarationContext variable_declaration() throws RecognitionException {
		Variable_declarationContext _localctx = new Variable_declarationContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_variable_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346); match(ID);
			setState(347); datatype();
			setState(354);
			_la = _input.LA(1);
			if (_la==DEFAULT || _la==NOT || _la==ASSIGN) {
				{
				setState(350);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(348); match(NOT);
					setState(349); match(NULL);
					}
				}

				setState(352);
				_la = _input.LA(1);
				if ( !(_la==DEFAULT || _la==ASSIGN) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(353); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Constant_declarationContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(PLSQLParser.ASSIGN, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(PLSQLParser.CONSTANT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(PLSQLParser.DEFAULT, 0); }
		public Constant_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterConstant_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitConstant_declaration(this);
		}
	}

	public final Constant_declarationContext constant_declaration() throws RecognitionException {
		Constant_declarationContext _localctx = new Constant_declarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_constant_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356); match(ID);
			setState(357); match(CONSTANT);
			setState(358); datatype();
			setState(361);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(359); match(NOT);
				setState(360); match(NULL);
				}
			}

			setState(363);
			_la = _input.LA(1);
			if ( !(_la==DEFAULT || _la==ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(364); expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exception_declarationContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode EXCEPTION() { return getToken(PLSQLParser.EXCEPTION, 0); }
		public Exception_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exception_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterException_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitException_declaration(this);
		}
	}

	public final Exception_declarationContext exception_declaration() throws RecognitionException {
		Exception_declarationContext _localctx = new Exception_declarationContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_exception_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366); match(ID);
			setState(367); match(EXCEPTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_definitionContext extends ParserRuleContext {
		public Ref_cursor_type_definitionContext ref_cursor_type_definition() {
			return getRuleContext(Ref_cursor_type_definitionContext.class,0);
		}
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public Record_type_definitionContext record_type_definition() {
			return getRuleContext(Record_type_definitionContext.class,0);
		}
		public KTYPEContext kTYPE() {
			return getRuleContext(KTYPEContext.class,0);
		}
		public Collection_type_definitionContext collection_type_definition() {
			return getRuleContext(Collection_type_definitionContext.class,0);
		}
		public Type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterType_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitType_definition(this);
		}
	}

	public final Type_definitionContext type_definition() throws RecognitionException {
		Type_definitionContext _localctx = new Type_definitionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_type_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369); kTYPE();
			setState(370); match(ID);
			setState(371); match(IS);
			setState(375);
			switch (_input.LA(1)) {
			case RECORD:
				{
				setState(372); record_type_definition();
				}
				break;
			case TABLE:
			case VARRAY:
			case VARYING:
				{
				setState(373); collection_type_definition();
				}
				break;
			case REF:
				{
				setState(374); ref_cursor_type_definition();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Subtype_definitionContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public TerminalNode SUBTYPE() { return getToken(PLSQLParser.SUBTYPE, 0); }
		public Subtype_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtype_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSubtype_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSubtype_definition(this);
		}
	}

	public final Subtype_definitionContext subtype_definition() throws RecognitionException {
		Subtype_definitionContext _localctx = new Subtype_definitionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_subtype_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(377); match(SUBTYPE);
			setState(378); match(ID);
			setState(379); match(IS);
			setState(380); datatype();
			setState(383);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(381); match(NOT);
				setState(382); match(NULL);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Record_type_definitionContext extends ParserRuleContext {
		public TerminalNode RECORD() { return getToken(PLSQLParser.RECORD, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public Record_field_declarationContext record_field_declaration(int i) {
			return getRuleContext(Record_field_declarationContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public List<Record_field_declarationContext> record_field_declaration() {
			return getRuleContexts(Record_field_declarationContext.class);
		}
		public Record_type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterRecord_type_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitRecord_type_definition(this);
		}
	}

	public final Record_type_definitionContext record_type_definition() throws RecognitionException {
		Record_type_definitionContext _localctx = new Record_type_definitionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_record_type_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385); match(RECORD);
			setState(386); match(LPAREN);
			setState(387); record_field_declaration();
			setState(392);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(388); match(COMMA);
				setState(389); record_field_declaration();
				}
				}
				setState(394);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(395); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Record_field_declarationContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(PLSQLParser.ASSIGN, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(PLSQLParser.DEFAULT, 0); }
		public Record_field_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record_field_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterRecord_field_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitRecord_field_declaration(this);
		}
	}

	public final Record_field_declarationContext record_field_declaration() throws RecognitionException {
		Record_field_declarationContext _localctx = new Record_field_declarationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_record_field_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397); match(ID);
			setState(398); datatype();
			setState(405);
			_la = _input.LA(1);
			if (_la==DEFAULT || _la==NOT || _la==ASSIGN) {
				{
				setState(401);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(399); match(NOT);
					setState(400); match(NULL);
					}
				}

				setState(403);
				_la = _input.LA(1);
				if ( !(_la==DEFAULT || _la==ASSIGN) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(404); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Collection_type_definitionContext extends ParserRuleContext {
		public Varray_type_definitionContext varray_type_definition() {
			return getRuleContext(Varray_type_definitionContext.class,0);
		}
		public Nested_table_type_definitionContext nested_table_type_definition() {
			return getRuleContext(Nested_table_type_definitionContext.class,0);
		}
		public Collection_type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collection_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCollection_type_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCollection_type_definition(this);
		}
	}

	public final Collection_type_definitionContext collection_type_definition() throws RecognitionException {
		Collection_type_definitionContext _localctx = new Collection_type_definitionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_collection_type_definition);
		try {
			setState(409);
			switch (_input.LA(1)) {
			case VARRAY:
			case VARYING:
				enterOuterAlt(_localctx, 1);
				{
				setState(407); varray_type_definition();
				}
				break;
			case TABLE:
				enterOuterAlt(_localctx, 2);
				{
				setState(408); nested_table_type_definition();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Varray_type_definitionContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode ARRAY() { return getToken(PLSQLParser.ARRAY, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode VARYING() { return getToken(PLSQLParser.VARYING, 0); }
		public KOFContext kOF() {
			return getRuleContext(KOFContext.class,0);
		}
		public Numeric_literalContext numeric_literal() {
			return getRuleContext(Numeric_literalContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public TerminalNode VARRAY() { return getToken(PLSQLParser.VARRAY, 0); }
		public Varray_type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varray_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterVarray_type_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitVarray_type_definition(this);
		}
	}

	public final Varray_type_definitionContext varray_type_definition() throws RecognitionException {
		Varray_type_definitionContext _localctx = new Varray_type_definitionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_varray_type_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(416);
			switch (_input.LA(1)) {
			case VARYING:
				{
				setState(411); match(VARYING);
				setState(413);
				_la = _input.LA(1);
				if (_la==ARRAY) {
					{
					setState(412); match(ARRAY);
					}
				}

				}
				break;
			case VARRAY:
				{
				setState(415); match(VARRAY);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(418); match(LPAREN);
			setState(419); numeric_literal();
			setState(420); match(RPAREN);
			setState(421); kOF();
			setState(422); datatype();
			setState(425);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(423); match(NOT);
				setState(424); match(NULL);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Nested_table_type_definitionContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode INDEX() { return getToken(PLSQLParser.INDEX, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public KOFContext kOF() {
			return getRuleContext(KOFContext.class,0);
		}
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public Associative_index_typeContext associative_index_type() {
			return getRuleContext(Associative_index_typeContext.class,0);
		}
		public TerminalNode TABLE() { return getToken(PLSQLParser.TABLE, 0); }
		public TerminalNode BY() { return getToken(PLSQLParser.BY, 0); }
		public Nested_table_type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nested_table_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNested_table_type_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNested_table_type_definition(this);
		}
	}

	public final Nested_table_type_definitionContext nested_table_type_definition() throws RecognitionException {
		Nested_table_type_definitionContext _localctx = new Nested_table_type_definitionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_nested_table_type_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427); match(TABLE);
			setState(428); kOF();
			setState(429); datatype();
			setState(432);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(430); match(NOT);
				setState(431); match(NULL);
				}
			}

			setState(437);
			_la = _input.LA(1);
			if (_la==INDEX) {
				{
				setState(434); match(INDEX);
				setState(435); match(BY);
				setState(436); associative_index_type();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Associative_index_typeContext extends ParserRuleContext {
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public Associative_index_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_associative_index_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAssociative_index_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAssociative_index_type(this);
		}
	}

	public final Associative_index_typeContext associative_index_type() throws RecognitionException {
		Associative_index_typeContext _localctx = new Associative_index_typeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_associative_index_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439); datatype();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ref_cursor_type_definitionContext extends ParserRuleContext {
		public TerminalNode CURSOR() { return getToken(PLSQLParser.CURSOR, 0); }
		public TerminalNode RETURN() { return getToken(PLSQLParser.RETURN, 0); }
		public TerminalNode REF() { return getToken(PLSQLParser.REF, 0); }
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public Ref_cursor_type_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref_cursor_type_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterRef_cursor_type_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitRef_cursor_type_definition(this);
		}
	}

	public final Ref_cursor_type_definitionContext ref_cursor_type_definition() throws RecognitionException {
		Ref_cursor_type_definitionContext _localctx = new Ref_cursor_type_definitionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_ref_cursor_type_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(441); match(REF);
			setState(442); match(CURSOR);
			setState(445);
			_la = _input.LA(1);
			if (_la==RETURN) {
				{
				setState(443); match(RETURN);
				setState(444); datatype();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatatypeContext extends ParserRuleContext {
		public TerminalNode ROWTYPE() { return getToken(PLSQLParser.ROWTYPE, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public List<Numeric_literalContext> numeric_literal() {
			return getRuleContexts(Numeric_literalContext.class);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode PERCENT() { return getToken(PLSQLParser.PERCENT, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public Numeric_literalContext numeric_literal(int i) {
			return getRuleContext(Numeric_literalContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public TerminalNode REF() { return getToken(PLSQLParser.REF, 0); }
		public KTYPEContext kTYPE() {
			return getRuleContext(KTYPEContext.class,0);
		}
		public DatatypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datatype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterDatatype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitDatatype(this);
		}
	}

	public final DatatypeContext datatype() throws RecognitionException {
		DatatypeContext _localctx = new DatatypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_datatype);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
			_la = _input.LA(1);
			if (_la==REF) {
				{
				setState(447); match(REF);
				}
			}

			setState(450); match(ID);
			setState(453);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(451); match(DOT);
				setState(452); match(ID);
				}
			}

			setState(471);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				setState(455); match(LPAREN);
				setState(456); numeric_literal();
				setState(461);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(457); match(COMMA);
					setState(458); numeric_literal();
					}
					}
					setState(463);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(464); match(RPAREN);
				}
				break;
			case PERCENT:
				{
				setState(466); match(PERCENT);
				setState(469);
				switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
				case 1:
					{
					setState(467); kTYPE();
					}
					break;

				case 2:
					{
					setState(468); match(ROWTYPE);
					}
					break;
				}
				}
				break;
			case EOF:
			case AS:
			case AUTHID:
			case DEFAULT:
			case INDEX:
			case IS:
			case NOT:
			case DETERMINISTIC:
			case PARALLEL_ENABLE:
			case PIPELINED:
			case RESULT_CACHE:
			case SEMI:
			case COMMA:
			case RPAREN:
			case ASSIGN:
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_declaration_or_definitionContext extends ParserRuleContext {
		public Function_headingContext function_heading() {
			return getRuleContext(Function_headingContext.class,0);
		}
		public TerminalNode RESULT_CACHE(int i) {
			return getToken(PLSQLParser.RESULT_CACHE, i);
		}
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public TerminalNode DETERMINISTIC(int i) {
			return getToken(PLSQLParser.DETERMINISTIC, i);
		}
		public List<TerminalNode> DETERMINISTIC() { return getTokens(PLSQLParser.DETERMINISTIC); }
		public TerminalNode PARALLEL_ENABLE(int i) {
			return getToken(PLSQLParser.PARALLEL_ENABLE, i);
		}
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode PIPELINED(int i) {
			return getToken(PLSQLParser.PIPELINED, i);
		}
		public List<TerminalNode> RESULT_CACHE() { return getTokens(PLSQLParser.RESULT_CACHE); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public List<TerminalNode> PARALLEL_ENABLE() { return getTokens(PLSQLParser.PARALLEL_ENABLE); }
		public List<TerminalNode> PIPELINED() { return getTokens(PLSQLParser.PIPELINED); }
		public Function_declaration_or_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_declaration_or_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFunction_declaration_or_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFunction_declaration_or_definition(this);
		}
	}

	public final Function_declaration_or_definitionContext function_declaration_or_definition() throws RecognitionException {
		Function_declaration_or_definitionContext _localctx = new Function_declaration_or_definitionContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_function_declaration_or_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(473); function_heading();
			setState(477);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) {
				{
				{
				setState(474);
				_la = _input.LA(1);
				if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				}
				setState(479);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(485);
			_la = _input.LA(1);
			if (_la==AS || _la==IS) {
				{
				setState(480);
				_la = _input.LA(1);
				if ( !(_la==AS || _la==IS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(482);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(481); declare_section();
					}
					break;
				}
				setState(484); body();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_declarationContext extends ParserRuleContext {
		public List<TerminalNode> DETERMINISTIC() { return getTokens(PLSQLParser.DETERMINISTIC); }
		public TerminalNode PARALLEL_ENABLE(int i) {
			return getToken(PLSQLParser.PARALLEL_ENABLE, i);
		}
		public Function_headingContext function_heading() {
			return getRuleContext(Function_headingContext.class,0);
		}
		public TerminalNode PIPELINED(int i) {
			return getToken(PLSQLParser.PIPELINED, i);
		}
		public List<TerminalNode> RESULT_CACHE() { return getTokens(PLSQLParser.RESULT_CACHE); }
		public List<TerminalNode> PARALLEL_ENABLE() { return getTokens(PLSQLParser.PARALLEL_ENABLE); }
		public TerminalNode RESULT_CACHE(int i) {
			return getToken(PLSQLParser.RESULT_CACHE, i);
		}
		public List<TerminalNode> PIPELINED() { return getTokens(PLSQLParser.PIPELINED); }
		public TerminalNode DETERMINISTIC(int i) {
			return getToken(PLSQLParser.DETERMINISTIC, i);
		}
		public Function_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFunction_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFunction_declaration(this);
		}
	}

	public final Function_declarationContext function_declaration() throws RecognitionException {
		Function_declarationContext _localctx = new Function_declarationContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_function_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(487); function_heading();
			setState(491);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) {
				{
				{
				setState(488);
				_la = _input.LA(1);
				if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				}
				setState(493);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_definitionContext extends ParserRuleContext {
		public Function_headingContext function_heading() {
			return getRuleContext(Function_headingContext.class,0);
		}
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode RESULT_CACHE(int i) {
			return getToken(PLSQLParser.RESULT_CACHE, i);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public TerminalNode DETERMINISTIC(int i) {
			return getToken(PLSQLParser.DETERMINISTIC, i);
		}
		public List<TerminalNode> DETERMINISTIC() { return getTokens(PLSQLParser.DETERMINISTIC); }
		public TerminalNode PARALLEL_ENABLE(int i) {
			return getToken(PLSQLParser.PARALLEL_ENABLE, i);
		}
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode PIPELINED(int i) {
			return getToken(PLSQLParser.PIPELINED, i);
		}
		public List<TerminalNode> RESULT_CACHE() { return getTokens(PLSQLParser.RESULT_CACHE); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public List<TerminalNode> PARALLEL_ENABLE() { return getTokens(PLSQLParser.PARALLEL_ENABLE); }
		public List<TerminalNode> PIPELINED() { return getTokens(PLSQLParser.PIPELINED); }
		public Function_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFunction_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFunction_definition(this);
		}
	}

	public final Function_definitionContext function_definition() throws RecognitionException {
		Function_definitionContext _localctx = new Function_definitionContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_function_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494); function_heading();
			setState(498);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) {
				{
				{
				setState(495);
				_la = _input.LA(1);
				if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DETERMINISTIC - 65)) | (1L << (PARALLEL_ENABLE - 65)) | (1L << (PIPELINED - 65)) | (1L << (RESULT_CACHE - 65)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				}
				setState(500);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(501);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(503);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(502); declare_section();
				}
				break;
			}
			setState(505); body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Procedure_declaration_or_definitionContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public Procedure_headingContext procedure_heading() {
			return getRuleContext(Procedure_headingContext.class,0);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public Procedure_declaration_or_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procedure_declaration_or_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterProcedure_declaration_or_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitProcedure_declaration_or_definition(this);
		}
	}

	public final Procedure_declaration_or_definitionContext procedure_declaration_or_definition() throws RecognitionException {
		Procedure_declaration_or_definitionContext _localctx = new Procedure_declaration_or_definitionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_procedure_declaration_or_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507); procedure_heading();
			setState(513);
			_la = _input.LA(1);
			if (_la==AS || _la==IS) {
				{
				setState(508);
				_la = _input.LA(1);
				if ( !(_la==AS || _la==IS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(510);
				switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
				case 1:
					{
					setState(509); declare_section();
					}
					break;
				}
				setState(512); body();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Procedure_declarationContext extends ParserRuleContext {
		public Procedure_headingContext procedure_heading() {
			return getRuleContext(Procedure_headingContext.class,0);
		}
		public Procedure_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procedure_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterProcedure_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitProcedure_declaration(this);
		}
	}

	public final Procedure_declarationContext procedure_declaration() throws RecognitionException {
		Procedure_declarationContext _localctx = new Procedure_declarationContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_procedure_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515); procedure_heading();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Procedure_definitionContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public Procedure_headingContext procedure_heading() {
			return getRuleContext(Procedure_headingContext.class,0);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public Procedure_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_procedure_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterProcedure_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitProcedure_definition(this);
		}
	}

	public final Procedure_definitionContext procedure_definition() throws RecognitionException {
		Procedure_definitionContext _localctx = new Procedure_definitionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_procedure_definition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(517); procedure_heading();
			setState(518);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(520);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(519); declare_section();
				}
				break;
			}
			setState(522); body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BodyContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public List<Exception_handlerContext> exception_handler() {
			return getRuleContexts(Exception_handlerContext.class);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public List<PragmaContext> pragma() {
			return getRuleContexts(PragmaContext.class);
		}
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public TerminalNode BEGIN() { return getToken(PLSQLParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public Exception_handlerContext exception_handler(int i) {
			return getRuleContext(Exception_handlerContext.class,i);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public PragmaContext pragma(int i) {
			return getRuleContext(PragmaContext.class,i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public TerminalNode EXCEPTION() { return getToken(PLSQLParser.EXCEPTION, 0); }
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBody(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524); match(BEGIN);
			setState(525); statement();
			setState(526); match(SEMI);
			setState(535);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (PRAGMA - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0)) {
				{
				setState(533);
				switch (_input.LA(1)) {
				case CASE:
				case COMMIT:
				case DELETE:
				case FETCH:
				case FOR:
				case FORALL:
				case GOTO:
				case IF:
				case INSERT:
				case LOCK:
				case NULL:
				case OPEN:
				case RAISE:
				case ROLLBACK:
				case SAVEPOINT:
				case SELECT:
				case SET:
				case UPDATE:
				case WHILE:
				case BEGIN:
				case CLOSE:
				case CONTINUE:
				case DECLARE:
				case EXECUTE:
				case EXIT:
				case LOOP:
				case RETURN:
				case ID:
				case COLON:
				case LLABEL:
					{
					setState(527); statement();
					setState(528); match(SEMI);
					}
					break;
				case PRAGMA:
					{
					setState(530); pragma();
					setState(531); match(SEMI);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(537);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(544);
			_la = _input.LA(1);
			if (_la==EXCEPTION) {
				{
				setState(538); match(EXCEPTION);
				setState(540); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(539); exception_handler();
					}
					}
					setState(542); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WHEN );
				}
			}

			setState(546); match(END);
			setState(548);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(547); match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exception_handlerContext extends ParserRuleContext {
		public Qual_idContext qual_id(int i) {
			return getRuleContext(Qual_idContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode THEN() { return getToken(PLSQLParser.THEN, 0); }
		public List<Qual_idContext> qual_id() {
			return getRuleContexts(Qual_idContext.class);
		}
		public TerminalNode OTHERS() { return getToken(PLSQLParser.OTHERS, 0); }
		public TerminalNode WHEN() { return getToken(PLSQLParser.WHEN, 0); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public List<TerminalNode> OR() { return getTokens(PLSQLParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(PLSQLParser.OR, i);
		}
		public Exception_handlerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exception_handler; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterException_handler(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitException_handler(this);
		}
	}

	public final Exception_handlerContext exception_handler() throws RecognitionException {
		Exception_handlerContext _localctx = new Exception_handlerContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_exception_handler);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(550); match(WHEN);
			setState(560);
			switch (_input.LA(1)) {
			case ID:
			case COLON:
				{
				setState(551); qual_id();
				setState(556);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==OR) {
					{
					{
					setState(552); match(OR);
					setState(553); qual_id();
					}
					}
					setState(558);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case OTHERS:
				{
				setState(559); match(OTHERS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(562); match(THEN);
			setState(566); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(563); statement();
				setState(564); match(SEMI);
				}
				}
				setState(568); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public Null_statementContext null_statement() {
			return getRuleContext(Null_statementContext.class,0);
		}
		public Return_statementContext return_statement() {
			return getRuleContext(Return_statementContext.class,0);
		}
		public Raise_statementContext raise_statement() {
			return getRuleContext(Raise_statementContext.class,0);
		}
		public While_loop_statementContext while_loop_statement() {
			return getRuleContext(While_loop_statementContext.class,0);
		}
		public Plsql_blockContext plsql_block() {
			return getRuleContext(Plsql_blockContext.class,0);
		}
		public Execute_immediate_statementContext execute_immediate_statement() {
			return getRuleContext(Execute_immediate_statementContext.class,0);
		}
		public Fetch_statementContext fetch_statement() {
			return getRuleContext(Fetch_statementContext.class,0);
		}
		public Close_statementContext close_statement() {
			return getRuleContext(Close_statementContext.class,0);
		}
		public Basic_loop_statementContext basic_loop_statement() {
			return getRuleContext(Basic_loop_statementContext.class,0);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public Case_statementContext case_statement() {
			return getRuleContext(Case_statementContext.class,0);
		}
		public Sql_statementContext sql_statement() {
			return getRuleContext(Sql_statementContext.class,0);
		}
		public For_loop_statementContext for_loop_statement() {
			return getRuleContext(For_loop_statementContext.class,0);
		}
		public Forall_statementContext forall_statement() {
			return getRuleContext(Forall_statementContext.class,0);
		}
		public If_statementContext if_statement() {
			return getRuleContext(If_statementContext.class,0);
		}
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public Continue_statementContext continue_statement() {
			return getRuleContext(Continue_statementContext.class,0);
		}
		public Exit_statementContext exit_statement() {
			return getRuleContext(Exit_statementContext.class,0);
		}
		public Assign_or_call_statementContext assign_or_call_statement() {
			return getRuleContext(Assign_or_call_statementContext.class,0);
		}
		public Goto_statementContext goto_statement() {
			return getRuleContext(Goto_statementContext.class,0);
		}
		public Open_statementContext open_statement() {
			return getRuleContext(Open_statementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LLABEL) {
				{
				{
				setState(570); label();
				}
				}
				setState(575);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(595);
			switch (_input.LA(1)) {
			case ID:
			case COLON:
				{
				setState(576); assign_or_call_statement();
				}
				break;
			case CASE:
				{
				setState(577); case_statement();
				}
				break;
			case CLOSE:
				{
				setState(578); close_statement();
				}
				break;
			case CONTINUE:
				{
				setState(579); continue_statement();
				}
				break;
			case LOOP:
				{
				setState(580); basic_loop_statement();
				}
				break;
			case EXECUTE:
				{
				setState(581); execute_immediate_statement();
				}
				break;
			case EXIT:
				{
				setState(582); exit_statement();
				}
				break;
			case FETCH:
				{
				setState(583); fetch_statement();
				}
				break;
			case FOR:
				{
				setState(584); for_loop_statement();
				}
				break;
			case FORALL:
				{
				setState(585); forall_statement();
				}
				break;
			case GOTO:
				{
				setState(586); goto_statement();
				}
				break;
			case IF:
				{
				setState(587); if_statement();
				}
				break;
			case NULL:
				{
				setState(588); null_statement();
				}
				break;
			case OPEN:
				{
				setState(589); open_statement();
				}
				break;
			case BEGIN:
			case DECLARE:
				{
				setState(590); plsql_block();
				}
				break;
			case RAISE:
				{
				setState(591); raise_statement();
				}
				break;
			case RETURN:
				{
				setState(592); return_statement();
				}
				break;
			case COMMIT:
			case DELETE:
			case INSERT:
			case LOCK:
			case ROLLBACK:
			case SAVEPOINT:
			case SELECT:
			case SET:
			case UPDATE:
				{
				setState(593); sql_statement();
				}
				break;
			case WHILE:
				{
				setState(594); while_loop_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalueContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(PLSQLParser.DOT); }
		public List<CallContext> call() {
			return getRuleContexts(CallContext.class);
		}
		public CallContext call(int i) {
			return getRuleContext(CallContext.class,i);
		}
		public TerminalNode DOT(int i) {
			return getToken(PLSQLParser.DOT, i);
		}
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterLvalue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitLvalue(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_lvalue);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(597); call();
			setState(602);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(598); match(DOT);
					setState(599); call();
					}
					} 
				}
				setState(604);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,55,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_or_call_statementContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode ASSIGN() { return getToken(PLSQLParser.ASSIGN, 0); }
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public Delete_callContext delete_call() {
			return getRuleContext(Delete_callContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Assign_or_call_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_or_call_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAssign_or_call_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAssign_or_call_statement(this);
		}
	}

	public final Assign_or_call_statementContext assign_or_call_statement() throws RecognitionException {
		Assign_or_call_statementContext _localctx = new Assign_or_call_statementContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_assign_or_call_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(605); lvalue();
			setState(610);
			switch (_input.LA(1)) {
			case DOT:
				{
				setState(606); match(DOT);
				setState(607); delete_call();
				}
				break;
			case ASSIGN:
				{
				setState(608); match(ASSIGN);
				setState(609); expression();
				}
				break;
			case SEMI:
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CallContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public TerminalNode COLON() { return getToken(PLSQLParser.COLON, 0); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public CallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCall(this);
		}
	}

	public final CallContext call() throws RecognitionException {
		CallContext _localctx = new CallContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_call);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(613);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(612); match(COLON);
				}
			}

			setState(615); match(ID);
			setState(628);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(616); match(LPAREN);
				setState(625);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FALSE) | (1L << NOT) | (1L << NULL) | (1L << SQL) | (1L << TRUE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING))) != 0) || ((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (QUOTED_STRING - 91)) | (1L << (ID - 91)) | (1L << (COLON - 91)) | (1L << (LPAREN - 91)) | (1L << (PLUS - 91)) | (1L << (MINUS - 91)) | (1L << (INTEGER - 91)) | (1L << (REAL_NUMBER - 91)))) != 0)) {
					{
					setState(617); parameter();
					setState(622);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(618); match(COMMA);
						setState(619); parameter();
						}
						}
						setState(624);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(627); match(RPAREN);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Delete_callContext extends ParserRuleContext {
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public TerminalNode DELETE() { return getToken(PLSQLParser.DELETE, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public Delete_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterDelete_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitDelete_call(this);
		}
	}

	public final Delete_callContext delete_call() throws RecognitionException {
		Delete_callContext _localctx = new Delete_callContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_delete_call);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(630); match(DELETE);
			setState(636);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(631); match(LPAREN);
				setState(633);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FALSE) | (1L << NOT) | (1L << NULL) | (1L << SQL) | (1L << TRUE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING))) != 0) || ((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (QUOTED_STRING - 91)) | (1L << (ID - 91)) | (1L << (COLON - 91)) | (1L << (LPAREN - 91)) | (1L << (PLUS - 91)) | (1L << (MINUS - 91)) | (1L << (INTEGER - 91)) | (1L << (REAL_NUMBER - 91)))) != 0)) {
					{
					setState(632); parameter();
					}
				}

				setState(635); match(RPAREN);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Basic_loop_statementContext extends ParserRuleContext {
		public List<TerminalNode> LOOP() { return getTokens(PLSQLParser.LOOP); }
		public Label_nameContext label_name() {
			return getRuleContext(Label_nameContext.class,0);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode LOOP(int i) {
			return getToken(PLSQLParser.LOOP, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public Basic_loop_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basic_loop_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBasic_loop_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBasic_loop_statement(this);
		}
	}

	public final Basic_loop_statementContext basic_loop_statement() throws RecognitionException {
		Basic_loop_statementContext _localctx = new Basic_loop_statementContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_basic_loop_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638); match(LOOP);
			setState(642); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(639); statement();
				setState(640); match(SEMI);
				}
				}
				setState(644); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
			setState(646); match(END);
			setState(647); match(LOOP);
			setState(649);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(648); label_name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Case_statementContext extends ParserRuleContext {
		public List<TerminalNode> CASE() { return getTokens(PLSQLParser.CASE); }
		public TerminalNode ELSE() { return getToken(PLSQLParser.ELSE, 0); }
		public Label_nameContext label_name() {
			return getRuleContext(Label_nameContext.class,0);
		}
		public List<TerminalNode> THEN() { return getTokens(PLSQLParser.THEN); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode CASE(int i) {
			return getToken(PLSQLParser.CASE, i);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public List<TerminalNode> WHEN() { return getTokens(PLSQLParser.WHEN); }
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public TerminalNode WHEN(int i) {
			return getToken(PLSQLParser.WHEN, i);
		}
		public TerminalNode THEN(int i) {
			return getToken(PLSQLParser.THEN, i);
		}
		public Case_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_case_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCase_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCase_statement(this);
		}
	}

	public final Case_statementContext case_statement() throws RecognitionException {
		Case_statementContext _localctx = new Case_statementContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_case_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651); match(CASE);
			setState(653);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FALSE) | (1L << NOT) | (1L << NULL) | (1L << SQL) | (1L << TRUE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING))) != 0) || ((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (QUOTED_STRING - 91)) | (1L << (ID - 91)) | (1L << (COLON - 91)) | (1L << (LPAREN - 91)) | (1L << (PLUS - 91)) | (1L << (MINUS - 91)) | (1L << (INTEGER - 91)) | (1L << (REAL_NUMBER - 91)))) != 0)) {
				{
				setState(652); expression();
				}
			}

			setState(665); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(655); match(WHEN);
				setState(656); expression();
				setState(657); match(THEN);
				setState(661); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(658); statement();
					setState(659); match(SEMI);
					}
					}
					setState(663); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
				}
				}
				setState(667); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WHEN );
			setState(673);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(669); match(ELSE);
				setState(670); statement();
				setState(671); match(SEMI);
				}
			}

			setState(675); match(END);
			setState(676); match(CASE);
			setState(678);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(677); label_name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Close_statementContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode CLOSE() { return getToken(PLSQLParser.CLOSE, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public Close_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_close_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterClose_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitClose_statement(this);
		}
	}

	public final Close_statementContext close_statement() throws RecognitionException {
		Close_statementContext _localctx = new Close_statementContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_close_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(680); match(CLOSE);
			setState(681); match(ID);
			setState(684);
			_la = _input.LA(1);
			if (_la==DOT) {
				{
				setState(682); match(DOT);
				setState(683); match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Continue_statementContext extends ParserRuleContext {
		public Token lbl;
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode CONTINUE() { return getToken(PLSQLParser.CONTINUE, 0); }
		public TerminalNode WHEN() { return getToken(PLSQLParser.WHEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Continue_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continue_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterContinue_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitContinue_statement(this);
		}
	}

	public final Continue_statementContext continue_statement() throws RecognitionException {
		Continue_statementContext _localctx = new Continue_statementContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_continue_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686); match(CONTINUE);
			setState(688);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(687); ((Continue_statementContext)_localctx).lbl = match(ID);
				}
			}

			setState(692);
			_la = _input.LA(1);
			if (_la==WHEN) {
				{
				setState(690); match(WHEN);
				setState(691); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Execute_immediate_statementContext extends ParserRuleContext {
		public Bulk_collect_into_clauseContext bulk_collect_into_clause() {
			return getRuleContext(Bulk_collect_into_clauseContext.class,0);
		}
		public Dynamic_returning_clauseContext dynamic_returning_clause() {
			return getRuleContext(Dynamic_returning_clauseContext.class,0);
		}
		public TerminalNode IMMEDIATE() { return getToken(PLSQLParser.IMMEDIATE, 0); }
		public Using_clauseContext using_clause() {
			return getRuleContext(Using_clauseContext.class,0);
		}
		public Into_clauseContext into_clause() {
			return getRuleContext(Into_clauseContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXECUTE() { return getToken(PLSQLParser.EXECUTE, 0); }
		public Execute_immediate_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_execute_immediate_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterExecute_immediate_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitExecute_immediate_statement(this);
		}
	}

	public final Execute_immediate_statementContext execute_immediate_statement() throws RecognitionException {
		Execute_immediate_statementContext _localctx = new Execute_immediate_statementContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_execute_immediate_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(694); match(EXECUTE);
			setState(695); match(IMMEDIATE);
			setState(696); expression();
			setState(709);
			switch (_input.LA(1)) {
			case BULK:
			case INTO:
				{
				setState(699);
				switch (_input.LA(1)) {
				case INTO:
					{
					setState(697); into_clause();
					}
					break;
				case BULK:
					{
					setState(698); bulk_collect_into_clause();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(702);
				_la = _input.LA(1);
				if (_la==USING) {
					{
					setState(701); using_clause();
					}
				}

				}
				break;
			case USING:
				{
				setState(704); using_clause();
				setState(706);
				_la = _input.LA(1);
				if (_la==RETURN || _la==RETURNING) {
					{
					setState(705); dynamic_returning_clause();
					}
				}

				}
				break;
			case RETURN:
			case RETURNING:
				{
				setState(708); dynamic_returning_clause();
				}
				break;
			case SEMI:
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exit_statementContext extends ParserRuleContext {
		public Token lbl;
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode WHEN() { return getToken(PLSQLParser.WHEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EXIT() { return getToken(PLSQLParser.EXIT, 0); }
		public Exit_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exit_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterExit_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitExit_statement(this);
		}
	}

	public final Exit_statementContext exit_statement() throws RecognitionException {
		Exit_statementContext _localctx = new Exit_statementContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_exit_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(711); match(EXIT);
			setState(713);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(712); ((Exit_statementContext)_localctx).lbl = match(ID);
				}
			}

			setState(717);
			_la = _input.LA(1);
			if (_la==WHEN) {
				{
				setState(715); match(WHEN);
				setState(716); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Fetch_statementContext extends ParserRuleContext {
		public Bulk_collect_into_clauseContext bulk_collect_into_clause() {
			return getRuleContext(Bulk_collect_into_clauseContext.class,0);
		}
		public TerminalNode LIMIT() { return getToken(PLSQLParser.LIMIT, 0); }
		public Qual_idContext qual_id() {
			return getRuleContext(Qual_idContext.class,0);
		}
		public Into_clauseContext into_clause() {
			return getRuleContext(Into_clauseContext.class,0);
		}
		public TerminalNode FETCH() { return getToken(PLSQLParser.FETCH, 0); }
		public Numeric_expressionContext numeric_expression() {
			return getRuleContext(Numeric_expressionContext.class,0);
		}
		public Fetch_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fetch_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFetch_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFetch_statement(this);
		}
	}

	public final Fetch_statementContext fetch_statement() throws RecognitionException {
		Fetch_statementContext _localctx = new Fetch_statementContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_fetch_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(719); match(FETCH);
			setState(720); qual_id();
			setState(727);
			switch (_input.LA(1)) {
			case INTO:
				{
				setState(721); into_clause();
				}
				break;
			case BULK:
				{
				setState(722); bulk_collect_into_clause();
				setState(725);
				_la = _input.LA(1);
				if (_la==LIMIT) {
					{
					setState(723); match(LIMIT);
					setState(724); numeric_expression();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Into_clauseContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public List<LvalueContext> lvalue() {
			return getRuleContexts(LvalueContext.class);
		}
		public LvalueContext lvalue(int i) {
			return getRuleContext(LvalueContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public TerminalNode INTO() { return getToken(PLSQLParser.INTO, 0); }
		public Into_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_into_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterInto_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitInto_clause(this);
		}
	}

	public final Into_clauseContext into_clause() throws RecognitionException {
		Into_clauseContext _localctx = new Into_clauseContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_into_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(729); match(INTO);
			setState(730); lvalue();
			setState(735);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(731); match(COMMA);
				setState(732); lvalue();
				}
				}
				setState(737);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bulk_collect_into_clauseContext extends ParserRuleContext {
		public TerminalNode BULK() { return getToken(PLSQLParser.BULK, 0); }
		public TerminalNode COLLECT() { return getToken(PLSQLParser.COLLECT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public List<LvalueContext> lvalue() {
			return getRuleContexts(LvalueContext.class);
		}
		public LvalueContext lvalue(int i) {
			return getRuleContext(LvalueContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public TerminalNode INTO() { return getToken(PLSQLParser.INTO, 0); }
		public Bulk_collect_into_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bulk_collect_into_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBulk_collect_into_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBulk_collect_into_clause(this);
		}
	}

	public final Bulk_collect_into_clauseContext bulk_collect_into_clause() throws RecognitionException {
		Bulk_collect_into_clauseContext _localctx = new Bulk_collect_into_clauseContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_bulk_collect_into_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(738); match(BULK);
			setState(739); match(COLLECT);
			setState(740); match(INTO);
			setState(741); lvalue();
			setState(746);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(742); match(COMMA);
				setState(743); lvalue();
				}
				}
				setState(748);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Using_clauseContext extends ParserRuleContext {
		public List<Param_modifiersContext> param_modifiers() {
			return getRuleContexts(Param_modifiersContext.class);
		}
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode USING() { return getToken(PLSQLParser.USING, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public Param_modifiersContext param_modifiers(int i) {
			return getRuleContext(Param_modifiersContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public Using_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_using_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterUsing_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitUsing_clause(this);
		}
	}

	public final Using_clauseContext using_clause() throws RecognitionException {
		Using_clauseContext _localctx = new Using_clauseContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_using_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(749); match(USING);
			setState(751);
			_la = _input.LA(1);
			if (_la==IN || _la==OUT) {
				{
				setState(750); param_modifiers();
				}
			}

			setState(753); expression();
			setState(761);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(754); match(COMMA);
				setState(756);
				_la = _input.LA(1);
				if (_la==IN || _la==OUT) {
					{
					setState(755); param_modifiers();
					}
				}

				setState(758); expression();
				}
				}
				setState(763);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_modifiersContext extends ParserRuleContext {
		public TerminalNode IN() { return getToken(PLSQLParser.IN, 0); }
		public TerminalNode OUT() { return getToken(PLSQLParser.OUT, 0); }
		public Param_modifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_modifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterParam_modifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitParam_modifiers(this);
		}
	}

	public final Param_modifiersContext param_modifiers() throws RecognitionException {
		Param_modifiersContext _localctx = new Param_modifiersContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_param_modifiers);
		int _la;
		try {
			setState(769);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(764); match(IN);
				setState(766);
				_la = _input.LA(1);
				if (_la==OUT) {
					{
					setState(765); match(OUT);
					}
				}

				}
				break;
			case OUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(768); match(OUT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Dynamic_returning_clauseContext extends ParserRuleContext {
		public Bulk_collect_into_clauseContext bulk_collect_into_clause() {
			return getRuleContext(Bulk_collect_into_clauseContext.class,0);
		}
		public TerminalNode RETURN() { return getToken(PLSQLParser.RETURN, 0); }
		public Into_clauseContext into_clause() {
			return getRuleContext(Into_clauseContext.class,0);
		}
		public TerminalNode RETURNING() { return getToken(PLSQLParser.RETURNING, 0); }
		public Dynamic_returning_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dynamic_returning_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterDynamic_returning_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitDynamic_returning_clause(this);
		}
	}

	public final Dynamic_returning_clauseContext dynamic_returning_clause() throws RecognitionException {
		Dynamic_returning_clauseContext _localctx = new Dynamic_returning_clauseContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_dynamic_returning_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(771);
			_la = _input.LA(1);
			if ( !(_la==RETURN || _la==RETURNING) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(774);
			switch (_input.LA(1)) {
			case INTO:
				{
				setState(772); into_clause();
				}
				break;
			case BULK:
				{
				setState(773); bulk_collect_into_clause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class For_loop_statementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public List<TerminalNode> LOOP() { return getTokens(PLSQLParser.LOOP); }
		public Label_nameContext label_name() {
			return getRuleContext(Label_nameContext.class,0);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode FOR() { return getToken(PLSQLParser.FOR, 0); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode LOOP(int i) {
			return getToken(PLSQLParser.LOOP, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public TerminalNode IN() { return getToken(PLSQLParser.IN, 0); }
		public For_loop_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_loop_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterFor_loop_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitFor_loop_statement(this);
		}
	}

	public final For_loop_statementContext for_loop_statement() throws RecognitionException {
		For_loop_statementContext _localctx = new For_loop_statementContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_for_loop_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(776); match(FOR);
			setState(777); match(ID);
			setState(778); match(IN);
			setState(780); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(779);
				_la = _input.LA(1);
				if ( _la <= 0 || (_la==LOOP) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				}
				setState(782); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << ARRAY) | (1L << AS) | (1L << AUTHID) | (1L << BETWEEN) | (1L << BODY) | (1L << BULK) | (1L << BULK_ROWCOUNT) | (1L << BY) | (1L << CASE) | (1L << CREATE) | (1L << COLLECT) | (1L << COMMIT) | (1L << CURRENT_USER) | (1L << DEFAULT) | (1L << DEFINER) | (1L << DELETE) | (1L << ELSE) | (1L << ELSIF) | (1L << EXTERNAL) | (1L << FALSE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << IN) | (1L << INDEX) | (1L << INSERT) | (1L << INTO) | (1L << IS) | (1L << LANGUAGE) | (1L << LIKE) | (1L << LIMIT) | (1L << LOCK) | (1L << NOT) | (1L << NOTFOUND) | (1L << NULL) | (1L << OPEN) | (1L << OR) | (1L << PACKAGE) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << SQL) | (1L << TABLE) | (1L << TRANSACTION) | (1L << TRUE) | (1L << THEN) | (1L << UPDATE) | (1L << WHILE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING) | (1L << ISOPEN) | (1L << EXISTS) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONSTANT) | (1L << CONTINUE) | (1L << CURSOR))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (DETERMINISTIC - 64)) | (1L << (END - 64)) | (1L << (EXCEPTION - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (FUNCTION - 64)) | (1L << (IMMEDIATE - 64)) | (1L << (NOCOPY - 64)) | (1L << (OTHERS - 64)) | (1L << (OUT - 64)) | (1L << (PARALLEL_ENABLE - 64)) | (1L << (PIPELINED - 64)) | (1L << (PRAGMA - 64)) | (1L << (PROCEDURE - 64)) | (1L << (RECORD - 64)) | (1L << (REF - 64)) | (1L << (RESULT_CACHE - 64)) | (1L << (RETURN - 64)) | (1L << (RETURNING - 64)) | (1L << (ROWTYPE - 64)) | (1L << (SUBTYPE - 64)) | (1L << (USING - 64)) | (1L << (VARRAY - 64)) | (1L << (VARYING - 64)) | (1L << (WHEN - 64)) | (1L << (QUOTED_STRING - 64)) | (1L << (ID - 64)) | (1L << (SEMI - 64)) | (1L << (COLON - 64)) | (1L << (DOUBLEDOT - 64)) | (1L << (DOT - 64)) | (1L << (COMMA - 64)) | (1L << (EXPONENT - 64)) | (1L << (ASTERISK - 64)) | (1L << (AT_SIGN - 64)) | (1L << (RPAREN - 64)) | (1L << (LPAREN - 64)) | (1L << (RBRACK - 64)) | (1L << (LBRACK - 64)) | (1L << (PLUS - 64)) | (1L << (MINUS - 64)) | (1L << (DIVIDE - 64)) | (1L << (EQ - 64)) | (1L << (PERCENT - 64)) | (1L << (LLABEL - 64)) | (1L << (RLABEL - 64)) | (1L << (ASSIGN - 64)) | (1L << (ARROW - 64)) | (1L << (VERTBAR - 64)) | (1L << (DOUBLEVERTBAR - 64)) | (1L << (NOT_EQ - 64)) | (1L << (LTH - 64)) | (1L << (LEQ - 64)) | (1L << (GTH - 64)) | (1L << (GEQ - 64)) | (1L << (INTEGER - 64)) | (1L << (REAL_NUMBER - 64)) | (1L << (WS - 64)) | (1L << (SL_COMMENT - 64)) | (1L << (ML_COMMENT - 64)))) != 0) );
			setState(784); match(LOOP);
			setState(788); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(785); statement();
				setState(786); match(SEMI);
				}
				}
				setState(790); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
			setState(792); match(END);
			setState(793); match(LOOP);
			setState(795);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(794); label_name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Forall_statementContext extends ParserRuleContext {
		public Bounds_clauseContext bounds_clause() {
			return getRuleContext(Bounds_clauseContext.class,0);
		}
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public Sql_statementContext sql_statement() {
			return getRuleContext(Sql_statementContext.class,0);
		}
		public TerminalNode FORALL() { return getToken(PLSQLParser.FORALL, 0); }
		public KEXCEPTIONSContext kEXCEPTIONS() {
			return getRuleContext(KEXCEPTIONSContext.class,0);
		}
		public KSAVEContext kSAVE() {
			return getRuleContext(KSAVEContext.class,0);
		}
		public TerminalNode IN() { return getToken(PLSQLParser.IN, 0); }
		public Forall_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forall_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterForall_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitForall_statement(this);
		}
	}

	public final Forall_statementContext forall_statement() throws RecognitionException {
		Forall_statementContext _localctx = new Forall_statementContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_forall_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797); match(FORALL);
			setState(798); match(ID);
			setState(799); match(IN);
			setState(800); bounds_clause();
			setState(801); sql_statement();
			setState(805);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				{
				setState(802); kSAVE();
				setState(803); kEXCEPTIONS();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bounds_clauseContext extends ParserRuleContext {
		public TerminalNode BETWEEN() { return getToken(PLSQLParser.BETWEEN, 0); }
		public Numeric_expressionContext numeric_expression(int i) {
			return getRuleContext(Numeric_expressionContext.class,i);
		}
		public TerminalNode DOUBLEDOT() { return getToken(PLSQLParser.DOUBLEDOT, 0); }
		public KINDICESContext kINDICES() {
			return getRuleContext(KINDICESContext.class,0);
		}
		public KOFContext kOF() {
			return getRuleContext(KOFContext.class,0);
		}
		public TerminalNode AND() { return getToken(PLSQLParser.AND, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public KVALUESContext kVALUES() {
			return getRuleContext(KVALUESContext.class,0);
		}
		public List<Numeric_expressionContext> numeric_expression() {
			return getRuleContexts(Numeric_expressionContext.class);
		}
		public Bounds_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bounds_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBounds_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBounds_clause(this);
		}
	}

	public final Bounds_clauseContext bounds_clause() throws RecognitionException {
		Bounds_clauseContext _localctx = new Bounds_clauseContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_bounds_clause);
		int _la;
		try {
			setState(825);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(807); numeric_expression();
				setState(808); match(DOUBLEDOT);
				setState(809); numeric_expression();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(811); kINDICES();
				setState(812); kOF();
				setState(813); atom();
				setState(819);
				_la = _input.LA(1);
				if (_la==BETWEEN) {
					{
					setState(814); match(BETWEEN);
					setState(815); numeric_expression();
					setState(816); match(AND);
					setState(817); numeric_expression();
					}
				}

				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(821); kVALUES();
				setState(822); kOF();
				setState(823); atom();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Goto_statementContext extends ParserRuleContext {
		public Label_nameContext label_name() {
			return getRuleContext(Label_nameContext.class,0);
		}
		public TerminalNode GOTO() { return getToken(PLSQLParser.GOTO, 0); }
		public Goto_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goto_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterGoto_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitGoto_statement(this);
		}
	}

	public final Goto_statementContext goto_statement() throws RecognitionException {
		Goto_statementContext _localctx = new Goto_statementContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_goto_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(827); match(GOTO);
			setState(828); label_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_statementContext extends ParserRuleContext {
		public TerminalNode ELSIF(int i) {
			return getToken(PLSQLParser.ELSIF, i);
		}
		public TerminalNode ELSE() { return getToken(PLSQLParser.ELSE, 0); }
		public List<TerminalNode> IF() { return getTokens(PLSQLParser.IF); }
		public List<TerminalNode> ELSIF() { return getTokens(PLSQLParser.ELSIF); }
		public List<TerminalNode> THEN() { return getTokens(PLSQLParser.THEN); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode IF(int i) {
			return getToken(PLSQLParser.IF, i);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public TerminalNode THEN(int i) {
			return getToken(PLSQLParser.THEN, i);
		}
		public If_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterIf_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitIf_statement(this);
		}
	}

	public final If_statementContext if_statement() throws RecognitionException {
		If_statementContext _localctx = new If_statementContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_if_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(830); match(IF);
			setState(831); expression();
			setState(832); match(THEN);
			setState(836); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(833); statement();
				setState(834); match(SEMI);
				}
				}
				setState(838); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
			setState(852);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ELSIF) {
				{
				{
				setState(840); match(ELSIF);
				setState(841); expression();
				setState(842); match(THEN);
				setState(846); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(843); statement();
					setState(844); match(SEMI);
					}
					}
					setState(848); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
				}
				}
				setState(854);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(863);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(855); match(ELSE);
				setState(859); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(856); statement();
					setState(857); match(SEMI);
					}
					}
					setState(861); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
				}
			}

			setState(865); match(END);
			setState(866); match(IF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Null_statementContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public Null_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNull_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNull_statement(this);
		}
	}

	public final Null_statementContext null_statement() throws RecognitionException {
		Null_statementContext _localctx = new Null_statementContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_null_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(868); match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Open_statementContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public List<TerminalNode> DOT() { return getTokens(PLSQLParser.DOT); }
		public TerminalNode OPEN() { return getToken(PLSQLParser.OPEN, 0); }
		public Select_statementContext select_statement() {
			return getRuleContext(Select_statementContext.class,0);
		}
		public TerminalNode FOR() { return getToken(PLSQLParser.FOR, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode DOT(int i) {
			return getToken(PLSQLParser.DOT, i);
		}
		public Call_argsContext call_args() {
			return getRuleContext(Call_argsContext.class,0);
		}
		public Open_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_open_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterOpen_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitOpen_statement(this);
		}
	}

	public final Open_statementContext open_statement() throws RecognitionException {
		Open_statementContext _localctx = new Open_statementContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_open_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(870); match(OPEN);
			setState(871); match(ID);
			setState(876);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(872); match(DOT);
				setState(873); match(ID);
				}
				}
				setState(878);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(880);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(879); call_args();
				}
			}

			setState(884);
			_la = _input.LA(1);
			if (_la==FOR) {
				{
				setState(882); match(FOR);
				setState(883); select_statement();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PragmaContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode PRAGMA() { return getToken(PLSQLParser.PRAGMA, 0); }
		public PragmaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterPragma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitPragma(this);
		}
	}

	public final PragmaContext pragma() throws RecognitionException {
		PragmaContext _localctx = new PragmaContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_pragma);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(886); match(PRAGMA);
			setState(887); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Raise_statementContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public List<TerminalNode> DOT() { return getTokens(PLSQLParser.DOT); }
		public TerminalNode RAISE() { return getToken(PLSQLParser.RAISE, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode DOT(int i) {
			return getToken(PLSQLParser.DOT, i);
		}
		public Raise_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_raise_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterRaise_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitRaise_statement(this);
		}
	}

	public final Raise_statementContext raise_statement() throws RecognitionException {
		Raise_statementContext _localctx = new Raise_statementContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_raise_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(889); match(RAISE);
			setState(898);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(890); match(ID);
				setState(895);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(891); match(DOT);
					setState(892); match(ID);
					}
					}
					setState(897);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Return_statementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(PLSQLParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Return_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_return_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterReturn_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitReturn_statement(this);
		}
	}

	public final Return_statementContext return_statement() throws RecognitionException {
		Return_statementContext _localctx = new Return_statementContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_return_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(900); match(RETURN);
			setState(902);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FALSE) | (1L << NOT) | (1L << NULL) | (1L << SQL) | (1L << TRUE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING))) != 0) || ((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (QUOTED_STRING - 91)) | (1L << (ID - 91)) | (1L << (COLON - 91)) | (1L << (LPAREN - 91)) | (1L << (PLUS - 91)) | (1L << (MINUS - 91)) | (1L << (INTEGER - 91)) | (1L << (REAL_NUMBER - 91)))) != 0)) {
				{
				setState(901); expression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Plsql_blockContext extends ParserRuleContext {
		public TerminalNode DECLARE() { return getToken(PLSQLParser.DECLARE, 0); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public Plsql_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plsql_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterPlsql_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitPlsql_block(this);
		}
	}

	public final Plsql_blockContext plsql_block() throws RecognitionException {
		Plsql_blockContext _localctx = new Plsql_blockContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_plsql_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(906);
			_la = _input.LA(1);
			if (_la==DECLARE) {
				{
				setState(904); match(DECLARE);
				setState(905); declare_section();
				}
			}

			setState(908); body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode RLABEL() { return getToken(PLSQLParser.RLABEL, 0); }
		public TerminalNode LLABEL() { return getToken(PLSQLParser.LLABEL, 0); }
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitLabel(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(910); match(LLABEL);
			setState(911); label();
			setState(912); match(RLABEL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Qual_idContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public List<TerminalNode> DOT() { return getTokens(PLSQLParser.DOT); }
		public TerminalNode COLON(int i) {
			return getToken(PLSQLParser.COLON, i);
		}
		public List<TerminalNode> COLON() { return getTokens(PLSQLParser.COLON); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode DOT(int i) {
			return getToken(PLSQLParser.DOT, i);
		}
		public Qual_idContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qual_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterQual_id(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitQual_id(this);
		}
	}

	public final Qual_idContext qual_id() throws RecognitionException {
		Qual_idContext _localctx = new Qual_idContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_qual_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(915);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(914); match(COLON);
				}
			}

			setState(917); match(ID);
			setState(925);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(918); match(DOT);
				setState(920);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(919); match(COLON);
					}
				}

				setState(922); match(ID);
				}
				}
				setState(927);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_statementContext extends ParserRuleContext {
		public Update_statementContext update_statement() {
			return getRuleContext(Update_statementContext.class,0);
		}
		public Insert_statementContext insert_statement() {
			return getRuleContext(Insert_statementContext.class,0);
		}
		public Select_statementContext select_statement() {
			return getRuleContext(Select_statementContext.class,0);
		}
		public Rollback_statementContext rollback_statement() {
			return getRuleContext(Rollback_statementContext.class,0);
		}
		public Commit_statementContext commit_statement() {
			return getRuleContext(Commit_statementContext.class,0);
		}
		public Delete_statementContext delete_statement() {
			return getRuleContext(Delete_statementContext.class,0);
		}
		public Lock_table_statementContext lock_table_statement() {
			return getRuleContext(Lock_table_statementContext.class,0);
		}
		public Set_transaction_statementContext set_transaction_statement() {
			return getRuleContext(Set_transaction_statementContext.class,0);
		}
		public Savepoint_statementContext savepoint_statement() {
			return getRuleContext(Savepoint_statementContext.class,0);
		}
		public Sql_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSql_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSql_statement(this);
		}
	}

	public final Sql_statementContext sql_statement() throws RecognitionException {
		Sql_statementContext _localctx = new Sql_statementContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_sql_statement);
		try {
			setState(937);
			switch (_input.LA(1)) {
			case COMMIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(928); commit_statement();
				}
				break;
			case DELETE:
				enterOuterAlt(_localctx, 2);
				{
				setState(929); delete_statement();
				}
				break;
			case INSERT:
				enterOuterAlt(_localctx, 3);
				{
				setState(930); insert_statement();
				}
				break;
			case LOCK:
				enterOuterAlt(_localctx, 4);
				{
				setState(931); lock_table_statement();
				}
				break;
			case ROLLBACK:
				enterOuterAlt(_localctx, 5);
				{
				setState(932); rollback_statement();
				}
				break;
			case SAVEPOINT:
				enterOuterAlt(_localctx, 6);
				{
				setState(933); savepoint_statement();
				}
				break;
			case SELECT:
				enterOuterAlt(_localctx, 7);
				{
				setState(934); select_statement();
				}
				break;
			case SET:
				enterOuterAlt(_localctx, 8);
				{
				setState(935); set_transaction_statement();
				}
				break;
			case UPDATE:
				enterOuterAlt(_localctx, 9);
				{
				setState(936); update_statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Commit_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode COMMIT() { return getToken(PLSQLParser.COMMIT, 0); }
		public Commit_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commit_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCommit_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCommit_statement(this);
		}
	}

	public final Commit_statementContext commit_statement() throws RecognitionException {
		Commit_statementContext _localctx = new Commit_statementContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_commit_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(939); match(COMMIT);
			setState(941);
			switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
			case 1:
				{
				setState(940); swallow_to_semi();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Delete_statementContext extends ParserRuleContext {
		public TerminalNode DELETE() { return getToken(PLSQLParser.DELETE, 0); }
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public Delete_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterDelete_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitDelete_statement(this);
		}
	}

	public final Delete_statementContext delete_statement() throws RecognitionException {
		Delete_statementContext _localctx = new Delete_statementContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_delete_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(943); match(DELETE);
			setState(944); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Insert_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode INSERT() { return getToken(PLSQLParser.INSERT, 0); }
		public Insert_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterInsert_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitInsert_statement(this);
		}
	}

	public final Insert_statementContext insert_statement() throws RecognitionException {
		Insert_statementContext _localctx = new Insert_statementContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_insert_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(946); match(INSERT);
			setState(947); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Lock_table_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode LOCK() { return getToken(PLSQLParser.LOCK, 0); }
		public TerminalNode TABLE() { return getToken(PLSQLParser.TABLE, 0); }
		public Lock_table_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lock_table_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterLock_table_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitLock_table_statement(this);
		}
	}

	public final Lock_table_statementContext lock_table_statement() throws RecognitionException {
		Lock_table_statementContext _localctx = new Lock_table_statementContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_lock_table_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(949); match(LOCK);
			setState(950); match(TABLE);
			setState(951); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rollback_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode ROLLBACK() { return getToken(PLSQLParser.ROLLBACK, 0); }
		public Rollback_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rollback_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterRollback_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitRollback_statement(this);
		}
	}

	public final Rollback_statementContext rollback_statement() throws RecognitionException {
		Rollback_statementContext _localctx = new Rollback_statementContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_rollback_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(953); match(ROLLBACK);
			setState(955);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				{
				setState(954); swallow_to_semi();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Savepoint_statementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode SAVEPOINT() { return getToken(PLSQLParser.SAVEPOINT, 0); }
		public Savepoint_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_savepoint_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSavepoint_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSavepoint_statement(this);
		}
	}

	public final Savepoint_statementContext savepoint_statement() throws RecognitionException {
		Savepoint_statementContext _localctx = new Savepoint_statementContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_savepoint_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(957); match(SAVEPOINT);
			setState(958); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode SELECT() { return getToken(PLSQLParser.SELECT, 0); }
		public Select_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSelect_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSelect_statement(this);
		}
	}

	public final Select_statementContext select_statement() throws RecognitionException {
		Select_statementContext _localctx = new Select_statementContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_select_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(960); match(SELECT);
			setState(961); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Set_transaction_statementContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode TRANSACTION() { return getToken(PLSQLParser.TRANSACTION, 0); }
		public TerminalNode SET() { return getToken(PLSQLParser.SET, 0); }
		public Set_transaction_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_transaction_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSet_transaction_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSet_transaction_statement(this);
		}
	}

	public final Set_transaction_statementContext set_transaction_statement() throws RecognitionException {
		Set_transaction_statementContext _localctx = new Set_transaction_statementContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_set_transaction_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(963); match(SET);
			setState(964); match(TRANSACTION);
			setState(965); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Update_statementContext extends ParserRuleContext {
		public TerminalNode UPDATE() { return getToken(PLSQLParser.UPDATE, 0); }
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public Update_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterUpdate_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitUpdate_statement(this);
		}
	}

	public final Update_statementContext update_statement() throws RecognitionException {
		Update_statementContext _localctx = new Update_statementContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_update_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(967); match(UPDATE);
			setState(968); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Swallow_to_semiContext extends ParserRuleContext {
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public Swallow_to_semiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_swallow_to_semi; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterSwallow_to_semi(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitSwallow_to_semi(this);
		}
	}

	public final Swallow_to_semiContext swallow_to_semi() throws RecognitionException {
		Swallow_to_semiContext _localctx = new Swallow_to_semiContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_swallow_to_semi);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(971); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(970);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==SEMI) ) {
					_errHandler.recoverInline(this);
					}
					consume();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(973); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
			} while ( _alt!=2 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class While_loop_statementContext extends ParserRuleContext {
		public List<TerminalNode> LOOP() { return getTokens(PLSQLParser.LOOP); }
		public Label_nameContext label_name() {
			return getRuleContext(Label_nameContext.class,0);
		}
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode LOOP(int i) {
			return getToken(PLSQLParser.LOOP, i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public TerminalNode WHILE() { return getToken(PLSQLParser.WHILE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public While_loop_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while_loop_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterWhile_loop_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitWhile_loop_statement(this);
		}
	}

	public final While_loop_statementContext while_loop_statement() throws RecognitionException {
		While_loop_statementContext _localctx = new While_loop_statementContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_while_loop_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(975); match(WHILE);
			setState(976); expression();
			setState(977); match(LOOP);
			setState(981); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(978); statement();
				setState(979); match(SEMI);
				}
				}
				setState(983); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CASE) | (1L << COMMIT) | (1L << DELETE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INSERT) | (1L << LOCK) | (1L << NULL) | (1L << OPEN) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << UPDATE) | (1L << WHILE) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONTINUE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (LOOP - 64)) | (1L << (RETURN - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (LLABEL - 64)))) != 0) );
			setState(985); match(END);
			setState(986); match(LOOP);
			setState(988);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(987); label_name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Match_parensContext extends ParserRuleContext {
		public TerminalNode AS(int i) {
			return getToken(PLSQLParser.AS, i);
		}
		public TerminalNode IS(int i) {
			return getToken(PLSQLParser.IS, i);
		}
		public TerminalNode SEMI(int i) {
			return getToken(PLSQLParser.SEMI, i);
		}
		public List<TerminalNode> RPAREN() { return getTokens(PLSQLParser.RPAREN); }
		public List<TerminalNode> AS() { return getTokens(PLSQLParser.AS); }
		public List<TerminalNode> IN() { return getTokens(PLSQLParser.IN); }
		public TerminalNode OUT(int i) {
			return getToken(PLSQLParser.OUT, i);
		}
		public Match_parensContext match_parens() {
			return getRuleContext(Match_parensContext.class,0);
		}
		public List<TerminalNode> IS() { return getTokens(PLSQLParser.IS); }
		public TerminalNode RPAREN(int i) {
			return getToken(PLSQLParser.RPAREN, i);
		}
		public TerminalNode IN(int i) {
			return getToken(PLSQLParser.IN, i);
		}
		public List<TerminalNode> LPAREN() { return getTokens(PLSQLParser.LPAREN); }
		public List<TerminalNode> SEMI() { return getTokens(PLSQLParser.SEMI); }
		public TerminalNode LPAREN(int i) {
			return getToken(PLSQLParser.LPAREN, i);
		}
		public List<TerminalNode> OUT() { return getTokens(PLSQLParser.OUT); }
		public Match_parensContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_match_parens; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterMatch_parens(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitMatch_parens(this);
		}
	}

	public final Match_parensContext match_parens() throws RecognitionException {
		Match_parensContext _localctx = new Match_parensContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_match_parens);
		int _la;
		try {
			setState(1000);
			switch (_input.LA(1)) {
			case AND:
			case ARRAY:
			case AUTHID:
			case BETWEEN:
			case BODY:
			case BULK:
			case BULK_ROWCOUNT:
			case BY:
			case CASE:
			case CREATE:
			case COLLECT:
			case COMMIT:
			case CURRENT_USER:
			case DEFAULT:
			case DEFINER:
			case DELETE:
			case ELSE:
			case ELSIF:
			case EXTERNAL:
			case FALSE:
			case FETCH:
			case FOR:
			case FORALL:
			case GOTO:
			case IF:
			case INDEX:
			case INSERT:
			case INTO:
			case LANGUAGE:
			case LIKE:
			case LIMIT:
			case LOCK:
			case NOT:
			case NOTFOUND:
			case NULL:
			case OPEN:
			case OR:
			case PACKAGE:
			case RAISE:
			case ROLLBACK:
			case SAVEPOINT:
			case SELECT:
			case SET:
			case SQL:
			case TABLE:
			case TRANSACTION:
			case TRUE:
			case THEN:
			case UPDATE:
			case WHILE:
			case INSERTING:
			case UPDATING:
			case DELETING:
			case ISOPEN:
			case EXISTS:
			case BEGIN:
			case CLOSE:
			case CONSTANT:
			case CONTINUE:
			case CURSOR:
			case DECLARE:
			case DETERMINISTIC:
			case END:
			case EXCEPTION:
			case EXECUTE:
			case EXIT:
			case FUNCTION:
			case IMMEDIATE:
			case LOOP:
			case NOCOPY:
			case OTHERS:
			case PARALLEL_ENABLE:
			case PIPELINED:
			case PRAGMA:
			case PROCEDURE:
			case RECORD:
			case REF:
			case RESULT_CACHE:
			case RETURN:
			case RETURNING:
			case ROWTYPE:
			case SUBTYPE:
			case USING:
			case VARRAY:
			case VARYING:
			case WHEN:
			case QUOTED_STRING:
			case ID:
			case COLON:
			case DOUBLEDOT:
			case DOT:
			case COMMA:
			case EXPONENT:
			case ASTERISK:
			case AT_SIGN:
			case LPAREN:
			case RBRACK:
			case LBRACK:
			case PLUS:
			case MINUS:
			case DIVIDE:
			case EQ:
			case PERCENT:
			case LLABEL:
			case RLABEL:
			case ASSIGN:
			case ARROW:
			case VERTBAR:
			case DOUBLEVERTBAR:
			case NOT_EQ:
			case LTH:
			case LEQ:
			case GTH:
			case GEQ:
			case INTEGER:
			case REAL_NUMBER:
			case WS:
			case SL_COMMENT:
			case ML_COMMENT:
				enterOuterAlt(_localctx, 1);
				{
				setState(993);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AND) | (1L << ARRAY) | (1L << AUTHID) | (1L << BETWEEN) | (1L << BODY) | (1L << BULK) | (1L << BULK_ROWCOUNT) | (1L << BY) | (1L << CASE) | (1L << CREATE) | (1L << COLLECT) | (1L << COMMIT) | (1L << CURRENT_USER) | (1L << DEFAULT) | (1L << DEFINER) | (1L << DELETE) | (1L << ELSE) | (1L << ELSIF) | (1L << EXTERNAL) | (1L << FALSE) | (1L << FETCH) | (1L << FOR) | (1L << FORALL) | (1L << GOTO) | (1L << IF) | (1L << INDEX) | (1L << INSERT) | (1L << INTO) | (1L << LANGUAGE) | (1L << LIKE) | (1L << LIMIT) | (1L << LOCK) | (1L << NOT) | (1L << NOTFOUND) | (1L << NULL) | (1L << OPEN) | (1L << OR) | (1L << PACKAGE) | (1L << RAISE) | (1L << ROLLBACK) | (1L << SAVEPOINT) | (1L << SELECT) | (1L << SET) | (1L << SQL) | (1L << TABLE) | (1L << TRANSACTION) | (1L << TRUE) | (1L << THEN) | (1L << UPDATE) | (1L << WHILE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING) | (1L << ISOPEN) | (1L << EXISTS) | (1L << BEGIN) | (1L << CLOSE) | (1L << CONSTANT) | (1L << CONTINUE) | (1L << CURSOR))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (DECLARE - 64)) | (1L << (DETERMINISTIC - 64)) | (1L << (END - 64)) | (1L << (EXCEPTION - 64)) | (1L << (EXECUTE - 64)) | (1L << (EXIT - 64)) | (1L << (FUNCTION - 64)) | (1L << (IMMEDIATE - 64)) | (1L << (LOOP - 64)) | (1L << (NOCOPY - 64)) | (1L << (OTHERS - 64)) | (1L << (PARALLEL_ENABLE - 64)) | (1L << (PIPELINED - 64)) | (1L << (PRAGMA - 64)) | (1L << (PROCEDURE - 64)) | (1L << (RECORD - 64)) | (1L << (REF - 64)) | (1L << (RESULT_CACHE - 64)) | (1L << (RETURN - 64)) | (1L << (RETURNING - 64)) | (1L << (ROWTYPE - 64)) | (1L << (SUBTYPE - 64)) | (1L << (USING - 64)) | (1L << (VARRAY - 64)) | (1L << (VARYING - 64)) | (1L << (WHEN - 64)) | (1L << (QUOTED_STRING - 64)) | (1L << (ID - 64)) | (1L << (COLON - 64)) | (1L << (DOUBLEDOT - 64)) | (1L << (DOT - 64)) | (1L << (COMMA - 64)) | (1L << (EXPONENT - 64)) | (1L << (ASTERISK - 64)) | (1L << (AT_SIGN - 64)) | (1L << (RBRACK - 64)) | (1L << (LBRACK - 64)) | (1L << (PLUS - 64)) | (1L << (MINUS - 64)) | (1L << (DIVIDE - 64)) | (1L << (EQ - 64)) | (1L << (PERCENT - 64)) | (1L << (LLABEL - 64)) | (1L << (RLABEL - 64)) | (1L << (ASSIGN - 64)) | (1L << (ARROW - 64)) | (1L << (VERTBAR - 64)) | (1L << (DOUBLEVERTBAR - 64)) | (1L << (NOT_EQ - 64)) | (1L << (LTH - 64)) | (1L << (LEQ - 64)) | (1L << (GTH - 64)) | (1L << (GEQ - 64)) | (1L << (INTEGER - 64)) | (1L << (REAL_NUMBER - 64)) | (1L << (WS - 64)) | (1L << (SL_COMMENT - 64)) | (1L << (ML_COMMENT - 64)))) != 0)) {
					{
					{
					setState(990);
					_la = _input.LA(1);
					if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AS) | (1L << IN) | (1L << IS))) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & ((1L << (OUT - 75)) | (1L << (SEMI - 75)) | (1L << (RPAREN - 75)) | (1L << (LPAREN - 75)))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					consume();
					}
					}
					setState(995);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(996); match(RPAREN);
				setState(997); match_parens();
				setState(998); match(LPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Label_nameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public Label_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterLabel_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitLabel_name(this);
		}
	}

	public final Label_nameContext label_name() throws RecognitionException {
		Label_nameContext _localctx = new Label_nameContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_label_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1002); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public Or_exprContext or_expr() {
			return getRuleContext(Or_exprContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1004); or_expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Or_exprContext extends ParserRuleContext {
		public List<And_exprContext> and_expr() {
			return getRuleContexts(And_exprContext.class);
		}
		public List<TerminalNode> OR() { return getTokens(PLSQLParser.OR); }
		public And_exprContext and_expr(int i) {
			return getRuleContext(And_exprContext.class,i);
		}
		public TerminalNode OR(int i) {
			return getToken(PLSQLParser.OR, i);
		}
		public Or_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterOr_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitOr_expr(this);
		}
	}

	public final Or_exprContext or_expr() throws RecognitionException {
		Or_exprContext _localctx = new Or_exprContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_or_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1006); and_expr();
			setState(1011);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(1007); match(OR);
				setState(1008); and_expr();
				}
				}
				setState(1013);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class And_exprContext extends ParserRuleContext {
		public Not_exprContext not_expr(int i) {
			return getRuleContext(Not_exprContext.class,i);
		}
		public TerminalNode AND(int i) {
			return getToken(PLSQLParser.AND, i);
		}
		public List<TerminalNode> AND() { return getTokens(PLSQLParser.AND); }
		public List<Not_exprContext> not_expr() {
			return getRuleContexts(Not_exprContext.class);
		}
		public And_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAnd_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAnd_expr(this);
		}
	}

	public final And_exprContext and_expr() throws RecognitionException {
		And_exprContext _localctx = new And_exprContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_and_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1014); not_expr();
			setState(1019);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(1015); match(AND);
				setState(1016); not_expr();
				}
				}
				setState(1021);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Not_exprContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public Compare_exprContext compare_expr() {
			return getRuleContext(Compare_exprContext.class,0);
		}
		public Not_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNot_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNot_expr(this);
		}
	}

	public final Not_exprContext not_expr() throws RecognitionException {
		Not_exprContext _localctx = new Not_exprContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_not_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1023);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(1022); match(NOT);
				}
			}

			setState(1025); compare_expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compare_exprContext extends ParserRuleContext {
		public TerminalNode GEQ() { return getToken(PLSQLParser.GEQ, 0); }
		public TerminalNode GTH() { return getToken(PLSQLParser.GTH, 0); }
		public List<Is_null_exprContext> is_null_expr() {
			return getRuleContexts(Is_null_exprContext.class);
		}
		public TerminalNode LEQ() { return getToken(PLSQLParser.LEQ, 0); }
		public TerminalNode NOT_EQ() { return getToken(PLSQLParser.NOT_EQ, 0); }
		public TerminalNode LTH() { return getToken(PLSQLParser.LTH, 0); }
		public Is_null_exprContext is_null_expr(int i) {
			return getRuleContext(Is_null_exprContext.class,i);
		}
		public TerminalNode EQ() { return getToken(PLSQLParser.EQ, 0); }
		public Compare_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compare_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCompare_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCompare_expr(this);
		}
	}

	public final Compare_exprContext compare_expr() throws RecognitionException {
		Compare_exprContext _localctx = new Compare_exprContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_compare_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1027); is_null_expr();
			setState(1030);
			_la = _input.LA(1);
			if (((((_la - 108)) & ~0x3f) == 0 && ((1L << (_la - 108)) & ((1L << (EQ - 108)) | (1L << (NOT_EQ - 108)) | (1L << (LTH - 108)) | (1L << (LEQ - 108)) | (1L << (GTH - 108)) | (1L << (GEQ - 108)))) != 0)) {
				{
				setState(1028);
				_la = _input.LA(1);
				if ( !(((((_la - 108)) & ~0x3f) == 0 && ((1L << (_la - 108)) & ((1L << (EQ - 108)) | (1L << (NOT_EQ - 108)) | (1L << (LTH - 108)) | (1L << (LEQ - 108)) | (1L << (GTH - 108)) | (1L << (GEQ - 108)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(1029); is_null_expr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Is_null_exprContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public Like_exprContext like_expr() {
			return getRuleContext(Like_exprContext.class,0);
		}
		public Is_null_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_is_null_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterIs_null_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitIs_null_expr(this);
		}
	}

	public final Is_null_exprContext is_null_expr() throws RecognitionException {
		Is_null_exprContext _localctx = new Is_null_exprContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_is_null_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1032); like_expr();
			setState(1038);
			_la = _input.LA(1);
			if (_la==IS) {
				{
				setState(1033); match(IS);
				setState(1035);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1034); match(NOT);
					}
				}

				setState(1037); match(NULL);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Like_exprContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public List<Between_exprContext> between_expr() {
			return getRuleContexts(Between_exprContext.class);
		}
		public TerminalNode LIKE() { return getToken(PLSQLParser.LIKE, 0); }
		public Between_exprContext between_expr(int i) {
			return getRuleContext(Between_exprContext.class,i);
		}
		public Like_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_like_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterLike_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitLike_expr(this);
		}
	}

	public final Like_exprContext like_expr() throws RecognitionException {
		Like_exprContext _localctx = new Like_exprContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_like_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040); between_expr();
			setState(1046);
			_la = _input.LA(1);
			if (_la==LIKE || _la==NOT) {
				{
				setState(1042);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1041); match(NOT);
					}
				}

				setState(1044); match(LIKE);
				setState(1045); between_expr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Between_exprContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public TerminalNode BETWEEN() { return getToken(PLSQLParser.BETWEEN, 0); }
		public List<In_exprContext> in_expr() {
			return getRuleContexts(In_exprContext.class);
		}
		public TerminalNode AND() { return getToken(PLSQLParser.AND, 0); }
		public In_exprContext in_expr(int i) {
			return getRuleContext(In_exprContext.class,i);
		}
		public Between_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_between_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBetween_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBetween_expr(this);
		}
	}

	public final Between_exprContext between_expr() throws RecognitionException {
		Between_exprContext _localctx = new Between_exprContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_between_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1048); in_expr();
			setState(1057);
			switch ( getInterpreter().adaptivePredict(_input,127,_ctx) ) {
			case 1:
				{
				setState(1050);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1049); match(NOT);
					}
				}

				setState(1052); match(BETWEEN);
				setState(1053); in_expr();
				setState(1054); match(AND);
				setState(1055); in_expr();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class In_exprContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PLSQLParser.NOT, 0); }
		public List<Add_exprContext> add_expr() {
			return getRuleContexts(Add_exprContext.class);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public Add_exprContext add_expr(int i) {
			return getRuleContext(Add_exprContext.class,i);
		}
		public TerminalNode IN() { return getToken(PLSQLParser.IN, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public In_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterIn_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitIn_expr(this);
		}
	}

	public final In_exprContext in_expr() throws RecognitionException {
		In_exprContext _localctx = new In_exprContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_in_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1059); add_expr();
			setState(1075);
			switch ( getInterpreter().adaptivePredict(_input,130,_ctx) ) {
			case 1:
				{
				setState(1061);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(1060); match(NOT);
					}
				}

				setState(1063); match(IN);
				setState(1064); match(LPAREN);
				setState(1065); add_expr();
				setState(1070);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1066); match(COMMA);
					setState(1067); add_expr();
					}
					}
					setState(1072);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1073); match(RPAREN);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_expressionContext extends ParserRuleContext {
		public Add_exprContext add_expr() {
			return getRuleContext(Add_exprContext.class,0);
		}
		public Numeric_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNumeric_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNumeric_expression(this);
		}
	}

	public final Numeric_expressionContext numeric_expression() throws RecognitionException {
		Numeric_expressionContext _localctx = new Numeric_expressionContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_numeric_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1077); add_expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Add_exprContext extends ParserRuleContext {
		public TerminalNode MINUS(int i) {
			return getToken(PLSQLParser.MINUS, i);
		}
		public List<TerminalNode> DOUBLEVERTBAR() { return getTokens(PLSQLParser.DOUBLEVERTBAR); }
		public TerminalNode DOUBLEVERTBAR(int i) {
			return getToken(PLSQLParser.DOUBLEVERTBAR, i);
		}
		public List<Mul_exprContext> mul_expr() {
			return getRuleContexts(Mul_exprContext.class);
		}
		public Mul_exprContext mul_expr(int i) {
			return getRuleContext(Mul_exprContext.class,i);
		}
		public List<TerminalNode> MINUS() { return getTokens(PLSQLParser.MINUS); }
		public List<TerminalNode> PLUS() { return getTokens(PLSQLParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(PLSQLParser.PLUS, i);
		}
		public Add_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_add_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAdd_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAdd_expr(this);
		}
	}

	public final Add_exprContext add_expr() throws RecognitionException {
		Add_exprContext _localctx = new Add_exprContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_add_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1079); mul_expr();
			setState(1084);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 105)) & ~0x3f) == 0 && ((1L << (_la - 105)) & ((1L << (PLUS - 105)) | (1L << (MINUS - 105)) | (1L << (DOUBLEVERTBAR - 105)))) != 0)) {
				{
				{
				setState(1080);
				_la = _input.LA(1);
				if ( !(((((_la - 105)) & ~0x3f) == 0 && ((1L << (_la - 105)) & ((1L << (PLUS - 105)) | (1L << (MINUS - 105)) | (1L << (DOUBLEVERTBAR - 105)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(1081); mul_expr();
				}
				}
				setState(1086);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Mul_exprContext extends ParserRuleContext {
		public List<Unary_sign_exprContext> unary_sign_expr() {
			return getRuleContexts(Unary_sign_exprContext.class);
		}
		public TerminalNode DIVIDE(int i) {
			return getToken(PLSQLParser.DIVIDE, i);
		}
		public List<KMODContext> kMOD() {
			return getRuleContexts(KMODContext.class);
		}
		public List<TerminalNode> DIVIDE() { return getTokens(PLSQLParser.DIVIDE); }
		public KMODContext kMOD(int i) {
			return getRuleContext(KMODContext.class,i);
		}
		public Unary_sign_exprContext unary_sign_expr(int i) {
			return getRuleContext(Unary_sign_exprContext.class,i);
		}
		public List<TerminalNode> ASTERISK() { return getTokens(PLSQLParser.ASTERISK); }
		public TerminalNode ASTERISK(int i) {
			return getToken(PLSQLParser.ASTERISK, i);
		}
		public Mul_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mul_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterMul_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitMul_expr(this);
		}
	}

	public final Mul_exprContext mul_expr() throws RecognitionException {
		Mul_exprContext _localctx = new Mul_exprContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_mul_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1087); unary_sign_expr();
			setState(1096);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(1091);
					switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
					case 1:
						{
						setState(1088); match(ASTERISK);
						}
						break;

					case 2:
						{
						setState(1089); match(DIVIDE);
						}
						break;

					case 3:
						{
						setState(1090); kMOD();
						}
						break;
					}
					setState(1093); unary_sign_expr();
					}
					} 
				}
				setState(1098);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,133,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_sign_exprContext extends ParserRuleContext {
		public Exponent_exprContext exponent_expr() {
			return getRuleContext(Exponent_exprContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(PLSQLParser.MINUS, 0); }
		public TerminalNode PLUS() { return getToken(PLSQLParser.PLUS, 0); }
		public Unary_sign_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_sign_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterUnary_sign_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitUnary_sign_expr(this);
		}
	}

	public final Unary_sign_exprContext unary_sign_expr() throws RecognitionException {
		Unary_sign_exprContext _localctx = new Unary_sign_exprContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_unary_sign_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1100);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1099);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
			}

			setState(1102); exponent_expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Exponent_exprContext extends ParserRuleContext {
		public TerminalNode EXPONENT() { return getToken(PLSQLParser.EXPONENT, 0); }
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public Exponent_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exponent_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterExponent_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitExponent_expr(this);
		}
	}

	public final Exponent_exprContext exponent_expr() throws RecognitionException {
		Exponent_exprContext _localctx = new Exponent_exprContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_exponent_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1104); atom();
			setState(1107);
			switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
			case 1:
				{
				setState(1105); match(EXPONENT);
				setState(1106); atom();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public Boolean_atomContext boolean_atom() {
			return getRuleContext(Boolean_atomContext.class,0);
		}
		public TerminalNode NULL() { return getToken(PLSQLParser.NULL, 0); }
		public Variable_or_function_callContext variable_or_function_call() {
			return getRuleContext(Variable_or_function_callContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode SQL() { return getToken(PLSQLParser.SQL, 0); }
		public Numeric_atomContext numeric_atom() {
			return getRuleContext(Numeric_atomContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode PERCENT() { return getToken(PLSQLParser.PERCENT, 0); }
		public String_literalContext string_literal() {
			return getRuleContext(String_literalContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_atom);
		try {
			setState(1125);
			switch ( getInterpreter().adaptivePredict(_input,137,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1109); variable_or_function_call();
				setState(1112);
				switch ( getInterpreter().adaptivePredict(_input,136,_ctx) ) {
				case 1:
					{
					setState(1110); match(PERCENT);
					setState(1111); attribute();
					}
					break;
				}
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1114); match(SQL);
				setState(1115); match(PERCENT);
				setState(1116); attribute();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1117); string_literal();
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1118); numeric_atom();
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1119); boolean_atom();
				}
				break;

			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1120); match(NULL);
				}
				break;

			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1121); match(LPAREN);
				setState(1122); expression();
				setState(1123); match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Variable_or_function_callContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(PLSQLParser.DOT); }
		public Delete_callContext delete_call() {
			return getRuleContext(Delete_callContext.class,0);
		}
		public List<CallContext> call() {
			return getRuleContexts(CallContext.class);
		}
		public CallContext call(int i) {
			return getRuleContext(CallContext.class,i);
		}
		public TerminalNode DOT(int i) {
			return getToken(PLSQLParser.DOT, i);
		}
		public Variable_or_function_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable_or_function_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterVariable_or_function_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitVariable_or_function_call(this);
		}
	}

	public final Variable_or_function_callContext variable_or_function_call() throws RecognitionException {
		Variable_or_function_callContext _localctx = new Variable_or_function_callContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_variable_or_function_call);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1127); call();
			setState(1132);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(1128); match(DOT);
					setState(1129); call();
					}
					} 
				}
				setState(1134);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,138,_ctx);
			}
			setState(1137);
			switch ( getInterpreter().adaptivePredict(_input,139,_ctx) ) {
			case 1:
				{
				setState(1135); match(DOT);
				setState(1136); delete_call();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeContext extends ParserRuleContext {
		public TerminalNode BULK_ROWCOUNT() { return getToken(PLSQLParser.BULK_ROWCOUNT, 0); }
		public KFOUNDContext kFOUND() {
			return getRuleContext(KFOUNDContext.class,0);
		}
		public TerminalNode NOTFOUND() { return getToken(PLSQLParser.NOTFOUND, 0); }
		public KROWCOUNTContext kROWCOUNT() {
			return getRuleContext(KROWCOUNTContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode ISOPEN() { return getToken(PLSQLParser.ISOPEN, 0); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_attribute);
		try {
			setState(1148);
			switch ( getInterpreter().adaptivePredict(_input,140,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1139); match(BULK_ROWCOUNT);
				setState(1140); match(LPAREN);
				setState(1141); expression();
				setState(1142); match(RPAREN);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1144); kFOUND();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1145); match(ISOPEN);
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1146); match(NOTFOUND);
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1147); kROWCOUNT();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Call_argsContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public Call_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_call_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCall_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCall_args(this);
		}
	}

	public final Call_argsContext call_args() throws RecognitionException {
		Call_argsContext _localctx = new Call_argsContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_call_args);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1150); match(LPAREN);
			setState(1159);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FALSE) | (1L << NOT) | (1L << NULL) | (1L << SQL) | (1L << TRUE) | (1L << INSERTING) | (1L << UPDATING) | (1L << DELETING))) != 0) || ((((_la - 91)) & ~0x3f) == 0 && ((1L << (_la - 91)) & ((1L << (QUOTED_STRING - 91)) | (1L << (ID - 91)) | (1L << (COLON - 91)) | (1L << (LPAREN - 91)) | (1L << (PLUS - 91)) | (1L << (MINUS - 91)) | (1L << (INTEGER - 91)) | (1L << (REAL_NUMBER - 91)))) != 0)) {
				{
				setState(1151); parameter();
				setState(1156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1152); match(COMMA);
					setState(1153); parameter();
					}
					}
					setState(1158);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(1161); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_atomContext extends ParserRuleContext {
		public Conditional_predicateContext conditional_predicate() {
			return getRuleContext(Conditional_predicateContext.class,0);
		}
		public Collection_existsContext collection_exists() {
			return getRuleContext(Collection_existsContext.class,0);
		}
		public Boolean_literalContext boolean_literal() {
			return getRuleContext(Boolean_literalContext.class,0);
		}
		public Boolean_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBoolean_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBoolean_atom(this);
		}
	}

	public final Boolean_atomContext boolean_atom() throws RecognitionException {
		Boolean_atomContext _localctx = new Boolean_atomContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_boolean_atom);
		try {
			setState(1166);
			switch (_input.LA(1)) {
			case FALSE:
			case TRUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1163); boolean_literal();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(1164); collection_exists();
				}
				break;
			case INSERTING:
			case UPDATING:
			case DELETING:
				enterOuterAlt(_localctx, 3);
				{
				setState(1165); conditional_predicate();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_atomContext extends ParserRuleContext {
		public Numeric_literalContext numeric_literal() {
			return getRuleContext(Numeric_literalContext.class,0);
		}
		public Numeric_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNumeric_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNumeric_atom(this);
		}
	}

	public final Numeric_atomContext numeric_atom() throws RecognitionException {
		Numeric_atomContext _localctx = new Numeric_atomContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_numeric_atom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1168); numeric_literal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Numeric_literalContext extends ParserRuleContext {
		public TerminalNode REAL_NUMBER() { return getToken(PLSQLParser.REAL_NUMBER, 0); }
		public TerminalNode INTEGER() { return getToken(PLSQLParser.INTEGER, 0); }
		public Numeric_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numeric_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterNumeric_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitNumeric_literal(this);
		}
	}

	public final Numeric_literalContext numeric_literal() throws RecognitionException {
		Numeric_literalContext _localctx = new Numeric_literalContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_numeric_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1170);
			_la = _input.LA(1);
			if ( !(_la==INTEGER || _la==REAL_NUMBER) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_literalContext extends ParserRuleContext {
		public TerminalNode FALSE() { return getToken(PLSQLParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(PLSQLParser.TRUE, 0); }
		public Boolean_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterBoolean_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitBoolean_literal(this);
		}
	}

	public final Boolean_literalContext boolean_literal() throws RecognitionException {
		Boolean_literalContext _localctx = new Boolean_literalContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_boolean_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1172);
			_la = _input.LA(1);
			if ( !(_la==FALSE || _la==TRUE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class String_literalContext extends ParserRuleContext {
		public TerminalNode QUOTED_STRING() { return getToken(PLSQLParser.QUOTED_STRING, 0); }
		public String_literalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterString_literal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitString_literal(this);
		}
	}

	public final String_literalContext string_literal() throws RecognitionException {
		String_literalContext _localctx = new String_literalContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_string_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1174); match(QUOTED_STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Collection_existsContext extends ParserRuleContext {
		public TerminalNode EXISTS() { return getToken(PLSQLParser.EXISTS, 0); }
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Collection_existsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collection_exists; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCollection_exists(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCollection_exists(this);
		}
	}

	public final Collection_existsContext collection_exists() throws RecognitionException {
		Collection_existsContext _localctx = new Collection_existsContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_collection_exists);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1176); match(ID);
			setState(1177); match(DOT);
			setState(1178); match(EXISTS);
			setState(1179); match(LPAREN);
			setState(1180); expression();
			setState(1181); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditional_predicateContext extends ParserRuleContext {
		public TerminalNode UPDATING() { return getToken(PLSQLParser.UPDATING, 0); }
		public TerminalNode INSERTING() { return getToken(PLSQLParser.INSERTING, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public TerminalNode QUOTED_STRING() { return getToken(PLSQLParser.QUOTED_STRING, 0); }
		public TerminalNode DELETING() { return getToken(PLSQLParser.DELETING, 0); }
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public Conditional_predicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterConditional_predicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitConditional_predicate(this);
		}
	}

	public final Conditional_predicateContext conditional_predicate() throws RecognitionException {
		Conditional_predicateContext _localctx = new Conditional_predicateContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_conditional_predicate);
		try {
			setState(1191);
			switch (_input.LA(1)) {
			case INSERTING:
				enterOuterAlt(_localctx, 1);
				{
				setState(1183); match(INSERTING);
				}
				break;
			case UPDATING:
				enterOuterAlt(_localctx, 2);
				{
				setState(1184); match(UPDATING);
				setState(1188);
				switch ( getInterpreter().adaptivePredict(_input,144,_ctx) ) {
				case 1:
					{
					setState(1185); match(LPAREN);
					setState(1186); match(QUOTED_STRING);
					setState(1187); match(RPAREN);
					}
					break;
				}
				}
				break;
			case DELETING:
				enterOuterAlt(_localctx, 3);
				{
				setState(1190); match(DELETING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(PLSQLParser.ARROW, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1195);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				{
				setState(1193); match(ID);
				setState(1194); match(ARROW);
				}
				break;
			}
			setState(1197); expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IndexContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitIndex(this);
		}
	}

	public final IndexContext index() throws RecognitionException {
		IndexContext _localctx = new IndexContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1199); expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_packageContext extends ParserRuleContext {
		public Token schema_name;
		public Token package_name;
		public TerminalNode PACKAGE() { return getToken(PLSQLParser.PACKAGE, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode OR() { return getToken(PLSQLParser.OR, 0); }
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode SEMI() { return getToken(PLSQLParser.SEMI, 0); }
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public KREPLACEContext kREPLACE() {
			return getRuleContext(KREPLACEContext.class,0);
		}
		public TerminalNode CREATE() { return getToken(PLSQLParser.CREATE, 0); }
		public Invoker_rights_clauseContext invoker_rights_clause() {
			return getRuleContext(Invoker_rights_clauseContext.class,0);
		}
		public Create_packageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_package; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCreate_package(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCreate_package(this);
		}
	}

	public final Create_packageContext create_package() throws RecognitionException {
		Create_packageContext _localctx = new Create_packageContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_create_package);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1201); match(CREATE);
			setState(1204);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(1202); match(OR);
				setState(1203); kREPLACE();
				}
			}

			setState(1206); match(PACKAGE);
			setState(1209);
			switch ( getInterpreter().adaptivePredict(_input,148,_ctx) ) {
			case 1:
				{
				setState(1207); ((Create_packageContext)_localctx).schema_name = match(ID);
				setState(1208); match(DOT);
				}
				break;
			}
			setState(1211); ((Create_packageContext)_localctx).package_name = match(ID);
			setState(1213);
			_la = _input.LA(1);
			if (_la==AUTHID) {
				{
				setState(1212); invoker_rights_clause();
				}
			}

			setState(1215);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(1217);
			switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
			case 1:
				{
				setState(1216); declare_section();
				}
				break;
			}
			setState(1219); match(END);
			setState(1221);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(1220); match(ID);
				}
			}

			setState(1223); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_package_bodyContext extends ParserRuleContext {
		public Token schema_name;
		public Token package_name;
		public BodyContext initialize_section;
		public Token package_name2;
		public TerminalNode PACKAGE() { return getToken(PLSQLParser.PACKAGE, 0); }
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode OR() { return getToken(PLSQLParser.OR, 0); }
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public TerminalNode SEMI() { return getToken(PLSQLParser.SEMI, 0); }
		public TerminalNode END() { return getToken(PLSQLParser.END, 0); }
		public KREPLACEContext kREPLACE() {
			return getRuleContext(KREPLACEContext.class,0);
		}
		public TerminalNode CREATE() { return getToken(PLSQLParser.CREATE, 0); }
		public TerminalNode BODY() { return getToken(PLSQLParser.BODY, 0); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Create_package_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_package_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCreate_package_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCreate_package_body(this);
		}
	}

	public final Create_package_bodyContext create_package_body() throws RecognitionException {
		Create_package_bodyContext _localctx = new Create_package_bodyContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_create_package_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1225); match(CREATE);
			setState(1228);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(1226); match(OR);
				setState(1227); kREPLACE();
				}
			}

			setState(1230); match(PACKAGE);
			setState(1231); match(BODY);
			setState(1234);
			switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
			case 1:
				{
				setState(1232); ((Create_package_bodyContext)_localctx).schema_name = match(ID);
				setState(1233); match(DOT);
				}
				break;
			}
			setState(1236); ((Create_package_bodyContext)_localctx).package_name = match(ID);
			setState(1237);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(1239);
			switch ( getInterpreter().adaptivePredict(_input,154,_ctx) ) {
			case 1:
				{
				setState(1238); declare_section();
				}
				break;
			}
			setState(1246);
			switch (_input.LA(1)) {
			case BEGIN:
				{
				setState(1241); ((Create_package_bodyContext)_localctx).initialize_section = body();
				}
				break;
			case END:
				{
				setState(1242); match(END);
				setState(1244);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(1243); ((Create_package_bodyContext)_localctx).package_name2 = match(ID);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(1248); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_procedureContext extends ParserRuleContext {
		public Token schema_name;
		public Token procedure_name;
		public TerminalNode EXTERNAL() { return getToken(PLSQLParser.EXTERNAL, 0); }
		public Call_specContext call_spec() {
			return getRuleContext(Call_specContext.class,0);
		}
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode OR() { return getToken(PLSQLParser.OR, 0); }
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public List<Parameter_declarationContext> parameter_declaration() {
			return getRuleContexts(Parameter_declarationContext.class);
		}
		public TerminalNode SEMI() { return getToken(PLSQLParser.SEMI, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public KREPLACEContext kREPLACE() {
			return getRuleContext(KREPLACEContext.class,0);
		}
		public TerminalNode PROCEDURE() { return getToken(PLSQLParser.PROCEDURE, 0); }
		public TerminalNode CREATE() { return getToken(PLSQLParser.CREATE, 0); }
		public Parameter_declarationContext parameter_declaration(int i) {
			return getRuleContext(Parameter_declarationContext.class,i);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Invoker_rights_clauseContext invoker_rights_clause() {
			return getRuleContext(Invoker_rights_clauseContext.class,0);
		}
		public Create_procedureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_procedure; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCreate_procedure(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCreate_procedure(this);
		}
	}

	public final Create_procedureContext create_procedure() throws RecognitionException {
		Create_procedureContext _localctx = new Create_procedureContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_create_procedure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1250); match(CREATE);
			setState(1253);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(1251); match(OR);
				setState(1252); kREPLACE();
				}
			}

			setState(1255); match(PROCEDURE);
			setState(1258);
			switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
			case 1:
				{
				setState(1256); ((Create_procedureContext)_localctx).schema_name = match(ID);
				setState(1257); match(DOT);
				}
				break;
			}
			setState(1260); ((Create_procedureContext)_localctx).procedure_name = match(ID);
			setState(1272);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1261); match(LPAREN);
				setState(1262); parameter_declaration();
				setState(1267);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1263); match(COMMA);
					setState(1264); parameter_declaration();
					}
					}
					setState(1269);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1270); match(RPAREN);
				}
			}

			setState(1275);
			_la = _input.LA(1);
			if (_la==AUTHID) {
				{
				setState(1274); invoker_rights_clause();
				}
			}

			setState(1277);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(1284);
			switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
			case 1:
				{
				setState(1279);
				switch ( getInterpreter().adaptivePredict(_input,162,_ctx) ) {
				case 1:
					{
					setState(1278); declare_section();
					}
					break;
				}
				setState(1281); body();
				}
				break;

			case 2:
				{
				setState(1282); call_spec();
				}
				break;

			case 3:
				{
				setState(1283); match(EXTERNAL);
				}
				break;
			}
			setState(1286); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_functionContext extends ParserRuleContext {
		public Token schema_name;
		public Token function_name;
		public TerminalNode EXTERNAL() { return getToken(PLSQLParser.EXTERNAL, 0); }
		public Call_specContext call_spec() {
			return getRuleContext(Call_specContext.class,0);
		}
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public TerminalNode ID(int i) {
			return getToken(PLSQLParser.ID, i);
		}
		public TerminalNode RPAREN() { return getToken(PLSQLParser.RPAREN, 0); }
		public TerminalNode OR() { return getToken(PLSQLParser.OR, 0); }
		public Declare_sectionContext declare_section() {
			return getRuleContext(Declare_sectionContext.class,0);
		}
		public TerminalNode AS() { return getToken(PLSQLParser.AS, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(PLSQLParser.COMMA, i);
		}
		public List<TerminalNode> ID() { return getTokens(PLSQLParser.ID); }
		public TerminalNode DOT() { return getToken(PLSQLParser.DOT, 0); }
		public TerminalNode FUNCTION() { return getToken(PLSQLParser.FUNCTION, 0); }
		public TerminalNode RETURN() { return getToken(PLSQLParser.RETURN, 0); }
		public TerminalNode IS() { return getToken(PLSQLParser.IS, 0); }
		public List<Parameter_declarationContext> parameter_declaration() {
			return getRuleContexts(Parameter_declarationContext.class);
		}
		public TerminalNode SEMI() { return getToken(PLSQLParser.SEMI, 0); }
		public TerminalNode LPAREN() { return getToken(PLSQLParser.LPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PLSQLParser.COMMA); }
		public KREPLACEContext kREPLACE() {
			return getRuleContext(KREPLACEContext.class,0);
		}
		public TerminalNode CREATE() { return getToken(PLSQLParser.CREATE, 0); }
		public Parameter_declarationContext parameter_declaration(int i) {
			return getRuleContext(Parameter_declarationContext.class,i);
		}
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public Invoker_rights_clauseContext invoker_rights_clause() {
			return getRuleContext(Invoker_rights_clauseContext.class,0);
		}
		public Create_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCreate_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCreate_function(this);
		}
	}

	public final Create_functionContext create_function() throws RecognitionException {
		Create_functionContext _localctx = new Create_functionContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_create_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1288); match(CREATE);
			setState(1291);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(1289); match(OR);
				setState(1290); kREPLACE();
				}
			}

			setState(1293); match(FUNCTION);
			setState(1296);
			switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
			case 1:
				{
				setState(1294); ((Create_functionContext)_localctx).schema_name = match(ID);
				setState(1295); match(DOT);
				}
				break;
			}
			setState(1298); ((Create_functionContext)_localctx).function_name = match(ID);
			setState(1310);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1299); match(LPAREN);
				setState(1300); parameter_declaration();
				setState(1305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1301); match(COMMA);
					setState(1302); parameter_declaration();
					}
					}
					setState(1307);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1308); match(RPAREN);
				}
			}

			setState(1312); match(RETURN);
			setState(1313); datatype();
			setState(1315);
			_la = _input.LA(1);
			if (_la==AUTHID) {
				{
				setState(1314); invoker_rights_clause();
				}
			}

			setState(1317);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==IS) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(1324);
			switch ( getInterpreter().adaptivePredict(_input,170,_ctx) ) {
			case 1:
				{
				setState(1319);
				switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
				case 1:
					{
					setState(1318); declare_section();
					}
					break;
				}
				setState(1321); body();
				}
				break;

			case 2:
				{
				setState(1322); call_spec();
				}
				break;

			case 3:
				{
				setState(1323); match(EXTERNAL);
				}
				break;
			}
			setState(1326); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Invoker_rights_clauseContext extends ParserRuleContext {
		public TerminalNode AUTHID() { return getToken(PLSQLParser.AUTHID, 0); }
		public TerminalNode CURRENT_USER() { return getToken(PLSQLParser.CURRENT_USER, 0); }
		public TerminalNode DEFINER() { return getToken(PLSQLParser.DEFINER, 0); }
		public Invoker_rights_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_invoker_rights_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterInvoker_rights_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitInvoker_rights_clause(this);
		}
	}

	public final Invoker_rights_clauseContext invoker_rights_clause() throws RecognitionException {
		Invoker_rights_clauseContext _localctx = new Invoker_rights_clauseContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_invoker_rights_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1328); match(AUTHID);
			setState(1329);
			_la = _input.LA(1);
			if ( !(_la==CURRENT_USER || _la==DEFINER) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Call_specContext extends ParserRuleContext {
		public Swallow_to_semiContext swallow_to_semi() {
			return getRuleContext(Swallow_to_semiContext.class,0);
		}
		public TerminalNode LANGUAGE() { return getToken(PLSQLParser.LANGUAGE, 0); }
		public Call_specContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_call_spec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterCall_spec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitCall_spec(this);
		}
	}

	public final Call_specContext call_spec() throws RecognitionException {
		Call_specContext _localctx = new Call_specContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_call_spec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1331); match(LANGUAGE);
			setState(1332); swallow_to_semi();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KERRORSContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KERRORSContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kERRORS; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKERRORS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKERRORS(this);
		}
	}

	public final KERRORSContext kERRORS() throws RecognitionException {
		KERRORSContext _localctx = new KERRORSContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_kERRORS);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1334);
			if (!(_input.LT(1).getText().length() >= 3 && "errors".startsWith(_input.LT(1).getText().toLowerCase()))) throw new FailedPredicateException(this, "_input.LT(1).getText().length() >= 3 && \"errors\".startsWith(_input.LT(1).getText().toLowerCase())");
			setState(1335); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KEXCEPTIONSContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KEXCEPTIONSContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kEXCEPTIONS; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKEXCEPTIONS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKEXCEPTIONS(this);
		}
	}

	public final KEXCEPTIONSContext kEXCEPTIONS() throws RecognitionException {
		KEXCEPTIONSContext _localctx = new KEXCEPTIONSContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_kEXCEPTIONS);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1337);
			if (!(_input.LT(1).getText().equalsIgnoreCase("exceptions"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"exceptions\")");
			setState(1338); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KFOUNDContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KFOUNDContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kFOUND; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKFOUND(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKFOUND(this);
		}
	}

	public final KFOUNDContext kFOUND() throws RecognitionException {
		KFOUNDContext _localctx = new KFOUNDContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_kFOUND);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1340);
			if (!(_input.LT(1).getText().equalsIgnoreCase("found"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"found\")");
			setState(1341); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KINDICESContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KINDICESContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kINDICES; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKINDICES(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKINDICES(this);
		}
	}

	public final KINDICESContext kINDICES() throws RecognitionException {
		KINDICESContext _localctx = new KINDICESContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_kINDICES);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			if (!(_input.LT(1).getText().equalsIgnoreCase("indices"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"indices\")");
			setState(1344); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KMODContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KMODContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kMOD; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKMOD(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKMOD(this);
		}
	}

	public final KMODContext kMOD() throws RecognitionException {
		KMODContext _localctx = new KMODContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_kMOD);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1346);
			if (!(_input.LT(1).getText().equalsIgnoreCase("mod"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"mod\")");
			setState(1347); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KNAMEContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KNAMEContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kNAME; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKNAME(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKNAME(this);
		}
	}

	public final KNAMEContext kNAME() throws RecognitionException {
		KNAMEContext _localctx = new KNAMEContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_kNAME);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1349);
			if (!(_input.LT(1).getText().equalsIgnoreCase("name"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"name\")");
			setState(1350); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KOFContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KOFContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kOF; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKOF(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKOF(this);
		}
	}

	public final KOFContext kOF() throws RecognitionException {
		KOFContext _localctx = new KOFContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_kOF);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1352);
			if (!(_input.LT(1).getText().equalsIgnoreCase("of"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"of\")");
			setState(1353); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KREPLACEContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KREPLACEContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kREPLACE; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKREPLACE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKREPLACE(this);
		}
	}

	public final KREPLACEContext kREPLACE() throws RecognitionException {
		KREPLACEContext _localctx = new KREPLACEContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_kREPLACE);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1355);
			if (!(_input.LT(1).getText().equalsIgnoreCase("replace"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"replace\")");
			setState(1356); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KROWCOUNTContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KROWCOUNTContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kROWCOUNT; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKROWCOUNT(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKROWCOUNT(this);
		}
	}

	public final KROWCOUNTContext kROWCOUNT() throws RecognitionException {
		KROWCOUNTContext _localctx = new KROWCOUNTContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_kROWCOUNT);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1358);
			if (!(_input.LT(1).getText().equalsIgnoreCase("rowcount"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"rowcount\")");
			setState(1359); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KSAVEContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KSAVEContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kSAVE; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKSAVE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKSAVE(this);
		}
	}

	public final KSAVEContext kSAVE() throws RecognitionException {
		KSAVEContext _localctx = new KSAVEContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_kSAVE);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1361);
			if (!(_input.LT(1).getText().equalsIgnoreCase("save"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"save\")");
			setState(1362); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KSHOWContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KSHOWContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kSHOW; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKSHOW(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKSHOW(this);
		}
	}

	public final KSHOWContext kSHOW() throws RecognitionException {
		KSHOWContext _localctx = new KSHOWContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_kSHOW);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1364);
			if (!(_input.LT(1).getText().equalsIgnoreCase("show"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"show\")");
			setState(1365); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KTYPEContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KTYPEContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kTYPE; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKTYPE(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKTYPE(this);
		}
	}

	public final KTYPEContext kTYPE() throws RecognitionException {
		KTYPEContext _localctx = new KTYPEContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_kTYPE);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1367);
			if (!(_input.LT(1).getText().equalsIgnoreCase("type"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"type\")");
			setState(1368); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KVALUESContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PLSQLParser.ID, 0); }
		public KVALUESContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kVALUES; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).enterKVALUES(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PLSQLListener ) ((PLSQLListener)listener).exitKVALUES(this);
		}
	}

	public final KVALUESContext kVALUES() throws RecognitionException {
		KVALUESContext _localctx = new KVALUESContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_kVALUES);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1370);
			if (!(_input.LT(1).getText().equalsIgnoreCase("values"))) throw new FailedPredicateException(this, "_input.LT(1).getText().equalsIgnoreCase(\"values\")");
			setState(1371); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 108: return kERRORS_sempred((KERRORSContext)_localctx, predIndex);

		case 109: return kEXCEPTIONS_sempred((KEXCEPTIONSContext)_localctx, predIndex);

		case 110: return kFOUND_sempred((KFOUNDContext)_localctx, predIndex);

		case 111: return kINDICES_sempred((KINDICESContext)_localctx, predIndex);

		case 112: return kMOD_sempred((KMODContext)_localctx, predIndex);

		case 113: return kNAME_sempred((KNAMEContext)_localctx, predIndex);

		case 114: return kOF_sempred((KOFContext)_localctx, predIndex);

		case 115: return kREPLACE_sempred((KREPLACEContext)_localctx, predIndex);

		case 116: return kROWCOUNT_sempred((KROWCOUNTContext)_localctx, predIndex);

		case 117: return kSAVE_sempred((KSAVEContext)_localctx, predIndex);

		case 118: return kSHOW_sempred((KSHOWContext)_localctx, predIndex);

		case 119: return kTYPE_sempred((KTYPEContext)_localctx, predIndex);

		case 120: return kVALUES_sempred((KVALUESContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean kFOUND_sempred(KFOUNDContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return _input.LT(1).getText().equalsIgnoreCase("found");
		}
		return true;
	}
	private boolean kSAVE_sempred(KSAVEContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9: return _input.LT(1).getText().equalsIgnoreCase("save");
		}
		return true;
	}
	private boolean kSHOW_sempred(KSHOWContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10: return _input.LT(1).getText().equalsIgnoreCase("show");
		}
		return true;
	}
	private boolean kINDICES_sempred(KINDICESContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3: return _input.LT(1).getText().equalsIgnoreCase("indices");
		}
		return true;
	}
	private boolean kOF_sempred(KOFContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6: return _input.LT(1).getText().equalsIgnoreCase("of");
		}
		return true;
	}
	private boolean kEXCEPTIONS_sempred(KEXCEPTIONSContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1: return _input.LT(1).getText().equalsIgnoreCase("exceptions");
		}
		return true;
	}
	private boolean kMOD_sempred(KMODContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4: return _input.LT(1).getText().equalsIgnoreCase("mod");
		}
		return true;
	}
	private boolean kREPLACE_sempred(KREPLACEContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7: return _input.LT(1).getText().equalsIgnoreCase("replace");
		}
		return true;
	}
	private boolean kTYPE_sempred(KTYPEContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11: return _input.LT(1).getText().equalsIgnoreCase("type");
		}
		return true;
	}
	private boolean kROWCOUNT_sempred(KROWCOUNTContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8: return _input.LT(1).getText().equalsIgnoreCase("rowcount");
		}
		return true;
	}
	private boolean kERRORS_sempred(KERRORSContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return _input.LT(1).getText().length() >= 3 && "errors".startsWith(_input.LT(1).getText().toLowerCase());
		}
		return true;
	}
	private boolean kNAME_sempred(KNAMEContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5: return _input.LT(1).getText().equalsIgnoreCase("name");
		}
		return true;
	}
	private boolean kVALUES_sempred(KVALUESContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12: return _input.LT(1).getText().equalsIgnoreCase("values");
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\177\u0560\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\3\2\3\2\3\2\5\2\u00f8\n\2\3\2\5\2\u00fb\n\2\6\2"+
		"\u00fd\n\2\r\2\16\2\u00fe\3\2\3\2\3\3\3\3\3\3\5\3\u0106\n\3\3\4\3\4\3"+
		"\4\3\4\5\4\u010c\n\4\3\5\3\5\3\5\5\5\u0111\n\5\3\6\3\6\3\6\5\6\u0116\n"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\7\7\u011f\n\7\f\7\16\7\u0122\13\7\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\5\b\u012b\n\b\3\b\5\b\u012e\n\b\5\b\u0130\n\b\3"+
		"\b\3\b\3\b\5\b\u0135\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\6\t\u014c\n\t\r\t\16\t\u014d\3"+
		"\n\3\n\3\n\5\n\u0153\n\n\3\n\3\n\3\n\3\13\3\13\3\13\5\13\u015b\n\13\3"+
		"\f\3\f\3\f\3\f\5\f\u0161\n\f\3\f\3\f\5\f\u0165\n\f\3\r\3\r\3\r\3\r\3\r"+
		"\5\r\u016c\n\r\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\5\17\u017a\n\17\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u0182\n\20\3\21\3"+
		"\21\3\21\3\21\3\21\7\21\u0189\n\21\f\21\16\21\u018c\13\21\3\21\3\21\3"+
		"\22\3\22\3\22\3\22\5\22\u0194\n\22\3\22\3\22\5\22\u0198\n\22\3\23\3\23"+
		"\5\23\u019c\n\23\3\24\3\24\5\24\u01a0\n\24\3\24\5\24\u01a3\n\24\3\24\3"+
		"\24\3\24\3\24\3\24\3\24\3\24\5\24\u01ac\n\24\3\25\3\25\3\25\3\25\3\25"+
		"\5\25\u01b3\n\25\3\25\3\25\3\25\5\25\u01b8\n\25\3\26\3\26\3\27\3\27\3"+
		"\27\3\27\5\27\u01c0\n\27\3\30\5\30\u01c3\n\30\3\30\3\30\3\30\5\30\u01c8"+
		"\n\30\3\30\3\30\3\30\3\30\7\30\u01ce\n\30\f\30\16\30\u01d1\13\30\3\30"+
		"\3\30\3\30\3\30\3\30\5\30\u01d8\n\30\5\30\u01da\n\30\3\31\3\31\7\31\u01de"+
		"\n\31\f\31\16\31\u01e1\13\31\3\31\3\31\5\31\u01e5\n\31\3\31\5\31\u01e8"+
		"\n\31\3\32\3\32\7\32\u01ec\n\32\f\32\16\32\u01ef\13\32\3\33\3\33\7\33"+
		"\u01f3\n\33\f\33\16\33\u01f6\13\33\3\33\3\33\5\33\u01fa\n\33\3\33\3\33"+
		"\3\34\3\34\3\34\5\34\u0201\n\34\3\34\5\34\u0204\n\34\3\35\3\35\3\36\3"+
		"\36\3\36\5\36\u020b\n\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\7\37\u0218\n\37\f\37\16\37\u021b\13\37\3\37\3\37\6\37\u021f"+
		"\n\37\r\37\16\37\u0220\5\37\u0223\n\37\3\37\3\37\5\37\u0227\n\37\3 \3"+
		" \3 \3 \7 \u022d\n \f \16 \u0230\13 \3 \5 \u0233\n \3 \3 \3 \3 \6 \u0239"+
		"\n \r \16 \u023a\3!\7!\u023e\n!\f!\16!\u0241\13!\3!\3!\3!\3!\3!\3!\3!"+
		"\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u0256\n!\3\"\3\"\3\"\7\"\u025b"+
		"\n\"\f\"\16\"\u025e\13\"\3#\3#\3#\3#\3#\5#\u0265\n#\3$\5$\u0268\n$\3$"+
		"\3$\3$\3$\3$\7$\u026f\n$\f$\16$\u0272\13$\5$\u0274\n$\3$\5$\u0277\n$\3"+
		"%\3%\3%\5%\u027c\n%\3%\5%\u027f\n%\3&\3&\3&\3&\6&\u0285\n&\r&\16&\u0286"+
		"\3&\3&\3&\5&\u028c\n&\3\'\3\'\5\'\u0290\n\'\3\'\3\'\3\'\3\'\3\'\3\'\6"+
		"\'\u0298\n\'\r\'\16\'\u0299\6\'\u029c\n\'\r\'\16\'\u029d\3\'\3\'\3\'\3"+
		"\'\5\'\u02a4\n\'\3\'\3\'\3\'\5\'\u02a9\n\'\3(\3(\3(\3(\5(\u02af\n(\3)"+
		"\3)\5)\u02b3\n)\3)\3)\5)\u02b7\n)\3*\3*\3*\3*\3*\5*\u02be\n*\3*\5*\u02c1"+
		"\n*\3*\3*\5*\u02c5\n*\3*\5*\u02c8\n*\3+\3+\5+\u02cc\n+\3+\3+\5+\u02d0"+
		"\n+\3,\3,\3,\3,\3,\3,\5,\u02d8\n,\5,\u02da\n,\3-\3-\3-\3-\7-\u02e0\n-"+
		"\f-\16-\u02e3\13-\3.\3.\3.\3.\3.\3.\7.\u02eb\n.\f.\16.\u02ee\13.\3/\3"+
		"/\5/\u02f2\n/\3/\3/\3/\5/\u02f7\n/\3/\7/\u02fa\n/\f/\16/\u02fd\13/\3\60"+
		"\3\60\5\60\u0301\n\60\3\60\5\60\u0304\n\60\3\61\3\61\3\61\5\61\u0309\n"+
		"\61\3\62\3\62\3\62\3\62\6\62\u030f\n\62\r\62\16\62\u0310\3\62\3\62\3\62"+
		"\3\62\6\62\u0317\n\62\r\62\16\62\u0318\3\62\3\62\3\62\5\62\u031e\n\62"+
		"\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u0328\n\63\3\64\3\64\3\64"+
		"\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\64\5\64\u0336\n\64\3\64\3\64"+
		"\3\64\3\64\5\64\u033c\n\64\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66"+
		"\6\66\u0347\n\66\r\66\16\66\u0348\3\66\3\66\3\66\3\66\3\66\3\66\6\66\u0351"+
		"\n\66\r\66\16\66\u0352\7\66\u0355\n\66\f\66\16\66\u0358\13\66\3\66\3\66"+
		"\3\66\3\66\6\66\u035e\n\66\r\66\16\66\u035f\5\66\u0362\n\66\3\66\3\66"+
		"\3\66\3\67\3\67\38\38\38\38\78\u036d\n8\f8\168\u0370\138\38\58\u0373\n"+
		"8\38\38\58\u0377\n8\39\39\39\3:\3:\3:\3:\7:\u0380\n:\f:\16:\u0383\13:"+
		"\5:\u0385\n:\3;\3;\5;\u0389\n;\3<\3<\5<\u038d\n<\3<\3<\3=\3=\3=\3=\3>"+
		"\5>\u0396\n>\3>\3>\3>\5>\u039b\n>\3>\7>\u039e\n>\f>\16>\u03a1\13>\3?\3"+
		"?\3?\3?\3?\3?\3?\3?\3?\5?\u03ac\n?\3@\3@\5@\u03b0\n@\3A\3A\3A\3B\3B\3"+
		"B\3C\3C\3C\3C\3D\3D\5D\u03be\nD\3E\3E\3E\3F\3F\3F\3G\3G\3G\3G\3H\3H\3"+
		"H\3I\6I\u03ce\nI\rI\16I\u03cf\3J\3J\3J\3J\3J\3J\6J\u03d8\nJ\rJ\16J\u03d9"+
		"\3J\3J\3J\5J\u03df\nJ\3K\7K\u03e2\nK\fK\16K\u03e5\13K\3K\3K\3K\3K\5K\u03eb"+
		"\nK\3L\3L\3M\3M\3N\3N\3N\7N\u03f4\nN\fN\16N\u03f7\13N\3O\3O\3O\7O\u03fc"+
		"\nO\fO\16O\u03ff\13O\3P\5P\u0402\nP\3P\3P\3Q\3Q\3Q\5Q\u0409\nQ\3R\3R\3"+
		"R\5R\u040e\nR\3R\5R\u0411\nR\3S\3S\5S\u0415\nS\3S\3S\5S\u0419\nS\3T\3"+
		"T\5T\u041d\nT\3T\3T\3T\3T\3T\5T\u0424\nT\3U\3U\5U\u0428\nU\3U\3U\3U\3"+
		"U\3U\7U\u042f\nU\fU\16U\u0432\13U\3U\3U\5U\u0436\nU\3V\3V\3W\3W\3W\7W"+
		"\u043d\nW\fW\16W\u0440\13W\3X\3X\3X\3X\5X\u0446\nX\3X\7X\u0449\nX\fX\16"+
		"X\u044c\13X\3Y\5Y\u044f\nY\3Y\3Y\3Z\3Z\3Z\5Z\u0456\nZ\3[\3[\3[\5[\u045b"+
		"\n[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\5[\u0468\n[\3\\\3\\\3\\\7\\\u046d"+
		"\n\\\f\\\16\\\u0470\13\\\3\\\3\\\5\\\u0474\n\\\3]\3]\3]\3]\3]\3]\3]\3"+
		"]\3]\5]\u047f\n]\3^\3^\3^\3^\7^\u0485\n^\f^\16^\u0488\13^\5^\u048a\n^"+
		"\3^\3^\3_\3_\3_\5_\u0491\n_\3`\3`\3a\3a\3b\3b\3c\3c\3d\3d\3d\3d\3d\3d"+
		"\3d\3e\3e\3e\3e\3e\5e\u04a7\ne\3e\5e\u04aa\ne\3f\3f\5f\u04ae\nf\3f\3f"+
		"\3g\3g\3h\3h\3h\5h\u04b7\nh\3h\3h\3h\5h\u04bc\nh\3h\3h\5h\u04c0\nh\3h"+
		"\3h\5h\u04c4\nh\3h\3h\5h\u04c8\nh\3h\3h\3i\3i\3i\5i\u04cf\ni\3i\3i\3i"+
		"\3i\5i\u04d5\ni\3i\3i\3i\5i\u04da\ni\3i\3i\3i\5i\u04df\ni\5i\u04e1\ni"+
		"\3i\3i\3j\3j\3j\5j\u04e8\nj\3j\3j\3j\5j\u04ed\nj\3j\3j\3j\3j\3j\7j\u04f4"+
		"\nj\fj\16j\u04f7\13j\3j\3j\5j\u04fb\nj\3j\5j\u04fe\nj\3j\3j\5j\u0502\n"+
		"j\3j\3j\3j\5j\u0507\nj\3j\3j\3k\3k\3k\5k\u050e\nk\3k\3k\3k\5k\u0513\n"+
		"k\3k\3k\3k\3k\3k\7k\u051a\nk\fk\16k\u051d\13k\3k\3k\5k\u0521\nk\3k\3k"+
		"\3k\5k\u0526\nk\3k\3k\5k\u052a\nk\3k\3k\3k\5k\u052f\nk\3k\3k\3l\3l\3l"+
		"\3m\3m\3m\3n\3n\3n\3o\3o\3o\3p\3p\3p\3q\3q\3q\3r\3r\3r\3s\3s\3s\3t\3t"+
		"\3t\3u\3u\3u\3v\3v\3v\3w\3w\3w\3x\3x\3x\3y\3y\3y\3z\3z\3z\3z\2\2{\2\4"+
		"\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNP"+
		"RTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e"+
		"\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6"+
		"\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be"+
		"\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6"+
		"\u00d8\u00da\u00dc\u00de\u00e0\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee"+
		"\u00f0\u00f2\2\17\4\2\21\21rr\5\2CCNOTT\4\2\5\5!!\3\2UV\3\2JJ\3\2__\b"+
		"\2\5\5\35\35!!MM__gh\4\2nnvz\4\2kluu\3\2kl\3\2{|\4\2\27\27\64\64\4\2\20"+
		"\20\22\22\u05c5\2\u00fc\3\2\2\2\4\u0102\3\2\2\2\6\u010b\3\2\2\2\b\u010d"+
		"\3\2\2\2\n\u0112\3\2\2\2\f\u011a\3\2\2\2\16\u0125\3\2\2\2\20\u014b\3\2"+
		"\2\2\22\u014f\3\2\2\2\24\u015a\3\2\2\2\26\u015c\3\2\2\2\30\u0166\3\2\2"+
		"\2\32\u0170\3\2\2\2\34\u0173\3\2\2\2\36\u017b\3\2\2\2 \u0183\3\2\2\2\""+
		"\u018f\3\2\2\2$\u019b\3\2\2\2&\u01a2\3\2\2\2(\u01ad\3\2\2\2*\u01b9\3\2"+
		"\2\2,\u01bb\3\2\2\2.\u01c2\3\2\2\2\60\u01db\3\2\2\2\62\u01e9\3\2\2\2\64"+
		"\u01f0\3\2\2\2\66\u01fd\3\2\2\28\u0205\3\2\2\2:\u0207\3\2\2\2<\u020e\3"+
		"\2\2\2>\u0228\3\2\2\2@\u023f\3\2\2\2B\u0257\3\2\2\2D\u025f\3\2\2\2F\u0267"+
		"\3\2\2\2H\u0278\3\2\2\2J\u0280\3\2\2\2L\u028d\3\2\2\2N\u02aa\3\2\2\2P"+
		"\u02b0\3\2\2\2R\u02b8\3\2\2\2T\u02c9\3\2\2\2V\u02d1\3\2\2\2X\u02db\3\2"+
		"\2\2Z\u02e4\3\2\2\2\\\u02ef\3\2\2\2^\u0303\3\2\2\2`\u0305\3\2\2\2b\u030a"+
		"\3\2\2\2d\u031f\3\2\2\2f\u033b\3\2\2\2h\u033d\3\2\2\2j\u0340\3\2\2\2l"+
		"\u0366\3\2\2\2n\u0368\3\2\2\2p\u0378\3\2\2\2r\u037b\3\2\2\2t\u0386\3\2"+
		"\2\2v\u038c\3\2\2\2x\u0390\3\2\2\2z\u0395\3\2\2\2|\u03ab\3\2\2\2~\u03ad"+
		"\3\2\2\2\u0080\u03b1\3\2\2\2\u0082\u03b4\3\2\2\2\u0084\u03b7\3\2\2\2\u0086"+
		"\u03bb\3\2\2\2\u0088\u03bf\3\2\2\2\u008a\u03c2\3\2\2\2\u008c\u03c5\3\2"+
		"\2\2\u008e\u03c9\3\2\2\2\u0090\u03cd\3\2\2\2\u0092\u03d1\3\2\2\2\u0094"+
		"\u03ea\3\2\2\2\u0096\u03ec\3\2\2\2\u0098\u03ee\3\2\2\2\u009a\u03f0\3\2"+
		"\2\2\u009c\u03f8\3\2\2\2\u009e\u0401\3\2\2\2\u00a0\u0405\3\2\2\2\u00a2"+
		"\u040a\3\2\2\2\u00a4\u0412\3\2\2\2\u00a6\u041a\3\2\2\2\u00a8\u0425\3\2"+
		"\2\2\u00aa\u0437\3\2\2\2\u00ac\u0439\3\2\2\2\u00ae\u0441\3\2\2\2\u00b0"+
		"\u044e\3\2\2\2\u00b2\u0452\3\2\2\2\u00b4\u0467\3\2\2\2\u00b6\u0469\3\2"+
		"\2\2\u00b8\u047e\3\2\2\2\u00ba\u0480\3\2\2\2\u00bc\u0490\3\2\2\2\u00be"+
		"\u0492\3\2\2\2\u00c0\u0494\3\2\2\2\u00c2\u0496\3\2\2\2\u00c4\u0498\3\2"+
		"\2\2\u00c6\u049a\3\2\2\2\u00c8\u04a9\3\2\2\2\u00ca\u04ad\3\2\2\2\u00cc"+
		"\u04b1\3\2\2\2\u00ce\u04b3\3\2\2\2\u00d0\u04cb\3\2\2\2\u00d2\u04e4\3\2"+
		"\2\2\u00d4\u050a\3\2\2\2\u00d6\u0532\3\2\2\2\u00d8\u0535\3\2\2\2\u00da"+
		"\u0538\3\2\2\2\u00dc\u053b\3\2\2\2\u00de\u053e\3\2\2\2\u00e0\u0541\3\2"+
		"\2\2\u00e2\u0544\3\2\2\2\u00e4\u0547\3\2\2\2\u00e6\u054a\3\2\2\2\u00e8"+
		"\u054d\3\2\2\2\u00ea\u0550\3\2\2\2\u00ec\u0553\3\2\2\2\u00ee\u0556\3\2"+
		"\2\2\u00f0\u0559\3\2\2\2\u00f2\u055c\3\2\2\2\u00f4\u00f7\5\6\4\2\u00f5"+
		"\u00f6\7m\2\2\u00f6\u00f8\5\4\3\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2"+
		"\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00fb\7m\2\2\u00fa\u00f9\3\2\2\2\u00fa"+
		"\u00fb\3\2\2\2\u00fb\u00fd\3\2\2\2\u00fc\u00f4\3\2\2\2\u00fd\u00fe\3\2"+
		"\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u0101\7\2\2\3\u0101\3\3\2\2\2\u0102\u0103\5\u00eex\2\u0103\u0105\5\u00da"+
		"n\2\u0104\u0106\7_\2\2\u0105\u0104\3\2\2\2\u0105\u0106\3\2\2\2\u0106\5"+
		"\3\2\2\2\u0107\u010c\5\u00d0i\2\u0108\u010c\5\u00d0i\2\u0109\u010c\5\u00d4"+
		"k\2\u010a\u010c\5\u00d2j\2\u010b\u0107\3\2\2\2\u010b\u0108\3\2\2\2\u010b"+
		"\u0109\3\2\2\2\u010b\u010a\3\2\2\2\u010c\7\3\2\2\2\u010d\u010e\7Q\2\2"+
		"\u010e\u0110\7^\2\2\u010f\u0111\5\f\7\2\u0110\u010f\3\2\2\2\u0110\u0111"+
		"\3\2\2\2\u0111\t\3\2\2\2\u0112\u0113\7H\2\2\u0113\u0115\7^\2\2\u0114\u0116"+
		"\5\f\7\2\u0115\u0114\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117"+
		"\u0118\7U\2\2\u0118\u0119\5.\30\2\u0119\13\3\2\2\2\u011a\u011b\7h\2\2"+
		"\u011b\u0120\5\16\b\2\u011c\u011d\7c\2\2\u011d\u011f\5\16\b\2\u011e\u011c"+
		"\3\2\2\2\u011f\u0122\3\2\2\2\u0120\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121"+
		"\u0123\3\2\2\2\u0122\u0120\3\2\2\2\u0123\u0124\7g\2\2\u0124\r\3\2\2\2"+
		"\u0125\u012f\7^\2\2\u0126\u0130\7\35\2\2\u0127\u012b\7M\2\2\u0128\u0129"+
		"\7\35\2\2\u0129\u012b\7M\2\2\u012a\u0127\3\2\2\2\u012a\u0128\3\2\2\2\u012b"+
		"\u012d\3\2\2\2\u012c\u012e\7K\2\2\u012d\u012c\3\2\2\2\u012d\u012e\3\2"+
		"\2\2\u012e\u0130\3\2\2\2\u012f\u0126\3\2\2\2\u012f\u012a\3\2\2\2\u012f"+
		"\u0130\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0134\5.\30\2\u0132\u0133\t\2"+
		"\2\2\u0133\u0135\5\u0098M\2\u0134\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"\17\3\2\2\2\u0136\u0137\5\34\17\2\u0137\u0138\7_\2\2\u0138\u014c\3\2\2"+
		"\2\u0139\u013a\5\36\20\2\u013a\u013b\7_\2\2\u013b\u014c\3\2\2\2\u013c"+
		"\u013d\5\22\n\2\u013d\u013e\7_\2\2\u013e\u014c\3\2\2\2\u013f\u0140\5\24"+
		"\13\2\u0140\u0141\7_\2\2\u0141\u014c\3\2\2\2\u0142\u0143\5\60\31\2\u0143"+
		"\u0144\7_\2\2\u0144\u014c\3\2\2\2\u0145\u0146\5\66\34\2\u0146\u0147\7"+
		"_\2\2\u0147\u014c\3\2\2\2\u0148\u0149\5p9\2\u0149\u014a\7_\2\2\u014a\u014c"+
		"\3\2\2\2\u014b\u0136\3\2\2\2\u014b\u0139\3\2\2\2\u014b\u013c\3\2\2\2\u014b"+
		"\u013f\3\2\2\2\u014b\u0142\3\2\2\2\u014b\u0145\3\2\2\2\u014b\u0148\3\2"+
		"\2\2\u014c\u014d\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014e\3\2\2\2\u014e"+
		"\21\3\2\2\2\u014f\u0150\7A\2\2\u0150\u0152\7^\2\2\u0151\u0153\5\f\7\2"+
		"\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0155"+
		"\7!\2\2\u0155\u0156\5\u008aF\2\u0156\23\3\2\2\2\u0157\u015b\5\26\f\2\u0158"+
		"\u015b\5\30\r\2\u0159\u015b\5\32\16\2\u015a\u0157\3\2\2\2\u015a\u0158"+
		"\3\2\2\2\u015a\u0159\3\2\2\2\u015b\25\3\2\2\2\u015c\u015d\7^\2\2\u015d"+
		"\u0164\5.\30\2\u015e\u015f\7&\2\2\u015f\u0161\7(\2\2\u0160\u015e\3\2\2"+
		"\2\u0160\u0161\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0163\t\2\2\2\u0163\u0165"+
		"\5\u0098M\2\u0164\u0160\3\2\2\2\u0164\u0165\3\2\2\2\u0165\27\3\2\2\2\u0166"+
		"\u0167\7^\2\2\u0167\u0168\7?\2\2\u0168\u016b\5.\30\2\u0169\u016a\7&\2"+
		"\2\u016a\u016c\7(\2\2\u016b\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016d"+
		"\3\2\2\2\u016d\u016e\t\2\2\2\u016e\u016f\5\u0098M\2\u016f\31\3\2\2\2\u0170"+
		"\u0171\7^\2\2\u0171\u0172\7E\2\2\u0172\33\3\2\2\2\u0173\u0174\5\u00f0"+
		"y\2\u0174\u0175\7^\2\2\u0175\u0179\7!\2\2\u0176\u017a\5 \21\2\u0177\u017a"+
		"\5$\23\2\u0178\u017a\5,\27\2\u0179\u0176\3\2\2\2\u0179\u0177\3\2\2\2\u0179"+
		"\u0178\3\2\2\2\u017a\35\3\2\2\2\u017b\u017c\7X\2\2\u017c\u017d\7^\2\2"+
		"\u017d\u017e\7!\2\2\u017e\u0181\5.\30\2\u017f\u0180\7&\2\2\u0180\u0182"+
		"\7(\2\2\u0181\u017f\3\2\2\2\u0181\u0182\3\2\2\2\u0182\37\3\2\2\2\u0183"+
		"\u0184\7R\2\2\u0184\u0185\7h\2\2\u0185\u018a\5\"\22\2\u0186\u0187\7c\2"+
		"\2\u0187\u0189\5\"\22\2\u0188\u0186\3\2\2\2\u0189\u018c\3\2\2\2\u018a"+
		"\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018d\3\2\2\2\u018c\u018a\3\2"+
		"\2\2\u018d\u018e\7g\2\2\u018e!\3\2\2\2\u018f\u0190\7^\2\2\u0190\u0197"+
		"\5.\30\2\u0191\u0192\7&\2\2\u0192\u0194\7(\2\2\u0193\u0191\3\2\2\2\u0193"+
		"\u0194\3\2\2\2\u0194\u0195\3\2\2\2\u0195\u0196\t\2\2\2\u0196\u0198\5\u0098"+
		"M\2\u0197\u0193\3\2\2\2\u0197\u0198\3\2\2\2\u0198#\3\2\2\2\u0199\u019c"+
		"\5&\24\2\u019a\u019c\5(\25\2\u019b\u0199\3\2\2\2\u019b\u019a\3\2\2\2\u019c"+
		"%\3\2\2\2\u019d\u019f\7[\2\2\u019e\u01a0\7\4\2\2\u019f\u019e\3\2\2\2\u019f"+
		"\u01a0\3\2\2\2\u01a0\u01a3\3\2\2\2\u01a1\u01a3\7Z\2\2\u01a2\u019d\3\2"+
		"\2\2\u01a2\u01a1\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4\u01a5\7h\2\2\u01a5"+
		"\u01a6\5\u00c0a\2\u01a6\u01a7\7g\2\2\u01a7\u01a8\5\u00e6t\2\u01a8\u01ab"+
		"\5.\30\2\u01a9\u01aa\7&\2\2\u01aa\u01ac\7(\2\2\u01ab\u01a9\3\2\2\2\u01ab"+
		"\u01ac\3\2\2\2\u01ac\'\3\2\2\2\u01ad\u01ae\7\62\2\2\u01ae\u01af\5\u00e6"+
		"t\2\u01af\u01b2\5.\30\2\u01b0\u01b1\7&\2\2\u01b1\u01b3\7(\2\2\u01b2\u01b0"+
		"\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b7\3\2\2\2\u01b4\u01b5\7\36\2\2"+
		"\u01b5\u01b6\7\13\2\2\u01b6\u01b8\5*\26\2\u01b7\u01b4\3\2\2\2\u01b7\u01b8"+
		"\3\2\2\2\u01b8)\3\2\2\2\u01b9\u01ba\5.\30\2\u01ba+\3\2\2\2\u01bb\u01bc"+
		"\7S\2\2\u01bc\u01bf\7A\2\2\u01bd\u01be\7U\2\2\u01be\u01c0\5.\30\2\u01bf"+
		"\u01bd\3\2\2\2\u01bf\u01c0\3\2\2\2\u01c0-\3\2\2\2\u01c1\u01c3\7S\2\2\u01c2"+
		"\u01c1\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c7\7^"+
		"\2\2\u01c5\u01c6\7b\2\2\u01c6\u01c8\7^\2\2\u01c7\u01c5\3\2\2\2\u01c7\u01c8"+
		"\3\2\2\2\u01c8\u01d9\3\2\2\2\u01c9\u01ca\7h\2\2\u01ca\u01cf\5\u00c0a\2"+
		"\u01cb\u01cc\7c\2\2\u01cc\u01ce\5\u00c0a\2\u01cd\u01cb\3\2\2\2\u01ce\u01d1"+
		"\3\2\2\2\u01cf\u01cd\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d2\3\2\2\2\u01d1"+
		"\u01cf\3\2\2\2\u01d2\u01d3\7g\2\2\u01d3\u01da\3\2\2\2\u01d4\u01d7\7o\2"+
		"\2\u01d5\u01d8\5\u00f0y\2\u01d6\u01d8\7W\2\2\u01d7\u01d5\3\2\2\2\u01d7"+
		"\u01d6\3\2\2\2\u01d8\u01da\3\2\2\2\u01d9\u01c9\3\2\2\2\u01d9\u01d4\3\2"+
		"\2\2\u01d9\u01da\3\2\2\2\u01da/\3\2\2\2\u01db\u01df\5\n\6\2\u01dc\u01de"+
		"\t\3\2\2\u01dd\u01dc\3\2\2\2\u01de\u01e1\3\2\2\2\u01df\u01dd\3\2\2\2\u01df"+
		"\u01e0\3\2\2\2\u01e0\u01e7\3\2\2\2\u01e1\u01df\3\2\2\2\u01e2\u01e4\t\4"+
		"\2\2\u01e3\u01e5\5\20\t\2\u01e4\u01e3\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5"+
		"\u01e6\3\2\2\2\u01e6\u01e8\5<\37\2\u01e7\u01e2\3\2\2\2\u01e7\u01e8\3\2"+
		"\2\2\u01e8\61\3\2\2\2\u01e9\u01ed\5\n\6\2\u01ea\u01ec\t\3\2\2\u01eb\u01ea"+
		"\3\2\2\2\u01ec\u01ef\3\2\2\2\u01ed\u01eb\3\2\2\2\u01ed\u01ee\3\2\2\2\u01ee"+
		"\63\3\2\2\2\u01ef\u01ed\3\2\2\2\u01f0\u01f4\5\n\6\2\u01f1\u01f3\t\3\2"+
		"\2\u01f2\u01f1\3\2\2\2\u01f3\u01f6\3\2\2\2\u01f4\u01f2\3\2\2\2\u01f4\u01f5"+
		"\3\2\2\2\u01f5\u01f7\3\2\2\2\u01f6\u01f4\3\2\2\2\u01f7\u01f9\t\4\2\2\u01f8"+
		"\u01fa\5\20\t\2\u01f9\u01f8\3\2\2\2\u01f9\u01fa\3\2\2\2\u01fa\u01fb\3"+
		"\2\2\2\u01fb\u01fc\5<\37\2\u01fc\65\3\2\2\2\u01fd\u0203\5\b\5\2\u01fe"+
		"\u0200\t\4\2\2\u01ff\u0201\5\20\t\2\u0200\u01ff\3\2\2\2\u0200\u0201\3"+
		"\2\2\2\u0201\u0202\3\2\2\2\u0202\u0204\5<\37\2\u0203\u01fe\3\2\2\2\u0203"+
		"\u0204\3\2\2\2\u0204\67\3\2\2\2\u0205\u0206\5\b\5\2\u02069\3\2\2\2\u0207"+
		"\u0208\5\b\5\2\u0208\u020a\t\4\2\2\u0209\u020b\5\20\t\2\u020a\u0209\3"+
		"\2\2\2\u020a\u020b\3\2\2\2\u020b\u020c\3\2\2\2\u020c\u020d\5<\37\2\u020d"+
		";\3\2\2\2\u020e\u020f\7=\2\2\u020f\u0210\5@!\2\u0210\u0219\7_\2\2\u0211"+
		"\u0212\5@!\2\u0212\u0213\7_\2\2\u0213\u0218\3\2\2\2\u0214\u0215\5p9\2"+
		"\u0215\u0216\7_\2\2\u0216\u0218\3\2\2\2\u0217\u0211\3\2\2\2\u0217\u0214"+
		"\3\2\2\2\u0218\u021b\3\2\2\2\u0219\u0217\3\2\2\2\u0219\u021a\3\2\2\2\u021a"+
		"\u0222\3\2\2\2\u021b\u0219\3\2\2\2\u021c\u021e\7E\2\2\u021d\u021f\5> "+
		"\2\u021e\u021d\3\2\2\2\u021f\u0220\3\2\2\2\u0220\u021e\3\2\2\2\u0220\u0221"+
		"\3\2\2\2\u0221\u0223\3\2\2\2\u0222\u021c\3\2\2\2\u0222\u0223\3\2\2\2\u0223"+
		"\u0224\3\2\2\2\u0224\u0226\7D\2\2\u0225\u0227\7^\2\2\u0226\u0225\3\2\2"+
		"\2\u0226\u0227\3\2\2\2\u0227=\3\2\2\2\u0228\u0232\7\\\2\2\u0229\u022e"+
		"\5z>\2\u022a\u022b\7*\2\2\u022b\u022d\5z>\2\u022c\u022a\3\2\2\2\u022d"+
		"\u0230\3\2\2\2\u022e\u022c\3\2\2\2\u022e\u022f\3\2\2\2\u022f\u0233\3\2"+
		"\2\2\u0230\u022e\3\2\2\2\u0231\u0233\7L\2\2\u0232\u0229\3\2\2\2\u0232"+
		"\u0231\3\2\2\2\u0233\u0234\3\2\2\2\u0234\u0238\7\65\2\2\u0235\u0236\5"+
		"@!\2\u0236\u0237\7_\2\2\u0237\u0239\3\2\2\2\u0238\u0235\3\2\2\2\u0239"+
		"\u023a\3\2\2\2\u023a\u0238\3\2\2\2\u023a\u023b\3\2\2\2\u023b?\3\2\2\2"+
		"\u023c\u023e\5x=\2\u023d\u023c\3\2\2\2\u023e\u0241\3\2\2\2\u023f\u023d"+
		"\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0255\3\2\2\2\u0241\u023f\3\2\2\2\u0242"+
		"\u0256\5D#\2\u0243\u0256\5L\'\2\u0244\u0256\5N(\2\u0245\u0256\5P)\2\u0246"+
		"\u0256\5J&\2\u0247\u0256\5R*\2\u0248\u0256\5T+\2\u0249\u0256\5V,\2\u024a"+
		"\u0256\5b\62\2\u024b\u0256\5d\63\2\u024c\u0256\5h\65\2\u024d\u0256\5j"+
		"\66\2\u024e\u0256\5l\67\2\u024f\u0256\5n8\2\u0250\u0256\5v<\2\u0251\u0256"+
		"\5r:\2\u0252\u0256\5t;\2\u0253\u0256\5|?\2\u0254\u0256\5\u0092J\2\u0255"+
		"\u0242\3\2\2\2\u0255\u0243\3\2\2\2\u0255\u0244\3\2\2\2\u0255\u0245\3\2"+
		"\2\2\u0255\u0246\3\2\2\2\u0255\u0247\3\2\2\2\u0255\u0248\3\2\2\2\u0255"+
		"\u0249\3\2\2\2\u0255\u024a\3\2\2\2\u0255\u024b\3\2\2\2\u0255\u024c\3\2"+
		"\2\2\u0255\u024d\3\2\2\2\u0255\u024e\3\2\2\2\u0255\u024f\3\2\2\2\u0255"+
		"\u0250\3\2\2\2\u0255\u0251\3\2\2\2\u0255\u0252\3\2\2\2\u0255\u0253\3\2"+
		"\2\2\u0255\u0254\3\2\2\2\u0256A\3\2\2\2\u0257\u025c\5F$\2\u0258\u0259"+
		"\7b\2\2\u0259\u025b\5F$\2\u025a\u0258\3\2\2\2\u025b\u025e\3\2\2\2\u025c"+
		"\u025a\3\2\2\2\u025c\u025d\3\2\2\2\u025dC\3\2\2\2\u025e\u025c\3\2\2\2"+
		"\u025f\u0264\5B\"\2\u0260\u0261\7b\2\2\u0261\u0265\5H%\2\u0262\u0263\7"+
		"r\2\2\u0263\u0265\5\u0098M\2\u0264\u0260\3\2\2\2\u0264\u0262\3\2\2\2\u0264"+
		"\u0265\3\2\2\2\u0265E\3\2\2\2\u0266\u0268\7`\2\2\u0267\u0266\3\2\2\2\u0267"+
		"\u0268\3\2\2\2\u0268\u0269\3\2\2\2\u0269\u0276\7^\2\2\u026a\u0273\7h\2"+
		"\2\u026b\u0270\5\u00caf\2\u026c\u026d\7c\2\2\u026d\u026f\5\u00caf\2\u026e"+
		"\u026c\3\2\2\2\u026f\u0272\3\2\2\2\u0270\u026e\3\2\2\2\u0270\u0271\3\2"+
		"\2\2\u0271\u0274\3\2\2\2\u0272\u0270\3\2\2\2\u0273\u026b\3\2\2\2\u0273"+
		"\u0274\3\2\2\2\u0274\u0275\3\2\2\2\u0275\u0277\7g\2\2\u0276\u026a\3\2"+
		"\2\2\u0276\u0277\3\2\2\2\u0277G\3\2\2\2\u0278\u027e\7\23\2\2\u0279\u027b"+
		"\7h\2\2\u027a\u027c\5\u00caf\2\u027b\u027a\3\2\2\2\u027b\u027c\3\2\2\2"+
		"\u027c\u027d\3\2\2\2\u027d\u027f\7g\2\2\u027e\u0279\3\2\2\2\u027e\u027f"+
		"\3\2\2\2\u027fI\3\2\2\2\u0280\u0284\7J\2\2\u0281\u0282\5@!\2\u0282\u0283"+
		"\7_\2\2\u0283\u0285\3\2\2\2\u0284\u0281\3\2\2\2\u0285\u0286\3\2\2\2\u0286"+
		"\u0284\3\2\2\2\u0286\u0287\3\2\2\2\u0287\u0288\3\2\2\2\u0288\u0289\7D"+
		"\2\2\u0289\u028b\7J\2\2\u028a\u028c\5\u0096L\2\u028b\u028a\3\2\2\2\u028b"+
		"\u028c\3\2\2\2\u028cK\3\2\2\2\u028d\u028f\7\f\2\2\u028e\u0290\5\u0098"+
		"M\2\u028f\u028e\3\2\2\2\u028f\u0290\3\2\2\2\u0290\u029b\3\2\2\2\u0291"+
		"\u0292\7\\\2\2\u0292\u0293\5\u0098M\2\u0293\u0297\7\65\2\2\u0294\u0295"+
		"\5@!\2\u0295\u0296\7_\2\2\u0296\u0298\3\2\2\2\u0297\u0294\3\2\2\2\u0298"+
		"\u0299\3\2\2\2\u0299\u0297\3\2\2\2\u0299\u029a\3\2\2\2\u029a\u029c\3\2"+
		"\2\2\u029b\u0291\3\2\2\2\u029c\u029d\3\2\2\2\u029d\u029b\3\2\2\2\u029d"+
		"\u029e\3\2\2\2\u029e\u02a3\3\2\2\2\u029f\u02a0\7\24\2\2\u02a0\u02a1\5"+
		"@!\2\u02a1\u02a2\7_\2\2\u02a2\u02a4\3\2\2\2\u02a3\u029f\3\2\2\2\u02a3"+
		"\u02a4\3\2\2\2\u02a4\u02a5\3\2\2\2\u02a5\u02a6\7D\2\2\u02a6\u02a8\7\f"+
		"\2\2\u02a7\u02a9\5\u0096L\2\u02a8\u02a7\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9"+
		"M\3\2\2\2\u02aa\u02ab\7>\2\2\u02ab\u02ae\7^\2\2\u02ac\u02ad\7b\2\2\u02ad"+
		"\u02af\7^\2\2\u02ae\u02ac\3\2\2\2\u02ae\u02af\3\2\2\2\u02afO\3\2\2\2\u02b0"+
		"\u02b2\7@\2\2\u02b1\u02b3\7^\2\2\u02b2\u02b1\3\2\2\2\u02b2\u02b3\3\2\2"+
		"\2\u02b3\u02b6\3\2\2\2\u02b4\u02b5\7\\\2\2\u02b5\u02b7\5\u0098M\2\u02b6"+
		"\u02b4\3\2\2\2\u02b6\u02b7\3\2\2\2\u02b7Q\3\2\2\2\u02b8\u02b9\7F\2\2\u02b9"+
		"\u02ba\7I\2\2\u02ba\u02c7\5\u0098M\2\u02bb\u02be\5X-\2\u02bc\u02be\5Z"+
		".\2\u02bd\u02bb\3\2\2\2\u02bd\u02bc\3\2\2\2\u02be\u02c0\3\2\2\2\u02bf"+
		"\u02c1\5\\/\2\u02c0\u02bf\3\2\2\2\u02c0\u02c1\3\2\2\2\u02c1\u02c8\3\2"+
		"\2\2\u02c2\u02c4\5\\/\2\u02c3\u02c5\5`\61\2\u02c4\u02c3\3\2\2\2\u02c4"+
		"\u02c5\3\2\2\2\u02c5\u02c8\3\2\2\2\u02c6\u02c8\5`\61\2\u02c7\u02bd\3\2"+
		"\2\2\u02c7\u02c2\3\2\2\2\u02c7\u02c6\3\2\2\2\u02c7\u02c8\3\2\2\2\u02c8"+
		"S\3\2\2\2\u02c9\u02cb\7G\2\2\u02ca\u02cc\7^\2\2\u02cb\u02ca\3\2\2\2\u02cb"+
		"\u02cc\3\2\2\2\u02cc\u02cf\3\2\2\2\u02cd\u02ce\7\\\2\2\u02ce\u02d0\5\u0098"+
		"M\2\u02cf\u02cd\3\2\2\2\u02cf\u02d0\3\2\2\2\u02d0U\3\2\2\2\u02d1\u02d2"+
		"\7\30\2\2\u02d2\u02d9\5z>\2\u02d3\u02da\5X-\2\u02d4\u02d7\5Z.\2\u02d5"+
		"\u02d6\7$\2\2\u02d6\u02d8\5\u00aaV\2\u02d7\u02d5\3\2\2\2\u02d7\u02d8\3"+
		"\2\2\2\u02d8\u02da\3\2\2\2\u02d9\u02d3\3\2\2\2\u02d9\u02d4\3\2\2\2\u02da"+
		"W\3\2\2\2\u02db\u02dc\7 \2\2\u02dc\u02e1\5B\"\2\u02dd\u02de\7c\2\2\u02de"+
		"\u02e0\5B\"\2\u02df\u02dd\3\2\2\2\u02e0\u02e3\3\2\2\2\u02e1\u02df\3\2"+
		"\2\2\u02e1\u02e2\3\2\2\2\u02e2Y\3\2\2\2\u02e3\u02e1\3\2\2\2\u02e4\u02e5"+
		"\7\t\2\2\u02e5\u02e6\7\16\2\2\u02e6\u02e7\7 \2\2\u02e7\u02ec\5B\"\2\u02e8"+
		"\u02e9\7c\2\2\u02e9\u02eb\5B\"\2\u02ea\u02e8\3\2\2\2\u02eb\u02ee\3\2\2"+
		"\2\u02ec\u02ea\3\2\2\2\u02ec\u02ed\3\2\2\2\u02ed[\3\2\2\2\u02ee\u02ec"+
		"\3\2\2\2\u02ef\u02f1\7Y\2\2\u02f0\u02f2\5^\60\2\u02f1\u02f0\3\2\2\2\u02f1"+
		"\u02f2\3\2\2\2\u02f2\u02f3\3\2\2\2\u02f3\u02fb\5\u0098M\2\u02f4\u02f6"+
		"\7c\2\2\u02f5\u02f7\5^\60\2\u02f6\u02f5\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7"+
		"\u02f8\3\2\2\2\u02f8\u02fa\5\u0098M\2\u02f9\u02f4\3\2\2\2\u02fa\u02fd"+
		"\3\2\2\2\u02fb\u02f9\3\2\2\2\u02fb\u02fc\3\2\2\2\u02fc]\3\2\2\2\u02fd"+
		"\u02fb\3\2\2\2\u02fe\u0300\7\35\2\2\u02ff\u0301\7M\2\2\u0300\u02ff\3\2"+
		"\2\2\u0300\u0301\3\2\2\2\u0301\u0304\3\2\2\2\u0302\u0304\7M\2\2\u0303"+
		"\u02fe\3\2\2\2\u0303\u0302\3\2\2\2\u0304_\3\2\2\2\u0305\u0308\t\5\2\2"+
		"\u0306\u0309\5X-\2\u0307\u0309\5Z.\2\u0308\u0306\3\2\2\2\u0308\u0307\3"+
		"\2\2\2\u0309a\3\2\2\2\u030a\u030b\7\31\2\2\u030b\u030c\7^\2\2\u030c\u030e"+
		"\7\35\2\2\u030d\u030f\n\6\2\2\u030e\u030d\3\2\2\2\u030f\u0310\3\2\2\2"+
		"\u0310\u030e\3\2\2\2\u0310\u0311\3\2\2\2\u0311\u0312\3\2\2\2\u0312\u0316"+
		"\7J\2\2\u0313\u0314\5@!\2\u0314\u0315\7_\2\2\u0315\u0317\3\2\2\2\u0316"+
		"\u0313\3\2\2\2\u0317\u0318\3\2\2\2\u0318\u0316\3\2\2\2\u0318\u0319\3\2"+
		"\2\2\u0319\u031a\3\2\2\2\u031a\u031b\7D\2\2\u031b\u031d\7J\2\2\u031c\u031e"+
		"\5\u0096L\2\u031d\u031c\3\2\2\2\u031d\u031e\3\2\2\2\u031ec\3\2\2\2\u031f"+
		"\u0320\7\32\2\2\u0320\u0321\7^\2\2\u0321\u0322\7\35\2\2\u0322\u0323\5"+
		"f\64\2\u0323\u0327\5|?\2\u0324\u0325\5\u00ecw\2\u0325\u0326\5\u00dco\2"+
		"\u0326\u0328\3\2\2\2\u0327\u0324\3\2\2\2\u0327\u0328\3\2\2\2\u0328e\3"+
		"\2\2\2\u0329\u032a\5\u00aaV\2\u032a\u032b\7a\2\2\u032b\u032c\5\u00aaV"+
		"\2\u032c\u033c\3\2\2\2\u032d\u032e\5\u00e0q\2\u032e\u032f\5\u00e6t\2\u032f"+
		"\u0335\5\u00b4[\2\u0330\u0331\7\7\2\2\u0331\u0332\5\u00aaV\2\u0332\u0333"+
		"\7\3\2\2\u0333\u0334\5\u00aaV\2\u0334\u0336\3\2\2\2\u0335\u0330\3\2\2"+
		"\2\u0335\u0336\3\2\2\2\u0336\u033c\3\2\2\2\u0337\u0338\5\u00f2z\2\u0338"+
		"\u0339\5\u00e6t\2\u0339\u033a\5\u00b4[\2\u033a\u033c\3\2\2\2\u033b\u0329"+
		"\3\2\2\2\u033b\u032d\3\2\2\2\u033b\u0337\3\2\2\2\u033cg\3\2\2\2\u033d"+
		"\u033e\7\33\2\2\u033e\u033f\5\u0096L\2\u033fi\3\2\2\2\u0340\u0341\7\34"+
		"\2\2\u0341\u0342\5\u0098M\2\u0342\u0346\7\65\2\2\u0343\u0344\5@!\2\u0344"+
		"\u0345\7_\2\2\u0345\u0347\3\2\2\2\u0346\u0343\3\2\2\2\u0347\u0348\3\2"+
		"\2\2\u0348\u0346\3\2\2\2\u0348\u0349\3\2\2\2\u0349\u0356\3\2\2\2\u034a"+
		"\u034b\7\25\2\2\u034b\u034c\5\u0098M\2\u034c\u0350\7\65\2\2\u034d\u034e"+
		"\5@!\2\u034e\u034f\7_\2\2\u034f\u0351\3\2\2\2\u0350\u034d\3\2\2\2\u0351"+
		"\u0352\3\2\2\2\u0352\u0350\3\2\2\2\u0352\u0353\3\2\2\2\u0353\u0355\3\2"+
		"\2\2\u0354\u034a\3\2\2\2\u0355\u0358\3\2\2\2\u0356\u0354\3\2\2\2\u0356"+
		"\u0357\3\2\2\2\u0357\u0361\3\2\2\2\u0358\u0356\3\2\2\2\u0359\u035d\7\24"+
		"\2\2\u035a\u035b\5@!\2\u035b\u035c\7_\2\2\u035c\u035e\3\2\2\2\u035d\u035a"+
		"\3\2\2\2\u035e\u035f\3\2\2\2\u035f\u035d\3\2\2\2\u035f\u0360\3\2\2\2\u0360"+
		"\u0362\3\2\2\2\u0361\u0359\3\2\2\2\u0361\u0362\3\2\2\2\u0362\u0363\3\2"+
		"\2\2\u0363\u0364\7D\2\2\u0364\u0365\7\34\2\2\u0365k\3\2\2\2\u0366\u0367"+
		"\7(\2\2\u0367m\3\2\2\2\u0368\u0369\7)\2\2\u0369\u036e\7^\2\2\u036a\u036b"+
		"\7b\2\2\u036b\u036d\7^\2\2\u036c\u036a\3\2\2\2\u036d\u0370\3\2\2\2\u036e"+
		"\u036c\3\2\2\2\u036e\u036f\3\2\2\2\u036f\u0372\3\2\2\2\u0370\u036e\3\2"+
		"\2\2\u0371\u0373\5\u00ba^\2\u0372\u0371\3\2\2\2\u0372\u0373\3\2\2\2\u0373"+
		"\u0376\3\2\2\2\u0374\u0375\7\31\2\2\u0375\u0377\5\u008aF\2\u0376\u0374"+
		"\3\2\2\2\u0376\u0377\3\2\2\2\u0377o\3\2\2\2\u0378\u0379\7P\2\2\u0379\u037a"+
		"\5\u0090I\2\u037aq\3\2\2\2\u037b\u0384\7,\2\2\u037c\u0381\7^\2\2\u037d"+
		"\u037e\7b\2\2\u037e\u0380\7^\2\2\u037f\u037d\3\2\2\2\u0380\u0383\3\2\2"+
		"\2\u0381\u037f\3\2\2\2\u0381\u0382\3\2\2\2\u0382\u0385\3\2\2\2\u0383\u0381"+
		"\3\2\2\2\u0384\u037c\3\2\2\2\u0384\u0385\3\2\2\2\u0385s\3\2\2\2\u0386"+
		"\u0388\7U\2\2\u0387\u0389\5\u0098M\2\u0388\u0387\3\2\2\2\u0388\u0389\3"+
		"\2\2\2\u0389u\3\2\2\2\u038a\u038b\7B\2\2\u038b\u038d\5\20\t\2\u038c\u038a"+
		"\3\2\2\2\u038c\u038d\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u038f\5<\37\2\u038f"+
		"w\3\2\2\2\u0390\u0391\7p\2\2\u0391\u0392\5x=\2\u0392\u0393\7q\2\2\u0393"+
		"y\3\2\2\2\u0394\u0396\7`\2\2\u0395\u0394\3\2\2\2\u0395\u0396\3\2\2\2\u0396"+
		"\u0397\3\2\2\2\u0397\u039f\7^\2\2\u0398\u039a\7b\2\2\u0399\u039b\7`\2"+
		"\2\u039a\u0399\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039c\3\2\2\2\u039c\u039e"+
		"\7^\2\2\u039d\u0398\3\2\2\2\u039e\u03a1\3\2\2\2\u039f\u039d\3\2\2\2\u039f"+
		"\u03a0\3\2\2\2\u03a0{\3\2\2\2\u03a1\u039f\3\2\2\2\u03a2\u03ac\5~@\2\u03a3"+
		"\u03ac\5\u0080A\2\u03a4\u03ac\5\u0082B\2\u03a5\u03ac\5\u0084C\2\u03a6"+
		"\u03ac\5\u0086D\2\u03a7\u03ac\5\u0088E\2\u03a8\u03ac\5\u008aF\2\u03a9"+
		"\u03ac\5\u008cG\2\u03aa\u03ac\5\u008eH\2\u03ab\u03a2\3\2\2\2\u03ab\u03a3"+
		"\3\2\2\2\u03ab\u03a4\3\2\2\2\u03ab\u03a5\3\2\2\2\u03ab\u03a6\3\2\2\2\u03ab"+
		"\u03a7\3\2\2\2\u03ab\u03a8\3\2\2\2\u03ab\u03a9\3\2\2\2\u03ab\u03aa\3\2"+
		"\2\2\u03ac}\3\2\2\2\u03ad\u03af\7\17\2\2\u03ae\u03b0\5\u0090I\2\u03af"+
		"\u03ae\3\2\2\2\u03af\u03b0\3\2\2\2\u03b0\177\3\2\2\2\u03b1\u03b2\7\23"+
		"\2\2\u03b2\u03b3\5\u0090I\2\u03b3\u0081\3\2\2\2\u03b4\u03b5\7\37\2\2\u03b5"+
		"\u03b6\5\u0090I\2\u03b6\u0083\3\2\2\2\u03b7\u03b8\7%\2\2\u03b8\u03b9\7"+
		"\62\2\2\u03b9\u03ba\5\u0090I\2\u03ba\u0085\3\2\2\2\u03bb\u03bd\7-\2\2"+
		"\u03bc\u03be\5\u0090I\2\u03bd\u03bc\3\2\2\2\u03bd\u03be\3\2\2\2\u03be"+
		"\u0087\3\2\2\2\u03bf\u03c0\7.\2\2\u03c0\u03c1\7^\2\2\u03c1\u0089\3\2\2"+
		"\2\u03c2\u03c3\7/\2\2\u03c3\u03c4\5\u0090I\2\u03c4\u008b\3\2\2\2\u03c5"+
		"\u03c6\7\60\2\2\u03c6\u03c7\7\63\2\2\u03c7\u03c8\5\u0090I\2\u03c8\u008d"+
		"\3\2\2\2\u03c9\u03ca\7\66\2\2\u03ca\u03cb\5\u0090I\2\u03cb\u008f\3\2\2"+
		"\2\u03cc\u03ce\n\7\2\2\u03cd\u03cc\3\2\2\2\u03ce\u03cf\3\2\2\2\u03cf\u03cd"+
		"\3\2\2\2\u03cf\u03d0\3\2\2\2\u03d0\u0091\3\2\2\2\u03d1\u03d2\7\67\2\2"+
		"\u03d2\u03d3\5\u0098M\2\u03d3\u03d7\7J\2\2\u03d4\u03d5\5@!\2\u03d5\u03d6"+
		"\7_\2\2\u03d6\u03d8\3\2\2\2\u03d7\u03d4\3\2\2\2\u03d8\u03d9\3\2\2\2\u03d9"+
		"\u03d7\3\2\2\2\u03d9\u03da\3\2\2\2\u03da\u03db\3\2\2\2\u03db\u03dc\7D"+
		"\2\2\u03dc\u03de\7J\2\2\u03dd\u03df\5\u0096L\2\u03de\u03dd\3\2\2\2\u03de"+
		"\u03df\3\2\2\2\u03df\u0093\3\2\2\2\u03e0\u03e2\n\b\2\2\u03e1\u03e0\3\2"+
		"\2\2\u03e2\u03e5\3\2\2\2\u03e3\u03e1\3\2\2\2\u03e3\u03e4\3\2\2\2\u03e4"+
		"\u03eb\3\2\2\2\u03e5\u03e3\3\2\2\2\u03e6\u03e7\7g\2\2\u03e7\u03e8\5\u0094"+
		"K\2\u03e8\u03e9\7h\2\2\u03e9\u03eb\3\2\2\2\u03ea\u03e3\3\2\2\2\u03ea\u03e6"+
		"\3\2\2\2\u03eb\u0095\3\2\2\2\u03ec\u03ed\7^\2\2\u03ed\u0097\3\2\2\2\u03ee"+
		"\u03ef\5\u009aN\2\u03ef\u0099\3\2\2\2\u03f0\u03f5\5\u009cO\2\u03f1\u03f2"+
		"\7*\2\2\u03f2\u03f4\5\u009cO\2\u03f3\u03f1\3\2\2\2\u03f4\u03f7\3\2\2\2"+
		"\u03f5\u03f3\3\2\2\2\u03f5\u03f6\3\2\2\2\u03f6\u009b\3\2\2\2\u03f7\u03f5"+
		"\3\2\2\2\u03f8\u03fd\5\u009eP\2\u03f9\u03fa\7\3\2\2\u03fa\u03fc\5\u009e"+
		"P\2\u03fb\u03f9\3\2\2\2\u03fc\u03ff\3\2\2\2\u03fd\u03fb\3\2\2\2\u03fd"+
		"\u03fe\3\2\2\2\u03fe\u009d\3\2\2\2\u03ff\u03fd\3\2\2\2\u0400\u0402\7&"+
		"\2\2\u0401\u0400\3\2\2\2\u0401\u0402\3\2\2\2\u0402\u0403\3\2\2\2\u0403"+
		"\u0404\5\u00a0Q\2\u0404\u009f\3\2\2\2\u0405\u0408\5\u00a2R\2\u0406\u0407"+
		"\t\t\2\2\u0407\u0409\5\u00a2R\2\u0408\u0406\3\2\2\2\u0408\u0409\3\2\2"+
		"\2\u0409\u00a1\3\2\2\2\u040a\u0410\5\u00a4S\2\u040b\u040d\7!\2\2\u040c"+
		"\u040e\7&\2\2\u040d\u040c\3\2\2\2\u040d\u040e\3\2\2\2\u040e\u040f\3\2"+
		"\2\2\u040f\u0411\7(\2\2\u0410\u040b\3\2\2\2\u0410\u0411\3\2\2\2\u0411"+
		"\u00a3\3\2\2\2\u0412\u0418\5\u00a6T\2\u0413\u0415\7&\2\2\u0414\u0413\3"+
		"\2\2\2\u0414\u0415\3\2\2\2\u0415\u0416\3\2\2\2\u0416\u0417\7#\2\2\u0417"+
		"\u0419\5\u00a6T\2\u0418\u0414\3\2\2\2\u0418\u0419\3\2\2\2\u0419\u00a5"+
		"\3\2\2\2\u041a\u0423\5\u00a8U\2\u041b\u041d\7&\2\2\u041c\u041b\3\2\2\2"+
		"\u041c\u041d\3\2\2\2\u041d\u041e\3\2\2\2\u041e\u041f\7\7\2\2\u041f\u0420"+
		"\5\u00a8U\2\u0420\u0421\7\3\2\2\u0421\u0422\5\u00a8U\2\u0422\u0424\3\2"+
		"\2\2\u0423\u041c\3\2\2\2\u0423\u0424\3\2\2\2\u0424\u00a7\3\2\2\2\u0425"+
		"\u0435\5\u00acW\2\u0426\u0428\7&\2\2\u0427\u0426\3\2\2\2\u0427\u0428\3"+
		"\2\2\2\u0428\u0429\3\2\2\2\u0429\u042a\7\35\2\2\u042a\u042b\7h\2\2\u042b"+
		"\u0430\5\u00acW\2\u042c\u042d\7c\2\2\u042d\u042f\5\u00acW\2\u042e\u042c"+
		"\3\2\2\2\u042f\u0432\3\2\2\2\u0430\u042e\3\2\2\2\u0430\u0431\3\2\2\2\u0431"+
		"\u0433\3\2\2\2\u0432\u0430\3\2\2\2\u0433\u0434\7g\2\2\u0434\u0436\3\2"+
		"\2\2\u0435\u0427\3\2\2\2\u0435\u0436\3\2\2\2\u0436\u00a9\3\2\2\2\u0437"+
		"\u0438\5\u00acW\2\u0438\u00ab\3\2\2\2\u0439\u043e\5\u00aeX\2\u043a\u043b"+
		"\t\n\2\2\u043b\u043d\5\u00aeX\2\u043c\u043a\3\2\2\2\u043d\u0440\3\2\2"+
		"\2\u043e\u043c\3\2\2\2\u043e\u043f\3\2\2\2\u043f\u00ad\3\2\2\2\u0440\u043e"+
		"\3\2\2\2\u0441\u044a\5\u00b0Y\2\u0442\u0446\7e\2\2\u0443\u0446\7m\2\2"+
		"\u0444\u0446\5\u00e2r\2\u0445\u0442\3\2\2\2\u0445\u0443\3\2\2\2\u0445"+
		"\u0444\3\2\2\2\u0446\u0447\3\2\2\2\u0447\u0449\5\u00b0Y\2\u0448\u0445"+
		"\3\2\2\2\u0449\u044c\3\2\2\2\u044a\u0448\3\2\2\2\u044a\u044b\3\2\2\2\u044b"+
		"\u00af\3\2\2\2\u044c\u044a\3\2\2\2\u044d\u044f\t\13\2\2\u044e\u044d\3"+
		"\2\2\2\u044e\u044f\3\2\2\2\u044f\u0450\3\2\2\2\u0450\u0451\5\u00b2Z\2"+
		"\u0451\u00b1\3\2\2\2\u0452\u0455\5\u00b4[\2\u0453\u0454\7d\2\2\u0454\u0456"+
		"\5\u00b4[\2\u0455\u0453\3\2\2\2\u0455\u0456\3\2\2\2\u0456\u00b3\3\2\2"+
		"\2\u0457\u045a\5\u00b6\\\2\u0458\u0459\7o\2\2\u0459\u045b\5\u00b8]\2\u045a"+
		"\u0458\3\2\2\2\u045a\u045b\3\2\2\2\u045b\u0468\3\2\2\2\u045c\u045d\7\61"+
		"\2\2\u045d\u045e\7o\2\2\u045e\u0468\5\u00b8]\2\u045f\u0468\5\u00c4c\2"+
		"\u0460\u0468\5\u00be`\2\u0461\u0468\5\u00bc_\2\u0462\u0468\7(\2\2\u0463"+
		"\u0464\7h\2\2\u0464\u0465\5\u0098M\2\u0465\u0466\7g\2\2\u0466\u0468\3"+
		"\2\2\2\u0467\u0457\3\2\2\2\u0467\u045c\3\2\2\2\u0467\u045f\3\2\2\2\u0467"+
		"\u0460\3\2\2\2\u0467\u0461\3\2\2\2\u0467\u0462\3\2\2\2\u0467\u0463\3\2"+
		"\2\2\u0468\u00b5\3\2\2\2\u0469\u046e\5F$\2\u046a\u046b\7b\2\2\u046b\u046d"+
		"\5F$\2\u046c\u046a\3\2\2\2\u046d\u0470\3\2\2\2\u046e\u046c\3\2\2\2\u046e"+
		"\u046f\3\2\2\2\u046f\u0473\3\2\2\2\u0470\u046e\3\2\2\2\u0471\u0472\7b"+
		"\2\2\u0472\u0474\5H%\2\u0473\u0471\3\2\2\2\u0473\u0474\3\2\2\2\u0474\u00b7"+
		"\3\2\2\2\u0475\u0476\7\n\2\2\u0476\u0477\7h\2\2\u0477\u0478\5\u0098M\2"+
		"\u0478\u0479\7g\2\2\u0479\u047f\3\2\2\2\u047a\u047f\5\u00dep\2\u047b\u047f"+
		"\7;\2\2\u047c\u047f\7\'\2\2\u047d\u047f\5\u00eav\2\u047e\u0475\3\2\2\2"+
		"\u047e\u047a\3\2\2\2\u047e\u047b\3\2\2\2\u047e\u047c\3\2\2\2\u047e\u047d"+
		"\3\2\2\2\u047f\u00b9\3\2\2\2\u0480\u0489\7h\2\2\u0481\u0486\5\u00caf\2"+
		"\u0482\u0483\7c\2\2\u0483\u0485\5\u00caf\2\u0484\u0482\3\2\2\2\u0485\u0488"+
		"\3\2\2\2\u0486\u0484\3\2\2\2\u0486\u0487\3\2\2\2\u0487\u048a\3\2\2\2\u0488"+
		"\u0486\3\2\2\2\u0489\u0481\3\2\2\2\u0489\u048a\3\2\2\2\u048a\u048b\3\2"+
		"\2\2\u048b\u048c\7g\2\2\u048c\u00bb\3\2\2\2\u048d\u0491\5\u00c2b\2\u048e"+
		"\u0491\5\u00c6d\2\u048f\u0491\5\u00c8e\2\u0490\u048d\3\2\2\2\u0490\u048e"+
		"\3\2\2\2\u0490\u048f\3\2\2\2\u0491\u00bd\3\2\2\2\u0492\u0493\5\u00c0a"+
		"\2\u0493\u00bf\3\2\2\2\u0494\u0495\t\f\2\2\u0495\u00c1\3\2\2\2\u0496\u0497"+
		"\t\r\2\2\u0497\u00c3\3\2\2\2\u0498\u0499\7]\2\2\u0499\u00c5\3\2\2\2\u049a"+
		"\u049b\7^\2\2\u049b\u049c\7b\2\2\u049c\u049d\7<\2\2\u049d\u049e\7h\2\2"+
		"\u049e\u049f\5\u0098M\2\u049f\u04a0\7g\2\2\u04a0\u00c7\3\2\2\2\u04a1\u04aa"+
		"\78\2\2\u04a2\u04a6\79\2\2\u04a3\u04a4\7h\2\2\u04a4\u04a5\7]\2\2\u04a5"+
		"\u04a7\7g\2\2\u04a6\u04a3\3\2\2\2\u04a6\u04a7\3\2\2\2\u04a7\u04aa\3\2"+
		"\2\2\u04a8\u04aa\7:\2\2\u04a9\u04a1\3\2\2\2\u04a9\u04a2\3\2\2\2\u04a9"+
		"\u04a8\3\2\2\2\u04aa\u00c9\3\2\2\2\u04ab\u04ac\7^\2\2\u04ac\u04ae\7s\2"+
		"\2\u04ad\u04ab\3\2\2\2\u04ad\u04ae\3\2\2\2\u04ae\u04af\3\2\2\2\u04af\u04b0"+
		"\5\u0098M\2\u04b0\u00cb\3\2\2\2\u04b1\u04b2\5\u0098M\2\u04b2\u00cd\3\2"+
		"\2\2\u04b3\u04b6\7\r\2\2\u04b4\u04b5\7*\2\2\u04b5\u04b7\5\u00e8u\2\u04b6"+
		"\u04b4\3\2\2\2\u04b6\u04b7\3\2\2\2\u04b7\u04b8\3\2\2\2\u04b8\u04bb\7+"+
		"\2\2\u04b9\u04ba\7^\2\2\u04ba\u04bc\7b\2\2\u04bb\u04b9\3\2\2\2\u04bb\u04bc"+
		"\3\2\2\2\u04bc\u04bd\3\2\2\2\u04bd\u04bf\7^\2\2\u04be\u04c0\5\u00d6l\2"+
		"\u04bf\u04be\3\2\2\2\u04bf\u04c0\3\2\2\2\u04c0\u04c1\3\2\2\2\u04c1\u04c3"+
		"\t\4\2\2\u04c2\u04c4\5\20\t\2\u04c3\u04c2\3\2\2\2\u04c3\u04c4\3\2\2\2"+
		"\u04c4\u04c5\3\2\2\2\u04c5\u04c7\7D\2\2\u04c6\u04c8\7^\2\2\u04c7\u04c6"+
		"\3\2\2\2\u04c7\u04c8\3\2\2\2\u04c8\u04c9\3\2\2\2\u04c9\u04ca\7_\2\2\u04ca"+
		"\u00cf\3\2\2\2\u04cb\u04ce\7\r\2\2\u04cc\u04cd\7*\2\2\u04cd\u04cf\5\u00e8"+
		"u\2\u04ce\u04cc\3\2\2\2\u04ce\u04cf\3\2\2\2\u04cf\u04d0\3\2\2\2\u04d0"+
		"\u04d1\7+\2\2\u04d1\u04d4\7\b\2\2\u04d2\u04d3\7^\2\2\u04d3\u04d5\7b\2"+
		"\2\u04d4\u04d2\3\2\2\2\u04d4\u04d5\3\2\2\2\u04d5\u04d6\3\2\2\2\u04d6\u04d7"+
		"\7^\2\2\u04d7\u04d9\t\4\2\2\u04d8\u04da\5\20\t\2\u04d9\u04d8\3\2\2\2\u04d9"+
		"\u04da\3\2\2\2\u04da\u04e0\3\2\2\2\u04db\u04e1\5<\37\2\u04dc\u04de\7D"+
		"\2\2\u04dd\u04df\7^\2\2\u04de\u04dd\3\2\2\2\u04de\u04df\3\2\2\2\u04df"+
		"\u04e1\3\2\2\2\u04e0\u04db\3\2\2\2\u04e0\u04dc\3\2\2\2\u04e1\u04e2\3\2"+
		"\2\2\u04e2\u04e3\7_\2\2\u04e3\u00d1\3\2\2\2\u04e4\u04e7\7\r\2\2\u04e5"+
		"\u04e6\7*\2\2\u04e6\u04e8\5\u00e8u\2\u04e7\u04e5\3\2\2\2\u04e7\u04e8\3"+
		"\2\2\2\u04e8\u04e9\3\2\2\2\u04e9\u04ec\7Q\2\2\u04ea\u04eb\7^\2\2\u04eb"+
		"\u04ed\7b\2\2\u04ec\u04ea\3\2\2\2\u04ec\u04ed\3\2\2\2\u04ed\u04ee\3\2"+
		"\2\2\u04ee\u04fa\7^\2\2\u04ef\u04f0\7h\2\2\u04f0\u04f5\5\16\b\2\u04f1"+
		"\u04f2\7c\2\2\u04f2\u04f4\5\16\b\2\u04f3\u04f1\3\2\2\2\u04f4\u04f7\3\2"+
		"\2\2\u04f5\u04f3\3\2\2\2\u04f5\u04f6\3\2\2\2\u04f6\u04f8\3\2\2\2\u04f7"+
		"\u04f5\3\2\2\2\u04f8\u04f9\7g\2\2\u04f9\u04fb\3\2\2\2\u04fa\u04ef\3\2"+
		"\2\2\u04fa\u04fb\3\2\2\2\u04fb\u04fd\3\2\2\2\u04fc\u04fe\5\u00d6l\2\u04fd"+
		"\u04fc\3\2\2\2\u04fd\u04fe\3\2\2\2\u04fe\u04ff\3\2\2\2\u04ff\u0506\t\4"+
		"\2\2\u0500\u0502\5\20\t\2\u0501\u0500\3\2\2\2\u0501\u0502\3\2\2\2\u0502"+
		"\u0503\3\2\2\2\u0503\u0507\5<\37\2\u0504\u0507\5\u00d8m\2\u0505\u0507"+
		"\7\26\2\2\u0506\u0501\3\2\2\2\u0506\u0504\3\2\2\2\u0506\u0505\3\2\2\2"+
		"\u0507\u0508\3\2\2\2\u0508\u0509\7_\2\2\u0509\u00d3\3\2\2\2\u050a\u050d"+
		"\7\r\2\2\u050b\u050c\7*\2\2\u050c\u050e\5\u00e8u\2\u050d\u050b\3\2\2\2"+
		"\u050d\u050e\3\2\2\2\u050e\u050f\3\2\2\2\u050f\u0512\7H\2\2\u0510\u0511"+
		"\7^\2\2\u0511\u0513\7b\2\2\u0512\u0510\3\2\2\2\u0512\u0513\3\2\2\2\u0513"+
		"\u0514\3\2\2\2\u0514\u0520\7^\2\2\u0515\u0516\7h\2\2\u0516\u051b\5\16"+
		"\b\2\u0517\u0518\7c\2\2\u0518\u051a\5\16\b\2\u0519\u0517\3\2\2\2\u051a"+
		"\u051d\3\2\2\2\u051b\u0519\3\2\2\2\u051b\u051c\3\2\2\2\u051c\u051e\3\2"+
		"\2\2\u051d\u051b\3\2\2\2\u051e\u051f\7g\2\2\u051f\u0521\3\2\2\2\u0520"+
		"\u0515\3\2\2\2\u0520\u0521\3\2\2\2\u0521\u0522\3\2\2\2\u0522\u0523\7U"+
		"\2\2\u0523\u0525\5.\30\2\u0524\u0526\5\u00d6l\2\u0525\u0524\3\2\2\2\u0525"+
		"\u0526\3\2\2\2\u0526\u0527\3\2\2\2\u0527\u052e\t\4\2\2\u0528\u052a\5\20"+
		"\t\2\u0529\u0528\3\2\2\2\u0529\u052a\3\2\2\2\u052a\u052b\3\2\2\2\u052b"+
		"\u052f\5<\37\2\u052c\u052f\5\u00d8m\2\u052d\u052f\7\26\2\2\u052e\u0529"+
		"\3\2\2\2\u052e\u052c\3\2\2\2\u052e\u052d\3\2\2\2\u052f\u0530\3\2\2\2\u0530"+
		"\u0531\7_\2\2\u0531\u00d5\3\2\2\2\u0532\u0533\7\6\2\2\u0533\u0534\t\16"+
		"\2\2\u0534\u00d7\3\2\2\2\u0535\u0536\7\"\2\2\u0536\u0537\5\u0090I\2\u0537"+
		"\u00d9\3\2\2\2\u0538\u0539\6n\2\2\u0539\u053a\7^\2\2\u053a\u00db\3\2\2"+
		"\2\u053b\u053c\6o\3\2\u053c\u053d\7^\2\2\u053d\u00dd\3\2\2\2\u053e\u053f"+
		"\6p\4\2\u053f\u0540\7^\2\2\u0540\u00df\3\2\2\2\u0541\u0542\6q\5\2\u0542"+
		"\u0543\7^\2\2\u0543\u00e1\3\2\2\2\u0544\u0545\6r\6\2\u0545\u0546\7^\2"+
		"\2\u0546\u00e3\3\2\2\2\u0547\u0548\6s\7\2\u0548\u0549\7^\2\2\u0549\u00e5"+
		"\3\2\2\2\u054a\u054b\6t\b\2\u054b\u054c\7^\2\2\u054c\u00e7\3\2\2\2\u054d"+
		"\u054e\6u\t\2\u054e\u054f\7^\2\2\u054f\u00e9\3\2\2\2\u0550\u0551\6v\n"+
		"\2\u0551\u0552\7^\2\2\u0552\u00eb\3\2\2\2\u0553\u0554\6w\13\2\u0554\u0555"+
		"\7^\2\2\u0555\u00ed\3\2\2\2\u0556\u0557\6x\f\2\u0557\u0558\7^\2\2\u0558"+
		"\u00ef\3\2\2\2\u0559\u055a\6y\r\2\u055a\u055b\7^\2\2\u055b\u00f1\3\2\2"+
		"\2\u055c\u055d\6z\16\2\u055d\u055e\7^\2\2\u055e\u00f3\3\2\2\2\u00ad\u00f7"+
		"\u00fa\u00fe\u0105\u010b\u0110\u0115\u0120\u012a\u012d\u012f\u0134\u014b"+
		"\u014d\u0152\u015a\u0160\u0164\u016b\u0179\u0181\u018a\u0193\u0197\u019b"+
		"\u019f\u01a2\u01ab\u01b2\u01b7\u01bf\u01c2\u01c7\u01cf\u01d7\u01d9\u01df"+
		"\u01e4\u01e7\u01ed\u01f4\u01f9\u0200\u0203\u020a\u0217\u0219\u0220\u0222"+
		"\u0226\u022e\u0232\u023a\u023f\u0255\u025c\u0264\u0267\u0270\u0273\u0276"+
		"\u027b\u027e\u0286\u028b\u028f\u0299\u029d\u02a3\u02a8\u02ae\u02b2\u02b6"+
		"\u02bd\u02c0\u02c4\u02c7\u02cb\u02cf\u02d7\u02d9\u02e1\u02ec\u02f1\u02f6"+
		"\u02fb\u0300\u0303\u0308\u0310\u0318\u031d\u0327\u0335\u033b\u0348\u0352"+
		"\u0356\u035f\u0361\u036e\u0372\u0376\u0381\u0384\u0388\u038c\u0395\u039a"+
		"\u039f\u03ab\u03af\u03bd\u03cf\u03d9\u03de\u03e3\u03ea\u03f5\u03fd\u0401"+
		"\u0408\u040d\u0410\u0414\u0418\u041c\u0423\u0427\u0430\u0435\u043e\u0445"+
		"\u044a\u044e\u0455\u045a\u0467\u046e\u0473\u047e\u0486\u0489\u0490\u04a6"+
		"\u04a9\u04ad\u04b6\u04bb\u04bf\u04c3\u04c7\u04ce\u04d4\u04d9\u04de\u04e0"+
		"\u04e7\u04ec\u04f5\u04fa\u04fd\u0501\u0506\u050d\u0512\u051b\u0520\u0525"+
		"\u0529\u052e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}