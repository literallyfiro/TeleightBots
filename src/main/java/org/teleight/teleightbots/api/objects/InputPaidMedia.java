package org.teleight.teleightbots.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.teleight.teleightbots.api.ApiResult;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = Void.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InputPaidMediaPhoto.class, name = "photo"),
        @JsonSubTypes.Type(value = InputPaidMediaVideo.class, name = "video")
})
public sealed interface InputPaidMedia extends ApiResult permits
        InputPaidMediaPhoto,
        InputPaidMediaVideo {

    String TYPE_NAME = "type";

    @JsonProperty(TYPE_NAME)
    String type();

}