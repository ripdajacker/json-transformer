package dk.mehmedbasic.jsontransform

import groovy.transform.TypeChecked

/**
 * Times a task
 */
@TypeChecked
public class TaskTimer {
    static void timeTaken(String operation, Closure closure) {
        long start = System.currentTimeMillis()
        closure()
        long elapsed = System.currentTimeMillis() - start
        println "$operation took (sec): " + elapsed / 1000
    }

}

