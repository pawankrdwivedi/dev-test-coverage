package mapping;

public final class HtmlTemplate {

    private HtmlTemplate() {}

    public static String header() {
        return "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<title>Test ↔ Dev Coverage Report</title>\n"
            + "<style>\n"
            + "body{font-family:Arial;padding:20px;}\n"
            + ".tabs{text-align:center;margin-bottom:20px;}\n"
            + ".tab{display:inline-block;padding:10px 15px;cursor:pointer;background:#eee;margin-right:5px;font-weight:bold;}\n"
            + ".tab.active{background:#4285f4;color:#fff;}\n"
            + ".tab-content{display:none;}\n"
            + ".tab-content.active{display:block;}\n"
            + "table{width:100%;border-collapse:collapse;margin-top:10px;}\n"
            + "th,td{border:1px solid #ccc;padding:8px;}\n"
            + "th{background:#f4f4f4;}\n"
            + ".risk-high{background:#ffcccc;}\n"
            + ".risk-medium{background:#fff3cd;}\n"
            + ".risk-low{background:#e8f5e9;}\n"
            + "</style>\n"
            + "<script>\n"
            + "function showTab(id){\n"
            + "document.querySelectorAll('.tab-content').forEach(t=>t.classList.remove('active'));\n"
            + "document.querySelectorAll('.tab').forEach(t=>t.classList.remove('active'));\n"
            + "document.getElementById(id).classList.add('active');\n"
            + "document.getElementById(id+'-tab').classList.add('active');}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body>\n"
            + "<h1 style='text-align:center;text-decoration:underline;'>"
            + "Test Case ↔ Development Code Mapping Report</h1>\n";
    }

    public static String footer() {
        return "</body></html>";
    }
}
