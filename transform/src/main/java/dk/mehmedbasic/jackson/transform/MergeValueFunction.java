package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.jackson.JsonArrayNode;
import dk.mehmedbasic.jackson.JsonBaseNode;
import dk.mehmedbasic.jackson.JsonValueNode;

/**
 * A function called when values are to be merged.
 */
public abstract class MergeValueFunction {

  public void applyValue(JsonValueNode source, JsonValueNode destination) {
    // Intentionally left empty
  }

  public void applyObject(JsonBaseNode source, JsonValueNode destination) {
    // Intentionally left empty
  }

  public void applyArray(JsonArrayNode source, JsonValueNode destination) {
    // Intentionally left empty
  }
}
