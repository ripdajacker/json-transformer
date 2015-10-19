package dk.mehmedbasic.jsonast

/**
 * A listener for node change events.
 */
interface NodeChangedListener {
    void nodeChanged(NodeChangeEventType type, BaseNode node)
}