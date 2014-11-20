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
  private SymbolTable st;
  private QueryTable qt;
  private IndentationPrinter iPrinter;
  private int t_num;
  private int while_num;
  private int if_num;
  private int if_else_num;
  private int null_num;
  private int indent;
  private String cur_cid;
  private String cur_mid;

  public VaporVisitor(SymbolTable st, QueryTable qt)
  {
    this.st = st;
    this.qt = qt;
    this.iPrinter = new IndentationPrinter();
    this.t_num = 0;
    this.while_num = 1;
    this.if_num = 1;
    this.if_else_num = 1;
    this.null_num = 1;
    this.indent = 1;
    this.cur_cid = null;
    this.cur_mid = null;
  }

  public void incrIndent() {indent += 2;}

  public void decrIndent() {indent -= 2;}

  public void incrTNum() {t_num += 1;}

  public void incrWhileNum() {while_num += 1;}

  public void incrIfNum() {if_num += 1;}

  public void incrNullNum () {null_num += 1;}

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
  public Result visit(ArrayAssignmentStatement n, Arguments argu) {

    n.f0.accept(this, argu);
    n.f2.accept(this, argu);
    n.f5.accept(this, argu);
    n.f6.accept(this, argu);
    return null;
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
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  @Override
  public Result visit(AllocationExpression n, Arguments argu)
  {
    String cid = st.StringOfId(n.f1);
    int chs = qt.getClazzHeapSize(cid);
    String allocs = iPrinter.getObjectAllocString(this, cid,
      indent, chs, t_num, null_num);
    return null;
  }
}

