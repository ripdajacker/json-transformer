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
            def prefix = { String value, String prefix -> value.startsWith(prefix) }
            return new PropertyConditionSelector(condition.localName, condition.value, prefix)
        } else if (condition instanceof PseudoClassConditionImpl) {
            // TODO parse pseudo classes
            throw new UnsupportedOperationException("Pseudo classes are not yet supported")
        } else if (condition instanceof ClassConditionImpl) {
            return new ClassConditionSelector(condition.value)
        } else if (condition instanceof IdConditionImpl) {
            return new IdConditionSelector(condition.value)
        }
        return null;
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

    private static class PropertyConditionSelector extends NodeFilter {

        String propertyName
        String parameter
        Closure<Boolean> condition

        PropertyConditionSelector(String propertyName, String parameter, Closure<Boolean> condition) {
            this.propertyName = propertyName
            this.condition = condition
            this.parameter = parameter
        }

        @Override
        boolean apply(BaseNode node, Integer index) {
            if (node.object) {
                def objectNode = node as JsonObjectNode

                def child = objectNode.get(propertyName)
                if (child) {
                    if (child.valueNode) {
                        return condition.call((child as JsonValueNode).stringValue(), parameter)
                    }
                }
            }
            false
        }
    }

}
