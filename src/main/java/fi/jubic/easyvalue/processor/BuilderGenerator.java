package fi.jubic.easyvalue.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import fi.jubic.easyvalue.EasyValue;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BuilderGenerator {
    void generateBuilder(
            ValueDefinition value,
            TypeSpec.Builder classBuilder
    ) {
        TypeSpec.Builder builderBuilder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.STATIC)
                .addTypeVariables(value.getTypeVariables());

        Set<Modifier> subClassModifiers = value.getBuilderElement().getModifiers();
        if (subClassModifiers.contains(Modifier.PUBLIC)) {
            builderBuilder.addModifiers(Modifier.PUBLIC);
        }
        else if (subClassModifiers.contains(Modifier.PRIVATE)) {
            builderBuilder.addModifiers(Modifier.PRIVATE);
        }

        value.getBuilderElement().getAnnotationMirrors().stream()
                .filter(annotationMirror -> !EasyValue.class.getName().equals(
                        ((QualifiedNameable) annotationMirror.getAnnotationType().asElement())
                                .getQualifiedName().toString()
                ))
                .map(AnnotationSpec::get)
                .forEach(builderBuilder::addAnnotation);

        value.getProperties().forEach(
                property -> generateField(property, builderBuilder)
        );
        generateConstructors(value, builderBuilder);
        generateFromSource(value, builderBuilder);
        value.getProperties().forEach(
                property -> generateSetter(property, value, builderBuilder)
        );
        value.getProperties().forEach(
                property -> generateAccessor(property, builderBuilder)
        );
        generateBuild(value, builderBuilder);

        classBuilder.addType(builderBuilder.build());
    }

    private void generateField(
            PropertyDefinition property,
            TypeSpec.Builder builderBuilder
    ) {
        builderBuilder.addField(
                TypeName.get(property.getType()),
                property.getName(),
                Modifier.PRIVATE
        );
    }

    private void generateConstructors(
            ValueDefinition value,
            TypeSpec.Builder builderBuilder
    ) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

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

        builderBuilder
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .build()
                )
                .addMethod(
                        constructorBuilder.build()
                );
    }

    private void generateFromSource(
            ValueDefinition value,
            TypeSpec.Builder builderBuilder
    ) {
        MethodSpec.Builder fromSourceBuilder = MethodSpec.methodBuilder("fromSource")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariables(value.getTypeVariables())
                .addParameter(
                        TypeName.get(value.getElement().asType()),
                        "source"
                )
                .returns(TypeName.get(value.getBuilderElement().asType()))
                .addStatement(
                        "$L builder = new $T()",
                        value.getTypeVariables().isEmpty()
                                ? "Builder"
                                : ParameterizedTypeName.get(
                                        ClassName.bestGuess("Builder"),
                                        value.getTypeVariables().toArray(new TypeName[0])
                                ),
                        TypeName.get(value.getBuilderElement().asType())
                );

        value.getProperties().forEach(
                property -> fromSourceBuilder
                        .addStatement(
                                "builder.$L = source.$L()",
                                property.getName(),
                                property.getElement()
                                        .getSimpleName()
                                        .toString()
                        )
        );

        builderBuilder.addMethod(
                fromSourceBuilder
                        .addStatement(
                                "return ($T) builder",
                                TypeName.get(value.getBuilderElement().asType())
                        )
                        .build()
        );
    }

    private void generateSetter(
            PropertyDefinition property,
            ValueDefinition value,
            TypeSpec.Builder builderBuilder
    ) {
        String setterName = "set"
                + property.getName().substring(0, 1).toUpperCase()
                + property.getName().substring(1);

        MethodSpec.Builder setterBuilder = MethodSpec.methodBuilder(setterName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(value.getBuilderElement().asType()))
                .addParameter(
                        TypeName.get(property.getType()),
                        property.getName()
                )
                .addStatement(
                        "$L builder = new $T()",
                        value.getTypeVariables().isEmpty()
                                ? "Builder"
                                : ParameterizedTypeName.get(
                                        ClassName.bestGuess("Builder"),
                                        value.getTypeVariables().toArray(new TypeName[0])
                                ),
                        TypeName.get(value.getBuilderElement().asType())
                );

        if (
                property.getElement().getAnnotation(Nullable.class) == null
                        && !property.getType().getKind().isPrimitive()
        ) {
            setterBuilder
                    .beginControlFlow(
                            "if ($L == null)",
                            property.getName()
                    )
                    .addStatement(
                            "throw new $T(\"Null $L\")",
                            NullPointerException.class,
                            property.getName()
                    )
                    .endControlFlow();
        }

        value.getProperties().forEach(
                valueProperty -> {
                    if (valueProperty == property) {
                        setterBuilder.addStatement(
                                "builder.$L = $L",
                                valueProperty.getName(),
                                valueProperty.getName()
                        );
                    }
                    else {
                        setterBuilder.addStatement(
                                "builder.$L = this.$L",
                                valueProperty.getName(),
                                valueProperty.getName()
                        );
                    }
                }
        );

        builderBuilder.addMethod(
                setterBuilder
                        .addStatement(
                                "return ($T) builder",
                                TypeName.get(value.getBuilderElement().asType())
                        )
                        .build()
        );

        String witherName = "with"
                + property.getName().substring(0, 1).toUpperCase()
                + property.getName().substring(1);
        MethodSpec.Builder witherBuilder = MethodSpec.methodBuilder(witherName)
                .addJavadoc(
                        "Wither for annotation-less Jackson Builder Pattern support. "
                                + "Do not use manually."
                )
                .addAnnotation(Deprecated.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(value.getBuilderElement().asType()))
                .addParameter(
                        TypeName.get(property.getType()),
                        property.getName()
                )
                .addStatement(
                        "return $L($L)",
                        setterName,
                        property.getName()
                );

        property.getElement().getAnnotationMirrors().stream()
                .map(AnnotationSpec::get)
                .forEach(witherBuilder::addAnnotation);

        builderBuilder.addMethod(witherBuilder.build());
    }

    private void generateAccessor(
            PropertyDefinition property,
            TypeSpec.Builder builderBuilder
    ) {
        builderBuilder.addMethod(
                MethodSpec.methodBuilder(property.getElement().getSimpleName().toString())
                        .addModifiers(Modifier.PROTECTED)
                        .returns(TypeName.get(property.getType()))
                        .addStatement(
                                "return this.$L",
                                property.getName()
                        )
                        .build()
        );
    }

    private void generateBuild(
            ValueDefinition value,
            TypeSpec.Builder builderBuilder
    ) {
        MethodSpec.Builder buildBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(value.getElement().asType()))
                .addStatement("$T missing = \"\"", String.class);

        value.getProperties()
                .stream()
                .filter(property -> property.getElement().getAnnotation(Nullable.class) == null)
                .filter(property -> !property.getType().getKind().isPrimitive())
                .forEach(
                        property -> buildBuilder
                                .beginControlFlow(
                                        "if (this.$L == null)",
                                        property.getName()
                                )
                                .addStatement(
                                        "missing += \" $L\"",
                                        property.getName()
                                )
                                .endControlFlow()
                );

        TypeName valueConstructorType;
        if (value.getTypeVariables().isEmpty()) {
            valueConstructorType = ClassName.bestGuess(value.getGeneratedName());
        }
        else {
            valueConstructorType = ParameterizedTypeName.get(
                    ClassName.bestGuess(value.getGeneratedName()),
                    value.getTypeVariables().toArray(new TypeVariableName[0])
            );
        }

        buildBuilder
                .beginControlFlow("if (!missing.isEmpty())")
                .addStatement(
                        "throw new $T(\"Missing required properties:\" + missing)",
                        IllegalStateException.class
                )
                .endControlFlow()
                .addStatement(
                        "return new $T("
                                + value.getProperties().stream()
                                        .map(ignore -> "this.$L")
                                        .collect(Collectors.joining(", "))
                                + ")",
                        Stream.of(
                                Stream.of(valueConstructorType),
                                value.getProperties().stream()
                                        .map(PropertyDefinition::getName)
                        )
                                .flatMap(s -> s)
                                .toArray()
                );

        builderBuilder.addMethod(buildBuilder.build());
    }
}
