package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j

/**
 * Moves nodes from one place to another.
 */
@Slf4j
@PackageScope
@CompileStatic
final class Mover implements TransformStrategy {
    final String selector

    Mover(String selector) {
        this.selector = selector
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        def nodes = new ArrayList<BaseNode>(root.roots)
        def queryNodes = new JsonNodes(root)

        log.info("Attempting move of ${nodes.size()} elements")
        Map<String, Integer> messages = [:]
        for (BaseNode source : nodes) {
            queryNodes.exclusions.clear()
            queryNodes.addExclusion(source)

            def parser = new JsonSelectionEngine(selector)

            def parsedSelector = parser.parse()
            def newDestinations = parser.execute(parsedSelector, queryNodes)
            BaseNode destination = null

            if (newDestinations.empty) {
                def key = "Found zero potential destinations "
                messages.put(key, (messages.get(key) ?: 0) + 1)
            } else if (newDestinations.hasMoreThanOneRoot()) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    def key = "Found ambiguous destinations"
                    messages.put(key, (messages.get(key) ?: 0) + 1)
                } else if (closest.size() > 1) {
                    def key = "Found more than one potential destination"
                    messages.put(key, (messages.get(key) ?: 0) + 1)
                } else {
                    destination = closest.first().first
                }
            } else {
                destination = newDestinations.getRoots().iterator().next()
            }

            if (destination) {
                source.changeParent(destination)
            }
        }
        for (String key : messages.keySet().asList().sort()) {
            log.info("$key ${messages.get(key)} time(s)")
        }
    }
}
