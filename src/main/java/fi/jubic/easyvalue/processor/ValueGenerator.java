package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fi.jubic.easyvalue.EasyValue;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ValueGenerator {
    private final ProcessingEnvironment processingEnv;

    ValueGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    ProcessingResult<Void> generateValue(ValueDefinition definition) {
        List<ProcessingMessage> messages = ProcessingMessage.list();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(definition.getGeneratedName())
                .addAnnotation(
                        AnnotationSpec.builder(Generated.class)
                                .addMember(
                                        "value",
                                        "\""
                                                + EasyValueProcessor.class.getCanonicalName()
                                                + "\""
                                )
                                .addMember(
                                        "date",
                                        "\""
                                                + Instant.now().toString()
                                                + "\""
                                )
                                .build()
                )
                .superclass(TypeName.get(definition.getElement().asType()))
                .addTypeVariables(definition.getTypeVariables());

        definition.getElement().getAnnotationMirrors().stream()
                .filter(annotationMirror -> !EasyValue.class.getName().equals(
                        ((QualifiedNameable) annotationMirror.getAnnotationType().asElement())
                                .getQualifiedName()
                                .toString()
                ))
                .map(AnnotationSpec::get)
                .forEach(classBuilder::addAnnotation);


        addModifiers(definition, classBuilder, messages);
        definition.getProperties().forEach(
                property -> generateField(property, classBuilder)
        );
        generateConstructor(definition, classBuilder);
        definition.getProperties().forEach(
                property -> generateAccessors(property, classBuilder)
        );
        generateToBuilder(definition, classBuilder, messages);
        generateToString(definition, classBuilder);
        generateEquals(definition, classBuilder);
        generateHashCode(definition, classBuilder);

        boolean hasBuilder = definition.getBuilderElement() != null;
        if (hasBuilder) {
            new BuilderGenerator()
                    .generateBuilder(
                            definition,
                            classBuilder
                    );
        }

        try {
            JavaFile.builder(
                    processingEnv.getElementUtils()
                            .getPackageOf(definition.getElement())
                            .getQualifiedName()
                            .toString(),
                    classBuilder.build()
            )
                    .build()
                    .writeTo(processingEnv.getFiler());
        }
        catch (IOException e) {
            e.printStackTrace();
            messages.add(
                    ProcessingMessage.of(
                            Diagnostic.Kind.ERROR,
                            "Could not write class",
                            definition.getElement()
                    )
            );
        }

        return ProcessingResult.of(messages);
    }

    private void addModifiers(
            ValueDefinition value,
            TypeSpec.Builder classBuilder,
            List<ProcessingMessage> messages
    ) {
        Set<Modifier> sourceModifiers = value.getElement().getModifiers();
        if (sourceModifiers.contains(Modifier.PUBLIC)) {
            classBuilder.addModifiers(Modifier.PUBLIC);
        }
        else if (sourceModifiers.contains(Modifier.PROTECTED)) {
            classBuilder.addModifiers(Modifier.PROTECTED);
        }
        if (!sourceModifiers.contains(Modifier.ABSTRACT)) {
            messages.add(
                    ProcessingMessage.of(
                            Diagnostic.Kind.ERROR,
                            "@EasyValue annotated class must be abstract",
                            value.getElement()
                    )
            );
        }
        if (sourceModifiers.contains(Modifier.PRIVATE)) {
            messages.add(
                    ProcessingMessage.of(
                            Diagnostic.Kind.ERROR,
                            "@EasyValue annotated class cannot be private",
                            value.getElement()
                    )
            );
        }
    }

    private void generateField(
            PropertyDefinition property,
            TypeSpec.Builder classBuilder
    ) {
        classBuilder.addField(
                TypeName.get(property.getType()),
                property.getName(),
                Modifier.PRIVATE,
                Modifier.FINAL
        );
    }

    private void generateConstructor(
            ValueDefinition value,
            TypeSpec.Builder classBuilder
    ) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        value.getProperties().forEach(
                property -> constructorBuilder.addParameter(
                        TypeName.get(property.getType()),
                        property.getName()
                )
        );

        value.getProperties().forEach(
                property -> constructorBuilder.addStatement(
                        "this.$L = $L",
                        property.getName(),
                        property.getName()
                )
        );

        classBuilder.addMethod(constructorBuilder.build());
    }

    private void generateAccessors(
            PropertyDefinition property,
            TypeSpec.Builder classBuilder
    ) {
        MethodSpec.Builder accessorBuilder = MethodSpec
                .overriding((ExecutableElement) property.getElement())
                .addStatement(
                        "return $L",
                        property.getName()
                );
        classBuilder.addMethod(accessorBuilder.build());
    }

    private void generateToBuilder(
            ValueDefinition value,
            TypeSpec.Builder classBuilder,
            List<ProcessingMessage> messages
    ) {
        value.getElement()
                .getEnclosedElements()
                .stream()
                .filter(element -> element.getSimpleName().toString().equals("toBuilder"))
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .map(element -> (ExecutableElement) element)
                .findFirst()
                .ifPresent(toBuilder -> {
                    if (!toBuilder.getParameters().isEmpty()) {
                        messages.add(
                                ProcessingMessage.of(
                                        Diagnostic.Kind.ERROR,
                                        "toBuilder method cannot take any parameters",
                                        value.getElement()
                                )
                        );
                    }
                    if (!toBuilder.getModifiers().contains(Modifier.ABSTRACT)) {
                        messages.add(
                                ProcessingMessage.of(
                                        Diagnostic.Kind.ERROR,
                                        "toBuilder must be abstract",
                                        value.getElement()
                                )
                        );
                    }
                    if (toBuilder.getModifiers().contains(Modifier.PRIVATE)) {
                        messages.add(
                                ProcessingMessage.of(
                                        Diagnostic.Kind.ERROR,
                                        "toBuilder cannot be private",
                                        value.getElement()
                                )
                        );
                    }

                    classBuilder.addMethod(
                            MethodSpec.overriding(toBuilder)
                                    .addStatement(
                                            "return ($T) $T.fromSource(this)",
                                            TypeName.get(value.getBuilderElement().asType()),
                                            ClassName.bestGuess("Builder")
                                    )
                                    .build()
                    );
                });


    }

    private void generateToString(
            ValueDefinition value,
            TypeSpec.Builder classBuilder
    ) {
        boolean overridesToString = value.getElement().getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(element -> element.getSimpleName().toString().equals("toString"))
                .map(element -> (ExecutableElement)element)
                .filter(
                        element -> String.class.getName().equals(element.getReturnType().toString())
                )
                .anyMatch(element -> element.getParameters().isEmpty());

        if (overridesToString) return;

        classBuilder.addMethod(
                MethodSpec.methodBuilder("toString")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(String.class))
                        .addStatement(
                                "return \"$L{\""
                                + value.getProperties().stream()
                                        .map(ignore -> "+ \"$L=\" + $L")
                                        .collect(Collectors.joining(" + \", \""))
                                + "+ \"}\"",
                                Stream.of(
                                        Stream.of(value.getElement().getQualifiedName().toString()),
                                        value.getProperties().stream()
                                                .flatMap(property -> Stream.of(
                                                        property.getName(),
                                                        property.getName()
                                                ))
                                )
                                        .flatMap(s -> s)
                                        .toArray()
                        )
                        .build()
        );
    }

    private void generateEquals(
            ValueDefinition value,
            TypeSpec.Builder classBuilder
    ) {
        boolean overridesEquals = value.getElement().getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(element -> element.getSimpleName().toString().equals("equals"))
                .map(element -> (ExecutableElement)element)
                .filter(element -> element.getReturnType().getKind().equals(TypeKind.BOOLEAN))
                .map(element -> element.getParameters().get(0))
                .anyMatch(
                        param -> Object.class.getCanonicalName().equals(param.asType().toString())
                );

        if (overridesEquals) return;

        classBuilder.addMethod(
                MethodSpec.methodBuilder("equals")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(
                                ClassName.get(Object.class),
                                "o"
                        )
                        .returns(TypeName.BOOLEAN)
                        .beginControlFlow("if (o == this)")
                        .addStatement(
                                "return true"
                        )
                        .endControlFlow()
                        .beginControlFlow(
                                "if (o instanceof $T)",
                                ClassName.bestGuess(
                                        value.getElement()
                                                .getQualifiedName()
                                                .toString()
                                )
                        )
                        .addStatement(
                                "$T that = ($T) o",
                                TypeName.get(value.getElement().asType()),
                                TypeName.get(value.getElement().asType())
                        )
                        .addStatement(
                                "return " + value.getProperties().stream()
                                        .map(ignore -> "$T.equals(this.$L, that.$L())")
                                        .collect(Collectors.joining(" && ")),
                                value.getProperties().stream()
                                        .flatMap(
                                                property -> Stream.of(
                                                        ClassName.get(Objects.class),
                                                        property.getName(),
                                                        property.getElement()
                                                                .getSimpleName()
                                                                .toString()
                                                )
                                        )
                                        .toArray()
                        )
                        .endControlFlow()
                        .addStatement("return false")
                        .build()
        );
    }

    private void generateHashCode(
            ValueDefinition value,
            TypeSpec.Builder classBuilder
    ) {
        boolean overridesToHashCode = value.getElement().getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(element -> element.getSimpleName().toString().equals("hashCode"))
                .map(element -> (ExecutableElement)element)
                .filter(element -> element.getReturnType().getKind().equals(TypeKind.INT))
                .anyMatch(element -> element.getParameters().isEmpty());

        if (overridesToHashCode) return;

        classBuilder.addMethod(
                MethodSpec.methodBuilder("hashCode")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.INT)
                        .addStatement(
                                "return $T.hash($L)",
                                ClassName.get(Objects.class),
                                value.getProperties()
                                        .stream()
                                        .map(PropertyDefinition::getName)
                                        .collect(Collectors.joining(", "))
                        )
                        .build()
        );
    }
}
