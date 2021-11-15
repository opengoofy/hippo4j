<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.tenantId" clearable placeholder="租户ID" style="width: 200px;" class="filter-item"
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
      <el-table-column label="租户名称" align="center">
        <template slot-scope="scope">{{ scope.row.tenantName }}</template>
      </el-table-column>
      <el-table-column label="租户简介" align="center">
        <template slot-scope="scope">{{ scope.row.tenantDesc | ellipsis }}</template>
      </el-table-column>
      <el-table-column label="OWNER" width="200" align="center">
        <template slot-scope="scope">{{ scope.row.owner }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="200" align="center">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="800px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px">
        <el-form-item label="租户ID" prop="tenantId">
          <el-input v-model="temp.tenantId" :disabled="dialogStatus==='create'?false:true" placeholder="租户ID"
                    style="width: 40%"/>
        </el-form-item>
        <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="temp.tenantName" placeholder="租户名称" style="width: 40%"/>
        </el-form-item>
        <el-form-item label="OWNER" prop="owner">
          <el-input v-model="temp.owner" placeholder="OWNER" style="width: 40%"/>
        </el-form-item>
        <el-form-item label="租户描述" prop="tenantDesc">
          <el-input v-model="temp.tenantDesc" :autosize="{ minRows: 3, maxRows: 6}" type="textarea" placeholder="租户描述"
                    style="width: 40%"/>
        </el-form-item>
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
  import * as jobProjectApi from '@/api/hippo4j-tenant'
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
      },
      ellipsis(value) {
        if (!value) return "";
        if (value.length > 26) {
          return value.slice(0, 26) + "...";
        }
        return value;
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
          tenantId: ''
        },
        pluginTypeOptions: ['reader', 'writer'],
        dialogPluginVisible: false,
        pluginData: [],
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          update: 'Edit',
          create: 'Create'
        },
        rules: {
          tenantId: [{required: true, message: 'this is required', trigger: 'blur'}],
          tenantName: [{required: true, message: 'this is required', trigger: 'blur'}],
          owner: [{required: true, message: 'this is required', trigger: 'blur'}],
          tenantDesc: [{required: true, message: 'this is required', trigger: 'blur'}]
        },
        temp: {
          id: undefined,
          tenantId: '',
          tenantName: '',
          owner: '',
          tenantDesc: ''
        },
        visible: true
      }
    },
    created() {
      this.fetchData()
    },
    methods: {
      fetchData() {
        this.listLoading = true
        jobProjectApi.list(this.listQuery).then(response => {
          const {records} = response
          const {total} = response
          this.total = total
          this.list = records
          this.listLoading = false
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
            jobProjectApi.created(this.temp).then(() => {
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
            jobProjectApi.updated(tempData).then(() => {
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
        console.log('删除')
        jobProjectApi.deleted(row.tenantId).then(response => {
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
