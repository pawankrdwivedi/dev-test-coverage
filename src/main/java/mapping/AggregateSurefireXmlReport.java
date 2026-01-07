package mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.File;
import java.nio.file.*;

public class AggregateSurefireXmlReport {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void generateSurefireTestSuiteJson() throws Exception {
        Path surefireDir = Paths.get(ConfigReader.getProperty("surefire-report.location"));
        Path output = Paths.get(ConfigReader.getProperty("test-case-status.location"));
        aggregate(surefireDir, output);
        System.out.println("✅ Aggregated XML report created at: " + output);
    }

    public static void aggregate(Path inputDir, Path outputFile) throws Exception {
        ArrayNode testsArray = mapper.createArrayNode();
        int passed = 0, failed = 0, skipped = 0;
        DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DirectoryStream<Path> stream =
                Files.newDirectoryStream(inputDir, "*.xml");
        for (Path file : stream) {
            Document doc = builder.parse(file.toFile());
            Element suite = doc.getDocumentElement();

            String className = suite.getAttribute("name");
            NodeList testcases = suite.getElementsByTagName("testcase");
            for (int i = 0; i < testcases.getLength(); i++) {
                Element tc = (Element) testcases.item(i);
                ObjectNode testNode = mapper.createObjectNode();
                testNode.put("className", className);
                testNode.put("methodName", tc.getAttribute("name"));

                double timeSec = Double.parseDouble(tc.getAttribute("time"));
                testNode.put("timeMs", (int) (timeSec * 1000));

                if (tc.getElementsByTagName("failure").getLength() > 0 ||
                        tc.getElementsByTagName("error").getLength() > 0) {

                    failed++;
                    testNode.put("status", "FAILED");

                    Element failure = (Element)
                            (tc.getElementsByTagName("failure").getLength() > 0
                                    ? tc.getElementsByTagName("failure").item(0)
                                    : tc.getElementsByTagName("error").item(0));

                    testNode.put("errorMessage",
                            failure.getAttribute("message"));

                } else if (tc.getElementsByTagName("skipped").getLength() > 0) {

                    skipped++;
                    testNode.put("status", "SKIPPED");

                } else {

                    passed++;
                    testNode.put("status", "PASSED");
                }

                testsArray.add(testNode);
            }
        }

        ObjectNode summary = mapper.createObjectNode();
        summary.put("total", passed + failed + skipped);
        summary.put("passed", passed);
        summary.put("failed", failed);
        summary.put("skipped", skipped);

        ObjectNode finalReport = mapper.createObjectNode();
        finalReport.set("summary", summary);
        finalReport.set("tests", testsArray);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(outputFile.toFile(), finalReport);
    }
}