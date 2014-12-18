import java.io.*;
import java.util.*;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VVarRef.Local;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

public class V2VM
{
  public static void main(String[] args)
  {
    try
    {
      InputStream inputStream = System.in;
      PrintStream errorStream = System.err;
      System.out.println("here");
      VaporProgram vp = parseVapor(inputStream, errorStream);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public static VaporProgram parseVapor(InputStream in, PrintStream err)
    throws IOException
  {
    Op[] ops = {
      Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
      Op.PrintIntS, Op.HeapAllocZ, Op.Error,
    };
    boolean allowLocals = true;
    String[] registers = null;
    boolean allowStack = false;

    VaporProgram program;
    try {
      program = VaporParser.run(new InputStreamReader(in), 1, 1,
        java.util.Arrays.asList(ops),
        allowLocals, registers, allowStack);
    }
    catch (ProblemException ex) {
      err.println(ex.getMessage());
      return null;
    }
    return program;
  }
}
