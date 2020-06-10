package fi.jubic.easyvalue.processor;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class PropertyDefinition {
    private final Element element;
    private final String name;
    private final TypeMirror type;
    @Nullable
    private final TypeMirror typeArgument;
    private final boolean optional;

    PropertyDefinition(
            Element element,
            String name,
            TypeMirror type,
            @Nullable TypeMirror typeArgument,
            boolean optional
    ) {
        this.element = element;
        this.name = name;
        this.type = type;
        this.typeArgument = typeArgument;
        this.optional = optional;
    }

    Element getElement() {
        return element;
    }

    String getName() {
        return name;
    }

    TypeMirror getType() {
        return type;
    }

    Optional<TypeMirror> getTypeArgument() {
        return Optional.ofNullable(typeArgument);
    }

    boolean isOptional() {
        return optional;
    }
}
