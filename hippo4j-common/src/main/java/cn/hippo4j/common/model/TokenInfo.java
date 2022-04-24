package cn.hippo4j.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token info.
 *
 * @author chen.ma
 * @date 2021/12/20 20:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * tokenTtl
     */
    private Long tokenTtl;

}
