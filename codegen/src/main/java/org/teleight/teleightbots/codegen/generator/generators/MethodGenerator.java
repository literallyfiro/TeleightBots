package org.teleight.teleightbots.codegen.generator.generators;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.teleight.teleightbots.codegen.json.TelegramMethod;
import org.teleight.teleightbots.codegen.json.TelegramField;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public non-sealed class MethodGenerator implements Generator<TelegramMethod> {

    @Override
    public JavaFile generate(String key, TelegramMethod method) {
        String correctedClassName = key.substring(0, 1).toUpperCase() + key.substring(1);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.recordBuilder(correctedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unused")
                        .build())
                .addJavadoc("AutoGenerated Code. Do not modify!");

        // check if method had any subtypes
        if (!(method.subtype_of() == null)) {
            for (String subtypeOf : method.subtype_of()) {
                typeSpecBuilder.addSuperinterface(ClassName.get(METHODS_PACKAGE_NAME, sanitizeType(subtypeOf)));
            }
        }

        // if there are no fields, return an empty method
        if (method.fields() == null) {
            return JavaFile.builder(METHODS_PACKAGE_NAME, typeSpecBuilder.build())
                    .skipJavaLangImports(true)
                    .build();
        }

        List<TelegramField> requiredFields = populateFields(method, typeSpecBuilder);

        typeSpecBuilder.addMethod(createEndpointMethod(key));

        // ! todo add multipart
        if (method.returns().length == 1) {
            generateOneReturnMethod(method, typeSpecBuilder);
        } else {
            generateMultipleReturnMethod(method, typeSpecBuilder);
        }

        generateBuilderClass(correctedClassName, method, typeSpecBuilder, requiredFields);

        return JavaFile.builder(METHODS_PACKAGE_NAME, typeSpecBuilder.build())
                .skipJavaLangImports(true)
                .build();
    }

    private List<TelegramField> populateFields(TelegramMethod method, TypeSpec.Builder typeSpecBuilder) {
        List<TelegramField> requiredFields = new ArrayList<>();
        for (TelegramField field : method.fields()) {
            TypeToSet typeToSet = retrieveTypeToSet(field);
            String type = sanitizeType(typeToSet.type());
            TypeName returning = fetchTypeNameForType(type, false);

            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(returning, typeToSet.name());
            typeSpecBuilder.addField(fieldSpecBuilder.build());

            if (field.required()) {
                requiredFields.add(field);
            }
        }
        return requiredFields;
    }

    private MethodSpec createEndpointMethod(String key) {
        return MethodSpec.methodBuilder("getEndpointURL")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", key)
                .returns(String.class)
                .build();
    }

    private void generateOneReturnMethod(TelegramMethod method, TypeSpec.Builder typeSpecBuilder) {
        String type = sanitizeType(method.returns()[0], true);
        TypeName parameterizedTypeName = fetchTypeNameForType(type, true);
        typeSpecBuilder.addSuperinterface(parameterizedTypeName);

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("deserializeResponse")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(String.class, "answer").addAnnotation(NOT_NULL_ANNOTATION).build())
                .addStatement("return deserializeResponse(answer, " + type + ".class)")
                .returns(ClassName.get("", type))
                .build());
    }

    private TypeName fetchTypeNameForType(String type, boolean parameterized) {
        String packageName = getCorrectPackageName(type);
        TypeName typeName = ClassName.get(packageName, type);
        if (parameterized) {
            return ParameterizedTypeName.get(API_METHOD_INTERFACE, typeName);
        }
        return typeName;
    }

    private void generateMultipleReturnMethod(TelegramMethod method, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder.addSuperinterface(API_RESULT_SERIALIZABLE_INTERFACE);
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("getSerializableClasses")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return List.of(" + listToNiceString(Arrays.asList(method.returns()), false, String::toString) + ")")
                .returns(List.class)
                .build());

    }

    private void generateBuilderClass(String className, TelegramMethod telegramMethod, TypeSpec.Builder typeSpecBuilder, List<TelegramField> requiredFields) {
        TypeSpec.Builder builderTypeSpecBuilder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("AutoGenerated Code. Do not modify!");

        MethodSpec.Builder methodSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        MethodSpec.Builder ofBuilder = MethodSpec.methodBuilder("of")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("", "Builder"));

        for (TelegramField telegramField : telegramMethod.fields()) {
            TypeToSet typeToSet = retrieveTypeToSet(telegramField);
            TypeName returning = fetchTypeNameForType(sanitizeType(typeToSet.type()), false);
            FieldSpec.Builder fieldSpecBuilderBuilder = FieldSpec.builder(returning, toCamelCase(telegramField.name()))
                    .addModifiers(Modifier.PRIVATE);
            if (telegramField.required()) {
                fieldSpecBuilderBuilder.addModifiers(Modifier.FINAL);
            }
            builderTypeSpecBuilder.addField(fieldSpecBuilderBuilder.build());

            if (requiredFields.contains(telegramField)) {
                ofBuilder.addParameter(returning, telegramField.name());
                methodSpecBuilder.addParameter(returning, telegramField.name());
                methodSpecBuilder.addStatement("this.$N = $N", toCamelCase(telegramField.name()), telegramField.name());
            } else {
                MethodSpec.Builder methodSpecOtherStuffBuilder = MethodSpec.methodBuilder(toCamelCase(telegramField.name()))
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(NOT_NULL_ANNOTATION)
                        .returns(ClassName.get("", "Builder"))
                        .addParameter(ParameterSpec.builder(returning, telegramField.name()).addAnnotation(NOT_NULL_ANNOTATION).build())
                        .addStatement("this.$N = $N", toCamelCase(telegramField.name()), telegramField.name())
                        .addStatement("return this");
                try {
                    methodSpecOtherStuffBuilder.addJavadoc(telegramField.description());
                } catch (Exception ignored) {}
                builderTypeSpecBuilder.addMethod(methodSpecOtherStuffBuilder.build());
            }
        }

        String niceString = listToNiceString(Arrays.asList(telegramMethod.fields()), true, TelegramField::name);
        builderTypeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("", className))
                .addStatement("return new " + className + "(" + niceString + ")")
                .build()
        );

        String niceString2 = listToNiceString(requiredFields, false, TelegramField::name);
        ofBuilder.addStatement("return new Builder" + "(" + niceString2 + ")");

        builderTypeSpecBuilder.addMethod(methodSpecBuilder.build());
        typeSpecBuilder.addType(builderTypeSpecBuilder.build());
        typeSpecBuilder.addMethod(ofBuilder.build());
    }

}
