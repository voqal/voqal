package dev.voqal.assistant.tool.text

import com.intellij.lang.Language
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.assistant.processing.CodeExtractor
import dev.voqal.assistant.tool.code.CreateClassTool.Companion.getFileExtensionForLanguage
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class EditTextToolTest : JBTest() {

    fun `test edit visible range push offsets`() {
        val responseCode = File("src/test/resources/edit/rename-log-to-logger.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit/RenameTest.kt").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("RenameTest.kt", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = TextRange(19, 742)
            val testHighlighter = testEditor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor)
            testContext.verify {
                assertEquals(5, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertTrue(editHighlighters[0].let { it.startOffset == 52 && it.endOffset == 58 })
                assertTrue(editHighlighters[1].let { it.startOffset == 369 && it.endOffset == 375 })
                assertTrue(editHighlighters[2].let { it.startOffset == 570 && it.endOffset == 576 })
                assertTrue(editHighlighters[3].let { it.startOffset == 666 && it.endOffset == 672 })
                assertTrue(editHighlighters[4].let { it.startOffset == 734 && it.endOffset == 740 })
            }
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            testEditor.document.text
                .replace("val log = ", "val logger = ")
                .replace("log.", "logger.")
        )
    }

    fun `test edit empty file`() {
        val content = "package test;\n\npublic class Test {\n}"
        val testDocument = EditorFactory.getInstance().createDocument("")
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)

        val newHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            runBlocking {
                EditTextTool().doDocumentEdits(project, content, testEditor)
            }
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(1, newHighlighters.size)
        assertEquals(0, newHighlighters[0].startOffset)
        assertEquals(content.length, newHighlighters[0].endOffset)
        assertEquals(testEditor.document.text, content)
    }

    fun `test code edit`() {
        val json =
            JsonObject("{ \"content\": \"public class User {\\n    private String username;\\n    private String password;\\n    private String email;\\n}\" }")
        val content = json.getString("content")
        val testDocument = EditorFactory.getInstance().createDocument("")
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)

        val newHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            runBlocking {
                EditTextTool().doDocumentEdits(project, content, testEditor)
            }
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(1, newHighlighters.size)
        assertEquals(0, newHighlighters[0].startOffset)
        assertEquals(content.length, newHighlighters[0].endOffset)
    }

    fun `test replace visible range edit2`() {
        val lang = Language.findLanguageByID(System.getenv("VQL_LANG") ?: "JAVA")!!
        val fileExt = getFileExtensionForLanguage(lang)
        val largeCodeFile = File("src/test/resources/$fileExt/large/PLSQLParser.$fileExt")
        val largeCode = largeCodeFile.readText().replace("\r\n", "\n")
        val virtualFile = LightVirtualFile(largeCodeFile.name, lang, largeCode)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
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

            	// New field
            	private String newField;

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
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, 7093)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(1, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)

        val codeBlock = CodeExtractor.extractCodeBlock(responseCode)
        assertTrue(editor.document.text.contains(codeBlock))

        EditorFactory.getInstance().releaseEditor(editor)
    }

    fun `test python preserve indenting`() {
        val codeFile = File("src/test/resources/py/AddMethod.py")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            def add(self, x, y):
                return x + y #test
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(17, 62)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(1, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        val codeBlock = CodeExtractor.extractCodeBlock(responseCode)
        val indentedCodeBlock = codeBlock.lines().joinToString("\n") { "    $it" }
        assertTrue(editor.document.text.contains(indentedCodeBlock))
    }

    fun `test python preserve tab indenting`() {
        val codeFile = File("src/test/resources/py/AddMethod-Tabs.py")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            def add(self, x, y):
            	return x + y #test
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(17, 53)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(1, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        val codeBlock = CodeExtractor.extractCodeBlock(responseCode)
        val indentedCodeBlock = codeBlock.lines().joinToString("\n") { "\t$it" }
        assertTrue(editor.document.text.contains(indentedCodeBlock))
    }

    fun `test java fix incorrect indenting`() {
        val codeFile = File("src/test/resources/java/large/PLSQLParser.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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

            /**
             *
             */
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
        """.trimIndent()

        //fixed indented code, replace leading 4 spaces with tab
        var fixedResponseCode = responseCode.lines()
            .joinToString("\n") {
                if (it.isBlank()) {
                    it
                } else {
                    "\t$it"
                }
            }
            .replace("    ", "\t")
        //remove indent from first and last line
        fixedResponseCode = fixedResponseCode.replaceFirst("\t", "")
        val lastTab = fixedResponseCode.lastIndexOf("\t")
        fixedResponseCode = fixedResponseCode.substring(0, lastTab) + fixedResponseCode.substring(lastTab + 1)

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(163898, 170177)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(9, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        //todo: why -1?
        assertTrue(editor.document.text.contains(fixedResponseCode.substring(0, fixedResponseCode.length - 1)))
    }

    fun `test kotlin delete function`() {
        val codeText = """
            enum class TileType {
                GRASS, WATER, MOUNTAIN, ROAD, LAVA
            }

            class Tile(
                val type: TileType,
                var isWalkable: Boolean,
                var hasItem: Boolean = false,
                val x: Int,
                val y: Int
            ) {
                fun printTileInfo() {
                    println("Tile(type: ${'$'}type, walkable: ${'$'}isWalkable, has item: ${'$'}hasItem, coordinates: (${'$'}x, ${'$'}y))")
                }

                fun interact() {
                    if (hasItem) {
                        println("You found an item at (${'$'}x, ${'$'}y)!")
                        hasItem = false // Item is picked up
                    } else {
                        println("There's nothing here at (${'$'}x, ${'$'}y).")
                    }
                }
            }

            fun main() {
                val grassTile = Tile(TileType.GRASS, isWalkable = true, x = 1, y = 1)
                val waterTile = Tile(TileType.WATER, isWalkable = false, hasItem = true, x = 2, y = 3)

                grassTile.printTileInfo()
                waterTile.printTileInfo()

                grassTile.interact()
                waterTile.interact()
                waterTile.interact() // Interact again to show item is already picked up
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("Tile.kotlin", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            enum class TileType {
                GRASS, WATER, MOUNTAIN, ROAD, LAVA
            }

            class Tile(
                val type: TileType,
                var isWalkable: Boolean,
                var hasItem: Boolean = false,
                val x: Int,
                val y: Int
            ) {
                fun printTileInfo() {
                    println("Tile(type: ${'$'}type, walkable: ${'$'}isWalkable, has item: ${'$'}hasItem, coordinates: (${'$'}x, ${'$'}y))")
                }

                fun interact() {
                    if (hasItem) {
                        println("You found an item at (${'$'}x, ${'$'}y)!")
                        hasItem = false // Item is picked up
                    } else {
                        println("There's nothing here at (${'$'}x, ${'$'}y).")
                    }
                }
            }
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(0, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        assertFalse(editor.document.text.contains("fun main()"))
    }

    fun `test java delete import`() {
        val codeText = """
            import java.util.Scanner;

            public class Main {
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                    
                    System.out.println("Hello, World!");
                    
                    scanner.close();
                }
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("Main.java", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            public class Main {
            }
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            val rangeHighlighters = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(0, rangeHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        assertFalse(editor.document.text.contains("import java.util.Scanner;"))
    }
}
