package mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenerateHtmlReport {


    public static void generateHtmlReport() throws Exception {
        Path testDevJsonPath = Path.of(ConfigReader.getProperty("test-method-mapping.location"));
        Path uncoveredDevJsonPath = Path.of(ConfigReader.getProperty("untested-dev-methods.location"));
        Path testExecutionStatusJsonPath=Path.of(ConfigReader.getProperty("test-case-status.location"));
        Path outputHtml = Path.of(ConfigReader.getProperty("html-report.location"));

        ObjectMapper mapper = new ObjectMapper();
        /***
         * 1️⃣ Read JSON inputS
          */
        //Test Case Execution Status
        Map<String, String> testExecutionStatus = new java.util.HashMap<>();
        var root = mapper.readTree(Files.readString(testExecutionStatusJsonPath));
        var tests = root.path("tests");
        for (var t : tests) {
            String key = t.path("className").asText()
                    + "." + t.path("methodName").asText();
            String status = t.path("status").asText("UNKNOWN");
            testExecutionStatus.put(key, status);
        }
           //Test<--> Dev Mapping
        Map<String, List<String>> testToMethods =
                mapper.readValue(
                        Files.readString(testDevJsonPath),
                        new TypeReference<>() {}
                );
       //Uncovered Dev Methods
        List<String> uncoveredDevMethods =
                mapper.readValue(
                        Files.readString(uncoveredDevJsonPath),
                        new TypeReference<>() {}
                );

        // 2️⃣ Generate HTML
        String html = generateHtml(testToMethods, uncoveredDevMethods,testExecutionStatus);

        // 3️⃣ Write HTML output
        Files.writeString(outputHtml, html);
        System.out.println("✅ HTML report generated: " + outputHtml.toAbsolutePath());
    }

    private static String generateHtml(
            Map<String, List<String>> testToMethods,
            List<String> uncoveredDevMethods,
            Map<String, String> testExecutionStatus
    )
    {
        StringBuilder html = new StringBuilder();
        // ---------------- PRE-CALC ----------------
        List<String> noCoverageTests = new ArrayList<>();
        for (var e : testToMethods.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                noCoverageTests.add(e.getKey());
            }
        }
        // ---------------- HTML HEADER ----------------
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<title>Test ↔ Dev Coverage Report</title>\n")

                // ---------- CSS ----------
                .append("<style>\n")
                .append("body { font-family: Arial; padding: 20px; }\n")
                .append(".tabs { margin-bottom: 15px; }\n")
                .append(".tab { display: inline-block; padding: 10px 15px; cursor: pointer; background: #eee; margin-right: 5px; }\n")
                .append(".tab.active { background: #4285f4; color: white; }\n")
                .append(".tab-content { display: none; }\n")
                .append(".tab-content.active { display: block; }\n")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }\n")
                .append("th, td { border: 1px solid #ccc; padding: 8px; }\n")
                .append("th { background: #f4f4f4; }\n")
                //.append("tr.test-row { background: #e8f0fe; cursor: pointer; font-weight: bold; }\n")
                .append("tr.test-row { background: #e8f0fe; cursor: pointer; font-weight: normal; }\n")
                .append("tr.dev-row { display: none; background: #fafafa; }\n")
                .append("tr.no-dev { background-color: #ffebeb; color: #a00000; font-weight: normal; }\n")
                //.append("tr.no-dev { background-color: #ffebeb; color: #a00000; font-weight: bold; }\n")
                .append("</style>\n")
                // ---------- JS ----------
                .append("<script>\n")
                .append("function showTab(id) {\n")
                .append("  document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));\n")
                .append("  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));\n")
                .append("  document.getElementById(id).classList.add('active');\n")
                .append("  document.getElementById(id + '-tab').classList.add('active');\n")
                .append("}\n")
                .append("function toggle(cls) {\n")
                .append("  document.querySelectorAll('.' + cls).forEach(r => {\n")
                .append("    r.style.display = r.style.display === 'table-row' ? 'none' : 'table-row';\n")
                .append("  });\n")
                .append("}\n")
                .append("function filterTab(tabId, inputId) {\n")
                .append("  const q = document.getElementById(inputId).value.toLowerCase();\n")
                .append("  document.querySelectorAll('#' + tabId + ' tbody tr').forEach(row => {\n")
                .append("    row.style.display = row.innerText.toLowerCase().includes(q)\n")
                .append("      ? 'table-row' : 'none';\n")
                .append("  });\n")
                .append("}\n")
                .append("</script>\n")
                .append("</head>\n<body>\n");
        html.append("<h1 style='text-align:center; margin-bottom:20px;text-decoration: underline;'>")
                .append("Test Case Development Code Mapping Report")
                .append("</h1>\n");
        html.append("<div style='color:#003366; margin-bottom:20px;text-align:center;'>")
                .append("Automated Test ↔ Source Code Traceability</div>");
        // ---------------- TABS ----------------
        html.append("<div class='tabs' style='text-align:center; margin-bottom:20px;'>")
                //Tab 1- Test Case Execution Status
                .append("<div id='tab1-tab' class='tab active' ")
                .append("style='display:inline-block; font-weight:bold; margin-left:10px;' ")
                .append("onclick=\"showTab('tab1')\">")
                .append("Test Execution Status</div>")
                //Tab 2- Test ↔ Dev Covered Methods
                .append("<div id='tab2-tab' class='tab' ")
                .append("style='display:inline-block; font-weight:bold;' ")
                .append("onclick=\"showTab('tab2')\">")
                .append("Test ↔ Dev Covered Methods</div>")
                //Tab 3- Test ↔ Dev UnCovered Methods
                .append("<div id='tab3-tab' class='tab' ")
                .append("style='display:inline-block; font-weight:bold; margin-left:10px;' ")
                .append("onclick=\"showTab('tab3')\">")
                .append("Test ↔ Dev UnCovered Methods</div>")
                //Tab 4- Obsolete Test Cases
                .append("<div id='tab4-tab' class='tab' ")
                .append("style='display:inline-block; font-weight:bold; margin-left:10px;' ")
                .append("onclick=\"showTab('tab4')\">")
                .append("Obsolete Test Cases</div>")
                .append("</div>\n");
        // ============================================================
        // TAB 1 : TEST EXECUTION STATUS
        // ============================================================
        html.append("<div id='tab1' class='tab-content active'>\n");
        html.append("<h3>📊 Test Case Execution Status</h3>\n");
        html.append("<input ")
                .append("id='search-tab1' ")
                .append("onkeyup=\"filterTab('tab1','search-tab1')\" ")
                .append("placeholder='Search test execution status...' ")
                .append("style='width:300px; padding:6px; margin-bottom:10px;' />");
        html.append("<table>\n<thead><tr>")
                .append("<th>TestClass</th>")
                .append("<th>TestCase</th>")
                .append("<th>Status</th>")
                .append("</tr></thead><tbody>\n");
        for (var entry : testExecutionStatus.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            String status = entry.getValue();
            String color =
                    "PASSED".equals(status) ? "#d4edda" :
                            "FAILED".equals(status) ? "#f8d7da" :
                                    "SKIPPED".equals(status) ? "#fff3cd" :
                                            "#eeeeee";
            html.append("<tr style='background-color:")
                    .append(color).append(";'>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts[1]).append("</td>")
                    .append("<td>").append(status).append("</td>")
                    .append("</tr>\n");
        }
        html.append("</tbody></table>");
        html.append("</div>"); // END TAB 4
        // ============================================================
        // TAB 2 : TEST ↔ DEV MAPPING
        // ============================================================
        html.append("<div id='tab2' class='tab-content'>\n");
        html.append("<h3>✅ Test Cases with Dev Function Mapping</h3>\n");
        html.append("<input ")
                .append("id='search-tab2' ")
                .append("onkeyup=\"filterTab('tab2','search-tab2')\" ")
                .append("placeholder='Search test cases or dev methods...' ")
                .append("style='width:300px; padding:6px; margin-bottom:10px;' />");
        html.append("<table>\n<thead><tr>")
                .append("<th>TestClass</th><th>TestCase</th><th>DevClass</th><th>DevMethod</th>")
                .append("</tr></thead><tbody>\n");
        int idx = 0;
        for (var entry : testToMethods.entrySet()) {
            List<String> devMethods = entry.getValue();
            if (devMethods == null || devMethods.isEmpty()) continue;
            String[] testParts = entry.getKey().split("\\.");
            String grp = "grp" + idx++;

            html.append("<tr class='test-row'")
                    .append(grp).append("')\">")
                    .append("<td>").append(testParts[0]).append("</td>")
                    .append("<td>").append(testParts[1]).append("</td>")
                    .append("<td colspan='2'>")
                    .append("<a href='javascript:void(0)' ")
                    .append("onclick=\"toggle('").append(grp).append("')\" ")
                    .append("style='color:#1a73e8; text-decoration:underline;'>")
                    .append("Click to expand")
                    .append("</a>")
                    .append("</td>")
                    .append("</tr>\n");

            for (String dev : devMethods) {
                String[] devParts = dev.split("\\.");
                html.append("<tr class='dev-row ").append(grp).append("'>")
                        .append("<td></td><td></td>")
                        .append("<td>").append(devParts[0]).append("</td>")
                        .append("<td>").append(devParts[1]).append("</td>")
                        .append("</tr>\n");
            }
        }
        html.append("</tbody></table>");
        html.append("</div>"); // END TAB 1
        // ============================================================
        // TAB 3 : UNCOVERED DEV METHODS
        // ============================================================
        html.append("<div id='tab3' class='tab-content'>");
        html.append("<h3>❌ Dev Methods Not Covered by Any Test</h3>\n");
        html.append("<input ")
                .append("id='search-tab3' ")
                .append("onkeyup=\"filterTab('tab3','search-tab3')\" ")
                .append("placeholder='Search uncovered dev methods...' ")
                .append("style='width:300px; padding:6px; margin-bottom:10px;' />");
        html.append("<table>\n<thead><tr>")
                .append("<th>DevClass</th><th>DevMethod</th>")
                .append("</tr></thead><tbody>\n");

        for (String dev : uncoveredDevMethods) {
            String[] parts = dev.split("\\.");
            html.append("<tr class='no-dev'>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts[1]).append("</td>")
                    .append("</tr>\n");
        }

        html.append("</tbody></table>");
        html.append("</div>");
        // END TAB 3
        // ============================================================
        // TAB 4 : OBSOLETE TEST CASES
        // ============================================================
        html.append("<div id='tab4' class='tab-content'>\n");
        html.append("<h3>🚫 Obsolete Test Cases</h3>\n");
        html.append("<input ")
                .append("id='search-tab4' ")
                .append("onkeyup=\"filterTab('tab4','search-tab4')\" ")
                .append("placeholder='Search test cases...' ")
                .append("style='width:300px; padding:6px; margin-bottom:10px;' />");
        html.append("<table>\n<thead><tr>")
                .append("<th>TestClass</th><th>TestCase</th>")
                .append("</tr></thead><tbody>\n");
        for (String test : noCoverageTests) {
            String[] parts = test.split("\\.");
            html.append("<tr class='no-dev'>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts[1]).append("</td>")
                    .append("</tr>\n");
        }
        html.append("</tbody></table>");
        html.append("</div>"); // END TAB 4
        html.append("</body></html>");
        return html.toString();
    }
}
