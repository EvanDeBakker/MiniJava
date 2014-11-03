/*  MiniJava type checking system
 *  Copyright (C) 2014  marklrh
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;

/* Type checking vistor performs step two of type checking. After
 * step one, we have built up a symbol table. This means we now can
 * perform acyclic and no overloading check. We may also use pretty
 * printer to dump symbol table.
 * The majority of the work TypeCheckingVisitor needs to do consists
 * of type checking statement and method calls(message send)
 */

public class TypeCheckingVisitor extends GJDepthFirst<Node,ArrayList<Node> > 
{
  public SymbolTable st;
  public Quit quit;
  public String cur_cid;
  public String cur_mid;

  public TypeCheckingVisitor(SymbolTable st) 
  {
    this.st = st;
    this.quit = new Quit();
    cur_cid = null;
    cur_mid = null;
    initialCheck();
  }

  public void initialCheck()
  {
    st.overallAcyclic();
    st.overallNoOverLoading(); 
    st.prettyPrinter();
  }

  @Override
  public Node visit(NodeList n, ArrayList<Node> argu) 
  {
    return null;
  }

  // Lots of stupid hacks here
  @Override
  public Node visit(NodeListOptional n, ArrayList<Node> argu) 
  {
    if(argu == null)
    {
      if(n.present())
      {
        for(int i = 0; i < n.size(); i++)
          n.elementAt(i).accept(this, null);
      }
      return null;
    }
    else
    {
      if(n.present())
      {
        if(n.size() != argu.size())
          quit.q("Num of parameters mismatches with num of arguments");
        for(int i = 0; i < n.size(); i++)
        {
          if(!st.subTyping((n.elementAt(i).accept(this, null)), argu.get(i), false))
            quit.q("Type checking failure at NodeListOptional: MessageSend");
        }
      }
      else
      {
        if(argu.size() != 0)
          quit.q("Type checking failure at NodeListOptional: MessageSend");
      }
      return null;
    }
  }

  @Override
  public Node visit(NodeOptional n, ArrayList<Node> argu) 
  {
    if ( n.present() )
      return n.node.accept(this,argu);
    else
      return null;
  }

  @Override
  public Node visit(NodeSequence n, ArrayList<Node> argu) 
  {
    if(argu == null)
      return null;
    else
    {
      if(n.size() != argu.size())
        quit.q("Number of parameters mismatches with number of arguments");
      for(int i = 0; i < n.size(); i++)
      {
        if(!st.subTyping(n.elementAt(i).accept(this, null), argu.get(i), false))
          quit.q("Type checking failure at NodeListOptional: MessageSend");
      }
      return null;
    }
  }

