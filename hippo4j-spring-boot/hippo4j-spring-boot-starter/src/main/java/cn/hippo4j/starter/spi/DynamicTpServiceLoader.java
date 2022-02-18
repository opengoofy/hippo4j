package cn.hippo4j.starter.spi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Dynamic threadPool service loader.
 *
 * @author chen.ma
 * @date 2021/7/10 23:45
 */
public class DynamicTpServiceLoader {

    private static final Map<Class<?>, Collection<Object>> SERVICES = new ConcurrentHashMap();

    public static void register(final Class<?> serviceInterface) {
        if (!SERVICES.containsKey(serviceInterface)) {
            SERVICES.put(serviceInterface, load(serviceInterface));
        }
    }

    private static <T> Collection<Object> load(final Class<T> serviceInterface) {
        Collection<Object> result = new LinkedList<>();
        for (T each : ServiceLoader.load(serviceInterface)) {
            result.add(each);
        }
        return result;
    }

    public static <T> Collection<T> getSingletonServiceInstances(final Class<T> service) {
        return (Collection<T>) SERVICES.getOrDefault(service, Collections.emptyList());
    }

    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        return SERVICES.containsKey(service) ? SERVICES.get(service).stream().map(each -> (T) newServiceInstance(each.getClass())).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static Object newServiceInstance(final Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (final InstantiationException | IllegalAccessException ex) {
            throw new ServiceLoaderInstantiationException(clazz, ex);
        }
    }

}
