package dk.mehmedbasic.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.mehmedbasic.jackson.transform.ManipulateValueFunction;
import dk.mehmedbasic.jackson.transform.MergeValueFunction;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test transforms
 */
public class TestTransform {

  private JsonDocument document;

  @Before
  public void setup() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode tree = mapper.readTree(new FileInputStream("src/main/resources/move-rename.json"));
    document = JacksonConverter.asTransformable(tree);
  }

  @Test
  public void rename() {
    document.transform("residents").renameTo("ned").apply();
    JsonAssert.assertRootCount(document.select("ned"), 2);
  }

  @Test
  public void moveUpward() {
    document.transform("residents").moveTo("ned").apply();

    BaseNode residents = document.selectSingle("residents").orElseThrow();
    assertThat(residents.getParent().getIdentifier().getName()).isEqualTo("ned");
    JsonAssert.assertSize(residents.getParent(), 2);

    BaseNode castle = document.selectSingle("castle-black").orElseThrow();
    assertThat(castle).isInstanceOf(JsonBaseNode.class);
    JsonAssert.assertSize(castle, 0);
  }

  @Test
  public void moveToSibling() {
    JsonNodes names = document.select("name");

    JsonAssert.assertRootCount(names, 3);

    List<BaseNode> roots = new ArrayList<>(names.getRoots());
    JsonAssert.assertStringValue(roots.get(0), "Jon Snow");
    JsonAssert.assertStringValue(roots.get(1), "Aemon Targaryen");
    JsonAssert.assertStringValue(roots.get(2), "Ratty McRatson");
  }

  @Test
  public void manipulateString() {
    BaseNode before = document.selectSingle("status").orElseThrow();
    JsonAssert.assertStringValue(before, "alive");

    ManipulateValueFunction function = node -> node.setValue(
        node.stringValue() + " until end of Season 5");

    document.transform("status").manipulateValue(function).apply();

    BaseNode after = document.selectSingle("status").orElseThrow();
    JsonAssert.assertStringValue(after, "alive until end of Season 5");
  }

  @Test
  public void manipulateInt() {
    String selector = "age";
    BaseNode before = document.selectSingle(selector).orElseThrow();

    JsonAssert.assertIntValue(before, 16);

    ManipulateValueFunction ageOneYear = node -> node.setValue(node.intValue() + 1);
    document.transform(selector).manipulateValue(ageOneYear).apply();

    JsonNodes ages = document.select(selector);
    ArrayList<BaseNode> roots = new ArrayList<>(ages.getRoots());
    JsonAssert.assertIntValue(roots.get(0), 17);
    JsonAssert.assertIntValue(roots.get(1), 105);
  }

  @Test
  public void mergeStrings() {
    String selector = "title";
    BaseNode before = document.selectSingle(selector).orElseThrow();
    JsonAssert.assertStringValue(before, "Maester");

    MergeValueFunction function = new MergeValueFunction() {
      @Override
      public void applyValue(JsonValueNode source, JsonValueNode destination) {
        destination.setValue(destination.getValue() + ", " + source.getValue());
      }
    };

    document.transform(selector).merge("castle-black name", function).apply();

    JsonNodes ages = document.select("name");
    List<BaseNode> roots = new ArrayList<>(ages.getRoots());
    JsonAssert.assertStringValue(roots.get(0), "Jon Snow");
    JsonAssert.assertStringValue(roots.get(1), "Aemon Targaryen, Maester");
  }

  @Test
  public void deleteChildByName() {
    String selector = "title";
    BaseNode before = document.selectSingle(selector).orElseThrow();
    JsonAssert.assertStringValue(before, "Maester");

    document.transform("castle-black .object").deleteChild("title").apply();

    JsonNodes titles = document.select(selector);
    JsonAssert.assertRootCount(titles, 0);
    assertThat(before.getParent()).isNull();
  }

  @Test
  public void deleteChildByIndex() {
    String selector = "residents";
    BaseNode before = document.selectSingle(selector).orElseThrow();
    JsonAssert.assertSize(before, 2);

    document.transform("castle-black residents").deleteChild(0).apply();

    JsonNodes residents = document.select(selector);
    Assert.assertEquals("Selection should have 1 child", 1, residents.getRootCount());

    BaseNode array = residents.getRoots().iterator().next();
    BaseNode child = array.get(0);
    JsonAssert.assertStringValue(child.get("name"), "Ratty McRatson");
  }

  @Test
  public void addSimpleValue() {
    BaseNode jonSnow = document.selectSingle("son").orElseThrow();

    String addedSelector = "addedValue";
    assertThat(jonSnow.get(addedSelector)).isNull();

    document.transform("son").add(addedSelector, 42d).apply();

    assertThat(jonSnow.get(addedSelector)).isNotNull();
    JsonAssert.assertDoubleValue(jonSnow.get(addedSelector), 42);
  }

  @Test
  public void addArray() {
    BaseNode jonSnow = document.selectSingle("son").orElseThrow();

    String addedSelector = "addedValue";
    assertThat(jonSnow.get(addedSelector)).isNull();

    document.transform("son").add(addedSelector, JsonType.Array).apply();

    document.transform(addedSelector).addValue(42d).apply();

    assertThat(jonSnow.get(addedSelector)).isNotNull();
    assertThat(jonSnow.get(addedSelector)).isInstanceOf(JsonArrayNode.class);

    JsonArrayNode arrayNode = (JsonArrayNode) jonSnow.get(addedSelector);
    assertThat(arrayNode.size()).isEqualTo(1);

    JsonAssert.assertDoubleValue(arrayNode.get(0), 42);
  }

  @Test
  public void addObject() {
    BaseNode jonSnow = document.selectSingle("son").orElseThrow();

    String addedSelector = "addedValue";
    Assert.assertNull("The object should not have a value named 'addedValue'",
        jonSnow.get(addedSelector));

    document.transform("son").add(addedSelector, JsonType.Object).apply();

    document.transform(addedSelector).add("fortyTwo", 42d).apply();

    BaseNode newObject = jonSnow.get(addedSelector);
    Assert.assertNotNull("The object should now have a value named 'addedValue'", newObject);
    Assert.assertTrue("The new node should be an array", newObject.isObject());

    JsonBaseNode objectNode = (JsonBaseNode) jonSnow.get(addedSelector);
    JsonAssert.assertSize(objectNode, 1);
    JsonAssert.assertDoubleValue(objectNode.get("fortyTwo"), 42d);
  }

  @Test
  public void partitionNode() throws JsonProcessingException {
    BaseNode ned = document.selectSingle("ned").orElseThrow();
    JsonAssert.assertSize(ned, 1);

    document.transform("son")
        .partition(List.of(List.of("name_and_status", "name", "status"), List.of("age2", "age")))
        .apply();

    ned = document.selectSingle("ned").orElseThrow();
    JsonAssert.assertSize(ned, 3);
    assertThat(ned.get("age2")).isNotNull();
    assertThat(ned.get("son")).isNotNull();

    BaseNode nameAndStatus = ned.get("name_and_status");
    assertThat(nameAndStatus).isNotNull();

    assertThat(nameAndStatus.get("name")).isNotNull();
    assertThat(nameAndStatus.get("status")).isNotNull();

    BaseNode nameNode = nameAndStatus.get("name");
    JsonAssert.assertStringValue(nameNode, "Jon Snow");
    JsonAssert.assertStringValue(nameAndStatus.get("status"), "alive");

    JsonNode node = JacksonConverter.asJacksonNode(document);
    String string = new ObjectMapper().writer().writeValueAsString(node);
    System.out.println(string);
  }
}
