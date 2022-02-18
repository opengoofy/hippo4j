package cn.hippo4j.starter.handler;

import cn.hippo4j.starter.config.BootstrapProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * Dynamic thread pool print banner.
 *
 * @author chen.ma
 * @date 2021/6/20 16:34
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolBannerHandler implements InitializingBean {

    @NonNull
    private final BootstrapProperties properties;

    private final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";

    private final String HIPPO4J_GITHUB = "GitHub:  https://github.com/acmenlt/dynamic-threadpool";

    private final String HIPPO4J_SITE = "Site:    https://hippox.cn";

    private final int STRAP_LINE_SIZE = 50;

    @Override
    public void afterPropertiesSet() {
        printBanner();
    }

    private void printBanner() {
        String banner = "___                       _      _____ ___ \n" +
                "|   \\ _  _ _ _  __ _ _ __ (_)__  |_   _| _ \\\n" +
                "| |) | || | ' \\/ _` | '  \\| / _|   | | |  _/\n" +
                "|___/ \\_, |_||_\\__,_|_|_|_|_\\__|   |_| |_|  \n" +
                "      |__/                                  \n";

        if (properties.getBanner()) {
            String version = getVersion();
            version = (version != null) ? " (v" + version + ")" : "no version.";

            StringBuilder padding = new StringBuilder();
            while (padding.length() < STRAP_LINE_SIZE - (version.length() + DYNAMIC_THREAD_POOL.length())) {
                padding.append(" ");
            }

            System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                    padding.toString(), AnsiStyle.FAINT, version, "\n\n", HIPPO4J_GITHUB, "\n", HIPPO4J_SITE, "\n"));

        }
    }

    public static String getVersion() {
        final Package pkg = DynamicThreadPoolBannerHandler.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : "";
    }

}
