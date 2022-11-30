import java.util.*;
import javax.script.*;
import java.io.*;

public class Lab5 {

   public static void main(String[] args) throws Exception {

      //oracle
      //must have jdk and jre of version 8
      ScriptEngine nash = new ScriptEngineManager().getEngineByName("Nashorn");

      int amount = 0; //amount of equations to be generated
      int correct = 0;

      String equationEval = "";
      String equationMine = "";
      boolean flagValidEquation = true; // to get close to 50/50 valid equations and invalid equations

      //variables for timing how long each part takes
      long evalTime = 0;
      long myTime = 0;
      long generateTime = 0;


      // get user input for amount of equations
      Scanner sc = new Scanner(System.in);
      boolean validInput = false;
      while (validInput == false) {
         System.out.print("Amount of equations. Enter a whole positive number:   ");
         String input = sc.next();
         int intInputValue = 0;
         try {
            intInputValue = Integer.parseInt(input);
            if (intInputValue > 0) {
               validInput = true;
               amount = intInputValue;
            }
         } catch (NumberFormatException e) {}
      }
      System.out.println();



      //generate equation, solve with my calculator, solve with javascript eval, print results, show feedback
      for (int i = 0; i < amount; i++) {
         //generating expressions
         String[] equations = {"",""};
         long startTimeGen = System.currentTimeMillis();
         if (flagValidEquation) { // if true then equation valid. if false then equation not valid
            equations = generateValid();
         } else {
            equations = generateInvalid();
         }
         long endTimeGen = System.currentTimeMillis();
         long durationGen = (endTimeGen - startTimeGen); //milliseconds
         generateTime += durationGen; // adding the time it took to do my calculator for a total time

         //flip flag
         flagValidEquation = !flagValidEquation;

         //set the equation variables with the corresponding result from generated expression
         equationEval = equations[0];
         equationMine = "0 + " + equations[1]; // "0 + " to ensure equations that start with - work (just how i wrote my calculator)

         // makes string with no spaces. that uses the conventions of cos,sin,tan and not equationEval, generated with Math.XXX(), so hard to read
         String equationToPrint = equations[1].replaceAll("\\s", "");

         String myResult = "";
         String oracleResult = "";


         //my calculator evaluating
         long startTimeMine = System.currentTimeMillis();
         try {
            String postfix = infixToPostfix(equationMine);
            myResult = evalRPN(postfix);
         } catch (Exception e) {
            myResult = "Error";
         }
         long endTimeMine = System.currentTimeMillis();
         long durationMine = (endTimeMine - startTimeMine); //milliseconds
         myTime += durationMine; // adding the time it took to do my calculator for a total time


         //oracle evaluating
         long startTimeEval = System.currentTimeMillis();
         try {
            oracleResult = String.valueOf(nash.eval(equationEval));
         } catch (Exception e) {
            // e.printStackTrace();
            oracleResult = "Error";
         }
         long endTimeEval = System.currentTimeMillis();
         long durationEval = (endTimeEval - startTimeEval); //milliseconds
         evalTime += durationEval; // adding the time it took to do oracle for a total oracle time


         // both start with E (meaning Error)
         if (myResult.charAt(0) == 'E' || oracleResult.charAt(0) == 'E') {
            if (myResult.charAt(0) == 'E' && oracleResult.charAt(0) == 'E') {
               correct++;
            }
         }


         // both have same value
         else if (Double.parseDouble(myResult) == Double.parseDouble(oracleResult)) {
            correct++;
         }
         System.out.println("Expr " + (i + 1) + ": " + equationToPrint + "    My eval: " + myResult + ".    Oracle eval: " + oracleResult);
      } // end of loop

      System.out.println();
      double percent = (100 * ((double) correct / (double) amount));
      System.out.println("Amount of expressions: " + amount + ". Amount of my caclulator same as Oracle: " + correct + ". Percent that my caclulator is same as Oracle: " + percent + "%");

      System.out.println();
      System.out.println("Time for my calculator: " + myTime + " (milliseconds) or " + (myTime / 1000) + " (seconds)");
      System.out.println("Time for oracle calculator: " + evalTime + " (milliseconds) or " + (evalTime / 1000) + " (seconds)");
      System.out.println("Time to generate equations: " + generateTime + " (milliseconds) or " + (generateTime / 1000) + " (seconds)");
   }




