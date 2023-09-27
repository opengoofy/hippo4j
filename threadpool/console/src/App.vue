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
  mounted() {
    const userName = this.$cookie.get('userName');
    user
      .getCurrentUser(userName)
      .then((response) => {
        const { resources } = response;
        if (response.role == 'ROLE_ADMIN') {
          resources.unshift({
            action: "rw",
            resource: "所有租户",
            username: userName
          })
        }
        this.$store.dispatch('tenant/setTenantList', resources)
        if (!this.tenantInfo.resource) {
          this.$store.dispatch('tenant/setTenantInfo', resources[0])
          console.log("ssss", this.tenantInfo)
        }
      })
      .catch(() => {});
  }
};
</script>
