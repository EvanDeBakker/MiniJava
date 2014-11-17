import java.util.*;

public class DataSegments
{
  public HashMap<String, DataSegment> hm;
  public ArrayList<DataSegment> dss;
  public IndentationPrinter iPrinter;

  public DataSegments()
  {
    this.hm = new HashMap<String, DataSegment>();
    this.dss = new ArrayList<DataSegment>();
    System.out.println("here");
    this.iPrinter = new IndentationPrinter();
  }

  public DataSegments(SymbolTable st)
  {
    this.hm = new HashMap<String, DataSegment>();
    this.dss = new ArrayList<DataSegment>();
    this.iPrinter = new IndentationPrinter();
    for(Clazz c : st.getClazzList())
    {
      DataSegment ds = new DataSegment(true, c.getId());
      ArrayList<Clazz> cl = new ArrayList<Clazz>();
      Clazz cur_c = c;
      while(cur_c != null)
      {
        cl.add(cur_c);
        String pid = cur_c.getParentId();
        if(pid == null)
          cur_c = null;
        else
          cur_c = st.getClazz(pid);
      }
      Collections.reverse(cl);
      for(int i = 0; i < cl.size(); i++)
      {
        for(Meth m : cl.get(i).getMethList())
        {
          boolean overridden = false;
          for(int j = i + 1; j < cl.size(); j++)
          {
            for(Meth n : cl.get(j).getMethList())
            {
              if(m.getId().equals(n.getId()))
                overridden = true;
            }
          }
          if(!overridden)
          {
            ds.putFunctionLabel(cl.get(i).getId(), m.getId());
          }
        }
      }
      this.addDS(ds);
    }
  }

  public boolean addDS(DataSegment ds)
  {
    String DS_name = ds.getName();
    assert(!hm.containsKey(DS_name));
    hm.put(DS_name, ds);
    dss.add(ds);
    return true;
  }

  // get the position of method in a class in table
  public int getFunctionLabelPos(String cid, String mid)
  {
    DataSegment ds = hm.get(cid);
    assert(ds != null);
    return ds.getFunctionLabelPos(mid);
  }

  public DataSegment getDataSegment(String cid)
  {
    DataSegment ds = hm.get(cid);
    assert(ds != null);
    return ds;
  }


  public String dumpDataSegments()
  {
    String ret = "";
    for(DataSegment ds : this.dss)
      ret += ds.dumpDataSegment();
    return ret;
  }
}




















