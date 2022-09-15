package dk.mehmedbasic.jackson.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import dk.mehmedbasic.jackson.BaseNode;
import dk.mehmedbasic.jackson.JsonDocument;
import dk.mehmedbasic.jackson.JsonNodes;
import dk.mehmedbasic.jackson.JsonType;
import java.util.ArrayList;
import java.util.List;

/**
 * A json transformer builder.
 * <p>
 * This small class builds and executes transformation functions.
 */
public final class Transformer {

  private final List<TransformationFunction> functions = new ArrayList<>();
  private final String selector;
  private final JsonNodes destination;

  public Transformer(String selector, JsonNodes destination) {
    this.selector = selector;
    this.destination = destination;
  }

  public Transformer renameTo(String newName) {
    functions.add(new Renamer(newName));
    return this;
  }

  public Transformer renameChild(String from, String to) {
    functions.add(new Renamer(from, to));
    return this;
  }

  public Transformer moveTo(String selector) {
    functions.add(new Mover(selector));
    return this;
  }

  public Transformer merge(String selector) {
    return merge(selector, null);
  }

  public Transformer deleteChild(String childName) {
    functions.add(new Deleter(childName));
    return this;
  }

  public Transformer deleteChild(int index) {
    functions.add(new Deleter(index));
    return this;
  }

  public Transformer merge(String selector, MergeValueFunction function) {
    functions.add(new Merger(selector, function));
    return this;
  }

  public Transformer manipulateValue(ManipulateValueFunction function) {
    functions.add(new Manipulator(-1, null, function));
    return this;
  }

  public Transformer manipulateValue(int childIndex, ManipulateValueFunction function) {
    functions.add(new Manipulator(childIndex, null, function));
    return this;
  }

  public Transformer manipulateValue(String childName, ManipulateValueFunction function) {
    functions.add(new Manipulator(-1, childName, function));
    return this;
  }

  public Transformer add(String name, JsonType type) {
    functions.add(new AddValue(name, type, null));
    return this;
  }

  public Transformer add(String name, JsonType type, Object value) {
    functions.add(new AddValue(name, type, value));
    return this;
  }

  public Transformer addJson(String name, String jsonString) {
    JsonDocument parsed;
    try {
      parsed = JsonDocument.parse(jsonString);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    if (parsed.getRoots().isEmpty()) {
      return this;
    }

    BaseNode root = parsed.getRoots().iterator().next();
    functions.add(new AddValue(name, JsonType.fromNode(root), root));
    return this;
  }

  public Transformer addValue(Object value) {
    functions.add(new AddValue(null, JsonType.Value, value));
    return this;
  }

  public Transformer add(String name, Object value) {
    functions.add(new AddValue(name, JsonType.Value, value));
    return this;
  }

  public Transformer partition(List<List<String>> partitions) {
    functions.add(new Partitioner(partitions));
    return this;
  }

  public JsonNodes apply() {
    if (destination != null) {
      for (TransformationFunction function : functions) {
        function.apply(destination.getDocument(), destination.select(selector));
      }
    }

    return destination;
  }
}
