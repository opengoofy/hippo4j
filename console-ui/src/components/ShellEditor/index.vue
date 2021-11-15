<template>
  <div class="shell-editor">
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
require('codemirror/mode/shell/shell.js')

export default {
  name: 'ShellEditor',
  props: ['value'],
  data() {
    return {
      shellEditor: false
    }
  },
  watch: {
    value(value) {
      const editorValue = this.shellEditor.getValue()
      if (value !== editorValue) {
        this.shellEditor.setValue(this.value)
      }
    }
  },
  mounted() {
    this.shellEditor = CodeMirror.fromTextArea(this.$refs.textarea, {
      lineNumbers: true,
      mode: 'text/x-sh',
      gutters: ['CodeMirror-lint-markers'],
      theme: 'rubyblue',
      lint: true
    })

    this.shellEditor.setValue(this.value ? this.value : '')
    this.shellEditor.on('change', cm => {
      this.$emit('changed', cm.getValue())
      this.$emit('input', cm.getValue())
    })
  },
  methods: {
    getValue() {
      return this.shellEditor.getValue()
    }
  }
}
</script>

<style scoped>
.shell-editor{
  height: 100%;
  position: relative;
}
.shell-editor >>> .CodeMirror {
  height: auto;
  min-height: 300px;
}
.shell-editor >>> .CodeMirror-scroll{
  min-height: 300px;
}
.shell-editor >>> .cm-s-rubyblue span.cm-string {
  color: #F08047;
}
</style>
