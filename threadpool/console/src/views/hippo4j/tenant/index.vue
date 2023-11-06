<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.tenantId"
        clearable
        :placeholder="$t('tenantManage.tenant')"
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
      <el-button
        :disabled="isEditDisabled"
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >
        {{ $t('common.insert') }}
      </el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      :key="tableIsChange"
      border
      highlight-current-row
      element-loading-text="Loading"
    >
      <el-table-column :label="$t('common.num')" width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column :label="$t('tenantManage.tenant')">
        <template slot-scope="scope">{{ scope.row.tenantId }}</template>
      </el-table-column>
      <el-table-column :label="$t('tenantManage.tenantName')">
        <template slot-scope="scope">{{ scope.row.tenantName }}</template>
      </el-table-column>
      <!-- <el-table-column label="租户简介">
        <template slot-scope="scope">{{ scope.row.tenantDesc | ellipsis }}</template>
      </el-table-column> -->
      <el-table-column :label="$t('tenantManage.owner')">
        <template slot-scope="scope">{{ scope.row.owner }} </template>
      </el-table-column>
      <!-- <el-table-column label="创建时间">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column> -->
      <el-table-column :label="$t('common.updateTime')">
        <template slot-scope="scope">{{ scope.row.gmtModified }}</template>
      </el-table-column>
      <el-table-column
        :label="$t('common.operation')"
        width="90"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" :disabled="isEditDisabled" size="small" @click="handleUpdate(row)">
            {{ $t('common.edit') }}
          </el-button>
          <el-button size="small" :disabled="isEditDisabled" type="text" @click="handleDelete(row)">
            {{ $t('common.delete') }}
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        style="width: 500px; margin-left: 50px"
        :rules="rules"
        :model="temp"
        label-width="120px"
      >
        <el-form-item :label="$t('tenantManage.tenant')" prop="tenantId">
          <el-input
            v-model="temp.tenantId"
            :disabled="dialogStatus === 'create' ? false : true"
            :placeholder="$t('tenantManage.tenant')"
          />
        </el-form-item>
        <el-form-item :label="$t('tenantManage.tenantName')" prop="tenantName">
          <el-input v-model="temp.tenantName" :placeholder="$t('tenantManage.tenantName')" />
        </el-form-item>
        <el-form-item :label="$t('tenantManage.owner')" prop="owner">
          <el-input v-model="temp.owner" :placeholder="$t('tenantManage.owner')" />
        </el-form-item>
        <el-form-item :label="$t('tenantManage.tenantIntro')" prop="tenantDesc">
          <el-input
            v-model="temp.tenantDesc"
            :autosize="{ minRows: 3, maxRows: 6 }"
            type="textarea"
            :placeholder="$t('tenantManage.tenantIntro')"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> {{ $t('common.cancel') }} </el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogPluginVisible" title="Reading statistics">
      <el-table :data="pluginData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel" />
        <el-table-column prop="pv" label="Pv" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">{{
          $t('common.confirm')
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as jobProjectApi from '@/api/hippo4j-tenant';
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
      if (value.length > 26) {
        return value.slice(0, 26) + '...';
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
        desc: true,
      },
      tableIsChange: false,
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      isEditDisabled: false,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
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
  watch: {
    tenantInfo(newVal, oldVal) {
      this.listQuery.tenantId = newVal.tenantId;
      this.fetchData()
    }
  },
  computed:{
    ...mapGetters([
      'tenantInfo'
    ]),
    rules(){
      return{
        tenantId: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        tenantName: [
          { required: true, message: this.$t('message.requiredError'), trigger: 'blur' },
        ],
        owner: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        tenantDesc: [
          { required: true, message: this.$t('message.requiredError'), trigger: 'blur' },
        ],
      }
    },
  },
  created() {
    this.fetchData();
  },
  mounted() {
    this.isEditDisabled = localStorage.getItem('USER_ROLE') !== 'ROLE_ADMIN';
  },
  methods: {
    fetchData() {
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
      this.listLoading = true;
      jobProjectApi.list(this.listQuery).then((response) => {
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
              title: 'Success',
              message: 'Update Successfully',
              type: 'success',
              duration: 2000,
            });
          });
        }
      });
    },
    openDelConfirm(name) {
      return this.$confirm(this.$t('message.deleteMessage', { name }), this.$t('common.hint'), {
        confirmButtonText: this.$t('common.ok'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      });
    },
    handleDelete(row) {
      this.openDelConfirm(row.tenantId).then(() => {
        jobProjectApi.deleted(row.tenantId).then((response) => {
          this.fetchData();
          this.$notify({
            title: 'Success',
            message: 'Delete Successfully',
            type: 'success',
            duration: 2000,
          });
        });
      });
    },
  },
};
</script>
