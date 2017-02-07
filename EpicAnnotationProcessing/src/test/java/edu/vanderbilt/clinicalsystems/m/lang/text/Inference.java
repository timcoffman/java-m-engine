
package edu.vanderbilt.clinicalsystems.m.lang.text;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;

public class Inference {

    public static Value globalChannel;
    public static double globalInteger;
    public static boolean globalBoolean;
    public static String globalString;
    public static long globalDouble;

    /**
     * INFERENCE; see edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaWriterTest.canInferTypes
     * Q 
     * 
     */
    public static void main() {
        return ;
    }

    /**
     * inferMethodReturnsVoid; expect void
     * Q 
     * 
     */
    public static void inferMethodReturnsVoid() {
        return ;
    }

    /**
     * inferMethodReturnsInteger; expect int
     * Q 1
     * 
     */
    public static long inferMethodReturnsInteger() {
        return  1;
    }

    /**
     * inferMethodReturnsDouble; expect double
     * Q 3.14159
     * 
     */
    public static double inferMethodReturnsDouble() {
        return  3.14159D;
    }

    /**
     * inferMethodReturnsString; expect String
     * Q "foobar"
     * 
     */
    public static String inferMethodReturnsString() {
        return "foobar";
    }

    /**
     * inferMethodReturnsValue; expect Value
     * N x
     * S x("foo")="bar"
     * Q x
     * 
     */
    public static String inferMethodReturnsValue() {
        String x = "";
        x = "bar";
        return x;
    }

    /**
     * inferLocalVariableType; expect boolean, String, Integer, double
     * N localBoolean,localString,localInteger,localDouble
     * S localBoolean=3!7
     * S localString=3_7
     * S localInteger=3/7
     * S localDouble=3\7
     * Q 
     * 
     */
    public static void inferLocalVariableType() {
        boolean localBoolean = false;
        String localString = "";
        double localInteger = 0.0D;
        long localDouble = 0;
        localBoolean = true;
        localString = ("3"+"7");
        localInteger = (3.0D/ 7.0D);
        localDouble = (3 / 7);
        return ;
    }

    /**
     * inferGlobal; expect expect boolean, String, Integer, double
     * S globalBoolean=3!7
     * S globalString=3_7
     * S globalInteger=3/7
     * S globalDouble=3\7
     * Q 
     * 
     */
    public static void inferGlobal() {
        globalBoolean = true;
        globalString = ("3"+"7");
        globalInteger = (3.0D/ 7.0D);
        globalDouble = (3 / 7);
        return ;
    }

    /**
     * inferSecondOrderReturnTypeFromVariable; expect String
     * Q globalString
     * 
     */
    public static String inferSecondOrderReturnTypeFromVariable() {
        return globalString;
    }

    /**
     * inferSecondOrderReturnTypeFromMethod; expect String
     * Q $$inferMethodReturnsString()
     * 
     */
    public static String inferSecondOrderReturnTypeFromMethod() {
        return inferMethodReturnsString();
    }

    /**
     * inferTypeFromBuiltinFunctionU globalChannel; method for USE expects a ChannelDirective (i.e. Value)
     * Q 
     * 
     */
    public static void inferTypeFromBuiltinFunction() {
        ReadWrite.use(globalChannel);
        return ;
    }

    /**
     * inferMethodReturnTypeFromBuiltinFunction; expect Value
     * Q globalChannel
     * 
     */
    public static Value inferMethodReturnTypeFromBuiltinFunction() {
        return globalChannel;
    }

}
