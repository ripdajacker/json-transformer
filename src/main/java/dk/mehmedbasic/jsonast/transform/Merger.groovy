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

    public void apply(JsonDocument document, JsonNodes root) {
        def nodes = new ArrayList<BaseNode>(root.roots)
        def queryNodes = new JsonNodes(root)

        log.info("Attempting merge of of ${nodes.size()} elements")
        for (BaseNode source : nodes) {
            queryNodes.exclusions.clear()
            queryNodes.addExclusion(source)

            def parser = new JsonSelectionEngine(selector)

            def parsedSelector = parser.parse()
            def newDestinations = parser.execute(parsedSelector, queryNodes)

            BaseNode destination = null

            if (newDestinations.hasMoreThanOneRoot()) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    log.warning("Found ambiguous destinations for ($source, $selector)")
                } else if (closest.size() > 1) {
                    log.warning("Found more than one potential destination for ($source, $selector)")
                } else {
                    destination = closest.first().first
                }
            } else if (newDestinations.isEmpty()) {
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
