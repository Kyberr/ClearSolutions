package com.clearsolutions.service.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public abstract class MapperUtils {

  public static String[] definePropertiesWithNullValues(Object object) {
    Field[] fields = object.getClass().getDeclaredFields();
    List<String> fieldNames = new ArrayList<>();

    for (Field field : fields) {
      field.setAccessible(true);
      Object fieldValue = ReflectionUtils.getField(field, object);

      if (isNull(fieldValue)) {
        fieldNames.add(field.getName());
      }
    }
    return  fieldNames.toArray(new String[0]);
  }
}
