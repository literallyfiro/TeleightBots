package org.teleight.teleightbots.codegen.json.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.utils.ArrayUtils;

import java.util.Map;

import static org.teleight.teleightbots.codegen.generator.generators.Generator.OBJECTS_PACKAGE_NAME;

public class TelegramFieldObjectReplacer {

    private static final Map<String, String> customObjectsMatrix = Map.of(
            "parse_mode", "ParseMode"
    );

    public static TelegramField[] replaceTypes(String name, TelegramField[] fields) {
        if (fields == null || fields.length == 0) return fields;

        for (int i = 0; i < fields.length; i++) {
            TelegramField field = fields[i];
            String fieldName = field.name();

            if (fieldName.equalsIgnoreCase("chat_id") || (name.equals("Chat") && field.name().equals("id"))) {
                fields[i] = new TelegramField(field.name(), new TypeName[]{ClassName.get(String.class)}, field.required(), field.description());
            }
            if (fieldName.equalsIgnoreCase("user_id") || (name.equals("User") && field.name().equals("id"))) {
                fields[i] = new TelegramField(field.name(), new TypeName[]{ClassName.get(Long.class)}, field.required(), field.description());
            }

            for (Map.Entry<String, String> stringStringEntry : customObjectsMatrix.entrySet()) {
                final String key = stringStringEntry.getKey();
                final String value = stringStringEntry.getValue();
                if (field.name().equals(key)) {
                    TypeName[] newArray = ArrayUtils.add(field.types(), ClassName.get(OBJECTS_PACKAGE_NAME, value));
                    fields[i] = new TelegramField(field.name(), newArray, field.required(), field.description());
                }
            }
        }
        return fields;
    }

}
