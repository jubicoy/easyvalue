package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DefinitionParser {
    private static final Pattern ACCESSOR_PATTERN = Pattern.compile(
            "^(get|is)([A-Z0-9].*)$"
    );

    ProcessingResult<ValueDefinition> parseValue(
            ProcessingEnvironment processingEnv,
            Element element
    ) {
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

                            TypeMirror typeArgument = null;
                            boolean optional = false;
                            boolean list = false;
                            boolean set = false;
                            boolean map = false;
                            if (elem.getReturnType() instanceof DeclaredType) {
                                DeclaredType returnType = (DeclaredType) elem.getReturnType();
                                typeArgument = returnType.getTypeArguments().size() == 1
                                        ? returnType.getTypeArguments().get(0)
                                        : null;

                                TypeMirror optionalType = processingEnv.getTypeUtils().erasure(
                                        processingEnv.getElementUtils()
                                                .getTypeElement(Optional.class.getCanonicalName())
                                                .asType()
                                );
                                optional = processingEnv.getTypeUtils()
                                        .isAssignable(
                                                returnType,
                                                optionalType
                                        );

                                TypeMirror listType = processingEnv.getTypeUtils().erasure(
                                        processingEnv.getElementUtils()
                                                .getTypeElement(List.class.getCanonicalName())
                                                .asType()
                                );
                                list = processingEnv.getTypeUtils()
                                        .isAssignable(
                                                returnType,
                                                listType
                                        );

                                TypeMirror setType = processingEnv.getTypeUtils().erasure(
                                        processingEnv.getElementUtils()
                                                .getTypeElement(Set.class.getCanonicalName())
                                                .asType()
                                );
                                set = processingEnv.getTypeUtils()
                                        .isAssignable(
                                                returnType,
                                                setType
                                        );

                                TypeMirror mapType = processingEnv.getTypeUtils().erasure(
                                        processingEnv.getElementUtils()
                                                .getTypeElement(Map.class.getCanonicalName())
                                                .asType()
                                );
                                map = processingEnv.getTypeUtils()
                                        .isAssignable(
                                                returnType,
                                                mapType
                                        );
                            }

                            boolean array = false;
                            if (!list && !optional) {
                                array = elem.getReturnType().getKind() == TypeKind.ARRAY;
                            }
                            else if (!list) {
                                array = typeArgument.getKind() == TypeKind.ARRAY;
                            }

                            PropertyKind propertyKind;
                            if (list) {
                                propertyKind = PropertyKind.LIST;
                            }
                            else if (set) {
                                propertyKind = PropertyKind.SET;
                            }
                            else if (map) {
                                propertyKind = PropertyKind.MAP;
                            }
                            else if (optional && array) {
                                propertyKind = PropertyKind.OPTIONAL_ARRAY;
                            }
                            else if (optional) {
                                propertyKind = PropertyKind.OPTIONAL;
                            }
                            else if (array) {
                                propertyKind = PropertyKind.ARRAY;
                            }
                            else {
                                propertyKind = PropertyKind.PLAIN;
                            }

                            return Optional.of(
                                    new PropertyDefinition(
                                            elem,
                                            matcher.group(2).substring(0, 1).toLowerCase()
                                                    + matcher.group(2).substring(1),
                                            elem.getReturnType(),
                                            typeArgument,
                                            propertyKind
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
