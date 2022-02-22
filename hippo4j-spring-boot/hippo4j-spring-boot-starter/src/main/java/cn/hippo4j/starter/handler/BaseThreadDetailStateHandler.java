package cn.hippo4j.starter.handler;

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.model.ThreadDetailStateInfo;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Base thread detail state handler.
 *
 * <p> The Java 8 implementation is temporarily provided,
 * {@link ThreadDetailState} interface can be customized.
 *
 * @author chen.ma
 * @date 2022/1/9 13:01
 */
@Slf4j
public class BaseThreadDetailStateHandler implements ThreadDetailState {

    private final String WORKERS = "workers";

    private final String THREAD = "thread";

    @Override
    public List<ThreadDetailStateInfo> getThreadDetailStateInfo(String threadPoolId) {
        DynamicThreadPoolWrapper poolWrapper = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor executor = poolWrapper.getExecutor();
        return getThreadDetailStateInfo(executor);
    }

    @Override
    public List<ThreadDetailStateInfo> getThreadDetailStateInfo(ThreadPoolExecutor threadPoolExecutor) {
        List<ThreadDetailStateInfo> resultThreadState = new ArrayList();
        try {
            // TODO: Should the object be copied deeply to avoid the destruction of the worker
            HashSet<Object> workers = (HashSet<Object>) ReflectUtil.getFieldValue(threadPoolExecutor, WORKERS);
            if (CollectionUtil.isEmpty(workers)) {
                return resultThreadState;
            }

            for (Object worker : workers) {
                Thread thread;
                try {
                    thread = (Thread) ReflectUtil.getFieldValue(worker, THREAD);
                    if (thread == null) {
                        log.warn("Reflection get worker thread is null. Worker :: {}", worker);
                        continue;
                    }
                } catch (Exception ex) {
                    log.error("Reflection get worker thread exception. Worker :: {}", worker, ex);
                    continue;
                }

                long threadId = thread.getId();
                String threadName = thread.getName();
                String threadStatus = thread.getState().name();
                StackTraceElement[] stackTrace = thread.getStackTrace();
                List<String> stacks = new ArrayList(stackTrace.length);
                for (int i = 0; i < stackTrace.length; i++) {
                    stacks.add(stackTrace[i].toString());
                }
                ThreadDetailStateInfo threadState = new ThreadDetailStateInfo();
                threadState.setThreadId(threadId)
                        .setThreadName(threadName)
                        .setThreadStatus(threadStatus)
                        .setThreadStack(stacks);
                resultThreadState.add(threadState);
            }
        } catch (Exception ex) {
            log.error("Failed to get thread status.", ex);
        }

        return resultThreadState;
    }

}
