package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked
import org.codehaus.jackson.node.ArrayNode
import org.codehaus.jackson.node.ObjectNode
import org.codehaus.jackson.node.ValueNode

/**
 * One-to-one conversion of names
 */
@TypeChecked
class DefaultNamingStrategy implements ConversionNamingStrategy {
    @Override
    String toJackson(BaseNode node) {
        return node.identifier.name
    }

    @Override
    JsonIdentifier toTransformable(String name, ObjectNode objectNode) {
        return new JsonIdentifier(name)
    }

    @Override
    JsonIdentifier toTransformable(String name, ArrayNode arrayNode) {
        return new JsonIdentifier(name)
    }

    @Override
    JsonIdentifier toTransformable(String name, ValueNode valueNode) {
        return new JsonIdentifier(name)
    }
}
