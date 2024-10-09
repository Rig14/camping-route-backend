package ee.taltech.iti03022024backend.mapping;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampingRouteMapper {
    CampingRouteDto toDto(CampingRouteEntity entity);

    @Mapping(target = "id", ignore = true)
    CampingRouteEntity toEntity(CampingRouteDto dto);

    List<CampingRouteDto> toDtoList(List<CampingRouteEntity> list);
}
