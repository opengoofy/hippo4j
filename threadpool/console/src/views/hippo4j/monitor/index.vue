<template>
  <div class="monitor-wrapper">
    <div class="info-wrapper">
      <div class="top-card">
        <svg-icon id="top-icon" icon-class="monitor" />
        <div class="top-info">
          <div class="label">操作者：</div>
          <span class="user">{{ userName }}</span>
        </div>
      </div>
      <div class="query-monitoring">
        <div class="title">查询监控</div>
        <el-cascader :props="props" v-model="selectMonitorValue" class="el-cascader" @change="handleChangeMonitorSelect" placeholder="项目/线程池/实例"></el-cascader>
      </div>

      <div class="info-card">
        <div class="info-card-item">
          <div v-if="listQuery.itemId" class="info-card-show">{{ listQuery.itemId }}</div>
          <div class="data-num">项目：</div>
          <div v-if="listQuery.itemId" class="info-card-title">{{ listQuery.itemId }}</div>
        </div>
        <div class="info-card-item">
          <div v-if="listQuery.itemId" class="info-card-show">{{ listQuery.tpId }}</div>
          <div class="data-num">线程池：</div>
          <div v-if="listQuery.itemId" class="info-card-title">{{ listQuery.tpId }}</div>
        </div>
        <div class="info-card-item">
          <div v-if="listQuery.itemId" class="info-card-show">{{ listQuery.identify }}</div>
          <div class="data-num">实例：</div>
          <div v-if="listQuery.itemId" class="info-card-title">{{ listQuery.identify }}</div>
        </div>
      </div>

      <div class="tp-card">
        <div v-for="(item, index) in tableData" :key="index" class="tp-item">
          <div class="info-card-show">{{ item.label }}</div>
          <div>{{ item.name }}</div>
          <div class="tp-label">{{ item.label }}</div>
        </div>
      </div>
    </div>
    <div class="search-wrapper">
      <div class="demonstration">时间筛选</div>
      <el-date-picker v-model="timeValue" type="datetimerange" start-placeholder="开始日期" end-placeholder="结束日期"
        value-format="timestamp" :picker-options="pickerOptions">
      </el-date-picker>
      <el-button class="button" @click="changeMonitorData" v-loading.fullscreen.lock="fullscreenLoading">筛选</el-button>
    </div>
    <div class="center-chart-wrapper" v-if="listQuery.identify">
        <line-chart title="任务数" :chart-data="lineChartData" :times="times" height="100%" :cus-formatter="true" />
      </div>
      <div v-else class="center-chart-wrapper bottom-no-wraper">请选择实例～</div>
    <div class="bottom-chart-wraper" >
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
    let that = this;
    return {
      props: {
        lazy: true,
        lazyLoad (node, resolve) {
          const { level } = node;
          setTimeout(() => {
            if (level == 0) {
              that.userName = that.$cookie.get('userName')
              that.listQuery.tenantId = that?.tenantInfo?.tenantId || that.listQuery.tenantId
              let isAllTenant = that.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || that.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
              that.listQuery.tenantId = isAllTenant ? '' : that.listQuery.tenantId

              //请求项目-线程池-实例
              itemApi.list(that.listQuery).then((itemRes) => {
                let itemList = itemRes.records.map((item) => {
                  return {
                    ...item,
                    value: item.itemId,
                    label: item.itemId,
                    leaf: level >= 2
                  }
                })
                resolve(itemList)
              })
            } else if (level == 1) {
              that.listQuery.itemId = node.value
              threadPoolApi.list(that.listQuery).then((tpRes) => {
                let tpList = tpRes.records.map((tpItem) => {
                  return {
                    ...tpItem,
                    value: tpItem.tpId,
                    label: tpItem.tpId,
                    leaf: level >= 2
                  }
                })
                resolve(tpList)
              })
            } else if (level == 2) {
              that.listQuery.tpId = node.value
              let param = [
                that.listQuery.itemId,
                that.listQuery.tpId
              ]
              instanceApi.list(param).then((instanceRes) => {
                let itList = instanceRes.map((itItem) => {
                  return {
                    ...itItem,
                    value: itItem.identify,
                    label: itItem.identify,
                    leaf: level >= 2
                  }
                });
                resolve(itList ? itList : [{value: '暂无实例', label: '暂无实例'}])
              });
            }
          }, 1000);
        }
      },

      isFirstShow: true,
      lineChartData: [],
      times: [],
      selectMonitorValue: {
        default: [0]
      },
      activeSizeList: [],
      poolSizeList: [],
      queueSizeList: [],
      rangeRejectCountList: [],
      chooseData: {},
      timeValue: new Date(),
      fullscreenLoading: false,
      pickerOptions: {
        shortcuts: [{
          text: '30 分钟',
          onClick(picker) {
            const end = new Date();
            const start = that.getBeforeDate(new Date(), 0.5);
            that.timeValue = [start, end]
            picker.$emit("pick", [start, end]);
          }
        }, {
          text: '1 小时',
          onClick(picker) {
            const end = new Date();
            const start = that.getBeforeDate(new Date(), 1);
            picker.$emit("pick", [start, end]);
          }
        }, {
          text: '6 小时',
          onClick(picker) {
            const end = new Date();
            const start = that.getBeforeDate(new Date(), 6);
            picker.$emit("pick", [start, end]);
          }
        }, {
          text: '今天',
          onClick(picker) {
            const end = new Date();
            const start = that.getBeforeDate(new Date(), 24);
            picker.$emit("pick", [start, end]);
          }
        }, {
          text: '一周内',
          onClick(picker) {
            const end = new Date();
            const start = that.getBeforeDate(new Date(), 24 * 7);
            picker.$emit("pick", [start, end]);
          }
        }],
        onPick: ({ maxDate, minDate }) => {
          this.timeValue = [maxDate, minDate]
          this.choiceDate = minDate.getTime();//当选一个日期时 就是最小日期
          // // 如何你选择了两个日期了，就把那个变量置空
          if (maxDate) this.choiceDate = ''
        },
        disabledDate: time => {
          return time.getTime() > this.getDayStartOrEnd(new Date(), "end");
        }
      },
      itemList: [],
      selectMonitor: [],
      tableData: [],
      listQuery: {
        "current": 1,
        "size": 10,
        "itemId": "",
        "tpId": "",
        "tenantId": "",
        "identify": "",
        "instanceId": "",
        startTime:"",
        endTime:""
      },
      rejectCount: 0,
    }
  },
  computed: {
    ...mapGetters([
      'tenantInfo'
    ]),
  },
  watch: {
    listQuery:{
      handler(newVal) {
        if (!this.isFirstShow) {
          this.getSelectMonitor()
        }
      },
      deep: true
    },
    selectMonitorValue(newVal) {
      this.listQuery.identify = newVal[2]
      this.listQuery.instanceId = newVal[2]
       this.fetchChartData(this.listQuery)
    }

  },
  created() {
    const end = new Date().getTime();
    const start = this.getBeforeDate(new Date(), 0.5).getTime()
    this.timeValue = [start, end]
    this.listQuery.startTime = this.timeValue[0]
    this.listQuery.endTime = this.timeValue[1]
    this.fetchData()
    this.fetchChartData(this.listQuery)

  },
  methods: {
    fetchData() {
      // next
      this.userName = this.$cookie.get('userName')
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
    },


    //获取面板信息
    getSelectMonitor() {
      threadPoolApi.info(this.listQuery).then((res) => {
        this.temp = res;
        this.tableData = [{
          name: '线程池',
          label: res.tpId
        }, {
          name: '核心线程数',
          label: res.coreSize
        }, {
          name: '最大线程数',
          label: res.maxSize
        }, {
          name: '阻塞队列',
          label: this.queueFilter(res.queueType)
        }, {
          name: '队列容量',
          label: res.capacity
        }, {
          name: '任务完成数',
          label: this.completedTaskCount
        }, {
          name: '拒绝策略',
          label: this.rejectedTypeFilter(res.rejectedType)
        }, {
          name: '拒绝次数',
          label: this.rejectCount
        }]
      });
    },

    //监控数据
    fetchChartData(value) {
      let query = value
      monitorApi.active({ ...query }).then(res => {
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
        this.rejectCount = res?.rejectCountList[res.rejectCountList.length - 1]
        this.completedTaskCount = res?.completedTaskCountList[res.completedTaskCountList.length - 1]
        if (this.tableData.length > 1) {
          this.tableData[7].label = res?.rejectCountList[res.rejectCountList.length - 1]
          this.tableData[5].label = res?.completedTaskCountList[res.completedTaskCountList.length - 1]

        }
        this.times = res?.times.map(item => {
          const list = item.split(':')
          list.pop()
          return list.join(':')
        })
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
        this.fullscreenLoading = false
      })
    },

    handleChangeMonitorSelect(value) {
      this.isFirstShow = false
      this.selectMonitor[value[0]].map((item) => {
        if (item.children.label == value[1]) {
          this.listQuery.identify = item.identify || this.listQuery.identify
          this.listQuery.tenantId = item.tenantId || this.listQuery.tenantId
          this.listQuery.itemId = item.itemId || this.listQuery.itemId
          this.listQuery.instanceId = item.instanceId || this.listQuery.instanceId
          this.listQuery.tpId = item.tpId || this.listQuery.tpId
        }
      })
    },

    changeMonitorData() {
      this.fullscreenLoading = true
      this.listQuery.startTime = this.timeValue[0]
      this.listQuery.endTime = this.timeValue[1]
      this.fetchChartData(this.listQuery)
    },

    getBeforeDate(date = new Date(), days = 7) {
      date.setTime(date.getTime() - 3600 * 1000 * days);
      return date;
    },

    getDayStartOrEnd(time, type = "start") {
      if (type == "start") {
        return new Date(time).setHours(0, 0, 0, 0);
      } else {
        return new Date(time).setHours(23, 59, 59, 999);
      }
    },


    rejectedTypeFilter(type) {
      if ('1' == type) {
        return 'CallerRunsPolicy';
      } else if ('2' == type) {
        return 'AbortPolicy';
      } else if ('3' == type) {
        return 'DiscardPolicy';
      } else if ('4' == type) {
        return 'DiscardOldestPolicy';
      } else if ('5' == type) {
        return 'RunsOldestTaskPolicy';
      } else if ('6' == type) {
        return 'SyncPutQueuePolicy';
      } else {
        return 'CustomRejectedPolicy_' + type;
      }
    },
    queueFilter(type) {
      if ('1' == type) {
        return 'ArrayBlockingQueue';
      } else if ('2' == type) {
        return 'LinkedBlockingQueue';
      } else if ('3' == type) {
        return 'LinkedBlockingDeque';
      } else if ('4' == type) {
        return 'SynchronousQueue';
      } else if ('5' == type) {
        return 'LinkedTransferQueue';
      } else if ('6' == type) {
        return 'PriorityBlockingQueue';
      } else if ('9' == type) {
        return 'ResizableLinkedBlockingQueue';
      }
    },
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
    display: none;
    /*隐藏滚动条*/
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
      font-family: HelveticaNeue-Medium, Helvetica Medium, PingFangSC-Medium, STHeitiSC-Medium, Microsoft YaHei Bold, Arial, sans-serif;

      .info-card-title, .data-num {
        font-size: 14px;
        font-weight: 500;
        padding-bottom: 10px;
      }
      .info-card-show {
        display: none;
      }
      .info-card-item:hover .info-card-show {
        display: block;
        position: absolute;
        left: 30px;
        background: #313131;
        border-radius: 4px;
        padding: 5px 8px 6px 8px;
        box-shadow: 1px 1px 5px red($color: #efecec);
        z-index: 9999;
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
        flex-wrap: wrap;
        overflow: hidden;

        .tp-label {
          width: 600;
        }
      }
      .info-card-show {
        display: none;
      }
      .tp-item:hover .info-card-show {
        display: block;
        position: absolute;
        background: #313131;
        border-radius: 4px;
        padding: 5px 8px 6px 8px;
        box-shadow: 1px 1px 5px red($color: #efecec);
        z-index: 9999;
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

    .button {
      background: rgba($color: #435f81, $alpha: 0.9);
      color: white;
      margin-left: 20px;
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

  .bottom-no-wraper {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 600;
    color: #818689;
    font-family: 'Courier New', Courier, monospace;
  }
}
</style>
