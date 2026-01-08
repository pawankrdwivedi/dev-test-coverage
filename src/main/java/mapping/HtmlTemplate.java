package mapping;

public final class HtmlTemplate {

    private HtmlTemplate() {}

    public static String header() {
        return "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<title>Test ↔ Dev Coverage Report</title>\n"

                + "<style>\n"
                + "body { font-family: Arial; padding: 20px; }\n"
                + ".tabs { margin-bottom: 15px; text-align:center; }\n"
                + ".tab { display:inline-block; padding:10px 15px; cursor:pointer; background:#eee; margin-right:5px; font-weight:bold; }\n"
                + ".tab.active { background:#4285f4; color:white; }\n"
                + ".tab-content { display:none; }\n"
                + ".tab-content.active { display:block; }\n"
                + "table { width:100%; border-collapse:collapse; margin-top:10px; }\n"
                + "th, td { border:1px solid #ccc; padding:8px; }\n"
                + "th { background:#f4f4f4; }\n"
                + "tr.test-row { background:#e8f0fe; cursor:pointer; }\n"
                + "tr.dev-row { display:none; background:#fafafa; }\n"
                + "tr.no-dev { background:#ffebeb; color:#a00000; }\n"
                + "</style>\n"

                + "<script>\n"
                + "function showTab(id) {\n"
                + "  document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));\n"
                + "  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));\n"
                + "  document.getElementById(id).classList.add('active');\n"
                + "  document.getElementById(id + '-tab').classList.add('active');\n"
                + "}\n"

                // 🔥 FIXED toggle (robust)
                + "function toggle(cls) {\n"
                + "  document.querySelectorAll('.' + cls).forEach(r => {\n"
                + "    if (r.style.display === '' || r.style.display === 'none') {\n"
                + "      r.style.display = 'table-row';\n"
                + "    } else {\n"
                + "      r.style.display = 'none';\n"
                + "    }\n"
                + "  });\n"
                + "}\n"
                + "</script>\n"

                + "</head>\n"
                + "<body>\n"

                + "<h1 style='text-align:center; text-decoration:underline;'>\n"
                + "Test Case ↔ Development Code Mapping Report\n"
                + "</h1>\n"

                + "<div class='tabs'>\n"
                + "  <div id='tab1-tab' class='tab active' onclick=\"showTab('tab1')\">Test Execution Status</div>\n"
                + "  <div id='tab2-tab' class='tab' onclick=\"showTab('tab2')\">Test ↔ Dev Covered Methods</div>\n"
                + "  <div id='tab3-tab' class='tab' onclick=\"showTab('tab3')\">Uncovered Dev Methods</div>\n"
                + "  <div id='tab4-tab' class='tab' onclick=\"showTab('tab4')\">Obsolete Test Cases</div>\n"
                + "</div>\n";
    }

    public static String footer() {
        return "</body></html>";
    }
}
