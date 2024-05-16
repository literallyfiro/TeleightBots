package org.teleight.teleightbots.codegen.json.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DescriptionExtrapolator {

    public static String[] extrapolateDescriptionFromJson(JsonObject jsonObject) {
        JsonElement descriptionElement = jsonObject.get("description");
        if (descriptionElement == null) {
            return new String[0];
        }
        JsonArray descriptionArray = descriptionElement.getAsJsonArray();
        String[] description = new String[descriptionArray.size()];
        for (int i = 0; i < descriptionArray.size(); i++) {
            description[i] = descriptionArray.get(i).getAsString();
        }
        return description;
    }

}
