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
  private HashMap<Object, Class> hm;

  public SymbolTable()
  {
    hm = new HashMap<Object, Class>();
  }

  public boolean containsClass(String id)
  {
    return hm.containsKey(id);
  }

  public boolean addClass(String id, String p)
  {
    if(containsClass(id))
      return false;
    else
    {
      hm.put(id, new Class(id, p));
      return true;
    }
  }

  public Class getClass(String id)
  {
    return hm.get(id); 
  }


  public Method getMethod(String cid, String mid)
  {
    Class c = getClass(cid);
    while(c != null)
    {
      Method m = c.getMethod(mid);
      if(m != null)
        return m;
      else
      {
        if(c.getParentId() != null)
          c = getClass(c.getParentId());
        else
          c = null;
      }
    }
    return null;
  }

  public Type getMethodType(String cid, String mid)
  {
    Method m = getMethod(cid, mid);
    if(m == null)
      return null;
    else
      return m.getType();
  }

  public Variable getMVariable(String cid, String mid, String vid)
  {
    Class c = getClass(cid);
    if(c == null)
      return null;
    Method m = c.getMethod(mid);
    if(m == null)
      return null;
    Variable v = m.getParameter(vid);
    if(v != null)
      return v;
    else
      return m.getLocalVariable(vid);
  }

  public Type getMVariableType(String cid, String mid, String vid)
  {
    Variable v = getMVariable(cid, mid, vid);
    if(v == null)
      return null;
    else
      return v.getType();
  }

  public Variable getCVariable(String cid, String vid)
  {
    Class c = getClass(cid);
    while(c != null)
    {
      Variable v = c.getVar(vid);
      if(v != null)
        return v;
      else
      {
        if(c.getParentId() != null)
          c = getClass(c.getParentId());
        else
          c = null;
      }
    }
    return null;
  }

  public Type getCVariableType(String cid, String vid)
  {
    Variable v = getCVariable(cid, vid);
    if(v == null)
      return null;
    else
      return v.getType();
  }

  public class Class
  {
    public String id;
    public String parent_id;
    public HashMap<Object, Method> methods;
    public HashMap<Object, Variable> variables;
  
    public Class(String id, String parent_id)
    {
      this.id = id;
      parent_id = parent_id;
      methods = new HashMap<Object, Method>();
      variables = new HashMap<Object, Variable>();
    }
  
    public String getId() {return id;}
  
    public String getParentId() {return parent_id;}
  
    // methods
    public boolean containsMethod(String id)
    {
      return methods.containsKey(id);
    }
  
    public boolean addMethod(String id, Type t)
    {
      if(containsMethod(id))
        return false;
      else
      {
        methods.put(id, new Method(id, t));
        return true;
      }
    }
  
    public Method getMethod(String id)
    {
      return methods.get(id);
    }
  
    // variables
    public boolean containsVar(String id)
    {
      return variables.containsKey(id);
    }
  
    public boolean addVar(String id, Type t)
    {
      if(containsVar(id))
        return false;
      else
      {
        variables.put(id, new Variable(id, t));
        return true;
      }
    }
  
    public Variable getVar(String id)
    {
      return variables.get(id);
    }
  
  }
  
  public class Method
  {
    public String id;
    public Type type;
    public ArrayList<Variable> parameters;
    public HashMap<Object, Variable> local_vars;
  
    public Method(String id, Type t)
    {
      this.id = id;
      this.type = t;
      parameters = new HashMap<Object, Variable>();
      local_vars = new HashMap<Object, Variable>();
    }
  
    public String getId() {return id;}
  
    public Type getType() {return type;}
  
    // parameters
    public boolean containsPara(String id)
    {
      for(Variable v : parameters)
      {
        if(v != null)
          if(v.getId().equal(id))
            return true;
      }
      return false;
    }
  
    public boolean addParameter(String id, Type t)
    {
      if(containsPara(id))
        return false;
      else
      {
        parameters.add(new Variable(id, t));
        return true;
      }
    }
  
    public Variable getParameter(String id)
    {
      for(Variable v : parameters)
      {
        if(v != null)
          if(v.getId().equal(id))
            return v;
      }
      return null;
    }
  
    // local variables
    public boolean containslv(String id)
    {
      return (containsPara(id) || local_vars.containsKey(id));
    }
  
    public boolean addLocalVariable(String id, Type t)
    {
      if(containslv(id))
        return false;
      else
      {
        local_vars.put(id, new Variable(id, t));
        return true; 
      }
    }
  
    public Variable getLocalVariable(String id)
    {
      return parameters.get(id);
    }
  
  }
  
  // Variable
  public class Variable
  {
    public String id;
    public Type type;
  
    public Variable(String id, Type t)
    {
      this.id = id;
      this.type = t;
    }
  
    public String getId() {return id;}
  
    public Type getType() {return type;}
  }
}
