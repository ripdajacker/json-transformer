package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.*
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j

/**
 * A merger manipulation. Merges two values into one
 */
@PackageScope
@Slf4j
final class Merger implements TransformStrategy {
    final String selector
    MergeValueFunction function

    Merger(String selector, MergeValueFunction function) {
        this.selector = selector
        this.function = function
    }

    public void apply(JsonDocument document, JsonNodes root) {
        def queryRoot = document.select(null)

        List<Closure> changes = []

        log.info("Attempting merge of ${root.size()} elements")
        for (BaseNode source : root.roots) {
            queryRoot.exclusions.clear()
            queryRoot.addExclusion(source)

            def newDestinations = queryRoot.select(selector)

            BaseNode destination = null
            if (newDestinations.isEmpty()) {
                log.warn("Found zero potential destinations for ($source, $selector)")
            } else if (newDestinations.size() > 1) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    log.warn("Found ambiguous destinations for ($source, $selector)")
                } else if (closest.size() > 1) {
                    log.warn("Found more than one potential destination for ($source, $selector)")
                } else {
                    destination = closest.first().first
                }
            } else {
                destination = newDestinations.roots.first()
            }

            if (destination) {
                if (destination.isObject()) {
                    // Destination is object, add the source
                    destination.addChild(source)
                } else if (destination.isArray()) {
                    // Destination is array, add the source and wipe the name
                    source.identifier.name = null
                    destination.addChild(source)
                } else if (destination.isValueNode()) {
                    // Destination is JsonValue, apply the given function
                    if (function) {
                        def valueNode = destination as JsonValueNode
                        if (source.isArray()) {
                            function.apply(source as JsonArrayNode, valueNode)
                        } else if (source.isObject()) {
                            function.apply(source as JsonObjectNode, valueNode)
                        } else if (source.isValueNode()) {
                            function.apply(source as JsonValueNode, valueNode)
                        }
                    }
                }
            }
        }

    }

}
