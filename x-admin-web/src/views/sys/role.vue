<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.roleName" size="small" placeholder="请输入角色名称" clearable />
          <el-button type="primary" size="small" round icon="el-icon-search" @click="getRoleList">查询</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" size="small" icon="el-icon-plus" circle @click="openEditUI(null)" />
        </el-col>
      </el-row>
    </el-card>
    <!-- 结果列表 -->
    <el-card>
      <el-table :data="roleList" stripe style="width: 100%">
        <el-table-column label="序号" width="80">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo-1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="roleId" label="角色ID" width="200" />
        <el-table-column prop="roleName" label="角色名称" width="260" />
        <el-table-column prop="roleDesc" label="角色描述 " />
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" size="mini" circle @click="openEditUI(scope.row.roleId)" />
            <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="deleteRole(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 分页组件 -->
    <el-pagination
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      align="right"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 角色信息编辑对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" @close="clearForm">
      <el-form ref="roleFormRef" :model="roleForm" :rules="rules">
        <el-form-item label="角色名称" prop="roleName" :label-width="formLabelWidth">
          <el-input v-model="roleForm.roleName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc" :label-width="formLabelWidth">
          <el-input v-model="roleForm.roleDesc" autocomplete="off" />
        </el-form-item>
        <el-form-item label="权限设置" prop="menuIdList" :label-width="formLabelWidth">
          <el-tree ref="menuRef" :data="menuList" :props="menuProps" show-checkbox default-expand-all node-key="menuId" style="width: 87%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRole">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import roleApi from '@/api/roleManage'
import menuApi from '@/api/menuManage'
export default {
  data() {
    return {
      menuList: [],
      menuProps: {
        children: 'children',
        label: 'title'
      },
      title: '',
      dialogFormVisible: false,
      roleForm: {},
      formLabelWidth: '130px',
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10,
        roleName: '',
        phone: ''
      },
      roleList: [],
      rules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在1到20个字符', trigger: 'blur' }
        ],
        roleDesc: [
          { required: true, message: '请输入角色描述', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在2到20个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getRoleList()
    this.getAllMenu()
  },
  methods: {
    getAllMenu() {
      menuApi.getAllMenu().then(response => {
        this.menuList = response.data
      })
    },
    clearForm() {
      this.roleForm = {}
      // 清空表单验证
      this.$refs.roleFormRef.clearValidate()
      this.$refs.menuRef.setCheckedKeys([])
    },
    openEditUI(id) {
      if (id == null) {
        this.title = '新增角色'
      } else {
        this.title = '修改角色'
        // 根据id查询角色数据
        roleApi.getRoleById(id).then(response => {
          this.roleForm = response.data
          this.$refs.menuRef.setCheckedKeys(response.data.menuIdList)
        })
      }
      this.dialogFormVisible = true
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getRoleList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getRoleList()
    },
    getRoleList() {
      roleApi.getRoleList(this.searchModel).then(response => {
        this.roleList = response.data.rows
        this.total = response.data.total
      })
    },
    saveRole() {
      // 触发表单验证
      this.$refs.roleFormRef.validate((valid) => {
        if (valid) {
          const checkedKeys = this.$refs.menuRef.getCheckedKeys()
          const halfCheckedKeys = this.$refs.menuRef.getHalfCheckedKeys()
          this.roleForm.menuIdList = checkedKeys.concat(halfCheckedKeys)
          // 提交请求给后台
          roleApi.saveRole(this.roleForm).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            })
            // 关闭对话框
            this.dialogFormVisible = false
            // 刷新表单
            this.getRoleList()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    deleteRole(role) {
      this.$confirm(`您确认删除角色${role.roleName}？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        roleApi.deleteRoleById(role.roleId).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getRoleList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    }
  }
}
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right: 10px;
}
.el-dialog .el-input {
  width: 87%;
}
</style>
