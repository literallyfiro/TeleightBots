package org.teleight.teleightbots.codegen.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.javapoet.JavaFile;
import org.teleight.teleightbots.codegen.json.ApiRoot;
import org.teleight.teleightbots.codegen.json.Method;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.codegen.json.TelegramObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class CodeGenerator {

    private final File autogeneratedPath = new File("src/autogenerated/java");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public void generateApiClasses() {
        try (InputStream in = getClass().getResourceAsStream("/api.json")) {
            if (in == null) {
                throw new RuntimeException("api.json not found");
            }
            ApiRoot root = gson.fromJson(new InputStreamReader(in), ApiRoot.class);

            generateTelegramObjects(root);
            generateTelegramMethods(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateTelegramObjects(ApiRoot root) throws IOException {
        for (Map.Entry<String, TelegramObject> telegramObject : root.types().entrySet()) {
            String name = telegramObject.getKey();
            TelegramObject object = telegramObject.getValue();

            JavaFile javaFile = new ObjectGenerator().generate(name, object);
            if (javaFile == null) {
                System.out.println(name + "null");
                continue;
            }
            javaFile.writeTo(autogeneratedPath);
        }
    }

    private void generateTelegramMethods(ApiRoot root) throws IOException {
        for (Map.Entry<String, Method> telegramObject : root.methods().entrySet()) {
            String name = telegramObject.getKey();
            Method method = telegramObject.getValue();
            JavaFile javaFile = new MethodGenerator().generate(name, method);
            if (javaFile == null) {
                System.out.println(name + "null");
                continue;
            }
            javaFile.writeTo(autogeneratedPath);
        }
    }

    public static String toCamelCase(String snakeCase) {
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

    public static TypeToSet retrieveTypeToSet(TelegramField telegramField) {
        String typeToSet = "";
        String name = "";

        for (String type : telegramField.types()) {
            type = CodeGenerator.sanitizeType(type);
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
        if (telegramField.description().contains("32 significant bits") && typeToSet.equals("Integer")) {
            typeToSet = "Long";
            name = telegramField.name();
        }

        return new TypeToSet(typeToSet, name);
    }

    public static boolean isTelegramPrimitive(String type) {
        return type.contains("String")
                || type.contains("Integer")
                || type.contains("Long")
                || type.contains("Float")
                || type.contains("Double")
                || type.contains("Boolean");
    }


    public static String sanitizeType(String type) {
        if (type.contains("Array of")) {
            type = type.replace("Array of ", "");
            type += "[]";
        }
        return type;
    }

    public record TypeToSet(String type, String name) {}
}
