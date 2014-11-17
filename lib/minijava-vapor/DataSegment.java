import java.util.*;

public class DataSegment {
  public boolean isConst;
  public int f_pos;
  public String DS_name;
  // map cid + mid to position number
  public ArrayList<String> f_list;
  // map mid to position number
  public HashMap<String, Integer> hm;
  public IndentationPrinter iPrinter;

  public DataSegment(boolean ic, String DS_name) {
    this.isConst = ic;
    this.f_pos = 0;
    this.DS_name = DS_name;
    f_list = new ArrayList<String>();
    hm = new HashMap<String, Integer>();
    this.iPrinter = new IndentationPrinter();
  }

  public boolean isConst()
  {
    return this.isConst;
  }

  public String getName()
  {
    return this.DS_name;
  }

  public boolean putFunctionLabel(String cid, String mid) {
    assert(!hm.containsKey(mid));
    f_list.add(cid + "." + mid);
    hm.put(mid, new Integer(f_pos));
    f_pos++;
    return true;
  }

  public int getFunctionLabelPos(String mid) {
    Integer v = hm.get(mid);
    assert (v != null);
    return 4 * v.intValue();
  }

  public ArrayList<String> getFunctionLabelList()
  {
    return this.f_list;
  }

  public String dumpDataSegment()
  {
    String ret = "";
    if(this.isConst())
      ret += "const";
    else
      ret += "var";
    ret += " vmt_";
    ret += this.getName() + "\n"; // finish first line
    // print method list
    for(String s : this.getFunctionLabelList())
      ret += iPrinter.getIndentString(2, ":" + s + "\n");
    ret += "\n"; // one blank line at the end
    return ret;
  }
}