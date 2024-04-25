package com.clearsolutions.mapper;

import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {

  User toEntity(UserDto userDto);

  UserDto toDto(User user);
}
