package dk.mehmedbasic.jsonast.selector;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import com.steadystate.css.parser.selectors.AndConditionImpl;
import com.steadystate.css.parser.selectors.AttributeConditionImpl;
import com.steadystate.css.parser.selectors.ClassConditionImpl;
import com.steadystate.css.parser.selectors.IdConditionImpl;
import com.steadystate.css.parser.selectors.PrefixAttributeConditionImpl;
import com.steadystate.css.parser.selectors.SubstringAttributeConditionImpl;
import dk.mehmedbasic.jsonast.BaseNode;
import dk.mehmedbasic.jsonast.JsonNodes;
import dk.mehmedbasic.jsonast.JsonObjectNode;
import dk.mehmedbasic.jsonast.JsonValueNode;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.function.Predicate;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

/**
 * The selector engine that parses and applies the selection rules
 */
public class JsonSelectionEngine {

  private final SelectorList selectorList;

  public JsonSelectionEngine(String selector) throws IOException {
    CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
    selectorList = parser.parseSelectors(new InputSource(new StringReader(selector)));
    if (selectorList == null) {
      throw new IllegalArgumentException("Selector '" + selector + "' could not be parsed");
    }
  }

  public Selector parse() {
    if (selectorList == null || selectorList.getLength() == 0) {
      return null;
    }

    return selectorList.item(0);
  }

  public JsonNodes execute(JsonNodes roots) {
    return execute(parse(), roots);
  }

  public JsonNodes execute(Selector selector, JsonNodes roots) {
    if (selector instanceof ConditionalSelector conditionalSelector) {
      JsonNodes subtree = execute(conditionalSelector.getSimpleSelector(), roots);
      return subtree.filter(fromCondition(conditionalSelector.getCondition()));
    } else if (selector instanceof ElementSelector elementSelector) {
      return roots.findByName(elementSelector.getLocalName());
    } else if (selector instanceof DescendantSelector descendantSelector) {
      JsonNodes subtree = execute(descendantSelector.getAncestorSelector(), roots);
      return execute(descendantSelector.getSimpleSelector(), subtree);
    }

    return null;
  }

  private static NodeFilter fromCondition(final Condition condition) {
    if (condition instanceof AndConditionImpl andCondition) {
      return new AndFilter(fromCondition(andCondition.getFirstCondition()),
          fromCondition(andCondition.getSecondCondition()));
    } else if (condition instanceof PrefixAttributeConditionImpl prefixAttr) {
      return new PropertyConditionSelector(prefixAttr.getLocalName(),
          value -> value.startsWith(prefixAttr.getValue()));
    } else if (condition instanceof ClassConditionImpl classCondition) {
      return new ClassConditionSelector(classCondition.getValue());
    } else if (condition instanceof IdConditionImpl idCondition) {
      return new IdConditionSelector(idCondition.getValue());
    } else if (condition instanceof AttributeConditionImpl attributeCondition) {
      final String name = attributeCondition.getLocalName();
      final String value = attributeCondition.getValue();
      if (value != null && !value.isEmpty()) {
        return new PropertyConditionSelector(name, property -> property.equals(value));
      } else {
        return new PropertyNameCondition(propertyName -> propertyName.equals(name));
      }
    } else if (condition instanceof final SubstringAttributeConditionImpl substringCondition) {
      return new PropertyConditionSelector(
          substringCondition.getLocalName(),
          value -> value.contains(substringCondition.getValue()));
    }

    throw new UnsupportedOperationException(
        "The given CSS condition is not supported: " + condition.getClass().getName() + ", "
            + condition);
  }

  private static class ClassConditionSelector extends NodeFilter {

    private final String className;

    public ClassConditionSelector(String className) {
      this.className = className;
    }

    @Override
    public boolean apply(BaseNode node, Integer index) {
      for (String thatName : node.getIdentifier().getClasses()) {
        if (Objects.equals(thatName, className)) {
          return true;
        }
      }

      return false;
    }
  }

  private static class IdConditionSelector extends NodeFilter {

    private final String id;

    public IdConditionSelector(String id) {
      this.id = id;
    }

    @Override
    public boolean apply(BaseNode node, Integer index) {
      String nodeId = node.getIdentifier().getId();
      if (nodeId == null || nodeId.isEmpty()) {
        return false;
      }

      return Objects.equals(nodeId, id);
    }
  }

  private static class PropertyNameCondition extends NodeFilter {

    private final Predicate<String> condition;

    public PropertyNameCondition(Predicate<String> condition) {
      this.condition = condition;
    }

    @Override
    public boolean apply(BaseNode node, Integer index) {
      var name = node.getIdentifier().getName();
      if (name != null && !name.isEmpty()) {
        return condition.test(name);
      }

      return false;
    }
  }

  private static class PropertyConditionSelector extends NodeFilter {

    private final String propertyName;
    private final Predicate<String> condition;

    public PropertyConditionSelector(String propertyName, Predicate<String> condition) {
      this.propertyName = propertyName;
      this.condition = condition;
    }

    @Override
    public boolean apply(BaseNode node, Integer index) {
      if (node.isObject()) {
        JsonObjectNode objectNode = (JsonObjectNode) node;

        BaseNode child = objectNode.get(propertyName);
        if (child != null && child.isValueNode()) {
          return condition.test(((JsonValueNode) child).stringValue());
        }
      }

      return false;
    }
  }
}
