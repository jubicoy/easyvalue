# EasyValue

[![Build Status](https://travis-ci.org/jubicoy/easyvalue.svg?branch=master)](https://travis-ci.org/jubicoy/easyvalue)

Simple values that work with Jackson.

## Usage

* Properties are abstract methods named with a `get` prefix.
* Builder extends `EasyValue_*` generated builder.

```java
@EasyValue
@JsonDeserialize(builder = ValueClass.Builder.class)
public abstract class ValueClass {
    @Nullable
    public abstract Long getId();

    public abstract String getProperty();

    abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder();
    }

    public static Builder extends EasyValue_ValueClass.Builder {
    }
}
```

### Nullable properties

Properties annotated with `javax.annotation.Nullable` are treated as nullable.
Any property without a `Nullable` annotation has to be defined before
constructing and object or an `IllegalStateException` is thrown.

### Property default values

Default values should be defined in `Builder` initialization.

```java
@EasyValue
public abstract class ValueClass {
    @Nullable
    public abstract Long getId();

    public abstract String getProperty();

    public static Builder builder() {
        return new Builder()
                .setProperty(""); // Sets the default value for property
    }

    public static Builder extends EasyValue_ValueClass.Builder {
    }
}
```

### Builder from value

Value can be converted to a `Builder` instance using the `toBuilder` method.

```java
@EasyValue
public abstract class ValueClass {
    public abstract String getProperty();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder();
    }

    public static Builder extends EasyValue_ValueClass.Builder {
    }
}
```

### Generic values

Values can be generic.

```java
@EasyValue
public abstract class GenericValue<T> {
    public abstract T getProperty();

    public abstract Builder<T> toBuilder();

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static Builder<T> extends EasyValue_GenericValue.Builder<T> {
    }
}
```

### Jackson

Property names and builder format work together with Jackson's built-in builder
pattern support (available since 2.x). Serializing and deserializing value
objects requires minimal amount of extra annotations and configuration.

For example, an instance of `ValueClass` initialized like this

```java
ValueClass instance = ValueClass.builder()
        .setId(1L)
        .setProperty("value")
        .build()
```

will be serialized into JSON like this

```json
{ "id": 1, "property": "value" }
```

Deserialization works the same.

#### Property aliases

`@JsonProperty` annotations are copied from the properties to the builder.
Aliasing a property for both serialization and deserialization is requires
only a single annotation.

```java
@EasyValue
@JsonDeserialize(builder = AliasedClass.Builder.class)
public abstract class AliasedClass {
    public abstract Long getId();

    @JsonProperty("_property")
    public abstract String getProperty();

    public static Builder builder() {
      return new Builder();
    }

    public static Builder extends EasyValue_AliasedClass.Builder {
    }
}
```

It is possible to define different aliases for serialization and
deserialization. If a serialization alias has been defined, an empty
`@JsonProperty` annotation can be used to override in deserialization.

```java
@EasyValue
@JsonDeserialize(builder = AsymmetricAliasedClass.Builder.class)
public abstract class AsymmetricAliasedClass {
    public abstract Long getId();

    @JsonProperty("_property")
    public abstract String getProperty();

    abstract Builder toBuilder();

    public static Builder builder() {
      return new Builder();
    }

    public static Builder extends EasyValue_AsymmetricAliasedClass.Builder {
        @Override
        @JsonProperty("_id")
        public Builder setId(Long id) {
            return super.setId(id);
        }

        @Override
        @JsonProperty
        public Builder setProperty(String property) {
            return super.setProperty(property);
        }
    }
}
```

This class is serialized as

```json
{ "id": 1, "_property": "value" }
```

and deserialized as

```json
{ "_id": 1, "property": "value" }
```

## Migration from 0.x

Migration to 1.x value definitions requires a non-trivial amount of manual
labor. Implementation compatible with the original value definitions will be
available in `fi.jubic.easyvalue.legacy` package to allow a gradual migration.

Replace all references to `fi.jubic.easyvalue` with `fi.jubic.easyvalue.legacy`
to migrate an existing project to 1.x branch. Migration to the 1.x format is
recommended as the `legacy` package will eventually be deprecated and then
removed.
