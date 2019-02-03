package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

class ValueDefinition {
    private TypeElement element;
    private TypeElement builderElement;
    private List<TypeVariableName> typeVariables;

    private String generatedName;

    private List<PropertyDefinition> properties;

    private boolean jacksonAnnotated;

    ValueDefinition() {
        element = null;
        builderElement = null;
        typeVariables = Collections.emptyList();
        properties = Collections.emptyList();
        jacksonAnnotated = true;
    }

    TypeElement getElement() {
        return element;
    }

    void setElement(TypeElement element) {
        this.element = element;
    }

    TypeElement getBuilderElement() {
        return builderElement;
    }

    void setBuilderElement(TypeElement builderElement) {
        this.builderElement = builderElement;
    }

    List<TypeVariableName> getTypeVariables() {
        return typeVariables;
    }

    void setTypeVariables(List<TypeVariableName> typeVariables) {
        this.typeVariables = typeVariables;
    }

    String getGeneratedName() {
        return generatedName;
    }

    void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    List<PropertyDefinition> getProperties() {
        return properties;
    }

    void setProperties(List<PropertyDefinition> properties) {
        this.properties = properties;
    }

    boolean isJacksonAnnotated() {
        return jacksonAnnotated;
    }

    void setJacksonAnnotated(boolean jacksonAnnotated) {
        this.jacksonAnnotated = jacksonAnnotated;
    }
}
