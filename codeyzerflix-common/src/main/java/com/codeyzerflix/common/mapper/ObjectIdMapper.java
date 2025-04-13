package com.codeyzerflix.common.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class ObjectIdMapper {

    public ObjectId mapStringToObjectId(String id) {
        if (id == null || !ObjectId.isValid(id)) return null;
        return new ObjectId(id);
    }

    public String mapObjectIdToString(ObjectId id) {
        return (id != null) ? id.toHexString() : null;
    }
}
