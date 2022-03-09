package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.core.starter.config.BootstrapCoreProperties.PREFIX;

/**
 * Bootstrap core properties binder adapt.
 *
 * @author chen.ma
 * @date 2022/3/9 19:04
 */
public class BootstrapCorePropertiesBinderAdapt {

    /**
     * Bootstrap core properties binder.
     *
     * @param configInfo
     * @param bootstrapCoreProperties
     * @return
     */
    public static BootstrapCoreProperties bootstrapCorePropertiesBinder(Map<Object, Object> configInfo, BootstrapCoreProperties bootstrapCoreProperties) {
        BootstrapCoreProperties bindableCoreProperties = null;
        try {
            ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
            Binder binder = new Binder(sources);
            bindableCoreProperties = binder.bind(PREFIX, Bindable.ofInstance(bootstrapCoreProperties)).get();
        } catch (Exception ex) {
            try {
                Class.forName("org.springframework.boot.context.properties.bind.Binder");
            } catch (ClassNotFoundException notEx) {
                bindableCoreProperties = adapt(configInfo);
            }
        }

        return bindableCoreProperties;
    }

    /**
     * 此处采用硬编码适配低版本 SpringBoot 1.5.x, 如果有更好的方法进行逻辑转换的话, 欢迎 PR.
     *
     * @param configInfo
     * @return
     */
    private static BootstrapCoreProperties adapt(Map<Object, Object> configInfo) {
        BootstrapCoreProperties bindableCoreProperties;
        try {
            // filter
            Map<Object, Object> targetMap = Maps.newHashMap();
            configInfo.forEach((key, val) -> {
                if (key != null && StringUtil.isNotBlank((String) key) && ((String) key).indexOf(PREFIX + ".executors") != -1) {
                    String targetKey = key.toString().replace(PREFIX + ".", "");
                    targetMap.put(targetKey, val);
                }
            });

            // convert
            List<ExecutorProperties> executorPropertiesList = Lists.newArrayList();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                Map<String, Object> tarterSingleMap = Maps.newHashMap();

                for (Map.Entry entry : targetMap.entrySet()) {
                    String key = entry.getKey().toString();
                    if (key.indexOf(i + "") != -1) {
                        key = key.replace("executors[" + i + "].", "");

                        String[] keySplit = key.split("-");
                        if (keySplit != null && keySplit.length > 0) {
                            key = key.replace("-", "_");
                        }

                        tarterSingleMap.put(key, entry.getValue());
                    }
                }

                if (CollectionUtil.isEmpty(tarterSingleMap)) {
                    break;
                }

                ExecutorProperties executorProperties = BeanUtil.mapToBean(tarterSingleMap, ExecutorProperties.class, true, CopyOptions.create());
                if (executorProperties != null) {
                    executorPropertiesList.add(executorProperties);
                }
            }

            bindableCoreProperties = new BootstrapCoreProperties();
            bindableCoreProperties.setExecutors(executorPropertiesList);
        } catch (Exception ex) {
            throw ex;
        }

        return bindableCoreProperties;
    }

}
