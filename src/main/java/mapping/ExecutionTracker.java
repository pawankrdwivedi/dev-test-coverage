package mapping;

import java.io.File;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionTracker {
    private static final ThreadLocal<String> currentTest = new ThreadLocal<>();
    private static final Map<String, Set<String>> testToMethods = new HashMap<>();

    public static void setCurrentTest(Class<?> testClass, String testMethod) {
        String fullTestName = testClass.getSimpleName() + "." + testMethod;
        currentTest.set(fullTestName);
        testToMethods.putIfAbsent(fullTestName, new HashSet<>());
    }

    public static void logMethod(String className, String methodName) {
        String test = currentTest.get();
        if (test != null) {
            testToMethods.get(test).add(className + "." + methodName);
        }
    }

    public static void writeReport(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(path);
        // Ensure parent directories exist
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        Map<String, Object> existingData = new HashMap<>();
        if (file.exists() && file.length() > 0) {
            existingData = mapper.readValue(file, Map.class);
        }

        // merge new data
        existingData.putAll(testToMethods);

        // write back pretty-printed JSON
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, existingData);

        File projectDir = new File(ConfigReader.getProperty("proj.dir"));
        SourceAnalyzer.listClassesAndMethods(projectDir);
        FindUntestedMethods.untestedDevMethods();
        GenerateHtmlReport.generateHtmlReport();
    }
}
