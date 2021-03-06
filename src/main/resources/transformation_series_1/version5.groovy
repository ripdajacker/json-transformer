package transformation_series_1

import dk.mehmedbasic.jsonast.JsonValueNode
import dk.mehmedbasic.jsonast.transform.MergeValueFunction

version 5
comment "Merge first and last names into a single field"

transformations {
    def merger = new MergeValueFunction() {
        @Override
        void apply(JsonValueNode source, JsonValueNode destination) {
            destination.value = source.value + " " + destination.stringValue()
        }
    }

    transform("person firstName")
            .merge("lastName", merger)
            .apply()

    transform("person")
            .deleteChild("firstName")
            .renameChild("lastName", "name")
            .apply()
}

