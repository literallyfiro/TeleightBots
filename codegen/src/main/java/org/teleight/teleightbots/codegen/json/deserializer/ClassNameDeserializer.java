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

public class ClassNameDeserializer implements JsonDeserializer<TypeName[]> {

    String objectsPackageName = "org.teleight.teleightbots.api.objects";
    String[] telegramPrimitiveClasses = new String[] { "Integer", "Long", "Float", "Double", "Boolean", "String" };

    @Override
    public TypeName[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray typesArray = json.getAsJsonArray();
        TypeName[] typeNames = new TypeName[typesArray.size()];
        for (int i = 0; i < typesArray.size(); i++) {
            String type = typesArray.get(i).getAsString();
            String packageName = isPrimitive(type) ? "" : objectsPackageName;

            if (type.endsWith("[]")) {
                typeNames[i] = ArrayTypeName.of(ClassName.get(packageName, type.substring(0, type.length() - 2)));
            } else {
                typeNames[i] = ClassName.get(packageName, type);
            }
        }
        return typeNames;
    }

    private boolean isPrimitive(String className) {
        for (String telegramPrimitiveClass : telegramPrimitiveClasses) {
            if (className.equals(telegramPrimitiveClass)) {
                return true;
            }
        }
        return false;
    }
}
