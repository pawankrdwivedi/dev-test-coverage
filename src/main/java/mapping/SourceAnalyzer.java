package mapping;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SourceAnalyzer {
    private static final Map<String, Set<String>> devMethods = new HashMap<>();



    public static void listClassesAndMethods(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                listClassesAndMethods(file);
            } else if (file.getName().endsWith(".java")) {
                CompilationUnit cu = StaticJavaParser.parse(file);
                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                    System.out.println("Class: " + cls.getName());
                    devMethods.putIfAbsent(String.valueOf(cls.getName()), new LinkedHashSet<>());
                    List<MethodDeclaration> methods = cls.getMethods();
                    for (MethodDeclaration method : methods) {
                        System.out.println("  Method: " + method.getName());
                        devMethods.get(String.valueOf(cls.getName())).add(String.valueOf(method.getName()));
                    }
                });
                new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                        .writeValue(new java.io.File(ConfigReader.getProperty("dev-methods.location")), devMethods);
            }
        }
    }
}

