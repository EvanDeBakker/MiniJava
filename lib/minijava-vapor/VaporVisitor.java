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
  public ArrayList<Result> exp_res_l;
  public int t_num;
  public int while_num;
  public int if_num;
  public int null_num;
  public int bounds_num;
  public int ss_num;
  public int indent;
  public String cur_cid;
  public String cur_mid;
  public String cur_oid; // a hack
  public boolean start_collecting; // a hack

  public VaporVisitor(SymbolTable st, QueryTable qt)
  {
    this.st = st;
    this.qt = qt;
    this.iPrinter = new IndentationPrinter();
    this.exp_res_l = new ArrayList<Result>();
    this.t_num = 0;
    this.while_num = 1;
    this.if_num = 1;
    this.null_num = 1;
    this.bounds_num = 1;
    this.ss_num = 1;
    this.indent = 0;
    this.cur_cid = null;
    this.cur_mid = null;
    this.cur_oid = null;
    this.start_collecting = false;
  }

  // control indentation and label numbers
  public void incrIndent() {indent += 2;}

  public void decrIndent() {indent -= 2;}

  public void incrTNum() {t_num += 1;}

  public void incrWhileNum() {while_num += 1;}

  public void incrIfNum() {if_num += 1;}

  public void incrNullNum () {null_num += 1;}

  public void incrBoundsNum() {bounds_num += 1;}

  public void incrSSNum() {ss_num += 1;}

  public void resetTNum() {t_num = 0;}

  public void turn_on_collecting()
  {
    assert(this.exp_res_l.size() == 0);
    assert(!this.start_collecting);
    this.start_collecting = true;
  }

  public void turn_off_collecting()
  {
    assert(this.start_collecting);
    this.exp_res_l.clear();
    this.start_collecting = false;
  }

  // safely return null because it will not be used
  public Result visit(NodeList n, Arguments argu) {
    return null;
  }

  public Result visit(NodeListOptional n, Arguments argu) {
    if (n.present())
    {
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        Result r = e.nextElement().accept(this,argu);
        if(start_collecting)
        {
          String t = iPrinter.getTemp(this.t_num);
          incrTNum();
          iPrinter.printIndentStringln(this.indent, t + " = " + r.toString());
          exp_res_l.add(new Result(t));
        }
      }
      return null;
    }
    else
      return null;
  }

  public Result visit(NodeOptional n, Arguments argu) {
    if ( n.present() )
      return n.node.accept(this,argu);
    else
      return null;
  }

  public Result visit(NodeSequence n, Arguments argu) {
    Result _ret=null;
    int _count=0;
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this,argu);
      _count++;
    }
    return _ret;
  }

  public Result visit(NodeToken n, Arguments argu) { return null; }

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
    iPrinter.printIndentString(0, "\n" + iPrinter.BasicArrayAlloc());
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
    iPrinter.printIndentStringln(this.indent, "func Main()");
    incrIndent();
    String cid = st.StringOfId(n.f1);
    String mid = n.f6.toString();
    cur_cid = cid;
    cur_mid = mid;
    //n.f14.accept(this, argu); // node list optional
    n.f15.accept(this, argu); // node list optional
    cur_cid = null;
    cur_mid = null;
    iPrinter.printIndentStringln(this.indent, "ret");
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
    System.out.println(""); // print out a blank line to seperate functions
    cur_mid = st.StringOfId(n.f2);
    String m = "func " + cur_cid + "." +  cur_mid + "(this";
    iPrinter.printIndentString(0, m);
    n.f4.accept(this, argu);
    iPrinter.printIndentStringln(0, ")");
    incrIndent();
    n.f8.accept(this, argu);
    Result ret_res = n.f10.accept(this, argu);
    String ret_s = iPrinter.getRet(this, ret_res.toString());
    // no need to increment tnum because we are going to reset it anyway
    iPrinter.printIndentString(0, ret_s);
    cur_mid = null;
    decrIndent();
    resetTNum();
    return new Result("");
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
  /*
  t.1 = [this+4]
  if t.1 goto :null9
    Error("null pointer")
  null9:
  t.2 = [t.1]
  t.2 = Lt(0 t.2)
  if t.2 goto :bounds8
    Error("array index out of bounds")
  bounds8:
  t.2 = MulS(0 4)
  t.2 = Add(t.2 t.1)
  [t.2+4] = 20
   */
  public Result visit(ArrayAssignmentStatement n, Arguments argu)
  {
    // ------------------- f0 ----------------------
    // get array base
    Result rf0 = n.f0.accept(this, argu);
    String base = rf0.toString(); // raw base [this + 4]
    String array_access = iPrinter.ArrayAccess(this, base);
    iPrinter.printIndentString(0, array_access);
    // base now stored in a variable, which is t.1
    base = iPrinter.getTemp(this.t_num);
    incrNullNum();
    incrTNum();
    // get index
    // ------------------- f2 ----------------------
    Result rf2 = n.f2.accept(this, argu);
    String index = rf2.toString();
    String intert = iPrinter.getTemp(this.t_num);
    incrTNum();
    iPrinter.printIndentStringln(this.indent, intert + " = " + index);

    String check_index_range = iPrinter.CheckIndexInRange(this, base, intert);
    iPrinter.printIndentString(0, check_index_range);
    incrBoundsNum();

    String element_access = iPrinter.ArrayElementAccess(this, intert, base);
    iPrinter.printIndentString(0, element_access);
    // store current t_num
    int e_addr_num = this.t_num;
    incrTNum();
    // ------------------- f5 ----------------------
    // get right-hand expression
    Result rf5 = n.f5.accept(this, argu);
    String intert2 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert2 + " = " + rf5.toString());
    incrTNum();
    String assign = iPrinter.ArrayAssignment(this, e_addr_num, intert2);
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
  /*
  func Fac.ComputeFac(this num)
    t.0 = LtS(num 1) // expression
    if0 t.0 goto :if1_else
      num_aux = 1
      goto :if1_end
    if1_else:
      t.1 = [this]
      t.1 = [t.1+0]
      t.2 = Sub(num 1)
      t.3 = call t.1(this t.2)
      num_aux = MulS(num t.3)
    if1_end:
    ret num_aux
   */
  public Result visit(IfStatement n, Arguments argu) {
    // condition
    Result rf2 = n.f2.accept(this, argu); // expression
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + rf2.toString());
    incrTNum();
    int cur_if_num = this.if_num;
    incrIfNum();
    String if_cond = iPrinter.ifCondition0(this, intert, cur_if_num);
    iPrinter.printIndentString(0, if_cond);
    incrIndent();
    // statement
    Result rf4 = n.f4.accept(this, argu);
    String if_goto = iPrinter.ifGoto(this, cur_if_num);
    iPrinter.printIndentString(0, if_goto);
    decrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getIfElse(cur_if_num) + ":");
    // else statement
    incrIndent();
    Result rf6 = n.f6.accept(this, null);
    decrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getIfEnd(cur_if_num) + ":");
    // increment if_number at the very end
    return (new Result(""));
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  /*
  while1_top:
  t.1 = LtS(aux02 i)
  if0 t.1 goto :while1_end
    j = 1
    while2_top:
    t.2 = Add(i 1)
    t.3 = LtS(j t.2)
    if0 t.3 goto :while2_end
      ...
      ...
    while2_end:
    i = Sub(i 1)
    goto :while1_top
  while1_end:
  ret 0
   */
  public Result visit(WhileStatement n, Arguments argu)
  {
    int cur_while_num = this.while_num;
    incrWhileNum();
    String while_top = iPrinter.getWhileTop(cur_while_num);
    iPrinter.printIndentStringln(this.indent, while_top + ":");
    Result rf2 = n.f2.accept(this, argu); // expression
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " +
      rf2.toString());
    incrTNum();
    String while_cond = iPrinter.whileCondition(this, intert,
      cur_while_num);
    iPrinter.printIndentString(0, while_cond);
    incrIndent();
    // we don't need its result since it is a statement
    n.f4.accept(this, argu);
    // print goto top label
    iPrinter.printIndentStringln(this.indent, "goto :" + iPrinter.getWhileTop(cur_while_num));
    decrIndent();
    String while_end = iPrinter.getWhileEnd(cur_while_num);
    iPrinter.printIndentStringln(this.indent, while_end + ":");
    // incremnet while_number at the very end
    return (new Result(""));
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  public Result visit(PrintStatement n, Arguments argu) {
    Result rf2 = n.f2.accept(this, argu);
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + rf2.toString());
    incrTNum();
    String print_msg = iPrinter.print(this, intert);
    iPrinter.printIndentString(0, print_msg);
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
    return n.f0.accept(this, argu);
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  /*
  t.1 = Sub(1 var_end)
  if0 t.1 goto :ss1_else
    t.0 = Sub(1 ret_val)
    goto :ss1_end
  ss1_else:
    t.0 = 0
  ss1_end:
   */
  public Result visit(AndExpression n, Arguments argu) {
    // use this t to store final condition
    int cur_tnum = this.t_num;
    int cur_ssnum = this.ss_num;
    incrTNum();
    incrSSNum();
    // eval first
    Result rf0 = n.f0.accept(this, argu);
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + rf0.toString());
    incrTNum();
    // first and condition jump
    String foo = iPrinter.getAndLeft(this, intert, cur_ssnum);
    iPrinter.printIndentString(0, foo);
    // eval second
    incrIndent();
    Result rf2 = n.f2.accept(this, argu);
    String assign_second = iPrinter.getAndAssign(this, cur_tnum, rf2.toString());
    // print second assign
    iPrinter.printIndentString(0, assign_second);
    iPrinter.printIndentString(0, iPrinter.getAndGoto(this, cur_ssnum));
    decrIndent();
    // when first and second both false, set final t to zero
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSElse(cur_ssnum) + ":");
    incrIndent();
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSSetFalse(cur_tnum));
    decrIndent();
    // set ss_end label
    iPrinter.printIndentStringln(this.indent, iPrinter.getSSEnd(cur_ssnum) + ":");
    // final result stored in t.cur_tnum
    return (new Result(iPrinter.getTemp(cur_tnum)));
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public Result visit(CompareExpression n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    Result rf2 = n.f2.accept(this, argu);
    String intert1 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert1 + " = " + rf0.toString());
    incrTNum();
    String intert2 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert2 + " = " + rf2.toString());
    incrTNum();
    String comp_s = iPrinter.getLS(this, intert1, intert2);
    iPrinter.printIndentString(0, comp_s);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public Result visit(PlusExpression n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    Result rf2 = n.f2.accept(this, argu);
    String intert1 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert1 + " = " + rf0.toString());
    incrTNum();
    String intert2 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert2 + " = " + rf2.toString());
    incrTNum();
    String add_s = iPrinter.getAdd(this, intert1, intert2);
    iPrinter.printIndentString(0, add_s);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public Result visit(MinusExpression n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    Result rf2 = n.f2.accept(this, argu);
    String intert1 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert1 + " = " + rf0.toString());
    incrTNum();
    String intert2 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert2 + " = " + rf2.toString());
    incrTNum();
    String sub_s = iPrinter.getSub(this, intert1, intert2);
    iPrinter.printIndentString(0, sub_s);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public Result visit(TimesExpression n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    Result rf2 = n.f2.accept(this, argu);
    String intert1 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert1 + " = " + rf0.toString());
    incrTNum();
    String intert2 = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert2 + " = " + rf2.toString());
    incrTNum();
    String mult_s = iPrinter.getMulti(this, intert1, intert2);
    iPrinter.printIndentString(0, mult_s);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */

  /*
    t.2 = [this+4]
    if t.2 goto :null2
      Error("null pointer")
    null2:
    t.3 = [t.2]
    t.3 = Lt(j t.3)
    if t.3 goto :bounds1
      Error("array index out of bounds")
    bounds1:
    t.3 = MulS(j 4)
    t.3 = Add(t.3 t.2)
    t.4 = [t.3+4]
   */
  public Result visit(ArrayLookup n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    String base = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, base + " = " + rf0.toString());
    incrTNum();
    String ar_null_check = iPrinter.getArrayLookupNullCheck(this, base); // should return this+4
    incrNullNum();
    iPrinter.printIndentString(0, ar_null_check);
    Result rf2 = n.f2.accept(this, argu);
    String index = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, index + " = " + rf2.toString());
    incrTNum();
    String ar_inrange_check = iPrinter.getArrayLookupIndexInRange
                              (this, base, index);
    incrBoundsNum();
    // do not increment tnum here
    iPrinter.printIndentString(0, ar_inrange_check);
    String ar_access = iPrinter.getArrayLookupAccess(this, base, index);
    iPrinter.printIndentString(0, ar_access);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public Result visit(ArrayLength n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    String base = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, base + " = " + rf0.toString());
    incrTNum();
    String ar_length_s = iPrinter.getArrayLength(this, base);
    incrNullNum();
    iPrinter.printIndentString(0, ar_length_s);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  public Result visit(MessageSend n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    String obj_addr = rf0.toString();
    // rf0 is the object address other than this, first need to check null
    if(!cur_oid.equals("this"))
    {
      String intert = iPrinter.getTemp(this.t_num);
      iPrinter.printIndentStringln(this.indent, intert + " = " + obj_addr);
      incrTNum();
      obj_addr = intert;
      String check_null = iPrinter.getMessageSendCheckNull(this, obj_addr);
      iPrinter.printIndentString(0, check_null);
      incrNullNum();
    }
    String method_name = st.StringOfId(n.f2);
    assert(cur_oid != null);
    String query_class_name = cur_oid;
    if(cur_oid.equals("this"))
      query_class_name = cur_cid;
    int method_pos = qt.getFunctionLabelPos(query_class_name, method_name);
    Type rtype = st.getMeth(query_class_name, method_name).getType();
    String rtstr = st.MethodReturnTypeToString(rtype);
    if(rtstr != null)
      cur_oid = rtstr;
    // start collecting expression list's results
    turn_on_collecting();
    n.f4.accept(this, argu);
    // prepare method call
    String method_call = iPrinter.getMessageSendCall(this,
      cur_oid, method_pos, obj_addr);
    turn_off_collecting();
    iPrinter.printIndentString(0, method_call);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  public Result visit(ExpressionList n, Arguments argu) {
    Result rf0 = n.f0.accept(this, argu);
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + rf0.toString());
    incrTNum();
    this.exp_res_l.add(new Result(intert));
    n.f1.accept(this, argu);
    // we only collect result in this funciton,
    // so we don't need to return any result
    return (new Result(""));
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public Result visit(ExpressionRest n, Arguments argu) {
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
  public Result visit(PrimaryExpression n, Arguments argu) {
    return n.f0.accept(this, argu);
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public Result visit(IntegerLiteral n, Arguments argu) {
    String num = n.f0.toString();
    return (new Result(num));
  }

  /**
   * f0 -> "true"
   */
  public Result visit(TrueLiteral n, Arguments argu) {
    return (new Result("1"));
  }

  /**
   * f0 -> "false"
   */
  public Result visit(FalseLiteral n, Arguments argu) {
    return (new Result("0"));
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
      Var f = st.getCField(cur_cid, vid);
      this.cur_oid = st.StringOfType(f.getType());
      String ret = "";
      int fpos = qt.getFieldPos(cur_cid, vid);
      ret += "[this+";
      ret += (new Integer(fpos)).toString() + "]";
      /*
      String t = getTemp(vv.t_num);
      iPrinter.printIndentStringln(vv.indent, t + " = " + ret);
      Result r = new Result(iPrinter.getTemp(vv.t_num));
      incrTNum();
      */
      Result r = new Result(ret);
      return r;
    }
    else
    {
      Type vt = st.getLocalVarType(cur_cid, cur_mid, vid);
      this.cur_oid = st.StringOfType(vt);
      return (new Result(vid));
    }
  }

  /**
   * f0 -> "this"
   */
  public Result visit(ThisExpression n, Arguments argu) {
    this.cur_oid = "this";
    return (new Result("this"));
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public Result visit(ArrayAllocationExpression n, Arguments argu) {
    Result rf3 = n.f3.accept(this, argu);
    String sz = rf3.toString();
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + sz);
    incrTNum();
    String array_alloc = iPrinter.getArrayAlloc(this, intert);
    iPrinter.printIndentString(0, array_alloc);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
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
    String oid = st.StringOfId(n.f1);
    this.cur_oid = oid;
    String allocs = iPrinter.getObjectAlloc(this, oid);
    iPrinter.printIndentString(0, allocs);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public Result visit(NotExpression n, Arguments argu) {
    Result rf1 = n.f1.accept(this, argu);
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + rf1.toString());
    incrTNum();
    String notstr = iPrinter.getNot(this, intert);
    iPrinter.printIndentString(0, notstr);
    Result ret = new Result(iPrinter.getTemp(this.t_num));
    incrTNum();
    return ret;
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public Result visit(BracketExpression n, Arguments argu) {
    Result r = n.f1.accept(this, argu);
    String intert = iPrinter.getTemp(this.t_num);
    iPrinter.printIndentStringln(this.indent, intert + " = " + r.toString());
    incrTNum();
    Result ret = new Result(intert);
    return ret;
  }
}

