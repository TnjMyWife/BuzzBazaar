# BuzzBazaar项目指导

**目前代码已在github开源：**[TnjMyWife/BuzzBazaar: A WeB BBS, for Software Engineering Project (github.com)](https://github.com/TnjMyWife/BuzzBazaar)

**已上线服务器：**[http://8.134.124.93/](http://8.134.124.93/)

**小组成员：**

| 姓名   | 学号     |
| ------ | -------- |
| 黄军龙 | 21307233 |
| 方宇豪 | 21307260 |



## 项目结构树

```
├── src
│   ├── test:暂时不用管。可以执行它的入口函数，对你的一些功能编写测试。
│   └── main
│       ├── java:项目运行的全部java源代码，包含控制器、拦截器、视图层、数据持久	|				化、数据访问接口等。详见后续`com.my.bbs`解读。
│       ├── resources：
│       │   ├── mapper: MyBatis框架的xml配置，负责负责数据访问层的映射，将SQL语句与Java对象关联起来。
│       │   ├── static: css文件，logo等图片，js，参考的layui网页框架。
│       │   └── templates: 前端页面HTML文件
│       │   └── application.properties：包含了应用程序运行时需要的各种配置信 |						息，如数据库连接参数、服务器端口、日志级别、缓存设置等。
├── pom.xml:Maven项目的核心配置文件,包括：
|                1. 项目基本信息：定义项目的坐标（groupId, artifactId,      |					  version），用于项目的依赖管理和版本控制。
|                2. 依赖管理：列出项目所有依赖的库(dependencies部分)，	   |					Maven 会根据这些配置自动下载和引入这些依赖。
|                3. 构建配置：指定项目的构建路径、插件、目标环境，Maven会      |					根据这些配置来执行构建的生命周期。
|                4. 插件管理：定义项目使用的插件及其配置，插件可以用于执行各种    |					构建任务，如编译、测试、打包、部署等。(这部分在IDEA右侧栏中)
├── runBuzzBazaar.bat:自己写的一键运行脚本，**注意，只在你准备上传服务器前，最后在本地测试使用**，其他调试可通过IDEA直接run`MyBBSApplication`；点击后会自动运行target文件夹(maven打包会自动生成)中打包好的jar，即可登录localhost测试。注意，运行前，必须执行maven生命周期中的`clean`清除先前的包，`package`打包当前的项目生成target/BuzzBzar.jar。运行后，可在当前目录的`server.log`查看报错。
|
|
└── build_table.sql:一键生成五个基本表。
```

## 需要准备什么？

- 一个mysql数据库(版本8.4.0)，
    - 若在本地调试：需要**建立名为“bbsdb”的数据库，数据库用户“bbs_user”对其拥有所有权限**，可以修改`pom.xml`下的`url`、`username`、`password`指定为你数据库的jdbcURL和用户名密码。但数据库必须包含目前的项目的5个表，**名字和属性名请勿改变**(否则你可能需要大规模修改MyBatis框架的xml配置(`src/main/resources/mapper`))。表可以通过执行sql脚本`build_table.sql`创建。
    - 服务器调试：用户名密码不要改动。
- 创建一个用于接收前端上传来的图片文件夹upload；然后：
    - r若本地调试：修改`src/main/java/com/my/bbs/common/Constants.java`中的文件上传路径`FILE_UPLOAD_DIC`为你的upload路径，注意不同操作系统格式(看注释)；
    - 若服务器调试：请修改路径为注释中的服务器路径。
- java version 17.0.8 (JAVA_HOME要配好一点啊)
- tomcat 9.0.82（非必要，IDEA会自带）
- Maven。由于本项目使用IDEA作为开发，在创建项目时会帮你自动配置最新版本maven。

## 拿到源码怎么部署在IDEA：

参照博客：[idea怎么导入别人的maven项目_idea导入别人的项目-CSDN博客](https://blog.csdn.net/m0_59176231/article/details/130292468)

注：

- 源码不用改动(包括pom.xml，里面的依赖maven刷新一下都会自动配置)。

- 导入模块时，设置java版本一定需要17，包括jdk、语言级别、其他设置那部分都得17。

- 导入完成的标志是，右上角有![image-20240511014044217](image\image-20240511014044217.png)

    且运行后，访问localhost出现页面（如果出现图片不显示，试试上传头像后能否显示）。

## 备注：

数据库访问可适应idea提供的连接工具，在右侧栏

<img src="image\image-20240511014244082.png" alt="image-20240511014244082" style="zoom:50%;" />

点击+号新增MySQL数据源,正确输入你的数据库用户、密码、以及数据库，测试连接。

![image-20240511014321628](E:\privat\Work\software enginering\project\BBS_test\image-20240511014321628.png)

连接不上的话让我看看。

链接成功后即可确定应用，就可以右键它进入查询控制台愉快地复制sql脚本一键无压生成表了。
<img src="image\image-20240511014531166.png" alt="image-20240511014531166" style="zoom:50%;" />

## 源码com.my.bbs解读:

### 1.common常量处理

#### constants：

​		文件上传路径、session主键、验证码主键

#### ServiceResultEnum：

​		错误码和错误信息

### 2.util:自定义的工具

#### 		BeanUtil：

​			Bean用于对象复制和转换，处理数据的传输

#### IpUtil：

​			用于获取用户Ip，然后根据Ip利用第三方Ip2Region进行地区解析

#### 		MD5Util:

​			使用md5哈希算法，简单加密

#### 		NumberUtil：

​			用于随机数生成和11位电话号码正则表达式判断

#### 		PageQueryUtil：	

​			用于在分页查询场景中封装和操作分页参数

#### 		PageResult：

​			用于分页

#### 		PatternUtil:

​			匹配格式类，邮箱、网址等正则表达式

#### 		ResultUtil：

​			通过使用泛型，灵活地适应不同类型的数据，用于标准化方法的返回值

#### 		ResultGenUtil：

​			响应结果生成工具

#### 		SystemUtil:

​			一些安全处理和主机URL工具

### 3.config配置

#### MyBBSLoginInterceptor

​			设置登录拦截器和上传文件的静态资源处理器

### 4.controller控制器

#### 					common controller：负则

##### ErrorPageController：

​		实现ErrorViewResolver接口，负责解析错误视图，根据不同的HTTP错误状态返回不同的错误页面，提高用户体验。具体实现细节我也不详(之前照抄的，只用管errorpage的设计)。

##### UploadController：

- 处理“/uploadFile”的Post请求（在更改用户头像脚本中，对ajax图片上传插件的初始化），上传单个文件。需要参数为上传的文件流。

- 处理“/uploadFiles”的Post请求（配置创建帖子、编辑帖子在服务端图片上传映射地址），上传单个文件。需要参数为上传的文件流。

##### MyBBSExceptionHandler:

- 提供统一的异常处理逻辑，无论是AJAX请求还是普通请求，都能得到适当的错误响应 

##### CaptchaController:动态验证码控制器

- 处理“/common/captcha”的GET请求。设置HTTP响应头，禁止缓存：通过设置`Cache-Control`、`Pragma`和`Expires`头实现，确保每次请求都会生成新的验证码。然后生成动态验证码，并保存值到会话中。

#### 					response controller：负责处理来自前端的请求

##### IndexController:

​		处理URL以"", "/", "/index", "/index.html"开头的GET请求。只需要将所需数据、选择的类别、查询关键字、排序、榜单、分页参数传递放入请求属性，返回index模版即可。

##### UserController:

- 处理"/userCenter/{userId}"的GET请求，访问指定用户主页页面。`home.html`需要数据有：用户信息、用户最近发帖、用户最近的评论。
- 处理"/userSet"的GET请求，访问用户设置页面。只需要当前已登录用户的信息（从会话中获取）
- 处理"/myCenter”GET的请求，访问我的用户中心页面。需要我收藏的帖子和我发过的帖子。
- 处理"/logout"的GET请求。执行登出操作。清空会话内容，返回到登录页面。
- 处理"/updateUserInfo"的Post请求，执行用户修改数据。`@ResponseBody`指示返回的是JSON等序列化数据，不进行跳转页面。需要表单参数有：用户昵称、用户性别、用户位置、用户签名。更新相关数据，并返回指示结果。
- 处理"/updateHeadImg"的Post请求，执行用户更改头像的操作。需要表单参数：头像的URL。
- 处理"/updatePassword"的Post请求，执行用户修改密码操作。需要参数有：原密码和新密码。修改后返回结果。由前端根据结果进行跳转至登录页。
- 处理"/register"的Post请求。执行新用户注册操作。需要参数有：用户登录名、用户昵称、密码、验证码。**待修改优化**
- 处理"/login"的Post请求。执行用户登录操作。需要参数有：用户登录名、密码、验证码。**待修改优化**

##### PostController:

- 处理"detail/{postId}"的GET请求，访问指定帖子页面。帖子页面需要数据有：当前用户数据、发帖用户数据、帖子内容、分类数据、评论数据。跳转到帖子页面。
- 处理"editPostPage/{postId}"的GET请求，访问帖子修改页面。帖子修改页面需要数据有：帖子分类信息、帖子Id、帖子信息。
- 处理"/addPostPage"的GET请求，访问发布新帖的页面。需要数据有：帖子分类数据。
- 处理"/addPost"的Post请求，处理用户添加帖子的表单。需要参数：帖子标题、分类、帖子内容、验证码。
- 处理"/delPost/{postId}"的Post请求，处理用户删除帖子的表单。仅需要帖子Id。
- 处理"/editPost"的Post请求，处理用户编辑帖子后提交的表单。需要参数有帖子Id，帖子标题、分类、帖子内容、验证码。

##### CommentController：

- 处理"/replyPost"的Post请求，处理用户回复帖子的表单。需要参数：帖子id、被回复的评论id(如果有)、评论内容、验证码。
- 处理"/delReply/{commentId}"的Post请求，处理用户取消回复的操作。需要评论的Id。

##### CollectController：

- "/addCollect/{postId}"和"/delCollect/{postId}"分别处理添加收藏和取消收藏

##### ValidateController：

- 处理“/sendValidationEmail”请求发送重置邮件的Post请求。
- 处理“/resetPassword”请求修改密码的Post请求，将url的token和数据库里的token匹配，成功后便可修改密码。

### 5.entity：数据库表实体与自定义数据实体

#### 		PostEntity：

​			帖子表实体。

#### 		CategoryEntity：

​			类别表实体

#### 		CollectEntity：

​			收藏表实体

#### 		CommentEntity：

​			评论表实体

#### 		UserEntity：

​			用户实体

#### ValidateEntity：

​			验证信息实体

#### 		HotTopicBBSPostListEntity

​			热门帖子实体，页面展示时仅需要id、标题、评论数三个字段

#### 		PageCommentEntity：

​			页面评论实体，用于页面展示时需要的评论，需要再评论表的基础上增加用户头像和用户昵称

#### 		PagePostListEntity

​			页面帖子实体，主要用于Index页面展示，与帖子实体不同，不需要帖子内容，只需要帖子标题，但额外需要发布者昵称、头像

#### 		RecentCommentEntity

​			最近评论列表-实体类，个人中心页面展示时需要，只需要帖子ID、标题、评论内容、评论时间 。

### 6.DAO数据访问层

mybatis框架下的数据访问层，里面的方法有.xml中相关语句映射而来。新增方法时，需要在.xml编写sql语句并保证传入参数与ID对应、类型对应，以及返回结果的类型的统一。

#### CategoryMapper

#### CollectMapper

#### CommentMapper

#### PostMapper

#### UserMapper

#### ValidateMapper

### 7.service服务层

提供了一个比dao层更高层次的抽象，负责处理应用的核心业务逻辑，而dao层则专注于数据访问的细节（由.xml实现）。

service利用dao层提供的方便接口，实现各种业务逻辑。具体实现在service下的impl实现中

当前包括：

#### CategoryService：分类相关业务

​		获取所有分类列表（顶栏显示）、获取分类信息（帖子显示）

#### CollectService：收藏相关业务

​		收藏（用户操作）、取消收藏（用户操作）、验证是否收藏（用于标记）、查看收藏帖子（用户中心）

#### CommentService：评论相关业务

​		用户进行评论、用户删除评论、获取帖子评论列表（帖子详情页）、获取用户评论列表（个人中心）

#### PostService: 帖子相关业务

​		用户发布帖子、获取帖子信息、用户查看帖子、修改帖子、删除帖子、获取首页帖子列表、获取热议帖子列表、获取用户帖子(个人中心)、获取用户近期帖子（用户详情）

#### UserService：用户相关业务

​		用户注册、用户登录、查看用户信息、修改当前用户信息、修改用户头像、修改密码

#### ValidateService：邮箱验证相关业务

​		验证信息的创建、令牌检测、检查用户是否满足发送条件、检查连接是否失效





## resources

### .Mapper .xml文件

- 命名空间namespace定义了XML文件关联的**Mapper接口(dao中)**的名字。
- <resultMap>定义了数据库表列与实体类Entity属性之间的映射关系。
- <sql>片段，定义了表中的列名，可以在其他查询中引用，以避免重复。
- <select\><update\><insert\><delete\>片段，不同的id对应不同select语句，**会被映射到dao层响应的接口，提供给不同需求使用**
- 文件中id带Select为选择性插入\更新，只插入实体中为非空的属性进入数据库表
- `<trim prefix="(" suffix=")" suffixOverrides=",">`：
    - `trim`元素用于自定义字符串的内容
    - `prefix`属性定义了要添加到输出字符串开头的内容
    - `suffix`属性定义了要添加到输出字符串末尾的内容
    - `suffixOverrides`属性用于去除输出字符串中多余的字符
- `<foreach>`: 用于迭代一个集合或数组
    - `item`: 定义了迭代集合中的每个元素的变量名
    - `collection`:指定了要迭代的集合或数组的名称
    - `open="("`: 定义了迭代开始时的左括号
    - `separator=","`: 定义了每次迭代的分隔符，这里是逗号。
    - `close=")"`: 定义了迭代结束时的右括号。
