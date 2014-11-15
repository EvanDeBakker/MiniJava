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

import java.io.*;
import java.util.*;
import java.lang.*;
import visitor.*;
import syntaxtree.*;

public class Typecheck
{
  public static void main(String [] args)
  {
    try
    {
      Node root = new MiniJavaParser(System.in).Goal();
      BuildSymbolTableVisitor bstv = new BuildSymbolTableVisitor();
      root.accept(bstv);
      SymbolTable st = bstv.getSymbolTable();
      TypeCheckingVisitor tcv = new TypeCheckingVisitor(st);
      root.accept(tcv, null);
      System.out.println("Program type checked successfully");
    }
    catch(ParseException e)
    {
      System.out.println("Parsing error");
    }
  }
}
    
