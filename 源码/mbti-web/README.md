# MBTI职业性格测试系统（Servlet4 + JSP + JDBC + MySQL8）

## 运行前置
- JDK 8+
- Maven 3+
- MySQL 8+
- Tomcat 9.x（Servlet 4 / `javax.servlet`）

## 数据库
1. 新建数据库（示例叫 `mbti`，字符集建议 `utf8mb4`）
2. 导入脚本：`素材/mbti.sql`
3. 修改配置：`src/main/resources/app.properties`

## 本地运行（IDEA + Tomcat）
- 以 Maven 项目打开
- 配置 Tomcat 9，Deploy 选择 `mbti-web:war exploded`
- 访问：`http://localhost:8080/mbti-web/`

## 本地运行（VS Code / PowerShell）
- 推荐一键脚本：`./start-jetty.ps1`
- 或直接执行：
	- `mvn -f .\pom.xml org.eclipse.jetty:jetty-maven-plugin:9.4.54.v20240208:run`
- 访问：`http://127.0.0.1:8080/mbti-web/`

## 任务43：项目编译构建和项目部署（本地）

### 1) 项目编译构建流程
1. 进入项目目录：`mbti-web`
2. 执行构建脚本（默认跳过测试）：`./build-war.ps1`
3. 构建产物：`target/mbti-web.war`

如需带测试构建：
- `./build-war.ps1 -SkipTests:$false`

### 2) 项目部署流程（本地 Tomcat）
1. 确认 Tomcat 安装目录，例如：`D:/tomcat/apache-tomcat-9.0.xx`
2. 执行部署脚本（会先构建再复制 WAR）：
	- `./deploy-local.ps1 -TomcatWebapps "D:/tomcat/apache-tomcat-9.0.xx/webapps"`
3. 启动 Tomcat（若尚未启动）
4. 访问：`http://127.0.0.1:8080/mbti-web/`

说明：
- `deploy-local.ps1` 会覆盖旧的 `mbti-web.war`，并删除旧的 `webapps/mbti-web` 解压目录，避免部署残留。
- 如果你仅本地调试，不走 Tomcat，也可继续使用 `./start-jetty.ps1` 一键启动。

> 说明：某些环境下 `mvn jetty:run` 会出现 `No plugin found for prefix 'jetty'`，
> 使用上面的完整插件坐标可避免该问题。

## 账号
- 管理员（脚本内置）：`admin/123456`
- 学员示例（脚本内置）：`13928282298/123456`、`13902884229/123456`

## 主要功能入口
- 登录：`/login`
- 学员首页：`/app/home` → “开始测评”
- 测评流程：`/test/schedules` → 选场次 → 作答 → `/test/result`
- 管理员：`/admin/users`、`/admin/assessments`
