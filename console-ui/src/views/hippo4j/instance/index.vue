<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="listQuery.itemId" placeholder="项目ID" style="width:220px" class="filter-item">
        <el-option v-for="item in itemOptions" :key="item.key" :label="item.display_name" :value="item.key"/>
      </el-select>
      <el-select v-model="listQuery.tpId" placeholder="线程池ID" style="width:220px" class="filter-item">
        <el-option v-for="item in threadPoolOptions" :key="item.key" :label="item.display_name" :value="item.key"/>
      </el-select>

      <el-button v-waves class="filter-item" type="primary" style="margin-left: 10px;" icon="el-icon-search"
                 @click="fetchData">
        搜索
      </el-button>
    </div>


    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column align="center" label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index+1 }}</template>
      </el-table-column>

      <el-table-column label="实例标识" align="center" width="200">
        <template slot-scope="scope">{{ scope.row.identify }}</template>
      </el-table-column>
      <el-table-column label="核心线程" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.coreSize }}</template>
      </el-table-column>
      <el-table-column label="最大线程" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.maxSize }}</template>
      </el-table-column>
      <el-table-column label="队列类型" align="center">
        <template slot-scope="scope">{{ scope.row.queueType | queueFilter }}</template>
      </el-table-column>
      <el-table-column label="队列容量" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.capacity }}</template>
      </el-table-column>
      <el-table-column label="队列容量" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.rejectedType | rejectedFilter }}</template>
      </el-table-column>
      <el-table-column label="线程存活" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.keepAliveTime }}</template>
      </el-table-column>
      <el-table-column label="是否报警" align="center" width="200">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isAlarm" active-color="#00A854" active-text="报警" :active-value="0"
                     inactive-color="#F04134" inactive-text="忽略" :inactive-value="1" @change="changeSwitch(scope.row)"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleInfo(row)">
            查看
          </el-button>
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.current" :limit.sync="listQuery.size"
                @pagination="fetchData"/>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="instanceDialogFormVisible" width="1000px">
      <test v-if="dialogVisible"></test>
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="right" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线程池ID" prop="tpId">
              <el-input v-model="runTimeTemp.tpId" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前负载" prop="currentLoad">
              <el-input v-model="runTimeTemp.currentLoad" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="峰值负载" prop="peakLoad">
              <el-input v-model="runTimeTemp.peakLoad" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线程相关">
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="核心线程" prop="coreSize">
              <el-input v-model="runTimeTemp.coreSize" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前线程" prop="poolSize">
              <el-input v-model="runTimeTemp.poolSize" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大线程" prop="maximumSize">
              <el-input v-model="runTimeTemp.maximumSize" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活跃线程" prop="activeSize">
              <el-input v-model="runTimeTemp.activeSize" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <!--<el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="共存最大线程" prop="completedTaskCount">
              <el-input v-model="runTimeTemp.completedTaskCount" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>-->

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="队列相关">
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="阻塞队列" prop="queueType">
              <el-input v-model="runTimeTemp.queueType" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="队列容量" prop="queueCapacity">
              <el-input v-model="runTimeTemp.queueCapacity" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="队列元素" prop="queueSize">
              <el-input v-model="runTimeTemp.queueSize" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="队列剩余容量" prop="queueRemainingCapacity">
              <el-input v-model="runTimeTemp.queueRemainingCapacity" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="其它信息">
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务总量" prop="completedTaskCount">
              <el-input v-model="runTimeTemp.completedTaskCount" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拒绝次数" prop="rejectCount">
              <el-input v-model="runTimeTemp.rejectCount" :disabled="true"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="instanceDialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="handleInfo()">
          刷新
        </el-button>
      </div>
    </el-dialog>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="1000px"
               :before-close="handleClose">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租户ID" prop="tenantId">
              <el-select v-model="temp.tenantId" placeholder="请选择租户" style="display:block;"
                         :disabled="dialogStatus==='create'?false:true">
                <el-option v-for="item in tenantOptions" :key="item.key" :label="item.display_name" :value="item.key"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="核心线程" prop="coreSize">
              <el-input-number v-model="temp.coreSize" placeholder="核心线程" :min="1" :max="999"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目ID" prop="itemId">
              <el-select v-model="temp.itemId" placeholder="请选择项目" style="display:block;"
                         :disabled="dialogStatus==='create'?false:true">
                <el-option v-for="item in itemOptions" :key="item.key" :label="item.display_name" :value="item.key"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大线程" prop="maxSize">
              <el-input-number v-model="temp.maxSize" placeholder="最大线程" :min="1" :max="999"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线程池ID" prop="tpId">
              <el-input v-model="temp.tpId" size="medium" placeholder="请输入线程池ID"
                        :disabled="dialogStatus==='create'?false:true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="KVTime" prop="keepAliveTime">
              <el-input-number v-model="temp.keepAliveTime" placeholder="Time / S" :min="20" :max="90"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="队列类型" prop="queueType">
              <el-select v-model="temp.queueType" placeholder="队列类型" style="display:block;">
                <el-option v-for="item in queueTypeOptions" :key="item.key" :label="item.display_name"
                           :value="item.key"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="队列容量" prop="capacity">
              <el-input-number v-model="temp.capacity" placeholder="队列容量" :min="1" :max="99999"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="拒绝策略" prop="rejectedType">
              <el-select v-model="temp.rejectedType" style="display:block;" placeholder="拒绝策略">
                <el-option v-for="item in rejectedOptions" :key="item.key" :label="item.display_name"
                           :value="item.key"/>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="活跃度报警" prop="livenessAlarm">
              <el-input-number v-model="temp.livenessAlarm" placeholder="活跃度报警" :min="1" :max="99999"/>
            </el-form-item>
          </el-col>


        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否报警" prop="isAlarm">
              <el-select v-model="temp.isAlarm" placeholder="是否报警" style="display:block;">
                <el-option v-for="item in alarmTypes" :key="item.key" :label="item.display_name"
                           :value="item.key"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量报警" prop="capacityAlarm">
              <el-input-number v-model="temp.capacityAlarm" placeholder="容量报警" :min="20" :max="90"/>
            </el-form-item>
          </el-col>

        </el-row>

        <el-row :gutter="20">

        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确认
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogPluginVisible" title="Reading statistics">
      <el-table :data="pluginData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel"/>
        <el-table-column prop="pv" label="Pv"/>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import * as itemApi from '@/api/hippo4j-item'
  import * as threadPoolApi from '@/api/hippo4j-threadPool'
  import * as instanceApi from '@/api/hippo4j-instance'
  import waves from '@/directive/waves'
  import Pagination from '@/components/Pagination'
  import axios from 'axios'

  export default {
    name: 'JobProject',
    components: {Pagination},
    directives: {waves},
    filters: {
      statusFilter(status) {
        const statusMap = {
          published: 'success',
          draft: 'gray',
          deleted: 'danger'
        }
        return statusMap[status]
      }
    },
    filters: {
      queueFilter(type) {
        if ('1' == type) {
          return 'ArrayBlockingQueue'
        } else if ('2' == type) {
          return 'LinkedBlockingQueue'
        } else if ('3' == type) {
          return 'LinkedBlockingDeque'
        } else if ('4' == type) {
          return 'SynchronousQueue'
        } else if ('5' == type) {
          return 'LinkedTransferQueue'
        } else if ('6' == type) {
          return 'PriorityBlockingQueue'
        } else if ('9' == type) {
          return 'ResizableLinkedBlockingQueue'
        }
      },

      rejectedFilter(type) {
        if ('1' == type) {
          return 'CallerRunsPolicy'
        } else if ('2' == type) {
          return 'AbortPolicy'
        } else if ('3' == type) {
          return 'DiscardPolicy'
        } else if ('4' == type) {
          return 'DiscardOldestPolicy'
        }
      }
    },
    data() {
      return {
        list: null,
        listLoading: false,
        total: 0,
        listQuery: {
          current: 1,
          size: 10,
          itemId: '',
          tpId: ''
        },
        pluginTypeOptions: ['reader', 'writer'],
        dialogPluginVisible: false,
        pluginData: [],
        dialogFormVisible: false,
        instanceDialogFormVisible: false,
        tenantOptions: [],
        threadPoolOptions: [],
        itemOptions: [],
        queueTypeOptions: [
          {key: 1, display_name: 'ArrayBlockingQueue'},
          {key: 2, display_name: 'LinkedBlockingQueue'},
          {key: 3, display_name: 'LinkedBlockingDeque'},
          {key: 4, display_name: 'SynchronousQueue'},
          {key: 5, display_name: 'LinkedTransferQueue'},
          {key: 6, display_name: 'PriorityBlockingQueue'},
          {key: 9, display_name: 'ResizableLinkedBlockingQueue'}
        ],
        rejectedOptions: [
          {key: 1, display_name: 'CallerRunsPolicy'},
          {key: 2, display_name: 'AbortPolicy'},
          {key: 3, display_name: 'DiscardPolicy'},
          {key: 4, display_name: 'DiscardOldestPolicy'}
        ],
        alarmTypes: [
          {key: 0, display_name: '报警'},
          {key: 1, display_name: '不报警'}
        ],
        dialogStatus: '',
        textMap: {
          update: 'Edit',
          create: 'Create',
          info: 'Info'
        },
        temp: {
          id: undefined,
          tenantId: ''
        },
        runTimeTemp: {},
        tempRow: {},
        visible: true
      }
    },
    created() {
      // this.fetchData()
      // 初始化项目
      this.initSelect()
    },
    methods: {
      fetchData() {
        if (Object.keys(this.listQuery.itemId).length == 0) {
          alert('项目ID不允许为空')
          return
        }
        if (Object.keys(this.listQuery.tpId).length == 0) {
          alert('线程池ID不允许为空!')
          return
        }
        this.listLoading = true
        const listArray = [this.listQuery.itemId, this.listQuery.tpId]
        instanceApi.list(listArray).then(response => {
          const {records} = response
          // const { total } = response
          // this.total = total
          this.list = response
          this.listLoading = false
        })
      },
      initSelect() {
        itemApi.list({}).then(response => {
          const {records} = response
          for (var i = 0; i < records.length; i++) {
            this.itemOptions.push({
              key: records[i].itemId,
              display_name: records[i].itemId + ' ' + records[i].itemName
            })
          }

          threadPoolApi.list({}).then(response => {
            const {records} = response
            for (var i = 0; i < records.length; i++) {
              this.threadPoolOptions.push({
                key: records[i].tpId,
                display_name: records[i].tpId
              })
            }
          })
        })

      },
      resetTemp() {
        this.temp = {
          id: undefined,
          tenantName: '',
          tenantDesc: ''
        }
      },
      handleCreate() {
        this.resetTemp()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            threadPoolApi.created(this.temp).then(() => {
              this.fetchData()
              this.dialogFormVisible = false
              this.$notify({
                title: 'Success',
                message: 'Created Successfully',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      },
      handleUpdate(row) {
        this.temp = Object.assign({}, row) // copy obj
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      handleInfo(row) {
        this.instanceDialogFormVisible = true
        this.dialogStatus = 'info'

        if (typeof row === 'undefined' || row === null) {
          row = this.tempRow
        } else {
          this.tempRow = {"identify": row.identify, "clientBasePath": row.clientBasePath, "tpId": row.tpId}
        }
        this.refresh(row)
      },

      refresh(row) {
        const identify = row.identify
        let clientBasePath = row.clientBasePath
        if (Object.keys(clientBasePath).length == 0) {
          clientBasePath = ''
        }

        axios({
          method: 'get',
          changeOrigin: true,
          url: 'http://' + identify + clientBasePath + '/run/state/' + row.tpId,
          headers: {'Access-Control-Allow-Credentials': true},
          params: {}
        }).then((response) => {
          this.runTimeTemp = response.data.data
          console.log(this.runTimeTemp)
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)
            instanceApi.updated(tempData).then(() => {
              this.fetchData()
              this.dialogFormVisible = false
              this.$notify({
                title: 'Success',
                message: 'Update Successfully',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      }
    }
  }
</script>
