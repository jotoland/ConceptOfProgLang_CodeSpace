import java.io.*;
import java.security.*;
import java.util.*;

public class Interpreter
{
  public String per = ".";
  public String or ="v";
  public String t = "T";
  public String f = "F";
  public String and = "^";
  public String dash = "-";
  public String imply = "->";
  public String neg = "~";
  public String oPar = "(";
  public String cPar = ")";
  public String selSet = "";
  public String selSetAt;
  public String temp = "";
  public String temp2 = "";
  public static String startSym = "$";
  public static Lex stok;
  public static String lex = "";
  public static Stack st = new Stack();
  public static boolean trueArrow = true;
  
  public static void main(String argv[]) throws IOException
  {  
    Interpreter o;
    o = new Interpreter();
    
     if(argv.length == 0){
        System.out.println("usage:Enter Boolean Expression [ends with a period(.)] ");
        System.exit(0);
     }

     BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    
     while(true){
         System.out.print("\nEnter Boolean Expression:[ends w/period \".\"]:[\"q\" to quit]:\n[ello]: ");
         String str = input.readLine();
         if(str.equals("q")){
             System.out.println("[Goodbye]");
             break;
         }if(str.length()>1 && (str.indexOf(".") !=-1 &&(str.length()<=100))){
             stok = new Lex(str);
             
             lex = stok.nextToken();
             if(trueArrow){
                st.push(startSym);
                if(o.B()){
                    System.out.println("\n"+str+ " = " +st.pop());
                }
             }else{
                 o.printSynEr("Use: \"->\"\n");
                 trueArrow = true;
             }
         }else if(str.length()==1 && (str.indexOf(".")!=-1)){
            o.printSynEr("Empty Expression!");
         }else if(str.length() > 100){
            o.printSynEr("OverFlow!");
         }else{
            o.printSynEr("No Period!");
        }
      }
    
}

  public boolean B(){
    selSet = "{ ~, T, F, ( }";
    if(IT()){
      if(lex.equals(per)){
        return true;
      }
       printError(selSet);
       return false;
    }else{
      printError(selSet);
      return false;
    }
  }

  public boolean IT(){
    selSet = "{ ~, T, F, ( }";
    if(OT()){
      if(IT_tail()){
        return true;
      }
      printError(selSet);
      return false;
    }else{
      printError(selSet);
      return false;
    }
  }

  public boolean IT_tail(){
    selSet = "{ -, >, ., ) }";
    if(lex.equals(dash)){
      lex = lex + stok.nextToken();
      st.push(lex);
      lex = stok.nextToken();
      if(OT()){
        if(IT_tail()){
          return true;
        }
        printError(selSet);
        return false;
      }
      printError(selSet);
      return false;
    }else{
      return true;
    }
  }

  public boolean OT(){
    selSet = "{ ~, T, F, ( }";
    if(AT()){
      if(OT_tail()){
        return true;
      }
      printError(selSet);
      return false;
    }else{
      printError(selSet);
      return false;
    }
  }

  public boolean OT_tail(){
    selSet = "{ v, ->, ., ) }";
    if(lex.equals(or)){
      st.push(or);
      lex = stok.nextToken();
      if(AT()){
        if(OT_tail()){
          return true;
        }
        printError(selSet);
        return false;
      }
      printError(selSet);
      return false;
    }else{
      return true;
    }
  }

  public boolean AT(){
    selSetAt = "{ ~, T, F, ( }";
    if(L()){
      if(AT_tail()){
        return true;
      }
      printError(selSet);
      return false;
    }else{
      printError(selSet);
      return false;
    }
  }

  public boolean AT_tail(){
    selSet = "{ ^, v, ->, ., ) }";
    if(lex.equals(and)){
      lex = stok.nextToken();
      st.push(and);
      if(L()){
        if(AT_tail()){
          return true;
        }
        printError(selSet);
        return false;
      }
      printError(selSet);
      return false;
    }else{
      return true;
    }
  }

  public boolean L(){
    selSet = "{ T, F, (, ~ }";
    if(A()){
      return true;
    }else if(neg()){
      if(L()){
        return true;
      }
      printError(selSet);
      return false;
    }else{
      printError(selSet);
      return false;
    }
  }

  public boolean A(){
    selSet = "{ T, F, ( }";
    if(lex.equals(t) || lex.equals(f)){
      if(!seeStart()){
        temp = (String) st.pop();
        if(temp.equals(or)){
          calculateOr();
        }else if(temp.equals(and)){
          calculateAnd();
        }else if(temp.equals(neg)){
          st.push(neg);
          calculateNeg();
        }else if(temp.equals(imply)){
          calculateImply();
        }
      }else{
        st.push(lex);
      }
      lex = stok.nextToken();
      return true;
   }else if(lex.equals(oPar)){
      lex = stok.nextToken();
      if(IT()){
        if(lex.equals(cPar)){
          lex = stok.nextToken();
          return true;
        }
        return false;
      }
      return false;
    }else{
      if(!lex.equals(neg)){
         printError(selSet);
      }
      return false;
    }
  }
  
  public void calculateOr(){
      temp2 = (String) st.pop();
      if(temp2.equals(t)){
       st.push(t);
      }else{
        st.push(lex);
      }
  }
  
  public void calculateAnd(){
    temp2 = (String) st.pop();
    if(temp2.equals(lex)){
      st.push(lex);
    }else{
      st.push(f);
    }
  }
  
  public boolean neg(){
    if(!lex.equals(neg)){
      return false;
    }else{
      st.push(lex);
      lex = stok.nextToken();
      neg();
    }
    return true;
  }
  
  public void calculateNeg(){
    temp2 = (String) st.pop();
    if(!seeStart()){
      if(lex.equals(t) && temp2.equals(neg)){
        lex = f;
      }else if(lex.equals(f) && temp2.equals(neg)){
        lex = t;
      }
      calculateNeg();
    }else if(lex.equals(t) && temp2.equals(neg)){
      st.push(f);
    }else{
      st.push(t);
    }
  }
  
  public void calculateImply(){
    temp2 = (String) st.pop();
    if(temp2.equals(t) && lex.equals(f)){
      st.push(f);
    }else{
      st.push(t);
    }
  }
  
  public boolean seeStart(){
    temp = (String) st.pop();
    boolean start = false;
    if(temp == startSym){
      start = true;
    }
    st.push(temp);
    return start;
  }
  
  public void printError(String s){
    System.out.println("Expecting " + s + " but got [ " + lex +" ]\n");
  }
  
  public void printSynEr(String s){
    String str = s;
    System.out.println("\n[Error!]: Syntax Error! [ello]: "+ str +"\n");
  }
}

