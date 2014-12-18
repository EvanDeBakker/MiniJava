import cs132.vapor.ast.VInstr.VisitorP;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VReturn;

import java.util.HashMap;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRef.Stack;
import cs132.vapor.ast.VMemRef.Stack.Region;
import cs132.vapor.ast.VOperand;

public class VisitorB extends VisitorP<MIPSInstr, Exception>
{
  public void visit(MIPSInstr m, VAssign a) throws Exception
  {
    String destId = a.dest.toString();
    String sourceId = a.source.toString();
    if(sourceId.charAt(0) == '$')
      m.appendln("move " + destId + " " + sourceId);
    else if(sourceId.charAt(0) == ':')
    {
      String addrId = sourceId.substring(1, sourceId.length());
      m.appendln("la " + destId + " " + addrId);
    }
    else
      m.appendln("li " + destId + " " + sourceId);
    return;
  }
  public void visit(MIPSInstr m, VCall c) throws Exception
  {
    String faddrId = c.addr.toString();
    if(faddrId.charAt(0) == ':')
    {
      faddrId = faddrId.substring(1, faddrId.length());
      m.appendln("jal " + faddrId);
    }
    else
      m.appendln("jalr " + faddrId);
    return;
  }
  public void visit(MIPSInstr m, VBuiltIn c) throws Exception
  {
    String opName = c.op.name;
    if(opName.equals("Add"))
    {
      String mipsInstrName = "addu";
      String mipsInstrNameI = "addi";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      String destName = c.dest.toString();
      if(argAName.charAt(0) == '$')
      {
        if(argBName.charAt(0) == '$')
        {
          m.appendln(mipsInstrName + " " + destName + " " + argAName +
            " " + argBName);
        }
        else
        {
          m.appendln(mipsInstrNameI + " " + destName + " " + argAName +
            " " + argBName);
        }
      }
      else
      {
        m.appendln("li $t9 " + argAName);
        if(argBName.charAt(0) == '$')
        {
          m.appendln(mipsInstrName + " " + destName + " $t9 " + argBName);
        }
        else
        {
          m.appendln(mipsInstrNameI + " " + destName + "$t9 " + argBName);
        }
      }
    }
    else if(opName.equals("Sub"))
    {
      String mipsInstrName = "subu";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      String destName = c.dest.toString();
      if(argAName.charAt(0) == '$')
        m.appendln(mipsInstrName + " " + destName + " " + argAName +
          " " + argBName);
      else
      {
        m.appendln("li $t9 " + argAName);
        m.appendln(mipsInstrName + " " + destName + " $t9 " + argBName);
      }
    }
    else if(opName.equals("MulS"))
    {
      String mipsInstrName = "mul";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      String destName = c.dest.toString();
      if(argAName.charAt(0) == '$')
        m.appendln(mipsInstrName + " " + destName + " " + argAName +
          " " + argBName);
      else
      {
        m.appendln("li $t9 " + argAName);
        m.appendln(mipsInstrName + " " + destName + " $t9 " + argBName);
      }

    }
    else if(opName.equals("Eq"))
    {
      String seqInstrName = "seq";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      m.appendln(seqInstrName + " " + argAName + " " + argBName);
    }
    else if(opName.equals("Lt"))
    {
      String mipsInstrName = "sltu";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      String destName = c.dest.toString();
      if(argAName.charAt(0) == '$' && argBName.charAt(0) == '$')
      {
        m.appendln(mipsInstrName + " " + destName + " " + argAName +
          " " + argBName);
      }
      else if(argAName.charAt(0) == '$' && argBName.charAt(0) != '$')
      {
        m.appendln("li $t9 " + argBName);
        m.appendln(mipsInstrName + " " + destName + " " + argAName + " $t9");
      }
      else if(argAName.charAt(0) != '$' && argBName.charAt(0) == '$')
      {
        m.appendln("li $t9 " + argAName);
        m.appendln(mipsInstrName + " " + destName + " $t9 " + argBName);
      }
      else
      {
        int left = Integer.parseInt(argAName);
        int right = Integer.parseInt(argBName);
        if(left <  right)
          m.appendln("li " + destName + " 1");
        else
          m.appendln("li " + destName + " 0");
      }
    }
    else if(opName.equals("LtS"))
    {
      String mipsInstrNameI = "slti";
      String mipsInstrNameR = "slt";
      String argAName = c.args[0].toString();
      String argBName = c.args[1].toString();
      String destName = c.dest.toString();
      if(argBName.charAt(0) == '$')
      {
        if(argAName.charAt(0) == '$')
          m.appendln(mipsInstrNameR + " " + destName + " " + argAName +
            " " + argBName);
        else
        {
          m.appendln("li $t9 " + argAName);
          m.appendln(mipsInstrNameR + " " + destName + " $t9 " + argBName);
        }
      }
      else
      {
        if(argAName.charAt(0) == '$')
          m.appendln(mipsInstrNameI + " " + destName + " " + argAName +
            " " + argBName);
        else
        {
          m.appendln("li $t9 " + argAName);
          m.appendln(mipsInstrNameI + " " + destName + " $t9 " + argBName);
        }
      }
    }
    else if(opName.equals("PrintIntS"))
    {
      String argName = c.args[0].toString();
      String mipsInstrName = "jal _print";
      if(argName.charAt(0) != '$')
        m.appendln("li $a0 " + argName);
      else
        m.appendln("move $a0 " + argName);
      m.appendln(mipsInstrName);
    }
    else if(opName.equals("HeapAllocZ"))
    {
      String argName = c.args[0].toString();
      String destName = c.dest.toString();
      String mipsInstrName = "jal _heapAlloc";
      if(argName.charAt(0) != '$')
        m.appendln("li $a0 " + argName);
      else
        m.appendln("move $a0 " + argName);
      m.appendln(mipsInstrName);
      m.appendln("move " + destName + " $v0");
    }
    else if(opName.equals("Error"))
    {
      String argName = c.args[0].toString();
      String mipsInstrName = "j _error";
      if(argName.charAt(1) == 'n')
        m.appendln("la $a0 _str0");
      else
        m.appendln("la $a0 _str1");
      m.appendln(mipsInstrName);
    }
  }

