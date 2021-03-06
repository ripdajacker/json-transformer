package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonDocument
import groovy.transform.TypeChecked
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * A version definition, that can execute a script containing transformation.
 *
 * The transformations themselves are delegated directly to {@link JsonDocument}.
 */
@TypeChecked
class VersionDefinition implements Comparable<VersionDefinition> {
    int versionNumber
    String comment

    Closure closure
    DelegatingScript script


    VersionDefinition(int versionNumber, String definition) {
        this.versionNumber = versionNumber
        this.script = createScript(null, definition)
    }

    VersionDefinition(int versionNumber, String comment, Closure closure) {
        this.versionNumber = versionNumber
        this.comment = comment
        this.closure = closure
    }
/**
 * Executes the transformations described by this definition.
 *
 * @param delegate the document to execute the transformations on.
 */
    @SuppressWarnings("JavaStylePropertiesInvocation")
    void execute(JsonDocument delegate) {
        if (script != null) {
            script.setDelegate(delegate)
            script.run()
        }
        if (closure) {
            closure.setDelegate(delegate)
            closure.run()
        }
    }

    /**
     * Parse a version definition from a Groovy script.
     *
     * @param inputStream the stream pointing to the Groovy code.
     *
     * @return hopefully a viable version definition.
     */
    static VersionDefinition parse(InputStream inputStream) {
        def script = inputStream.readLines().join("\n")
        def definition = new VersionDefinition(-1, "")

        createScript(definition, script).run()
        return definition
    }

    @SuppressWarnings("JavaStylePropertiesInvocation")
    private static DelegatingScript createScript(Object delegate, String string) {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.scriptBaseClass = DelegatingScript.class.name;


        def binding = new Binding()
        GroovyShell shell = new GroovyShell(VersionDefinition.class.classLoader, binding, configuration);

        DelegatingScript delegatingScript = (DelegatingScript) shell.parse(string)
        if (delegate) {
            delegatingScript.setDelegate(delegate)
        }

        delegatingScript
    }

    /**
     * Sets the version.
     *
     * @param version the version.
     */
    @SuppressWarnings("GroovyUnusedDeclaration")
    void version(int version) {
        this.versionNumber = version
    }

    /**
     * Sets the comment.
     *
     * @param string the comment.
     */
    @SuppressWarnings("GroovyUnusedDeclaration")
    void comment(String string) {
        this.comment = string
    }

    /**
     * Sets the transformation closure.
     *
     * @param closure the closure to execute.
     */
    @SuppressWarnings("GroovyUnusedDeclaration")
    void transformations(Closure closure) {
        this.closure = closure
    }

    @Override
    int compareTo(VersionDefinition that) {
        return Integer.compare(this.versionNumber, that.versionNumber)
    }
}
