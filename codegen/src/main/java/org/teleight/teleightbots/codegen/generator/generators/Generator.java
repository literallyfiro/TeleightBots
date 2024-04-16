package org.teleight.teleightbots.codegen.generator.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import org.teleight.teleightbots.codegen.json.TelegramField;

import java.util.List;
import java.util.function.Function;

public sealed interface Generator<T> permits ObjectGenerator, MethodGenerator {

    ClassName API_METHOD_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiMethod");
    ClassName API_RESULT_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiResult");
    ClassName API_RESULT_SERIALIZABLE_INTERFACE = ClassName.get("org.teleight.teleightbots.api", "ApiMethodSerializable");

    ClassName NOT_NULL_ANNOTATION = ClassName.get("org.jetbrains.annotations", "NotNull");

    String METHODS_PACKAGE_NAME = "org.teleight.teleightbots.api.methods";
    String OBJECTS_PACKAGE_NAME = "org.teleight.teleightbots.api.objects";

    JavaFile generate(String key, T t);

    default String getCorrectPackageName(String type) {
        if (!isTelegramPrimitive(type)) {
            return OBJECTS_PACKAGE_NAME;
        }
        return METHODS_PACKAGE_NAME;
    }

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

    default TypeToSet retrieveTypeToSet(TelegramField telegramField) {
        String typeToSet = "";
        String name = "";

        for (String type : telegramField.types()) {
            type = sanitizeType(type);
            if (!isTelegramPrimitive(type)) {
                typeToSet = type;
                name = telegramField.name();
            } else {
                if (typeToSet.isEmpty() && isTelegramPrimitive(typeToSet)) {
                    typeToSet = type;
                    name = telegramField.name();
                }
                if (typeToSet.isEmpty()) {
                    typeToSet = type;
                    name = telegramField.name();
                }
            }

        }
        if (shouldBeLong(telegramField, typeToSet, name)) {
            typeToSet = "Long";
            name = telegramField.name();
        }
        if (shouldBeLong(telegramField, typeToSet, name)) {
            typeToSet = "Long";
        }

        return new TypeToSet(typeToSet, name);
    }

    default boolean isTelegramPrimitive(String type) {
        return type.contains("String")
                || type.contains("Integer")
                || type.contains("Long")
                || type.contains("Float")
                || type.contains("Double")
                || type.contains("Boolean");
    }


    default String sanitizeType(String type, boolean onlyBasicClassName) {
        if (type.contains("Array of")) {
            type = type.replace("Array of ", "");
            if (!onlyBasicClassName) {
                type += "[]";
            }
        }
        return type;
    }

    default String sanitizeType(String type) {
        return sanitizeType(type, false);
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

    private boolean shouldBeLong(TelegramField telegramField, String typeToSet, String name) {
        if (telegramField.description().contains("32 significant bits") && typeToSet.equals("Integer")) {
            return true;
        }
        return name.equals("chat_id") || name.equals("user_id");
    }

    record TypeToSet(String type, String name) {}

}
