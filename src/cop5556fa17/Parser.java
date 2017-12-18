package cop5556fa17;



import java.util.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.*;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

    @SuppressWarnings("serial")
    public class SyntaxException extends Exception {
        Token t;

        public SyntaxException(Token t, String message) {
            super(message);
            this.t = t;
        }

    }


    Scanner scanner;
    Token t;
    Parser(Scanner scanner) {
        this.scanner = scanner;
        t = scanner.nextToken();
    }

    /**
    * Main method called by compiler to parser input.
    * Checks for EOF
    * 
    * @throws SyntaxException
    */
    public Program parse() throws SyntaxException {
        Program prog = program();
        matchEOF();
        return prog;
    }
    
    
     public Program program() throws SyntaxException {
        //TODO  implement this
        if(t.kind==EOF)
            throw new SyntaxException(t,"Token List is Empty");
        ArrayList<ASTNode> list = new ArrayList<ASTNode>();
        Token ft = t;
        match(IDENTIFIER);
        while(t.kind!=(Kind.EOF))
        {
            ASTNode node=null;
            if(t.kind==(KW_int)||t.kind==(Kind.KW_boolean)||t.kind==(KW_url)||t.kind==(KW_file)||t.kind==(KW_image))
            {
                node = declaration();
                list.add(node);
            }
            else if(t.kind==(IDENTIFIER))
            {
                node = statement();
                list.add(node);
            }
            else throw new SyntaxException(t,"Error in Program. Token:"+t.kind);
            match(SEMI);
        }
        Program p = new Program(ft,ft, list);
        return p;
    }

    Declaration declaration() throws SyntaxException{
        // TODO Auto-generated method stub
        Declaration declare = null;
        if(t.kind==(KW_int)||t.kind==(KW_boolean)){
            declare=variableDeclaration();
        }
        else if(t.kind==(KW_url)||t.kind==(KW_file))
        {
             Token temp=t;
             consume();
             Token name=t;
             match(IDENTIFIER);
             match(OP_ASSIGN);
             Declaration_SourceSink ssd=new Declaration_SourceSink(temp,temp,name,source());
             return ssd;
        }
        
        else if(t.kind==(KW_image)){
            declare=imageDeclaration();
        }
        else throw new SyntaxException(t, "Error in declaration. Token:"+t.kind);
        return declare;
    }
    
    Declaration_Variable variableDeclaration() throws SyntaxException {
        Token temp=t;
        consume();
        Token name=t;
        match(IDENTIFIER);
        if(t.kind==(OP_ASSIGN)){
            consume();
            return new Declaration_Variable(temp,temp,name,expression());
        }
        else return new Declaration_Variable(temp,temp,name,null);
    }

    Source source() throws SyntaxException {
        //TODO implement this.
        Token ft=t;
        switch(t.kind) {
	        case STRING_LITERAL:
	        
	            consume();
	            return new Source_StringLiteral(ft,ft.getText());
	        
	        case IDENTIFIER:
	            
	            consume();
	            return new Source_Ident(ft,ft);
	        
	        case OP_AT:
	            consume();
	            Expression e=expression();
	            return new Source_CommandLineParam(ft,e);
	        
	        default:
	            throw new SyntaxException(t,"Error in source. Token:"+t.kind);
        }
    }
    
    Declaration_Image imageDeclaration() throws SyntaxException {
        //TODO implement this.
        Token ft=t;
        Token name = t;
        consume();
        Expression expr1=null;
        Expression expr2=null;
        Source src=null;
        if(t.kind==(LSQUARE)){
                consume();
                expr1=expression();
                match(COMMA);
                expr2=expression();
                match(Kind.RSQUARE);
            }
            if(t.kind==(IDENTIFIER)){
                name = t;
                consume();
                if(t.kind==(Kind.OP_LARROW)){
                    consume();
                    src=source();
                }
            }
            else
                throw new SyntaxException(t, "Error in image Declaration. Token:"+t.kind);
            return new Declaration_Image(ft,expr1,expr2,name,src);
    }
    
    Statement statement() throws SyntaxException{
        // TODO Auto-generated method stub
        Token ft=t;
        match(IDENTIFIER);
        //ArrayList<ASTNod>
        Expression e=null;
        LHS lhs=null;
        switch(t.kind) {
	        case LSQUARE:
	            consume();
	             match(LSQUARE);
	             Index index=null;
	             if(t.kind==(KW_x)){
	                 index=xySelector();
	             }
	             else if(t.kind==(KW_r)){
	                 index=raSelector();
	             }
	             else throw new SyntaxException(t, "Error in lhsSelector. Token:"+t.kind);
	             match(RSQUARE);
	             match(RSQUARE);
	             lhs =  new LHS(ft, ft, index);
	             match(OP_ASSIGN);
	             e=expression();
	             return new Statement_Assign(ft,lhs,e);
	        
	        case OP_ASSIGN:
	            lhs=new LHS(ft,ft,null);
	            consume();
	            e=expression();
	            return new Statement_Assign(ft, lhs, e);
	        
	        case OP_RARROW:
	            consume();
	            Sink sink=sink();
	            consume();
	            return new Statement_Out(ft,ft,sink);
	        
	        case OP_LARROW:
	            consume();
	            Source src=source();
	            return new Statement_In(ft,ft,src);
	        
	        default:
	        	throw new SyntaxException(t,"Error in statement. Token:"+t.kind);
        }
    }

    
    Sink sink() throws SyntaxException{
        // TODO Auto-generated method stub
        Token ft=t;
        if(t.kind==(IDENTIFIER))
        {
            return new Sink_Ident(ft, ft);
        }
        else if(t.kind == KW_SCREEN){
            return new Sink_SCREEN(ft);
        }
        else
            throw new SyntaxException(t,"Error in Sink. Toke: "+t.kind); 
        
    }
  
   Expression expression() throws SyntaxException {
        //TODO implement this.
        Token firstToken = t;
        Expression e=null;
        e=orExpression();
        if(t.kind==(OP_Q))
        {
            consume();
            Expression trueExpression = expression();
            match(OP_COLON);
            Expression falseExpression = expression();
            return new Expression_Conditional(firstToken, e, trueExpression, falseExpression);
        }
        return e;
    }
    
    Expression orExpression() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=andExpression(); 
        while(t.kind==(OP_OR)){
            op=t;
            consume();
            expr1=andExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
        
    }
        
    Expression andExpression() throws SyntaxException{
        // TODO Auto-generated method stub
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=eqExpression();
        while(t.kind==(OP_AND)){
            op=t;
            consume();
            expr1=eqExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
        
    }
    
    Expression eqExpression() throws SyntaxException {
        // TODO Auto-generated method stub
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=relExpression();
        while(t.kind==(OP_EQ)||t.kind==(OP_NEQ)){
            op=t;
            consume();
            expr1=relExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
    }
    
    Expression relExpression() throws SyntaxException {
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=addExpression();
        while(t.kind==(OP_LT)||t.kind==(OP_GT)||t.kind==(OP_LE)||t.kind==(OP_GE)){
            op=t;
            consume();
            expr1=addExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
    }
    
    Expression addExpression() throws SyntaxException {
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=multExpression();
        while(t.kind==(OP_PLUS)||t.kind==(OP_MINUS)){
            op=t;
            consume();
            expr1=multExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
    }
        
    Expression multExpression() throws SyntaxException{
        Token ft=t;
        Token op=null;
        Expression expr1=null;
        Expression expr2=unaryExpression();
        while(t.kind==(OP_TIMES)||t.kind==(OP_DIV)||t.kind==(OP_MOD)){
            op=t;
            consume();
            expr1=unaryExpression();
            expr2 = new Expression_Binary(ft,expr2,op,expr1);
        }
        return expr2;
    }
    
    Expression unaryExpression() throws SyntaxException{
        Token ft=t;
        Token op=null;
        Expression expr2=null;
        Expression expr1=null;
        if(t.kind==(OP_PLUS) || t.kind==(OP_MINUS)){
            op=t;
            consume();
            expr1=unaryExpression();
            return new Expression_Unary(ft,op,expr1);
        }
        else {
            
            expr2=unaryExpressionNotPlusMinus();
            return expr2;
        }
    }
    
    Expression unaryExpressionNotPlusMinus() throws SyntaxException{
        Token ft=t;
        Token op=null;
        Expression e=null;
        if(t.kind==(IDENTIFIER)){
            op=t;
            Index index=null;
            Token name = t;
            consume();    
            if(t.kind==(LSQUARE)){
                consume();
                index=selector();
                match(RSQUARE);
                e =  new Expression_PixelSelector(ft,name,index);
            }
            if(e==null)e = new Expression_Ident(ft,name);
            return e;
        }
        
        else if(t.kind==(INTEGER_LITERAL)||t.kind==(LPAREN)||t.kind==(BOOLEAN_LITERAL)||t.kind==(KW_sin)||t.kind==(KW_cos)||t.kind==(KW_atan)||t.kind==(KW_abs)||t.kind==(KW_cart_x)||t.kind==(KW_cart_y)||t.kind==(KW_polar_a)||t.kind==(KW_polar_r))
        {
            op=t;
            e=primary();
            return e;
        }
        else if(t.kind==(KW_x)||t.kind==(KW_y)||t.kind==(KW_r)||t.kind==(KW_a)||t.kind==(KW_X)||t.kind==(KW_Y)||t.kind==(KW_Z)||t.kind==(KW_A)||t.kind==(KW_R)||t.kind==(KW_DEF_X)||t.kind==(KW_DEF_Y)){
            op=t;
            consume();
            e=new Expression_PredefinedName(ft,op.kind);
            return e;
        }
        else if(t.kind==(OP_EXCL)){
            op=t;
            consume();
            e=unaryExpression();
            return new Expression_Unary(ft, op, e);
        }
        else
            throw new SyntaxException(t,"Error in unaryExpressionNotPlusMinus. Token:"+t.kind);
        
    }
    
    Expression primary() throws SyntaxException{
        Token ft=t;
        if(t.kind==(INTEGER_LITERAL)){
            int val=0;
            try {
                val=Integer.parseInt(t.getText());
            }
            catch(Exception e) {
                System.out.println("Error. Number out of range");
            }
            consume();
            return new Expression_IntLit(ft,val);
        }
        else if(t.kind==(LPAREN)){
            consume();
            Expression e=expression();
            match(RPAREN);
            return e;
        }
        else if(t.kind==(BOOLEAN_LITERAL)){
            boolean val=false;
            if(t.getText().equals("true"))
                val=true;
            consume();
            return new Expression_BooleanLit(ft,val);
        }
        else if(t.kind==(KW_sin)||t.kind==(KW_cos)||t.kind==(KW_atan)||t.kind==(KW_abs)||t.kind==(KW_cart_x)||t.kind==(KW_cart_y)||t.kind==(KW_polar_a)||t.kind==(KW_polar_r))
        {
            Kind kind = t.kind;
            consume();
            if(t.kind==(LPAREN)){
                consume();
                Expression e=expression();
                match(RPAREN);
                return new Expression_FunctionAppWithExprArg(ft, kind, e);
            }
            else if(t.kind==(LSQUARE)){
                consume();
                Index ind = selector();
                match(RSQUARE);
                return new Expression_FunctionAppWithIndexArg(ft, kind, ind);
                
            }
            else throw new SyntaxException(t, "Error in functionApplication. Token: "+t.kind);
        }
        
        else throw new SyntaxException(t,"Error in primary. Token: "+t.kind);
    }
    
    
    Index xySelector() throws SyntaxException{
        Token ft=t;
        Expression_PredefinedName expr0=null;
        Expression_PredefinedName expr1=null;
        if(t.kind==(KW_x)){
            expr0=new Expression_PredefinedName(ft, t.kind);
            consume();
        }
        else{
            throw new SyntaxException(t,"Exception in xySelector");
        }
        match(COMMA);
        if(t.kind==(KW_y)){
            expr1=new Expression_PredefinedName(t, t.kind);
            consume();
        }
        else{
            throw new SyntaxException(t,"Exception in xySelector");
        }
        return new Index(ft, expr0, expr1);
    }
    
    Index raSelector() throws SyntaxException{
        Token ft = t;
        Expression_PredefinedName expr0=null, expr1 = null;
        if(t.kind==(KW_r)){
            expr0=new Expression_PredefinedName(ft, t.kind);
            consume();
        }
        else{
            throw new SyntaxException(t,"Exception in raSelector");
        }
        match(COMMA);
        if(t.kind==(KW_a)){
            expr1=new Expression_PredefinedName(t, t.kind);
            consume();
        }
        else{
            throw new SyntaxException(t,"Exception in raSelector");
        }
        return new Index(ft,expr0,expr1);

    }
    
    Index selector() throws SyntaxException{
        Token ft=t;
        Expression expr0=expression();
        match(COMMA);
        Expression expr1=expression();
        return new Index(ft,expr0,expr1);
    }        
            
    
    protected void match(Kind k) throws SyntaxException{
        
        if(t.kind==(k))
        {
            consume();
        }
        else
            throw new SyntaxException(t, "Error in match. Found: "+t.kind+", Expected token: "+k+", pos in line:"+t.pos_in_line);
    }
            
    private void consume() {
        // TODO Auto-generated method stub
        t=scanner.nextToken();
    }
    
    /**
    * Only for check at end of program. Does not "consume" EOF so no attempt to get
    * nonexistent next Token.
    * 
    * @return
    * @throws SyntaxException
    */
    private Token matchEOF() throws SyntaxException {
        if (t.kind == EOF) {
            return t;
        }
        String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
        throw new SyntaxException(t, message);
    }
    
}