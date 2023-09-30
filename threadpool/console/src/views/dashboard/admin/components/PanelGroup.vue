<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('chartInfo')">
        <div class="card-panel-icon-wrapper ">
          <svg-icon icon-class="tenant_logo2" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{ $t('tenantManage.tenant') }}
          </div>
          <count-to :start-val="0" :end-val="successCount" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper ">
          <svg-icon icon-class="item_logo4" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{ $t('projectManage.item') }}
          </div>
          <count-to :start-val="0" :end-val="failCount" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper ">
          <svg-icon icon-class="threadPool_logo1" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{ $t('threadPool.threadPool') }}
          </div>
          <count-to :start-val="0" :end-val="runningCount" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper ">
          <svg-icon icon-class="threadPool_logo2" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{ $t('report.poolInstance') }}
          </div>
          <count-to :start-val="0" :end-val="runningCountInstance" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  props: {
    countSucTotal: {
      type: Number,
      default: 0,
    },
    countFailTotal: {
      type: Number,
      default: 0,
    },
    countRunningTotal: {
      type: Number,
      default: 0,
    },
    countRunningInstanceTotal: {
      type: Number,
      default: 0,
    },
  },
  data () {
    return {
      successCount: this.countSucTotal,//parseInt(localStorage.getItem('countSucTotal')),
      failCount: this.countFailTotal,//parseInt(localStorage.getItem('countFailTotal')),
      runningCount: this.countRunningTotal,//parseInt(localStorage.getItem('countRunningTotal'))
      runningCountInstance: this.countRunningInstanceTotal
    }
  },
  methods: {
    handleSetLineChartData (type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.05);
    border-color: rgba(0, 0, 0, 0.05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #dae8d6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #a0a6f4;
      }

      .icon-shopping {
        background: #dae8d6;
      }
    }

    .icon-people {
      color: #40c9c6;
    }

    .icon-message {
      color: #36a3f7;
    }

    .icon-money {
      color: #a0a6f4;
    }

    .icon-shopping {
      color: #34bfa3;
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width: 550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
