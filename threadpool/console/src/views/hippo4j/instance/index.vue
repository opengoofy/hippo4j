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
      <el-select
        v-model="listQuery.tpId"
        :placeholder="$t('threadPool.threadPoolRequired')"
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
        style="margin-left: 10px"
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
      border
      :data="list"
      element-loading-text="Loading"
      :fit="true"
      max-height="714"
      highlight-current-row
      header-row-name="headerRowName"
    >
      <el-table-column :label="$t('common.num')" fixed="left" width="95">
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
      <el-table-column :label="$t('threadPool.coreSize')" width="100">
        <template slot-scope="scope">
          <el-link type="success" :underline="false">{{ scope.row.coreSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.maximumSize')" :width="$langMatch({ zh: '100', en: '150' }, 'auto')">
        <template slot-scope="scope">
          <el-link type="danger" :underline="false">{{ scope.row.maxSize }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.queueType')" width="260">
        <template slot-scope="scope">{{ scope.row.queueType | queueFilter }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.queueCapacity')" :width="$langMatch({ zh: '100', en: '160' }, 'auto')">
        <template slot-scope="scope">{{ scope.row.capacity }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.rejectedHandler')" width="260">
        <template slot-scope="scope">{{ scope.row.rejectedType | rejectedFilter }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.executionTimeout')" :width="$langMatch({ zh: '100', en: '160' }, 'auto')">
        <template slot-scope="scope">
          {{ scope.row.executeTimeOut }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.keepAliveTime')" :width="$langMatch({ zh: '100', en: '160' }, 'auto')">
        <template slot-scope="scope">
          {{ scope.row.keepAliveTime }}
        </template>
      </el-table-column>
      <!--<el-table-column label="是否报警" width="200">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isAlarm" active-color="#00A854" active-text="报警" :active-value="0"
                     inactive-color="#F04134" inactive-text="忽略" :inactive-value="1" @change="changeSwitch(scope.row)"/>
        </template>
      </el-table-column>-->
      <el-table-column
        :label="$t('common.operation')"
        fixed="right"
        width="120"
        align="center"
        class-name="small-padding fixed-width"
      >
        <!--<template slot-scope="{ row }">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              操作<i class="el-icon-arrow-down el-icon&#45;&#45;right"/>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="handleInfo(row)">查看</el-dropdown-item>
              <el-dropdown-item @click.native="handleShowStack(row)">堆栈</el-dropdown-item>
              <el-dropdown-item divided @click.native="handleUpdate(row)">编辑</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>-->
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleInfo(row)"> {{ $t('common.detail') }} </el-button>
          <el-button type="text" size="small" @click="handleShowStack(row)"> {{ $t('common.stack') }} </el-button>
          <el-button type="text" size="small" @click="handleUpdate(row)"> {{ $t('common.edit') }} </el-button>
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

    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="instanceDialogFormVisible"
      width="1000px"
    >
      <template>
        <el-descriptions class="margin-top" :title="$t('threadPoolInstance.basicInformation')" :column="3" :size="size" border>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.threadPoolID') }} </template>
            {{ runTimeTemp.tpId }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.activeID') }} </template>
            <el-tag :type="runTimeTemp.active | statusFilter">
              {{ runTimeTemp.active }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.runningState') }} </template>
            <el-tag>{{ runTimeTemp.state }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.instanceHost') }} </template>
            {{ runTimeTemp.host }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.instanceID') }} </template>
            {{ runTimeTemp.identify }}
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <br />
        <el-descriptions class="margin-top" :title="$t('threadPoolInstance.LoadInformation')" :column="3" :size="size" border>
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
        <el-descriptions class="margin-top" :title="$t('threadPoolInstance.threadInformation')" :column="3" :size="size" border>
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
        <el-descriptions class="margin-top" :title="$t('threadPoolInstance.queueInformation')" :column="3" :size="size" border>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPool.queueCapacity') }} </template>
            {{ runTimeTemp.queueCapacity }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.queueCount') }} </template>
            {{ runTimeTemp.queueSize }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.queueRemainingCapacity') }} </template>
            {{ runTimeTemp.queueRemainingCapacity }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.queueType') }} </template>
            {{ runTimeTemp.queueType }}
          </el-descriptions-item>
        </el-descriptions>
        <br />
        <br />
        <el-descriptions class="margin-top" :title="$t('threadPoolInstance.otherInformation')" :column="3" :size="size" border>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.totalTask') }} </template>
            {{ runTimeTemp.completedTaskCount }}
          </el-descriptions-item>
          <el-descriptions-item>
            <template slot="label"> {{ $t('threadPoolInstance.totalRejection') }} </template>
            <el-link type="danger" :underline="false">{{ runTimeTemp.rejectCount }}</el-link>
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
        style="width: 500px; margin-left: 50px"
        :rules="rules"
        :model="temp"
        :label-width="$langMatch({ zh: '80px', en: '150px' })"
      >
        <el-form-item :label="$t('threadPool.coreSize')" prop="coreSize">
          <el-input-number
            v-model="temp.coreSize"
            :placeholder="$t('threadPool.coreSize')"
            controls-position="right"
            :min="1"
            :max="9999"
          ></el-input-number>
        </el-form-item>
        <el-form-item :label="$t('threadPool.maximumSize')" prop="maxSize">
          <el-input-number
            v-model="temp.maxSize"
            :placeholder="$t('threadPool.maximumSize')"
            controls-position="right"
            :min="1"
            :max="9999"
          />
        </el-form-item>

        <el-form-item :label="$t('threadPool.queueType')" prop="queueType">
          <el-select
            v-model="temp.queueType"
            :placeholder="$t('threadPool.queueType')"
            :disabled="true"
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
        <el-form-item :label="$t('threadPool.queueCapacity')" prop="capacity">
          <el-input-number
            v-model="temp.capacity"
            :placeholder="$t('threadPool.queueCapacity')"
            controls-position="right"
            :min="0"
            :max="2147483647"
            :disabled="temp.queueType === 9 ? false : true"
          />
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.allowCoreThreadTimeOut')" prop="isAlarm">
          <template>
            <div>
              <el-radio-group v-model="temp.allowCoreThreadTimeOut">
                <el-radio-button :label="1">{{ $t('threadPool.timeout') }}</el-radio-button>
                <el-radio-button :label="0">{{ $t('threadPool.noTimeout') }}</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item :label="$t('threadPool.executionTimeout')" prop="executeTimeOut">
          <el-input-number
            v-model="temp.executeTimeOut"
            :placeholder="$t('threadPool.executionTimeoutUnit')"
            controls-position="right"
            :min="-1"
            :max="999999"
          />
        </el-form-item>
        <el-form-item :label="$t('threadPool.keepAliveTime')" prop="keepAliveTime">
          <el-input-number
            v-model="temp.keepAliveTime"
            :placeholder="$t('threadPool.keepAliveTimeUnit')"
            controls-position="right"
            :min="1"
            :max="999999"
          />
        </el-form-item>
        <el-form-item :label="$t('threadPool.rejectedHandler')" prop="rejectedType">
          <el-select
            v-model="temp.rejectedType"
            style="display: block"
            :placeholder="$t('threadPool.rejectedHandler')"
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
        <el-form-item v-if="isRejectShow" :label="$t('threadPool.customRejectedHandler')" prop="customRejectedType">
          <el-input
            v-model="temp.customRejectedType"
            :placeholder="$t('threadPool.customRejectedHandlerTip')"
            @input="onInput()"
          />
        </el-form-item>
        <el-form-item :label="$t('threadPoolInstance.changeAll')" prop="allUpdate">
          <el-switch v-model="allUpdate"> </el-switch>
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
        <el-button type="primary" @click="dialogPvVisible = false"> {{ $t('common.confirm') }} </el-button>
      </span>
    </el-dialog>
    <el-dialog title="Stack Info" :visible.sync="isStackShow" width="60%">
      <ul class="stack-info">
        <li v-for="item in stackInfo" :key="item.threadId">
          <p>
            "{{ item.threadName }}" #{{ item.threadId }} java.lang.Thread.State:
            {{ item.threadStatus }}
          </p>
          <ul>
            <li v-for="(tip, index) in item.threadStack" :key="index">at {{ tip }}</li>
          </ul>
        </li>
      </ul>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isStackShow = false"> <i class="el-icon-close" />{{ $t('common.close') }}</el-button>
        <el-button type="primary" @click="handleStackInfo">
          <i class="el-icon-refresh-right" />
          {{ $t('common.refresh') }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as tenantApi from '@/api/hippo4j-tenant';
import * as itemApi from '@/api/hippo4j-item';
import * as threadPoolApi from '@/api/hippo4j-threadPool';
import * as instanceApi from '@/api/hippo4j-instance';
import waves from '@/directive/waves';
import Pagination from '@/components/Pagination';
import axios from 'axios';
import { mapGetters } from 'vuex';
import { i18nConfig } from '@/locale/config'

export default {
  components: { Pagination },
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

    rejectedFilter(type) {
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
      list: [],
      listLoading: false,
      total: 0,
      listQuery: {
        current: 1,
        size: 10,
        itemId: '',
        tpId: '',
      },

      isStackShow: false, // 堆栈信息是否显示
      stackInfo: [], // 堆栈信息
      rowInfo: {}, // 行信息
      size: 500,
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      isRejectShow: false, // 是否显示spi拒绝策略
      instanceDialogFormVisible: false,
      tenantOptions: [],
      threadPoolOptions: [],
      itemOptions: [],
      queueTypeOptions: [
        { key: 1, display_name: 'ArrayBlockingQueue' },
        { key: 2, display_name: 'LinkedBlockingQueue' },
        { key: 3, display_name: 'LinkedBlockingDeque' },
        { key: 4, display_name: 'SynchronousQueue' },
        { key: 5, display_name: 'LinkedTransferQueue' },
        { key: 6, display_name: 'PriorityBlockingQueue' },
        {
          key: 9,
          display_name: 'ResizableLinkedBlockingQueue (动态修改队列大小)',
        },
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
      dialogStatus: '',
      textMap: {
        update: this.$t('common.edit'),
        create: this.$t('common.create'),
        info: this.$t('common.info'),
      },
      allUpdate: false,
      temp: {
        id: undefined,
        tenantId: '',
        itemId: '',
        rejectedType: null,
        customRejectedType: null,
      },
      runTimeTemp: {},
      tempRow: {},
      visible: true,
    };
  },
  computed:{
    ...mapGetters([
      'tenantInfo'
    ]),
    rules(){
      return{
        tenantId: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        itemId: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        tpId: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        coreSize: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        maxSize: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        queueType: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        executeTimeOut: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        keepAliveTime: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        isAlarm: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        capacity: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        capacityAlarm: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        livenessAlarm: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        rejectedType: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
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
    // this.fetchData()
    // 初始化项目
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? '' : this.listQuery.tenantId
    this.tenantSelectList();
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
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('tenantManage.tenant') }));
        return;
      }
      if (!this.listQuery.itemId) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('projectManage.item') }));
        return;
      }
      if (!this.listQuery.tpId) {
        this.$message.warning(this.$t('message.emptyWarning', { name: this.$t('threadPool.threadPool') }));
        return;
      }
      this.listLoading = true;
      const listArray = [this.listQuery.itemId, this.listQuery.tpId];
      instanceApi.list(listArray).then((response) => {
        this.list = response;
        this.listLoading = false;
      });
    },
    refreshData() {
      this.listQuery.tenantId = null;
      this.listQuery.itemId = null;
      this.listQuery.tpId = null;
      this.itemOptions = [];
      this.threadPoolOptions = [];
    },
    initSelect() {
      tenantApi
        .list({ size: this.size })
        .then((response) => {
          const { records } = response;
          for (let i = 0; i < records.length; i++) {
            this.tenantOptions.push({
              key: records[i].tenantId,
              display_name: records[i].tenantId + ' ' + records[i].tenantName,
            });
          }
        })
        .catch(() => {});
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
      this.$refs['dataForm']
        .validate((valid) => {
          if (valid) {
            threadPoolApi.created(this.temp).then(() => {
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
        })
        .catch(() => {});
    },
    // 打开弹框
    handleUpdate(row) {
      this.temp = Object.assign({}, row); // copy obj
      this.allUpdate = false;
      this.dialogStatus = 'update';
      const rejectedType = this.temp.rejectedType;
      const rejectTypeList = [1, 2, 3, 4, 5, 6];
      if (!rejectTypeList.includes(rejectedType)) {
        this.isRejectShow = true;
        this.temp.customRejectedType = this.temp.rejectedType;
        this.temp.rejectedType = 99;
      } else {
        this.isRejectShow = false;
      }
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    handleInfo(row) {
      this.dialogStatus = 'info';

      if (typeof row == 'undefined' || row == null) {
        row = this.tempRow;
      } else {
        this.tempRow = {
          identify: row.identify,
          clientAddress: row.clientAddress,
          clientBasePath: row.clientBasePath,
          tpId: row.tpId,
        };
      }
      this.refresh(row);
    },

    selectRejectedType(value) {
      if (value == 99) {
        this.isRejectShow = true;
      } else {
        this.isRejectShow = false;
      }
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
        .runState({ clientAddress: clientAddressStr, tpId: row.tpId })
        .then((response) => {
          this.instanceDialogFormVisible = true;
          this.runTimeTemp = response;
        })
        .catch((error) => {
          console.log(error);
          this.$message.error(this.$t('message.queryFailure'));
        });
    },
    handleShowStack(row) {
      this.rowInfo = row;
      this.handleStackInfo();
    },
    handleStackInfo() {
      const { clientAddress, tpId } = this.rowInfo;
      const clientBasePath = this.rowInfo.clientBasePath || '';

      let clientAddressStr = clientAddress + clientBasePath;
      threadPoolApi
        .runThreadState({ clientAddress: clientAddressStr, tpId: tpId })
        .then((response) => {
          const data = response;
          if (data && data.length != 0) {
            this.stackInfo = data;
            this.isStackShow = true;
          } else {
            this.$message.warning(this.$t('threadPoolInstance.stackRequestFail'));
          }
        })
        .catch((error) => {
          console.log(error);
          this.$message.error(this.$t('message.queryFailure'));
        });
    },
    // 修改操作
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (parseInt(this.temp.maxSize) < parseInt(this.temp.coreSize)) {
            this.$message({
              message: this.$t('threadPool.threadsNumErrorTip'),
              type: 'warning',
            });
            return;
          }
          let rejectedType = this.temp.rejectedType;
          const rejectTypeList = [1, 2, 3, 4, 5, 6];
          if (!rejectTypeList.includes(rejectedType)) {
            if (this.temp.customRejectedType != null) {
              this.temp.rejectedType = this.temp.customRejectedType;
            }
          }
          const tempData = Object.assign({}, this.temp);
          if (this.allUpdate != undefined && this.allUpdate != null && this.allUpdate) {
            tempData.identify = '';
          }
          instanceApi
            .updated(tempData)
            .then(() => {
              this.dialogFormVisible = false;
              this.$notify({
                title: this.$t('message.success'),
                message: this.$t('message.updateSuccess'),
                type: 'success',
                duration: 2000,
              });
              this.listLoading = true;
              setTimeout(() => {
                const listArray = [this.listQuery.itemId, this.listQuery.tpId];
                instanceApi.list(listArray).then((response) => {
                  this.list = response;
                });
                this.listLoading = false;
              }, 1500);
            })
            .catch(() => {});
        }
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

      this.itemOptions = [];
      this.threadPoolOptions = [];
      const tenantId = { tenantId: this.listQuery.tenantId, size: this.size };
      console.log("XXXXXXXXXXXXXXX", tenantId)
      itemApi
        .list(tenantId)
        .then((response) => {
          const { records = [] } = response || {};
          for (var i = 0; i < records.length; i++) {
            this.itemOptions.push({
              key: records[i].itemId,
              display_name: records[i].itemId + ' ' + records[i].itemName,
            });
          }
        })
        .catch(() => {});
    },

    itemSelectList() {
      this.listQuery.tpId = null;

      this.threadPoolOptions = [];
      const itemId = { itemId: this.listQuery.itemId, size: this.size };
      threadPoolApi
        .list(itemId)
        .then((response) => {
          const { records = [] } = response || {};
          for (var i = 0; i < records.length; i++) {
            this.threadPoolOptions.push({
              key: records[i].tpId,
              display_name: records[i].tpId,
            });
          }
        })
        .catch(() => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.headerRowName{

}
/* 滚动槽 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  border-radius: 5px;
  background: rgba(0, 0, 0, 0.06);
  -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.08);
}

/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 5px;
  background: rgba(0, 0, 0, 0.12);
  -webkit-box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
}

.stack-info {
  height: 400px;
  overflow: auto;

  & > li {
    margin-bottom: 24px;

    p:first-child {
      color: #0066ff;
      font-weight: 600;
      margin-top: 10px;
    }

    ul {
      margin-left: 30px;

      li {
        color: #fc5531;
        text-align: justify;
        margin: 10px auto;
      }
    }
  }
}
</style>
