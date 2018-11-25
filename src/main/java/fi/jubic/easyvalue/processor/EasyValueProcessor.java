package fi.jubic.easyvalue.processor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.service.AutoService;
import com.google.auto.value.AutoValue;
import com.squareup.javapoet.*;
import fi.jubic.easyvalue.EasyProperty;
import fi.jubic.easyvalue.EasyValue;

import javax.annotation.Nullable;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "fi.jubic.easyvalue.EasyValue",
        "fi.jubic.easyvalue.EasyProeprty"
})
public class EasyValueProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;

    private Map<String, String> annotatedClasses;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();

        annotatedClasses = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(EasyValue.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied only to classes");
                return true;
            }

            TypeElement typeElement = (TypeElement) element;
            annotatedClasses.put(
                    typeElement.getQualifiedName()
                            .toString(),
                    elements.getPackageOf(typeElement)
                            .getQualifiedName()
                            .toString()
            );

            String generatedName;
            {
                List<String> nestingTree = new ArrayList<>();
                Element enclosing = typeElement.getEnclosingElement();
                while (enclosing.getKind() == ElementKind.CLASS) {
                    nestingTree.add(enclosing.getSimpleName().toString());
                    enclosing = enclosing.getEnclosingElement();
                }

                generatedName = nestingTree.stream()
                        .reduce(
                                "EasyValue_",
                                (a, b) -> a + b + "_"
                        )
                        + typeElement.getSimpleName().toString();
            }

            String packageName;
            {
                PackageElement pkg = processingEnv.getElementUtils().getPackageOf(element);
                packageName = pkg.getQualifiedName().toString();
            }

            TypeSpec.Builder easyClass = TypeSpec
                    .classBuilder(generatedName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .superclass(TypeName.get(typeElement.asType()))
                    .addAnnotation(AutoValue.class);


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

            if (typeVariables.size() > 0) {
                easyClass = easyClass.addTypeVariables(typeVariables);
            }

            boolean hasJson = !element.getAnnotation(EasyValue.class).excludeJson();

            if (hasJson) {
                easyClass = easyClass
                        .addAnnotation(
                                AnnotationSpec.builder(JsonDeserialize.class)
                                        .addMember(
                                                "builder",
                                                "$L_$L.$L.$L",
                                                "AutoValue",
                                                generatedName,
                                                "Builder",
                                                "class"
                                        )
                                        .build()
                        )
                        .addAnnotation(
                                AnnotationSpec.builder(JsonInclude.class)
                                        .addMember(
                                                "value",
                                                "$T.$L",
                                                JsonInclude.Include.class,
                                                JsonInclude.Include.NON_NULL.name()
                                        )
                                        .build()
                        )
                        .addAnnotation(
                                AnnotationSpec.builder(JsonIgnoreProperties.class)
                                        .addMember(
                                                "ignoreUnknown",
                                                "true"
                                        )
                                        .build()
                        );
            }

            List<Property> properties = new ArrayList<>();

            Element parentBuilder = null;
            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() != ElementKind.CLASS) continue;
                if (!enclosedElement.getSimpleName().toString().equals("Builder")) continue;
                parentBuilder = enclosedElement;
            }
            if (parentBuilder == null) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Could not find Builder class"
                );
                return true;
            }

            for (Element valueElement : roundEnvironment.getElementsAnnotatedWith(EasyProperty.class)) {
                if (valueElement.getEnclosingElement() != element) continue;

                if (valueElement.getKind() != ElementKind.METHOD) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied only to methods");
                    return true;
                }

                Property prop;
                {
                    String annotatedName = valueElement.getAnnotation(EasyProperty.class).value();
                    String jsonName = annotatedName.length() > 0
                            ? annotatedName
                            : valueElement.getSimpleName().toString();

                    prop = new Property(
                            valueElement.getSimpleName(),
                            ((ExecutableType) valueElement.asType()).getReturnType(),
                            jsonName
                    );
                }

                properties.add(prop);

                MethodSpec.Builder propertyMethod = MethodSpec.overriding((ExecutableElement) valueElement)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

                if (valueElement.getAnnotation(Nullable.class) != null) {
                    propertyMethod = propertyMethod.addAnnotation(Nullable.class);
                }

                if (hasJson) {
                    propertyMethod = propertyMethod.addAnnotation(
                            AnnotationSpec.builder(JsonProperty.class)
                                    .addMember("value", "\"$L\"", prop.getJsonName())
                                    .build()
                    );
                }

                easyClass.addMethod(
                        propertyMethod.build()
                );
            }

            TypeSpec.Builder builderClass = TypeSpec.classBuilder("Builder")
                    .addAnnotation(AutoValue.Builder.class)
                    .addAnnotation(
                            AnnotationSpec.builder(JsonIgnoreProperties.class)
                                    .addMember(
                                            "ignoreUnknown",
                                            "true"
                                    )
                                    .build()
                    )
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT);

            if (typeVariables.size() > 0) {
                builderClass = builderClass.addTypeVariables(typeVariables);
            }

            TypeName builderType = ClassName.get(packageName, generatedName, "Builder");
            if (typeVariables.size() > 0) {
                builderType = ParameterizedTypeName.get(
                        (ClassName) builderType,
                        typeVariables.toArray(new TypeName[0])
                );
            }

            for (Property property : properties) {
                String nameString = property.getName().toString().substring(0, 1).toUpperCase()
                        + property.getName().toString().substring(1);

                MethodSpec.Builder setterMethod = MethodSpec.methodBuilder("set" + nameString)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(
                                TypeName.get(property.getType()),
                                property.getName().toString()
                        )
                        .returns(builderType);

                if (hasJson) {
                    setterMethod = setterMethod.addAnnotation(
                            AnnotationSpec.builder(JsonProperty.class)
                                    .addMember("value", "\"$L\"", property.getJsonName())
                                    .build()
                    );
                }

                builderClass = builderClass.addMethod(setterMethod.build());
            }

            TypeName easyName = ClassName.get(packageName, generatedName);
            if (typeVariables.size() > 0) {
                easyName = ParameterizedTypeName.get(
                        (ClassName) easyName,
                        typeVariables.toArray(new TypeVariableName[0])
                );
            }

            easyClass
                    .addType(
                            builderClass
                                    .addMethod(
                                            MethodSpec.methodBuilder("build")
                                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                                    .returns(easyName)
                                                    .build()
                                    )
                                    .build()
                    )
                    .addType(
                            builderWrapper(
                                    TypeName.get(typeElement.asType()),
                                    packageName,
                                    generatedName,
                                    properties,
                                    typeVariables
                            )
                    )
                    .addMethod(
                            MethodSpec.methodBuilder("toInnerBuilder")
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(builderType)
                                    .build()
                    )
                    .addMethod(
                            MethodSpec.methodBuilder("toBuilder")
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.get(parentBuilder.asType()))
                                    .addStatement(
                                            "$T wrapper = new $T()",
                                            TypeName.get(parentBuilder.asType()),
                                            TypeName.get(parentBuilder.asType())
                                    )
                                    .addStatement(
                                            "wrapper.builder = toInnerBuilder()"
                                    )
                                    .addStatement("return wrapper")
                                    .build()
                    );

            {
                MethodSpec.Builder getBuilderSpecBuilder = MethodSpec.methodBuilder("getBuilder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.get(parentBuilder.asType()))
                        .addStatement(
                                "$T wrapper = new $T()",
                                TypeName.get(parentBuilder.asType()),
                                TypeName.get(parentBuilder.asType())
                        )
                        .addStatement(
                                "wrapper.builder = new $L_$L.$L()",
                                "AutoValue",
                                generatedName,
                                "Builder"
                        )
                        .addStatement("return wrapper");

                if (typeVariables.size() > 0) {
                    getBuilderSpecBuilder = getBuilderSpecBuilder.addTypeVariables(typeVariables);
                }

                easyClass.addMethod(getBuilderSpecBuilder.build());
            }

            try {
                JavaFile.builder(packageName, easyClass.build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    static class Property {
        private final Name name;
        private final TypeMirror type;
        private final String jsonName;

        Property(Name name, TypeMirror type, String jsonName) {
            this.name = name;
            this.type = type;
            this.jsonName = jsonName;
        }

        public Name getName() {
            return name;
        }

        public TypeMirror getType() {
            return type;
        }

        public String getJsonName() {
            return jsonName;
        }
    }

    private TypeSpec builderWrapper(
            TypeName originClass,
            String packageName,
            String generatedName,
            List<Property> properties,
            List<TypeVariableName> typeVariables
    ) {
        TypeName klassType = ClassName.get(packageName, generatedName);
        TypeName builderType = ClassName.get(packageName, generatedName, "Builder");
        if (typeVariables.size() > 0) {
            builderType = ParameterizedTypeName.get(
                    (ClassName) builderType,
                    typeVariables.toArray(new TypeName[0])
            );
        }

        TypeVariableName typeVariable = TypeVariableName.get("BBB");
        List<TypeVariableName> combinedTypeVariableNames = new ArrayList<>(typeVariables);
        StringBuilder externalVars = new StringBuilder();
        for (TypeVariableName name : combinedTypeVariableNames) {
            externalVars.append(name.name);
            externalVars.append(", ");
        }
        combinedTypeVariableNames.add(
                TypeVariableName.get(
                        String.format("BBB extends BuilderWrapper<%sBBB>", externalVars)
                )
        );

        TypeSpec.Builder wrapper = TypeSpec.classBuilder("BuilderWrapper")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                .addTypeVariables(combinedTypeVariableNames)
                .addField(
                        FieldSpec.builder(builderType, "builder", Modifier.PROTECTED)
                                .build()
                )
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("this.builder = null")
                                .build()
                )
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(
                                        ParameterSpec.builder(builderType, "builder").build()
                                )
                                .addStatement("this.builder = builder")
                                .build()
                )
                .addMethod(
                        MethodSpec.methodBuilder("create")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(typeVariable)
                                .build()
                )
                .addMethod(
                        MethodSpec.methodBuilder("copyWrappedFrom")
                                .addParameter(
                                        ClassName.get(packageName, generatedName, "BuilderWrapper"),
                                        "other"
                                )
                                .addStatement("this.builder = other.builder")
                                .build()
                )
                .addMethod(
                        MethodSpec.methodBuilder("build")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(originClass)
                                .addStatement("return this.builder.build()")
                                .build()
                );

        for (Property property : properties) {
            String nameString = "set"
                    + property.getName().toString().substring(0, 1).toUpperCase()
                    + property.getName().toString().substring(1);
            wrapper = wrapper
                    .addMethod(
                            MethodSpec.methodBuilder(nameString)
                                    .addModifiers(Modifier.PUBLIC)
                                    .addParameter(
                                            ParameterSpec.builder(
                                                    TypeName.get(property.getType()),
                                                    property.getName().toString()
                                            ).build()
                                    )
                                    .returns(typeVariable)
                                    .addStatement("BBB t = create()")
                                    .addStatement(
                                            "t.builder = this.builder.$L($L)",
                                            nameString,
                                            property.getName().toString()
                                    )
                                    .addStatement("return t")
                                    .build()
                    );
        }

        return wrapper.build();
    }
}
