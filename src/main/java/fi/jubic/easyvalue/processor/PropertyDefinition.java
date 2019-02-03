package fi.jubic.easyvalue.processor;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

class PropertyDefinition {
    private final Element element;

    private final String name;
    private final TypeMirror type;
    private final String jsonName;

    PropertyDefinition(
            Element element,
            String name,
            TypeMirror type,
            String jsonName
    ) {
        this.element = element;
        this.name = name;
        this.type = type;
        this.jsonName = jsonName;
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

    String getJsonName() {
        return jsonName;
    }
}
