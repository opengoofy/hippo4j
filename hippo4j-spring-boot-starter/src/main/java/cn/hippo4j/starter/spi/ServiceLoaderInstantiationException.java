package cn.hippo4j.starter.spi;

/**
 * Service loader instantiation exception.
 *
 * @author chen.ma
 * @date 2021/7/10 23:48
 */
public class ServiceLoaderInstantiationException extends RuntimeException {

    public ServiceLoaderInstantiationException(final Class<?> clazz, final Exception cause) {
        super(String.format("Can not find public default constructor for SPI class `%s`", clazz.getName()), cause);
    }

}

