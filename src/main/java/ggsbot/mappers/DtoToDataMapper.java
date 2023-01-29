package ggsbot.mappers;

import ggsbot.dto.Feature;
import ggsbot.model.data.Point;
import ggsbot.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring")
public interface DtoToDataMapper {

    @Mapping(target = "centerType", source = "center")
    @Mapping(target = "pointClass", source = "clazz", qualifiedByName = "addDescription")
    @Mapping(target = "zone", source = "lon", qualifiedByName = "findZone")
    @Mapping(target = "name", source = ".", qualifiedByName = "addName")
    @Mapping(target = "description", source = "location")
    Point dtoToPoint(Feature.Attributes attr);

    @Named("findZone")
    default int findZone(double lon) {
        return Utils.findZone(lon);
    }

    @Named("addName")
    default String addName(Feature.Attributes attr) {
        return attr.getName() == null ? attr.getObjectId() : attr.getName();
    }

    @Named("addDescription")
    default String addDescription(String clazz) {
        return clazz + " ГВО";
    }
}
