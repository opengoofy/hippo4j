package cn.hippo4j.config.springboot.starter.refresher;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import com.tencent.polaris.configuration.api.core.ConfigKVFile;
import com.tencent.polaris.configuration.api.core.ConfigKVFileChangeListener;
import com.tencent.polaris.configuration.api.core.ConfigPropertyChangeInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

/**
 *@author : wh
 *@date : 2022/10/1 15:24
 *@description:
 */
@RequiredArgsConstructor
public class PolarisRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

	private final ConfigFileService configFileService;

	private static final String POLARIS_NAMESPACE = "${spring.dynamic.thread-pool.polaris.namespace:dev}";

	private static final String POLARIS_FILE_GROUP = "${spring.dynamic.thread-pool.polaris.file.group:dynamic}";

	private static final String POLARIS_FILE_NAME = "${spring.dynamic.thread-pool.polaris.file.name:root/bootstrap.yaml}";

	private static final String POLARIS_FILE_TYPE = "${spring.dynamic.thread-pool.polaris.file.type:properties}";

	@Value(POLARIS_NAMESPACE)
	private String namespace;

	@Value(POLARIS_FILE_GROUP)
	private String fileGroup;

	@Value(POLARIS_FILE_NAME)
	private String fileName;


	@Override
	public String getProperties() {
		ConfigKVFile configFile = getConfigKVFile();
		configFile.getContent();
		return configFile.getContent();
	}

	@Override
	public void afterPropertiesSet() {
		ConfigKVFile configFile = getConfigKVFile();
		configFile.addChangeListener((ConfigKVFileChangeListener) event -> {
			String content = configFile.getContent();
			Map<String, Object> newChangeValueMap = Maps.newHashMap();
			for (String key : event.changedKeys()) {
				ConfigPropertyChangeInfo changeInfo = event.getChangeInfo(key);
				newChangeValueMap.put(key, changeInfo.getNewValue());
			}
			dynamicRefresh(content, newChangeValueMap);
		});
	}

	private ConfigKVFile getConfigKVFile() {
		return Objects.equals(POLARIS_FILE_TYPE, "yaml") ? configFileService.getConfigYamlFile(namespace, fileGroup, fileName) : configFileService.getConfigPropertiesFile(namespace, fileGroup, fileName);
	}

}
