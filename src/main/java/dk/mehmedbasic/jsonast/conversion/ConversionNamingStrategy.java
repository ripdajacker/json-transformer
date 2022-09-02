package dk.mehmedbasic.jsonast.conversion;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonIdentifier;
import java.util.List;

/**
 * Naming strategy for transformable nodes.
 */
public interface ConversionNamingStrategy {

  List<StringPair> toJacksonInArray(BaseNode node);

  String toJacksonName(BaseNode node);

  JsonIdentifier toTransformableName(String name, BaseNode objectNode);

  record StringPair(String name, String value) {

  }
}
