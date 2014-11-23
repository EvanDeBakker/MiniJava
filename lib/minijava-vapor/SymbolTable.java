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

import syntaxtree.*;
import java.util.*;

public class SymbolTable
{
  public HashMap<String, Clazz> hm;
  public ArrayList<Clazz> clazz_list;
  public Quit quit;
  public String main_cid;


  public SymbolTable()
  {
    hm = new HashMap<String, Clazz>();
    quit = new Quit();
    clazz_list = new ArrayList<Clazz>();
    main_cid = null;
  }

  public boolean containsClazz(String id)
  {
    return hm.containsKey(id);
  }

  public boolean addClazz(String id, String p)
  {
    if(containsClazz(id))
      return false;
    else
    {
      Clazz c = new Clazz(id, p);
      hm.put(id, c);
      clazz_list.add(c);
      return true;
    }
  }

  public boolean setMainCid(String m)
  {
    this.main_cid = m;
    return true;
  }

  public String getMainCid()
  {
    return this.main_cid;
  }


  public Clazz getClazz(String id)
  {
    return hm.get(id); 
  }

  public ArrayList<Clazz> getClazzList() {return this.clazz_list;}

  // used when looking for method in the target class 
  // and its parent class
  public Meth getMeth(String cid, String mid)
  {
    Clazz c = getClazz(cid);
    while(c != null)
    {
      Meth m = c.getMeth(mid);
      if(m != null)
        return m;
      else
      {
        if(c.getParentId() != null)
          c = getClazz(c.getParentId());
        else
          c = null;
      }
    }
    return null;
  }

  public Type getLocalVarType(String cid, String mid, String vid)
  {
    Meth m = getMeth(cid, mid);
    assert(m != null);
    Var v = m.getLocalVar(vid);
    if(v == null)
      v = m.getParameter(vid);
    assert(v != null);
    return v.getType();
  }

  // used when looking for method in the target class 
  // and its parent class
  public Var getCField(String cid, String vid)
  {
    Clazz c = getClazz(cid);
    while(c != null)
    {
      Var v = c.getField(vid);
      if(v != null)
        return v;
      else
      {
        if(c.getParentId() != null)
          c = getClazz(c.getParentId());
        else
          c = null;
      }
    }
    return null;
  }


  public void indentPrint(int i, String s)
  {
    String blank = "* ";
    while(i != 0)
    {
      blank += " ";
      i--;
    }
    System.out.print(blank + s);
  }

  // Pretty printer used to debug
  public String StringOfType(Type a)
  {
    if(a == null)
      return "void";
    Node at = a.f0.choice;
    if(at instanceof ArrayType)
      return "Array";
    if(at instanceof BooleanType)
      return "Boolean";
    if(at instanceof IntegerType)
      return "Integer";
    if(at instanceof Identifier)
    {
      String aid = ((Identifier)at).f0.toString();
      assert(aid != null);
      return aid;
    }
    else
      return null;
  }

  public String MethodReturnTypeToString(Type a)
  {
    assert(a != null);
    Node at = a.f0.choice;
    if(at instanceof Identifier)
    {
      String aid = ((Identifier)at).f0.toString();
      return aid;
    }
    else
      return null;
  }

  public void printSingleMethod(int i, Meth m)
  {
    String output = "";
    output += m.getId() + " : ";
    for(Var v : m.getParameters())
      output += StringOfType(v.getType()) + " -> ";
    output += StringOfType(m.getType());
    indentPrint(i, output + "\n");
  }

  public void printSingleVar(int i, Var v)
  {
    String output = "";
    output += v.getId() + " : ";
    output += StringOfType(v.getType()) + "\n";
    indentPrint(i, output);
  }

  public void prettyPrinter()
  {
    System.out.println("*****************************************************");
    for(Clazz c : this.clazz_list)
    {
      String pid = c.getParentId();
      if(pid == null)
        indentPrint(0, "Class " + c.getId() + "\n");
      else
        indentPrint(0, "Class " + c.getId() +
                    " extends " + pid + "\n");
      indentPrint(2, "Fields:\n");
      for(Var v : c.getFieldList())
        printSingleVar(4, v);
      indentPrint(2, "Methods:\n");
      for(Meth m : c.getMethList())
        printSingleMethod(4, m);
      indentPrint(2, "Heap allocation size: " + calcHeapSize(c) + "\n");
    }
    System.out.println("*****************************************************");
  }


  public int calcHeapSize(Clazz c)
  {
    return (c.getFieldList().size() + 1);
  }

  public String StringOfId(Identifier id)
  {
    return id.f0.toString();
  }

  public boolean isField(String cid, String mid, String vid)
  {
    Clazz c = this.getClazz(cid);
    Meth m = c.getMeth(mid);
    return !m.containsLocalVariableOrParameter(vid);
  }

}
