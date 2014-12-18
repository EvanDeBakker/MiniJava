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

public class OutVisitor extends VisitorP<Functions, Exception>
{
  public void visit(Functions fs, VAssign a) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VCall c) throws Exception
  {
    fs.getCurFS().setOut(c.args.length);
  }
  public void visit(Functions fs, VBuiltIn c) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VMemWrite w) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VMemRead r) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VBranch b) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VGoto g) throws Exception
  {
    return;
  }
  public void visit(Functions fs, VReturn r) throws Exception
  {
    return;
  }
}