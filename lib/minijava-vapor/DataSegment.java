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

public class DataSegment {
  public boolean isConst;
  public int f_pos;
  public String DS_name;
  // store cid + mid in order
  public ArrayList<String> f_list;
  // store mid in order
  public ArrayList<String> f_mid_only_list;
  // map mid to position number
  public HashMap<String, Integer> mid_pos;
  // map field to position number
  public ArrayList<String> fvid_pos;
  // seperation mark
  public HashMap<String, Integer> sep_mark;
  // printer
  public IndentationPrinter iPrinter;

  public DataSegment(boolean ic, String DS_name) {
    this.isConst = ic;
    this.f_pos = 0;
    this.DS_name = DS_name;
    this.f_list = new ArrayList<String>();
    this.f_mid_only_list = new ArrayList<String>();
    this.mid_pos = new HashMap<String, Integer>();
    this.fvid_pos = new ArrayList<String>();
    this.sep_mark = new HashMap<String, Integer>();
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

  // function labels
  public boolean putFunctionLabel(String cid, String mid) {
    assert(!mid_pos.containsKey(mid));
    f_list.add(cid + "." + mid);
    f_mid_only_list.add(mid);
    mid_pos.put(mid, new Integer(f_pos));
    f_pos++;
    return true;
  }

  public boolean putFunctionLabelSp(String cm)
  {
    StringBuilder cb = new StringBuilder();
    StringBuilder mb = new StringBuilder();
    boolean collect_cid = true;
    for(int i = 0; i < cm.length(); i++)
    {
      if(cm.charAt(i) == '.')
      {
        collect_cid = false;
        continue;
      }
      if(collect_cid)
        cb.append(cm.charAt(i));
      else
        mb.append(cm.charAt(i));
    }
    String cid = cb.toString();
    String mid = mb.toString();
    return this.putFunctionLabel(cid, mid);
  }

  public int getFunctionLabelPos(String mid) {
    Integer v = mid_pos.get(mid);
    assert (v != null);
    return 4 * v.intValue();
  }

  public boolean isFunctionAdded(String mid)
  {
    return mid_pos.containsKey(mid);
  }

  public ArrayList<String> getFunctionLabelList()
  {
    return this.f_list;
  }

  public ArrayList<String> getFunctionLabelMidOnlyList()
  {
    return this.f_mid_only_list;
  }

  // field pos
  public boolean putFieldPos(String vid)
  {
    fvid_pos.add(vid);
    return true;
  }

  public boolean putSeparationMark(String cid, int pos)
  {
    Integer mark = new Integer(pos);
    sep_mark.put(cid, mark);
    return true;
  }

  public int getFieldPos(String vid)
  {
    //Integer v = sep_mark.get(cid);
    //assert(v != null);
    //int sep = v.intValue();
    for(int i = fvid_pos.size() - 1; i >= 0; i--)
    {
      if(fvid_pos.get(i).equals(vid))
        return i;
    }
    assert(false);
    return -1;
  }

  public int getNumOfFields()
  {
    return this.fvid_pos.size();
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
      ret += iPrinter.getIndentStringln(2, ":" + s);
    ret += "\n"; // one blank line at the end
    return ret;
  }
}