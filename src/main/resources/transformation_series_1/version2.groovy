package transformation_series_1

import dk.mehmedbasic.jsonast.JsonValueNode
import dk.mehmedbasic.jsonast.transform.MergeValueFunction

version 2
comment "Partitions the 'billy' property into a first and last name."

transformations {
    // First we add the two names
    transform("person")
            .add("firstName", "")
            .add("lastName", "")
            .apply()

    def first = new MergeValueFunction() {
        @Override
        void applyValue(JsonValueNode source, JsonValueNode destination) {
          def list = source.stringValue().split(" ").toList()

          destination.value = list.subList(0, list.size() - 1).join(" ")
        }
    }
    def last = new MergeValueFunction() {

      @Override
      void applyValue(JsonValueNode source, JsonValueNode destination) {
        def list = source.stringValue().split(" ").toList()

        destination.value = list.last()
      }
    }

    transform("person billy")
            .merge("firstName", first)
            .merge("lastName", last)
            .apply()


    transform("person")
            .deleteChild("billy")
            .apply()


}

