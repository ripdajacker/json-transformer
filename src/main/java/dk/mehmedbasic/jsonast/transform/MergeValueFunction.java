package dk.mehmedbasic.jsonast.transform;

import dk.mehmedbasic.jsonast.JsonArrayNode;
import dk.mehmedbasic.jsonast.JsonObjectNode;
import dk.mehmedbasic.jsonast.JsonValueNode;

/**
 * A function called when values are to be merged.
 */
public abstract class MergeValueFunction {

  public void applyValue(JsonValueNode source, JsonValueNode destination) {
    // Intentionally left empty
  }

  public void applyObject(JsonObjectNode source, JsonValueNode destination) {
    // Intentionally left empty
  }

  public void applyArray(JsonArrayNode source, JsonValueNode destination) {
    // Intentionally left empty
  }
}
