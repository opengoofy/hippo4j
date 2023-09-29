
<template>
  <div class="monitor-wrapper">
    <div class="info-wrapper"></div>
    <div class="search-wrapper"></div>
    <div class="center-chart-wrapper">

      <line-chart title="任务" :chart-data="lineChartData" :times="times" height="100%" :cus-formatter="true" />
    </div>
    <div class="bottom-chart-wraper">
      <div class="inner-chart">
        <line-chart title="活跃线程数" :chart-data="activeSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="活跃线程数" :chart-data="poolSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="活跃线程数" :chart-data="queueSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="活跃线程数" :chart-data="rangeRejectCountList" :times="times" height="100%" />
      </div>
    </div>
  </div>
</template>
<script>
import * as threadPoolApi from '@/api/hippo4j-threadPool';
import * as monitorApi from '@/api/hippo4j-monitor';
import LineChart from './components/LineChart.vue';
const params = {
  "current": 1,
  "size": 10,
  "itemId": "dynamic-threadpool-example",
  "tpId": "message-produce",
  "tenantId": "prescription",
  "identify": "127.0.0.1:8088_93bfe5307a844e07ada13cbfc3f16662",
  "instanceId": "127.0.0.1:8088_93bfe5307a844e07ada13cbfc3f16662"
}
export default {
  name: 'Monitor',
  components: {
    LineChart,
  },
  data() {
    return {
      lineChartData: [],
      times: [],
      activeSizeList: [],
      poolSizeList: [],
      queueSizeList: [],
      rangeRejectCountList: []
    }
  },

  created() {
    this.fetchData()
    this.fetchChartData()
  },
  methods: {
    fetchData() {
      threadPoolApi.info(params).then((res) => {
        this.temp = res;
      });
    },
    fetchChartData() {
      monitorApi.active(params).then(res => {
        this.lineChartData = [
          {
            name: '区间任务完成数',
            data: res?.rangeCompletedTaskCountList,
            show: true
          }, {
            name: '拒绝总数',
            data: res?.rejectCountList,
            show: false,
            itemStyle: {
              opacity: 0,
            },
            textStyle: {
              opacity: 0,
            }
          }, {
            name: '任务总数',
            data: res?.completedTaskCountList,
            show: false,
            itemStyle: {
              opacity: 0,
            },
            textStyle: {
              opacity: 0,
            }
          },]
        this.times = res?.times.map(item => {
          const list = item.split(':')
          list.pop()
          return list.join(':')
        })
        this.activeSizeList = [{
          name: '活跃线程数',
          data: res?.activeSizeList,
          show: true
        }]
        this.poolSizeList = [{
          name: '线程数',
          data: res?.poolSizeList,
          show: true
        }]
        this.queueSizeList = [{
          name: '队列元素数',
          data: res?.queueSizeList,
          show: true
        }]
        this.rangeRejectCountList = [{
          name: '拒绝数',
          data: res?.rangeRejectCountList,
          show: true
        }]
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.monitor-wrapper {
  width: 100%;
  height: calc(100vh - 50px);
  display: grid;
  grid-template-rows: 60px 1fr 2fr;
  grid-template-columns: 300px 2fr 1fr;
  background-color: #ebebf3;
  gap: 10px;
  padding: 10px;
  box-sizing: border-box;

  >div {
    border-radius: 8px;
    background-color: #fff;
    padding: 12px;
  }

  .chart-title {
    font-size: 16px;
    font-weight: bolder;
    margin: 0;
  }

  .info-wrapper {
    grid-row: 1 / 4;
    grid-column: 1 / 2;
  }

  .search-wrapper {
    grid-row: 1 / 2;
    grid-column: 2 / 4;
  }

  .center-chart-wrapper {
    grid-row: 2 / 3;
    grid-column: 2 / 4;
  }

  .bottom-chart-wraper {
    grid-row: 3 / 4;
    grid-column: 2 / 4;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    gap: 12px;

    .inner-chart {
      grid-column: span 1;
      grid-row: span 1;

    }
  }
}
</style>
