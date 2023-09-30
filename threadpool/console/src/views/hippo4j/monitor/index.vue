<template>
  <div class="monitor-wrapper">
    <div class="info-wrapper">
      <div class="top-card">
        <svg-icon id="top-icon" icon-class="monitor" />
        <div class="top-info">
          <div class="label">操作者：</div>
          <span class="user">{{userName}}</span>
        </div>
      </div>
      <div class="query-monitoring">
        <div class="title">查询监控</div>
        <el-cascader
          class="el-cascader"
          learable
          v-model="selectMonitorValue"
          :options="selectMonitor"
          :props="{ expandTrigger: 'hover' }"
          @change="handleChangeMonitorSelect"></el-cascader>
      </div>
      <div class="info-card">
        <div  class="info-card-title">{{ selectMonitor[0].label }}数量</div>
        <div class="data-num">
          <span class="num">
            {{ selectMonitor[0].children.length || "_" }}
          </span>个
        </div>
      </div>
      <div class="tp-card">
        <div class="tp-item" v-for="(item, index) in tableData" :key="index">
          <div>{{ item.name }}</div>
          <div class="tp-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
    <div class="search-wrapper">
        <div class="demonstration">时间筛选</div>
        <el-date-picker
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          v-model="timeValue"
          @input="changeMonitorData">
        </el-date-picker>
    </div>
    <div class="center-chart-wrapper">
      <line-chart title="任务数" :chart-data="lineChartData" :times="times" height="100%" :cus-formatter="true" />
    </div>
    <div class="bottom-chart-wraper">
      <div class="inner-chart">
        <line-chart title="活跃线程数" :chart-data="activeSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="线程数" :chart-data="poolSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="队列元素数" :chart-data="queueSizeList" :times="times" height="100%" />
      </div>
      <div class="inner-chart">
        <line-chart title="拒绝数" :chart-data="rangeRejectCountList" :times="times" height="100%" />
      </div>
    </div>
  </div>
</template>
<script>
import * as threadPoolApi from '@/api/hippo4j-threadPool';
import * as monitorApi from '@/api/hippo4j-monitor';
import * as instanceApi from '@/api/hippo4j-instance';
import * as itemApi from '@/api/hippo4j-item';
import { mapGetters } from 'vuex';
import { i18nConfig } from '@/locale/config'
import LineChart from './components/LineChart.vue';

