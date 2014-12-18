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

public class DataSegments
{
  StringBuffer bf;
  public DataSegments(VDataSegment[] dataSegments)
  {

    this.bf = new StringBuffer();
    for(VDataSegment dataSegment: dataSegments)
    {
      bf.append(dataSegment.ident + ":\n");
      for(VOperand.Static value : dataSegment.values)
      {
        String v = value.toString();
        v = v.substring(1, v.length());
        bf.append("  " + v + "\n");
      }
      bf.append("\n");
    }
  }

  public String dump()
  {
    return bf.toString();
  }
}
