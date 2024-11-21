package ee.taltech.iti03022024backend.mapping;

import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(UserDto dto);
}
