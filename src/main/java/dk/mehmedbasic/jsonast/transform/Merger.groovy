package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.*
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import groovy.transform.PackageScope
import groovy.util.logging.Log

/**
 * A merger manipulation. Merges two values into one
 */
@PackageScope
@Log
final class Merger implements TransformStrategy {
    final String selector
    MergeValueFunction function

    Merger(String selector, MergeValueFunction function) {
        this.selector = selector
        this.function = function
    }

    public void apply(JsonNodes root) {
        for (BaseNode source : root.roots) {
            def parents = new JsonNodes()
            parents.addExclusion(source)
            for (BaseNode parent : source.parents()) {
                parents.addRoot(parent)
            }

            def parser = new JsonSelectionEngine(selector)

            def parsedSelector = parser.parse()
            def newDestinations = parser.execute(parsedSelector, parents)

            BaseNode destination = null

            if (newDestinations.length > 1) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    log.warning("Found ambiguous destinations for ($source, $selector)")
                } else if (closest.size() > 1) {
                    log.warning("Found more than one potential destination for ($source, $selector)")
                } else {
                    destination = closest.first().first
                }
            } else if (newDestinations.length == 0) {
                log.warning("Found zero potential destinations for ($source, $selector)")
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
