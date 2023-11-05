# webpan
毕业设计,基于Springboot和Vue的网盘

# 主要功能模块
## 1.我的网盘
### 1.1账号管理
- 邮箱注册
- 登录
- 修改/找回密码
- 发送邮箱验证码
### 1.2文件管理
- 文件上传
    - 断点续传
    - 秒传
    - 取消上传
- 文件修改
    - 重命名
    - 移动
    - 移入回收站
- 目录修改
    - 新建目录
    - 重命名
    - 移动
    - 移入回收站
- 文件预览
    - 视频
    - 图片
    - 音频
    - pdf
    - docx
    - excel
    - 代码
- 文件分享
    - 分享,生成分享码
    - 取消分享
### 1.3回收站
- 彻底删除
- 恢复
## 2.外部分享
- 分享码验证
- 下载文件
- 保存至"我的网盘"
- 当前用户取消分享
## 3.后台管理
- 用户管理
  - 分配空间
  - 启用,禁用
- 文件管理
  - 下载
  - 禁用,删除
# 主要技术
- ssm框架
- ffmpeg,生成切片与缩略图
- redis,保存验证码和分享码
- Vue
  
