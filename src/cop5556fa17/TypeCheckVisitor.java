package cop5556fa17;

import java.util.HashMap;
import java.util.Map;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import java.net.URL;
public class TypeCheckVisitor implements ASTVisitor {
	
		public HashMap<String, Declaration> symbolTable;
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
		public TypeCheckVisitor() {
			// TODO Auto-generated constructor stub
			symbolTable = new HashMap<String, Declaration>();
		}

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		if(lookupType(declaration_Variable.name)!=null)
			throw new SemanticException(declaration_Variable.firstToken,"Exception in declaration variable. Duplicate name");
		declaration_Variable.typeKind =TypeUtils.getType(declaration_Variable.type);
		
		if(declaration_Variable.e!=null)
		{	
			declaration_Variable.e.visit(this, arg);
			if(declaration_Variable.typeKind != declaration_Variable.e.typeKind)
				throw new SemanticException(declaration_Variable.firstToken,"Exception in declaration variable. type should be same as e type");
		}
		insert(declaration_Variable.name, declaration_Variable);
		
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		Type t = Type.NONE;
		if(expression_Binary.op==Kind.OP_EQ || expression_Binary.op==Kind.OP_NEQ)
			t=Type.BOOLEAN;
		else if((expression_Binary.op==Kind.OP_GE || expression_Binary.op==Kind.OP_GT 
				|| expression_Binary.op==Kind.OP_LT || expression_Binary.op==Kind.OP_LE)
				&& expression_Binary.e0.typeKind==Type.INTEGER)
			t=Type.BOOLEAN;
		else if((expression_Binary.op==Kind.OP_AND || expression_Binary.op==Kind.OP_OR)
				&& (expression_Binary.e0.typeKind==Type.INTEGER|| expression_Binary.e0.typeKind == Type.BOOLEAN))
			t=expression_Binary.e0.typeKind;
		else if((expression_Binary.op==Kind.OP_DIV || expression_Binary.op==Kind.OP_MINUS 
				|| expression_Binary.op==Kind.OP_MOD || expression_Binary.op==Kind.OP_PLUS
				|| expression_Binary.op==Kind.OP_POWER || expression_Binary.op==Kind.OP_TIMES)
				&& expression_Binary.e0.typeKind==Type.INTEGER)
			t=Type.INTEGER;
		expression_Binary.typeKind = t;

		if(!(expression_Binary.e0.typeKind==expression_Binary.e1.typeKind && expression_Binary.typeKind!=Type.NONE))
			throw new SemanticException(expression_Binary.firstToken, "Exception in Expression Binary! e1 and e2 should be same type.");
		return expression_Binary;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_Unary.e.visit(this, arg);
		if(expression_Unary.op == Kind.OP_EXCL &&(expression_Unary.e.typeKind==Type.BOOLEAN||expression_Unary.e.typeKind==Type.INTEGER))
			expression_Unary.typeKind=expression_Unary.e.typeKind;
		else if(expression_Unary.e.typeKind == Type.INTEGER &&(expression_Unary.op==Kind.OP_PLUS||expression_Unary.op==Kind.OP_MINUS))
			expression_Unary.typeKind = Type.INTEGER;
		else
			expression_Unary.typeKind = Type.NONE;
		
		if(expression_Unary.typeKind==Type.NONE)
			throw new SemanticException(expression_Unary.firstToken, "Exception in unary expression! Type should not be blank");
		return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(index.e0.typeKind!=Type.INTEGER || index.e1.typeKind!=Type.INTEGER)
			throw new SemanticException(index.firstToken, "Exception in index! e0 and e1 must have type INTEGER");
		index.setCartesian(!(index.e0.firstToken.getText().equals("r")&&index.e1.firstToken.getText().equals("a")));
		return index;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		if(expression_PixelSelector.index!=null)
			expression_PixelSelector.index.visit(this, arg);
		Declaration dec = lookupType(expression_PixelSelector.name);
		Type type;
		if(dec==null)
			type=Type.NONE;
		else type= dec.typeKind;
		if(type==Type.IMAGE){
			expression_PixelSelector.typeKind = Type.INTEGER;
		}
		else if(expression_PixelSelector.index==null)
			expression_PixelSelector.typeKind = type;
		else
			expression_PixelSelector.typeKind=Type.NONE;
		if(expression_PixelSelector.typeKind==Type.NONE)
			throw new SemanticException(expression_PixelSelector.firstToken, "Exception in PixelSelector! Type Should not be blank");
		return expression_PixelSelector;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_Conditional.condition.visit(this, arg);
		expression_Conditional.trueExpression.visit(this, arg);
		expression_Conditional.falseExpression.visit(this, arg);
		if(expression_Conditional.condition.typeKind != Type.BOOLEAN || !(expression_Conditional.trueExpression.typeKind == expression_Conditional.falseExpression.typeKind))
			throw new SemanticException(expression_Conditional.firstToken, "Exception in Expression_Conditional! Expression Condition type should be Boolean");
		expression_Conditional.typeKind = expression_Conditional.trueExpression.typeKind;
		
		return expression_Conditional;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(lookupType(declaration_Image.name)!=null)
			throw new SemanticException(declaration_Image.firstToken,"Error. Duplicate name");
		insert(declaration_Image.name, declaration_Image);
		declaration_Image.typeKind = Type.IMAGE;
		
