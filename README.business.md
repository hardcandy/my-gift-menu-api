# 心意清单后端

Spring Boot + MyBatis-Plus 后端，用于存储「心意清单」小程序数据。

## 目录

- `src/main/java/com/wx/gift`：后端代码
- `sql/schema.sql`：MySQL 建表脚本
- `sql/20260609_add_game_library.sql`：新增「我的游戏」增量脚本
- `sql/20260609_add_restaurant_choice.sql`：新增「美食之选」增量脚本

## 本地启动

先创建数据库并执行：

```sql
source sql/schema.sql;
```

再配置环境变量：

```bash
export MYSQL_URL='jdbc:mysql://127.0.0.1:3306/my_gift_menu?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai'
export MYSQL_USERNAME='root'
export MYSQL_PASSWORD='你的数据库密码'
export WX_APPID='wx451f85e2b68fbc8f'
export WX_SECRET='你的小程序 secret'
```

启动：

```bash
mvn spring-boot:run
```

默认地址：

```
http://127.0.0.1:8082/gift-menu-api
```

如果部署到 bdnzljh.com，建议反代到：

```
https://bdnzljh.com/gift-menu-api
```

## 核心接口

- `POST /gift-menu-api/home/userInfoByCode`
- `POST /gift-menu-api/home/profile`
- `POST /gift-menu-api/family/ensure`
- `POST /gift-menu-api/family/detail`
- `POST /gift-menu-api/family/invite/detail`
- `POST /gift-menu-api/family/join`
- `POST /gift-menu-api/family/child/create`
- `POST /gift-menu-api/family/child/list`
- `POST /gift-menu-api/gift/create`
- `POST /gift-menu-api/gift/list`
- `POST /gift-menu-api/gift/detail`
- `POST /gift-menu-api/gift/approve`
- `POST /gift-menu-api/gift/reject`
- `POST /gift-menu-api/gift/claim`
- `POST /gift-menu-api/gift/confirm`
- `POST /gift-menu-api/gift/complete`
- `POST /gift-menu-api/gift/thank`
- `POST /gift-menu-api/game/list`
- `POST /gift-menu-api/game/detail`
- `POST /gift-menu-api/game/save`
- `POST /gift-menu-api/game/delete`
- `POST /gift-menu-api/game/play`
- `POST /gift-menu-api/restaurant/list`
- `POST /gift-menu-api/restaurant/detail`
- `POST /gift-menu-api/restaurant/save`
- `POST /gift-menu-api/restaurant/delete`
- `POST /gift-menu-api/restaurant/visit`

## 我的游戏

「我的游戏」按圈子隔离数据，只有当前圈子成员可以查看和操作。游戏删除为软删除，游玩日志会继续保留。

新增表：

- `t_gift_game`：游戏库，保存图片、名称、存放位置、单局时间、最近玩的人和最近游玩时间。
- `t_gift_game_play_log`：游玩日志，保存每次游玩的游戏、圈子、操作者、玩家列表、来源和时间。

已有数据库升级时执行：

```sql
source sql/20260609_add_game_library.sql;
```

全新环境直接执行 `sql/schema.sql` 即可。该功能不需要新增后端环境变量。

## 美食之选

「美食之选」按圈子隔离数据，只有当前圈子成员可以查看和操作。餐厅删除为软删除，历史就餐和评分记录会继续保留。

新增表：

- `t_gift_restaurant`：餐厅库，保存图片、名称、位置、人均、距离、推荐菜、菜系、标签、平均分和最后吃的时间。
- `t_gift_restaurant_visit`：就餐记录，保存每次吃饭的餐厅、成员、菜品、备注和时间。
- `t_gift_restaurant_score`：成员评分，保存每个人 1-10 分的评分。

已有数据库升级时执行：

```sql
source sql/20260609_add_restaurant_choice.sql;
```

全新环境直接执行 `sql/schema.sql` 即可。该功能不需要新增后端环境变量。
