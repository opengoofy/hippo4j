<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.itemId"
        :placeholder="$t('projectManage.item')"
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
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        style="margin-left: 10px"
        icon="el-icon-refresh"
        @click="refreshData"
      >
        {{ $t('common.reset') }}
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
      <el-table-column fixed :label="$t('common.num')" width="80">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column :label="$t('projectManage.item')" width="260">
        <template slot-scope="scope">{{ scope.row.itemId }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.threadPool')" width="260">
        <template slot-scope="scope">{{ scope.row.tpId || '-' }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolInstance.instanceID')" width="280">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{ scope.row.identify || '-' }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.changeType')" width="110">
        <template slot-scope="scope"> {{ scope.row.type | modifyTypeFilter(that) }} </template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolInstance.changeAll')" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.modifyAll | modifyAllTagFilter">
            {{ scope.row.modifyAll | modifyAllFilter }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.modifiedBy')" width="120">
        <template slot-scope="scope"> {{ scope.row.modifyUser }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.auditStatus')" width="130">
        <template slot-scope="scope">{{
          scope.row.verifyStatus | verifyStatusFilter(that)
        }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.reviewer')" width="120">
        <template slot-scope="scope">{{ scope.row.verifyUser }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.submissionTime')" width="180">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPoolAudit.auditTime')" width="180">
        <template slot-scope="scope">{{ scope.row.gmtVerify }}</template>
      </el-table-column>
      <el-table-column
        :label="$t('common.operation')"
        fixed="right"
        width="90"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="applicationDetail(row)">
            {{ $t('common.audit') }}
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

    <!--thread pool manager-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="threadPoolManagerDialog">
      <el-form
        ref="dataForm"
        :model="temp"
        style="width: 500px; margin-left: 50px"
        label-width="140px"
      >
        <el-form-item :label="$t('threadPool.coreSize')" prop="corePoolSize">
          {{ detailInfo.corePoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maxSize">
          {{ detailInfo.maximumPoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.queueType')" prop="queueType">
          {{ detailInfo.queueType | queueTypeFilter }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.queueCapacity')" prop="capacity">
          {{ detailInfo.capacity }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.executionTimeout')" prop="executeTimeOut">
          {{ detailInfo.executeTimeOut }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.keepAliveTime')" prop="keepAliveTime">
          {{ detailInfo.keepAliveTime }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.isTimeout')" prop="allowCoreThreadTimeOut">
          {{ detailInfo.allowCoreThreadTimeOut | enableFilter(that) }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.isAlarm')" prop="isAlarm">
          {{ detailInfo.isAlarm | enableFilter(that) }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.activeAlarm')" prop="livenessAlarm">
          {{ detailInfo.livenessAlarm }}
        </el-form-item>

        <el-form-item :label="$t('threadPool.capacityAlarm')" prop="capacityAlarm">
          {{ detailInfo.capacityAlarm }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.rejectedHandler')" prop="rejectedType">
          {{ detailInfo.rejectedType | rejectedTypeFilter }}
        </el-form-item>
        <!-- <el-form-item v-if="isRejectShow" label="自定义拒绝策略" prop="customRejectedType">
          <el-input
            v-model="temp.customRejectedType"
            placeholder="请输入自定义 SPI 拒绝策略标识"
            @input="onInput()"
          />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :disabled="detailInfo.verifyStatus != 0" @click="reject(detailInfo)">
          {{ $t('threadPoolAudit.auditRejection') }}
        </el-button>
        <el-button
          :disabled="detailInfo.verifyStatus != 0"
          type="primary"
          @click="accept(detailInfo)"
        >
          {{ $t('threadPoolAudit.auditApproved') }}
        </el-button>
      </div>
    </el-dialog>

    <!-- thread pool instance-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="threadPoolInstanceDialog">
      <el-form
        ref="dataForm"
        style="width: 500px; margin-left: 50px"
        :model="temp"
        label-width="80px"
      >
        <el-form-item :label="$t('threadPool.coreSize')" prop="corePoolSize">
          {{ detailInfo.corePoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maximumPoolSize">
          {{ detailInfo.maximumPoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.queueType')" prop="queueType">
          {{ detailInfo.queueType | queueTypeFilter }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.queueCapacity')" prop="capacity">
          {{ detailInfo.capacity }}
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.allowCoreThreadTimeOut')" prop="isAlarm">
          {{ detailInfo.allowCoreThreadTimeOut | enableFilter(that) }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.executionTimeout')" prop="executeTimeOut">
          {{ detailInfo.executeTimeOut }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.keepAliveTime')" prop="keepAliveTime">
          {{ detailInfo.keepAliveTime }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.rejectedHandler')" prop="rejectedType">
          {{ detailInfo.rejectedType | rejectedTypeFilter }}
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.changeAll')" prop="allUpdate">
          <el-switch v-model="detailInfo.modifyAll"> </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :disabled="detailInfo.verifyStatus != 0" @click="reject(detailInfo)">
          {{ $t('threadPoolAudit.auditRejection') }}
        </el-button>
        <el-button
          type="primary"
          :disabled="detailInfo.verifyStatus != 0"
          @click="accept(detailInfo)"
        >
          {{ $t('threadPoolAudit.auditApproved') }}
        </el-button>
      </div>
    </el-dialog>

    <!--web thread pool-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="webThreadPoolDialog">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="110px">
        <el-form-item :label="$t('threadPool.coreSize')" prop="corePoolSize">
          {{ detailInfo.corePoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maximumPoolSize">
          {{ detailInfo.maximumPoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.keepAliveTime')" prop="keepAliveTime">
          {{ detailInfo.keepAliveTime }}
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.changeAll')" prop="modifyAll">
          <el-switch v-model="detailInfo.modifyAll"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :disabled="detailInfo.verifyStatus != 0" @click="reject(detailInfo)">
          {{ $t('threadPoolAudit.auditRejection') }}
        </el-button>
        <el-button
          :disabled="detailInfo.verifyStatus != 0"
          type="primary"
          @click="accept(detailInfo)"
        >
          {{ $t('threadPoolAudit.auditApproved') }}
        </el-button>
      </div>
    </el-dialog>

    <!-- adapter thread pool -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="adapterThreadPoolDialog">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="110px">
        <el-form-item label="mark" prop="corePoolSize">
          {{ detailInfo.mark }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.coreSize')" prop="corePoolSize">
          {{ detailInfo.corePoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maximumPoolSize">
          {{ detailInfo.maximumPoolSize }}
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.changeAll')" prop="allUpdate">
          <el-switch v-model="detailInfo.modifyAll"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :disabled="detailInfo.verifyStatus != 0" @click="reject(detailInfo)">
          {{ $t('threadPoolAudit.auditRejection') }}
        </el-button>
        <el-button
          :disabled="detailInfo.verifyStatus != 0"
          type="primary"
          @click="accept(detailInfo)"
        >
          {{ $t('threadPoolAudit.auditApproved') }}
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
import * as verifyApi from '@/api/verify';
import waves from '@/directive/waves';
import Pagination from '@/components/Pagination';
import { mapGetters } from 'vuex';
import { i18nConfig } from '@/locale/config'
import * as threadPoolApi from '@/api/hippo4j-threadPool';
import * as threadPoolAdapterApi from '@/api/hippo4j-adapterThreadPool';

export default {
  name: 'JobProject',
  components: { Pagination },
  directives: { waves },
  filters: {
    modifyAllTagFilter(status) {
      const statusMap = {
        true: 'success',
        false: 'danger',
      };
      return statusMap[status];
    },
    defaultExecuteTimeoutValue(value) {
      if (value == undefined || value == null) {
        return 0;
      }
      return value;
    },
    enableFilter(type, that) {
      if (1 == type) {
        return that.$t('common.yes');
      } else if (0 == type) {
        return that.$t('common.no');
      }
    },
    alarmFilter(type) {
      if (1 == type) {
        return '报警';
      } else if (0 == type) {
        return '不报警';
      }
    },
    queueTypeFilter(type) {
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
    modifyTypeFilter(type, that) {
      console.log(that);
      if (1 == type) {
        return that.$t('threadPoolAudit.manage');
      } else if (2 == type) {
        return that.$t('threadPoolAudit.instance');
      } else if (3 == type) {
        return that.$t('threadPoolAudit.container');
      } else if (4 == type) {
        return that.$t('threadPoolAudit.framework');
      }
    },
    verifyStatusFilter(type, that) {
      if (0 == type) {
        return that.$t('threadPoolAudit.unaudited');
      } else if (1 == type) {
        return that.$t('threadPoolAudit.auditApproved');
      } else if (2 == type) {
        return that.$t('threadPoolAudit.auditRejection');
      } else if (3 == type) {
        return that.$t('threadPoolAudit.expired');
      }
    },
    modifyAllFilter(type) {
      if (0 == type) {
        return 'N';
      } else if (1 == type) {
        return 'Y';
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
      detailInfo: {},
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      isEditDisabled: true,
      threadPoolManagerDialog: false,
      threadPoolInstanceDialog: false,
      webThreadPoolDialog: false,
      adapterThreadPoolDialog: false,
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
        1: this.$t('threadPoolAudit.threadPoolManage'),
        2: this.$t('threadPoolAudit.threadPoolInstance'),
        3: this.$t('threadPoolAudit.containerThreadPool'),
        4: this.$t('threadPoolAudit.frameworkThreadPool'),
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
      // filters中无法使用this来调用$t，通过参数that传入filters中
      that: this,
    };
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
    this.fetchData();
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
      this.listLoading = true;
      verifyApi.list(this.listQuery).then((response) => {
        const { records } = response;
        const { total } = response;
        this.total = total;
        this.list = records;
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
    refreshData() {
      this.listQuery.tenantId = null;
      this.listQuery.itemId = null;
      this.itemOptions = [];
    },
    closeDetailDialog(type) {
      if (type == 1) {
        this.threadPoolManagerDialog = false;
      } else if (type == 2) {
        this.threadPoolInstanceDialog = false;
      } else if (type == 3) {
        this.webThreadPoolDialog = false;
      } else if ((type = 4)) {
        this.adapterThreadPoolDialog = false;
      }
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
    accept(detailInfo) {
      this.openAcceptConfitm().then(() => {
        let acceptDetail = {};
        acceptDetail = detailInfo;
        acceptDetail.accept = true;
        if (detailInfo.type == 4) {
          acceptDetail.threadPoolKey = detailInfo.tpId;
        }
        verifyApi.verify(acceptDetail).then((response) => {
          this.fetchData();
          this.$notify({
            title: 'Success',
            message: 'accept Successfully',
            type: 'success',
            duration: 2000,
          });
          this.closeDetailDialog(detailInfo.type);
        });
      });
    },
    openAcceptConfitm() {
      return this.$confirm(this.$t('message.auditApprovedMessage'), this.$t('common.hint'), {
        confirmButtonText: this.$t('common.ok'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      });
    },
    openRejectConfirm() {
      return this.$confirm(this.$t('message.auditRejectionMessage'), this.$t('common.hint'), {
        confirmButtonText: this.$t('common.ok'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      });
    },
    reject(detailInfo) {
      let rejectDetail = {
        accept: false,
        id: detailInfo.id,
        type: detailInfo.type,
      };
      this.openRejectConfirm().then(() => {
        verifyApi.verify(rejectDetail).then((response) => {
          this.fetchData();
          this.$notify({
            title: 'Success',
            message: 'reject Successfully',
            type: 'success',
            duration: 2000,
          });
          this.closeDetailDialog(detailInfo.type);
        });
      });
    },

    applicationDetail(row) {
      let id = row.id;
      let type = row.type;
      if (type == 1) {
        this.dialogStatus = 1;
        this.threadPoolManagerDialog = true;
      } else if (type == 2) {
        this.dialogStatus = 2;
        this.threadPoolInstanceDialog = true;
      } else if (type == 3) {
        this.dialogStatus = 3;
        this.webThreadPoolDialog = true;
      } else if (type == 4) {
        this.dialogStatus = 4;
        this.adapterThreadPoolDialog = true;
      }
      verifyApi
        .applicationDetail(id)
        .then((response) => {
          if (response != null) {
            this.detailInfo = response;
            this.detailInfo.mark = row.mark;
            this.detailInfo.modifyAll = row.modifyAll;
            this.detailInfo.id = row.id;
            this.detailInfo.type = row.type;
            this.detailInfo.verifyStatus = row.verifyStatus;
            this.detailInfo.tenantId = row.tenantId;
            this.detailInfo.itemId = row.itemId;
            this.detailInfo.identify = row.identify;
          }
        })
        .catch((error) => {
          console.log(error);
          this.$message.error(this.$t('message.queryFailure'));
        });
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
      verifyApi.list(itemId).then((response) => {
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
