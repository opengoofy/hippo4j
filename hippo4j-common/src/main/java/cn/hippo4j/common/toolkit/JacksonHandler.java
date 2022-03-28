package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.api.JsonFacade;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Jackson util.
 *
 * @author chen.ma
 * @date 2021/12/13 20:02
 */
public class JacksonHandler implements JsonFacade {

    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        MAPPER.setDateFormat(new SimpleDateFormat(dateTimeFormat));
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    @SneakyThrows
    public String toJSONString(Object object) {
        return MAPPER.writeValueAsString(object);
    }

    @Override
    @SneakyThrows
    public <T> T parseObject(String text, Class<T> clazz) {
        JavaType javaType = MAPPER.getTypeFactory().constructType(clazz);
        return MAPPER.readValue(text, javaType);
    }

    @Override
    @SneakyThrows
    public <T> List<T> parseArray(String text, Class<T> clazz) {
        CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return MAPPER.readValue(text, collectionType);
    }

}
