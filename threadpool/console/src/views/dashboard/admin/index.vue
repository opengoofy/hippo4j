<template>
  <div v-if="show" class="dashboard-editor-container">
    <github-corner class="github-corner" />

    <panel-group
      :count-suc-total="countSucTotal"
      :count-fail-total="countFailTotal"
      :count-running-total="countRunningTotal"
      :count-running-instance-total="countRunningInstanceTotal"
      @handleSetLineChartData="handleSetLineChartData"
    />

    <el-row style="background: #fff; padding: 16px 16px 0; margin-bottom: 32px">
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <el-form label-position="left">
            <el-form-item :label="$t('report.user')" label-width="120px">
              <span>{{ temp.userName }}</span>
            </el-form-item>
            <el-form-item :label="$t('report.role')" label-width="120px">
              <span>{{ temp.role }}</span>
            </el-form-item>
            <el-form-item :label="$t('report.tenants')" label-width="120px">
              <span>{{ temp.tempResources }}</span>
            </el-form-item>
            <el-form-item :label="$t('common.createTime')" label-width="120px">
              <span> {{ temp.gmtCreate }}</span>
            </el-form-item>
            <el-form-item :label="$t('common.updateTime')" label-width="120px">
              <span>{{ temp.gmtModified }}</span>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart />
        </div>
      </el-col>
      <!-- <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart />
        </div>
      </el-col> -->
    </el-row>

    <el-row :gutter="8">
      <el-col
        :xs="{ span: 24 }"
        :sm="{ span: 24 }"
        :md="{ span: 24 }"
        :lg="{ span: 12 }"
        :xl="{ span: 12 }"
        style="padding-right: 8px; margin-bottom: 30px"
      >
        <transaction-table />
      </el-col>
      <!--<el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">
        <todo-list />
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">
        <box-card />
      </el-col>-->
    </el-row>
  </div>
</template>

<script>
import GithubCorner from '@/components/GithubCorner';
import PanelGroup from './components/PanelGroup.vue';
import LineChart from './components/LineChart.vue';
import RaddarChart from './components/RaddarChart.vue';
import PieChart from './components/PieChart.vue';
import TransactionTable from './components/TransactionTable.vue';
/*import TodoList from './components/TodoList'
  import BoxCard from './components/BoxCard'*/
import * as dashborad from '@/api/dashborad';
import * as user from '@/api/hippo4j-user';

const lineChartData = {
  chartInfo: {
    oneList: [1, 3, 4, 5, 3, 2],
    twoList: [1, 2, 3, 4, 1, 3],
    threeList: [1, 2, 3, 4, 1, 3],
    fourList: [1, 2, 3, 4, 1, 3],
    dayList: ['ten', 'twenty', 'thirty', 'forty', 'fifty', 'sixty'],
  },
};

export default {
  name: 'DashboardAdmin',
  components: {
    GithubCorner,
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart,
    TransactionTable,
    /*TodoList,
      BoxCard*/
  },
  data() {
    return {
      lineChartData: lineChartData.chartInfo,
      countSucTotal: 0,
      countRunningTotal: 0,
      countFailTotal: 0,
      countRunningInstanceTotal: 0,
      show: false,
      temp: {},
    };
  },
  async created() {
    this.chartInfo();
    this.lintChart();
    this.userInfo();
  },

  methods: {
    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type];
    },
    chartInfo() {
      dashborad
        .chartInfo()
        .then((res) => {
          const { tenantCount, threadPoolCount, threadPoolInstanceCount, itemCount } = res || {};
          this.show = true;
          this.countSucTotal = tenantCount;
          this.countRunningTotal = threadPoolCount;
          this.countFailTotal = itemCount;
          this.countRunningInstanceTotal = threadPoolInstanceCount;
        })
        .catch(() => {});
    },

    lintChart() {
      dashborad
        .lineChart({})
        .then((res) => {
          const { oneList, twoList, threeList, fourList } = res || {};
          this.lineChartData.oneList = oneList;
          this.lineChartData.twoList = twoList;
          this.lineChartData.threeList = threeList;
          this.lineChartData.fourList = fourList;
        })
        .catch(() => {});
    },

    userInfo() {
      const userName = this.$cookie.get('userName');
      user
        .getCurrentUser(userName)
        .then((response) => {
          this.temp = response;
        })
        .catch(() => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }

  .el-form-item {
    margin-bottom: 5px !important;
    padding-bottom: 20px;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
