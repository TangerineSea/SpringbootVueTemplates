<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.username" size="small" placeholder="请输入用户名" clearable />
          <el-input v-model="searchModel.phone" size="small" placeholder="请输入电话" clearable />
          <el-button type="primary" size="small" round icon="el-icon-search" @click="getUserList">查询</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" size="small" icon="el-icon-plus" circle @click="openEditUI(null)" />
        </el-col>
      </el-row>
    </el-card>
    <!-- 结果列表 -->
    <el-card>
      <el-table :data="userList" stripe style="width: 100%">
        <el-table-column label="序号" width="80">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{ (searchModel.pageNo-1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="用户ID" width="180" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="phone" label="电话 " width="180" />
        <el-table-column prop="status" label="用户状态 " width="180">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status == 1">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="电子邮件" />
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" size="mini" circle @click="openEditUI(scope.row.id)" />
            <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="deleteUser(scope.row)" />
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

    <!-- 用户信息编辑对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" @close="clearForm">
      <el-form ref="userFormRef" :model="userForm" :rules="rules">
        <el-form-item label="用户名" prop="username" :label-width="formLabelWidth">
          <el-input v-model="userForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="登录密码" prop="password" :label-width="formLabelWidth">
          <el-input v-model="userForm.password" type="password" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="联系电话" :label-width="formLabelWidth">
          <el-input v-model="userForm.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="电子邮件" prop="email" :label-width="formLabelWidth">
          <el-input v-model="userForm.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="用户状态" :label-width="formLabelWidth">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="用户角色" :label-width="formLabelWidth">
          <el-checkbox-group
            v-model="userForm.roleIdList"
            style="width: 87%"
            :max="2"
          >
            <el-checkbox v-for="role in roleList" :key="role.roleId" :label="role.roleId">{{ role.roleDesc }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveUser">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
import roleApi from '@/api/roleManage'
export default {
  data() {
    var checkEmail = (rule, value, callback) => {
      var reg = /^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\.[a-z]{2,}$/
      if (!reg.test(value)) {
        return callback(new Error('邮箱格式错误'))
      }
      callback()
    }
    return {
      roleList: [],
      title: '',
      dialogFormVisible: false,
      userForm: {
        roleIdList: []
      },
      formLabelWidth: '130px',
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10,
        username: '',
        phone: ''
      },
      userList: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在3到50个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在6到16个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮件', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getUserList()
    this.getAllRoleList()
  },
  methods: {
    getAllRoleList() {
      debugger
      roleApi.getAllRoleList().then(response => {
        this.roleList = response.data
        console.log(this.roleList)
      })
    },
    clearForm() {
      this.userForm = {
        roleIdList: []
      }
      // 清空表单验证
      this.$refs.userFormRef.clearValidate()
    },
    openEditUI(id) {
      if (id == null) {
        this.title = '新增用户'
      } else {
        this.title = '修改用户'
        // 根据id查询用户数据
        userApi.getUserById(id).then(response => {
          this.userForm = response.data
        })
      }
      this.dialogFormVisible = true
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getUserList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getUserList()
    },
    getUserList() {
      userApi.getUserList(this.searchModel).then(response => {
        this.userList = response.data.rows
        this.total = response.data.total
      })
    },
    saveUser() {
      // 触发表单验证
      this.$refs.userFormRef.validate((valid) => {
        if (valid) {
          // 提交请求给后台
          userApi.saveUser(this.userForm).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            })
            // 关闭对话框
            this.dialogFormVisible = false
            // 刷新表单
            this.getUserList()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    deleteUser(user) {
      this.$confirm(`您确认删除用户${user.username}？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userApi.deleteUserById(user.id).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getUserList()
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
