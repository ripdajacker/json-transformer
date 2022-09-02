package dk.mehmedbasic.jsontransform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy;
import dk.mehmedbasic.jsonast.conversion.JacksonConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * A test of a quite large tree.
 */
public class TimeQueriesOnExtremeTreesTest {

  private JsonDocument document;

  @Before
  public void prepare() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    JsonNode tree = mapper.readTree(
        new FileInputStream("src/main/resources/file_1_million.json"));

    document = JacksonConverter.asTransformable(tree, new InlineIdsNamingStrategy());
  }

  @Test
  public void testSelectByName() {
    TaskTimer.timeQuery("node", document);
  }

  @Test
  @SuppressWarnings("UnstableApiUsage")
  public void testSelectByNameMultipleTimes() {
    var permutations = Collections2.permutations(List.of("node", "hancock", "portapotty", "horse"));
    for (List<String> permutation : permutations) {
      TaskTimer.timeQuery(String.join(" ", permutation), document);
      TaskTimer.timeQuery(String.join(" > ", permutation), document);
    }
  }

  @Test
  public void testReading() throws Exception {
    for (int i = 0; i < 2; i++) {
      prepare();
    }
  }

  @Test
  public void testSubQueries() {
    TaskTimer.timeQuery("person > name > lastName", document);
  }
}