   /* VALID EXPRESSION
   return String array of expression for my calculator and for oracle. 
   no parameter */
   public static String[] generateValid() {
      // #, +, -, *, /, fun, ({, }), ^, =
      // starting point
      int randOp = (int)(Math.random() * (4));
      switch (randOp) {
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

      int randNum = (int)(Math.random() * (9) + 1);
      boolean run = true;
      String[] eq = {"",""};
      boolean firstDigit = true;
      int balance = 0;

      //eq[0] = ;  // for eval()
      //eq[1] = ;  // for mine

      while (run) {
         if (randOp == 0) {
            // #  can go = , # , - , + , * , /
            eq[0] = eq[0] + randNum;
            eq[1] = eq[1] + randNum;

            randNum = (int)(Math.random() * (10));
            if (firstDigit == true && randNum == 0) {
               randNum++;
            }
            firstDigit = false;

            // get next operator
            randOp = (int)(Math.random() * (7));
            switch (randOp) {
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
         } else if (randOp == 1) {
            // +  can go # , - , fun , ( , ^
            eq[0] = eq[0] + "+ ";
            eq[1] = eq[1] + "+ ";

            // get next operator
            randOp = (int)(Math.random() * (5));

            switch (randOp) {
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
         } else if (randOp == 2) {
            // -  can go # , + , - , fun , ( , ^
            eq[0] = eq[0] + "- ";
            eq[1] = eq[1] + "- ";

            // get next operator
            randOp = (int)(Math.random() * (6));

            switch (randOp) {
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
         } else if (randOp == 3) {
            // *  can go # , - , fun , ( , ^
            eq[0] = eq[0] + "* ";
            eq[1] = eq[1] + "* ";

            // get next operator
            randOp = (int)(Math.random() * (5));

            switch (randOp) {
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
         } else if (randOp == 4) {
            // divide  can go # , - , fun , ( , ^
            eq[0] = eq[0] + "/ ";
            eq[1] = eq[1] + "/ ";

            // get next operator
            randOp = (int)(Math.random() * (5));

            switch (randOp) {
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
         } else if (randOp == 5) {
            // functions with a (
            balance--;

            // 6 functions
            int ranFun = (int)(Math.random() * (4));

            String functionString0 = "";
            String functionString1 = "";

            //reminder
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
            randOp = (int)(Math.random() * (3));

            switch (randOp) {
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
         } else if (randOp == 6) {
            balance--;
            // ( or { can go # , - , fun

            eq[0] = eq[0] + "( ";
            eq[1] = eq[1] + "( ";

            // get next operator
            randOp = (int)(Math.random() * (3));

            switch (randOp) {
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
         } else if (randOp == 7) {
            // if a ) can be added only
            if (balance < 0) {
               eq[0] = eq[0] + ") ";
               eq[1] = eq[1] + ") ";
               ++balance;
            }
            // ) or } can go - , + , * , /  , ) , =

            // get next operator
            randOp = (int)(Math.random() * (6));

            switch (randOp) {
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
               if (balance < 0) {
                  randOp = 7;
                  break;
               }
               case 6: // =
                  if (balance == 0 && (eq[0].length() > 0)) {
                     randOp = 9;
                  }
                  break;
            }
         } else if (randOp == 8) {
            int randX = (int)(Math.random() * 201 + 1) - 11;
            int randY = (int)(Math.random() * 21 + 1) - 11;

            // ^ can go + , - , * , /
            eq[1] = eq[1] + randX + " ^ ( " + randY + " ) ";
            eq[0] = eq[0] + " Math.pow( " + randX + ", " + randY + " ) ";

            // get next operator
            randOp = (int)(Math.random() * (4));

            switch (randOp) {
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
         } else if (randOp == 9) {
            // =
            // END
            run = false;
         }
      }

      // add as many ) as needed to balance ( )
      while (balance < 0) {
         eq[0] = eq[0] + " )";
         eq[1] = eq[1] + " )";
         ++balance;
      }

      //if the first char is a space then remove it
      if (eq[0].charAt(0) == ' ') {
         eq[0] = eq[0].substring(1, eq[0].length());
      }
      if (eq[1].charAt(0) == ' ') {
         eq[1] = eq[1].substring(1, eq[1].length());
      }
      //if the last char is a space then remove it
      if (eq[0].charAt(eq[0].length() - 1) == ' ') {
         eq[0] = eq[0].substring(0, eq[0].length() - 1);
      }
      if (eq[1].charAt(eq[1].length() - 1) == ' ') {
         eq[1] = eq[1].substring(0, eq[1].length() - 1);
      }
      return eq;
   } //END OF generateValid



   /* INVALID EXPRESSION
   return String array of expression for my calculator and for oracle. 
   no parameter */
   public static String[] generateInvalid() {
      // #, +, -, *, /, fun, (, ), ^, =
      //starting point
      int randOp = (int)(Math.random() * (7));
      switch (randOp) {
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
      case 4: // *
         randOp = 3;
         break;
      case 5: // /
         randOp = 4;
         break;
      case 6: // )
         randOp = 7;
         break;
      }

      int randNum = (int)(Math.random() * (9) + 1);
      boolean run = true;
      String[] eq = {
         "",
         ""
      };
      boolean firstDigit = true;
      int balance = 0;

      //eq[0] = ;  // for eval()
      //eq[1] = ;  // for mine

      while (run) {
         if (randOp == 0) {
            // #  can go = , # , - , + , * , /
            // wrong options: fun
            eq[0] = eq[0] + randNum;
            eq[1] = eq[1] + randNum;

            randNum = (int)(Math.random() * (10));
            if (firstDigit == true && randNum == 0) {
               randNum++;
            }
            firstDigit = false;

            // get next operator
            randOp = (int)(Math.random() * (8));

            switch (randOp) {
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
            case 6: // fun
               eq[0] = eq[0] + " ";
               eq[1] = eq[1] + " ";
               randOp = 5;
               break;
            case 7: // ) or try number again
               if (balance < 0) {
                  eq[0] = eq[0] + " ";
                  eq[1] = eq[1] + " ";
                  randOp = 7;
               } else {
                  randOp = 0;
               }
               break;
            }
         } else if (randOp == 1) {
            // +  can go # , - , fun , ( , ^
            // wrong options: + , )
            eq[0] = eq[0] + "+ ";
            eq[1] = eq[1] + "+ ";

            // get next operator
            randOp = (int)(Math.random() * (7));

            switch (randOp) {
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
            case 5: // +
               randOp = 1;
               break;
            case 6: // )
               randOp = 7;
               break;
            }
         } else if (randOp == 2) {
            // -  can go # , + , - , fun , ( , ^
            // wrong options: ) , /
            eq[0] = eq[0] + "- ";
            eq[1] = eq[1] + "- ";

            // get next operator
            randOp = (int)(Math.random() * (8));

            switch (randOp) {
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
            case 6: // )
               randOp = 7;
               break;
            case 7: // /
               randOp = 4;
               break;
            }
         } else if (randOp == 3) {
            // *  can go # , - , fun , ( , ^
            // wrong options: / , )
            eq[0] = eq[0] + "* ";
            eq[1] = eq[1] + "* ";

            // get next operator
            randOp = (int)(Math.random() * (7));

            switch (randOp) {
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
            case 5: // /
               randOp = 4;
               break;
            case 6: // )
               randOp = 7;
               break;
            }
         } else if (randOp == 4) {
            // divide  can go # , - , fun , ( , ^
            // wrong options: * , ) , +
            eq[0] = eq[0] + "/ ";
            eq[1] = eq[1] + "/ ";

            // get next operator
            randOp = (int)(Math.random() * (8));

            switch (randOp) {
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
            case 5: // *
               randOp = 3;
               break;
            case 6: // )
               randOp = 7;
               break;
            case 7: // +
               randOp = 1;
               break;
            }
         } else if (randOp == 5) {
            // functions with a (
            balance--;

            // 6 functions
            int ranFun = (int)(Math.random() * (4));

            String functionString0 = "";
            String functionString1 = "";

            //reminder
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
            randOp = (int)(Math.random() * (3));

            switch (randOp) {
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
         } else if (randOp == 6) {
            balance--;
            // ( or { can go # , - , fun

            eq[0] = eq[0] + "( ";
            eq[1] = eq[1] + "( ";

            // get next operator
            randOp = (int)(Math.random() * (3));

            switch (randOp) {
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
         } else if (randOp == 7) {
            // if a ) can be added only
            if (balance < 0) {
               eq[0] = eq[0] + ") ";
               eq[1] = eq[1] + ") ";
               ++balance;
            }

            // 1 in 25 chance that it adds a ) no matter if needed or not
            int changePara = (int)(Math.random() * (25));
            if (changePara == 0) {
               eq[0] = eq[0] + ") ";
               eq[1] = eq[1] + ") ";
               ++balance;
            }

            // ) or } can go - , + , * , /  , ) , =
            // get next operator
            randOp = (int)(Math.random() * (6));

            switch (randOp) {
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
               if (balance < 0) {
                  randOp = 7;
                  break;
               }
               case 6: // =
                  if (balance == 0 && (eq[0].length() > 0)) {
                     randOp = 9;
                  }
                  break;
            }
         } else if (randOp == 8) {
            int randX = (int)(Math.random() * 101 + 1) - 21;
            int randY = (int)(Math.random() * 21 + 1) - 11;

            // ^ can go + , - , * , /
            eq[1] = eq[1] + randX + " ^ ( " + randY + " ) ";
            eq[0] = eq[0] + " Math.pow( " + randX + ", " + randY + " ) ";

            // get next operator
            randOp = (int)(Math.random() * (4));

            switch (randOp) {
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
         } else if (randOp == 9) {
            // =
            // END
            run = false;
         }
      }

      // add as many ) as needed to balance ( )
      while (balance < 0) {
         eq[0] = eq[0] + " )";
         eq[1] = eq[1] + " )";
         ++balance;
      }

      //if the first char is a space then remove it
      if (eq[0].charAt(0) == ' ') {
         eq[0] = eq[0].substring(1, eq[0].length());
      }
      if (eq[1].charAt(0) == ' ') {
         eq[1] = eq[1].substring(1, eq[1].length());
      }
      //if the last char is a space then remove it
      if (eq[0].charAt(eq[0].length() - 1) == ' ') {
         eq[0] = eq[0].substring(0, eq[0].length() - 1);
      }
      if (eq[1].charAt(eq[1].length() - 1) == ' ') {
         eq[1] = eq[1].substring(0, eq[1].length() - 1);
      }
      return eq;
   } //END OF generateInvalid



   /*my calculator
   part 1 of 2*/

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
            if (parts[i].equals("-") && parts[i + 1].equals("-")) {
               idx = 1;
               specialCase = 1;
               token = "+";
            } else if (parts[i].equals("+") && parts[i + 1].equals("-")) {
               idx = 0;
               specialCase = 2;
               token = "-";
            } else if (parts[i].equals("-") && parts[i + 1].equals("+")) {
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
      return sb.toString();
   }


   /*my calculator
   part 2 of 2*/

   //from this link. going from postfix to parsing || solving it
   //https://rosettacode.org/wiki/Parsing/RPN_calculator_algorithm#Java
   //added the case of # / 0 - when the token equals '/' case to return invalid equation.
   private static String evalRPN(String expr) {
      LinkedList < Double > stack = new LinkedList < Double > ();
      for (String token: expr.split("\\s")) {
         if (token.equals("*")) {
            double secondOperand = stack.pop();
            double firstOperand = stack.pop();
            stack.push(firstOperand * secondOperand);
         } else if (token.equals("/")) {
            double secondOperand = stack.pop();
            double firstOperand = stack.pop();
            if (secondOperand == 0) { //added special case of dividing by 0
               return "Error";
            }
            stack.push(firstOperand / secondOperand);
         } else if (token.equals("-")) {
            double secondOperand = stack.pop();
            double firstOperand = stack.pop();
            stack.push(firstOperand - secondOperand);
         } else if (token.equals("+")) {
            double secondOperand = stack.pop();
            double firstOperand = stack.pop();
            stack.push(firstOperand + secondOperand);
         } else if (token.equals("^")) {
            double secondOperand = stack.pop();
            double firstOperand = stack.pop();
            stack.push(Math.pow(firstOperand, secondOperand));
         } else {
            try {
               stack.push(Double.parseDouble(token + ""));
            } catch (NumberFormatException e) {
               return "Error";
            }
         }
      }
      if (stack.size() > 1) {
         return "Error";
      }
      String finalResult = Double.toString(stack.pop());
      return finalResult;
   }

}