  @Override
  public Node visit(NodeToken n, ArrayList<Node> argu) 
  { 
    return null;
  }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
  */
  @Override
  public Node visit(Goal n, ArrayList<Node> argu) 
  {
    n.f0.accept(this, null);
    n.f1.accept(this, null);
    return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier()
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )*
   * f15 -> ( Statement() )* -> should be checked
   * f16 -> "}"
   * f17 -> "}"
   */
  @Override
  public Node visit(MainClass n, ArrayList<Node> argu) {
    if(cur_cid != null || cur_mid != null)
      quit.q("Unexpected Error at 162");
    String cid = n.f1.f0.toString();
    String mid = n.f6.toString();
    cur_cid = cid;
    cur_mid = mid;
    n.f15.accept(this, null);
    cur_cid = null;
    cur_mid = null;
    return null;
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public Node visit(TypeDeclaration n, ArrayList<Node> argu) {
    n.f0.accept(this, null);
    return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  @Override
  public Node visit(ClassDeclaration n, ArrayList<Node> argu) {
    if(cur_cid != null || cur_mid != null)
      quit.q("Unexpecetd Error at 193");
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    n.f4.accept(this, null);
    cur_cid = null;
    cur_mid = null;
    return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */
  @Override
  public Node visit(ClassExtendsDeclaration n, ArrayList<Node> argu) {
    if(cur_cid != null || cur_mid != null)
      quit.q("Unexpected Error at 215");
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    n.f6.accept(this, null);
    cur_cid = null;
    cur_mid = null;
    return null;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public Node visit(VarDeclaration n, ArrayList<Node> argu) {
    // Declartion is already checked in step 1
    return null;
  }

  /**
   * f0 -> "public"
   * f1 -> Type()
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( FormalParameterList() )?
   * f5 -> ")"
   * f6 -> "{"
   * f7 -> ( VarDeclaration() )*
   * f8 -> ( Statement() )*
   * f9 -> "return"
   * f10 -> Expression()
   * f11 -> ";"
   * f12 -> "}"
   */
  @Override
  public Node visit(MethodDeclaration n,  ArrayList<Node> argu) {
    if(cur_mid != null || cur_cid == null)
      quit.q("Unexpected Error at 259");
    String mid = n.f2.f0.toString();
    assert(mid != null);
    cur_mid = mid;
    Node f8t = n.f8.accept(this, null);
    Node f10t = n.f10.accept(this, null);
    if(!st.subTyping(f10t, st.getNodeFromType(n.f1), true))
      quit.q("Return type mismatches with declared return type");
    cur_mid = null;
    return null;
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  @Override
  public Node visit(FormalParameterList n, ArrayList<Node> argu)
  {
    /* simply return null since there is nothing
     * we need to check now */
    return null;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public Node visit(FormalParameter n, ArrayList<Node> argu) {
    /* simply return null since there is nothing
     * we need to check now */
    return null;
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  @Override
  public Node visit(FormalParameterRest n, ArrayList<Node> argu) {
    /* simply return null since there is nothing
     * we need to check now */
    return null;
  }

  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  @Override
  public Node visit(Type n, ArrayList<Node> argu) {
    return n.f0.accept(this, null);
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  @Override
  public Node visit(ArrayType n, ArrayList<Node> argu) {
    return n;
  }


  /**
   * f0 -> "boolean"
   */
  public Node visit(BooleanType n, ArrayList<Node> argu) {
    return n;
  }

  /**
   * f0 -> "int"
   */
  @Override
  public Node visit(IntegerType n, ArrayList<Node> argu) {
    return n;
  }

  /**
   * f0 -> Block()
   *       | AssignmentStatement()
   *       | ArrayAssignmentStatement()
   *       | IfStatement()
   *       | WhileStatement()
   *       | PrintStatement()
   */
  public Node visit(Statement n, ArrayList<Node> argu) {
    // expect success type here
    return n.f0.accept(this, null);
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  @Override
  public Node visit(Block n, ArrayList<Node> argu) {
  // expect success type
    return n.f1.accept(this, null);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  @Override
  public Node visit(AssignmentStatement n, ArrayList<Node> argu) {
    Node id_type = n.f0.accept(this, null);
    Node exp_type = n.f2.accept(this, null);
    if(!st.subTyping(exp_type, id_type, false))
      quit.q("Type checking failure at AssignmentStatement");
    return null;
  }

  /**
   * f0 -> Identifier() // should be array type
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  @Override
  public Node visit(ArrayAssignmentStatement n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    if(!(f0t instanceof ArrayType))
      quit.q("Type checking failure at ArrayAssignmentStatement");

    Node f2t = n.f2.accept(this, null);
    if(!(f2t instanceof IntegerType))
      quit.q("Type checking failure at ArrayAssignmentStatement");

    Node f5t = n.f5.accept(this, null);
    if(!(f5t instanceof IntegerType))
      quit.q("Type checking failure at ArrayAssignmentStatement");
    return null;
  }

  /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   */
  @Override
  public Node visit(IfStatement n, ArrayList<Node> argu) {
    Node f2t = n.f2.accept(this, null);
    if(!(f2t instanceof BooleanType))
      quit.q("Type checking failure at IfStatement");
    n.f4.accept(this, null); // expect success type
    n.f6.accept(this, null); // expect success type
    return null;
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  @Override
  public Node visit(WhileStatement n, ArrayList<Node> argu) {
    Node f2t = n.f2.accept(this, null);
    if(!(f2t instanceof BooleanType))
      quit.q("Type checking failure at WhileStatement");
    n.f4.accept(this, null);
    return null;
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  @Override
  public Node visit(PrintStatement n, ArrayList<Node> argu) {
    Node f2t = n.f2.accept(this, null);
    if(!(f2t instanceof IntegerType))
      quit.q("Type checking failure at PrintStatement");
    return null;
  }

  /**
   * f0 -> AndExpression()
   *       | CompareExpression()
   *       | PlusExpression()
   *       | MinusExpression()
   *       | TimesExpression()
   *       | ArrayLookup()
   *       | ArrayLength()
   *       | MessageSend()
   *       | PrimaryExpression()
   */
  public Node visit(Expression n, ArrayList<Node> argu) {
    return n.f0.accept(this, null);
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  @Override
  public Node visit(AndExpression n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if( (!(f0t instanceof BooleanType)) ||
        (!(f2t instanceof BooleanType)) )
      quit.q("Type checking failure at AndExpression");
    return (new BooleanType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  @Override
  public Node visit(CompareExpression n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if((!(f0t instanceof IntegerType)) || (!(f2t instanceof IntegerType)))
      quit.q("Type checking failure at CompareExpression");
    return (new BooleanType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  @Override
  public Node visit(PlusExpression n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if((!(f0t instanceof IntegerType)) || (!(f2t instanceof IntegerType)))
      quit.q("Type checking failure at CompareExpression");
    return (new IntegerType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  @Override
  public Node visit(MinusExpression n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if((!(f0t instanceof IntegerType)) || (!(f2t instanceof IntegerType)))
      quit.q("Type checking failure at CompareExpression");
    return (new IntegerType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  @Override
  public Node visit(TimesExpression n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if((!(f0t instanceof IntegerType)) || (!(f2t instanceof IntegerType)))
      quit.q("Type checking failure at CompareExpression");
    return (new IntegerType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  @Override
  public Node visit(ArrayLookup n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    Node f2t = n.f2.accept(this, null);
    if(!((f0t instanceof ArrayType) && (f2t instanceof IntegerType)))
      quit.q("Type checking failure at ArrayLookup");
    return (new IntegerType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  @Override
  public Node visit(ArrayLength n, ArrayList<Node> argu) {
    Node f0t = n.f0.accept(this, null);
    if(!(f0t instanceof ArrayType))
      quit.q("Type checking failure at ArrayLength");
    return (new IntegerType());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   * 
   * The overall checking can be decomposed into the following steps.
   * 1. If one step fails, whole checking fails immediately.
   * 2. f0 should be of identifier type and should be an EXISTING class
   * 3. f2 should be of identifier type and should exist in class's method set
   * 4. get the method return type and parameters' types
   * 5. get expression list's list of types
   * 6. if parameters' types match expression list's types, we return 
   *    the return type of the method
   */
  @Override
  public Node visit(MessageSend n, ArrayList<Node> argu) {
    if(cur_cid == null || cur_mid == null)
      quit.q("Unexpected Error at 566");
    Node f0t = n.f0.accept(this, null);
    String cid = null;
    if(f0t instanceof Identifier)
      cid = ((Identifier)f0t).f0.toString();
    else
      quit.q("Type checking fails at MessageSend");
    Clazz c = st.getClazz(cid);
    if(c == null)
      quit.q("Type checking fails at MessageSend: unknown class");
    String mid = n.f2.f0.toString();
    Meth m = c.getMeth(mid);
    if(m == null)
      quit.q("Type checking fails at MessageSend: unknown method"); 
    ArrayList<Var> ps = m.getParameters();
    ArrayList<Node> ts = new ArrayList<Node>();
    for(Var v : ps)
    {
      if(v == null)
        quit.q("Unexpected Error at 585");
      Node tt = st.getNodeFromType(v.getType());
      if(tt == null)
        quit.q("Unexpected Error at 588");
      ts.add(tt);
    }
    // f4 is NodeOptional essentially
    // NodeOption -> null or ExpressionList
    // ExpressionList -> Expression and NodeListOptional
    // should expect SuccessType()
    n.f4.accept(this, ts);
    Type rt = m.getType();
    return st.getNodeFromType(rt);
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )* // NodeList optional
   */
  @Override
  public Node visit(ExpressionList n, ArrayList<Node> argu) 
  {
    assert(argu != null);
    if(argu.size () == 0)
      quit.q("Array of expected types should not be zero");
    Node hdt = argu.get(0);
    Node f0t = n.f0.accept(this, null);
    if(!st.subTyping(f0t, hdt, false))
      quit.q("Type check failure at ExpressionList");
    ArrayList<Node> tl = new ArrayList<Node>();
    for(int i = 1; i < argu.size(); i++)
      tl.add(argu.get(i));
    n.f1.accept(this, tl);
    return null;
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  @Override
  public Node visit(ExpressionRest n, ArrayList<Node> argu) {
    return n.f1.accept(this, argu);
  }

  /**
   * f0 -> IntegerLiteral()
   *       | TrueLiteral()
   *       | FalseLiteral()
   *       | Identifier()
   *       | ThisExpression()
   *       | ArrayAllocationExpression()
   *       | AllocationExpression()
   *       | NotExpression()
   *       | BracketExpression()
   */
  @Override
  public Node visit(PrimaryExpression n, ArrayList<Node> argu) {
    return n.f0.accept(this, argu);
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  @Override
  public Node visit(IntegerLiteral n, ArrayList<Node> argu) {
    return (new IntegerType());
  }

   /**
    * f0 -> "true"
    */
  @Override
  public Node visit(TrueLiteral n, ArrayList<Node> argu) {
    return (new BooleanType());
  }

   /**
    * f0 -> "false"
    */
  @Override
  public Node visit(FalseLiteral n, ArrayList<Node> argu) {
    return (new BooleanType());
  }

  /**
   * f0 -> <IDENTIFIER>
   * BAD HACK because of loss of generality
   */
  @Override
  public Node visit(Identifier n, ArrayList<Node> argu) {
    if(cur_cid != null && cur_mid != null)
    {
      String id = n.f0.toString();
      Clazz c = st.getClazz(cur_cid);
      if(c == null)
        quit.q("Unexpected Error at 679");
      Meth m = c.getMeth(cur_mid);
      if(m == null)
        quit.q("Unexpected Error at 682");
      Var v = m.getParameter(id);
      if(v != null)
      {
        Type t = v.getType();
        if(t == null)
          quit.q("Unexpected Error at 688");
        return st.getNodeFromType(t);
      }
      else
      {
        v = m.getLocalVar(id);
        if(v != null)
        {
          Type t = v.getType();
          if(t == null)
            quit.q("Unexpected Error at 698");
          return st.getNodeFromType(t);
        }
        else
        {
          v = st.getCField(cur_cid, id);
          if(v == null)
            quit.q("Cannot find variable " + id + " in " + cur_cid + "." + cur_mid);
          Type t = v.getType();
          if(t == null)
            quit.q("Unexpected Error at 708");
          return st.getNodeFromType(t);
        }
      }
    }
    // Wrong usage of this function happens!
    quit.q("Unexpected Error at 741");
    return null;
  }

   /**
    * f0 -> "this"
    */
  @Override
  public Node visit(ThisExpression n, ArrayList<Node> argu) 
  {
    if(cur_cid == null)
      quit.q("Unexpected Error at 751");
    Clazz c = st.getClazz(cur_cid);
    if(c == null)
      quit.q("Unexpected Error at 754");
    return new Identifier(new NodeToken(cur_cid));
  }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public Node visit(ArrayAllocationExpression n, ArrayList<Node> argu) {
      Node f3t = n.f3.accept(this, null);
      if(!(f3t instanceof IntegerType))
        quit.q("Type check fails at ArrayAllocationExpression");
      return (new ArrayType());
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    * identifier should be some class 
    * return Identifier
    */
   public Node visit(AllocationExpression n, ArrayList<Node> argu) {
      String cid = n.f1.f0.toString();
      if(!st.containsClazz(cid))
        quit.q("Type check fails at AllocationExpression");
      return n.f1;
   }

   /**
    * f0 -> "!"
    * f1 -> Expression()
    */
   public Node visit(NotExpression n, ArrayList<Node> argu) {
      Node f1t = n.f1.accept(this, null);
      if(!(f1t instanceof BooleanType))
        quit.q("Type check fails at NotExpression");
      return (new BooleanType());
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
  public Node visit(BracketExpression n, ArrayList<Node> argu) 
  {
    return n.f1.accept(this, null);
  }
}
