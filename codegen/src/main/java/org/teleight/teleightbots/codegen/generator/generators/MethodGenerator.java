package org.teleight.teleightbots.codegen.generator.generators;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.codegen.json.TelegramMethod;

import javax.lang.model.element.Modifier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public non-sealed class MethodGenerator implements Generator<TelegramMethod> {

    @Override
    public TypeSpec.Builder generate(String key, TelegramMethod method) {
        // Methods are in camelCase. We need to make the first letter capitalized
        String correctedClassName = key.substring(0, 1).toUpperCase() + key.substring(1);
        
        TypeSpec.Builder typeSpecBuilder = TypeSpec.recordBuilder(correctedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unused")
                        .build())
                .addJavadoc("AutoGenerated Code. Do not modify!");

        // check if method had any subtypes
        if (!(method.subtypeOf() == null)) {
            for (TypeName subtypeOf : method.subtypeOf()) {
                typeSpecBuilder.addSuperinterface(subtypeOf);
            }
        }

        // Start populating the fields of the record
        Map<TelegramField, Boolean> populatedFields = populateFields(method.fields(), typeSpecBuilder);

        // Override the getEndpointURL method
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("getEndpointURL")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", key)
                .returns(String.class)
                .build());
        
        // A telegram method can return different types.
        //  - If the method returns only one type, then the super interface is ApiMethod<T>
        //  - If the method returns multiple types, we need to extends to ApiMethodSerializable<T>
        //  - If the method returns one type but needs a multipart field, we need to generate it accordingly. todo add multipart
        if (method.returns().length == 1) {
            generateOneReturnMethod(method.returns()[0], typeSpecBuilder);
        } else {
            generateMultipleReturnMethod(method, typeSpecBuilder);
        }

        generateBuilderClass(correctedClassName, populatedFields, typeSpecBuilder);

        return typeSpecBuilder;
    }

    @Override
    public String getPackageName() {
        return METHODS_PACKAGE_NAME;
    }

    private List<TelegramField> populateFields(TelegramMethod method, TypeSpec.Builder typeSpecBuilder) {
        if (method.fields() == null) return List.of();

        List<TelegramField> requiredFields = new ArrayList<>();
        for (TelegramField field : method.fields()) {
            if (field == null) continue;
            TypeName typeToSet = retrieveMostImportantType(field);
            
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(typeToSet, field.name());
            typeSpecBuilder.addField(fieldSpecBuilder.build());

            if (field.required()) {
                requiredFields.add(field);
            }
        }
        return requiredFields;
    }


    private void generateOneReturnMethod(TypeName type, TypeSpec.Builder typeSpecBuilder) {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(API_METHOD_INTERFACE, type);
        typeSpecBuilder.addSuperinterface(parameterizedTypeName);

        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("deserializeResponse")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(String.class, "answer").addAnnotation(NOT_NULL_ANNOTATION).build())
                .addStatement("return deserializeResponse(answer, $L.class)", type)
                .returns(type)
                .build());
    }

    private void generateMultipleReturnMethod(TelegramMethod method, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder.addSuperinterface(API_RESULT_SERIALIZABLE_INTERFACE);
        
        TypeName serializableWildcard = WildcardTypeName.subtypeOf(Serializable.class);
        TypeName classParameter = ParameterizedTypeName.get(JAVA_CLASS_CLASSNAME, serializableWildcard);
        TypeName listParameterizedType = ParameterizedTypeName.get(JAVA_LIST_CLASSNAME, classParameter);

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("getSerializableClasses")
                .addAnnotation(Override.class)
                .addAnnotation(NOT_NULL_ANNOTATION)
                .addModifiers(Modifier.PUBLIC)
                .addCode("return $T.of(", List.class)
                .returns(listParameterizedType);

        boolean isLast;
        final TypeName[] returns = method.returns();
        for (int i = 0; i < returns.length; i++) {
            TypeName returnClass = returns[i];
            isLast = i == returns.length - 1;

            methodSpecBuilder.addCode("$T.class", returnClass);
            if (!isLast) {
                methodSpecBuilder.addCode(", ");
            } else {
                methodSpecBuilder.addCode(");");
            }

        }

        typeSpecBuilder.addMethod(methodSpecBuilder.build());
    }

}
