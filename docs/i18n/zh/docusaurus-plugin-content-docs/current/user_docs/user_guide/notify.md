---
sidebar_position: 4
---

# 通知报警

现阶段已集成钉钉、企业微信、飞书的消息推送，后续会持续接入邮箱、短信和自定义通知渠道。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220904181527453.png)

**通知平台**

- DING：钉钉平台；

- LARK：飞书平台；

- WECHAT：企业微信。

**通知类型**

- CONFIG：线程池配置变更推送；

- ALARM：线程池运行报警推送。

**Token**

获取 DING、LARK、WECHAT 机器人 Token。

**报警间隔**

- CONFIG 类型通知没有报警间隔；

- ALARM 类型设置报警间隔后，某一节点下的同一线程池指定间隔只会发送一次报警通知。

**接收者**

```tex
多个接收者使用英文逗号 , 分割 (注意不要有空格)
DING：填写手机号
WECHART：填写user_id会以@的消息发给用户，填写姓名则是普通的@，如：龙台
LARK：填写ou_开头用户唯一标识会以@的消息发给用户，填写手机号则是普通的@
```


## 钉钉平台

[钉钉创建群机器人](https://www.dingtalk.com/qidian/help-detail-20781541.html)

| 配置变更 | 报警通知 |
| :---: |  :---: |
| ![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211013122816688.png) | ![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211013113649068.png) |

添加钉钉机器人后，需在机器人配置自定义关键字，才可发送成功。如下所示：

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220530200133377.png?x-oss-process=image/resize,h_500,w_800)

:::tip
如果使用 1.4.3 及以上版本，`警报` 替换为 `告警`。
:::

## 企业微信

[企业微信创建群机器人](https://open.work.weixin.qq.com/help2/pc/14931?person_id=1&from=homesearch)

| 配置变更 | 报警通知 |
| :---: |  :---: |
| ![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211203213443242.png) | ![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211203213512019.png) |

## 飞书平台

[飞书创建群机器人](https://www.feishu.cn/hc/zh-CN/articles/360024984973)

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220304081729347.png)

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220304081507907.png)
