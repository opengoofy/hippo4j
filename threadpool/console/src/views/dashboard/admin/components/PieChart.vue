<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import resize from './mixins/resize'
import * as dashborad from '@/api/dashborad'

const pieChartData = {
  itemIds: ['Industries', 'Technology', 'Forex', 'Gold', 'Forecasts'],
  pieDataList: [
    { value: 320, name: 'Industries' },
    { value: 240, name: 'Technology' },
    { value: 149, name: 'Forex' },
    { value: 100, name: 'Gold' },
    { value: 59, name: 'Forecasts' }
  ]
}

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data () {
    return {
      chart: null
    }
  },
  mounted () {
    this.pieChartInfo()
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    pieChartInfo () {

    },

    initChart () {
      let echarts = require('echarts')
      this.chart = echarts.init(this.$el, 'macarons')
      dashborad.pieChart({}).then(response => {
        pieChartData.itemIds = response.itemIds
        pieChartData.pieDataList = response.pieDataList
        this.chart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
          },
          legend: {
            left: 'center',
            bottom: '10',
            data: pieChartData.itemIds
          },
          toolbox: {
            show: true,
            feature: {
              mark: { show: true },
            }
          },
          series: [
            {
              name: 'NUMBER OF THREAD POOLS',
              type: 'pie',
              roseType: 'radius',
              radius: [15, 95],
              center: ['50%', '38%'],
              data: pieChartData.pieDataList,
              animationEasing: 'cubicInOut',
              animationDuration: 2600
            }
          ]
        })
      })

    }
  }
}
</script>
