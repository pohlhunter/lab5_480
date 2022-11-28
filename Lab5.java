import java.util.*;
import java.util.Random;
import javax.script.*;
import java.io.*;

public class Lab5 {

  public static void main(String[] args) throws Exception {
      
      int amount = 0;
      String equationEval = "";
      String equationMine = "";
      
      int correct = 0;
      
      while (amount < 1) {
         Scanner myObj = new Scanner(System.in);
         System.out.println("Amount of equations: ");
         amount = myObj.nextInt();
      }
      
      for (int i = 0; i < amount; i++) {
         String[] equations = generateValid();
         equationEval = equations[0];
         equationMine = equations[1];
         System.out.println("equation eval: " + equationEval);
         System.out.println("equation mine: " + equationMine);

         String myResult = "";
         String oracleResult = "";
         
         try {
            String postfix = infixToPostfix(equationMine);
            myResult = evalRPN(postfix);
          } catch (Exception e) {
            myResult = "Error";
          }
          
          //System.out.println("my result: " + myResult);
          
          
          try {
            oracleResult = oracleEval(equationEval);
          } catch (Exception e) {
            e.printStackTrace();
            oracleResult = "Error";
          }
          //System.out.println("oracle result: " + oracleResult);
          
          
          // both start with E
          if (myResult.charAt(0) == 'E' || oracleResult.charAt(0) == 'E') {
            if (myResult.charAt(0) == 'E' && oracleResult.charAt(0) == 'E') {
               correct++;
            }
            System.out.println("oracleResult:  " + oracleResult);
            System.out.println("myResult:  " + myResult);
          }
          // both have same value
          else if (Double.parseDouble(myResult) == Double.parseDouble(oracleResult)) {
            correct++;
          }
          
          //System.out.println();
       }
       System.out.println("Total amount: " + amount);
       System.out.println("Amount of correct: " + correct);
       double percent = (100 * ((double)correct / (double)amount));
       System.out.println("Percent correct: " + percent + "%");
   }





   public static String[] generateValid() {
        // #, +, -, *, /, fun, ({, }), ^, =
        int randOp = (int) (Math.random() * (4));
        switch(randOp){
            case 0: // #
               randOp = 0;
               break;
            case 1: // -
               randOp = 2;
               break;
            case 2: // fun
               randOp = 5;
               break;
            case 3: // ({
               randOp = 6;
               break;
        }
        
        int randNum = (int) (Math.random() * (9) + 1);
        boolean run = true;
        String[] eq = {"", ""};
        boolean firstDigit = true;
        int balance = 0;
        
        //eq[0] = ;  // for eval()
        //eq[1] = ;  // for mine
        
        while (run)
        {
            if (randOp == 0)
            {
                // #  can go = , # , - , + , * , /
                eq[0] = eq[0] + randNum;
                eq[1] = eq[1] + randNum;
                
                randNum = (int) (Math.random() * (10));
                if (firstDigit == true && randNum == 0) {
                    randNum++;
                }
                firstDigit = false;
                
                // get next operator
                randOp =  (int) (Math.random() * (7));
                
                
                switch(randOp){
                  case 0: // =
                     randOp = 9;
                     break;
                  case 1: // #
                     randOp = 0;
                     break;
                  case 2: // -
                     eq[0] = eq[0] + " ";
                     eq[1] = eq[1] + " ";
                     randOp = 2;
                     break;
                  case 3: // +
                     eq[0] = eq[0] + " ";
                     eq[1] = eq[1] + " ";
                     randOp = 1;
                     break;
                  case 4: // *
                     eq[0] = eq[0] + " ";
                     eq[1] = eq[1] + " ";
                     randOp = 3;
                     break;
                  case 5: // /
                     eq[0] = eq[0] + " ";
                     eq[1] = eq[1] + " ";
                     randOp = 4;
                     break;
                  case 6: // ) or try number again
                     if (balance < 0) {
                        eq[0] = eq[0] + " ";
                        eq[1] = eq[1] + " ";
                        randOp = 7;
                     } else {
                        randOp = 0;
                     }
                     break;
                }

            }
            
            else if (randOp == 1)
            {
                // +  can go # , - , fun , ( , ^
                eq[0] = eq[0] + "+ ";
                eq[1] = eq[1] + "+ ";
                
                // get next operator
                randOp =  (int) (Math.random() * (5));
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // fun
                     randOp = 5;
                     break;
                  case 3: // (
                     randOp = 6;
                     break;
                  case 4: // ^
                     randOp = 8;
                     break;
                }
            }
            
            else if (randOp == 2)
            {
                // -  can go # , + , - , fun , ( , ^
                eq[0] = eq[0] + "- ";
                eq[1] = eq[1] + "- ";
                
                // get next operator
                randOp =  (int) (Math.random() * (6));
                
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // +
                     randOp = 1;
                     break;
                  case 2: // -
                     randOp = 2;
                     break;
                  case 3: // fun
                     randOp = 5;
                     break;
                  case 4: // (
                     randOp = 6;
                     break;
                  case 5: // ^
                     randOp = 8;
                     break;
                }
            }
            
