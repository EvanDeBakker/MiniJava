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


public class Clazz
{
  public String id;
  public String parent_id;
  public HashMap<String, Meth> methods;
  public HashMap<String, Var> fields;

  public Clazz(String id, String parent_id)
  {
    this.id = id;
    this.parent_id = parent_id;
    methods = new HashMap<String, Meth>();
    fields = new HashMap<String, Var>();
  }

  public String getId() {return id;}

  public String getParentId() 
  {
    return parent_id;
  }

  // methods
  public boolean containsMeth(String id)
  {
    return methods.containsKey(id);
  }

  public boolean addMeth(String id, Type t)
  {
    if(containsMeth(id))
      return false;
    else
    {
      methods.put(id, new Meth(id, t));
      return true;
    }
  }

  public Meth getMeth(String id)
  {
    return methods.get(id);
  }

  // variables
  public boolean containsField(String id)
  {
    return fields.containsKey(id);
  }

  public boolean addField(String id, Type t)
  {
    if(containsField(id))
      return false;
    else
    {
      fields.put(id, new Var(id, t));
      return true;
    }
  }

  public Var getField(String id)
  {
    return fields.get(id);
  }
}