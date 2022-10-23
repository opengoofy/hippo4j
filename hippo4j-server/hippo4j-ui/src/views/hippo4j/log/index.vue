<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.category"
        clearable
        placeholder="业务类型"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.bizNo"
        clearable
        placeholder="业务标识"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.operator"
        clearable
        placeholder="操作人"
        style="width: 200px"
        class="filter-item"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="fetchData"
      >
        搜索
      </el-button>
    </div>
    <el-table
      v-loading="listLoading"
      border
      :data="list"
      element-loading-text="Loading"
      fit
      highlight-current-row
    >
      <el-table-column label="序号" fixed width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="业务类型" width="200">
        <template slot-scope="scope">{{ scope.row.category }}</template>
      </el-table-column>
      <el-table-column label="业务标识" width="380">
        <template slot-scope="scope">{{ scope.row.bizNo }}</template>
      </el-table-column>
      <el-table-column label="日志内容" width="500">
        <template slot-scope="scope">{{ scope.row.action | ellipsis }}</template>
      </el-table-column>
      <el-table-column label="操作人" width="140">
        <template slot-scope="scope">{{ scope.row.operator }} </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
      <el-table-column
        label="操作"
        fixed="right"
        width="90"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleUpdate(row)"> 查看 </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.current"
      :limit.sync="listQuery.size"
      @pagination="fetchData"
    />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="800px">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="100px">
        <el-form-item label="业务类型" prop="category">
          <el-input
            v-model="temp.category"
            :disabled="true"
            placeholder="业务类型"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="业务标识" prop="bizNo">
          <el-input
            v-model="temp.bizNo"
            :disabled="true"
            placeholder="业务标识"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input
            v-model="temp.operator"
            :disabled="true"
            placeholder="操作人"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-input
            v-model="temp.createTime"
            :disabled="true"
            placeholder="创建时间"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="日志内容" prop="action">
          <el-input
            v-model="temp.action"
            :disabled="true"
            :autosize="{ minRows: 4, maxRows: 10 }"
            type="textarea"
            placeholder="日志内容"
            style="width: 60%"
          />
        </el-form-item>
      </el-form>
    </el-dialog>
    <el-dialog :visible.sync="dialogPluginVisible" title="Reading statistics">
      <el-table :data="pluginData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel" />
        <el-table-column prop="pv" label="Pv" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as logApi from '@/api/hippo4j-log';
import waves from '@/directive/waves';
import Pagination from '@/components/Pagination';

export default {
  name: 'JobProject',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'gray',
        deleted: 'danger',
      };
      return statusMap[status];
    },
    ellipsis(value) {
      if (!value) return '';
      if (value.length > 100) {
        return value.slice(0, 100) + '...';
      }
      return value;
    },
  },
  data() {
    return {
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        tenantId: '',
      },
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Info',
        create: 'Create',
      },
      temp: {
        id: undefined,
        tenantId: '',
        tenantName: '',
        owner: '',
        tenantDesc: '',
      },
      visible: true,
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      logApi.list(this.listQuery).then((response) => {
        const { records } = response;
        const { total } = response;
        this.total = total;
        this.list = records;
        this.listLoading = false;
      });
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        tenantName: '',
        tenantDesc: '',
      };
    },
    handleCreate() {
      this.resetTemp();
      this.dialogStatus = 'create';
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          jobProjectApi.created(this.temp).then(() => {
            this.fetchData();
            this.dialogFormVisible = false;
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000,
            });
          });
        }
      });
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row); // copy obj
      this.dialogStatus = 'update';
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp);
          jobProjectApi.updated(tempData).then(() => {
            this.fetchData();
            this.dialogFormVisible = false;
            this.$notify({
              title: 'Success',
              message: 'Update Successfully',
              type: 'success',
              duration: 2000,
            });
          });
        }
      });
    },
    handleDelete(row) {
      console.log('删除');
      jobProjectApi.deleted(row.tenantId).then((response) => {
        this.fetchData();
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000,
        });
      });
    },
  },
};
</script>
