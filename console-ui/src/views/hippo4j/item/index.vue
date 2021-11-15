<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.itemId" clearable placeholder="项目ID" style="width: 200px;" class="filter-item"
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
      <el-table-column label="项目名称" align="center">
        <template slot-scope="scope">{{ scope.row.itemName }}</template>
      </el-table-column>
      <el-table-column label="项目简介" align="center">
        <template slot-scope="scope">{{ scope.row.itemDesc | ellipsis }}</template>
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
          <el-select v-model="temp.tenantId" placeholder="租户ID" filterable clearable class="filter-item"
                     style="width: 40%" :disabled="dialogStatus==='create'?false:true">
            <el-option v-for="item in tenantOptions" :key="item.key" :label="item.display_name" :value="item.key"/>
          </el-select>
        </el-form-item>
        <el-form-item label="项目ID" prop="itemId">
          <el-input v-model="temp.itemId" :disabled="dialogStatus==='create'?false:true" placeholder="项目ID"
                    style="width: 40%"/>
        </el-form-item>
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="temp.itemName" placeholder="项目名称" style="width: 40%"/>
        </el-form-item>
        <el-form-item label="OWNER" prop="owner">
          <el-input v-model="temp.owner" placeholder="OWNER" style="width: 40%"/>
        </el-form-item>
        <el-form-item label="项目描述" prop="itemDesc">
          <el-input v-model="temp.itemDesc" :autosize="{ minRows: 3, maxRows: 6}" type="textarea" placeholder="项目描述"
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
  import * as jobProjectApi from '@/api/hippo4j-item'
  import * as tenantApi from '@/api/hippo4j-tenant'
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
          itemId: ''
        },
        pluginTypeOptions: ['reader', 'writer'],
        dialogPluginVisible: false,
        pluginData: [],
        dialogFormVisible: false,
        tenantOptions: [],
        dialogStatus: '',
        textMap: {
          update: 'Edit',
          create: 'Create'
        },
        rules: {
          tenantId: [{required: true, message: 'this is required', trigger: 'blur'}],
          itemId: [{required: true, message: 'this is required', trigger: 'blur'}],
          itemName: [{required: true, message: 'this is required', trigger: 'blur'}],
          owner: [{required: true, message: 'this is required', trigger: 'blur'}],
          itemDesc: [{required: true, message: 'this is required', trigger: 'blur'}]
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
      this.fetchData();
      // 初始化租户
      this.initSelect();
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
      initSelect() {
        tenantApi.list({}).then(response => {
          const {records} = response
          for (var i = 0; i < records.length; i++) {
            this.tenantOptions.push({
              key: records[i].tenantId,
              display_name: records[i].tenantId + ' ' + records[i].tenantName
            });
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
        const delObj = [row.tenantId, row.itemId]
        jobProjectApi.deleted(delObj).then(response => {
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
