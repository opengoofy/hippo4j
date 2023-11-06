<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.itemId"
        :placeholder="$t('projectManage.itemRequired')"
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
      :data="list"
      element-loading-text="Loading"
      border
      fit
      max-height="714"
      highlight-current-row
    >
      <el-table-column :label="$t('common.num')" fixed width="95">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolInstance.instanceID')" width="260">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{ scope.row.identify }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolInstance.active')" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.active | statusFilter">
            {{ scope.row.active }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.coreSize')" width="120">
        <template slot-scope="scope">
          <el-link type="success" :underline="false">{{ scope.row.coreSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.maximumSize')" width="120">
        <template slot-scope="scope">
          <el-link type="danger" :underline="false">{{ scope.row.maximumSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.queueType')" width="260">
        <template slot-scope="scope">{{ scope.row.queueType }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.queueCapacity')" width="160">
        <template slot-scope="scope">{{ scope.row.queueCapacity }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.rejectedHandler')" width="260">
        <template slot-scope="scope">{{ scope.row.rejectedName }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.keepAliveTime')" width="100">
        <template slot-scope="scope">{{ scope.row.keepAliveTime }}</template>
      </el-table-column>
      <el-table-column
        :label="$t('common.operation')"
        width="90"
        align="center"
        fixed="right"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleInfo(row)">
            {{ $t('common.detail') }}
          </el-button>
          <el-button type="text" size="small" @click="handleUpdate(row)">
            {{ $t('common.edit') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="instanceDialogFormVisible"
      width="1000px"
    >
      <template>
        <el-descriptions
          class="margin-top"
          :title="$t('threadPoolInstance.LoadInformation')"
          :column="3"
          :size="size"
          border
        >
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.CurrentLoad') }} </template>
            {{ runTimeTemp.currentLoad }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.PeakLoad') }} </template>
            {{ runTimeTemp.peakLoad }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.RemainingMemory') }} </template>
            {{ runTimeTemp.freeMemory }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.MemoryRatio') }} </template>
            {{ runTimeTemp.memoryProportion }}
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <br />
        <el-descriptions
          class="margin-top"
          :title="$t('threadPoolInstance.threadInformation')"
          :column="3"
          :size="size"
          border
        >
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPool.coreSize') }} </template>
            {{ runTimeTemp.coreSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.currentSize') }} </template>
            {{ runTimeTemp.poolSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPool.maximumSize') }} </template>
            {{ runTimeTemp.maximumSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.activeSize') }} </template>
            {{ runTimeTemp.activeSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.largestSize') }} </template>
            {{ runTimeTemp.largestPoolSize }}
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <br />
        <el-descriptions
          class="margin-top"
          :title="$t('threadPoolInstance.queueInformation')"
          :column="3"
          :size="size"
          border
        >
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPool.queueCapacity') }} </template>
            {{ runTimeTemp.queueCapacity }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.queueCount') }} </template>
            {{ runTimeTemp.queueSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label">
              {{ $t('threadPoolInstance.queueRemainingCapacity') }}
            </template>
            {{ runTimeTemp.queueRemainingCapacity }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.queueType') }} </template>
            {{ runTimeTemp.queueType }}
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <br />
        <el-descriptions
          class="margin-top"
          :title="$t('threadPoolInstance.otherInformation')"
          :column="3"
          :size="size"
          border
        >
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.totalTask') }} </template>
            {{ runTimeTemp.completedTaskCount }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.lastUpdateTime') }} </template>
            {{ runTimeTemp.clientLastRefreshTime }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPool.rejectedHandler') }} </template>
            {{ runTimeTemp.rejectedName }}
          </el-descriptions-item>
        </el-descriptions>
      </template>
      <div slot="footer" class="dialog-footer">
        <el-button @click="instanceDialogFormVisible = false">
          <i class="el-icon-close" />
          {{ $t('common.close') }}
        </el-button>
        <el-button type="primary" @click="handleInfo()">
          <i class="el-icon-refresh-right" />
          {{ $t('common.refresh') }}
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        style="width: 500px; margin-left: 50px"
        label-width="120px"
      >
        <el-form-item :label="$t('threadPool.coreSize')" prop="coreSize">
          <template>
            <el-input-number
              v-model="temp.coreSize"
              controls-position="right"
              :min="1"
              :max="9999"
            ></el-input-number>
          </template>
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maximumSize">
          <template>
            <el-input-number
              v-model="temp.maximumSize"
              controls-position="right"
              :min="1"
              :max="9999"
            ></el-input-number>
          </template>
        </el-form-item>
        <el-form-item :label="$t('threadPool.keepAliveTime')" prop="keepAliveTime">
          <template>
            <el-input-number
              v-model="temp.keepAliveTime"
              placeholder="Time（秒）"
              controls-position="right"
              :min="1"
            ></el-input-number>
          </template>
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.changeAll')" prop="allUpdate">
          <el-switch v-model="temp.allUpdate"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> {{ $t('common.cancel') }} </el-button>
        <el-button type="primary" @click="updateData()"> {{ $t('common.confirm') }} </el-button>
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
  },
  data() {
    return {
      isRejectShow: false, // 是否显示spi拒绝策略
      list: [],
      listLoading: false,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        tpId: '',
        itemId: '',
        mark: 'Tomcat',
      },
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      isEditDisabled: false,
      runTimeTemp: {},
      tenantOptions: [],
      instanceDialogFormVisible: false,
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
        info: 'Info',
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
  computed:{
    ...mapGetters([
      'tenantInfo'
    ]),
    rules(){
      return{
        coreSize: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        maximumSize: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
      }
    },
  },
  watch: {
    tenantInfo(newVal, oldVal) {
      this.listQuery.tenantId = newVal.tenantId;
      this.fetchData()
    }
  },
  created() {
    // 初始化租户、项目
    this.tenantSelectList();
  },
  mounted() {
    this.isEditDisabled = localStorage.getItem('USER_ROLE') !== 'ROLE_ADMIN';
  },
  methods: {
    onInput() {
      this.$forceUpdate();
    },
    fetchData() {
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
      if (!this.listQuery.tenantId) {
        this.$message.warning(
          this.$t('message.emptyWarning', { name: this.$t('tenantManage.tenant') }),
        );
        return;
      }
      if (!this.listQuery.itemId) {
        this.$message.warning(
          this.$t('message.emptyWarning', { name: this.$t('projectManage.item') }),
        );
        return;
      }
      this.listLoading = true;
      threadPoolApi.listClient(this.listQuery).then((response) => {
        if (response == null) {
          this.listLoading = false;
          return;
        }
        const tempResp = response;
        const tempList = [];
        for (let i = 0; i < tempResp.length; i++) {
          const tempData = {};
          const tempResp0 = response[i];
          tempData.identify = tempResp0.identify;
          tempData.active = tempResp0.active;
          tempData.clientAddress = tempResp0.clientAddress;
          tempData.coreSize = tempResp0.coreSize;
          tempData.maximumSize = tempResp0.maximumSize;
          tempData.queueType = tempResp0.queueType;
          tempData.queueCapacity = tempResp0.queueCapacity;
          tempData.rejectedName = tempResp0.rejectedName;
          tempData.keepAliveTime = tempResp0.keepAliveTime;
          tempData.tenantId = tempResp0.tenantId;
          tempData.itemId = tempResp0.itemId;
          tempList.push(tempData);
        }
        this.list = tempList;
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
      this.temp = {
        id: undefined,
        tenantId: '',
        itemId: '',
        rejectedType: null,
        customRejectedType: null,
      };
    },
    handleCreate() {
      this.resetTemp();
      this.dialogStatus = 'create';
      this.dialogFormVisible = true;
      this.isRejectShow = false;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
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
      const typeList = [1, 2, 3, 4, 5, 6];
      if (!typeList.includes(rejectedType)) {
        this.isRejectShow = true;
        this.temp.customRejectedType = this.temp.rejectedType;
        this.temp.rejectedType = 99;
      } else {
        this.isRejectShow = false;
      }
      this.dialogStatus = 'update';
      this.dialogFormVisible = true;

      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (parseInt(this.temp.maximumSize) < parseInt(this.temp.coreSize)) {
            this.$message({
              message: this.$t('threadPool.threadsNumErrorTip'),
              type: 'warning',
            });
            return;
          }
          const clientAddressList = [];
          const tempData = {
            corePoolSize: this.temp.coreSize,
            itemId: this.temp.itemId,
            tenantId: this.temp.tenantId,
            maximumPoolSize: this.temp.maximumSize,
            keepAliveTime: this.temp.keepAliveTime,
            clientAddressList: clientAddressList,
          };
          if (!this.temp.allUpdate) {
            tempData.modifyAll = false;
            tempData.identify = this.temp.identify;
            clientAddressList[0] = this.temp.clientAddress;
          } else {
            tempData.modifyAll = true;
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
      threadPoolApi
        .webUpdatePool(updateData)
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
          this.$message.error(this.$t('message.queryFailure'));
        });
    },

    openDelConfirm(name) {
      return this.$confirm(this.$t('message.deleteMessage', { name }), this.$t('common.hint'), {
        confirmButtonText: this.$t('common.ok'),
        cancelButtonText: this.$t('common.cancel'),
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
      this.listQuery.tpId = null;

      this.temp.itemId = null;

      this.itemOptions = [];
      this.itemTempOptions = [];
      this.threadPoolOptions = [];
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

    tenantTempSelectList() {
      this.itemTempOptions = [];
      if (this.temp.itemId) {
        this.temp.itemId = null;
      }
      const tenantId = { tenantId: this.temp.tenantId, size: this.size };
      itemApi.list(tenantId).then((response) => {
        const { records } = response;
        for (let i = 0; i < records && records.length; i++) {
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
        const { records } = response;
        for (let i = 0; i < records && records.length; i++) {
          this.threadPoolOptions.push({
            key: records[i].tpId,
            display_name: records[i].tpId,
          });
        }
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
          if (response != null) {
            this.runTimeTemp = response;
          }
        })
        .catch((error) => {
          console.log(error);
          this.$message.error(this.$t('message.queryFailure'));
        });
    },
  },
};
</script>
