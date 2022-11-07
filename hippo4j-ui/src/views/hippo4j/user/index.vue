<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.userName"
        placeholder="用户名"
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
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >
        添加
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
      <el-table-column label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="用户名">
        <template slot-scope="scope">{{ scope.row.userName }}</template>
      </el-table-column>
      <el-table-column label="角色">
        <template slot-scope="scope">
          <span>
            <el-tag :type="scope.row.role | statusFilter">{{ scope.row.role }}</el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column>
      <el-table-column label="修改时间">
        <template slot-scope="scope">{{ scope.row.gmtModified }}</template>
      </el-table-column>
      <el-table-column label="操作" class-name="small-padding fixed-width">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleUpdate(row)"> 编辑 </el-button>
          <el-button
            v-if="row.status !== 'deleted'"
            size="small"
            type="text"
            @click="handleDelete(row)"
          >
            删除
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
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="temp.userName" placeholder="用户名" />
        </el-form-item>
        <el-form-item label="密  码" prop="password">
          <el-input v-model="temp.password" placeholder="密码" minlength="6" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="temp.role" class="filter-item" placeholder="角色类型">
            <el-option v-for="item in roles" :key="item.key" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> 取消 </el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as user from '@/api/hippo4j-user';
import waves from '@/directive/waves';
import Pagination from '@/components/Pagination';

export default {
  name: 'User',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        ROLE_ADMIN: 'danger',
        ROLE_USER: '',
      };
      return statusMap[status];
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
        userName: undefined,
      },
      roles: ['ROLE_USER', 'ROLE_ADMIN'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create',
      },
      rules: {
        role: [{ required: true, message: 'role is required', trigger: 'change' }],
        userName: [{ required: true, message: 'userName is required', trigger: 'blur' }],
        password: [{ required: false, message: 'password is required', trigger: 'blur' }],
      },
      temp: {
        id: undefined,
        role: '',
        userName: '',
        password: '',
        permission: '',
      },
      resetTemp() {
        this.temp = this.$options.data().temp;
      },
    };
  },
  created() {
    this.fetchData();
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
          user.createUser(this.temp).then(() => {
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
          user.updateUser(tempData).then(() => {
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
      return this.$confirm(`此操作将删除 ${name}, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      });
    },
    handleDelete(row) {
      this.openDelConfirm(row.userName).then(() => {
        user.deleteUser(row.userName).then((response) => {
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
