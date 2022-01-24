package cn.hippo4j.tools.logrecord.parse;

import cn.hippo4j.tools.logrecord.service.FunctionService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Log record value parser.
 *
 * @author chen.ma
 * @date 2021/10/24 21:27
 */
public class LogRecordValueParser implements BeanFactoryAware {

    @Autowired
    private FunctionService functionService;

    protected BeanFactory beanFactory;

    private final LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();

    private static final Pattern PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    public Map<String, String> processTemplate(Collection<String> templates, Object ret,
                                               Class<?> targetClass, Method method, Object[] args, String errorMsg,
                                               Map<String, String> beforeFunctionNameAndReturnMap) {
        Map<String, String> expressionValues = Maps.newHashMap();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, ret, errorMsg, beanFactory);

        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("{")) {
                Matcher matcher = PATTERN.matcher(expressionTemplate);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    String functionReturnValue = getFunctionReturnValue(beforeFunctionNameAndReturnMap, value, matcher.group(1));
                    matcher.appendReplacement(parsedStr, Strings.nullToEmpty(functionReturnValue));
                }
                matcher.appendTail(parsedStr);
                expressionValues.put(expressionTemplate, parsedStr.toString());
            } else {
                expressionValues.put(expressionTemplate, expressionTemplate);
            }
        }

        return expressionValues;
    }

    public Map<String, String> processBeforeExecuteFunctionTemplate(Collection<String> templates, Class<?> targetClass, Method method, Object[] args) {
        Map<String, String> functionNameAndReturnValueMap = new HashMap(16);
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);

        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("{")) {
                Matcher matcher = PATTERN.matcher(expressionTemplate);
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    if (expression.contains("#_ret") || expression.contains("#_errorMsg")) {
                        continue;
                    }
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String functionName = matcher.group(1);
                    if (functionService.beforeFunction(functionName)) {
                        String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                        String functionReturnValue = getFunctionReturnValue(null, value, functionName);
                        functionNameAndReturnValueMap.put(functionName, functionReturnValue);
                    }
                }
            }
        }

        return functionNameAndReturnValueMap;
    }

    private String getFunctionReturnValue(Map<String, String> beforeFunctionNameAndReturnMap, String value, String functionName) {
        String functionReturnValue = "";
        if (beforeFunctionNameAndReturnMap != null) {
            functionReturnValue = beforeFunctionNameAndReturnMap.get(functionName);
        }
        if (StringUtils.isEmpty(functionReturnValue)) {
            functionReturnValue = functionService.apply(functionName, value);
        }
        return functionReturnValue;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
