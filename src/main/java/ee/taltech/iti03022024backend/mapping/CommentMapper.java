package ee.taltech.iti03022024backend.mapping;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toDto(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    CommentEntity toEntity(CommentDto dto);

    List<CommentDto> toDtoList(List<CommentEntity> list);
}
