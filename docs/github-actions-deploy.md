# 后端 GitHub Actions 自动部署教程

## 1. 自动化目标

当前后端部署流程已经自动化为：推送 `release/20260328` 分支后，GitHub Actions 自动构建后端 jar，上传到服务器，并重启 Docker Compose 中的 `backstage-admin` 服务。

自动化后的流程：

1. 拉取最新后端代码。
2. 使用 JDK 17 构建项目。
3. 执行 `mvn clean install -DskipTests -pl '!backstage-flink'`，跳过 flink 模块。
4. 校验 `backstage-admin/target/backstage-admin.jar` 是否存在。
5. 使用 SSH 连接服务器。
6. 先停止 `backstage-admin` 容器，避免运行中的 Java 进程读取半包。
7. 使用 `rsync --inplace` 上传 jar 到 `/www/ruoyi/backstage-admin.jar`。
8. 对比本地和服务器 SHA256，确认文件完整一致。
9. 在服务器执行 `unzip -t` 校验 jar 内嵌依赖可读。
10. 进入 `/opt/docker-apps`，执行 `docker-compose up -d backstage-admin` 重启服务。
11. 输出后端容器状态和最近日志，便于排查。

## 2. Workflow 文件

后端 workflow 文件位置：

```text
.github/workflows/deploy-release.yml
```

触发方式：

```yaml
on:
  push:
    branches:
      - release/20260328
  workflow_dispatch:
```

含义：

- 推送到 `release/20260328` 时自动部署。
- 也可以在 GitHub 页面手动点击 `Run workflow` 部署。

## 3. GitHub Secrets 配置

后端仓库必须配置这个 Secret：

| 名称 | 说明 |
| --- | --- |
| `DEPLOY_SSH_PRIVATE_KEY` | GitHub Actions 连接服务器使用的 SSH 私钥 |

私钥内容在本机：

```bash
/Users/rengang/.ssh/osh_github_actions_deploy_key
```

复制私钥到剪贴板：

```bash
pbcopy < /Users/rengang/.ssh/osh_github_actions_deploy_key
```

GitHub 配置路径：

```text
后端仓库 -> Settings -> Secrets and variables -> Actions -> New repository secret
```

## 4. 默认部署参数

workflow 已内置默认参数：

| 参数 | 默认值 |
| --- | --- |
| 服务器 | `43.242.200.25` |
| SSH 用户 | `root` |
| SSH 端口 | `58753` |
| jar 上传目录 | `/www/ruoyi` |
| jar 文件名 | `backstage-admin.jar` |
| Docker Compose 目录 | `/opt/docker-apps` |
| 后端服务名 | `backstage-admin` |
| JDK 版本 | `17` |

如果以后服务器信息变化，可以在 GitHub Variables 中覆盖：

| Variable | 用途 |
| --- | --- |
| `DEPLOY_HOST` | 覆盖服务器 IP 或域名 |
| `DEPLOY_USER` | 覆盖 SSH 用户 |
| `DEPLOY_PORT` | 覆盖 SSH 端口 |
| `BACKEND_DEPLOY_DIR` | 覆盖 jar 上传目录 |
| `BACKEND_JAR_NAME` | 覆盖 jar 文件名 |
| `DOCKER_COMPOSE_DIR` | 覆盖 Docker Compose 目录 |
| `BACKEND_SERVICE_NAME` | 覆盖后端服务名 |

## 5. 手动触发部署

1. 打开 GitHub 后端仓库。
2. 进入 `Actions`。
3. 选择 `Deploy Backend Release`。
4. 点击 `Run workflow`。
5. 分支选择 `release/20260328`。
6. 点击确认执行。

## 6. 本地等价测试命令

本地构建：

```bash
cd /Users/rengang/chuangye/osh-projects/osh-backend
JAVA_HOME=/Users/rengang/Library/Java/JavaVirtualMachines/ms-17.0.17/Contents/Home \
  '/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn' \
  clean install -DskipTests -pl '!backstage-flink'
```

校验 jar：

```bash
test -f backstage-admin/target/backstage-admin.jar
```

安全上传并重启后端：

```bash
cd /Users/rengang/chuangye/osh-projects/osh-backend

ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "mkdir -p /www/ruoyi && cd /opt/docker-apps && docker-compose stop backstage-admin"

rsync -az --inplace --progress \
  -e "ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753" \
  backstage-admin/target/backstage-admin.jar \
  root@43.242.200.25:/www/ruoyi/backstage-admin.jar

LOCAL_SHA=$(shasum -a 256 backstage-admin/target/backstage-admin.jar | awk '{print $1}')
REMOTE_SHA=$(ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "sha256sum /www/ruoyi/backstage-admin.jar | cut -d ' ' -f 1")
test "$LOCAL_SHA" = "$REMOTE_SHA"

ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "unzip -t /www/ruoyi/backstage-admin.jar 'BOOT-INF/lib/*' >/tmp/backend-jar-check.log && tail -5 /tmp/backend-jar-check.log"

ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "cd /opt/docker-apps && docker-compose up -d backstage-admin && sleep 20 && docker-compose ps backstage-admin"
```

## 7. 部署后验证

检查后端容器：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "cd /opt/docker-apps && docker-compose ps backstage-admin"
```

检查后端 HTTP：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "curl -I http://127.0.0.1:8081/"
```

预期结果：

```text
HTTP/1.1 200
```

查看日志：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "docker logs -f backstage-admin"
```

## 8. 故障排查

### 8.1 SSH 失败

检查 GitHub Secret 是否配置完整：

```text
DEPLOY_SSH_PRIVATE_KEY
```

检查服务器是否启用公钥登录：

```bash
ssh -p 58753 root@43.242.200.25 "sshd -T | grep pubkeyauthentication"
```

预期：

```text
pubkeyauthentication yes
```

### 8.2 构建失败

先本地执行：

```bash
mvn clean install -DskipTests -pl '!backstage-flink'
```

如果本地也失败，优先修复编译错误，再重新推送 `release/20260328`。

### 8.3 容器没有重启成功

检查 compose 服务名：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "cd /opt/docker-apps && docker-compose ps"
```

如果服务名不是 `backstage-admin`，需要修改 GitHub Variable：

```text
BACKEND_SERVICE_NAME
```

### 8.4 jar 上传后没有生效

检查服务器 jar 修改时间：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "ls -lh /www/ruoyi/backstage-admin.jar"
```

检查容器挂载配置：

```bash
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "cd /opt/docker-apps && docker-compose config | grep -A5 -B5 /www/ruoyi"
```


### 8.5 jar 显示 Invalid or corrupt jarfile

这通常是上传中断后把半包覆盖到了 `/www/ruoyi/backstage-admin.jar`。不要直接启动容器，先执行下面三步：

```bash
# 1. 停止后端，避免读取半包
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "cd /opt/docker-apps && docker-compose stop backstage-admin"

# 2. 使用 rsync --inplace 重新上传完整 jar
cd /Users/rengang/chuangye/osh-projects/osh-backend
rsync -az --inplace --progress \
  -e "ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753" \
  backstage-admin/target/backstage-admin.jar \
  root@43.242.200.25:/www/ruoyi/backstage-admin.jar

# 3. 校验 SHA 一致后再启动
shasum -a 256 backstage-admin/target/backstage-admin.jar
ssh -i /Users/rengang/.ssh/osh_github_actions_deploy_key -p 58753 root@43.242.200.25 \
  "sha256sum /www/ruoyi/backstage-admin.jar && cd /opt/docker-apps && docker-compose up -d backstage-admin"
```
