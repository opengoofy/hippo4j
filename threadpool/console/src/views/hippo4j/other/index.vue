<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.mark"
        placeholder="线程池类型"
        style="width: 260px"
        filterable
        class="filter-item"
      >
        <el-option
          v-for="item in typeOptions"
          :key="item.key"
          :label="item.display_name"
          :value="item.key"
        />
      </el-select>
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
        v-model="listQuery.threadPoolKey"
        placeholder="线程池标识"
        style="width: 220px"
        filterable
        class="filter-item"
      >
        <el-option
          v-for="item in threadPoolKeyOptions"
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
        v-waves
        class="filter-item"
        type="primary"
        style="margin-left: 10px"
        icon="el-icon-refresh"
        @click="refreshData"
      >
        重置
      </el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      fit
      border
      max-height="714"
      highlight-current-row
    >
      <el-table-column label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="实例标识" width="260">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{ scope.row.identify }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="Active">
        <template slot-scope="scope">
          <el-tag :type="scope.row.active | statusFilter">
            {{ scope.row.active }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="线程池标识">
        <template slot-scope="scope">
          {{ scope.row.threadPoolKey }}
        </template>
      </el-table-column>
      <el-table-column label="核心线程">
        <template slot-scope="scope">
          <el-link type="success" :underline="false">{{ scope.row.coreSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="最大线程">
        <template slot-scope="scope">
          <el-link type="danger" :underline="false">{{ scope.row.maximumSize }}</el-link>
        </template>
      </el-table-column>
      <!--<el-table-column label="队列类型" >
        <template slot-scope="scope">{{ scope.row.queueType }}</template>
      </el-table-column>
      <el-table-column label="队列容量" >
        <template slot-scope="scope">{{ scope.row.queueCapacity }}</template>
      </el-table-column>-->
      <el-table-column label="操作" width="180" class-name="small-padding fixed-width">
        <template slot-scope="{ row }">
          <!--<el-dropdown trigger="click">
            <span class="el-dropdown-link">
              操作<i class="el-icon-arrow-down el-icon&#45;&#45;right"/>
            </span>
            <el-dropdown-menu slot="dropdown">
              &lt;!&ndash;<el-dropdown-item @click.native="handleInfo(row)">查看</el-dropdown-item>&ndash;&gt;
              <el-dropdown-item @click.native="handleUpdate(row)">编辑</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>-->
          <el-button type="text" size="small" @click="handleUpdate(row)"> 编辑 </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="110px"
      >
        <el-form-item label="核心线程" prop="coreSize">
          <template>
            <el-input-number
              v-model="temp.coreSize"
              controls-position="right"
              :min="1"
              :max="9999"
            ></el-input-number>
          </template>
        </el-form-item>
        <el-form-item label="最大线程" prop="maximumSize">
          <template>
            <el-input-number
              v-model="temp.maximumSize"
              controls-position="right"
              :min="1"
              :max="9999"
            ></el-input-number>
          </template>
        </el-form-item>
        <el-form-item label="全部修改" prop="allUpdate">
          <el-switch v-model="temp.allUpdate"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> 取消 </el-button>
        <el-button type="primary" @click="updateData()"> 确认 </el-button>
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
import * as threadPoolAdapterApi from '@/api/hippo4j-adapterThreadPool';
import waves from '@/directive/waves';
import { mapGetters } from 'vuex';
import { i18nConfig } from '@/locale/config'

export default {
  name: 'JobProject',
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        DEV: 'info',
        TEST: 'success',
        UAT: 'warning',
        PROD: 'danger',
      };
      return statusMap[status];
    },
    data() {
      return {
        isRejectShow: false, // 是否显示spi拒绝策略
        list: null,
        listLoading: false,
        total: 0,
        listQuery: {
          itemId: '',
          mark: '',
          tenantId: '',
          threadPoolKey: '',
        },
        pluginTypeOptions: ['reader', 'writer'],
        dialogPluginVisible: false,
        pluginData: [],
        dialogFormVisible: false,
        runTimeTemp: {},
        typeOptions: [
          { key: 'Dubbo', display_name: 'Dubbo' },
          { key: 'RocketMQ', display_name: 'RocketMQ' },
          { key: 'RocketMQSpringCloudStream', display_name: 'RocketMQSpringCloudStream' },
          { key: 'RabbitMQ', display_name: 'RabbitMQ' },
          { key: 'RabbitMQSpringCloudStream', display_name: 'RabbitMQSpringCloudStream' },
        ],
        tenantOptions: [],
        instanceDialogFormVisible: false,
        threadPoolOptions: [],
        itemOptions: [],
        threadPoolKeyOptions: [],
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
          coreSize: [{ required: true, message: 'this is required', trigger: 'blur' }],
          maximumSize: [
            { required: true, message: 'this is required', trigger: 'blur' },
            {
              validator: (rule, value, callback) => {
                if (parseInt(value) < parseInt(this.temp.coreSize)) {
                  callback('最大线程必须大于等于核心线程');
                }
                callback();
              },
            },
          ],
        },
        temp: {
          id: undefined,
          tenantId: '',
          itemId: '',
          rejectedType: null,
          allUpdate: '1',
          customRejectedType: null,
        },
        visible: true,
      };
    },
    computed: {
    ...mapGetters([
      'tenantInfo'
    ]),
  },
    created() {
      // 初始化租户、项目
      this.initSelect();
    },
    methods: {
      onInput() {
        this.$forceUpdate();
      },
      fetchData() {
        this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
        let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
        this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
        if (!this.listQuery.mark) {
          this.$message.warning('线程池类型不允许为空');
          return;
        }
        if (!this.listQuery.tenantId) {
          this.$message.warning('租户不允许为空');
          return;
        }
        if (!this.listQuery.itemId) {
          this.$message.warning('项目不允许为空');
          return;
        }
        if (!this.listQuery.threadPoolKey) {
          this.$message.warning('线程池标识不允许为空');
          return;
        }
        this.listLoading = true;
        threadPoolAdapterApi.list(this.listQuery).then((response) => {
          if (response == null) {
            this.listLoading = false;
          }
          this.list = response;
          this.listLoading = false;
        });
      },
    },
  },
  computed: {
    ...mapGetters([
      'tenantInfo'
    ]),
  },
  watch: {
    tenantInfo(newVal, oldVal) {
      this.listQuery.tenantId = newVal.tenantId;
      this.fetchData()
    }
  },
  created() {
    // 初始化租户、项目
    this.initSelect();
  },
  methods: {
    onInput() {
      this.$forceUpdate();
    },
    fetchData() {
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
      if (!this.listQuery.mark) {
        this.$message.warning('线程池类型不允许为空');
        return;
      }
      if (!this.listQuery.tenantId) {
        this.$message.warning('租户不允许为空');
        return;
      }
      if (!this.listQuery.itemId) {
        this.$message.warning('项目不允许为空');
        return;
      }
      if (!this.listQuery.threadPoolKey) {
        this.$message.warning('线程池标识不允许为空');
        return;
      }
      this.listLoading = true;
      threadPoolAdapterApi.list(this.listQuery).then((response) => {
        if (response == null) {
          this.listLoading = false;
        }
        this.list = response;
        this.listLoading = false;
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
      this.temp = {
        id: undefined,
        tenantId: '',
        itemId: '',
        rejectedType: null,
        customRejectedType: null,
      };
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const clientAddressList = [];
          const tempData = {
            mark: this.listQuery.mark,
            tenant: this.listQuery.tenantId,
            item: this.listQuery.itemId,
            threadPoolKey: this.temp.threadPoolKey,
            identify: this.temp.identify,
            clientAddressList: clientAddressList,
            corePoolSize: this.temp.coreSize,
            maximumPoolSize: this.temp.maximumSize,
          };
          if (
            this.temp.allUpdate === '0' ||
            this.temp.allUpdate == undefined ||
            this.temp.allUpdate == null
          ) {
            clientAddressList[0] = this.temp.clientAddress;
          } else {
            for (let i = 0; i < this.list.length; i++) {
              if (this.list[i] != null) {
                clientAddressList[i] = this.list[i].clientAddress;
              }
            }
          }
          this.updateExecute(tempData);
        }
      });
    },

    updateExecute(updateData) {
      threadPoolAdapterApi
        .updatePool(updateData)
        .then((response) => {
          this.dialogFormVisible = false;
          this.$notify({
            title: 'Success',
            message: 'Update Successfully',
            type: 'success',
            duration: 2000,
          });
          this.fetchData();
        })
        .catch((error) => {
          console.log(error);
          this.$message.error('修改线程池失败');
        });
    },
    openDelConfirm(name) {
      return this.$confirm(`此操作将删除 ${name}, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
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
      this.listQuery.threadPoolKey = null;

      this.temp.itemId = null;

      this.itemOptions = [];
      this.itemTempOptions = [];
      this.threadPoolKeyOptions = [];
      const tenantId = { tenantId: this.listQuery.tenantId, size: this.size };
      itemApi.list(tenantId).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.itemOptions.push({
            key: records[i].itemId,
            display_name: records[i].itemId + ' ' + records[i].itemName,
          });
        }
      });
    },

    itemSelectList() {
      this.listQuery.tpId = null;

      this.threadPoolKeyOptions = [];
      const data = {
        mark: this.listQuery.mark,
        tenantId: this.listQuery.tenantId,
        itemId: this.listQuery.itemId,
      };
      threadPoolAdapterApi.listKey(data).then((response) => {
        for (let i = 0; i < response.length; i++) {
          this.threadPoolKeyOptions.push({
            key: response[i],
            display_name: response[i],
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
    selectRejectedType(value) {
      if (value == 99) {
        this.isRejectShow = true;
      } else {
        this.isRejectShow = false;
      }
    },

    handleInfo(row) {
      this.instanceDialogFormVisible = true;
      this.dialogStatus = 'info';

      if (typeof row == 'undefined' || row == null) {
        row = this.tempRow;
      } else {
        this.tempRow = {
          clientAddress: row.clientAddress,
        };
      }
      this.refresh(row);
    },

    refreshData() {
      this.listQuery.mark = null;
      this.listQuery.tenantId = null;
      this.listQuery.itemId = null;
      this.listQuery.threadPoolKey = null;
      this.itemOptions = [];
      this.threadPoolKeyOptions = [];
    },

    refresh(row) {
      let clientAddressStr = '';
      const clientAddress = row.clientAddress;
      let clientBasePath = row.clientBasePath;
      if (clientBasePath != null) {
        clientAddressStr = clientAddress + clientBasePath;
      } else {
        clientAddressStr = clientAddress;
      }
      threadPoolApi
        .webBaseState({ clientAddress: clientAddressStr })
        .then((response) => {
          this.runTimeTemp = response;
        })
        .catch((error) => {
          console.log(error);
          this.$message.error('查询失败，请尝试刷新页面');
        });
    },
  },
};
</script>
