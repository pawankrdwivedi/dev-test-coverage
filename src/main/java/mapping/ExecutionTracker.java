package mapping;

import java.util.*;

public class ExecutionTracker {
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();
    private static final Map<String, Set<String>> testToMethods = new HashMap<>();

    public static void setCurrentTest(String testName) {
        currentTest.set(testName);
        testToMethods.putIfAbsent(testName, new HashSet<>());
    }

    public static void logMethod(String className, String methodName) {
        String test = currentTest.get();
        if (test != null) {
            testToMethods.get(test).add(className + "." + methodName);
        }
    }

    public static void writeReport(String path) throws Exception {
        new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValue(new java.io.File(path), testToMethods);
    }
}
