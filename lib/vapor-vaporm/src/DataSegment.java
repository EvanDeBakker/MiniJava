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

public class DataSegment
{
  String ds;

  public DataSegment(VDataSegment[] dataSegments)
  {

    this.ds = "";
    for(VDataSegment dataSegment: dataSegments)
    {
      String mutable = dataSegment.mutable ? "var " : "const ";
      this.ds += mutable + dataSegment.ident + "\n";
      for(VOperand.Static value : dataSegment.values)
      {
        this.ds += "  " + value.toString() + "\n";
      }
      this.ds += "\n";
    }
  }

  public String dump()
  {
    return this.ds;
  }
}
