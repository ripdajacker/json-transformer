package dk.mehmedbasic.jsonast.conversion

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked

/**
 * Naming strategy for transformable nodes.
 */
@TypeChecked
interface ConversionNamingStrategy {

    List<Tuple2<String, String>> toJacksonInArray(BaseNode node)

    String toJacksonName(BaseNode node)

    JsonIdentifier toTransformableName(String name, BaseNode objectNode)

}
