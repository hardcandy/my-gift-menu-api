# 心意清单后端

微信云托管 Java Spring Boot 服务，负责「心意清单」小程序的圈子、愿望、送礼提案、反馈、订阅消息和生活模块数据。


## 快速开始
前往 [微信云托管快速开始页面](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/basic/guide.html)，选择相应语言的模板，根据引导完成部署。

## 本地调试
下载代码在本地调试，请参考[微信云托管本地调试指南](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/guide/debug/)。

## 实时开发
代码变动时，不需要重新构建和启动容器，即可查看变动后的效果。请参考[微信云托管实时开发指南](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/guide/debug/dev.html)

## Dockerfile最佳实践
请参考[如何提高项目构建效率](https://developers.weixin.qq.com/miniprogram/dev/wxcloudrun/src/scene/build/speed.html)

## 目录结构说明
~~~
.
├── Dockerfile                      Dockerfile 文件
├── LICENSE                         LICENSE 文件
├── README.md                       README 文件
├── container.config.json           模板部署「服务设置」初始化配置（二开请忽略）
├── mvnw                            mvnw 文件，处理mevan版本兼容问题
├── mvnw.cmd                        mvnw.cmd 文件，处理mevan版本兼容问题
├── pom.xml                         pom.xml文件
├── settings.xml                    maven 配置文件
├── springboot-cloudbaserun.iml     项目配置文件
└── src                             源码目录
    └── main                        源码主目录
        ├── java                    业务逻辑目录
        └── resources               资源文件目录
~~~


## 业务 API

所有接口均挂在 `/gift-menu-api` 下，使用 `POST` JSON 请求。

- `/home/userInfoByCode`：登录和资料初始化
- `/family/*`：圈子、成员、邀请码、加入申请和孩子资料
- `/gift/*`：愿望创建、审核、认领、确认、完成、感谢和留言
- `/proposal/*`：送礼提案创建、确认、拒绝和取消
- `/feedback/submit`：应用反馈
- `/subscription/save`：订阅消息授权记录
- `/game/list`：游戏库列表
- `/game/detail`：游戏详情
- `/game/save`：新增或编辑游戏
- `/game/delete`：软删除游戏
- `/game/play`：记录一次游玩，并更新最近玩的人和最近游玩时间

## SQL

全新环境执行：

```sql
source sql/schema.sql;
```

已有环境升级「我的游戏」模块执行：

```sql
source sql/20260609_add_game_library.sql;
```

新增表：

- `t_gift_game`
- `t_gift_game_play_log`

## 配置

基础数据库和微信配置见 `src/main/resources/application.yml`。当前「我的游戏」模块不需要新增后端环境变量。

## 使用注意
如果不是通过微信云托管控制台部署模板代码，而是自行复制/下载模板代码后，手动新建一个服务并部署，需要在「服务设置」中补全以下环境变量，才可正常使用，否则会引发无法连接数据库，进而导致部署失败。
- MYSQL_ADDRESS
- MYSQL_PASSWORD
- MYSQL_USERNAME
以上三个变量的值请按实际情况填写。如果使用云托管内MySQL，可以在控制台MySQL页面获取相关信息。


## License

[MIT](./LICENSE)
