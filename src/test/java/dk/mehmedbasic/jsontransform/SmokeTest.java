package dk.mehmedbasic.jsontransform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.mehmedbasic.jsonast.JsonDocument;
import dk.mehmedbasic.jsonast.JsonType;
import dk.mehmedbasic.jsonast.JsonValueNode;
import dk.mehmedbasic.jsonast.conversion.JacksonConverter;
import dk.mehmedbasic.jsonast.transform.MergeValueFunction;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Examples used in my thesis.
 */
public class SmokeTest {

  private JsonDocument document;

  @Before
  public void prepare() throws IOException {
    document = JsonDocument.parse(new FileInputStream("src/main/resources/thesis-example.json"));
  }

  @Test
  public void addValue() {
    document.transform("person").add("age", 60d).apply();
  }

  @Test
  public void addObject() {
    document.transform("person").add("microphone", JsonType.Object).apply();
    document.transform("person microphone").add("type", "cardioid").apply();
  }

  @Test
  public void renameChild() {
    for (int i = 0; i < 10000; i++) {
      document.transform("person").renameChild("name", "navn").apply();
    }
  }

  @Test
  public void renameField() {
    document.transform("person name").renameTo("navn").apply();
  }

  @Test
  public void deleteField() {
    document.transform("person").deleteChild("name").apply();
  }

  @Test
  public void partition() {
    document.transform("person")
        .partition(List.of(List.of("value1", "name"), List.of("value2", "occupation"))).apply();
    document.transform("").deleteChild("person").apply();
  }

  @Test
  public void traversal() {
    document.transform("person").renameTo("bill").apply().transform("name").renameTo("namen")
        .apply();
    printOut();
  }

  @Test
  public void multipleAdditions() {
    document.transform("person").add("age", 60d).add("pet", JsonType.Object).apply()
        .transform("pet").add("type", "dog").add("name", "bingo").apply();
  }

  @Test
  public void verbatimAddition() {
    document.transform("person").add("age", 60d)
        .addJson("pet", "{\"type\":\"dog\", \"name\": \"bingo\"}")
        .renameChild("pet", "bingo_the_dog").apply();
  }

  @Test
  public void move() {
    document.select("person").transform().addJson("array", "[]").apply();
    document.transform("name").moveTo("array").apply();
  }

  @Test
  public void mergeAndRename() {
    MergeValueFunction mergeFunction = new MergeValueFunction() {
      @Override
      public void applyValue(final JsonValueNode source, final JsonValueNode destination) {
        destination.setValue(source.stringValue() + ", " + destination.stringValue());
      }
    };

    document.transform("name").merge("occupation", mergeFunction).apply().select("name").parent()
        .transform().deleteChild("name").apply().transform("occupation")
        .renameTo("name_and_occupation").apply();
  }

  @Test
  public void simpleMerge() {
    MergeValueFunction mergeFunction = new MergeValueFunction() {
      @Override
      public void applyValue(final JsonValueNode source, final JsonValueNode destination) {
        destination.setValue(source.stringValue() + ", " + destination.stringValue());
      }
    };

    document.transform("name").merge("occupation", mergeFunction).apply();
  }

  @Test
  public void manipulateValues() {
    document.transform("person").manipulateValue("name", it -> {
      String newValue = it.stringValue().toLowerCase();
      it.setValue(newValue);
    }).apply();
  }

  @After
  public void printOut() {
    JsonNode node = JacksonConverter.asJacksonNode(document);
    String string;
    try {
      string = new ObjectMapper().writer().writeValueAsString(node);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    System.out.println(string);
    System.out.println();
  }
}
