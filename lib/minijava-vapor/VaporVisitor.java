/*  MiniJava program to Vapor compilation program
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
import visitor.*;
import syntaxtree.*;

/* Vapor visitor will print out vapor instructions


 */

public class VaporVisitor extends GJDepthFirst<Result, Arguments>
{
  public SymbolTable st;
  public QueryTable qt;
  public IndentationPrinter iPrinter;
  public int t_num;
  public int while_num;
  public int if_num;
  public int null_num;
  public int bounds_num;
  public int ss_num;
  public int indent;
  public String cur_cid;
  public String cur_mid;

  public VaporVisitor(SymbolTable st, QueryTable qt)
  {
    this.st = st;
    this.qt = qt;
    this.iPrinter = new IndentationPrinter();
    this.t_num = 0;
    this.while_num = 1;
    this.if_num = 1;
    this.null_num = 1;
    this.bounds_num = 1;
    this.ss_num = 1;
    this.indent = 1;
    this.cur_cid = null;
    this.cur_mid = null;
  }

  // control indentation and label numbers
  public void incrIndent() {indent += 2;}

  public void decrIndent() {indent -= 2;}

  public void incrTNum() {t_num += 1;}

  public void incrWhileNum() {while_num += 1;}

  public void incrIfNum() {if_num += 1;}

  public void incrNullNum () {null_num += 1;}

  public void incrBoundsNum() {bounds_num += 1;}

