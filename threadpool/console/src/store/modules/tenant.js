import * as user from '@/api/hippo4j-user';

const state = {
  tenantList: [],
  tenantInfo: {}
}

const mutations = {
  SET_TENANT_LIST: (state, log) => {
    state.tenantList = log;
    console.log("sssssss", state.tenantList)
  },
  SET_GLOBAL_TENANT: (state, log) => {
    state.tenantInfo = log;
  }
}

const actions = {
  async getTenantList({commit}) {
    debugger
    const userName = this.$cookie.get('userName');
      user
        .getCurrentUser(userName)
        .then((response) => {
          const { resources } = response;
          if (response.role == 'ROLE_ADMIN') {
            resources.unshift({
              action: "rw",
              resource: this.$t('common.allTenant'),
              username: userName,
              tenantId: this.$t('common.allTenant'),
              index: 0,
            })
          }
          const resourcesRes = resources.map((item, index) => ({
            ...item,
            tenantId: item.resource,
            index: index,
          }))
          console.log("asasxasxas==========", resourcesRes)
          commit('SET_TENANT_LIST', resourcesRes)
          if (!state.tenantInfo) {
            commit('SET_GLOBAL_TENANT', resourcesRes[0])
          }
        })
        .catch(() => {});
  },
  
  setTenantList({commit}, log) {
    commit('SET_TENANT_LIST', log)
  },
  setTenantInfo({commit}, log) {
    commit('SET_GLOBAL_TENANT', log)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
