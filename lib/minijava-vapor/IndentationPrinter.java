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

import java.util.*;

public class IndentationPrinter
{
  public IndentationPrinter(){}

  public String getIndentString(int n, String s)
  {
    String i = "";
    while(n != 0)
    {
      i += " ";
      n--;
    }
    return i + s;
  }

  public String getIndentStringln(int n, String s)
  {
    return getIndentString(n, s) + "\n";
  }

  public String getIndentation(int n)
  {
    String i = "";
    while(n != 0)
    {
      i += " ";
      n--;
    }
    return i;
  }

  public void printIndentString(int n, String s)
  {
    System.out.print(this.getIndentString(n,s));
  }

  public void printIndentation(int n)
  {
    System.out.print(this.getIndentation(n));
  }

  public void printIndentStringln(int n, String s)
  {
    printIndentString(n, s);
    System.out.println("");
  }

  public void printLog(String msg)
  {
    String ret = "-----------------------------";
    ret += msg;
    ret += "-----------------------------";
    System.out.println(ret);
    System.exit(1);
  }


  // *********************** ArrayAssignmentStatement ***********************
  // mysterious comments: atomic null
  public String ArrayAccess
  (VaporVisitor vv, String base)
  {
    String ret = "";
    String nn = getNull(vv.null_num);
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = " + base);
    ret += getIndentStringln(vv.indent, "if " + t + " goto :" + nn);
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, nn + ":");
    return ret;
  }

  // atomic bound
  public String CheckIndexInRange(VaporVisitor vv, String base, String index)
  {
    String tcur = getTemp(vv.t_num);
    String bounds = getBounds(vv.bounds_num);
    String ret = "";
    ret += getIndentStringln(vv.indent, tcur + " = [" + base + "]");
    ret += getIndentStringln(vv.indent, tcur + " = Lt(" + index + " " + tcur + ")");
    ret += getIndentStringln(vv.indent, "if " + tcur + " goto: " + bounds);
    ret += getIndentStringln(vv.indent + 2, "Error(\"array index out of bounds\")");
    ret += getIndentStringln(vv.indent, bounds + ":");
    // do not increment tnum
    return ret;
  }

  public String ArrayElementAccess(VaporVisitor vv, String index)
  {
    String tcur = getTemp(vv.t_num);
    String tprev = getTemp(vv.t_num - 1);
    String ret = "";
    ret += getIndentStringln(vv.indent, tcur + " = Muls(" + index + " 4)");
    ret += getIndentStringln(vv.indent, tcur + " = Add(" +
      tcur + " " + tprev + ")");
    return ret;
  }

  public String ArrayAssignment(VaporVisitor vv, int tn, String r)
  {
    String t = getTemp(tn);
    String ret = "";
    ret += getIndentStringln(vv.indent, "[" + t + "+4] = " + r);
    return ret;
  }



  public String BasicArrayAlloc()
  {
    String ret = "";
    ret += getIndentStringln(0, "func AllocArray(size)");
    ret += getIndentStringln(2, "bytes = MulS(size 4)");
    ret += getIndentStringln(2, "bytes = Add(bytes 4)");
    ret += getIndentStringln(2, "v = HeapAllocZ(bytes)");
    ret += getIndentStringln(2, "[v] = size");
    ret += getIndentStringln(2, "ret v");
    return ret;
  }




  // *********************** IfStatement ***********************
  public String ifCondition0(VaporVisitor vv, String cond, int cur_if_num)
  {
    String s = "if0 " + cond + " goto :" + getIfElse(cur_if_num);
    return getIndentStringln(vv.indent, s);
  }

  public String ifGoto(VaporVisitor vv, int cur_if_num)
  {
    return getIndentStringln(vv.indent, "goto :" + getIfEnd(cur_if_num));
  }

  // *********************** WhileStatement ***********************
  public String getWhileTop(int n)
  {
    return "while" + (new Integer(n)).toString() + "_top";
  }

  public String getWhileEnd(int n)
  {
    return "while" + (new Integer(n)).toString() + "_end";
  }

  public String whileCondition(VaporVisitor vv, String cond, int cwn)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent,
      "if0 " + cond + " goto :" + getWhileEnd(cwn));
    return ret;
  }

  // *********************** PrintStatement ***********************
  public String print(VaporVisitor vv, String s)
  {
    return getIndentStringln(vv.indent, "PrintIntS(" + s + ")");
  }

  // *********************** AndExpression ***********************
  public String getAndLeft(VaporVisitor vv, String s, int cur_ss_num)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent, "if0 " + s +
                             " goto :" + getSSElse(cur_ss_num));
    return ret;
  }

  public String getAndGoto(VaporVisitor vv, int cur_ss_num)
  {
    return getIndentStringln(vv.indent, "goto :" + getSSEnd(cur_ss_num));
  }

  public String getAndAssign(VaporVisitor vv, int tnum, String res)
  {
    return getIndentStringln(vv.indent, getTemp(tnum) + " = " + res);
  }

  // *********************** CompareExpression ***********************
  public String getLS(VaporVisitor vv, String l, String r)
  {
    String ret = "";
    ret += getTemp(vv.t_num) + " = LtS(";
    ret += l + " " + r + ")";
    return getIndentStringln(vv.indent, ret);
  }


  // *********************** PlusExpression ***********************
  public String getAdd(VaporVisitor vv, String l, String r)
  {
    String ret = "";
    ret += getTemp(vv.t_num) + " = Add(";
    ret += l + " " + r + ")";
    return getIndentStringln(vv.indent, ret);
  }

  // *********************** MinusExpression ***********************
  public String getSub(VaporVisitor vv, String l, String r)
  {
    String ret = "";
    ret += getTemp(vv.t_num) + " = Sub(";
    ret += l + " " + r + ")";
    return getIndentStringln(vv.indent, ret);
  }

  public String getMulti(VaporVisitor vv, String l, String r)
  {
    String ret = "";
    ret += getTemp(vv.t_num) + " = MulS(";
    ret += l + " " + r + ")";
    return getIndentStringln(vv.indent, ret);
  }

  public String getArrayLookupNullCheck(VaporVisitor vv, String r)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent, getTemp(vv.t_num) + " = " + "[" + r + "]");
    ret += getIndentStringln(vv.indent, "if " + getTemp(vv.t_num) + " goto :"
                             + getNull(vv.null_num));
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, getNull(vv.null_num) + ":");
    return ret;
  }

  public String getArrayLookupIndexInRange(VaporVisitor vv, String base, String i)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent, getTemp(vv.t_num) + " = [" +
                             getTemp(vv.t_num - 1) + "]");
    ret += getIndentStringln(vv.indent, getTemp(vv.t_num) + " = Lt(" + i
                              + " " + getTemp(vv.t_num) + ")");
    ret += getIndentStringln(vv.indent, "if " + getTemp(vv.t_num) + " goto :" +
                             getBounds(vv.bounds_num));
    ret += getIndentStringln(vv.indent + 2, "Error(\"array index out of bounds\")");
    ret += getIndentStringln(vv.indent, getBounds(vv.bounds_num) + ":");
    return ret;
  }

  public String getArrayLookupAccess(VaporVisitor vv, String base, String i)
  {
    String ret = "";
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = MulS(" + i + " 4)");
    ret += getIndentStringln(vv.indent, t + " = Add(" + t + " " + base + ")");
    vv.incrTNum();
    ret += getIndentStringln(vv.indent, getTemp(vv.t_num) + " = [" + t + "+4]");
    return ret;
  }

  public String getArrayLength(VaporVisitor vv, String base)
  {
    String ret = "";
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = [" + base + "]");
    ret += getIndentStringln(vv.indent, "if " + t + " goto :" + getNull(vv.null_num));
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, getNull(vv.null_num) + ":");
    vv.incrTNum();
    String tnext = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, tnext + " = [" + t + "]");
    return ret;
  }

  public String getMessageSendCheckNull(VaporVisitor vv, String obj_addr)
  {
    String ret = "";
    String null_label = getNull(vv.null_num);
    ret += getIndentStringln(vv.indent, "if " + obj_addr + " goto :" + null_label);
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, null_label + ":");
    return ret;
  }

  // t.3 = call t.1(this aux01 t.2)
  // aux02 = call t.5(other)
  public String getMessageSendCall(VaporVisitor vv, String on, int mp, String oa)
  {
    String t = getTemp(vv.t_num);
    String ret = "";
    String pos = (new Integer(mp)).toString();
    ret += getIndentStringln(vv.indent, t + " = [" + oa + "]");
    ret += getIndentStringln(vv.indent, t + " = [" + t + "+" + pos + "]");
    vv.incrTNum();
    String tnext = getTemp(vv.t_num);
    ret += getIndentString(vv.indent, tnext + " = call " + t + "(" + oa); // possible bugs!
    for(Result r : vv.exp_res_l)
    {
      ret += " " + r.toString();
    }
    ret += ")\n";
    return ret;
  }

  public String getArrayAlloc(VaporVisitor vv, String sz)
  {
    String t = getTemp(vv.t_num);
    String ret = "";
    ret += getIndentStringln(vv.indent, t + " = call :AllocArray(" + sz + ")");
    return ret;
  }

  public String getObjectAlloc(VaporVisitor vv, String cid)
  {
    int s = vv.qt.getClazzHeapSize(cid);
    String sz = (new Integer(s)).toString();
    String ret = "";
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = HeapAllocZ(" + sz + ")");
    ret += getIndentStringln(vv.indent, "[" + t + "] = :vmt_" + cid);
    return ret;
  }

  public String getNot(VaporVisitor vv, String r)
  {
    String t = getTemp(vv.t_num);
    String ret = "";
    ret += getIndentStringln(vv.indent, t + " = Sub(1 " + r + ")");
    return ret;
  }

  public String getRet(VaporVisitor vv, String r)
  {
    String t = getTemp(vv.t_num);
    String ret = "";
    ret += getIndentStringln(vv.indent, t + " = " + r);
    ret += getIndentStringln(vv.indent, "ret " + t);
    return ret;
  }

  public String getTemp(int n)
  {
    return "t." + (new Integer(n)).toString();
  }

  public String getNull(int n)
  {
    return "null" + (new Integer(n)).toString();
  }

  public String getBounds(int n)
  {
    return "bounds" + (new Integer(n)).toString();
  }

  public String getIfElse(int n)
  {
    return "if" + (new Integer(n)).toString() + "_else";
  }

  public String getIfEnd(int n)
  {
    return "if" + (new Integer(n)).toString() + "_end";
  }

  public String getSSElse(int n)
  {
    return "ss" + (new Integer(n)).toString() + "_else";
  }

  public String getSSEnd(int n)
  {
    return "ss" + (new Integer(n)).toString() + "_end";
  }

  public String getSSSetFalse(int n)
  {
    return getTemp(n) + " = 0";
  }

}