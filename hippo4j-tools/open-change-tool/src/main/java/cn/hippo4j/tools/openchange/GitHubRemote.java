package cn.hippo4j.tools.openchange;

import com.dtflys.forest.annotation.Get;

/**
 * GitHub remote.
 *
 * @author chen.ma
 * @date 2021/10/31 10:45
 */
public interface GitHubRemote {

    /**
     * 获取 GitHub 仓库详细信息.
     *
     * @return
     */
    @Get("https://api.github.com/repos/acmenlt/dynamic-threadpool")
    OpenChangeInfo getGitHubItemInfo();

}
