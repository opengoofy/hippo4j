package io.dynamic.threadpool.starter.banner;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * 动态线程池 Banner
 *
 * @author chen.ma
 * @date 2021/7/4 15:58
 */
public class DynamicThreadPoolBanner {

    private static final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";

    private static final int STRAP_LINE_SIZE = 50;

    public static void printBanner(boolean isBanner) {
        String banner = "\n___                       _      _____ ___ \n" +
                "|   \\ _  _ _ _  __ _ _ __ (_)__  |_   _| _ \\\n" +
                "| |) | || | ' \\/ _` | '  \\| / _|   | | |  _/\n" +
                "|___/ \\_, |_||_\\__,_|_|_|_|_\\__|   |_| |_|  \n" +
                "      |__/                                  \n";

        if (isBanner) {
            String version = getVersion();
            version = (version != null) ? " (v" + version + ")" : "no version.";

            StringBuilder padding = new StringBuilder();
            while (padding.length() < STRAP_LINE_SIZE - (version.length() + DYNAMIC_THREAD_POOL.length())) {
                padding.append(" ");
            }

            System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                    padding.toString(), AnsiStyle.FAINT, version, "\n"));
        }
    }

    public static String getVersion() {
        final Package pkg = DynamicThreadPoolBanner.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : "";
    }

}