            else if (randOp == 3)
            {
                // *  can go # , - , fun , ( , ^
                eq[0] = eq[0] + "* ";
                eq[1] = eq[1] + "* ";
                
                // get next operator
                randOp =  (int) (Math.random() * (5));
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // fun
                     randOp = 5;
                     break;
                  case 3: // (
                     randOp = 6;
                     break;
                  case 4: // ^
                     randOp = 8;
                     break;
                }
            }
            
            else if (randOp == 4)
            {
                // divide  can go # , - , fun , ( , ^
                eq[0] = eq[0] + "/ ";
                eq[1] = eq[1] + "/ ";
                
                // get next operator
                randOp =  (int) (Math.random() * (5));
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // fun
                     randOp = 5;
                     break;
                  case 3: // (
                     randOp = 6;
                     break;
                  case 4: // ^
                     randOp = 8;
                     break;
                }
            }
            
            else if (randOp == 5)
            {
                // functions with a (
                balance--;
                
                // 6 functions
                int ranFun = (int) (Math.random() * (4));
                String functionString0 = "";
                String functionString1 = "";
                
                // eq[0] = equationEval
                // eq[1] = equationMine
                if (ranFun == 0) {
                   // cos(
                   functionString1 = "cos( ";
                   // Math.cos(
                   functionString0 = "Math.cos(";
                } else if (ranFun == 1) {
                   // sin(
                   functionString1 = "sin( ";
                   // Math.sin(
                   functionString0 = "Math.sin(";
                } else if (ranFun == 2) {
                   // tan(
                   functionString1 = "tan( ";
                   // Math.tan(
                   functionString0 = "Math.tan(";
                } else if (ranFun == 3) {
                   // log(
                   functionString1 = "log( ";
                   // Math.log(
                   functionString0 = "Math.log(";
                }

                eq[0] = eq[0] + functionString0;
                eq[1] = eq[1] + functionString1;
                
                // fun can go # , - , fun
                // get next operator
                randOp = (int) (Math.random() * (3));
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // fun
                     randOp = 5;
                     break;
                }
            }
            
            else if (randOp == 6)
            {
               balance--;
               // ( or { can go # , - , fun
               
                eq[0] = eq[0] + "( ";
                eq[1] = eq[1] + "( ";
                
                // get next operator
                randOp =  (int) (Math.random() * (3));
                switch(randOp){
                  case 0: // #
                     randOp = 0;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // fun
                     randOp = 5;
                     break;
                }
            }
            
            else if (randOp == 7)
            {
               // if a ) can be added only
               if (balance < 0)
               {
                  eq[0] = eq[0] + ") ";
                  eq[1] = eq[1] + ") ";
                  ++balance;
               }
               // ) or } can go - , + , * , /  , ) , =

                // get next operator
                randOp =  (int) (Math.random() * (6));
                
                switch(randOp){
                  case 0: // -
                     randOp = 2;
                     break;
                  case 1: // +
                     randOp = 1;
                     break;
                  case 2: // *
                     randOp = 3;
                     break;
                  case 3: // /
                     randOp = 4;
                     break;
                  case 5: // )
                     if (balance < 0)
                     {
                        randOp = 7;
                        break;
                     } 
                  case 6: // =
                     if (balance == 0) {
                        randOp = 9;
                     }
                     break;
                }
            }
            
            else if (randOp == 8)
            {
         		int randX = (int)(Math.random() *201 +1) - 11;
               int randY = (int)(Math.random() *21 +1) - 11;
               
               // ^ can go + , - , * , /
                eq[1] = eq[1] + randX + " ^ ( " + randY + " ) ";
                eq[0] = eq[0] + " Math.pow( " + randX + ", " + randY + " ) ";
                
                // get next operator
                randOp =  (int) (Math.random() * (4));
                
                switch(randOp){
                  case 0: // +
                     randOp = 1;
                     break;
                  case 1: // -
                     randOp = 2;
                     break;
                  case 2: // *
                     randOp = 3;
                     break;
                  case 3: // /
                     randOp = 4;
                     break;
                }
            }
            
            else if (randOp == 9)
            {
               // =
               // END
                run = false;
            }
        }
        
        while (balance < 0) {
            eq[0] = eq[0] + " )";
            eq[1] = eq[1] + " )";
            ++balance;
        }
        //
        
        //if the first char is a space then remove it
        if (eq[0].charAt(0) == ' ')
        {
            eq[0] = eq[0].substring(1, eq[0].length());
        }
        if (eq[1].charAt(0) == ' ')
        {
            eq[1] = eq[1].substring(1, eq[1].length());
        }
        //if the last char is a space then remove it
        if (eq[0].charAt(eq[0].length() - 1) == ' ')
        {
            eq[0] = eq[0].substring(0, eq[0].length() - 1);
        }
        if (eq[1].charAt(eq[1].length() - 1) == ' ')
        {
            eq[1] = eq[1].substring(0, eq[1].length() - 1);
        }
        
        //System.out.println("eq[0] " + eq[0]);
        //System.out.println("eq[1] " + eq[1]);
        
        return eq;       
    }






   static String oracleEval(String equation) throws Exception {
            
      ScriptEngine nash = new ScriptEngineManager().getEngineByName("Nashorn");
      String strResult = String.valueOf(nash.eval(equation));
      return strResult;
   }
   





  //from the link below. going from infix to postfix and taking into account the precedence of the operation or ()
  //https://rosettacode.org/wiki/Parsing/Shunting-yard_algorithm#Java
  static String infixToPostfix(String infix) {
    /* To find out the precedence, we take the index of the
       token in the ops string and divide by 2 (rounding down). 
       This will give us: 0, 0, 1, 1, 2 */
    final String ops = "-+/*^";

    StringBuilder sb = new StringBuilder();
    Stack < Integer > s = new Stack < > ();

    int specialCase = 0;

    String[] parts = infix.split(" ");
    for (int i = 0; i < parts.length; i++) {
      String token;
      token = parts[i]; // get the current token

      if (token.isEmpty())
        continue;
      char c = token.charAt(0);
      int idx = ops.indexOf(c);

      if ((parts[i].equals("-") && parts[i + 1].equals("-")) || (parts[i].equals("+") && parts[i + 1].equals("-")) || (parts[i].equals("-") && parts[i + 1].equals("+"))) {
        //System.out.println("token before: " + token);
        if (parts[i].equals("-") && parts[i + 1].equals("-")) {
          //System.out.println("become +");
          idx = 1;
          specialCase = 1;
          token = "+";
        } else if (parts[i].equals("+") && parts[i + 1].equals("-")) {
          //System.out.println("become -");
          idx = 0;
          specialCase = 2;
          token = "-";
        } else if (parts[i].equals("-") && parts[i + 1].equals("+")) {
          //System.out.println("become -");
          idx = 0;
          specialCase = 3;
          token = "-";
        }
      } else if (idx != -1) { // check for regular operator
        if (s.isEmpty()) {
          s.push(idx);
        } else {
          while (!s.isEmpty()) {
            int prec2 = s.peek() / 2;
            int prec1 = idx / 2;
            if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
              sb.append(ops.charAt(s.pop())).append(' ');
            else break;
          }
          s.push(idx);
        }
      } else if (c == '(' || c == '{') {
        s.push(-2); // -2 stands for '('
      } else if (c == ')' || c == '}') {
        // until '(' on stack, pop operators.
        while (s.peek() != -2)
          sb.append(ops.charAt(s.pop())).append(' ');
        s.pop();
      } else {
        sb.append(token).append(' ');
      }

    }
    while (!s.isEmpty()) {
      if (specialCase == 1) { // - - turns into +
        s.pop();
        s.push(1);
      }
      if (specialCase == 2) { // + - turns into -
        s.pop();
        s.push(0);
      }
      if (specialCase == 3) { // - + turns into -
        s.pop();
        s.push(0);
      }
      specialCase = 0;
      sb.append(ops.charAt(s.pop())).append(' ');
    }

    //System.out.println("final postfix equation:  " + sb.toString());
    return sb.toString();
  }




  //from this link. going from postfix to parsing || solving it
  //https://rosettacode.org/wiki/Parsing/RPN_calculator_algorithm#Java
  //added the case of # / 0 - when the token equals '/' case to return invalid equation.

  private static String evalRPN(String expr) {
    LinkedList < Double > stack = new LinkedList < Double > ();
    //System.out.println("Input\tOperation\tStack after");
    for (String token: expr.split("\\s")) {
      //System.out.print("\n" + expr + "");
      //System.out.print("    " + token + "\n");

      //System.out.print(token + "\t");
      if (token.equals("*")) {
        //System.out.print("Operate\t\t");
        double secondOperand = stack.pop();
        double firstOperand = stack.pop();
        stack.push(firstOperand * secondOperand);
      } else if (token.equals("/")) {
        //System.out.print("Operate\t\t");
        double secondOperand = stack.pop();
        double firstOperand = stack.pop();
        if (secondOperand == 0) { //added special case of dividing by 0
          //System.out.println("\nError: cannot divide by 0");
          return "Error";
        }
        stack.push(firstOperand / secondOperand);
      } else if (token.equals("-")) {
        //System.out.print("Operate\t\t");
        double secondOperand = stack.pop();
        double firstOperand = stack.pop();
        stack.push(firstOperand - secondOperand);
      } else if (token.equals("+")) {
        //System.out.print("Operate\t\t");
        double secondOperand = stack.pop();
        double firstOperand = stack.pop();
        stack.push(firstOperand + secondOperand);
      } else if (token.equals("^")) {
        //System.out.print("Operate\t\t");
        double secondOperand = stack.pop();
        double firstOperand = stack.pop();
        stack.push(Math.pow(firstOperand, secondOperand));
      } else {
        //System.out.print("Push\t\t");
        try {
          stack.push(Double.parseDouble(token + ""));
        } catch (NumberFormatException e) {
          //System.out.println("\nError: invalid token " + token);
          return "Error";
        }
      }
      //System.out.println(stack);
    }
    if (stack.size() > 1) {
      //System.out.println("Error, too many operands: " + stack);
      return "Error";
    }
    String finalResult = Double.toString( stack.pop());
    //System.out.println("Final answer: " + finalResult);
    return finalResult;
  }
  
  
  
  
  
  
  
}