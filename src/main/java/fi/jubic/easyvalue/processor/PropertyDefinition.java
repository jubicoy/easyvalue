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
    private final PropertyKind propertyKind;

    PropertyDefinition(
            Element element,
            String name,
            TypeMirror type,
            @Nullable TypeMirror typeArgument,
            PropertyKind propertyKind
    ) {
        this.element = element;
        this.name = name;
        this.type = type;
        this.typeArgument = typeArgument;
        this.propertyKind = propertyKind;
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

    PropertyKind getPropertyKind() {
        return propertyKind;
    }

    boolean isOptional() {
        return propertyKind == PropertyKind.OPTIONAL
                || propertyKind == PropertyKind.OPTIONAL_ARRAY;
    }
}