		if(declaration_Image.xSize!=null)
		{	declaration_Image.xSize.visit(this, arg);
			if(declaration_Image.ySize!=null)
				declaration_Image.ySize.visit(this, arg);
			else
				throw new SemanticException(declaration_Image.firstToken, "Error. No ysize after xsize");
			if(declaration_Image.xSize.typeKind!=Type.INTEGER || declaration_Image.ySize.typeKind!=Type.INTEGER){
				throw new SemanticException(declaration_Image.firstToken, "Error. xsize and ysize type should be integer");
			}
		}
		if(declaration_Image.source!=null)
			declaration_Image.source.visit(this, arg);
		return declaration_Image;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		try{
			new URL(source_StringLiteral.fileOrUrl);
			source_StringLiteral.typeKind = Type.URL;
		}
		catch(Exception e){
			source_StringLiteral.typeKind = Type.FILE;
		}
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		source_CommandLineParam.paramNum.visit(this, arg);
		source_CommandLineParam.typeKind = Type.NONE;
		if(source_CommandLineParam.paramNum.typeKind!=Type.INTEGER)
			throw new SemanticException(source_CommandLineParam.firstToken, "Error in Source CommanLine! Type should be INTEGER");
		return source_CommandLineParam;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Declaration dec = lookupType(source_Ident.name);
		if(dec==null)
			throw new SemanticException(source_Ident.firstToken, "Error in Source Ident! Type Should be FILE or URL");
		source_Ident.typeKind = dec.typeKind;
		if(source_Ident.typeKind != Type.FILE && source_Ident.typeKind!=Type.URL)
			throw new SemanticException(source_Ident.firstToken, "Error in Source Ident! Type Should be FILE or URL");
		return source_Ident;
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		if(lookupType(declaration_SourceSink.name)!=null)
			throw new SemanticException(declaration_SourceSink.firstToken,"Error in source sink declaration. Duplicate name");
		declaration_SourceSink.source.visit(this, arg);
		declaration_SourceSink.typeKind = TypeUtils.getType(declaration_SourceSink.firstToken);
		System.out.println(declaration_SourceSink.source.typeKind +","+declaration_SourceSink.typeKind);
		if(!(declaration_SourceSink.source.typeKind==Type.NONE || declaration_SourceSink.source.typeKind==declaration_SourceSink.typeKind))
			throw new SemanticException(declaration_SourceSink.firstToken, "Error in source sink declaration. typeKind should be equal to source's typeKind");
		insert(declaration_SourceSink.name, declaration_SourceSink);
		return declaration_SourceSink.name;
		// TODO Auto-generated method stub
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_IntLit.typeKind = Type.INTEGER;
		return expression_IntLit;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.arg.typeKind != Type.INTEGER)
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "Error in functionAppWithExprArg! Type should be INTEGER");
		expression_FunctionAppWithExprArg.typeKind = Type.INTEGER;
		return expression_FunctionAppWithExprArg;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		expression_FunctionAppWithIndexArg.typeKind = Type.INTEGER;
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_PredefinedName.typeKind = Type.INTEGER;
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		statement_Out.sink.visit(this, arg);
		Declaration dec = lookupType(statement_Out.name); 
		if(dec==null){
			throw new SemanticException(statement_Out.firstToken, "Exception! Declaration cannot be null in Statement Out");
			
		}
		else if(!(((dec.typeKind == Type.INTEGER || dec.typeKind == Type.BOOLEAN)&&statement_Out.sink.typeKind==Type.SCREEN)||
				(dec.typeKind == Type.IMAGE && (statement_Out.sink.typeKind==Type.SCREEN || statement_Out.sink.typeKind==Type.FILE))))
			throw new SemanticException(statement_Out.firstToken, "Error! Name.Declaration.Type not equal to Source.type");
		statement_Out.setDec(dec);
		return statement_Out;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Declaration dec = lookupType(statement_In.name); 
		statement_In.source.visit(this, arg);
		statement_In.setDec(dec);
		return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		statement_Assign.lhs.visit(this, arg);
		statement_Assign.e.visit(this, arg);
		if(statement_Assign.lhs.typeKind != statement_Assign.e.typeKind
			&& (statement_Assign.lhs.typeKind != Type.IMAGE || statement_Assign.e.typeKind != Type.INTEGER)	)
			throw new SemanticException(statement_Assign.firstToken, "Exception! Lhs type and expression type not same in Statement Assign");
		statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(lhs.index!=null)
			lhs.index.visit(this, arg);
		lhs.dec=lookupType(lhs.name);
		if(lhs.dec==null)
			lhs.typeKind = Type.NONE;
		else
			lhs.typeKind = lhs.dec.typeKind;
		if(lhs.index!=null)lhs.isCartesian = lhs.index.isCartesian();
		
		return lhs;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		sink_SCREEN.typeKind = Type.SCREEN;
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Declaration dec = lookupType(sink_Ident.name);
		if(dec==null){
			sink_Ident.typeKind = Type.NONE;
		}
		else
			sink_Ident.typeKind = dec.typeKind;
		if(sink_Ident.typeKind!=Type.FILE)
			throw new SemanticException(sink_Ident.firstToken, "Exception in Sink Ident! Type Should be FILE");
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		expression_BooleanLit.typeKind = Type.BOOLEAN;
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Declaration dec = lookupType(expression_Ident.name);
		if(dec==null)
			expression_Ident.typeKind=Type.NONE;
		else
			expression_Ident.typeKind = dec.typeKind;
		return expression_Ident;
	}
	
	public Declaration lookupType(String name){
		if(symbolTable.containsKey(name))
		{
			return symbolTable.get(name);
		}
		else
			return null;
	}
	
	public void insert(String name, Declaration dec){
		if(!symbolTable.containsKey(name)){
			symbolTable.put(name, dec);
		}
	}
	
	
}
