# EasyValue

[![Build Status](https://travis-ci.org/jubicoy/easyvalue.svg?branch=master)](https://travis-ci.org/jubicoy/easyvalue)

Simple wrapper for Google's AutoValue and Jackson (de)serialization.

## Usage

Value class definitions are similar to `@AutoValue` with some minor
differences.

* Every property has to be annotated using `@EasyProperty`.
* `Builder` requires some tricks. That's the price you'll have to pay to
  use the builder generated by `AutoValue` and assign it to object of
  type `ValueObject.Builder`.
* Serialization and deserialization need to be defined using Jackson
  annotations.

```java
@EasyValue
@JsonDeserialize(as = EasyValue_ValueClass.class)
@JsonSerialize(as = EasyValue_ValueClass.class)
public abstract class ValueClass {
  @EasyProperty
  @Nullable
  public abstract Long id();

  @EasyProperty
  public abstract String property();

  abstract Builder toBuilder();

  public static Builder builder() {
    return new Builder();
  }

  public static Builder extends EasyValue_ValueClass.Builder {
  }
}
```