export default {
  name: 'Monitor',
  components: {
    LineChart,
  },
  data() {
    return {
      lineChartData: [],
      times: [],
      timeValue: new Date(),
      selectMonitorValue: {
        default: [0]
      },
      selectMonitor: [{
        value: 0,
        label: '项目',
        children: []
      }, {
        value: 1,
        label: '线程池',
        children: [],
      }, {
        value: 2,
        label: '实例',
        children: [],
      }],
      activeSizeList: [],
      poolSizeList: [],
      queueSizeList: [],
      rangeRejectCountList: [],
      chooseData: {},
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date());
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24);
            picker.$emit('pick', date);
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date();
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', date);
          }
        }]
      },
      selectMonitor: [],
      tableData: [{
        date: '2016-05-02',
        name: '王小虎',
      }, {
        date: '2016-05-04',
        name: '王小虎',
      }, {
        date: '2016-05-01',
        name: '王小虎',
      }, {
        date: '2016-05-03',
        name: '王小虎',
      }],
      listQuery : {
        "current": 1,
        "size": 10,
        "itemId": "dynamic-threadpool-example",
        "tpId": "message-produce",
        "tenantId": "prescription",
        "identify": "127.0.0.1:8088_93bfe5307a844e07ada13cbfc3f16662",
        "instanceId": "127.0.0.1:8088_93bfe5307a844e07ada13cbfc3f16662"
      }
    }
  },
  computed: {
    ...mapGetters([
      'tenantInfo'
    ]),
  },
  watch: {
    tenantInfo(newVal, oldVal) {
      this.listQuery.tenantId = newVal.tenantId;
      this.fetchData()
    }
  },
  created() {
    this.fetchData()
    this.fetchChartData()
    this.getSelectMonitor()
  },
  methods: {
    fetchData() {
      this.userName = this.$cookie.get('userName')
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
      threadPoolApi.info( this.listQuery).then((res) => {
        this.temp = res;
      });
    },

    getSelectMonitor() {
      this.selectMonitor = [{
        value: 0,
        label: '项目',
        children: []
      }, {
        value: 1,
        label: '线程池',
        children: [],
      }, {
        value: 2,
        label: '实例',
        children: [],
      }]
      //项目接口 itenId
      itemApi.list(this.listQuery).then((res) => {
        const { records } = res || {};
        records && records.map((item) => {
          item.value = item.tenantId
          item.label = item.tenantId
        });
        this.selectMonitor[0].children = records
        //初始化tenantId
        this.selectMonitorValue = [0, records[0].tenantId ]
        this.listQuery.tenantId = records[0].tenantId
        this.listQuery.itemId = records[0].itemId
        this.listQuery.current = records[0].current
      });
      //线程池接口tpId,itemId,tenantId
      threadPoolApi.list( this.listQuery).then((res) => {
        const { records } = res || {};
        records && records.map((item) => {
          item.value = item.tenantId
          item.label = item.tenantId
        });
        this.selectMonitor[1].children = records
        this.listQuery.tenantId = records[0].tenantId
        this.listQuery.itemId = records[0].itemId
        this.listQuery.current = records[0].current
        this.listQuery.tpId = records[0].tpId
      });
      let param = [
        this.listQuery.itemId,
        this.listQuery.tpId,
      ]
      //实例接口
      instanceApi.list(param).then((res) => {
        res.map((item) => {
          item.value = item.tenantId
          item.label = item.tenantId
        });
        this.selectMonitor[2].children = res
        this.listQuery.tenantId = res[0].tenantId
        this.listQuery.itemId = res[0].itemId
        this.listQuery.current = res[0].current
        this.listQuery.tpId = res[0].tpId,
        this.listQuery.identify = res[0].identify
      });
      
      //线程池配置
      threadPoolApi.info(this.listQuery).then((res) => {
        this.temp = res;
        this.tableData = [{
          name: '租户',
          label: res.tenantId
        }, {
          name: '线程池',
          label: res.tpId
        }, {
          name: '核心线程池',
          label: res.coreSize
        }, {
        //   name: '项目',
        //   label: res.itemId
        // }, {
          name: '拒绝次数',
          label: res.rejectedType
        }]
      });
    },

    fetchChartData() {
      monitorApi.active( this.listQuery).then(res => {
        this.monitor = res
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
        console.log
        this.activeSizeList = [{
          name: '活跃线程数',
          data: res?.activeSizeList,
          show: true,
          num: res?.activeSizeList[res.activeSizeList.length - 1]
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
    },

    changeMonitorData(value, row) {
      console.log("time", value)
      this.listQuery.startTime = value[0]
      this.listQuery.endTime = value[1]
      this.fetchChartData()
    },

    handleChangeMonitorSelect(value) {
      console.log("asaksasa", value)
      this.selectMonitor[value[0]].map((item) => {
        if (item.children.label == value[1]) {
          this.listQuery.identify = item.identify || this.listQuery.identify
          this.listQuery.tenantId = item.tenantId || this.listQuery.tenantId
          this.listQuery.itemId = item.itemId || this.listQuery.itemId
          this.listQuery.instanceId = item.current || this.listQuery.instanceId
          this.listQuery.tpId = item.tpId || this.listQuery.tpId
        }
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
  grid-template-columns: 240px 2fr 1fr;
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
  .info-wrapper::-webkit-scrollbar {
    display: none; /*隐藏滚动条*/
  }
  .info-wrapper {
    grid-row: 1 / 4;
    grid-column: 1 / 2;
    padding: 20rpx;
    overflow-y: scroll;
    .top-card {
      display: flex;
      justify-content: space-around;
      padding: 20px 0;
      align-items: center;
      color: rgba($color: #435f81, $alpha: 0.9);
      font-weight: 600;
      .svg-icon {
        width: 60px;
        height: 60px;
        left: 0;
      }
      .top-icon {
        width: 40px;
      }
    }
    .query-monitoring {
      margin-bottom: 30px;
      .title {
        padding: 0 0 10px 5px;
        font-weight: 600;
        color: rgba($color: #435f81, $alpha: 0.9);
      }
      .el-cascader {
        width: 100%;
      }
    }
    .info-card {
      // background: #435f81;
      background-color: rgba($color: #435f81, $alpha: 0.9);
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 20px;
      color: #fefefe;
      font-family: HelveticaNeue-Medium,Helvetica Medium,PingFangSC-Medium,STHeitiSC-Medium,Microsoft YaHei Bold,Arial,sans-serif;
      .info-card-title, .data-num  {
        font-size: 14px;
        font-weight: 500;
        padding-bottom: 10px;
      }
      .num {
        font-family: DIDIFD-Regular;
        font-size: 40px;
      }
      .operation-list {
        color: darkgray;
      }
    }
    .tp-card {
      // background: rgb(237, 237, 237);
        border: 1px solid #dfe6ec;
        padding: 20px 10px 30px 10px;
        border-radius: 8px;
      background-color: rgba($color: #435f81, $alpha: 0.9);
      color: white;
      .tp-item {
        border-bottom: 1px solid #e3e3e3;
        line-height: 40px;
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        flex-wrap: nowrap;
        overflow: hidden;
        .tp-label {
          width: 600;
        }
      }
    }
  }

  .search-wrapper {
    grid-row: 1 / 2;
    grid-column: 2 / 4;
    display: flex;
    align-items: center;
    .demonstration {
      margin: 0 10px;
      font-weight: 600;
      color: #3a3b3c;
    }
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
    gap: 24px;
    .inner-chart {
      grid-column: span 1;
      grid-row: span 1;

    }
  }
}
</style>
