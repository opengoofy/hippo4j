import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import { useEffect } from 'react';
echarts.use([GridComponent, LineChart, CanvasRenderer, UniversalTransition]);

const ThreadPoolMonitor = () => {
  useEffect(() => {
    let chartDom = document.getElementById('main');
    let myChart = echarts.init(chartDom);
    let option;
    option = {
      xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          data: [150, 230, 224, 218, 135, 147, 260],
          type: 'line',
        },
      ],
    };
    option && myChart.setOption(option);
  });
  return <div id="main" style={{ width: '400px', height: '400px' }}></div>;
};

export default ThreadPoolMonitor;
