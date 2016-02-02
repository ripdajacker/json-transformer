package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonDocument

/**
 * A version controlling class.
 */
class VersionControl {
    List<VersionDefinition> definitions = []

    VersionControl(File directory) {
        for (File file : directory.listFiles()) {
            definitions << VersionDefinition.parse(file.newInputStream())
        }

        Collections.sort(definitions)
    }

    VersionControl() {
    }

    /**
     * Applies the given version to the given JsonDocument.
     *
     * @param document the document to transform.
     * @param version the version to transform it to.
     */
    void apply(JsonDocument document, int version) {
        List<VersionDefinition> potentials = []

        def selection = document.selectSingle("sysclass_version")
        int currentVersion = -1
        if (selection.present) {
            currentVersion = selection.get().intValue()
        }
        for (VersionDefinition definition : definitions) {
            if (definition.versionNumber <= version && definition.versionNumber >= currentVersion) {
                potentials.add(definition)
            }
        }

        for (VersionDefinition definition : potentials) {
            definition.execute(document)
        }
    }
}
