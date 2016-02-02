package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked
import org.codehaus.jackson.node.ArrayNode
import org.codehaus.jackson.node.ObjectNode
import org.codehaus.jackson.node.ValueNode

/**
 * Listener for conversion events.
 */
@TypeChecked
interface ConversionNamingStrategy {

    List<Tuple2<String, String>> toJacksonInArray(BaseNode node)

    String toJackson(BaseNode node)

    JsonIdentifier toTransformable(String name, ObjectNode objectNode)

    JsonIdentifier toTransformable(String name, ArrayNode arrayNode)

    JsonIdentifier toTransformable(String name, ValueNode valueNode)

}
