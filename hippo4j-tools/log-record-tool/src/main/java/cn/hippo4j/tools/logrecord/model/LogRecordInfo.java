package cn.hippo4j.tools.logrecord.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 日志记录实体.
 *
 * @author chen.ma
 * @date 2021/10/24 17:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogRecordInfo {

    private Long id;

    private String tenant;

    @NotBlank(message = "bizKey required")
    @Length(max = 200, message = "appKey max length is 200")
    private String bizKey;

    @NotBlank(message = "bizNo required")
    @Length(max = 200, message = "bizNo max length is 200")
    private String bizNo;

    @NotBlank(message = "operator required")
    @Length(max = 63, message = "operator max length 63")
    private String operator;

    @NotBlank(message = "opAction required")
    @Length(max = 511, message = "operator max length 511")
    private String action;

    private String category;

    private Date createTime;

    private String detail;

}