  public void resetTNum() {t_num = 0;}

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  @Override
  public Result visit(Goal n, Arguments argu)
  {
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return (new Result(""));
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
   * f15 -> ( Statement() )*
   * f16 -> "}"
   * f17 -> "}"
   */
  @Override
  public Result visit(MainClass n, Arguments argu)
  {
    iPrinter.printIndentStringln(indent, "func Main()");
    incrIndent();
    String cid = st.StringOfId(n.f1);
    String mid = n.f6.toString();
    cur_cid = cid;
    cur_mid = mid;
    n.f14.accept(this, argu);
    n.f15.accept(this, argu);
    cur_cid = null;
    cur_mid = null;
    decrIndent();
    resetTNum();
    return new Result("");
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public Result visit(TypeDeclaration n, Arguments argu) {
    n.f0.accept(this, argu);
    return (new Result(""));
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
  public Result visit(ClassDeclaration n, Arguments argu) {
    cur_cid = st.StringOfId(n.f1);
    n.f4.accept(this, argu);
    cur_cid = null;
    return (new Result(""));
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
  public Result visit(ClassExtendsDeclaration n, Arguments argu) {
    cur_cid = st.StringOfId(n.f1);
    n.f6.accept(this, argu);
    cur_cid = null;
    return (new Result(""));
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public Result visit(VarDeclaration n, Arguments argu) {
    return new Result("");
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
  public Result visit(MethodDeclaration n, Arguments argu) {
    cur_mid = st.StringOfId(n.f2);
    String m = "func " + cur_cid + "." +  cur_mid + "(this";
    iPrinter.printIndentString(0, m);
    n.f4.accept(this, argu);
    iPrinter.printIndentStringln(0, ")");
    incrIndent();
    n.f8.accept(this, argu);
    Result ret_res = n.f10.accept(this, argu);
    // TODO, print ret instruction
    cur_mid = null;
    decrIndent();
    resetTNum();
    return new Result("");
    /*
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    n.f7.accept(this, argu);
    n.f8.accept(this, argu);
    n.f9.accept(this, argu);
    n.f10.accept(this, argu);
    n.f11.accept(this, argu);
    n.f12.accept(this, argu);
    return _ret;
    */
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  @Override
  public Result visit(FormalParameterList n, Arguments argu) {
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return new Result("");
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public Result visit(FormalParameter n, Arguments argu) {
    iPrinter.printIndentString(0, " " + st.StringOfId(n.f1));
    return new Result("");
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  @Override
  public Result visit(FormalParameterRest n, Arguments argu) {
    n.f1.accept(this, argu);
    return new Result("");
  }

  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  @Override
  public Result visit(Type n, Arguments argu) {
    return (new Result(""));
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  @Override
  public Result visit(ArrayType n, Arguments argu)
  {
    return (new Result(""));
  }

  /**
   * f0 -> "boolean"
   */
  @Override
  public Result visit(BooleanType n, Arguments argu) {
    return (new Result(""));
  }

  /**
   * f0 -> "int"
   */
  @Override
  public Result visit(IntegerType n, Arguments argu) {
    return (new Result(""));
  }

  /**
   * f0 -> Block()
   *       | AssignmentStatement()
   *       | ArrayAssignmentStatement()
   *       | IfStatement()
   *       | WhileStatement()
   *       | PrintStatement()
   */
  @Override
  public Result visit(Statement n, Arguments argu) {
    n.f0.accept(this, argu);
    return (new Result(""));
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  @Override
  public Result visit(Block n, Arguments argu) {
    n.f1.accept(this, argu);
    return (new Result(""));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  /*
    First we need to load identifier, and then load the result of expression
    Then, we need to assign the result to identifier
   */
  @Override
  public Result visit(AssignmentStatement n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    Result rf1 = n.f2.accept(this, argu);
    String assigns = rf0.toString() + " = " + rf1.toString();
    iPrinter.printIndentStringln(indent, assigns);
    return (new Result(""));
  }

  /**
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  public Result visit(ArrayAssignmentStatement n, Arguments argu)
  {
    // get array base
    Result rf0 = n.f0.accept(this, argu); // base
    String array_access = iPrinter.ArrayAccess(this, rf0.toString());
    // get index
    Result rf2 = n.f2.accept(this, argu); // index
    String check_index_range = iPrinter.CheckIndexInRange(this, rf2.toString());
    String element_access = iPrinter.ArrayElementAccess(this, rf2.toString());
    // store current t_num
    int e_addr_num = this.t_num;
    // get right-hand expression
    Result rf5 = n.f1.accept(this, argu);
    String assign = iPrinter.ArrayAssignment(this, e_addr_num, rf5.toString());
    iPrinter.printIndentString(0, check_index_range);
    iPrinter.printIndentString(0, element_access);
    iPrinter.printIndentString(0, assign);
    return (new Result(""));
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
  public Result visit(IfStatement n, Arguments argu) {
    // condition
    Result rf2 = n.f2.accept(this, argu); // expression
    String if_cond = iPrinter.IfCondition0(this, rf2.toString());
    iPrinter.printIndentString(0, if_cond);
    incrIndent();
    // statement
    Result rf4 = n.f4.accept(this, argu);
    Strint if_goto = iPrinter.ifGoto(this);
    iPrinter.printIndentString(0, if_goto);
    decrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getIfElse(if_num) + ":");
    // else statement
    incrIndent();
    Result rf6 = n.f6.accept(this, null);
    decrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getIfEnd(if_num) + ":");
    // increment if number
    incrIfNum();
    return (new Result(""));
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  public Result visit(WhileStatement n, Arguments argu) {
    String while_top = iPrinter.getWhileTop(this.while_num);
    iPrinter.printIndentStringln(this.indent, while_top + ":");
    Result rf2 = n.f2.accept(this, argu); // expression
    String while_cond = iPrinter.whileCondition(this, rf2.toString());
    incrIndent();
    n.f4.accept(this, argu);
    decrIndent();
    String while_end = iPrinter.getWhileEnd(this.while_num);
    iPrinter.printIndentStringln(this.indent, while_end + ":");
    incrWhileNum();
    return (new Result(""));
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  public Result visit(PrintStatement n, Argument argu) {
    Result rf2 = n.f2.accept(this, argu);
    String print_msg = iPrinter.print(this, rf2.toString());
    return (new Result(""));
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
  public Result visit(Expression n, Arguments argu) {
    int cur_tnum = this.tnum;
    incrTNum();
    return n.f0.accept(this, new Arguments(cur_tnum));
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  public Result visit(AndExpression n, Arguments argu) {
    // use this t to store final condition
    int cur_tnum = argu.getNum();
    // eval first
    Result rf0 = n.f0.accept(this, new Arguments(cur_tnum));
    // first and condition jump
    String foo = iPrinter.andLeft(this, rf0.toString());
    iPrinter.printIndentString(0, foo);
    // eval second
    incrIndent();
    Result rf2 = n.f2.accept(this, new Arguments(cur_tnum));
    iPrinter.printIndentString(0, iPrinter.andGoto(this));
    decrIndent();
    // when first and second both false, set final t to zero
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSElse(ss_num) + ":");
    incrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSSetFalse(cur_tnum));
    decrIndent();
    // set ss_end label
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSEnd(ss_num));
    return (new Result(iPrinter.getTemp(cur_tnum));
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public R visit(CompareExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public R visit(PlusExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public R visit(MinusExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public R visit(TimesExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public R visit(ArrayLookup n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public R visit(ArrayLength n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  public R visit(MessageSend n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    n.f5.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  public R visit(ExpressionList n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public R visit(ExpressionRest n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return _ret;
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
  public R visit(PrimaryExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public R visit(IntegerLiteral n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> "true"
   */
  public R visit(TrueLiteral n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> "false"
   */
  public R visit(FalseLiteral n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  @Override
  public Result visit(Identifier n, Arguments argu)
  {
    String vid = st.StringOfId(n);
    if(st.isField(cur_cid, cur_mid, vid))
    {
      String ret = "";
      int fpos = qt.getFieldPos(cur_cid, vid);
      ret += "this+";
      ret += (new Integer(fpos)).toString();
      return (new Result(ret));
    }
    else
      return (new Result(vid));
  }

  /**
   * f0 -> "this"
   */
  public R visit(ThisExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public R visit(ArrayAllocationExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  @Override
  public Result visit(AllocationExpression n, Arguments argu)
  {
    String cid = st.StringOfId(n.f1);
    String allocs = iPrinter.getObjectAllocString(this, cid);
    // TODO
    return null;
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public R visit(NotExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    return _ret;
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public R visit(BracketExpression n, A argu) {
    R _ret=null;
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    return _ret;
  }


}

