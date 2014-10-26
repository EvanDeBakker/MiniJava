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

import syntaxtree.*
import visitor.*
import java.util.*;

public class BuildSymbolTable extends DepthFirstVisitor
{
  private SymbolTable st;
  private String cur_cid;
  private String cur_mid;

  public BuildSymbolTable()
  {
    cur_cid = null;
    cur_mid = null;
    st = new SymbolTable();
  }

  public SymbolTable getST() {return st;}
  
  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public void visit(NodeList n) 
  {
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
  }

  public void visit(NodeListOptional n) 
  {
    if ( n.present() )
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
        e.nextElement().accept(this);
  }

  public void visit(NodeOptional n) {
    if ( n.present() )
      n.node.accept(this)
  public void visit(NodeSequence n) {
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
  }

  public void visit(NodeToken n) { }

  //
  // User-generated visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public void visit(Goal n) throw TypeCheckingException
  {
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
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
  public void visit(MainClass n) throw TypeCheckingException
  {
    if(cur_cid != null || cur_mid != null)
      throw new TypeCheckingException();
    String cid = n.f1.f0.toString();
    // set states
    cur_cid = cid;
    cur_mid = null;
    if(!st.addClass(cid, null))
    {
      throw new TypeCheckingException();
    }
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
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
  public void visit(TypeDeclaration n) throw TypeCheckingException
  {
    n.f0.accept(this);
  }   

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public void visit(ClassDeclaration n) throw TypeCheckingException 
  {
    if(cur_cid != null || cur_mid != null)
      throw new TypeCheckingException();
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    cur_mid = null;
    if(!st.addClass(cid, null))
    {
      throw new TypeCheckingException();
    }
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
  public void visit(ClassExtendsDeclaration n) throw TypeCheckingException
  {
    if(cur_cid != null || cur_mid != null)
      throw new TypeCheckingException();
    String cid = n.f1.f0.toString();
    cur_cid = cid;
    cur_mid = mid;
    String p = n.f3.f0.toString();
    if(!st.addClass(cid, p))
    {
      throw new TypeCheckingException();
    }
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
  public void visit(VarDeclaration n) throw TypeCheckingException
  {
    if(cur_cid == null)
      throw new TypeCheckingException();
    Class cur_c = st.getClass(cur_cid);
    String vid = n.f1.f0.toString();
    if(cur_c == null)
      throw new TypeCheckingException();
    if(cur_mid == null)
    {
      boolean sucesss = false;
      if(n.f0.f0.which == 0)
        success = cur_c.addVar(vid, new ArrayType());
      else if(n.f0.f0.which == 1)
        sucesss = cur_c.addVar(vid, new BooleanType());
      else if(n.f0.f0.which == 2)
        success = cur_c.addVar(vid, new IntegerType());
      else if(n.f0.f0.which == 3)
        sucesss = cur_c.addVar(vid, new Identifier());
      else
        throw new TypeCheckingException();
      if(!sucesss)
        throw new TypeCheckingException();
    }
    else
    {
      Method cur_m = cur_c.getMethod(cur_mid);
      if(cur_m == null)
        throw new TypeCheckingException();
      if(n.f0.f0.which == 0)
        success = cur_m.addLocalVariable(vid, new ArrayType());
      else if(n.f0.f0.which == 1)
        sucesss = cur_m.addLocalVariable(vid, new BooleanType());
      else if(n.f0.f0.which == 2)
        success = cur_m.addLocalVariable(vid, new IntegerType());
      else if(n.f0.f0.which == 3)
        sucesss = cur_m.addLocalVariable(vid, new Identifier());
      else
        throw new TypeCheckingException();
      if(!sucesss)
        throw new TypeCheckingException();
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
  public void visit(MethodDeclaration n) {
    if(cur_mid != null)
      throw new TypeCheckingException();
    if(cur_cid == null)
      throw new TypeCheckingException();
    Class cur_c = st.getClass(cur_cid);
    if(cur_c == null)
      throw new TypeCheckingException();
    String mid = n.f2.f0.toString();
    /* if this method be added into the current class,
     * then this class and all parent classes should not
     * have this method */
    while(cur_c != null)
    {
      if(cur_c.containsMethod(mid))
        throw new TypeCheckingException();
      else
        cur_c = st.getClass(cur_c.getParentId());
    }
    Type t = null;
    if(n.f1.f0.which == 0)
      t = new ArrayType();
    else if(n.f1.f0.which == 1)
      t = new BooleanType();
    else if(n.f1.f0.which == 2)
      t = new IntegerType();
    else if(n.f1.f0.which == 3)
      t = new IntegerType();
    else
      throw TypeCheckingException();
    cur_c = st.getClass(cur_cid);
    if(!cur_c.addMethod(mid, t))
      throw new TypeCheckingException();

    // change state
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

    // restore state
    cur_mid = null;
  }






  
}






























