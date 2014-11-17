import java.util.*;

public class IndentationPrinter
{
  public IndentationPrinter(){}

  public String getIndentString(int n, String s)
  {
    String i = "";
    while(n != 0)
    {
      i += " ";
      n--;
    }
    return i + s;
  }

  public String getIndentation(int n)
  {
    String i = "";
    while(n != 0)
    {
      i += " ";
      n--;
    }
    return i;
  }

  public void printIndentString(int n, String s)
  {
    System.out.println(this.getIndentString(n,s));
  }

  public void printIndentation(int n)
  {
    System.out.println(this.getIndentation(n));
  }
}