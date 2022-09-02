package dk.mehmedbasic.jsonast.conversion;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonIdentifier;
import java.util.ArrayList;
import java.util.List;

/**
 * One-to-one conversion of names
 */
public class DefaultNamingStrategy implements ConversionNamingStrategy {

  @Override
  public List<StringPair> toJacksonInArray(BaseNode node) {
    return new ArrayList<>();
  }

  @Override
  public String toJacksonName(BaseNode node) {
    return node.getIdentifier().getName();
  }

  @Override
  public JsonIdentifier toTransformableName(String name, BaseNode objectNode) {
    return new JsonIdentifier(name);
  }
}
