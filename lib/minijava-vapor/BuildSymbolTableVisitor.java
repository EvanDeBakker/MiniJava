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
import visitor.*;
import java.util.*;

public class BuildSymbolTableVisitor extends DepthFirstVisitor
{
  private SymbolTable st;
  private String cur_cid;
  private String cur_mid;
  private Quit quit;

  public BuildSymbolTableVisitor()
  {
    cur_cid = null;
    cur_mid = null;
    st = new SymbolTable();
    quit = new Quit();
  }

  public SymbolTable getSymbolTable()
  {
    return st;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier() // process now
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier() // process recursively
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )* // process recursively
   * f15 -> ( Statement() )* // process recursively
   * f16 -> "}"
   * f17 -> "}"
   */
  @Override
  public void visit(MainClass n)
  {
    if(cur_cid != null || cur_mid != null)
      quit.q();
    String cid = n.f1.f0.toString();
    st.setMainCid(cid);
    // set states
    cur_cid = cid;
    if(!st.addClazz(cid, null))
      quit.q("Cannot add main class");
    Clazz c = st.getClazz(cid);
    if(c == null)
      quit.q();
    String mid = n.f6.toString();
    c.addMeth(mid, null);
    cur_mid = mid;
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);
    n.f13.accept(this);
    n.f14.accept(this);
    n.f15.accept(this);
    n.f16.accept(this);
    n.f17.accept(this);
    // restore states
    cur_cid = null;
    cur_mid = null;
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
  public void visit(ClassDeclaration n)
  {
    if(cur_cid != null || cur_mid != null)
      quit.q();
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    if(!st.addClazz(cid, null))
      quit.q("Cannot add class");
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    // restore states
    cur_cid = null;
    cur_mid = null;
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
  @Override
  public void visit(ClassExtendsDeclaration n)
  {
    if(cur_cid != null || cur_mid != null)
      quit.q();
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    String p = n.f3.f0.toString();
    if(!st.addClazz(cid, p))
      quit.q("Cannot add class(extend)");
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    // restore states
    cur_cid = null;
    cur_mid = null;
  }
  
  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public void visit(VarDeclaration n)
  {
    if(cur_cid == null)
      quit.q();
    Clazz cur_c = st.getClazz(cur_cid);
    String vid = n.f1.f0.toString();
    if(cur_c == null)
      quit.q();
    if(cur_mid == null)
    {
      if(!cur_c.addField(vid, n.f0))
        quit.q("cannot add fieild in class");
    }
    else
    {
      Meth cur_m = cur_c.getMeth(cur_mid);
      if(cur_m == null)
        quit.q("cur_m returns null"); 
      if(!cur_m.addLocalVar(vid, n.f0))
      {
        quit.q("Cannot add local variable in method");
      }
    }
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
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
  public void visit(MethodDeclaration n)
  {
    if(cur_mid != null || cur_cid == null)
      quit.q();
    Clazz cur_c = st.getClazz(cur_cid);
    assert(cur_c != null);
    String mid = n.f2.f0.toString();
    // check if a method with the same is already in the class 
    if(!cur_c.addMeth(mid, n.f1))
      quit.q("Cannot add method in class");
    // change state
    cur_mid = mid;
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    /* After acceptance of f4, methodtype can be derived,
     * which means we should check overloading.
     * but this checking should be performed in STEP 2*/
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);
    // restore state
    cur_mid = null;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public void visit(FormalParameter n)
  {
    if(cur_cid == null || cur_mid == null)
      quit.q();
    Clazz cur_c = st.getClazz(cur_cid);
    if(cur_c == null)
      quit.q();
    Meth cur_m = cur_c.getMeth(cur_mid);
    if(cur_m == null)
      quit.q();
    String para_id = n.f1.f0.toString();
    if(!cur_m.addParameter(para_id, n.f0))
      quit.q("Cannot add paramter in method");
    n.f0.accept(this);
    n.f1.accept(this);
  }
}
