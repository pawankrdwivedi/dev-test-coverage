package com.iris.automation.app.calculator;

public class Calculator {
    public int dev_add(int a, int b) {
        //ExecutionTracker.logMethod(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        return a + b;
    }
    public int dev_subtract(int a, int b) {
        //ExecutionTracker.logMethod(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        return a - b;
    }
    public int dev_multiply(int a, int b) {
        //ExecutionTracker.logMethod(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        return a * b;
    }
    public int dev_divide(int a, int b) {
        //ExecutionTracker.logMethod(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
        if (b == 0) throw new IllegalArgumentException("Divide by zero");
        return a / b;
    }
}
