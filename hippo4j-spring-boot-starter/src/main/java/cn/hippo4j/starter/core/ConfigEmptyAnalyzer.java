package cn.hippo4j.starter.core;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Config empty analyzer.
 *
 * @author chen.ma
 * @date 2021/11/28 21:59
 */
public class ConfigEmptyAnalyzer extends AbstractFailureAnalyzer<ConfigEmptyException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, ConfigEmptyException cause) {
        return new FailureAnalysis(cause.getDescription(), cause.getAction(), cause);
    }

}
