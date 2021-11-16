package cn.hippo4j.tools.openchange;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * Change timed task.
 *
 * @author chen.ma
 * @date 2021/10/31 10:39
 */
@Slf4j
@RequiredArgsConstructor
public class OpenChangeTimedTask implements InitializingBean {

    @NonNull
    private final GitHubRemote gitHubRemote;

    @NonNull
    private final OpenChangeNotifyBootstrapProperties bootstrapProperties;

    private OpenChangeInfo lastChangeInfo;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        OpenChangeInfo itemInfo = null;
        try {
            itemInfo = gitHubRemote.getGitHubItemInfo();
            // 防止初始化调用 GitHub 403
            if (lastChangeInfo == null) {
                lastChangeInfo = itemInfo;
                log.info("Init... GitHub Item Info :: {}", JSONUtil.toJsonStr(lastChangeInfo));
                return;
            }
        } catch (Throwable ex) {
            log.error("Failed to call GitHub API interface.", ex);

            // 担心 GitHub 限制访问 403, 如异常则睡眠 5 分钟
            ThreadUtil.sleep(5, TimeUnit.MINUTES);
            return;
        }

        long addStarNum, addForkChange;
        boolean starChange = (addStarNum = (itemInfo.getStargazers_count() - lastChangeInfo.getStargazers_count())) > 0;
        if (starChange) {
            itemInfo.setStar_add(addStarNum);
        }

        boolean forkChange = (addForkChange = (itemInfo.getForks_count() - lastChangeInfo.getForks_count())) > 0;
        if (forkChange) {
            itemInfo.setFork_add(addForkChange);
        }

        if (starChange == false && forkChange == false) {
            log.info("Star and fork are unchanged.");
            return;
        }

        sendDingMessage(itemInfo);
        lastChangeInfo = itemInfo;
    }

    public void sendDingMessage(OpenChangeInfo itemInfo) {
        String gitHubItemUrl = itemInfo.getHtml_url();
        String markdownText = String.format(
                "<font color='#2a9d8f'>[通知] </font> GitHub Star Fork 变更通知 \n\n" +
                        " --- \n\n " +
                        "<font color='#708090' size=2>项目名称：HIPPO-JAVA</font> \n\n" +
                        "<font color='#708090' size=2>项目地址：[%s](%s)</font> \n\n" +
                        " --- \n\n " +
                        "<font color='#708090' size=2>Hippo Stars Add：</font><font color='#FF8C00' size=2>%s</font> \n\n " +
                        "<font color='#708090' size=2>Hippo Forks Add：</font><font color='#FF8C00' size=2>%s</font> \n\n " +
                        " --- \n\n  " +
                        "<font color='#708090' size=2>Hippo Now Star：[%d](" + gitHubItemUrl + "/stargazers)</font> \n\n" +
                        "<font color='#708090' size=2>Hippo Now Fork：[%d](" + gitHubItemUrl + "/members)</font> \n\n" +
                        "<font color='#708090' size=2>Hippo Open Issue：[%d](" + gitHubItemUrl + "/issues)</font> \n\n" +
                        "<font color='#708090' size=2>Hippo Subscribers Count：[%d](" + gitHubItemUrl + "/watchers)</font> \n\n" +
                        " --- \n\n  " +
                        "<font color='#708090' size=2>友情提示：5 分钟内 Star Fork 有变更则通知（可配置）</font> \n\n" +
                        "<font color='#708090' size=2>OWNER：Long-Tai</font> \n\n" +
                        " --- \n\n  " +
                        "**播报时间：%s**",

                itemInfo.getName().toUpperCase(),
                gitHubItemUrl,

                itemInfo.getStar_add() == null ? "-" : itemInfo.getStar_add().toString() + "+",
                itemInfo.getFork_add() == null ? "-" : itemInfo.getFork_add().toString() + "+",

                itemInfo.getStargazers_count(),
                itemInfo.getForks_count(),
                itemInfo.getOpen_issues_count(),
                itemInfo.getSubscribers_count(),

                DateUtil.now()
        );

        OpenChangeNotifyBootstrapProperties.NotifyConfig notifyConfig = bootstrapProperties.getNotifys().get(0);
        String serverUrl = notifyConfig.getUrl() + notifyConfig.getToken();
        DingTalkClient dingTalkClient = new DefaultDingTalkClient(serverUrl);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");

        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("GitHub Star Fork 变更通知");
        markdown.setText(markdownText);
        request.setMarkdown(markdown);

        try {
            dingTalkClient.execute(request);
        } catch (ApiException ex) {
            log.error("Ding failed to send message.", ex.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            lastChangeInfo = gitHubRemote.getGitHubItemInfo();
        } catch (Throwable ex) {
            log.error("Init error...");
        }

        log.info("Init success... GitHub Item Info :: {}", JSONUtil.toJsonStr(lastChangeInfo));
    }

}
