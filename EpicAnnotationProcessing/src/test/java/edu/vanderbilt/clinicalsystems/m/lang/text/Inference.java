
package edu.vanderbilt.clinicalsystems.m.lang.text;


public class Inference {

    public double globalInteger;
    public boolean globalBoolean;
    public String globalString;
    public int globalDouble;

    /**
     * inferMethodReturnsString; expect String
     * 
     * 
     */
    public String inferMethodReturnsString() {
        return "foobar";
    }

    /**
     * inferMethodReturnsValue; expect Value
     * N x
     * S x("foo")="bar"
     * 
     * 
     */
    public String inferMethodReturnsValue() {
        String x = "";
        x = "bar";
        return x;
    }

    /**
     * inferMethodReturnsVoid; expect void
     * 
     * 
     */
    public void inferMethodReturnsVoid() {
        return ;
    }

    /**
     * inferLocalVariableType; expect boolean, String, Integer, double
     * N localBoolean,localString,localInteger,localDouble
     * S localBoolean=3!7
     * S localString=3_7
     * S localInteger=3/7
     * S localDouble=3\7
     * 
     * 
     */
    public void inferLocalVariableType() {
        boolean localBoolean = false;
        String localString = "";
        double localInteger = 0.0D;
        int localDouble = 0;
        localBoolean = true;
        localString = ("3"+"7");
        localInteger = (3.0D/ 7.0D);
        localDouble = (3 / 7);
        return ;
    }

    /**
     * inferSecondOrderReturnTypeFromMethod; expect String
     * 
     * 
     */
    public String inferSecondOrderReturnTypeFromMethod() {
        return inferMethodReturnsString();
    }

    /**
     * INFERENCE; see edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaWriterTest.canInferTypes
     * 
     * 
     */
    public void main() {
        return ;
    }

    /**
     * inferGlobal; expect expect boolean, String, Integer, double
     * S globalBoolean=3!7
     * S globalString=3_7
     * S globalInteger=3/7
     * S globalDouble=3\7
     * 
     * 
     */
    public void inferGlobal() {
        globalBoolean = true;
        globalString = ("3"+"7");
        globalInteger = (3.0D/ 7.0D);
        globalDouble = (3 / 7);
        return ;
    }

    /**
     * inferSecondOrderReturnTypeFromVariable; expect String
     * 
     * 
     */
    public String inferSecondOrderReturnTypeFromVariable() {
        return globalString;
    }

    /**
     * inferMethodReturnsInteger; expect int
     * 
     * 
     */
    public int inferMethodReturnsInteger() {
        return  1;
    }

    /**
     * inferMethodReturnsDouble; expect double
     * 
     * 
     */
    public double inferMethodReturnsDouble() {
        return  3.14159D;
    }

}
