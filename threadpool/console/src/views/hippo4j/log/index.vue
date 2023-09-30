<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.category"
        clearable
        :placeholder="$t('logManage.bizType')"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.bizNo"
        clearable
        :placeholder="$t('logManage.bizID')"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.operator"
        clearable
        :placeholder="$t('logManage.operator')"
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
        {{ $t('common.query') }}
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
      <el-table-column :label="$t('common.num')" fixed width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column :label="$t('logManage.bizType')" width="200">
        <template slot-scope="scope">{{ scope.row.category }}</template>
      </el-table-column>
      <el-table-column :label="$t('logManage.bizID')" width="380">
        <template slot-scope="scope">{{ scope.row.bizNo }}</template>
      </el-table-column>
      <el-table-column :label="$t('logManage.logContent')" width="500">
        <template slot-scope="scope">{{ scope.row.action | ellipsis }}</template>
      </el-table-column>
      <el-table-column :label="$t('logManage.operator')" width="140">
        <template slot-scope="scope">{{ scope.row.operator }} </template>
      </el-table-column>
      <el-table-column :label="$t('common.createTime')" width="160">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
      <el-table-column
        :label="$t('common.operation')"
        fixed="right"
        width="90"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleUpdate(row)">
            {{ $t('common.detail') }}
          </el-button>
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
      <el-form ref="dataForm" :model="temp" label-width="110px">
        <el-form-item :label="$t('logManage.bizType')" prop="category">
          <el-input
            v-model="temp.category"
            :disabled="true"
            :placeholder="$t('logManage.bizType')"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item :label="$t('logManage.bizID')" prop="bizNo">
          <el-input
            v-model="temp.bizNo"
            :disabled="true"
            :placeholder="$t('logManage.bizID')"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item :label="$t('logManage.operator')" prop="operator">
          <el-input
            v-model="temp.operator"
            :disabled="true"
            :placeholder="$t('logManage.operator')"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item :label="$t('common.createTime')" prop="createTime">
          <el-input
            v-model="temp.createTime"
            :disabled="true"
            :placeholder="$t('common.createTime')"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item :label="$t('logManage.logContent')" prop="action">
          <el-input
            v-model="temp.action"
            :disabled="true"
            :autosize="{ minRows: 4, maxRows: 10 }"
            type="textarea"
            :placeholder="$t('logManage.logContent')"
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
import { mapGetters } from 'vuex';
import { i18nConfig } from '@/locale/config'

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
      // this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      // let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      // this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
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
              title: this.$t('message.success'),
              message: this.$t('message.createdSuccess'),
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
              title: this.$t('message.success'),
              message: this.$t('message.updateSuccess'),
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
          title: this.$t('message.success'),
          message: this.$t('message.deleteSuccess'),
          type: 'success',
          duration: 2000,
        });
      });
    },
  },
};
</script>
