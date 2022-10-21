package cn.hippo4j.core.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Plug in runtime information.
 *
 * @author huangchengxing
 */
@RequiredArgsConstructor
@Getter
public class PluginRuntime {

    /**
     * plugin id
     */
    private final String pluginId;

    /**
     * runtime info items
     */
    private final List<Item> items = new ArrayList<>();

    /**
     * Add a runtime info item.
     *
     * @param name name
     * @param value value
     * @return runtime info item
     */
    public PluginRuntime addItem(String name, Object value) {
        items.add(new Item(name, value));
        return this;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Item {
        private final String name;
        private final Object value;
    }

}
