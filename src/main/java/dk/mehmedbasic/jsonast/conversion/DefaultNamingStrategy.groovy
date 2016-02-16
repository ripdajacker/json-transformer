package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked

/**
 * One-to-one conversion of names
 */
@TypeChecked
class DefaultNamingStrategy implements ConversionNamingStrategy {
    @Override
    List<Tuple2<String, String>> toJacksonInArray(BaseNode node) {
        return []
    }

    @Override
    String toJacksonName(BaseNode node) {
        return node.identifier.name
    }

    @Override
    JsonIdentifier toTransformableName(String name, BaseNode objectNode) {
        return new JsonIdentifier(name)
    }
}
