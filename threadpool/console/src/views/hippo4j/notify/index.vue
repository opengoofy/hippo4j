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
      <el-select
        v-model="listQuery.tpId"
        :placeholder="$t('threadPool.threadPool')"
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
      <el-table-column :label="$t('tenantManage.tenant')" width="150">
        <template slot-scope="scope">{{ scope.row.tenantId }}</template>
      </el-table-column>
      <el-table-column :label="$t('projectManage.item')" width="260">
        <template slot-scope="scope">{{ scope.row.itemId }}</template>
      </el-table-column>
      <el-table-column :label="$t('threadPool.threadPool')" width="260">
        <template slot-scope="scope">{{ scope.row.tpId }}</template>
      </el-table-column>
      <el-table-column :label="$t('notifyAlarm.platform')" width="150">
        <template slot-scope="scope">
          <el-tag> {{ scope.row.platform }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('notifyAlarm.type')" width="150">
        <template slot-scope="scope">
          <el-tag type="success">{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('notifyAlarm.enabled')" width="100">
        <template slot-scope="scope">
          <el-tooltip :content="'Switch value: ' + scope.row.enable" placement="top">
            <el-switch
              v-model="scope.row.enable"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
              @change="changeEnable(scope.row)"
            >
            </el-switch>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column :label="$t('notifyAlarm.interval')" width="150">
        <template slot-scope="scope">{{ scope.row.interval | intervals }}</template>
      </el-table-column>
      <el-table-column :label="$t('notifyAlarm.receiver')" width="200">
        <template slot-scope="scope">{{ scope.row.receives | ellipsis }}</template>
      </el-table-column>
      <el-table-column :label="$t('common.createTime')" width="180">
        <template slot-scope="scope">{{ scope.row.gmtCreate }}</template>
      </el-table-column>
      <el-table-column :label="$t('common.updateTime')" width="180">
        <template slot-scope="scope">{{ scope.row.gmtModified }}</template>
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
            {{ $t('common.edit') }}
          </el-button>
          <el-button
            v-if="row.status !== 'deleted'"
            size="small"
            type="text"
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
        style="width: 500px; margin-left: 50px"
        :rules="rules"
        :model="temp"
        label-width="105px"
      >
        <el-form-item v-if="isEdit" :label="$t('tenantManage.tenant')" prop="tenantId">
          <el-select
            v-model="temp.tenantId"
            :placeholder="$t('message.selectMessage', { target: $t('tenantManage.tenant') })"
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
        <el-form-item v-if="isEdit" :label="$t('projectManage.item')" prop="itemId">
          <el-select
            v-model="temp.itemId"
            :placeholder="$t('message.selectMessage', { target: $t('projectManage.item') })"
            style="display: block"
            :disabled="dialogStatus === 'create' ? false : true"
            @change="itemTempSelectList()"
          >
            <el-option
              v-for="item in itemTempOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" :label="$t('threadPool.threadPool')" prop="tpId">
          <el-select
            v-model="temp.tpId"
            :placeholder="$t('threadPool.threadPool')"
            style="display: block"
            :disabled="dialogStatus === 'create' ? false : true"
          >
            <el-option
              v-for="item in threadPoolTempOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('notifyAlarm.platform')" prop="platform">
          <template>
            <div>
              <el-radio-group v-model="temp.platform">
                <el-radio-button label="DING">DING</el-radio-button>
                <el-radio-button label="LARK">LARK</el-radio-button>
                <el-radio-button label="WECHAT">WECHAT</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item :label="$t('notifyAlarm.enabled')" prop="enable">
          <template>
            <div>
              <el-radio-group v-model="temp.enable">
                <el-radio-button :label="1">{{ $t('notifyAlarm.enabling') }}</el-radio-button>
                <el-radio-button :label="0">{{ $t('notifyAlarm.disabling') }}</el-radio-button>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>

        <el-form-item :label="$t('notifyAlarm.type')" prop="configType">
          <!--          <el-tooltip :content="123">
            <i class="el-icon-question"/>
          </el-tooltip>-->
          <template>
            <div>
              <el-checkbox
                v-model="temp.configType"
                :disabled="dialogStatus === 'create' ? false : true"
                label="CONFIG"
                border
              />
              <el-checkbox
                v-model="temp.alarmType"
                :disabled="dialogStatus === 'create' ? false : true"
                label="ALARM"
                border
              />
            </div>
          </template>
        </el-form-item>
        <el-form-item :label="$t('notifyAlarm.interval')" prop="interval">
          <el-input-number
            v-model="temp.interval"
            :placeholder="$t('notifyAlarm.interval') + ' / Min'"
            controls-position="right"
            :min="0"
            :max="999999"
            :disabled="temp.alarmType === true ? false : true"
          />
        </el-form-item>

        <el-form-item :label="$t('notifyAlarm.token')" prop="secretKey">
          <el-input
            v-model="temp.secretKey"
            type="textarea"
            :placeholder="$t('message.inputMessage', { target: $t('notifyAlarm.token') })"
            show-word-limit
          />
        </el-form-item>

        <el-form-item :label="$t('notifyAlarm.receiver')" prop="receives">
          <el-input
            v-model="temp.receives"
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 4 }"
            :placeholder="$t('notifyAlarm.receiverTip')"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cleanForm()"> {{ $t('common.cancel') }} </el-button>
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
import * as itemApi from '@/api/hippo4j-item';
import * as tenantApi from '@/api/hippo4j-tenant';
import * as notifyApi from '@/api/hippo4j-notify';
import * as threadPoolApi from '@/api/hippo4j-threadPool';
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
      if (value.length > 22) {
        return value.slice(0, 22) + '...';
      }
      return value;
    },

    intervals(value) {
      if (value == null || value == '') {
        return '-';
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
        tpId: '',
        itemId: '',
      },
      isEdit: false,
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      tenantOptions: [],
      itemOptions: [],
      itemTempOptions: [],
      identifyOptions: [],
      threadPoolTempOptions: [],
      threadPoolOptions: [],
      platformTypes: [
        { key: 'DING', display_name: 'DING' },
        { key: 'LARK', display_name: 'LARK' },
        { key: 'WECHAT', display_name: 'WECHAT' },
      ],

      typeTypes: [
        { key: 'CONFIG', display_name: 'CONFIG' },
        { key: 'ALARM', display_name: 'ALARM' },
      ],

      enableTypes: [
        { key: 1, display_name: '启用' },
        { key: 0, display_name: '停用' },
      ],
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create',
      },
      temp: {
        id: undefined,
        tenantId: '',
        interval: undefined,
        configType: false,
        alarmType: false,
      },
      visible: true,
      size: 500,
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
        receives: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        secretKey: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        platform: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
        configType: [
          { required: true, message: this.$t('message.requiredError'), trigger: 'blur' },
        ],
        enable: [{ required: true, message: this.$t('message.requiredError'), trigger: 'blur' }],
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
    this.fetchData();
    // 初始化租户、项目
    this.tenantSelectList();
  },
  methods: {
    fetchData() {
      this.listQuery.tenantId = this?.tenantInfo?.tenantId || this.listQuery.tenantId
      let isAllTenant = this.listQuery.tenantId == i18nConfig.messages.zh.common.allTenant || this.listQuery.tenantId == i18nConfig.messages.en.common.allTenant
      this.listQuery.tenantId = isAllTenant ? undefined : this.listQuery.tenantId
      this.listLoading = true;
      notifyApi.list(this.listQuery).then((response) => {
        const { records } = response;
        const { total } = response;
        this.total = total;
        this.list = records;
        this.listLoading = false;
      }).catch((err) => {
        console.log("isAError", err)
      });
    },
    initSelect() {
      tenantApi.list({}).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.tenantOptions.push({
            key: records[i].tenantId,
            display_name: records[i].tenantId + ' ' + records[i].tenantName,
          });
        }
      });

      itemApi.list({}).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.itemOptions.push({
            key: records[i].itemId,
            display_name: records[i].itemId + ' ' + records[i].itemName,
          });
        }
      });

      threadPoolApi.list({}).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.threadPoolOptions.push({
            key: records[i].tpId,
            display_name: records[i].tpId,
          });
        }
      });
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        tenantName: '',
        tenantDesc: '',
        tenantId: '',
        interval: undefined,
        configType: false,
        alarmType: false,
      };
      this.isEdit = false;
    },
    handleCreate() {
      this.resetTemp();
      this.isEdit = true;
      this.dialogStatus = 'create';
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    cleanForm() {
      this.resetTemp();
      this.dialogFormVisible = false;
    },
    createData() {
      this.selectType(this.temp.configType, this.temp.alarmType);
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          notifyApi.created(this.temp).then(() => {
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
      if (this.temp.interval == null) {
        this.temp.interval = undefined;
      }
      this.dialogStatus = 'update';
      this.dialogFormVisible = true;
      this.isEdit = false;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
        this.selectType(this.temp.configType, this.temp.alarmType);
      });
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp);
          notifyApi.updated(tempData).then(() => {
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
      const name = '[' + row.tpId + ']-[' + row.platform + ']-[' + row.type + ']';
      this.openDelConfirm(name).then(() => {
        notifyApi.deleted(row).then((response) => {
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
    changeEnable(row) {
      notifyApi.enable(row).then((response) => {
        this.fetchData();
        this.$notify({
          title: this.$t('message.success'),
          message: this.$t('message.updateSuccess'),
          type: 'success',
          duration: 2000,
        });
      });
    },

    selectType(configType, alarmType) {
      if (configType && alarmType === false) {
        this.temp.interval = undefined;
        this.rules['interval'] = [];
      }
      if (configType != null && configType != undefined && alarmType === true) {
        this.rules['interval'] = [
          { required: true, message: this.$t('message.requiredError'), trigger: 'blur' },
        ];
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

    itemSelectList() {
      this.listQuery.tpId = null;

      this.threadPoolOptions = [];
      const itemId = { itemId: this.listQuery.itemId, size: this.size };
      threadPoolApi.list(itemId).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.threadPoolOptions.push({
            key: records[i].tpId,
            display_name: records[i].tpId,
          });
        }
      });
    },

    tenantTempSelectList() {
      this.itemTempOptions = [];
      this.threadPoolTempOptions = [];
      if (this.temp.itemId != null && Object.keys(this.temp.itemId).length != 0) {
        this.temp.itemId = null;
        if (this.temp.tpId != null && Object.keys(this.temp.tpId).length != 0) {
          this.temp.tpId = null;
        }
      }
      const tenantId = { tenantId: this.temp.tenantId, size: this.size };
      itemApi.list(tenantId).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.itemTempOptions.push({
            key: records[i].itemId,
            display_name: records[i].itemId + ' ' + records[i].itemName,
          });
        }
      });
    },

    itemTempSelectList() {
      this.threadPoolTempOptions = [];
      if (this.temp.tpId != null && Object.keys(this.temp.tpId).length != 0) {
        this.temp.tpId = null;
      }
      const query = { tenantId: this.temp.tenantId, itemId: this.temp.itemId, size: this.size };
      threadPoolApi.list(query).then((response) => {
        const { records } = response;
        for (let i = 0; i < records.length; i++) {
          this.threadPoolTempOptions.push({
            key: records[i].tpId,
            display_name: records[i].tpId,
          });
        }
      });
    },
  },
};
</script>
