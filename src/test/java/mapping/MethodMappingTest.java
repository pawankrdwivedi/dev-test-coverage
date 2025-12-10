package mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.io.File;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class MethodMappingTest {

    @Test
    @DisplayName("Verify method-test mapping JSON is generated and non-empty")
    public void verifyMappingFile() throws Exception {
        String path = ConfigReader.getProperty("test-method-mapping.location");
        File file = new File(path);
        // Ensure the file exists
        assertTrue(file.exists(), "Mapping file does not exist: " + path);
        // Read JSON content
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> data = mapper.readValue(file, Map.class);
        // The mapping should contain at least one entry
        assertFalse(data.isEmpty(), "Mapping JSON is empty");
    }
}
