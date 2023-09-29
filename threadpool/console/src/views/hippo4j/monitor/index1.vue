<template>
  <div class="dashboard-editor-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.tenantId"
        :placeholder="$t('tenantManage.tenantRequired')"
        style="width: 220px"
        filterable
        class="filter-item"
        @change="tenantSelectList()"
      >
        <el-option
          v-for="item in tenantOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.itemId"
        :placeholder="$t('projectManage.itemRequired')"
        style="width: 220px"
        filterable
        class="filter-item"
        @change="itemSelectList()"
      >
        <el-option
          v-for="item in itemOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.tpId"
        :placeholder="$t('threadPool.threadPoolRequired')"
        style="width: 220px"
        filterable
        class="filter-item"
        @change="threadPoolSelectList()"
      >
        <el-option
          v-for="item in threadPoolOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.identify"
        :placeholder="$t('threadPoolMonitor.ipPortRequired')"
        style="width: 220px"
        filterable
        class="filter-item"
      >
        <el-option
          v-for="item in identifyOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        style="margin-left: 10px"
        icon="el-icon-search"
        @click="fetchData"
      >
        {{ $t('common.query') }}
      </el-button>
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        style="margin-left: 10px"
        icon="el-icon-refresh"
        @click="refreshData"
      >
        {{ $t('common.reset') }}
      </el-button>
    </div>
    <el-empty v-if="!temp.coreSize" :description="$t('threadPoolMonitor.noResultsYet')" />
    <section v-else>
      <!-- <el-card shadow="hover">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="实例 ID">{{ listQuery.identify }}</el-descriptions-item>

          <el-descriptions-item label="核心线程">{{ temp.coreSize }}</el-descriptions-item>
          <el-descriptions-item label="最大线程">{{ temp.maxSize }}</el-descriptions-item>
          <el-descriptions-item label="队列类型">
            {{ temp.queueType | queueFilter }}
          </el-descriptions-item>
          <el-descriptions-item label="队列容量">{{ temp.capacity }}</el-descriptions-item>

          <el-descriptions-item label="已完成任务数">{{ lastTaskCount }}</el-descriptions-item>
          <el-descriptions-item label="拒绝策略">
            {{ temp.rejectedType | rejectedFilter }}
          </el-descriptions-item>

          <el-descriptions-item label="是否报警">
            {{ temp.isAlarm | alarmFilter }}
          </el-descriptions-item>
          <el-descriptions-item label="任务拒绝次数">
            {{ rejectCount }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card> -->

      <el-row :gutter="10" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData1" :times="times" />
          </el-card>
        </el-col>
        <!-- <el-col :span="8">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData2" :times="times" />
          </el-card>
        </el-col> -->

        <el-col :span="12">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData3" :times="times" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10" style="margin-top: 16px">
        <el-col :span="12">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData5" :times="times" />
          </el-card>
        </el-col>
        <!-- <el-col :span="8">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData4" :times="times" />
          </el-card>
        </el-col> -->
        <el-col :span="12">
          <el-card shadow="hover">
            <line-chart :chart-data="lineChartData6" :times="times" />
          </el-card>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script>
import LineChart from './components/LineChart.vue';
import waves from '@/directive/waves';
import * as itemApi from '@/api/hippo4j-item';
import * as tenantApi from '@/api/hippo4j-tenant';
import * as threadPoolApi from '@/api/hippo4j-threadPool';
import * as instanceApi from '@/api/hippo4j-instance';
import * as monitorApi from '@/api/hippo4j-monitor';

export default {
  name: 'Monitor',
  components: {
    LineChart,
  },
  directives: { waves },
  filters: {
    queueFilter(type) {
      const typeList = {
        1: 'ArrayBlockingQueue',
        2: 'LinkedBlockingQueue',
        3: 'LinkedBlockingDeque',
        4: 'SynchronousQueue',
        5: 'LinkedTransferQueue',
        6: 'PriorityBlockingQueue',
        9: 'ResizableLinkedBlockingQueue',
      };
      return typeList[type];
    },

    rejectedFilter(type) {
      const typeList = {
        1: 'CallerRunsPolicy',
        2: 'AbortPolicy',
        3: 'DiscardPolicy',
        4: 'DiscardOldestPolicy',
        5: 'RunsOldestTaskPolicy',
        6: 'SyncPutQueuePolicy',
      };
      return type && typeList[type] ? typeList[type] : type ? `CustomRejectedPolicy-${type}` : '';
    },

    alarmFilter(type) {
      return type === '1' ? '报警' : '忽略';
    },
    allowCoreThreadTimeOutFilter(type) {
      return type === '1' ? '超时' : '不超时';
    },
  },
  data() {
    return {
      lineChartData1: [
        {
          name: 'activeSizeList',
          data: [],
          color: '#709775',
        },
      ],
      lineChartData2: [
        {
          name: 'poolSizeList',
          data: [],
          color: '#e9c46a',
        },
      ],
      lineChartData3: [
        {
          name: 'queueSizeList',
          data: [],
          color: '#003049',
        },
      ],
      lineChartData4: [
        {
          name: 'queueRemainingCapacityList',
          data: [],
          color: '#f4a261',
        },
      ],
      lineChartData5: [
        {
          name: 'completedTaskCountList',
          data: [],
          color: '#023e8a',
        },
      ],
      lineChartData6: [
        {
          name: 'rejectCountList',
          data: [],
          color: '#dd1c1a',
        },
      ],
      times: [],
      size: 500,
      tenantOptions: [],
      threadPoolOptions: [],
      itemOptions: [],
      identifyOptions: [],
      listQuery: {
        current: 1,
        size: 10,
        itemId: '',
        tpId: '',
        tenantId: '',
        identify: '',
        instanceId: '',
      },

      temp: {},
      rejectCount: null,
      lastTaskCount: null,
    };
  },
  async created() {
    this.initSelect();
  },
  methods: {
    fetchData() {
      if (!this.listQuery.tenantId) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('tenantManage.tenant') }));
        return;
      }
      if (!this.listQuery.itemId) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('projectManage.item') }));
        return;
      }
      if (!this.listQuery.tpId) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('threadPool.threadPool') }));
        return;
      }
      if (!this.listQuery.identify) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('threadPoolMonitor.ipPort') }));
        return;
      }

      this.listQuery.instanceId = this.listQuery.identify;
      threadPoolApi.info(this.listQuery).then((res) => {
        this.temp = res;
      });

      // monitorApi.lastTaskCountFun(this.listQuery).then((res) => {
      //   this.rejectCount = res.rejectCount;
      //   this.lastTaskCount = res.completedTaskCount;
      // });

      this.initChart();
    },
    refreshData() {
      this.listQuery.tenantId = null;
      this.listQuery.itemId = null;
      this.listQuery.tpId = null;
      this.listQuery.identify = null;
      this.itemOptions = [];
      this.threadPoolOptions = [];
      this.identifyOptions = [];
    },

    initSelect() {
      tenantApi.list({ size: this.size }).then((res) => {
        const { records } = res || {};
        this.tenantOptions =
          records &&
          records.map((item) => {
            return {
              key: item.tenantId,
              display_name: item.tenantId + ' ' + item.tenantName,
            };
          });
      });
    },

    tenantSelectList() {
      this.listQuery.itemId = null;
      this.listQuery.tpId = null;
      this.listQuery.identify = null;
      this.itemOptions = [];
      this.threadPoolOptions = [];
      this.identifyOptions = [];
      const params = { tenantId: this.listQuery.tenantId, size: this.size };
      itemApi.list(params).then((res) => {
        const { records } = res || {};
        this.itemOptions =
          records &&
          records.map((item) => {
            return {
              key: item.itemId,
              display_name: item.itemId + ' ' + item.itemName,
            };
          });
      });
    },

    itemSelectList() {
      this.listQuery.tpId = null;
      this.listQuery.identify = null;
      this.threadPoolOptions = [];
      this.identifyOptions = [];
      const params = { itemId: this.listQuery.itemId, size: this.size };
      threadPoolApi.list(params).then((res) => {
        const { records } = res || {};
        this.threadPoolOptions =
          records &&
          records.map((item) => {
            return {
              key: item.tpId,
              display_name: item.tpId,
            };
          });
      });
    },

    threadPoolSelectList() {
      this.listQuery.identify = null;
      this.identifyOptions = [];
      const listArray = [this.listQuery.itemId, this.listQuery.tpId];
      instanceApi.list(listArray).then((res) => {
        this.identifyOptions =
          res &&
          res.map((item) => {
            return {
              key: item.identify,
              display_name: item.clientAddress,
            };
          });
      });
    },

    initChart() {
      monitorApi.active(this.listQuery).then((res) => {
        const {
          poolSizeList,
          activeSizeList,
          queueSizeList,
          queueRemainingCapacityList,
          completedTaskCountList,
          rejectCountList,
          times,
        } = res || {};
        this.times = times;
        this.lineChartData1[0].data = activeSizeList;
        this.lineChartData2[0].data = poolSizeList;
        this.lineChartData3[0].data = queueSizeList;
        this.lineChartData4[0].data = queueRemainingCapacityList;
        this.lineChartData5[0].data = completedTaskCountList;
        this.lineChartData6[0].data = rejectCountList;
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  /* background-color: #2f4256; */
  position: relative;
  min-height: 100vh;
}
</style>
