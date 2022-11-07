<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import resize from './mixins/resize';
import * as dashborad from '@/api/dashborad';

const pieChartData = {
  itemIds: ['Industries', 'Technology', 'Forex', 'Gold', 'Forecasts'],
  pieDataList: [
    { value: 320, name: 'Industries' },
    { value: 240, name: 'Technology' },
    { value: 149, name: 'Forex' },
    { value: 100, name: 'Gold' },
    { value: 59, name: 'Forecasts' },
  ],
};

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart',
    },
    width: {
      type: String,
      default: '100%',
    },
    height: {
      type: String,
      default: '300px',
    },
  },
  data() {
    return {
      chart: null,
    };
  },
  mounted() {
    this.pieChartInfo();
    this.$nextTick(() => {
      this.initChart();
    });
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    pieChartInfo() {},

    initChart() {
      let echarts = require('echarts');
      this.chart = echarts.init(this.$el, 'macarons');
      dashborad.pieChart({}).then((response) => {
        pieChartData.itemIds = response.itemIds;
        pieChartData.pieDataList = response.pieDataList;
        this.chart.setOption({
          xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          },
          title: {
            left: 'center',
            text: '队列元素',
          },
          yAxis: {
            type: 'value',
          },
          series: [
            {
              data: [820, 932, 901, 934, 1290, 1330, 1320],
              type: 'line',
              smooth: true,
            },
          ],
        });
      });
    },
  },
};
</script>
