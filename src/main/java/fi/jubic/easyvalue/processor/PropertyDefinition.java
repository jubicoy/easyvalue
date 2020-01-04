package fi.jubic.easyvalue.processor;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

class PropertyDefinition {
    private final Element element;
    private final String name;
    private final TypeMirror type;

    PropertyDefinition(
            Element element,
            String name,
            TypeMirror type
    ) {
        this.element = element;
        this.name = name;
        this.type = type;
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
}
