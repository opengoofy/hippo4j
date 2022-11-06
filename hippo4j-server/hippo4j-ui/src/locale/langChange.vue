<template>
  <div class="lang-drop-wrap">
    <el-dropdown @command="selectedLang" trigger="click">
      <span class="el-dropdown-link">
        {{ currentLangName }}
        <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="item in langSelectList"
          :key="item.lang"
          :command="item.lang"
        >
          <span class="dropdown-item-text" :data-active="item.lang === currentLang">{{ item.name }}</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>
<script>
import { langSelectList } from './config'
export default {
  data() {
    return {
      langSelectList: langSelectList(),
      currentLang: ''
    }
  },
  mounted() {
    const lang = this.$i18n.locale
    this.currentLang = lang || null
  },
  computed: {
    currentLangName() {
      const langItem = this.langSelectList.find(item => item.lang === this.currentLang)
      return langItem?.name || '选择语言'
    }
  },
  methods: {
    // 选择语言
    selectedLang(value) {
      this.currentLang = value
      this.$i18n.locale = value
      localStorage.setItem('locale_lang', value)
    }
  }
}
</script>
<style lang="scss" scoped>
  .lang-drop-wrap{
    height: 100%;
    margin-right: 15px;
    .el-dropdown-link{
      cursor: pointer;
    }
  }
  .dropdown-item-text{
    &[data-active=true] {
      color: var(--jjext-color-dropdown-text) !important;
    }
  }
</style>
