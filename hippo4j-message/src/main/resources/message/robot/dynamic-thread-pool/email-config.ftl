<style>
    li{list-style-type:none;}
</style>
<span style="color: rgb(0, 240, 0); ">[通知] </span>${active} - 动态线程池参数变更
<ul>
    <li>线程池ID：<span style="color: rgb(160, 0, 0);">${threadPoolId}</span></li>
    <li>应用名称：<span style="color: rgb(160, 0, 0);">${appName}</span></li>
    <li>应用实例：${identify}</li>
    <li>核心线程数：${beforeCorePoolSize} -> ${nowCorePoolSize}</li>
    <li>核心线程超时：${beforeMaximumPoolSize} -> ${nowMaximumPoolSize}</li>
    <li>线程存活时间：${beforeAllowsCoreThreadTimeOut} -> ${nowAllowsCoreThreadTimeOut}</li>
    <li>执行超时时间：${beforeKeepAliveTime} -> ${nowKeepAliveTime}</li>
    <li>队列类型：${blockingQueueName}</li>
    <li>队列容量：${beforeQueueCapacity} -> ${nowQueueCapacity}</li>
    <li>AGO 拒绝策略：${beforeRejectedName}</li>
    <li>NOW 拒绝策略：${nowRejectedName}</li>
    <li>OWNER： ${from}</li>
    <li>提示：动态线程池配置变更实时通知（无限制）</li>
</ul>

<b> 播报时间：${date} </b>
