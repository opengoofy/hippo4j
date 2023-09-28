<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.userName"
        :placeholder="$t('userAuthority.userName')"
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
      element-loading-text="Loading"
      fit
      border
      highlight-current-row
    >
      <el-table-column fixed :label="$t('common.num')" width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column :label="$t('userAuthority.userName')">
        <template slot-scope="scope">{{ scope.row.userName }}</template>
      </el-table-column>
      <el-table-column :label="$t('userAuthority.role')">
        <template slot-scope="scope">
          <span>
            <el-tag :type="scope.row.role | statusFilter">{{ scope.row.role }}</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.createTime')">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column>
      <el-table-column :label="$t('common.updateTime')">
        <template slot-scope="scope">{{ scope.row.gmtModified }}</template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" class-name="small-padding fixed-width">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleUpdate(row)"> {{ $t('common.edit') }} </el-button>
          <el-button
            v-if="row.status !== 'deleted'"
            size="small"
            type="text"
            :disabled="row.userName !== 'admin' ? false : true"
            @click="handleDelete(row)"
          >
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
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item :label="$t('userAuthority.userName')" prop="userName">
          <el-input
            v-model="temp.userName"
            :disabled="temp.userName !== 'admin' ? false : true"
            :placeholder="$t('userAuthority.userName')"
          />
        </el-form-item>
        <el-form-item :label="$t('userAuthority.password')" prop="password">
          <el-input v-model="temp.password" :placeholder="$t('userAuthority.password')" minlength="6" />
        </el-form-item>
        <el-form-item :label="$t('userAuthority.role')" prop="role">
          <el-select
            v-model="temp.role"
            class="filter-item"
            :disabled="temp.userName !== 'admin' ? false : true"
            :placeholder="$t('userAuthority.role')"
          >
            <el-option v-for="item in roles" :key="item.key" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('tenantManage.tenant')" prop="tenants" v-if="temp.userName !== 'admin' ? true : false">
          <el-checkbox-group v-model="temp.tempResources">
            <el-checkbox v-for="tenantKey in tenantList" :label="tenantKey" :key="tenantKey">{{
                tenantKey
              }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> {{ $t('common.cancel') }} </el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as user from '@/api/hippo4j-user';
import waves from '@/directive/waves';
import Pagination from '@/components/Pagination';
import * as tenantApi from '@/api/hippo4j-tenant';

export default {
  name: 'User',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        ROLE_ADMIN: 'danger',
        ROLE_MANAGE: 'warning',
        ROLE_USER: '',
      };
      return statusMap[status];
    },
  },
  data() {
    return {
      isTenantsShow: true,
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        userName: undefined,
      },
      roles: ['ROLE_USER', 'ROLE_MANAGE', 'ROLE_ADMIN'],
      tenantList: [],
      checkedCities: ['smo'],
      checkAll: false,
      isIndeterminate: true,
      cities: [],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create',
      },
      temp: {
        id: undefined,
        role: '',
        userName: '',
        password: '',
        permission: '',
        resources: [],
        tempResources: [],
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          role: '',
          userName: '',
          password: '',
          permission: '',
          resources: [],
          tempResources: [],
        }
      },
    };
  },
  computed:{
    rules(){
      return{
        role: [{ required: true, message: this.$t('message.requiredError'), trigger: 'change' }],
        userName: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        tenants: [{ required: false, message: this.$t('message.requiredError'), trigger: 'blur' }],
        password: [{ required: false, message: this.$t('message.requiredError'), trigger: 'blur' }],
      }
    },
  },
  created() {
    this.fetchData();
    this.initData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      user.getList(this.listQuery).then((response) => {
        this.total = response.total;
        this.list = response.records;
        this.listLoading = false;
      });
    },
    initData() {
      tenantApi.list({ size: this.size }).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.tenantList.push(records[i].tenantId);
        }
      });
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
          let resources = [];
          for (let i = 0; i < this.temp.tempResources.length; i++) {
            resources.push({
              resource: this.temp.tempResources[i],
              action: 'rw',
            });
          }
          this.temp.resources = resources;
          user.createUser(this.temp).then(() => {
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
      console.log(this.temp);
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          let resources = [];
          for (let i = 0; i < this.temp.tempResources.length; i++) {
            resources.push({
              resource: this.temp.tempResources[i],
              action: 'rw',
            });
          }
          const tempData = Object.assign({}, this.temp);
          tempData.resources = resources;
          user.updateUser(tempData).then(() => {
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
    openDelConfirm(name) {
      return this.$confirm(this.$t('message.deleteMessage', { name }), this.$t('common.hint'), {
        confirmButtonText: this.$t('common.ok'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      });
    },
    handleDelete(row) {
      this.openDelConfirm(row.userName).then(() => {
        user.deleteUser(row.userName).then((response) => {
          this.fetchData();
          this.$notify({
            title: this.$t('message.success'),
            message: this.$t('message.deleteSuccess'),
            type: 'success',
            duration: 2000,
          });
        });
      });
    },
  },
};
</script>
