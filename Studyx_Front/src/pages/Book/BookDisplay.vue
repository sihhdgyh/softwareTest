<template>
  <el-container>
    <el-aside style="width: 200px;margin-top: 20px">
      <switch></switch>
      <el-menu
    class="categories"
    default-active="0"
    @select="listByCategory"
    active-text-color="red">
    <el-menu-item index="0">
      <i class="el-icon-menu"></i>
      <span slot="title">全部</span>
    </el-menu-item>
    <el-menu-item index="1">
      <i class="el-icon-menu"></i>
      <span slot="title">小说</span>
    </el-menu-item>
    <el-menu-item index="2">
      <i class="el-icon-menu"></i>
      <span slot="title">社会学</span>
    </el-menu-item>
    <el-menu-item index="3">
      <i class="el-icon-menu"></i>
      <span slot="title">心理学</span>
    </el-menu-item>
    <el-menu-item index="4">
      <i class="el-icon-menu"></i>
      <span slot="title">诗歌</span>
    </el-menu-item>
    <el-menu-item index="5">
      <i class="el-icon-menu"></i>
      <span slot="title">童话</span>
    </el-menu-item>
    <el-menu-item index="6">
      <i class="el-icon-menu"></i>
      <span slot="title">传记</span>
    </el-menu-item>
  </el-menu>
    </el-aside>
    <el-main>
      <books1 class="books-area" ref="booksArea"></books1>
    </el-main>
  </el-container>
</template>

<script>



import Books1 from '@/components/BookCom/Books1'
import ReturnButton from '@/components/BookCom/ReturnButton'


export default {
  name: 'library',
  components: {Books1},
  methods: {
    listByCategory (key) {
      this.$message.warning("正在加载");
      var _this = this
      this.$axios.get('/type?cid=' + key, {
      }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$refs.booksArea.books = resp.data.result
          _this.$refs.booksArea.currentPage = 1
          _this.$message.success("加载成功")
        }else{
          _this.$message.console.error("加载失败");
        }
      })
    }
  }
}
</script>

<style scoped>
.books-area {
  width: 990px;
  margin-left: auto;
  margin-right: auto;
}
</style>
