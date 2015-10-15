package dk.mehmedbasic.jsonast

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 10/15/2015.
 */
interface NodeChangedListener {
    void nodeChanged(NodeChangeEventType type, BaseNode node)
}