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

  public void printLog(string msg)
  {
    String ret = "-----------------------------";
    ret += msg;
    ret += "-----------------------------";
    System.out.println(ret);
  }

  /*
   * t.0 = HeapAllocZ(4)
   * [t.0] = :vmt_Fac
   * if t.0 goto :null1
   *   Error("null pointer")
   * null1:
   */
  public String getObjectAllocString
  (VaporVisitor vv, String cid)
  {
    int heap_size = vv.qt.getClazzHeapSize(cid);
    String ret = "";
    String hs = (new Integer(heap_size)).toString();
    String nn = getNull(vv.null_num);
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = " + "HeapAllocZ(" + hs + ")");
    ret += getIndentStringln(vv.indent, "[" + t + "] = :vmt_" + cid);
    ret += getIndentStringln(vv.indent, "if " + t + " goto :" + nn);
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, nn + ":");
    vv.incrTNum();
    vv.incrNullNum();
    return ret;
  }

  public String ArrayAccess
  (VaporVisitor vv, String base)
  {
    String ret = "";
    String nn = getNull(vv.null_num);
    String t = getTemp(vv.t_num);
    ret += getIndentStringln(vv.indent, t + " = " + "[" + base + "]");
    ret += getIndentStringln(vv.indent, "if " + t + " goto :" + nn);
    ret += getIndentStringln(vv.indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(vv.indent, nn + ":");
    vv.incrTNum();
    vv.incrNullNum();
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

  public String CheckIndexInRange(VaporVisitor vv, String index)
  {
    String tcur = getTemp(vv.t_num);
    String tprev = getTemp(vv.t_num - 1);
    String bounds = getBound(vv.bounds_num);
    String ret = "";
    ret += getIndentStringln(vv.indent, tcur + " = [" + tprev + "]");
    ret += getIndentStringln(vv.indent, tcur + " = Lt(" + index + " " + tcur + ")");
    ret += getIndentStringln(vv.indent, "if " + tcur + " goto: " + bounds);
    ret += getIndentStringln(vv.indent + 2, "Error(\"array index out of bounds\")");
    ret += getIndentStringln(vv.indent, bounds + ":");
    // do not increment tnum
    vv.incrBoundsNum();
    return ret;
  }

  public String ArrayElementAccess(VaporVisitor vv, String index)
  {
    String tcur = getTemp(vv.t_num);
    String tprev = getTemp(vv.t_num - 1);
    String ret = "";
    ret += getIndentStringln(vv.indent, tcur + " = Muls(" + index + " 4)");
    ret += getIndentStringln(vv.indent, tcur + " = Add(" + tcur + " " + tprev + ")");
    return ret;
  }

  public String ArrayAssignment(VaporVisitor vv, int tn, String r)
  {
    String t = getTemp(tn);
    String ret = "";
    ret += getIndentStringln(vv.indent, "[" + t + "+4] = " + r);
    vv.incrTNum();
    return ret;
  }

  public String ifCondition0(VaporVisitor, vv, String cond)
  {
    String s = "if0 " + cond + " goto :" + getIfElse(vv.if_num);
    return getIndentStringln(vv.indent, s);
  }

  public String ifGoto(VaporVisitor vv)
  {
    return getIndentStringln(vv.indent, "goto :" + getIfEnd(this.if_num));
  }

  public String getWhileTop(int n)
  {
    return "while" + (new Integer(n)).toString() + "_top";
  }

  public String getWhileEnd(int n)
  {
    return "while" + (new Integer(n)).toString() + "_end";
  }

  public String whileCondition(VaporVisitor vv, String cond)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent,
      "if0 " + cond + " goto :" + getWhileEnd(vv.while_num);
    return ret;
  }

  public String print(VaporVisitor vv, String s)
  {
    return getIndentStringln(vv.indent, "PrintIntS(" + s ")");
  }

  // LinkedList.vapor 178 : if0 t.1 goto :ss1_else
  public String andLeft(VaporVisitor vv, String s)
  {
    String ret = "";
    ret += getIndentStringln(vv.indent, "if0 " + s + " goto :" + getSSElse(vv.ss_num));
    return ret;
  }

  public String andGoto(VaporVisit vv)
  {
    return getIndentStringln(vv.indent, "goto :" + getSSEnd(vv.ss_num));
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
    return "ss" + (new Intger(n)).toString() + "_end";
  }

  public Strin getSSSetFalse(int n)
  {
    return getTemp(n) + " = 0";
  }




}