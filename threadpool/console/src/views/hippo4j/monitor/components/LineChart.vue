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
    title: {
      type: String,
      default: '标题'
    },
    cusFormatter: {
      type: Boolean,
      default: false
    }
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
        const other = !item.show ? {
          symbolSize: 0,
          showSymbol: false,
          lineStyle: {
            width: 0,
            color: 'rgba(0, 0, 0, 0)'
          }
        } : {}
        return {
          name: item.name,
          type: 'line',
          // stack: 'Total',
          // areaStyle: {},
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
          ...other
        };
      });
      let numbers = this.chartData && this.chartData[0]?.data;
      let maxNumber = Math.max(...numbers);
      const cusFormatterObj = this.cusFormatter ? {
        formatter: (params) => {
          const taskAll = params.find(item => item.seriesName === '任务总数')
          const taskCom = params.find(item => item.seriesName === '区间任务完成数')
          const taskReject = params.find(item => item.seriesName === '拒绝总数')
          return `<div>
              <div style="padding: 6px 0;font-weight: bolder;">${taskCom?.seriesName}：${taskCom?.value}</div>
              <div style="width: 100%,height: 0px;border-bottom: 1px dashed #ddd;padding: 6px 0;"></div>
              <div style="padding: 6px 0;">${taskAll?.seriesName}：${taskAll?.value}</div>
              <div style="padding: 6px 0;">${taskReject?.seriesName}：${taskReject?.value}</div>
              </div>`
        }
      } : {};
      this.chart.setOption({
        title: {
          text: this.title,
          textStyle: {
            fontSize: '16px'
          },
          left: 0,
          right: 0,
          top: 0,
          bottom: 0,
          padding: 0,
        },
        tooltip: {
          trigger: 'axis',
          ...cusFormatterObj
        },
        legend: {
          show:false,
          data: this.chartData,
          icon: 'circle',
          top: 0,
        },
        toolbox: {
          feature: {},
        },
        grid: {
          left: 0,
          right: 0,
          top: '18%',
          bottom: '0',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.times,
          nameTextStyle: {
            width: '20px'
          },
          axisLabel: {
            color: '#333'
          },
          axisLine: {
            lineStyle: {
              color: '#ddd'
            }
          },
          axisTick: {
            show: true,
            lineStyle: {
              color: '#ddd'
            }
          },
        },
        yAxis: [
          {
            type: 'value',
            max: maxNumber,
          },
        ],
        series: series,
      });
    },
  },
};
</script>
