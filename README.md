# json-transformer


This small project is an attempt to reduce the complexity of having multiple versions of JSON documents.

This is part of my masters thesis and is a work in progress and the README will be updated with new examples as they are finished.

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

Implementing a CSS-like query language with a builder one can write:

```groovy
document.transform("source")
    .moveTo("destination")
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
        "foo": "bar",
        "baz": "botch",
        "stop": "the shitty examples"
    }
}
```

## Sufficiently complicated examples

### Renaming a node

The following code renames a node.

```groovy
document.transform("source")
    .renameTo("awesome_renaming")
    .apply()
```

The resulting JSON:
```json
{
    "destination": {
        "value": 42
    },
    "subtree": {
        "awesome_renaming": "Jon Snow dies in Season 5",
        "foo": "bar",
        "baz": "botch",
        "stop": "the shitty examples"
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