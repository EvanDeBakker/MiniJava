/*  MiniJava program to Vapor compilation program
 *  Copyright (C) 2014  marklrh
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.*;

/* QueryTable class serves as a general query table that provides the following
 * functionalities:
 * 1. build info related to datasegments, print datasegments
 * 2. build info related to clazz and meth
 * 3. provides API for step two visitor to query

 */

public class QueryTable
{
  private SymbolTable st;
  // map clazz id to datasegment
  private HashMap<String, DataSegment> hm;
  // Array of data segment
  private ArrayList<DataSegment> dss;
  // map clazz id to the heap size it needs
  private HashMap<String, Integer> heap_size;
  // indentation printer
  private IndentationPrinter iPrinter;

  public QueryTable()
  {
    this.hm = new HashMap<String, DataSegment>();
    this.dss = new ArrayList<DataSegment>();
    this.heap_size = new HashMap<String, Integer>();
    this.iPrinter = new IndentationPrinter();
  }

  public QueryTable(SymbolTable st)
  {
    this.st = st;
    this.hm = new HashMap<String, DataSegment>();
    this.dss = new ArrayList<DataSegment>();
    this.heap_size = new HashMap<String, Integer>();
    this.iPrinter = new IndentationPrinter();
    this.buildDataSegmentsInfo();
    this.buildFieldPosInfo();
    this.calculateClazzHeapAllocationSize(st);
  }

  public boolean addDS(DataSegment ds)
  {
    String DS_name = ds.getName();
    assert(!hm.containsKey(DS_name));
    hm.put(DS_name, ds);
    dss.add(ds);
    return true;
  }

  public DataSegment getDS(String cid)
  {
    DataSegment ds = hm.get(cid);
    assert(ds != null);
    return ds;
  }

  public String dumpDataSegments()
  {
    String ret = "\n";
    for(DataSegment ds : this.dss)
      ret += ds.dumpDataSegment();
    return ret;
  }

  public boolean calculateClazzHeapAllocationSize(SymbolTable st)
  {
    for(Clazz c : st.getClazzList())
      heap_size.put(c.getId(), new Integer(st.calcHeapSize(c)));
    return true;
  }

  public boolean buildDataSegmentsInfo()
  {
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
    return true;
  }

  // get the position of method in a class
  public int getFunctionLabelPos(String cid, String mid)
  {
    DataSegment ds = hm.get(cid);
    assert(ds != null);
    return ds.getFunctionLabelPos(mid);
  }

  public void buildFieldPosInfo() {}

  // get the position of field in a class
  public int getFieldPos(String cid, String fvid)
  {
    Clazz c = st.getClazz(cid);
    int pos = c.getFieldPos(fvid);
    if(pos == -1)
    {
      assert(false);
      System.exit(1);
    }
    return pos;
  }

  public boolean isField(String cid, String mid, String vid)
  {
    Clazz c = st.getClazz(cid);
    Meth m = c.getMeth(mid);
    return !m.containsLocalVariableOrParameter(vid);
  }

  public int getClazzHeapSize(String cid)
  {
    return heap_size.get(cid).intValue();
  }

}