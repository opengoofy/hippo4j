<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>

<script>
import resize from './mixins/resize';

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
    autoResize: {
      type: Boolean,
      default: true,
    },
    chartData: {
      type: Array,
      required: true,
    },
    times: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      chart: null,
      chartDataFrom: {},
    };
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val);
      },
    },
  },
  mounted() {
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
    initChart() {
      let echarts = require('echarts');
      this.chart = echarts.init(this.$el, 'macarons');
      this.setOptions();
    },
    setOptions() {
      const series = this.chartData.map((item) => {
        return {
          name: item.name,
          type: 'line',
          stack: 'Total',
          areaStyle: {},
          label: {
            position: 'top',
          },
          color: item.color,
          emphasis: {
            focus: 'series',
          },
          smooth: true,
          data: item.data,
          showSymbol: false,
        };
      });
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985',
            },
          },
        },
        legend: {
          data: this.chartData,
        },
        toolbox: {
          feature: {},
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: this.times,
          },
        ],
        yAxis: [
          {
            type: 'value',
          },
        ],
        series: series,
      });
    },
  },
};
</script>
