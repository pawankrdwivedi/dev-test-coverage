package mapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GenerateHtmlReport {

    public static void generateHtmlReport() throws Exception {

        Path testDevJsonPath = Path.of(ConfigReader.getProperty("test-method-mapping.location"));
        Path uncoveredDevJsonPath = Path.of(ConfigReader.getProperty("untested-dev-methods.location"));
        Path testExecutionStatusJsonPath = Path.of(ConfigReader.getProperty("test-case-status.location"));
        Path outputHtml = Path.of(ConfigReader.getProperty("html-report.location"));

        ObjectMapper mapper = new ObjectMapper();

        // ============================
        // 1️⃣ READ TEST EXECUTION STATUS
        // ============================
        Map<String, String> testExecutionStatus = new LinkedHashMap<>();

        var root = mapper.readTree(Files.readString(testExecutionStatusJsonPath));
        var tests = root.path("tests");

        for (var t : tests) {
            String key = t.path("className").asText()
                    + "." + t.path("methodName").asText();
            String status = t.path("status").asText("UNKNOWN");
            testExecutionStatus.put(key, status);
        }

        // ============================
        // 2️⃣ READ TEST ↔ DEV MAPPING
        // ============================
        Map<String, List<String>> testToMethods =
                mapper.readValue(
                        Files.readString(testDevJsonPath),
                        new TypeReference<>() {}
                );

        // ============================
        // 3️⃣ READ ALL DEV METHODS
        // ============================
        List<String> allDevMethods =
                mapper.readValue(
                        Files.readString(uncoveredDevJsonPath),
                        new TypeReference<>() {}
                );

        // ============================
        // 4️⃣ CALCULATE UNCOVERED METHODS
        // ============================
        Set<String> covered = new HashSet<>();
        for (List<String> methods : testToMethods.values()) {
            if (methods != null) covered.addAll(methods);
        }

        List<String> uncoveredDevMethods = new ArrayList<>();
        for (String dev : allDevMethods) {
            if (!covered.contains(dev)) {
                uncoveredDevMethods.add(dev);
            }
        }

        // ============================
        // 5️⃣ GENERATE HTML
        // ============================
        String html = generateHtml(testToMethods, uncoveredDevMethods, testExecutionStatus);

        Files.writeString(outputHtml, html);
        System.out.println("✅ HTML report generated: " + outputHtml.toAbsolutePath());
    }

    // ================================================================
    // HTML GENERATOR
    // ================================================================
    private static String generateHtml(
            Map<String, List<String>> testToMethods,
            List<String> uncoveredDevMethods,
            Map<String, String> testExecutionStatus
    ) {

        StringBuilder html = new StringBuilder();

        // ============================
        // PRE-CALC: OBSOLETE TESTS
        // ============================
        List<String> noCoverageTests = new ArrayList<>();
        for (var e : testToMethods.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                noCoverageTests.add(e.getKey());
            }
        }

        // ============================
        // HTML HEADER
        // ============================
        html.append("""
<!DOCTYPE html>
<html>
<head>
<title>Test ↔ Dev Coverage Report</title>
<style>
body { font-family: Arial; padding: 20px; }
.tabs { margin-bottom: 15px; text-align:center; }
.tab { display:inline-block; padding:10px 15px; cursor:pointer; background:#eee; margin-right:5px; font-weight:bold; }
.tab.active { background:#4285f4; color:white; }
.tab-content { display:none; }
.tab-content.active { display:block; }
table { width:100%; border-collapse:collapse; margin-top:10px; }
th, td { border:1px solid #ccc; padding:8px; }
th { background:#f4f4f4; }
tr.test-row { background:#e8f0fe; cursor:pointer; }
tr.dev-row { display:none; background:#fafafa; }
tr.no-dev { background:#ffebeb; color:#a00000; }
</style>

<script>
function showTab(id) {
  document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.getElementById(id).classList.add('active');
  document.getElementById(id + '-tab').classList.add('active');
}
function toggle(cls) {
  document.querySelectorAll('.' + cls).forEach(r => {
    r.style.display = r.style.display === 'table-row' ? 'none' : 'table-row';
  });
}
function filterTab(tabId, inputId) {
  const q = document.getElementById(inputId).value.toLowerCase();
  document.querySelectorAll('#' + tabId + ' tbody tr').forEach(row => {
    row.style.display = row.innerText.toLowerCase().includes(q) ? 'table-row' : 'none';
  });
}
</script>
</head>
<body>

<h1 style="text-align:center; text-decoration:underline;">
Test Case ↔ Development Code Mapping Report
</h1>
<div style="color:#003366; margin-bottom:20px;text-align:center;">
Automated Test ↔ Source Code Traceability
</div>

<div class="tabs">
  <div id="tab1-tab" class="tab active" onclick="showTab('tab1')">Test Execution Status</div>
  <div id="tab2-tab" class="tab" onclick="showTab('tab2')">Test ↔ Dev Covered Methods</div>
  <div id="tab3-tab" class="tab" onclick="showTab('tab3')">Uncovered Dev Methods</div>
  <div id="tab4-tab" class="tab" onclick="showTab('tab4')">Obsolete Tests</div>
</div>
""");

        // ============================================================
        // TAB 1 : TEST EXECUTION STATUS
        // ============================================================
        html.append("<div id='tab1' class='tab-content active'>");
        html.append("<h3>📊 Test Execution Status</h3>");
        html.append("<table><thead><tr><th>TestClass</th><th>TestCase</th><th>Status</th></tr></thead><tbody>");

        for (var e : testExecutionStatus.entrySet()) {
            String[] parts = e.getKey().split("\\.", 2);
            String status = e.getValue();
            String color =
                    "PASSED".equals(status) ? "#d4edda" :
                            "FAILED".equals(status) ? "#f8d7da" :
                                    "SKIPPED".equals(status) ? "#fff3cd" : "#eeeeee";

            html.append("<tr style='background:").append(color).append("'>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts.length > 1 ? parts[1] : "").append("</td>")
                    .append("<td>").append(status).append("</td></tr>");
        }

        html.append("</tbody></table></div>");

        // ============================================================
        // TAB 2 : TEST ↔ DEV MAPPING
        // ============================================================
        html.append("<div id='tab2' class='tab-content'>");
        html.append("<h3>✅ Test ↔ Dev Mapping</h3>");
        html.append("<table><thead><tr><th>TestClass</th><th>TestCase</th><th>DevClass</th><th>DevMethod</th></tr></thead><tbody>");

        int idx = 0;
        for (var entry : testToMethods.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;

            String[] testParts = entry.getKey().split("\\.", 2);
            String grp = "grp" + idx++;

            html.append("<tr class='test-row'>")
                    .append("<td>").append(testParts[0]).append("</td>")
                    .append("<td>").append(testParts.length > 1 ? testParts[1] : "").append("</td>")
                    .append("<td colspan='2'><a href='javascript:void(0)' onclick=\"toggle('")
                    .append(grp).append("')\">Expand</a></td></tr>");

            for (String dev : entry.getValue()) {
                int d = dev.lastIndexOf(".");
                String devClass = d > 0 ? dev.substring(0, d) : dev;
                String devMethod = d > 0 ? dev.substring(d + 1) : "";

                html.append("<tr class='dev-row ").append(grp).append("'>")
                        .append("<td></td><td></td>")
                        .append("<td>").append(devClass).append("</td>")
                        .append("<td>").append(devMethod).append("</td></tr>");
            }
        }

        html.append("</tbody></table></div>");

        // ============================================================
        // TAB 3 : UNCOVERED DEV METHODS
        // ============================================================
        html.append("<div id='tab3' class='tab-content'>");
        html.append("<h3>❌ Uncovered Dev Methods</h3>");
        html.append("<table><thead><tr><th>DevClass</th><th>DevMethod</th></tr></thead><tbody>");

        for (String dev : uncoveredDevMethods) {
            int d = dev.lastIndexOf(".");
            html.append("<tr class='no-dev'>")
                    .append("<td>").append(d > 0 ? dev.substring(0, d) : dev).append("</td>")
                    .append("<td>").append(d > 0 ? dev.substring(d + 1) : "").append("</td></tr>");
        }

        html.append("</tbody></table></div>");

        // ============================================================
        // TAB 4 : OBSOLETE TESTS
        // ============================================================
        html.append("<div id='tab4' class='tab-content'>");
        html.append("<h3>🚫 Obsolete Test Cases</h3>");
        html.append("<table><thead><tr><th>TestClass</th><th>TestCase</th></tr></thead><tbody>");

        for (String test : noCoverageTests) {
            String[] parts = test.split("\\.", 2);
            html.append("<tr class='no-dev'>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts.length > 1 ? parts[1] : "").append("</td></tr>");
        }

        html.append("</tbody></table></div>");
        html.append("</body></html>");

        return html.toString();
    }
}
