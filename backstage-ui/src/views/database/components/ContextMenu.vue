<template>
  <div
    class="context-menu"
    :style="{ left: x + 'px', top: y + 'px' }"
    v-click-outside="handleClickOutside"
  >
    <ul>
      <template v-if="node && node.type === 'table'">
        <li @click="emitCommand('viewData')">
          <i class="el-icon-s-grid"></i> 查看数据
        </li>
        <li class="divider"></li>
      </template>
      <li @click="emitCommand('newQuery')">
        <i class="el-icon-edit-outline"></i> 新建查询
      </li>
    </ul>
  </div>
</template>


<script>
export default {
  name: 'ContextMenu',
  props: {
    x: Number,
    y: Number,
    node: Object
  },
  directives: {
    'click-outside': {
      bind(el, binding, vnode) {
        el.clickOutsideEvent = function(event) {
          if (!(el === event.target || el.contains(event.target))) {
            vnode.context[binding.expression](event)
          }
        }
        document.body.addEventListener('click', el.clickOutsideEvent)
      },
      unbind(el) {
        document.body.removeEventListener('click', el.clickOutsideEvent)
      }
    }
  },
  methods: {
    emitCommand(command) {
      this.$emit('command', command)
    },
    handleClickOutside() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.context-menu {
  position: fixed;
  z-index: 9999;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 5px 0;
  min-width: 120px;
}

.context-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.context-menu li {
  padding: 8px 15px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
}

.context-menu li:hover {
  background: #f5f7fa;
  color: #409eff;
}

.context-menu li i {
  margin-right: 5px;
}

.divider {
  border-top: 1px solid #ebeef5;
  margin: 5px 0;
}
</style>
