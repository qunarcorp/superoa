--------------------------------------------------------------------------------
### **OA**:
### *接口文档-推荐*:(http://{ip}:{端口}/doc.html)
### *接口文档*:(http://{ip}:{端口}/swagger-ui.html)
### 1. 前端打包
*ps:后端代码中已有打包好的前端js包，若未修改前端代码，可跳过此打包步骤*  

1. 打开控制台，安装node
2. 安装[ykit](https://ykit.ymfe.org/)，运行npm install ykit -g
3. 控制台进入前端代码根目录，执行ykit p -mc
4. 将打包好的prd文件夹下的lib@dev.js、manifest@dev.js、index@VERSION.js、login@VERSION.js复制到后段代码静态资源下的lib.js文件夹中,并修改templates文件下的index.html的scripts引用  

### 2. 后端部署
#### 2.1 存储容器minio部署及配置
*ps:minio作为系统存储容器，用来存储用户上传的文件*
##### 2.1.1 minio服务器安装并启动
[地址](https://dl.min.io/server/minio/release/linux-amd64/minio): https://dl.min.io/server/minio/release/linux-amd64/minio  
###### 操作步骤：
1. wget https://dl.min.io/server/minio/release/linux-amd64/minio
2. chmod +x minio
3. ./minio server /data  

##### 2.1.2 minio客户端安装
[地址](https://dl.min.io/client/mc/release/linux-amd64/mc): https://dl.min.io/client/mc/release/linux-amd64/mc  
###### 操作步骤：
1. wget https://dl.min.io/client/mc/release/linux-amd64/mc
2. chmod +x mc
3. ./mc --help

##### 2.1.3 在minio客户端添加一个云存储服务
./mc config host add minio服务器别名 minion服务器url:port minio服务器accesskey minio服务器secretKey S3v2

##### 2.1.4 minio设置永久下载链接
在实际操作中，发现使用minio的分享文件功能，最多支持分享7天，对于“需要根据有规律的url来直接访问文件”这一需求显然不满足。对于“需要设置永久下载链接并根据url直接访问”这一要求，需：  
###### 配置如下：
1. 在minio客户端添加一个云存储服务
2. 设置开放管理： ./mc policy public minio服务器别名/bucket名

#### 2.2 redis部署
*ps1:redis用于统一管理session,避免系统集群部署时用户多次登录*
*ps2:以下步骤部署三台redis，多个哨兵，实际部署时可根据情况更改*
##### 2.2.1 redis安装
yum install epel-release  
yum install redis
##### 2.2.2 redis配置
IP1: 主redis - /etc/redis.conf:

```xml
#bind 127.0.0.1
port 6379
protected-mode no #为了能够远程连接
requirepass 所设密码
masterauth 所设密码
```

IP2,IP3: 从redis - /etc/redis.conf:

```xml
#bind 127.0.0.1
port 6379
protected-mode no #为了能够远程连接
slaveof IP1 6379 #主redisIP地址及端口号
requirepass 所设密码
masterauth 所设密码
```
##### 2.2.3 redis哨兵配置
###### IP1机器:
哨兵1: /etc/redis-sentinel.conf:

```xml
port 26379
sentinel monitor mymaster IP1 6379 2  #2表示至少需要2个哨兵同意才能启用从redis
protected-mode no  #为了能够远程访问
sentinel auth-pass mymaster 所设密码
```
哨兵2: 复制redis-sentinel.conf文件，命名为redis-sentinel2.conf:

```xml
port 26380
sentinel monitor mymaster IP1 6379 2  #2表示至少需要2个哨兵同意才能启用从redis
protected-mode no  #为了能够远程访问
sentinel auth-pass mymaster 所设密码
```
###### IP2、IP3机器哨兵配置方式同IP1
##### 2.2.4 redis启动
redis-server redis.conf
##### 2.2.5 redis哨兵启动
redis-sentinel redis-sentinel.conf  
redis-sentinel redis-sentinel2.conf
##### 2.2.6 查看redis哨兵部署情况
redis-cli -p 26379 进入哨兵端口  
sentinel master mymaster 查看master情况  
sentinel sentinels mymaster 查看其他哨兵信息

#### 2.3 定时任务xxljob部署
1. 控制台进入 文件根目录/xxljob/文件夹
2. 执行 “mvn clean package -Dmaven.test.skip=true” 命令，得到xxl-job.jar包
3. 执行 “java -jar xxl-job.jar” 命令，部署xxl-job

#### 2.4 系统application.yml配置修改
进入 文件根目录/src/main/resources/resources.{env}/，修改application.yml
###### 数据库PostgreSQL配置
```yml
datasource:
  url: jdbc:postgresql://{ip}:{端口}/{数据库名}
  driver-class-name: org.postgresql.Driver
  username: {username}
  password: {password}
```
###### redis配置
```yml
redis:
  host: {主redis部署ip}
  port: {端口}
  sentinel:
    master: mymaster
    nodes: {redis哨兵部署ip}:{端口},{redis哨兵部署ip}:{端口},...
```
###### xxl-job配置
```yml
xxl:
  job:
    admin:
      addresses: http://{ip}:{端口}/{路径}
    executor:
      # 执行器"AppName"和地址信息配置：AppName执行器心跳注册分组依据；地址信息用于"调度中心请求并触发任务"和"执行器注册"。执行器默认端口为9999，执行器IP默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用。单机部署多个执行器时，注意要配置不同执行器端口；
      appname: oa
      ip:
      port: {执行器端口号}
      # xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler 执行器运行日志文件存储的磁盘位置，需要对该路径拥有读写权限
      logpath: ./
      # 执行器Log文件定期清理功能，指定日志保存天数，日志文件过期自动删除。限制至少保持3天，否则功能不生效；
      logretentiondays: -1
    ### xxl-job, access token 执行器通讯TOKEN，非空时启用
    accessToken:
```
###### minio配置
```yml
minio:
  url: http://{ip}:{端口}
  accessKey: {通行码,部署minio时显示}
  secretKey: {密码,部署minio时显示}
  bucketName: {所创建bucket名}
```
###### 邮箱配置
```yml
mail:
  host: {邮箱服务器地址，eg:smtp.163.com}
  username: {用户名，eg: ***@163.com}
  password: {密码}
```
#### 2.5 系统打包并部署
控制台进入文件根目录，执行 “mvn clean package -Dmaven.test.skip=true”打包命令，执行成功后在target目录下生成superoa.jar文件
执行 “java -jar superoa.jar” 命令，部署项目
