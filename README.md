# json-transformer


This small project is an attempt to reduce the complexity of having multiple versions of JSON documents.

This is part of my masters thesis and is a work in progress.

## Crash course

Given a JSON document:

```json
{
    "destination": {
        "value": 42
    },
    "subtree": {
        "source": "Jon Snow dies in Season 5"
    }
}
```

Implementing a JsonSelect-like query language with a builder one can write:

```groovy
new Transformer("source").moveTo("destination").apply(document)   
```

The resulting JSON will be:

```json
{
    "destination": {
        "value": 42,
        "source": "Jon Snow dies in Season 5"
    },
    "subtree": {}
}
```

