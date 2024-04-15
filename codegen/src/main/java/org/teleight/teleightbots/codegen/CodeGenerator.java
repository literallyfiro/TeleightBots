package org.teleight.teleightbots.codegen;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.teleight.teleightbots.codegen.json.ApiRoot;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.codegen.json.TelegramObject;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final ClassName apiResultInterface = ClassName.get("org.teleight.teleightbots.api", "ApiResult");
    private final String objectsPackageName = "org.teleight.teleightbots.api.objects";

    public static void main(String[] args) {
        new CodeGenerator().generateApiClasses();
    }

    private void generateApiClasses() {
        try (InputStream in = getClass().getResourceAsStream("/api.json")) {
            if (in == null) {
                throw new RuntimeException("api.json not found");
            }
            ApiRoot root = gson.fromJson(new InputStreamReader(in), ApiRoot.class);

            generateTelegramObjects(root);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateTelegramObjects(ApiRoot root) throws IOException {
        for (Map.Entry<String, TelegramObject> telegramObject : root.types().entrySet()) {
            String name = telegramObject.getKey();
            TelegramObject object = telegramObject.getValue();
            generateTelegramObject(object, name);
        }
    }

    private void generateTelegramObject(TelegramObject object, String name) throws IOException {
        System.out.println("Generating telegram object: " + name);

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(apiResultInterface)
                .addJavadoc("AutoGenerated Code. Do not modify!");

        if (object.fields() == null) return;

        List<TelegramField> requiredFields = new ArrayList<>();

        for (TelegramField telegramField : object.fields()) {
            for (String type : telegramField.types()) {
                type = sanitizeType(type);
                ClassName className = ClassName.get(objectsPackageName, type);

                boolean isRequired = !telegramField.description().toLowerCase().contains("optional");
                if (isRequired) {
                    requiredFields.add(telegramField);
                }
                FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(className, toCamelCase(telegramField.name()))
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .addAnnotation(AnnotationSpec.builder(JsonProperty.class)
                                .addMember("value", "$S", telegramField.name())
                                .addMember("required", "$S", isRequired)
                                .build()
                        );
                typeSpecBuilder.addField(fieldSpecBuilder.build());
            }
        }

        JavaFile javaFile = JavaFile.builder(objectsPackageName, typeSpecBuilder.build())
                .skipJavaLangImports(true)
                .build();
        javaFile.writeTo(new File("test"));
    }

    private String toCamelCase(String snakeCase) {
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


    private String sanitizeType(String type) {
        if (type.contains("Array of")) {
            type = type.replace("Array of ", "");
            type += "[]";
        }
        return type;
    }

}
