<template>
  <div class="app-container database-manager">
    <div class="left-panel">
      <el-tree
        ref="databaseTree"
        :data="databaseTree"
        node-key="id"
        :props="treeProps"
        :highlight-current="true"
        :expand-on-click-node="false"
        @node-click="handleNodeClick"
        @node-contextmenu="handleRightClick"
      >
        <span slot-scope="{ node, data }" class="custom-tree-node">
          <i :class="getIcon(data.type)" style="margin-right: 5px;"></i>
          <span>{{ node.label }}</span>
        </span>
      </el-tree>

      <context-menu
        v-show="contextMenuVisible"
        :x="menuX"
        :y="menuY"
        :node="selectedNode"
        @command="handleMenuCommand"
        @close="contextMenuVisible = false"
      />
    </div>

    <div class="right-panel">
      <el-tabs v-model="activeTab" type="card" closable @tab-remove="removeTab">
        <el-tab-pane
          v-for="tab in tabs"
          :key="tab.name"
          :label="tab.label"
          :name="tab.name"
        >
          <data-grid
            v-if="tab.type === 'table'"
            :table-name="tab.tableName"
            :key="tab.name"
          />

          <query-editor
            v-if="tab.type === 'query'"
            :key="tab.name"
            @execute="handleQueryExecute"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import ContextMenu from './components/ContextMenu'
import DataGrid from './components/DataGrid'
import QueryEditor from './components/QueryEditor'
import { getDatabaseObjects } from '@/api/database'

export default {
  name: 'DatabaseManager',
  components: { ContextMenu, DataGrid, QueryEditor },
  data() {
    return {
      databaseTree: [],
      treeProps: {
        label: 'name',
        children: 'children'
      },
      selectedNode: null,
      contextMenuVisible: false,
      menuX: 0,
      menuY: 0,
      activeTab: '',
      tabs: [],
      tabIndex: 0
    }
  },
  created() {
    this.loadDatabaseTree()
  },
  methods: {
    getIcon(type) {
      const icons = {
        database: 'el-icon-odometer',
        table: 'el-icon-s-grid',
        view: 'el-icon-data-line',
        function: 'el-icon-cpu',
        procedure: 'el-icon-set-up',
        category: 'el-icon-folder'
      }
      return icons[type] || 'el-icon-document'
    },

    async loadDatabaseTree() {
      try {
        const res = await getDatabaseObjects()
        this.databaseTree = this.formatTreeData(res.data)
      } catch (error) {
        this.$message.error('加载数据库结构失败: ' + error.message)
      }
    },

    formatTreeData(data) {
      const treeData = []

      if (data.tables && data.tables.length > 0) {
        treeData.push({
          id: 'tables',
          name: '表',
          type: 'category',
          children: data.tables.map(table => ({
            id: `table_${table.name}`,
            name: table.name,
            type: 'table',
            ...table
          }))
        })
      }

      if (data.views && data.views.length > 0) {
        treeData.push({
          id: 'views',
          name: '视图',
          type: 'category',
          children: data.views.map(view => ({
            id: `view_${view.name}`,
            name: view.name,
            type: 'view',
            ...view
          }))
        })
      }

      if (data.functions && data.functions.length > 0) {
        treeData.push({
          id: 'functions',
          name: '函数',
          type: 'category',
          children: data.functions.map(func => ({
            id: `function_${func.name}`,
            name: func.name,
            type: 'function',
            ...func
          }))
        })
      }

      if (data.procedures && data.procedures.length > 0) {
        treeData.push({
          id: 'procedures',
          name: '存储过程',
          type: 'category',
          children: data.procedures.map(proc => ({
            id: `procedure_${proc.name}`,
            name: proc.name,
            type: 'procedure',
            ...proc
          }))
        })
      }

      return treeData
    },

    handleNodeClick(node) {
      this.selectedNode = node
      if (node.type === 'table') {
        this.openTableTab(node)
      }
    },

    handleRightClick(event, node) {
      this.selectedNode = node
      this.menuX = event.clientX
      this.menuY = event.clientY
      this.contextMenuVisible = true
      event.preventDefault()
    },

    handleMenuCommand(command) {
      this.contextMenuVisible = false

      switch (command) {
        case 'viewData':
          if (this.selectedNode.type === 'table') {
            this.openTableTab(this.selectedNode)
          }
          break
        case 'newQuery':
          this.openQueryTab()
          break
      }
    },

    openTableTab(node) {
      const tabName = `table_${node.name}`
      const existingTab = this.tabs.find(tab => tab.name === tabName)

      if (!existingTab) {
        this.tabs.push({
          name: tabName,
          label: node.name,
          type: 'table',
          tableName: node.name,
          icon: 'el-icon-s-grid'
        })
      }

      this.activeTab = tabName
    },

    openQueryTab() {
      const tabName = `query_${++this.tabIndex}`
      this.tabs.push({
        name: tabName,
        label: `查询 ${this.tabIndex}`,
        type: 'query',
        icon: 'el-icon-edit-outline'
      })
      this.activeTab = tabName
    },

    removeTab(targetName) {
      const tabs = this.tabs
      let activeName = this.activeTab

      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            const nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeName = nextTab.name
            }
          }
        })
      }

      this.activeTab = activeName
      this.tabs = tabs.filter(tab => tab.name !== targetName)
    }
  }
}
</script>

<style scoped>
.database-manager {
  display: flex;
  height: calc(100vh - 84px);
}

.left-panel {
  width: 250px;
  height: 100%;
  border-right: 1px solid #e6e6e6;
  overflow: auto;
}

.right-panel {
  flex: 1;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-right: 8px;
}

.el-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.el-tabs /deep/ .el-tabs__content {
  flex: 1;
  overflow: auto;
}
</style>
