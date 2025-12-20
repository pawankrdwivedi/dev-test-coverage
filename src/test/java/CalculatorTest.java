import app.calculator.Calculator;
import mapping.ConfigReader;
import mapping.ExecutionTracker;
import org.junit.jupiter.api.*;


public class CalculatorTest {

    Calculator calc;

    @BeforeEach
    void init(TestInfo info) {
        ExecutionTracker.setCurrentTest(this.getClass(), info.getTestMethod().get().getName());
    }

    @Test
    void test_Add() {
        calc = new Calculator();
        assert calc.dev_add(2, 3) == 5;
    }

    @Test
    void test_Subtract() {
        calc = new Calculator();
        assert calc.dev_subtract(5, 3) == 2;
    }

    @Test
    void test_Subtract1() {
        calc = new Calculator();
        assert calc.dev_subtract(6, 3) == 3;
    }

    @Test
    void test_SubtractMultiply() {
        calc = new Calculator();
        int i=calc.dev_subtract(6, 3);
        assert calc.dev_multiply(i, 3) == 9;
    }

    @Test
    void test_junk_test_cases() {
        String name="IRIS Software";
        assert name=="IRIS Software";
    }

    @AfterAll
    static void dumpMapping() throws Exception {
        ExecutionTracker.writeReport(ConfigReader.getProperty("test-method-mapping.location"));

    }
}
