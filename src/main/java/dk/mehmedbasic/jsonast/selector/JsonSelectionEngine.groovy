package dk.mehmedbasic.jsonast.selector

import com.steadystate.css.parser.CSSOMParser
import com.steadystate.css.parser.SACParserCSS3
import com.steadystate.css.parser.selectors.ClassConditionImpl
import com.steadystate.css.parser.selectors.IdConditionImpl
import com.steadystate.css.parser.selectors.PseudoClassConditionImpl
import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.TypeChecked
import org.w3c.css.sac.*

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonSelectionEngine {
    private SelectorList selectorList

    JsonSelectionEngine(String selector) {
        def parser = new CSSOMParser(new SACParserCSS3())
        selectorList = parser.parseSelectors(new InputSource(new StringReader(selector)))
    }

    Selector parse() {
        if (selectorList.length == 0) {
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

            def result = execute(selector.simpleSelector, subtree)
            return result
        }
        return null
    }

    private static NodeFilter fromCondition(Condition condition) {
        if (condition instanceof PseudoClassConditionImpl) {
            // TODO parse pseudo classes
        } else if (condition instanceof ClassConditionImpl) {
            return new ClassConditionSelector(condition.value)
        } else if (condition instanceof IdConditionImpl) {
            return new IdConditionSelector(condition.value)
        }
        return null;
    }

    private static class ClassConditionSelector implements NodeFilter {
        String className

        ClassConditionSelector(String className) {
            this.className = className
        }

        @Override
        boolean apply(BaseNode node) {
            if (!node.identifier.tag) {
                return false
            }
            return node.identifier.tag.equals(className)
        }
    }

    private static class IdConditionSelector implements NodeFilter {
        String id

        IdConditionSelector(String name) {
            this.id = name
        }

        @Override
        boolean apply(BaseNode node) {
            if (!node.identifier.id) {
                return false
            }
            return node.identifier.id.equals(id)
        }
    }

}
