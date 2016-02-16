package dk.mehmedbasic.jsonast

import groovy.transform.CompileStatic

/**
 * A small identifier class for aiding in CSS query calculation.
 */
@CompileStatic
class JsonIdentifier {
    String name
    String id
    Set<String> classes = new LinkedHashSet<>()

    JsonIdentifier() {
    }

    JsonIdentifier(String name) {
        this.name = name
    }

    @Override
    String toString() {
        return "Identifier[$name, #$id, classes: $classes]"
    }
}

