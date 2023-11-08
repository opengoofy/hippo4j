<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
      @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <!--        <search id="header-search" class="right-menu-item" />-->

        <!-- <error-log class="errLog-container right-menu-item hover-effect"/> -->

        <!--        <screenfull id="screenfull" class="right-menu-item hover-effect" />-->

        <!--        <el-tooltip content="Global Size" effect="dark" placement="bottom">-->
        <!--          <size-select id="size-select" class="right-menu-item hover-effect" />-->
        <!--        </el-tooltip>-->

      </template>
      <el-select v-model="currentTenant" class="select-tenant" @change="changeTenant">
        <el-option v-for="item in tenantList" :key="item.tenantId" :label="item.resource" :value="item.tenantId">
        </el-option>
      </el-select>
      <langChange />
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img src="../../../public/hippo4j.gif" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/">
            <el-dropdown-item>{{ this.$t('menu.dashboard') }}</el-dropdown-item>
          </router-link>
          <el-dropdown-item divided>
            <span style="display:block;" @click="logout">{{ $t('system.logOut') }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import * as user from '@/api/hippo4j-user';
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import langChange from '@/locale/langChange'

export default {
  components: {
    Breadcrumb,
    Hamburger,
    langChange
  },
  data() {
    return {
      tenant: {
        action: '',
        resource: '',
        username: ''
      },
      currentTenant: undefined
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'tenantList',
      'tenantInfo'
    ])
  },
  mounted() {
    this.getTenantList()
  },

  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$cookie.delete('userName')
      this.$cookie.delete('tenantInfo')
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    },
    async getTenantList() {
      const userName = this.$cookie.get('userName')
      await user
        .getCurrentUser(userName)
        .then((response) => {
          const { resources } = response;
          let resourcesRes = resources.map((item) => {
            let query = {
              ...item,
              tenantId: item.resource,
            }
            return query
          })
          if (response.role == 'ROLE_ADMIN') {
            resourcesRes = [{
              action: "rw",
              resource: this.$t('common.allTenant'),
              username: userName,
              tenantId: this.$t('common.allTenant'),
            }, ...resourcesRes]
          }

          this.$store.dispatch('tenant/setTenantList', resourcesRes)
          this.tenant = JSON.parse(this.$cookie.get('tenantInfo')) || resourcesRes[0]
          this.$store.dispatch('tenant/setTenantInfo', resourcesRes[0])
          this.$cookie.set('tenantInfo', JSON.stringify(this.tenant));
          this.currentTenant = resourcesRes[0]?.tenantId
          // console.log('resourceRes:::', this.tenantInfo, this.tenantList)
        })
        .catch(() => { });

    },
    changeTenant(id) {
      const tenant = this.tenantList.find(item => item.tenantId === id)
      this.currentTenant = id
      this.$store.dispatch('tenant/setTenantInfo', tenant)
    }
  },
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    display: flex;

    &:focus {
      outline: none;
    }

    ::v-deep .el-input__inner {
      border: none;
      box-shadow: none;
    }

    .select-tenant {
      margin-right: 20px;
      border: 0;
      width: 150px;
    }


    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>