import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class Functions
{
  public VFunction[] functions;
  public FunctionStack[] functionStacks;
  public boolean debug;
  public int indent;
  public int currentFunctionIndex;
  public StringBuffer codeBuf;


  public Functions(VFunction[] functions, boolean debug)
  {
    this.functions = functions;
    this.debug = debug;
    this.indent = 2;
    this.currentFunctionIndex = 0;
    this.codeBuf = new StringBuffer();
    inAndLocalAnalysis();
    buildOut();
    buildFuncInternal();
  }

  public FunctionStack getFS(int i)
  {
    return functionStacks[i];
  }

  public FunctionStack getCurFS()
  {
    return getFS(getCurrentFunctionIndex());
  }

  public String getIndent()
  {
    String ret = "";
    int i = indent;
    while(i >= 0)
    {
      ret += " ";
      i--;
    }
    return ret;
  }

  public boolean incrIndent()
  {
    indent += 2;
    return true;
  }

  public boolean decrIndent()
  {
    indent -= 2;
    return true;
  }

  public boolean isDebug()
  {
    return this.debug;
  }

  public StringBuffer getCodeBuf()
  {
    return this.codeBuf;
  }

  public int getCurrentFunctionIndex()
  {
    return currentFunctionIndex;
  }

  public boolean incrFI()
  {
    currentFunctionIndex++;
    return true;
  }

  public void inAndLocalAnalysis()
  {
    int numOfFunctions = functions.length;
    functionStacks = new FunctionStack[numOfFunctions];
    for(int i = 0; i < numOfFunctions; i++)
    {
      functionStacks[i] = new FunctionStack();
      VVarRef.Local[] parameters = functions[i].params;
      for(int j = 0; j < parameters.length; j++) {
        functionStacks[i].putIn(parameters[j].ident, "in[" + j + "]");
        //System.out.println("in: " + parameters[j].ident);
      }
      String[] localVars = functions[i].vars;
      int localNum = 0;
      for(int j = 0; j < localVars.length; j++)
      {
        if(functionStacks[i].putLocal(localVars[j], "local[" + localNum + "]"))
        {
          //System.out.println("Add local: " + localVars[j]);
          localNum++;
        }
      }
    }
  }

  public void buildOut()
  {
    for(int i = 0; i <  functions.length; i++)
    {
      VInstr[] body = functions[i].body;
      for(int j = 0; j < body.length; j++)
      {
        try
        {
          body[j].accept(this, new OutVisitor());
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      incrFI();
    }
    if(false)
    {
      for(int i = 0; i < functionStacks.length; i++)
      {
        System.out.println("Function " + i + " 's out : " + functionStacks[i].out);
      }
    }
  }

  public String queryId(String id)
  {
    String r = this.getCurFS().getIn(id);
    if(r == null)
      r = this.getCurFS().getLocal(id);
    if(r == null)
      r = id;
    return r;
  }

  public void buildFuncInternal()
  {
    currentFunctionIndex = 0;
    final int numFunctions = functions.length;
    int i = 0;
    while(i < numFunctions)
    {
      String curFunctionName = functions[i].ident;
      codeBuf.append("func " + curFunctionName);
      int numLocal = functions[i].vars.length;
      int numIn = functions[i].params.length;
      int numOut = getCurFS().getOut();
      codeBuf.append(" [in " + numIn + ", out " + numOut + ", local " + numLocal + "]\n");
      // start printing main body
      VInstr[] body = functions[i].body;
      VCodeLabel[] codeLabels = functions[i].labels;
      int instrIndex = 0, codeLabelIndex = 0;
      while(instrIndex <  body.length)
      {
        // copy over code labels
        while(codeLabelIndex < codeLabels.length &&
          codeLabels[codeLabelIndex].instrIndex == instrIndex)
        {
          codeBuf.append(codeLabels[codeLabelIndex].ident + ":\n");
          codeLabelIndex++;
        }
        // real code gen
        try
        {
          body[instrIndex].accept(this, new VisitorB());
          instrIndex++;
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
      codeBuf.append("\n");
      incrFI();
      i++;
    }
  }

  public String dump()
  {
    return codeBuf.toString() + "\n";
  }
}