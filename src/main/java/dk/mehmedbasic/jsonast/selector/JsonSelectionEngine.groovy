package dk.mehmedbasic.jsonast.selector

import com.steadystate.css.parser.CSSOMParser
import com.steadystate.css.parser.SACParserCSS3
import com.steadystate.css.parser.selectors.*
import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.JsonObjectNode
import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.TypeChecked
import org.w3c.css.sac.*

/**
 * The selector engine that parses and applies the selection rules
 */
@TypeChecked
class JsonSelectionEngine {
    private SelectorList selectorList

    JsonSelectionEngine(String selector) {
        def parser = new CSSOMParser(new SACParserCSS3())
        selectorList = parser.parseSelectors(new InputSource(new StringReader(selector)))
        if (selectorList == null) {
            throw new IllegalArgumentException("Selector '$selector' could not be parsed")
        }
    }

    Selector parse() {
        if (!selectorList || selectorList.length == 0) {
            return null
        }
        return selectorList.item(0)
    }

    JsonNodes execute(JsonNodes roots) {
        return execute(parse(), roots)
    }

    JsonNodes execute(Selector selector, JsonNodes roots) {
        if (selector instanceof ConditionalSelector) {
            def subtree = execute(selector.simpleSelector, roots)
            return subtree.filter(fromCondition(selector.condition))
        } else if (selector instanceof ElementSelector) {
            return roots.findByName(selector.localName)
        } else if (selector instanceof DescendantSelector) {
            def subtree = execute(selector.ancestorSelector, roots)
            return execute(selector.simpleSelector, subtree)
        }
        return null
    }

    private static NodeFilter fromCondition(Condition condition) {
        if (condition instanceof AndConditionImpl) {
            return new AndFilter(fromCondition(condition.firstCondition), fromCondition(condition.secondCondition))
        } else if (condition instanceof PrefixAttributeConditionImpl) {
            def prefix = condition.value
            def closure = { String value -> value.startsWith(prefix) }
            return new PropertyConditionSelector(condition.localName, closure)
        } else if (condition instanceof ClassConditionImpl) {
            return new ClassConditionSelector(condition.value)
        } else if (condition instanceof IdConditionImpl) {
            return new IdConditionSelector(condition.value)
        } else if (condition instanceof AttributeConditionImpl) {
            def attributeCondition = condition as AttributeConditionImpl
            def name = attributeCondition.localName
            def value = attributeCondition.value
            if (value) {
                def valueEquals = { String property -> property?.equals(value) }
                return new PropertyConditionSelector(name, valueEquals)
            } else {
                def nameEquals = { String propertyName -> propertyName.equals(name) }
                return new PropertyNameCondition(nameEquals)
            }
        } else if (condition instanceof SubstringAttributeConditionImpl) {
            def substring = condition as SubstringAttributeConditionImpl
            return new PropertyConditionSelector(condition.localName, { String value -> value?.contains(substring.value) })
        }
        throw new UnsupportedOperationException("The given CSS condition is not supported: ${condition.class.name}, $condition");
    }


    private static class ClassConditionSelector extends NodeFilter {
        String className

        ClassConditionSelector(String className) {
            this.className = className
        }

        @Override
        boolean apply(BaseNode node, Integer index) {
            for (String thatName : node.identifier.classes) {
                if (thatName.equals(className)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class IdConditionSelector extends NodeFilter {
        String id

        IdConditionSelector(String name) {
            this.id = name
        }

        @Override
        boolean apply(BaseNode node, Integer index) {
            if (!node.identifier.id) {
                return false
            }
            return node.identifier.id.equals(id)
        }
    }

    private static class PropertyNameCondition extends NodeFilter {
        Closure<Boolean> condition

        PropertyNameCondition(Closure<Boolean> condition) {
            this.condition = condition
        }

        @Override
        boolean apply(BaseNode node, Integer index) {
            if (node.identifier.name) {
                return condition.call(node.identifier.name)
            }
            return false
        }
    }

    private static class PropertyConditionSelector extends NodeFilter {

        String propertyName
        Closure<Boolean> condition

        PropertyConditionSelector(String propertyName, Closure<Boolean> condition) {
            this.propertyName = propertyName
            this.condition = condition
        }

        @Override
        boolean apply(BaseNode node, Integer index) {
            if (node.object) {
                def objectNode = node as JsonObjectNode

                def child = objectNode.get(propertyName)
                if (child) {
                    if (child.valueNode) {
                        return condition.call((child as JsonValueNode).stringValue())
                    }
                }
            }
            false
        }
    }

}
