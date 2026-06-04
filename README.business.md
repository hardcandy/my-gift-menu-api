# 心意清单后端

Spring Boot + MyBatis-Plus 后端，用于存储「心意清单」小程序数据。

## 目录

- `src/main/java/com/wx/gift`：后端代码
- `sql/schema.sql`：MySQL 建表脚本

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

