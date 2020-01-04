package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DefinitionParser {
    private static final Pattern ACCESSOR_PATTERN = Pattern.compile(
            "^(get|is)([A-Z0-9].*)$"
    );

    ProcessingResult<ValueDefinition> parseValue(Element element) {
        if (element.getKind() != ElementKind.CLASS) {
            return ProcessingResult.of(
                    ProcessingMessage.of(
                            Diagnostic.Kind.ERROR,
                            "@EasyValue can only be used to annotate classes",
                            element
                    )
            );
        }

        TypeElement typeElement = (TypeElement) element;

        ValueDefinition definition = new ValueDefinition();
        definition.setElement(typeElement);

        {
            List<String> enclosingElements = new ArrayList<>();
            Element enclosing = typeElement.getEnclosingElement();
            ElementKind enclosingKind = enclosing.getKind();
            while (
                    enclosingKind == ElementKind.CLASS
                            || enclosingKind == ElementKind.INTERFACE
                            || enclosingKind == ElementKind.ENUM
            ) {
                enclosingElements.add(enclosing.getSimpleName().toString());
                enclosing = enclosing.getEnclosingElement();
                enclosingKind = enclosing.getKind();
            }

            definition.setGeneratedName(
                    Stream.of(
                            Stream.of("EasyValue"),
                            enclosingElements.stream(),
                            Stream.of(typeElement.getSimpleName().toString())
                    )
                            .flatMap(Function.identity())
                            .collect(Collectors.joining("_"))
            );
        }

        definition.setProperties(
                typeElement.getEnclosedElements()
                        .stream()
                        .filter(elem -> elem.getKind() == ElementKind.METHOD)
                        .map(elem -> (ExecutableElement) elem)
                        .filter(elem -> elem.getModifiers().contains(Modifier.ABSTRACT))
                        .filter(elem -> elem.getParameters().isEmpty())
                        .filter(
                                elem -> ACCESSOR_PATTERN
                                        .matcher(elem.getSimpleName().toString())
                                        .matches()
                        )
                        .map(elem -> {
                            Matcher matcher = ACCESSOR_PATTERN.matcher(
                                    elem.getSimpleName().toString()
                            );
                            boolean match = matcher.find();
                            if (!match) {
                                return Optional.<PropertyDefinition>empty();
                            }
                            return Optional.of(
                                    new PropertyDefinition(
                                            elem,
                                            matcher.group(2).substring(0, 1).toLowerCase()
                                                    + matcher.group(2).substring(1),
                                            elem.getReturnType()
                                    )
                            );
                        })
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );

        typeElement.getEnclosedElements()
                .stream()
                .filter(elem -> elem.getKind() == ElementKind.CLASS)
                .map(elem -> (TypeElement) elem)
                .filter(elem -> elem.getSimpleName().toString().equals("Builder"))
                .findFirst()
                .ifPresent(definition::setBuilderElement);

        definition.setTypeVariables(
                typeElement.getTypeParameters()
                        .stream()
                        .map(param -> TypeVariableName.get(param.getSimpleName().toString())
                                .withBounds(
                                        param.getBounds()
                                                .stream()
                                                .map(TypeName::get)
                                                .collect(Collectors.toList())
                                )
                        )
                        .collect(Collectors.toList())
        );

        return ProcessingResult.of(definition);
    }
}
