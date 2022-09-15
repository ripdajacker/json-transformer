package dk.mehmedbasic.jackson;

import org.assertj.core.data.Offset;

/**
 * JsonAssert.
 */
public final class JsonAssert {

  private JsonAssert() {

  }

  static void assertRootCount(JsonNodes nodes, int count) {
    assertThat(nodes.getRootCount()).isEqualTo(count);
  }

  static void assertSize(BaseNode node, int expectedSize) {
    if (node instanceof JsonArrayNode array) {
      assertThat(array.size()).isEqualTo(expectedSize);
    } else if (node instanceof JsonBaseNode object) {
      assertThat(object.size()).isEqualTo(expectedSize);
    } else {
      throw new AssertionError("Expected array or object but got: " + node);
    }
  }

  static void assertStringValue(BaseNode node, String expected) {
    assertThat(node).isInstanceOf(JsonValueNode.class);

    JsonValueNode valueNode = (JsonValueNode) node;
    assertThat(valueNode.stringValue()).isEqualTo(expected);
  }

  static void assertIntValue(BaseNode node, int expected) {
    assertThat(node).isInstanceOf(JsonValueNode.class);

    JsonValueNode valueNode = (JsonValueNode) node;
    assertThat(valueNode.intValue()).isEqualTo(expected);
  }

  static void assertDoubleValue(BaseNode node, double expected) {
    assertThat(node).isInstanceOf(JsonValueNode.class);

    JsonValueNode valueNode = (JsonValueNode) node;
    assertThat(valueNode.doubleValue()).isEqualTo(expected, Offset.offset(0.0001d));
  }
}
