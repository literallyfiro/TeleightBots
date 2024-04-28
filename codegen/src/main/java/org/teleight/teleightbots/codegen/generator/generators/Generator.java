package org.teleight.teleightbots.codegen.generator.generators;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teleight.teleightbots.codegen.json.TelegramField;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public sealed interface Generator<T> permits ObjectGenerator, MethodGenerator {

    ClassName API_METHOD_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiMethod");
    ClassName API_MULTIPART_METHOD_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "MultiPartApiMethod");
    ClassName API_RESULT_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiResult");
    ClassName API_RESULT_SERIALIZABLE_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiMethodSerializable");
    ClassName INPUTFILE = ClassName.get("org.teleight.teleightbots.api.objects", "InputFile");

    ClassName JAVA_LIST_CLASSNAME = ClassName.get(List.class);
    ClassName JAVA_MAP_CLASSNAME = ClassName.get(Map.class);
    ClassName OBJECT_CLASSNAME = ClassName.get(Object.class);
    ClassName JAVA_STRING_CLASSNAME = ClassName.get(String.class);
    ClassName JAVA_CLASS_CLASSNAME = ClassName.get(Class.class);

    ClassName JSON_PROPERTY_CLASSNAME = ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty");

    String METHODS_PACKAGE_NAME = "org.teleight.teleightbots.api.methods";
    String OBJECTS_PACKAGE_NAME = "org.teleight.teleightbots.api.objects";

    TypeSpec.Builder generate(String name, T t);

    String getPackageName();

    default String toCamelCase(String snakeCase) {
        StringBuilder camelCaseBuilder = new StringBuilder();
        boolean nextCharToUpperCase = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextCharToUpperCase = true;
            } else if (nextCharToUpperCase) {
                camelCaseBuilder.append(Character.toUpperCase(c));
                nextCharToUpperCase = false;
            } else {
                camelCaseBuilder.append(c);
            }
        }
        return camelCaseBuilder.toString();
    }

    /**
     * Since we can only add one type of the same field in java records, we should get the most important type.
     * Custom objects are more important than "primitive" ones, so they take precedence
     * If there are no custom objects, then we return a primitive one
     *
     * @param telegramField the telegram field
     * @return the most important type
     */
    default TypeName retrieveMostImportantType(TelegramField telegramField) {
        TypeName mostImportantField = telegramField.types()[0];

        for (TypeName typeName : telegramField.types()) {
            if (typeName instanceof ClassName className) {
                if (className.packageName().equals(OBJECTS_PACKAGE_NAME)) {
                    mostImportantField = typeName;
                    break;
                } else {
                    mostImportantField = typeName;
                }
            }
        }
        return mostImportantField;
    }

    default <O> String listToNiceString(List<O> list, boolean toCamelCase, Function<O, String> nameFunction) {
        StringBuilder sb = new StringBuilder();
        boolean last;
        for (int i = 0; i < list.size(); i++) {
            O item = list.get(i);
            last = i == list.size() - 1;

            String name = toCamelCase ? toCamelCase(nameFunction.apply(item)) : nameFunction.apply(item);
            sb.append(name);

            if (!last) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    default Map<TelegramField, Boolean> populateFields(TelegramField[] fields, TypeSpec.Builder typeSpecBuilder) {
        if (fields == null) return Map.of();

        Map<TelegramField, Boolean> populatedFields = new LinkedHashMap<>();
        for (TelegramField field : fields) {
            if (field == null) continue;
            TypeName typeToSet = retrieveMostImportantType(field);
            String camelCaseFieldName = toCamelCase(field.name());

            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(typeToSet, camelCaseFieldName)
                    .addAnnotation(AnnotationSpec.builder(JSON_PROPERTY_CLASSNAME)
                            .addMember("value", "$S", field.name())
                            //.addMember("required", "$L", field.required())
                            .build()
                    );

            if (field.required()) {
                fieldSpecBuilder.addAnnotation(NotNull.class);
            } else {
                fieldSpecBuilder.addAnnotation(Nullable.class);
            }

            typeSpecBuilder.addJavadoc("\n@param $L $L", camelCaseFieldName, field.description());

            typeSpecBuilder.addField(fieldSpecBuilder.build());

            populatedFields.put(field, field.required());
        }
        return populatedFields;
    }

    default void generateBuilderClass(String className,
                                      Map<TelegramField, Boolean> populatedFields,
                                      TypeSpec.Builder typeSpecBuilder) {
        // Check for empty fields
        if (populatedFields == null) {
            return;
        }

        // Builder class definition
        TypeSpec.Builder builderTypeSpecBuilder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("AutoGenerated Code. Do not modify!");

        // Constructor for Builder
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        // Static factory method for Builder
        MethodSpec.Builder ofBuilder = MethodSpec.methodBuilder("of")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get("", "Builder"));

        // Process each field - I kinda hate this
        processBuilderField(populatedFields, builderTypeSpecBuilder, ofBuilder, constructorBuilder);

        // I hate this more
        List<TelegramField> allFields = new ArrayList<>(populatedFields.keySet());
        List<TelegramField> requiredFields = populatedFields.keySet().stream().filter(TelegramField::required).collect(Collectors.toList());

        // Build method for final object creation
        String niceString = listToNiceString(allFields, true, TelegramField::name);
        builderTypeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("", className))
                .addStatement("return new $L($L)", className, niceString)
                .build());

        // Build the "of" method with parameters for required fields
        String builderNiceString = listToNiceString(requiredFields, true, TelegramField::name);
        ofBuilder.addStatement("return new Builder($L)", builderNiceString);
        builderTypeSpecBuilder.addMethod(constructorBuilder.build());

        // Add Builder class and "of" method to the main type spec
        typeSpecBuilder.addType(builderTypeSpecBuilder.build());
        typeSpecBuilder.addMethod(ofBuilder.build());
    }

    private void processBuilderField(Map<TelegramField, Boolean> populatedFields,
                                     TypeSpec.Builder builderTypeSpecBuilder,
                                     MethodSpec.Builder ofBuilder,
                                     MethodSpec.Builder constructorBuilder) {
        for (Map.Entry<TelegramField, Boolean> telegramFieldBooleanEntry : populatedFields.entrySet()) {
            TelegramField telegramField = telegramFieldBooleanEntry.getKey();
            boolean required = telegramField.required();

            TypeName typeToSet = retrieveMostImportantType(telegramField);
            String camelCaseField = toCamelCase(telegramField.name());

            // Field definition in Builder class
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(typeToSet, camelCaseField)
                    .addModifiers(Modifier.PRIVATE);

            // If required field, mark as final
            if (telegramField.required()) {
                fieldSpecBuilder.addModifiers(Modifier.FINAL);
            }

            // Add field to Builder class
            builderTypeSpecBuilder.addField(fieldSpecBuilder.build());

            // Add parameter and assignment for required fields in constructor and of method
            if (required) {
                ofBuilder.addParameter(typeToSet, camelCaseField);
                constructorBuilder.addParameter(typeToSet, camelCaseField);
                constructorBuilder.addStatement("this.$N = $N", camelCaseField, camelCaseField);
            } else {
                // Builder method for optional fields with NotNull annotation and chaining
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(camelCaseField)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(NotNull.class)
                        .returns(ClassName.get("", "Builder"))
                        .addParameter(ParameterSpec.builder(typeToSet, camelCaseField).addAnnotation(NotNull.class).build())
                        .addStatement("this.$N = $N", camelCaseField, camelCaseField)
                        .addStatement("return this");
                try {
                    methodSpecBuilder.addJavadoc(telegramField.description());
                } catch (Exception ignored) {}
                builderTypeSpecBuilder.addMethod(methodSpecBuilder.build());
            }
        }
    }

}
