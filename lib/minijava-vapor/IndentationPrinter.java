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

  /*
   * t.0 = HeapAllocZ(4)
   * [t.0] = :vmt_Fac
   * if t.0 goto :null1
   *   Error("null pointer")
   * null1:
   */
  public String getObjectAllocString
  (VaporVisitor vv, String cid, int indent, int hsize, int tnum, int nnum)
  {
    String ret = "";
    String hs = (new Integer(hsize)).toString();
    String tn = (new Integer(tnum)).toString();
    String nn = (new Integer(nnum)).toString();
    String t = "t." + tn;
    ret += getIndentStringln(indent, t + " = " + "HeapAllocZ(" + hs + ")");
    ret += getIndentStringln(indent, "[" + t + "] = :vmt_" + cid);
    ret += getIndentStringln(indent, "if " + t + " goto :null" + nn);
    ret += getIndentStringln(indent + 2, "Error(\"null pointer\")");
    ret += getIndentStringln(indent, "null" + nn + ":");
    vv.incrTNum();
    vv.incrNullNum();
    return ret;
  }

}