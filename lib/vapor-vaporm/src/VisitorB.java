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
import cs132.vapor.ast.VVarRef.Local;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VOperand;


public class VisitorB extends VisitorP<Functions, Exception>
{
  public void visit(Functions fs, VAssign a) throws Exception
  {
    String indentation = fs.getIndent();
    String destId = a.dest.toString();
    String destR = fs.queryId(destId);
    String sourceId = a.source.toString();
    String sourceR = fs.queryId(sourceId);
    //fs.getCodeBuf().append("Start VAssign\n");
    fs.getCodeBuf().append(indentation + "$t0 = " + sourceR + "\n");
    fs.getCodeBuf().append(indentation + destR + " = $t0\n");
  }

  public void visit(Functions fs, VCall c) throws Exception
  {
    // load value to out stack
    for(int argIndex = 0; argIndex < c.args.length; argIndex++)
    {
      String argId = c.args[argIndex].toString();
      String argR = fs.getCurFS().getIn(argId);
      if (argR == null)
        argR = fs.getCurFS().getLocal(argId);
      if (argR == null)
      {
        argR = argId;
        fs.getCodeBuf().append(fs.getIndent() + "out[" + argIndex +
          "] = " + argR + "\n");
      }
      else
      {
        fs.getCodeBuf().append(fs.getIndent() + "$v0" +
          " = " + argR + "\n");
        fs.getCodeBuf().append(fs.getIndent() + "out[" + argIndex +
          "] = $v0" + "\n");
      }
    }
    String addrId = c.addr.toString();
    String addrR = fs.queryId(addrId);
    fs.getCodeBuf().append(fs.getIndent() + "$t0 = " + addrR + "\n");
    fs.getCodeBuf().append(fs.getIndent() + "call $t0\n");
    // could be null
    if(c.dest != null)
    {
      String destId = c.dest.toString();
      String destR = fs.queryId(destId);
      fs.getCodeBuf().append(fs.getIndent() + destR + " = $v0\n");
    }
  }
  public void visit(Functions fs, VBuiltIn c) throws Exception
  {
    String indent = fs.getIndent();
    int numParameters = c.op.numParams;
    String operation = c.op.name;
    // for heap alloc, etc
    if(numParameters == 1)
    {
      String id = c.args[0].toString();
      String var = fs.queryId(id);
      // load from stack
      fs.getCodeBuf().append(indent + "$t0 = " + var + "\n");
      String dest = null;
      // could be null
      if(c.dest != null)
        dest = c.dest.toString();
      if(dest == null)
        fs.getCodeBuf().append(indent + operation + "($t0)\n");
      else
      {
        String destR = fs.getCurFS().getLocal(dest);
        if (destR == null)
          destR = fs.getCurFS().getIn(dest);
        if (destR != null) {
          fs.getCodeBuf().append(indent + "$t1 = " + operation + "($t0)\n");
          fs.getCodeBuf().append(indent + destR + " = $t1\n");
        }
      }
    }
    else if(numParameters == 2)
    {
      String ida = c.args[0].toString();
      String vara = fs.queryId(ida);
      String idb = c.args[1].toString();
      String varb = fs.queryId(idb);
      fs.getCodeBuf().append(indent + "$t0 = " + vara + "\n");
      fs.getCodeBuf().append(indent + "$t1 = " + varb + "\n");
      fs.getCodeBuf().append(indent + "$t2 = " + operation + "($t0 $t1)\n");
      String dest = null;
      if(c.dest != null)
        dest = c.dest.toString();
      if(dest != null)
      {
        String destR = fs.getCurFS().getLocal(dest);
        if(destR != null)
          fs.getCodeBuf().append(indent + destR + " = $t2\n");
      }
    }
  }

  public void visit(Functions fs, VMemWrite w) throws Exception
  {
    String indent = fs.getIndent();
    String destId = ((Global)w.dest).base.toString();
    String destR = fs.queryId(destId);
    String sourceId = w.source.toString();
    String sourceR = fs.queryId(sourceId);
    fs.getCodeBuf().append(indent + "$t0 = " + destR + "\n");
    fs.getCodeBuf().append(indent + "$t1 = " + sourceR + "\n");
    int memOffset = ((Global)w.dest).byteOffset;
    fs.getCodeBuf().append(indent + "[$t0+" + memOffset + "] = $t1\n");
  }

  public void visit(Functions fs, VMemRead r) throws Exception
  {
    String indent = fs.getIndent();

    String destId = ((Local)r.dest).toString();
    String destR = fs.queryId(destId);
    String sourceId = ((Global)r.source).base.toString();
    String sourceR = fs.queryId(sourceId);
    int memOffSet = ((Global)r.source).byteOffset;
    // load source
    fs.getCodeBuf().append(indent + "$t0 = " + sourceR + "\n");
    fs.getCodeBuf().append(indent + "$t1 = " + destR + "\n");
    fs.getCodeBuf().append(indent + "$t1 = " + "[$t0+" + memOffSet + "]\n");
    fs.getCodeBuf().append(indent + destR + " = $t1\n");
  }

  public void visit(Functions fs, VBranch b) throws Exception
  {
    String condId = b.value.toString();
    String condR = fs.getCurFS().getIn(condId);
    String indent = fs.getIndent();
    if(condR == null)
      condR = fs.getCurFS().getLocal(condId);
    if(condR == null)
      condR = condId;
    fs.getCodeBuf().append(indent + "$t0 = " + condR + "\n");
    if(b.positive)
      fs.getCodeBuf().append(indent + "if $t0 goto " + b.target + "\n");
    else
      fs.getCodeBuf().append(indent + "if0 $t0 goto " + b.target + "\n");
  }

  public void visit(Functions fs, VGoto g) throws Exception
  {
    fs.getCodeBuf().append(fs.getIndent() + "goto " + g.target + "\n");
    return;
  }

  public void visit(Functions fs, VReturn r) throws Exception
  {
    String indent = fs.getIndent();
    if(r.value != null)
    {
      String valId = r.value.toString();
      String valR = fs.getCurFS().getIn(valId);
      if(valR == null)
        valR = fs.getCurFS().getLocal(valId);
      if(valR == null)
        valR = valId;
      fs.getCodeBuf().append(indent + "$v0 = " + valR + "\n");
    }
    fs.getCodeBuf().append(indent + "ret\n");
  }
}