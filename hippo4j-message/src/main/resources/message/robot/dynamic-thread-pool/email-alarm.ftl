<span style="color: rgb(255, 0, 0);">[警报] </span>${active} - 动态线程池运行告警（${notifyTypeEnum}）
<ul>
    <li>线程池ID：<span style="color: rgb(160, 0, 0);">${threadPoolId}</span></li>
    <li>应用名称：<span style="color: rgb(160, 0, 0);">${appName}</span></li>
    <li>应用实例：${identify}</li>
    <li>核心线程数：${corePoolSize}</li>
    <li>最大线程数：${maximumPoolSize}</li>
    <li>当前线程数：${poolSize}</li>
    <li>活跃线程数：${activeCount}</li>
    <li>同存最大线程数：${largestPoolSize}</li>
    <li>线程池任务总量：${completedTaskCount}</li>
    <li>队列类型：${queueName}</li>
    <li>队列容量：${capacity}</li>
    <li>队列元素个数：${queueSize}</li>
    <li>队列剩余个数：${remainingCapacity}</li>
    <li>拒绝策略：${rejectedExecutionHandlerName}</li>
    <li>拒绝策略执行次数：<span style="color: #FF0000; ">${rejectCountNum}</li>
    <li>OWNER：${from}</li>
    <li>提示：${interval} 分钟内此线程池不会重复告警（可配置）</li>
</ul>

<b> 播报时间：${date} </b>

<style>
    li {
        list-style-type: none;
    }
</style>