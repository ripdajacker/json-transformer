package dk.mehmedbasic.jsonast

/**
 * A listener for node change events.
 */
interface NodeChangedListener {

    /**
     * A callback for when a node is changed.
     *
     * @param type the event type.
     * @param node the node in question.
     */
    void nodeChanged(NodeChangeEventType type, BaseNode node)
}