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
  public Quit quit;

  public SymbolTable()
  {
    hm = new HashMap<String, Clazz>();
    quit = new Quit();
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
      hm.put(id, new Clazz(id, p));
      return true;
    }
  }

  public Clazz getClazz(String id)
  {
    return hm.get(id); 
  }

  // `Deprecated
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

  public Var getMVar(String cid, String mid, String vid)
  {
    Clazz c = getClazz(cid);
    if(c == null)
      return null;
    Meth m = c.getMeth(mid);
    if(m == null)
      return null;
    Var v = m.getParameter(vid);
    if(v != null)
      return v;
    else
      return m.getLocalVar(vid);
  }

  public Type getMVarType(String cid, String mid, String vid)
  {
    Var v = getMVar(cid, mid, vid);
    if(v == null)
      return null;
    else
      return v.getType();
  }

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

  public Type getCFieldType(String cid, String vid)
  {
    Var v = getCField(cid, vid);
    if(v == null)
      return null;
    else
      return v.getType();
  }

  // should be performed only after symbol table is built up
  public void overallNoOverLoading()
  {

    for(String clazz_name : hm.keySet())
      for(String meth_name : this.getClazz(clazz_name).methods.keySet())
        singleNoOverLoading(clazz_name, meth_name);
  }

  // should be performed only after symbol table is built up
  public void singleNoOverLoading(String cid, String mid)
  {
    assert(cid != null && mid != null);
    Meth cur_method = this.getMeth(cid, mid); // could be safely used
    assert(cur_method != null);
    String parent_cid = this.getClazz(cid).getParentId();
    while(parent_cid != null)
    {
      Clazz parent_clazz = this.getClazz(parent_cid);
      if(parent_clazz == null)
        quit.q();
      Meth parent_method = parent_clazz.getMeth(mid);
      if(parent_method != null)
        if(!compareMethodType(parent_method, cur_method))
        {
          quit.q("Overloading detected : " + cid + "." + mid + " <> "
                 + parent_cid + "." + parent_method);
        }
      parent_cid = this.getClazz(parent_cid).getParentId();
    }
  }

  // should be performed only after symbol table is built up
  public boolean compareMethodType(Meth m, Meth n)
  {
    assert(m != null && n != null);
    ArrayList<Var> m_para = m.getParameters();
    ArrayList<Var> n_para = n.getParameters();
    assert(m_para != null && n_para != null);
    if(m_para.size() != n_para.size())
      return false;
    for(int i = 0; i < m_para.size(); i++)
    {
      if(!sameType(m_para.get(i).getType(), n_para.get(i).getType()))
        return false;
    }
    return sameType(m.getType(), n.getType());
  }
  

  // Return true if a is b's subtype
  // If [s] is true, we don't go further down to check subtype, which means
  // only when two types are exactly the same, the function returns true
  public boolean subTyping(Node at, Node bt, boolean s)
  {
    assert(at != null && bt != null);
    if(at instanceof ArrayType && bt instanceof ArrayType)
      return true;
    else if(at instanceof BooleanType && bt instanceof BooleanType)
      return true;
    else if(at instanceof IntegerType && bt instanceof IntegerType)
      return true;
    else if(at instanceof ArrayType && bt instanceof ArrayType)
      return true;
    else if(at instanceof Identifier && bt instanceof Identifier)
    {
      ArrayList<String> atset = new ArrayList<String>();
      String aid = ((Identifier)at).f0.toString();
      String bid = ((Identifier)bt).f0.toString();
      assert(aid != null && bid != null);
      if(s)
        return aid.equals(bid);
      else
      {
        while(aid != null)
        {
          atset.add(aid);
          Clazz c = this.getClazz(aid);
          if(c == null)
            quit.q("Unexpected Error");
          aid = c.getParentId();
        }
        for(String m : atset)
        {
          if(m.equals(bid))
            return true;
        }
        return false;
      }
    }
    else
     return false;
  }

  // should be performed only after symbol table is built up
  // Note: only if a and b have exactly the same type, the function
  // return true. Subtyping is not concerned here
  public boolean sameType(Type a, Type b)
  {
    assert(a != null && b != null);
    Node at = a.f0.choice, bt = b.f0.choice;
    if(at instanceof ArrayType && bt instanceof ArrayType)
      return true;
    else if(at instanceof BooleanType && bt instanceof BooleanType)
      return true;
    else if(at instanceof IntegerType && bt instanceof IntegerType)
      return true;
    else if(at instanceof ArrayType && bt instanceof ArrayType)
      return true;
    else if(at instanceof Identifier && bt instanceof Identifier)
    {
      String aid = ((Identifier)at).f0.toString();
      String bid = ((Identifier)bt).f0.toString();
      assert(aid != null && bid != null);
      return aid.equals(bid);
    }
    else
      return false;
  }

  // should be performed only after symbol table is built up
  public void overallAcyclic()
  {
    for(String clazz_name : hm.keySet())
      if(!singleAcyclic(clazz_name))
        quit.q("Acyclic detected : " + clazz_name);
  }

  public boolean singleAcyclic(String cid)
  {
    assert(cid != null);
    Clazz cur_clazz = this.getClazz(cid);
    assert(cur_clazz != null);
    String parent_cid = cur_clazz.getParentId();
    while(parent_cid != null)
    {
      if(cid.equals(parent_cid))
        return false;
      Clazz parent_clazz = this.getClazz(parent_cid);
      if(parent_clazz == null)
        quit.q(cid + " cannot extend non-existent class " + parent_cid);
      else
        parent_cid = parent_clazz.getParentId();
    }
    return true;
  }

  public boolean allClassParentExists()
  {
    for(String clazz_name : hm.keySet())
    {
      Clazz cur_clazz = this.getClazz(clazz_name);
      if(cur_clazz == null)
        quit.q();
      String parent_class_id = cur_clazz.getParentId();
      if(parent_class_id != null)
        if(this.getClazz(parent_class_id) == null)
          quit.q(clazz_name + " 's parent" + parent_class_id 
                 + " does not exist");
    }
    return true;
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
  public void printSingleMethod(int i, Meth m)
  {
    String output = "";
    output += m.getId() + " : ";
    for(Var v : m.getParameters())
      output += StringOfType(v.getType()) + " -> ";
    output += StringOfType(m.getType());
    indentPrint(i, output + "\n");

  }
  public void prettyPrinter()
  {
    System.out.println("*****************************************************");
    for(String clazz_name : hm.keySet())
    {
      Clazz cur_clazz = this.getClazz(clazz_name);
      String pid = cur_clazz.getParentId();
      if(pid == null)
        indentPrint(0, "Class " + cur_clazz.getId() + "\n");
      else
        indentPrint(0, "Class " + cur_clazz.getId() + 
                    " extends " + pid + "\n");
      for(String method : cur_clazz.methods.keySet())
        printSingleMethod(2, cur_clazz.getMeth(method));
    }
    System.out.println("*****************************************************");
  }

  public Node getNodeFromType(Type rt)
  {
    if(rt == null)
      quit.q("wrong");
    if(rt.f0.choice instanceof IntegerType)
      return new IntegerType();
    if(rt.f0.choice instanceof BooleanType)
      return new BooleanType();
    if(rt.f0.choice instanceof ArrayType)
      return new ArrayType();
    if(rt.f0.choice instanceof Identifier)
      return rt.f0.choice;
    else
      return null;
  }
}
