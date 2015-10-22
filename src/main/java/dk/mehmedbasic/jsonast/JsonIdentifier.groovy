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
}
