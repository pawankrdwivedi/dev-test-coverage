package mapping;

public class GenerateCustomReport {

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Generating custom reports...");
        AggregateSurefireXmlReport.generateSurefireTestSuiteJson();
        GenerateHtmlReport.generateHtmlReport();
        System.out.println("✅ Custom reports generated successfully.");
    }
}
