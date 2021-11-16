<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.tenantId" clearable placeholder="租户ID" style="width: 200px;" class="filter-item"
                @keyup.enter.native="handleFilter"/>
      <el-input v-model="listQuery.itemId" clearable placeholder="项目ID" style="width: 200px;" class="filter-item"
                @keyup.enter.native="handleFilter"/>
      <el-input v-model="listQuery.tpId" clearable placeholder="线程池ID" style="width: 200px;" class="filter-item"
                @keyup.enter.native="handleFilter"/>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="fetchData">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit"
                 @click="handleCreate">
        添加
      </el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column align="center" label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index+1 }}</template>
      </el-table-column>
      <el-table-column label="租户ID" align="center">
        <template slot-scope="scope">{{ scope.row.tenantId }}</template>
      </el-table-column>
      <el-table-column label="项目ID" align="center">
        <template slot-scope="scope">{{ scope.row.itemId }}</template>
      </el-table-column>
      <el-table-column label="线程池ID" align="center">
        <template slot-scope="scope">{{ scope.row.tpId }}</template>
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
      <el-table-column label="是否报警" align="center" width="200">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isAlarm" active-color="#00A854" active-text="报警" :active-value="0"
                     inactive-color="#F04134" inactive-text="忽略" :inactive-value="1" @change="changeSwitch(scope.row)"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button v-if="row.status!=='deleted'" size="mini" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.current" :limit.sync="listQuery.size"
                @pagination="fetchData"/>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="1000px"
               :before-close="handleClose">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基本信息"></el-form-item>
          </el-col>
        </el-row>
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
            <el-form-item label="扩展信息"></el-form-item>
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
  import * as tenantApi from '@/api/hippo4j-tenant'
  import * as threadPoolApi from '@/api/hippo4j-threadPool'
  import waves from '@/directive/waves'
  import Pagination from '@/components/Pagination'

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
      }
    },
    data() {
      return {
        list: null,
        listLoading: true,
        total: 0,
        listQuery: {
          current: 1,
          size: 10,
          itemId: ''
        },
        pluginTypeOptions: ['reader', 'writer'],
        dialogPluginVisible: false,
        pluginData: [],
        dialogFormVisible: false,
        tenantOptions: [],
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
          create: 'Create'
        },
        rules: {
          tenantId: [{required: true, message: 'this is required', trigger: 'blur'}],
          itemId: [{required: true, message: 'this is required', trigger: 'blur'}],
          tpId: [{required: true, message: 'this is required', trigger: 'blur'}],
          coreSize: [{required: true, message: 'this is required', trigger: 'blur'}],
          maxSize: [{required: true, message: 'this is required', trigger: 'blur'}],
          queueType: [{required: true, message: 'this is required', trigger: 'blur'}],
          capacity: [{required: true, message: 'this is required', trigger: 'blur'}],
          keepAliveTime: [{required: true, message: 'this is required', trigger: 'blur'}],
          isAlarm: [{required: true, message: 'this is required', trigger: 'blur'}],
          capacityAlarm: [{required: true, message: 'this is required', trigger: 'blur'}],
          livenessAlarm: [{required: true, message: 'this is required', trigger: 'blur'}],
          rejectedType: [{required: true, message: 'this is required', trigger: 'blur'}]
        },
        temp: {
          id: undefined,
          tenantId: ''
        },
        visible: true
      }
    },
    created() {
      this.fetchData()
      // 初始化租户、项目
      this.initSelect()
    },
    methods: {
      fetchData() {
        this.listLoading = true
        threadPoolApi.list(this.listQuery).then(response => {
          const {records} = response
          const {total} = response
          this.total = total
          this.list = records
          this.listLoading = false
        })
      },
      initSelect() {
        tenantApi.list({}).then(response => {
          const {records} = response
          for (var i = 0; i < records.length; i++) {
            this.tenantOptions.push({
              key: records[i].tenantId,
              display_name: records[i].tenantId + ' ' + records[i].tenantName
            })
          }
        })

        itemApi.list({}).then(response => {
          const {records} = response
          for (var i = 0; i < records.length; i++) {
            this.itemOptions.push({
              key: records[i].itemId,
              display_name: records[i].itemId + ' ' + records[i].itemName
            })
          }
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
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)
            threadPoolApi.updated(tempData).then(() => {
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
      },
      handleDelete(row) {
        threadPoolApi.deleted(row).then(response => {
          this.fetchData()
          this.$notify({
            title: 'Success',
            message: 'Delete Successfully',
            type: 'success',
            duration: 2000
          })
        })
      }
    }
  }
</script>
