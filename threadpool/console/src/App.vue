<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import * as user from '@/api/hippo4j-user';
export default {
  name: 'App',
  computed: {
    ...mapGetters(['tenantInfo'])
  },
  async mounted() {
    const userName = this.$cookie.get('userName');
    await user
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
        this.$store.dispatch('tenant/setTenantList', resourcesRes)
        if (!this.tenantInfo.resource) {
          this.$store.dispatch('tenant/setTenantInfo', resourcesRes[0])
          console.log("ssss", this.tenantInfo)
        }
      })
      .catch(() => {});
  }
};
</script>
