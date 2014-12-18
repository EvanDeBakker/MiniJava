import java.io.*;
import java.lang.String;
import java.util.*;
import java.util.HashMap;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VVarRef.Local;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

public class FunctionStack
{
  public HashMap<String, String> InHm;
  public HashMap<String, String> LocalHm;
  public int out;

  public FunctionStack()
  {
    this.InHm = new HashMap<String, String>();
    this.LocalHm = new HashMap<String, String>();
    this.out = 0;
  }

  public boolean putIn(String id, String v)
  {
    if(InHm.containsKey(id))
      return false;
    else
    {
      InHm.put(id, v);
      return true;
    }
  }

  public boolean putLocal(String id, String v)
  {
    if(LocalHm.containsKey(id) || InHm.containsKey(id))
      return false;
    else
    {
      LocalHm.put(id, v);
      return true;
    }
  }

  public boolean setOut(int o)
  {
    if(o > out)
      out = o;
    return true;
  }

  public int getOut()
  {
    return out;
  }

  public String getIn(String id)
  {
    return InHm.get(id);
  }

  public String getLocal(String id)
  {
    return LocalHm.get(id);
  }

}