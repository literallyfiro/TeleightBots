package org.teleight.teleightbots.codegen.json.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;

import static org.teleight.teleightbots.codegen.generator.generators.Generator.OBJECTS_PACKAGE_NAME;

public class ClassNameDeserializer implements JsonDeserializer<TypeName[]> {

    @Override
    public TypeName[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray typesArray = json.getAsJsonArray();
        TypeName[] typeNames = new TypeName[typesArray.size()];
        for (int i = 0; i < typesArray.size(); i++) {
            String type = typesArray.get(i).getAsString();
            String packageName = isPrimitive(type) ? "" : OBJECTS_PACKAGE_NAME;
            if (type.startsWith("Array of")) {
                if (type.startsWith("Array of Array of")) {
                    typeNames[i] = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(packageName, type.substring("Array of Array of ".length()))));
                } else {
                    typeNames[i] = ArrayTypeName.of(ClassName.get(packageName, type.substring("Array of ".length())));
                }
            } else {
                typeNames[i] = ClassName.get(packageName, type);
            }
        }
        return typeNames;
    }

    private boolean isPrimitive(String className) {
        return className.contains("String")
                || className.contains("Integer")
                || className.contains("Long")
                || className.contains("Float")
                || className.contains("Double")
                || className.contains("Boolean");
    }
}
