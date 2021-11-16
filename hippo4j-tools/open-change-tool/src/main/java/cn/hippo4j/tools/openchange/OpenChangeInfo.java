package cn.hippo4j.tools.openchange;

import lombok.Data;

/**
 * Open change info.
 *
 * @author chen.ma
 * @date 2021/10/31 10:33
 */
@Data
public class OpenChangeInfo {

    /**
     * name
     */
    private String name;

    /**
     * stars
     */
    private Long stargazers_count;

    /**
     * forks
     */
    private Long forks_count;

    /**
     * open issues
     */
    private Long open_issues_count;

    /**
     * url
     */
    private String html_url;

    /**
     * subscribers
     */
    private Long subscribers_count;

    /**
     * default branch
     */
    private String default_branch;

    /**
     * star_add
     */
    private Long star_add;

    /**
     * fork_add
     */

    private Long fork_add;

}
