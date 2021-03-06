package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked

import java.util.regex.Pattern

/**
 * Naming strategy for inline id
 */
@TypeChecked
class InlineIdsNamingStrategy implements ConversionNamingStrategy {
    private static Pattern PATTERN_NAME_AND_ID = Pattern.compile(/([A-Za-z0-9]+) #(\-?[0-9a-f]+)\s*$/)

    @Override
    List<Tuple2<String, String>> toJacksonInArray(BaseNode node) {
        return [new Tuple2<String, String>("@id", node.identifier.id)]
    }

    @Override
    String toJacksonName(BaseNode node) {
        if (node.identifier.id != null) {
            return "${node.identifier.name} #${node.identifier.id}"
        }
        return node.identifier.name
    }

    @Override
    JsonIdentifier toTransformableName(String name, BaseNode objectNode) {
        return parseName(name)
    }

    private static JsonIdentifier parseName(String name) {
        def identifier = new JsonIdentifier(name)
        if (name == null) {
            return identifier
        }

        def matcher = PATTERN_NAME_AND_ID.matcher(name)
        if (matcher.find()) {
            identifier.name = matcher.group(1)
            identifier.id = matcher.group(2)
        }

        return identifier
    }
}
