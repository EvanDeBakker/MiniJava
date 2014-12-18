import java.io.*;
import java.util.*;

public class FunctionInfo
{
  int numIn;
  int numOut;
  int numLocal;
  int space;

  public FunctionInfo(int numIn, int numOut, int numLocal)
  {
    this.numIn = numIn;
    this.numOut = numOut;
    this.numLocal = numLocal;
    // stack space is accumulation of
    // 4 * out + 4 * local + 4 + 4
    this.space = 4 * this.numOut + 4 * this.numLocal + 8;
  }

  public int getNumIn()
  {
    return this.numIn;
  }

  public int getNumOut()
  {
    return this.numOut;
  }

  public int getNumLocal()
  {
    return this.numLocal;
  }

  public int getStackSpace()
  {
    return this.space;
  }

}