import java.io.*;
import java.util.*;

public class OtherStuff
{
  public OtherStuff(){}

  public void printDataLabel()
  {
    System.out.println(".data\n");
  }

  public void printTextLabel()
  {
    String indent = "  ";
    System.out.println(".text\n");
    System.out.println(indent + "jal Main");
    System.out.println(indent + "li $v0 10");
    System.out.println(indent + "syscall\n");
  }

  public void printOtherThings()
  {
    StringBuffer bf = new StringBuffer();
    String indent = "  ";
    bf.append("_print:\n");
    bf.append(indent + "li $v0 1\n");
    bf.append(indent + "syscall\n");
    bf.append(indent + "la $a0 _newline\n");
    bf.append(indent + "li $v0 4 \n");
    bf.append(indent + "syscall\n");
    bf.append(indent + "jr $ra\n\n");

    bf.append("_error:\n");
    bf.append(indent + "li $v0 4\n");
    bf.append(indent + "syscall\n");
    bf.append(indent + "li $v0 10 \n");
    bf.append(indent + "syscall\n\n");

    bf.append("_heapAlloc:\n");
    bf.append(indent+ "li $v0 9\n");
    bf.append(indent+ "syscall\n");
    bf.append(indent+ "jr $ra\n\n");

    bf.append(".data\n");
    bf.append(".align 0\n");
    bf.append("_newline: .asciiz \"\\n\"\n");
    bf.append("_str0: .asciiz \"null pointer\\n\"\n");
    bf.append("_str1: .asciiz \"array index out of bounds\n\"\n");

    System.out.println(bf.toString());
  }
}


