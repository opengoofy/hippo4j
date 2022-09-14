package cn.hippo4j.config.service.biz;

public interface ConfigChangeVerifyService {

    /**
     * get type
     * @return
     */
    Integer type();

    /**
     * reject config change
     * @param id
     */
    void reject(String id);

    /**
     * accept config change
     * @param id
     */
    void accept(String id);
}
