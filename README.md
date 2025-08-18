# Instrumented Mapper

## 📦 Features
- Java Agent (JaCoCo) instrumentation
- JUnit 5 test cases
- Coverage report generation (XML)
- Mapping to JSON with CoverageMapper.java

## 🚀 Usage
```bash
mvn clean test
mvn jacoco:report
java -cp target/classes:target/test-classes:mapping -Dexec.mainClass=mapping.CoverageMapper
```
