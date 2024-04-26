package com.clearsolutions.mapper;

import com.clearsolutions.repository.entity.User;
import com.clearsolutions.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import static org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {

  User toEntity(UserDto userDto);

  UserDto toDto(User user);

  default User mergeWithDto(UserDto userDto, User user) {
    BeanUtils.copyProperties(this.toEntity(userDto), user);
    return user;
  }
}
