# Dev Code with Test Script Execution Mapper

## 📦 Features
- Java Agent (JaCoCo) instrumentation
- JUnit 5 test cases
- Coverage report generation (XML)
- Mapping of Development code functions asscoiated with Test Scripts when executed

## 🚀 Usage
```bash
mvn clean test
mvn jacoco:report
```

## 🖥️ Output
```bash
Jacoco Report: ./target/site/jacoco/mapping/index.html
Dev Test Mapper: ./target/test-method-mapping.json
```
