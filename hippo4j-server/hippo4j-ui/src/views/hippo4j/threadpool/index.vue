<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.tenantId"
        placeholder="租户"
        style="width: 220px"
        filterable
        class="filter-item"
        @change="tenantSelectList()"
      >
        <el-option
          v-for="item in tenantOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.itemId"
        placeholder="项目"
        style="width: 220px"
        filterable
        class="filter-item"
        @change="itemSelectList()"
      >
        <el-option
          v-for="item in itemOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
      <el-select
        v-model="listQuery.tpId"
        placeholder="线程池"
        style="width: 220px"
        filterable
        class="filter-item"
      >
        <el-option
          v-for="item in threadPoolOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>

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
        :disabled="isEditDisabled"
      >
        添加
      </el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      element-loading-text="Loading"
      fit
      highlight-current-row
    >
      <el-table-column fixed label="序号" width="80">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="租户" width="150">
        <template slot-scope="scope">{{ scope.row.tenantId }}</template>
      </el-table-column>
      <el-table-column label="项目" width="260">
        <template slot-scope="scope">{{ scope.row.itemId }}</template>
      </el-table-column>
      <el-table-column label="线程池" width="260">
        <template slot-scope="scope">{{ scope.row.tpId }}</template>
      </el-table-column>
      <el-table-column label="核心线程" width="100">
        <template slot-scope="scope">
          <el-link type="success" :underline="false">{{ scope.row.coreSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="最大线程" width="100">
        <template slot-scope="scope">
          <el-link type="danger" :underline="false">{{ scope.row.maxSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="队列类型" width="260">
        <template slot-scope="scope">{{ scope.row.queueType | queueFilter }}</template>
      </el-table-column>
      <el-table-column label="队列容量" width="100">
        <template slot-scope="scope">{{ scope.row.capacity }}</template>
      </el-table-column>
      <el-table-column label="拒绝策略" width="200">
        <template slot-scope="scope">{{ scope.row.rejectedType | rejectedTypeFilter }}</template>
      </el-table-column>
      <el-table-column label="执行超时" width="100">
        <template slot-scope="scope">{{
          scope.row.executeTimeOut | defaultExecuteTimeoutValue
        }}</template>
      </el-table-column>
      <el-table-column label="是否报警" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.isAlarm"
            active-color="#00A854"
            :active-value="1"
            inactive-color="#F04134"
            :inactive-value="0"
            @change="changeAlarm(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="200">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="200">
        <template slot-scope="scope">{{ scope.row.gmtModified }}</template>
      </el-table-column>
      <el-table-column
        label="操作"
        fixed="right"
        width="90"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleUpdate(row)"> 编辑 </el-button>
          <el-button size="small" :disabled="isEditDisabled" type="text" @click="handleDelete(row)">
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
        style="width: 500px; margin-left: 50px"
        :rules="rules"
        :model="temp"
        label-width="80px"
      >
        <el-form-item v-if="isEdit" label="租户" prop="tenantId">
          <el-select
            v-model="temp.tenantId"
            placeholder="请选择租户"
            style="display: block"
            :disabled="dialogStatus === 'create' ? false : true"
            @change="tenantTempSelectList()"
          >
            <el-option
              v-for="item in tenantOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="项目" prop="itemId">
          <el-select
            v-model="temp.itemId"
            placeholder="请选择项目"
            style="display: block"
            :disabled="dialogStatus === 'create' ? false : true"
          >
            <el-option
              v-for="item in itemTempOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="isEdit" label="线程池" prop="tpId">
          <el-input
            v-model="temp.tpId"
            size="medium"
            placeholder="请输入线程池 "
            :disabled="dialogStatus === 'create' ? false : true"
          />
        </el-form-item>
        <el-form-item label="核心线程" prop="coreSize">
          <el-input-number
            v-model="temp.coreSize"
            placeholder="核心线程"
            controls-position="right"
            :min="1"
            :max="9999"
          />
        </el-form-item>
        <el-form-item label="最大线程" prop="maxSize">
          <el-input-number
            v-model="temp.maxSize"
            placeholder="最大线程"
            controls-position="right"
            :min="1"
            :max="9999"
          />
        </el-form-item>
        <el-form-item label="队列类型" prop="queueType">
          <el-select
            v-model="temp.queueType"
            placeholder="队列类型"
            style="display: block"
            @change="selectQueueType"
          >
            <el-option
              v-for="item in queueTypeOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="队列容量" prop="capacity">
          <el-input-number
            v-model="temp.capacity"
            placeholder="队列容量"
            controls-position="right"
            :min="0"
            :max="2147483647"
            :disabled="temp.queueType === 4 || temp.queueType === 5 ? true : false"
          />
        </el-form-item>
        <el-form-item label="执行超时" prop="executeTimeOut">
          <el-input-number
            v-model="temp.executeTimeOut"
            placeholder="执行超时（毫秒）"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="空闲回收" prop="keepAliveTime">
          <el-input-number
            v-model="temp.keepAliveTime"
            placeholder="空闲回收（秒）"
            controls-position="right"
            :min="1"
            :max="999999"
          />
        </el-form-item>
        <el-form-item label="是否超时" prop="allowCoreThreadTimeOut">
          <template>
            <div>
              <el-radio-group v-model="temp.allowCoreThreadTimeOut">
                <el-radio-button :label="1">超时</el-radio-button>
                <el-radio-button :label="0">不超时</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="是否报警" prop="isAlarm">
          <template>
            <div>
              <el-radio-group v-model="temp.isAlarm">
                <el-radio-button label="1">报警</el-radio-button>
                <el-radio-button label="0">不报警</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="活跃报警" prop="livenessAlarm">
          <template>
            <div>
              <el-radio-group v-model="temp.livenessAlarm">
                <el-radio-button label="0">不报警</el-radio-button>
                <el-radio-button label="60">60%</el-radio-button>
                <el-radio-button label="80">80%</el-radio-button>
                <el-radio-button label="90">90%</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>

        <el-form-item label="容量报警" prop="capacityAlarm">
          <template>
            <div>
              <el-radio-group v-model="temp.capacityAlarm">
                <el-radio-button label="0">不报警</el-radio-button>
                <el-radio-button label="60">60%</el-radio-button>
                <el-radio-button label="80">80%</el-radio-button>
                <el-radio-button label="90">90%</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="拒绝策略" prop="rejectedType">
          <el-select
            v-model="temp.rejectedType"
            style="display: block"
            placeholder="拒绝策略"
            @change="selectRejectedType"
          >
            <el-option
              v-for="item in rejectedOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isRejectShow" label="自定义拒绝策略" prop="customRejectedType">
          <el-input
            v-model="temp.customRejectedType"
            placeholder="请输入自定义 SPI 拒绝策略标识"
            @input="onInput()"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> 取消 </el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
          确认
        </el-button>
      </div>
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
import * as itemApi from '@/api/hippo4j-item';
import * as tenantApi from '@/api/hippo4j-tenant';
import * as threadPoolApi from '@/api/hippo4j-threadPool';
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
    defaultExecuteTimeoutValue(value) {
      if (value == undefined || value == null) {
        return 0;
      }
      return value;
    },
    queueFilter(type) {
      if ('1' == type) {
        return 'ArrayBlockingQueue';
      } else if ('2' == type) {
        return 'LinkedBlockingQueue';
      } else if ('3' == type) {
        return 'LinkedBlockingDeque';
      } else if ('4' == type) {
        return 'SynchronousQueue';
      } else if ('5' == type) {
        return 'LinkedTransferQueue';
      } else if ('6' == type) {
        return 'PriorityBlockingQueue';
      } else if ('9' == type) {
        return 'ResizableLinkedBlockingQueue';
      }
    },
    rejectedTypeFilter(type) {
      if ('1' == type) {
        return 'CallerRunsPolicy';
      } else if ('2' == type) {
        return 'AbortPolicy';
      } else if ('3' == type) {
        return 'DiscardPolicy';
      } else if ('4' == type) {
        return 'DiscardOldestPolicy';
      } else if ('5' == type) {
        return 'RunsOldestTaskPolicy';
      } else if ('6' == type) {
        return 'SyncPutQueuePolicy';
      } else {
        return 'CustomRejectedPolicy_' + type;
      }
    },
  },
  data() {
    return {
      isRejectShow: false, // 是否显示spi拒绝策略
      isEdit: false,
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        tpId: '',
        itemId: '',
        desc: true,
      },
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      isEditDisabled: false,
      dialogFormVisible: false,
      tenantOptions: [],
      threadPoolOptions: [],
      itemOptions: [],
      itemTempOptions: [],
      queueTypeOptions: [
        { key: 1, display_name: 'ArrayBlockingQueue' },
        { key: 2, display_name: 'LinkedBlockingQueue' },
        { key: 3, display_name: 'LinkedBlockingDeque' },
        { key: 4, display_name: 'SynchronousQueue' },
        { key: 5, display_name: 'LinkedTransferQueue' },
        { key: 6, display_name: 'PriorityBlockingQueue' },
        { key: 9, display_name: 'ResizableLinkedBlockingQueue (动态修改队列大小)' },
      ],
      rejectedOptions: [
        { key: 1, display_name: 'CallerRunsPolicy' },
        { key: 2, display_name: 'AbortPolicy' },
        { key: 3, display_name: 'DiscardPolicy' },
        { key: 4, display_name: 'DiscardOldestPolicy' },
        { key: 5, display_name: 'RunsOldestTaskPolicy' },
        { key: 6, display_name: 'SyncPutQueuePolicy' },
        { key: 99, display_name: 'CustomRejectedPolicy（自定义 SPI 策略）' },
      ],
      alarmTypes: [
        { key: 1, display_name: '报警' },
        { key: 0, display_name: '不报警' },
      ],
      allowCoreThreadTimeOutTypes: [
        { key: 1, display_name: '超时' },
        { key: 0, display_name: '不超时' },
      ],
      size: 500,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create',
      },
      rules: {
        tenantId: [{ required: true, message: 'this is required', trigger: 'blur' }],
        itemId: [{ required: true, message: 'this is required', trigger: 'blur' }],
        tpId: [{ required: true, message: 'this is required', trigger: 'blur' }],
        coreSize: [{ required: true, message: 'this is required', trigger: 'blur' }],
        maxSize: [
          { required: true, message: 'this is required', trigger: 'blur' },
          // {
          //   validator: (rule, value, callback) => {
          //     if (parseInt(value) < parseInt(this.temp.coreSize)) {
          //       console.log(value);
          //       console.log(this.temp.coreSize);
          //       callback('最大线程必须大于等于核心线程');
          //     }
          //     callback();
          //   },
          // },
        ],
        queueType: [{ required: true, message: 'this is required', trigger: 'blur' }],
        allowCoreThreadTimeOut: [{ required: true, message: 'this is required', trigger: 'blur' }],
        keepAliveTime: [{ required: true, message: 'this is required', trigger: 'blur' }],
        isAlarm: [{ required: true, message: 'this is required', trigger: 'blur' }],
        capacityAlarm: [{ required: true, message: 'this is required', trigger: 'blur' }],
        livenessAlarm: [{ required: true, message: 'this is required', trigger: 'blur' }],
        rejectedType: [{ required: true, message: 'this is required', trigger: 'blur' }],
        capacity: [{ required: true, message: 'this is required', trigger: 'blur' }],
        executeTimeOut: [{ required: true, message: 'this is required', trigger: 'blur' }],
      },
      temp: {
        id: undefined,
        tenantId: '',
        itemId: '',
        rejectedType: null,
        customRejectedType: null,
        coreSize: 4,
        maxSize: 8,
      },
      visible: true,
    };
  },
  created() {
    this.fetchData();
    // 初始化租户、项目
    this.initSelect();
  },
  mounted() {
    this.isEditDisabled = localStorage.getItem('USER_ROLE') !== 'ROLE_ADMIN';
  },
  methods: {
    onInput() {
      this.$forceUpdate();
    },
    fetchData() {
      this.listLoading = true;
      threadPoolApi.list(this.listQuery).then((response) => {
        const { records } = response;
        const { total } = response;
        this.total = total;
        this.list = records;
        this.listLoading = false;
      });
    },
    changeAlarm(row) {
      threadPoolApi.alarmEnable(row).then(() => {
        this.fetchData();
        this.$notify({
          title: 'Success',
          message: 'Update Successfully',
          type: 'success',
          duration: 2000,
        });
      });
    },
    initSelect() {
      tenantApi.list({ size: this.size }).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.tenantOptions.push({
            key: records[i].tenantId,
            display_name: records[i].tenantId + ' ' + records[i].tenantName,
          });
        }
      });
    },
    resetTemp() {
      this.isRejectShow = false;
      this.isEdit = false;
      this.temp = {
        id: undefined,
        tenantId: '',
        itemId: '',
        rejectedType: null,
        customRejectedType: null,
        isAlarm: '',
        allowCoreThreadTimeOut: '',
        livenessAlarm: '',
        capacityAlarm: '',
      };
    },
    handleCreate() {
      this.resetTemp();
      this.isEdit = true;
      this.temp.coreSize = 4;
      this.temp.maxSize = 8;
      this.temp.queueType = 9;
      this.temp.keepAliveTime = 9999;
      this.temp.capacity = 4096;
      this.temp.executeTimeOut = 0;
      this.temp.isAlarm = '1';
      this.temp.allowCoreThreadTimeOut = '1';
      this.temp.livenessAlarm = '80';
      this.temp.capacityAlarm = '80';
      this.temp.rejectedType = 2;
      this.dialogStatus = 'create';
      this.dialogFormVisible = true;
      this.isRejectShow = false;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (parseInt(this.temp.maxSize) < parseInt(this.temp.coreSize)) {
          this.$message({
            message: '最大线程必须大于等于核心线程',
            type: 'warning',
          });
          return;
        }
        if (valid) {
          if (this.isRejectShow) {
            if (this.temp.customRejectedType == null) {
              this.temp.rejectedType = 2;
            } else {
              this.temp.rejectedType = this.temp.customRejectedType;
            }
          }
          threadPoolApi.created(this.temp).then(() => {
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
      let rejectedType = this.temp.rejectedType;
      if (
        rejectedType != 1 &&
        rejectedType != 2 &&
        rejectedType != 3 &&
        rejectedType != 4 &&
        rejectedType != 5 &&
        rejectedType != 6
      ) {
        this.isRejectShow = true;
        this.temp.customRejectedType = this.temp.rejectedType;
        this.temp.rejectedType = 99;
      } else {
        this.isRejectShow = false;
      }
      this.dialogStatus = 'update';
      this.dialogFormVisible = true;
      this.isEdit = false;

      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (parseInt(this.temp.maxSize) < parseInt(this.temp.coreSize)) {
            this.$message({
              message: '最大线程必须大于等于核心线程',
              type: 'warning',
            });
            return;
          }
          let rejectedType = this.temp.rejectedType;
          if (
            rejectedType != 1 &&
            rejectedType != 2 &&
            rejectedType != 3 &&
            rejectedType != 4 &&
            rejectedType != 5 &&
            rejectedType != 6
          ) {
            if (this.temp.customRejectedType != null) {
              this.temp.rejectedType = this.temp.customRejectedType;
            }
          }
          const tempData = Object.assign({}, this.temp);
          threadPoolApi.updated(tempData).then(() => {
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
      const role = localStorage.getItem('USER_ROLE') === 'ROLE_ADMIN' ? true : false;
      if (!role) {
        this.$message.error('请联系管理员删除');
        return;
      }

      this.openDelConfirm(row.tpId).then(() => {
        threadPoolApi.deleted(row).then((response) => {
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
    selectQueueType(value) {
      if (value === 4) {
        this.temp.capacity = 0;
      } else if (value === 5) {
        this.temp.capacity = 2147483647;
      }
    },

    tenantSelectList() {
      this.listQuery.itemId = null;
      this.listQuery.tpId = null;

      this.temp.itemId = null;

      this.itemOptions = [];
      this.itemTempOptions = [];
      this.threadPoolOptions = [];
      const tenantId = { tenantId: this.listQuery.tenantId, size: this.size };
      itemApi.list(tenantId).then((response) => {
        const { records = [] } = response;
        for (let i = 0; i < records.length; i++) {
          this.itemOptions.push({
            key: records[i].itemId,
            display_name: records[i].itemId + ' ' + records[i].itemName,
          });
        }
      });
    },

    tenantTempSelectList() {
      this.itemTempOptions = [];
      if (this.temp.itemId) {
        this.temp.itemId = null;
      }
      const tenantId = { tenantId: this.temp.tenantId, size: this.size };
      itemApi.list(tenantId).then((response) => {
        const { records = [] } = response;
        for (let i = 0; i < records.length; i++) {
          this.itemTempOptions.push({
            key: records[i].itemId,
            display_name: records[i].itemId + ' ' + records[i].itemName,
          });
        }
      });
    },

    itemSelectList() {
      this.listQuery.tpId = null;

      this.threadPoolOptions = [];
      const itemId = { itemId: this.listQuery.itemId, size: this.size };
      threadPoolApi.list(itemId).then((response) => {
        const { records = [] } = response;
        for (let i = 0; i < records.length; i++) {
          this.threadPoolOptions.push({
            key: records[i].tpId,
            display_name: records[i].tpId,
          });
        }
      });
    },

    selectRejectedType(value) {
      this.isRejectShow = value === 99 ? true : false;
    },
  },
};
</script>
