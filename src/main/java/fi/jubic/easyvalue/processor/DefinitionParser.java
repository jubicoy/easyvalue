package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import fi.jubic.easyvalue.EasyProperty;
import fi.jubic.easyvalue.EasyValue;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;

class DefinitionParser {
    ProcessingResult<ValueDefinition> parseValue(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            return ProcessingResult.of(
                    ProcessingMessage.of(
                            Diagnostic.Kind.ERROR,
                            "@EasyValue can only be used to annotate classes"
                    )
            );
        }

        TypeElement typeElement = (TypeElement) element;
        EasyValue annotation = typeElement.getAnnotation(EasyValue.class);

        ValueDefinition definition = new ValueDefinition();
        definition.setElement(typeElement);

        {
            List<String> enclosingElements = new ArrayList<>();
            Element enclosing = typeElement.getEnclosingElement();
            while (enclosing.getKind() == ElementKind.CLASS) {
                enclosingElements.add(enclosing.getSimpleName().toString());
                enclosing = enclosing.getEnclosingElement();
            }

            definition.setGeneratedName(
                    enclosingElements.stream()
                            .reduce(
                                    "EasyValue_",
                                    (a, b) -> a + b + "_"
                            )
                            + typeElement.getSimpleName().toString()
            );
        }

        definition.setJacksonAnnotated(!annotation.excludeJson());

        List<PropertyDefinition> properties = new ArrayList<>();
        for (Element valueElement : typeElement.getEnclosedElements()) {
            EasyProperty easyProperty = valueElement.getAnnotation(EasyProperty.class);
            if (easyProperty == null) continue;
            if (valueElement.getKind() != ElementKind.METHOD) {
                return ProcessingResult.of(
                        ProcessingMessage.of(
                                Diagnostic.Kind.ERROR,
                                "@EasyProperty can only be applied to methods"
                        )
                );
            }

            String jsonName = easyProperty.value().length() > 0
                    ? easyProperty.value()
                    : valueElement.getSimpleName().toString();

            properties.add(
                    new PropertyDefinition(
                            valueElement,
                            valueElement.getSimpleName().toString(),
                            ((ExecutableElement) valueElement).getReturnType(),
                            definition.isJacksonAnnotated()
                                    ? jsonName
                                    : null
                    )
            );
        }
        definition.setProperties(properties);

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() != ElementKind.CLASS) continue;
            if (!enclosedElement.getSimpleName().toString().equals("Builder")) continue;

            definition.setBuilderElement((TypeElement) enclosedElement);
            break;
        }

        List<TypeVariableName> typeVariables;
        {
            typeVariables = new ArrayList<>();
            for (TypeParameterElement parameter : typeElement.getTypeParameters()) {
                TypeVariableName typeVariable = TypeVariableName.get(parameter.getSimpleName().toString());

                List<TypeName> bounds = new ArrayList<>();
                for (TypeMirror mirror : parameter.getBounds()) {
                    bounds.add(
                            TypeName.get(mirror)
                    );
                }

                typeVariables.add(
                        typeVariable.withBounds(bounds)
                );
            }
        }
        definition.setTypeVariables(typeVariables);

        return ProcessingResult.of(definition);
    }
}
