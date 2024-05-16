package org.teleight.teleightbots.codegen.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.javapoet.TypeName;
import org.teleight.teleightbots.codegen.json.TelegramField;
import org.teleight.teleightbots.codegen.json.TelegramMethod;

import java.lang.reflect.Type;

import static org.teleight.teleightbots.codegen.json.utils.DescriptionExtrapolator.extrapolateDescriptionFromJson;
import static org.teleight.teleightbots.codegen.json.utils.TelegramFieldObjectReplacer.replaceTypes;

public class TelegramMethodDeserializer implements JsonDeserializer<TelegramMethod> {
    @Override
    public TelegramMethod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final String name = jsonObject.get("name").getAsString();
        final String href = jsonObject.get("href").getAsString();
        final String[] description = extrapolateDescriptionFromJson(jsonObject);
        TelegramField[] fields = replaceTypes(name, context.deserialize(jsonObject.get("fields"), TelegramField[].class));
        final TypeName[] returns = jsonObject.has("returns") ? context.deserialize(jsonObject.get("returns"), TypeName[].class) : null;
        final TypeName[] subTypes = jsonObject.has("subtypes") ? context.deserialize(jsonObject.get("subtypes"), TypeName[].class) : null;
        final TypeName[] subTypeOf = jsonObject.has("subtype_of") ? context.deserialize(jsonObject.get("subtype_of"), TypeName[].class) : null;

        return new TelegramMethod(name, href, description, returns, fields, subTypeOf, subTypes);
    }
}
