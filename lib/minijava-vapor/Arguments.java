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

// oh, a phantom class!
public class Arguments
{

  int tnum;
  public Arguments()
  {
    tihs.tnum = 0;
  }

  public Arguments(int tnum)
  {
    this.tnum = tnum
  }

  public int getNum() {return this.tnum;}

  public String toString() {return (new Integer(tnum)).toString();}
}