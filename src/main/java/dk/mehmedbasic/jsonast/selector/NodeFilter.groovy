package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 10/15/2015.
 */
interface NodeFilter {
    boolean apply(BaseNode node)
}