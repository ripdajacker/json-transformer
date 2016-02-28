# json-transformer
This small project is an attempt to reduce the complexity of maintaining multiple versions of JSON documents.

It includes following:

* A CSS-based query language for querying JSON nodes.
* A mutable AST for transforming documents based on queries.
* A Groovy-based DSL for writing transformations.

A list of transformations supported:

* Add a JSON value to a node.
* Rename a JSON key.
* Delete a node from the tree.
* Move a JSON key/value pair up or sideways in the tree
* Merge a JSON node with another node.
* Partition a JSON node into more nodes.
 

## Crash course

Given a JSON document:

```json
{
    "destination": {
        "value": 42
    },
    "subtree": {       
        "source": "Jon Snow dies in Season 5",
        "foo": "bar",
        "baz": "botch",
        "stop": "the shitty examples"
    }
}
```

### Selectors
You can select all nodes with the name `stop` by writing:

```groovy
document.select("stop")
```

The result will be an instance of `JsonNodes`, that has a reference to all the matching nodes.

You can write some more complex selectors:

```groovy
document.select("subtree source")
```

Following selectors are supported:

* `name` gets all nodes matching the name.
* `.class` gets all the nodes that have a child `@class` with a value (or array containing) the classname.
* `#id` gets all the nodes that have a child `@id` with the value `id`.

Combinations of selectors are supported:

* `ancestorSelector selector` gets all nodes matching `selector` that also have a ancestor matching `ancestorSelector`.
* `parentSelector > selector` gets all nodes matching `selector` that have a parent matching `parentSelector`.
* `[keyName]` gets all nodes that have a child named `keyName`. 
* `selector[keyName^=Prefix]` gets all nodes matching  `selector` that also have a child named `keyName` whose value starts with `Prefix`. 
* `selector[keyName*=Substring]` gets all nodes matching  `selector` that also have a child named `keyName` whose value contains `Substring`. 
* `selector[keyName=SomeName]` gets all nodes matching  `selector` that also have a child named `keyName` whose value equals `SomeName`. 
 
 
### Some transformations 


Using the DSL one can write:

```groovy
document.transform("source")
    .moveTo("destination")
    .apply()
    
document.transform("stop")
    .renameTo("we love")
    .apply()
    
document.transform("subree")
    .deleteChild("foo")
    .deleteChild("baz")
    .apply()      
```

The resulting JSON will be:

```json
{
    "destination": {
        "source": "Jon Snow dies in Season 5",
        "value": 42
    },
    "subtree": {
        "we love": "the shitty examples"
    }
}
```


### Partitioning a subtree
The partition function splits a node into one or more sibling nodes.

```groovy
document.transform("subtree")
    .partition([["original_source", "source"], ["random_stuff", "foo", "baz", "stop"]])
    .apply()
```

The resulting JSON:
```json
{
    "destination": {
        "value": 42
    },
    "original_source": {
            "source": "Jon Snow dies in Season 5"
    },
    "random_stuff": {
        "foo": "bar",
        "baz": "botch",
        "stop": "the shitty examples"
    },
    "subtree": {}
}
```

## Transformations in a method

You will need a `JsonDocument` instance.
  
This can be created either by parsing the file with the included `BaseNodeParser` or by using the `JacksonConverter` 
class to convert a Jackson-based JSON tree.  

The `JsonDocument` class has a method for helping out with this:

```groovy
def document = JsonDocument.parse(some_input_stream)

document.transform(...) // Ready for transformation
```

## Defining transformations as Groovy scripts

It's possible to define transformation as `.groovy` files.

The syntax is as follows:

```groovy
version 1
comment "Renames the 'name' property to 'billy' and adds a dog named bingo."
 
transformations {
    transform("name")
            .renameTo("billy")
            .apply()
 
    transform("person")
            .addJson("dog", '{"type":"dog", "name": "bingo"}')
            .apply()
}
```

The script defines a version, a comment and a closure containing the transformations.
The closure is executed with a `JsonDocument` as delegate.  

To run these transformations you have to use an instance of the `VersionControl` class.
Following code shows the steps needed to do so:
                         
```groovy
def control = new VersionControl(new File("/path/to/directory/with/transformation scripts"))
control.apply(document, desiredVersionNumber)
```

Every script in the directory is read.
The version number used in the `apply` method will be the last transformation that is applied.