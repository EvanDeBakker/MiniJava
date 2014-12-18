import cs132.vapor.ast.VInstr.VisitorP;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VReturn;

public class VisitorA extends VisitorP<MIPSInstr, Exception>
{
  public void visit(MIPSInstr m, VAssign a) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VCall c) throws Exception
  {
    System.out.println(c.args.length);
  }
  public void visit(MIPSInstr m, VBuiltIn c) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VMemWrite w) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VMemRead r) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VBranch b) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VGoto g) throws Exception
  {
    return;
  }
  public void visit(MIPSInstr m, VReturn r) throws Exception
  {
    return;
  }
}