package org.teleight.teleightbots.codegen.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.javapoet.TypeName;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.codegen.json.TelegramObject;

import java.lang.reflect.Type;

import static org.teleight.teleightbots.codegen.json.utils.DescriptionExtrapolator.extrapolateDescriptionFromJson;
import static org.teleight.teleightbots.codegen.json.utils.TelegramFieldObjectReplacer.replaceTypes;

public class TelegramObjectDeserializer implements JsonDeserializer<TelegramObject> {

    private final String[] needsBuilder = {"ReplyParameters", "InlineKeyboardButton"};

    @Override
    public TelegramObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final String name = jsonObject.get("name").getAsString();
        final String href = jsonObject.get("href").getAsString();
        final String[] description = extrapolateDescriptionFromJson(jsonObject);
        TelegramField[] fields = replaceTypes(name, context.deserialize(jsonObject.get("fields"), TelegramField[].class));
        final TypeName[] subTypes = jsonObject.has("subtypes") ? context.deserialize(jsonObject.get("subtypes"), TypeName[].class) : null;
        final TypeName[] subTypeOf = jsonObject.has("subtype_of") ? context.deserialize(jsonObject.get("subtype_of"), TypeName[].class) : null;

        boolean requiresBuilder = false;
        for (String s : needsBuilder) {
            if (name.contains(s)) {
                requiresBuilder = true;
                break;
            }
        }

        return new TelegramObject(name, href, description, fields, subTypes, subTypeOf, requiresBuilder);
    }


}
