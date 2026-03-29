<template>
  <div class="file-upload">
    <el-upload
      multiple
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="true"
      :headers="headers"
      :disabled="disabled"
      class="upload-file-uploader"
      ref="fileUpload"
    >
      <el-button size="mini" type="primary" :disabled="disabled">选取文件</el-button>
      <div slot="tip" class="el-upload__tip" v-if="showTip && !disabled">
        请上传
        <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b> </template>
        <template v-if="fileType"> 格式为 <b style="color: #f56c6c">{{ fileType.join('/') }}</b> </template>
        的文件
      </div>
    </el-upload>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
  name: "FileUpload",
  props: {
    // 值
    value: [String, Object, Array],
    // 数量限制
    limit: {
      type: Number,
      default: 5,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 5,
    },
    // 文件类型
    fileType: {
      type: Array,
      default: () => ["doc", "xls", "ppt", "txt", "pdf"],
    },
    // 是否显示提示
    showTip: {
      type: Boolean,
      default: true
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      headers: {
        Authorization: "Bearer " + getToken(),
      },
      fileList: [],
    };
  },
  watch: {
    value: {
      handler(val) {
        if (val) {
          let list;
          if (Array.isArray(val)) {
            list = val;
          } else if (typeof val === 'string') {
            list = val.split(',').filter(Boolean).map((url, index) => ({
              name: url.substring(url.lastIndexOf('/') + 1),
              url: url,
            }));
          } else {
            list = [];
          }
          this.fileList = list;
        } else {
          this.fileList = [];
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    // 上传前校检格式和大小
    handleBeforeUpload(file) {
      // 校检文件类型
      if (this.fileType.length > 0) {
        const ext = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();
        const isTypeOk = this.fileType.some(type => ext === type.toLowerCase());
        if (!isTypeOk) {
          this.$message.error(`文件格式不正确, 请上传${this.fileType.join("/")}格式文件!`);
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isSizeOk = file.size / 1024 / 1024 < this.fileSize;
        if (!isSizeOk) {
          this.$message.error(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      return true;
    },
    // 文件个数超出
    handleExceed() {
      this.$message.error(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 上传失败
    handleUploadError(err) {
      this.$message.error("上传文件失败，请重试");
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      if (res.code === 200) {
        this.$message.success("上传成功");
        this.fileList.push({ name: res.fileName, url: res.fileName });
        this.$emit("input", this.listToString(this.fileList));
      } else {
        this.$message.error(res.msg || "上传失败");
      }
    },
    // 对象转字符串
    listToString(list) {
      return list.map(item => item.url).join(',');
    },
  },
};
</script>

<style scoped>
.file-upload {
  width: 100%;
}
.upload-file-uploader {
  margin-bottom: 5px;
}
</style>
