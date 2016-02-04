package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * A small identifier class for aiding in CSS query calculation.
 */
@TypeChecked
class JsonIdentifier {
    String name
    String id
    Set<String> classes = []

    JsonIdentifier() {
    }

    JsonIdentifier(String name) {
        this.name = name
    }

    String getName() {
        return name
    }

    @Override
    String toString() {
        return "Identifier[$name, #$id, classes: $classes]"
    }
}

