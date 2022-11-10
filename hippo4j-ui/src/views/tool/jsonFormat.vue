<template>
  <div class="app-container">
    <split-pane split="vertical" @resize="resize">
      <template slot="paneL">
        <el-input
          v-model="originText"
          type="textarea"
          :rows="36"
          placeholder="请输入内容"
          clearable
          @change="originChange"
        />
      </template>
      <template slot="paneR">
        <div class="editor-container">
          <JSONEditor :json="formatedValue" />
        </div>
      </template>
    </split-pane>
  </div>
</template>

<script>
import splitPane from 'vue-splitpane'
import JSONEditor from 'vue2-jsoneditor'

export default {
  name: 'JsonFormat',
  components: { splitPane, JSONEditor },
  data() {
    return {
      originText: '',
      formatedValue: ''
    }
  },
  watch: {
    originText(newValue) {
      try {
        this.formatedValue = JSON.parse(newValue)
      } catch (error) {
        // console.log(error)
      }
    }
  },
  methods: {
    resize() {

    },
    originChange(e) {
      // console.log(e)
    }
  }
}
</script>

<style  scoped>
  .editor-container{
    height: 82vh;
    overflow: auto;
  }
</style>
