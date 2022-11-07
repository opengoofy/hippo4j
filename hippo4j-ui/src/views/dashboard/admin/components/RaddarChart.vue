<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import resize from './mixins/resize'
import * as dashborad from '@/api/dashborad'

const animationDuration = 3000

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
    initChart () {
      let echarts = require('echarts')
      this.chart = echarts.init(this.$el, 'macarons')
      dashborad.tenantChart({}).then(response => {
        this.chart.setOption({
          legend: {
            left: 'center',
            bottom: '10',
          },
          toolbox: {
            show: true,
            feature: {
              mark: { show: true },
            }
          },
          series: [
            {
              name: 'Nightingale Chart',
              type: 'pie',
              roseType: 'radius',
              radius: [15, 95],
              center: ['50%', '38%'],
              roseType: 'area',
              itemStyle: {
                borderRadius: 8
              },
              data: response.tenantCharts
            }
          ]
        })
      })
    }
  }
}
</script>

