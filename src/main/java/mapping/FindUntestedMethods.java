package mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class FindUntestedMethods {

    public static void untestedDevMethods() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, List<String>> devJson = mapper.readValue(
                new File(ConfigReader.getProperty("dev-methods.location")),
                new TypeReference<Map<String, List<String>>>() {}
        );

        Map<String, List<String>> testMappingJson = mapper.readValue(
                new File(ConfigReader.getProperty("test-method-mapping.location")),
                new TypeReference<Map<String, List<String>>>() {}
        );

        Set<String> devMethods = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : devJson.entrySet()) {
            String className = entry.getKey();
            for (String method : entry.getValue()) {
                devMethods.add(className + "." + method);
            }
        }

        Set<String> testedMethods = new HashSet<>();
        for (List<String> list : testMappingJson.values()) {
            testedMethods.addAll(list);
        }

        // Identify untested methods
        Set<String> untested = new HashSet<>(devMethods);
        untested.removeAll(testedMethods);
        new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValue(new java.io.File(ConfigReader.getProperty("untested-dev-methods.location")), untested);

        System.out.println("=== Untested Methods ===");
        if (untested.isEmpty()) {
            System.out.println("✅ All dev methods are covered by tests!");
        } else {
            untested.forEach(method -> System.out.println("❌" + method));
        }
    }
}
