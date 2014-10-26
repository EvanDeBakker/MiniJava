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
      BuildSymbolTableVisitor vst = new BuildSymbolTableVisitor();
      TypeCheckingVisitor vtc = new TypeCheckingVisitor();
      root.accept(vst);
      SymbolTable st = vst.getSymbolTable();
      root.accept(vtc, st);
      System.out.println("Program type checked successfully");
    }
    catch(ParseException e)
    {
      System.out.println("Type error");
    }
    catch(TypeCheckingException e)
    {
      System.out.println("Type error");
    }
  }
}
    
