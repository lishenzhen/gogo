package zenuo.gogo.util;

import lombok.extern.slf4j.Slf4j;
import zenuo.gogo.core.config.Constants;

import java.io.IOException;

/**
 * JSON工具类
 */
@Slf4j
public final class JsonUtils {
    private JsonUtils() {

    }

    public static byte[] toJsonBytes(final Object object) {
        try {
            return Constants.MAPPER.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("toJson", e);
            throw new IllegalStateException(e);
        }
    }

    public static <T> T fromJsonBytes(byte[] json, Class<? extends T> clazz) {
        try {
            return Constants.MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("fromJson", e);
            throw new IllegalStateException(e);
        }
    }
}