  // dest could be VMemRef.Global and VMemRef.Stack
  // source is just VOperand
  public void visit(MIPSInstr m, VMemWrite w) throws Exception
  {
    if(w.dest instanceof Global)
    {
      Global dest = (Global)w.dest;
      String baseId = dest.base.toString();
      String sourceId = w.source.toString();
      int byteOffset = dest.byteOffset;
      // if source is mem address, like :vm_Fac
      if(sourceId.charAt(0) == ':')
      {
        String v = sourceId.substring(1, sourceId.length());
        m.appendln("la $t9 " + v);
        m.appendln("sw $t9 " + byteOffset + "(" + baseId + ")");
      }
      // if source is a register
      else if(sourceId.charAt(0) == '$')
      {
        m.appendln("sw " + sourceId + " " + byteOffset + "(" + baseId + ")");
      }
      // if source is an intermediate value
      else
      {
        m.appendln("li $t9 " + sourceId);
        m.appendln("sw $t9 " + byteOffset + "(" + baseId + ")");
      }
    }
    else
    {
      Stack dest = (Stack)w.dest;
      String sourceId = w.source.toString();
      int stackIndex = 4 * dest.index;
      if(dest.region == Region.Out)
      {
        if(sourceId.charAt(0) == '$')
          m.appendln("sw " + sourceId + " " + stackIndex + "($sp)");
        else
        {
          m.appendln("li $t9 " + sourceId);
          m.appendln("sw $t9 " + stackIndex + "($sp)");
        }
      }
      else if(dest.region == Region.Local)
      {
        int numOfOut = m.getCurFunctionInfo().getNumOut();
        int newStackIndex = numOfOut * 4 + stackIndex;
        if(sourceId.charAt(0) == '$')
        {
          m.appendln("sw " + sourceId + " " + newStackIndex + "($sp)");
        }
        else
        {
          m.appendln("li $t9 " + sourceId);
          m.appendln("sw $t9 " + newStackIndex + "($t9)");
        }
      }
      else
      {
        if(sourceId.charAt(0) == '$')
        {
          m.appendln("sw " + sourceId + " " + stackIndex + "($fp)");
        }
        else
        {
          m.appendln("li $t9 " + sourceId);
          m.appendln("sw $t9 " + stackIndex + "($fp)");
        }
      }
    }
  }

  // r.dest : VVarRef.Register
  // r.source : VMemRef.Global and VMemRef.Stack
  public void visit(MIPSInstr m, VMemRead r) throws Exception
  {
    String destId = r.dest.toString();
    if(r.source instanceof Global)
    {
      Global source =  (Global)r.source;
      String baseId = source.base.toString();
      int byteOffset = source.byteOffset;
      m.appendln("lw " + destId + " " + byteOffset + "(" + baseId + ")");
    }
    else
    {
      Stack s = (Stack)r.source;
      int stackIndex = 4 * s.index;
      if(s.region == Region.Out)
      {
        m.appendln("lw " + destId + " " + stackIndex + "($sp)");
      }
      else if(s.region == Region.Local)
      {
        int numOfOut = m.getCurFunctionInfo().getNumOut();
        int newStackIndex = stackIndex + 4 * numOfOut;
        m.appendln("lw " + destId + " " + newStackIndex + "($sp)");
      }
      else
      {
        m.appendln("lw " + destId + " " + stackIndex + "($fp)");
      }
    }
  }
  public void visit(MIPSInstr m, VBranch b) throws Exception
  {
    String target = b.target.ident;
    String branchOn = b.value.toString();
    if(b.positive)
      m.appendln("bnez " + branchOn + " " + target);
    else
      m.appendln("beqz " + branchOn + " " + target);
  }
  public void visit(MIPSInstr m, VGoto g) throws Exception
  {
    String target = g.target.toString();
    String t = target.substring(1, target.length());
    m.appendln("j " + t);
  }
  public void visit(MIPSInstr m, VReturn r) throws Exception
  {
    return;
  }
}