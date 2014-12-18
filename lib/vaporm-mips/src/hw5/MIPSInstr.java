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
import java.util.ArrayList;

public class MIPSInstr
{
  public VFunction[] functions;
  public ArrayList<FunctionInfo> fInfos;
  public int indent;
  public int curFI;
  public StringBuffer codeBuf;

  public MIPSInstr(VFunction[] functions)
  {
    this.functions = functions;
    this.fInfos = new ArrayList<FunctionInfo>();
    this.indent = 2;
    this.curFI = 0;
    this.codeBuf = new StringBuffer();
    buildFunctionInfos();
    buildMIPSInstr();
  }

  public void incrIndent()
  {
    indent = indent + 2;
    return;
  }

  public void decrIndent()
  {
    indent = indent - 2;
    return;
  }

  public void incrFI()
  {
    curFI = curFI + 1;
  }

  public ArrayList<FunctionInfo> getFInfos()
  {
    return this.fInfos;
  }

  public FunctionInfo getCurFunctionInfo()
  {
    return getFInfos().get(curFI);
  }

  public String getIndent()
  {
    String ret = "";
    int i = indent;
    while(i > 0)
    {
      ret += " ";
      i--;
    }
    return ret;
  }

  public boolean noIndentAppendln(String s)
  {
    codeBuf.append(s + "\n");
    return true;
  }

  public boolean append(String s)
  {
    codeBuf.append(getIndent() + s);
    return true;
  }

  public boolean appendln(String s)
  {
    return append(s + "\n");
  }

  public void buildFunctionInfos()
  {
    for(int i = 0; i < functions.length; i++)
    {
      int in = functions[i].stack.in;
      int out = functions[i].stack.out;
      int local = functions[i].stack.local;
      getFInfos().add(new FunctionInfo(in, out, local));
    }

  }

  public void buildMIPSInstr()
  {
    final int numFs = functions.length;
    curFI = 0;
    for(int i = 0; i < numFs; i++)
    {
      String curFName = functions[i].ident;
      noIndentAppendln(curFName + ":");
      appendln("sw $fp -8($sp)");
      appendln("move $fp $sp");
      appendln("subu $sp $sp " + getFInfos().get(i).getStackSpace());
      appendln("sw $ra -4($fp)");
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
          //System.err.println(body[instrIndex].toString());
          body[instrIndex].accept(this, new VisitorB());
          instrIndex++;
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      //System.err.println("");
      appendln("lw $ra -4($fp)");
      appendln("lw $fp -8($fp)");
      appendln("addu $sp $sp " + getFInfos().get(i).getStackSpace());
      appendln("jr $ra");
      appendln("");
      incrFI();
    }
  }

  public String dump()
  {
    return codeBuf.toString();
  }
}