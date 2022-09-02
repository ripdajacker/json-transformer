package dk.mehmedbasic.jsonast.conversion;

import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonIdentifier;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Naming strategy for inline id
 */
public class InlineIdsNamingStrategy implements ConversionNamingStrategy {

  private static final Pattern PATTERN_NAME_AND_ID = Pattern.compile(
      "([A-Za-z0-9]+) #(-?[0-9a-f]+)\\s*$");

  @Override
  public List<StringPair> toJacksonInArray(BaseNode node) {
    return Collections.singletonList(new StringPair("@id", node.getIdentifier().getId()));
  }

  @Override
  public String toJacksonName(final BaseNode node) {
    if (node.getIdentifier().getId() != null) {
      return node.getIdentifier().getName() + " #" + node.getIdentifier().getId();
    }

    return node.getIdentifier().getName();
  }

  @Override
  public JsonIdentifier toTransformableName(String name, BaseNode objectNode) {
    return parseName(name);
  }

  private static JsonIdentifier parseName(String name) {
    JsonIdentifier identifier = new JsonIdentifier(name);
    if (name == null) {
      return identifier;
    }

    Matcher matcher = PATTERN_NAME_AND_ID.matcher(name);
    if (matcher.find()) {
      identifier.setName(matcher.group(1));
      identifier.setId(matcher.group(2));
    }

    return identifier;
  }
}
