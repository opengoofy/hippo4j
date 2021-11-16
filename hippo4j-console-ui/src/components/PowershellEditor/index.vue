<template>
  <div class="powershell-editor">
    <textarea ref="textarea" />
  </div>
</template>

<script>
import CodeMirror from 'codemirror'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/rubyblue.css'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/addon/lint/lint'
require('codemirror/mode/powershell/powershell.js')

export default {
  name: 'PowershellEditor',
  props: ['value'],
  data() {
    return {
      powershellEditor: false
    }
  },
  watch: {
    value(value) {
      const editorValue = this.powershellEditor.getValue()
      if (value !== editorValue) {
        this.powershellEditor.setValue(this.value)
      }
    }
  },
  mounted() {
    this.powershellEditor = CodeMirror.fromTextArea(this.$refs.textarea, {
      lineNumbers: true,
      mode: 'powershell',
      gutters: ['CodeMirror-lint-markers'],
      theme: 'rubyblue',
      lint: true
    })

    this.powershellEditor.setValue(this.value ? this.value : '')
    this.powershellEditor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    getValue() {
      return this.powershellEditor.getValue()
    }
  }
}
</script>

<style scoped>
.powershell-editor{
  height: 100%;
  position: relative;
}
.powershell-editor >>> .CodeMirror {
  height: auto;
  min-height: 300px;
}
.powershell-editor >>> .CodeMirror-scroll{
  min-height: 300px;
}
.powershell-editor >>> .cm-s-rubyblue span.cm-string {
  color: #F08047;
}
</style>
