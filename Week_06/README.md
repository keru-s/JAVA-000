# 学习笔记

## 关系数据库

1970年Codd提出关系模型，以关系代数理论为数学基础，通过 E-R 图可以对现实世界进行建模，从而将现实世界的复杂模型简化为关系模型。

由于关系型数据库可以以十分自由的方式进行设计，为了使数据库设计变得易于上手，且设计出来的数据库效率更高，因此提出了数据库设计范式：

- 第一范式（1NF）：关系R属于第一范式，当且仅当R中的每一个属性A的值域只包含原
  子项
- 第二范式（2NF）：在满足1NF的基础上，消除非主属性对码的部分函数依赖
- 第三范式（3NF）：在满足2NF的基础上，消除非主属性对码的传递函数依赖
- BC范式（BCNF）：在满足3NF的基础上，消除主属性对码的部分和传递函数依赖
- 第四范式（4NF）：消除非平凡的多值依赖
- 第五范式（5NF）：消除一些不合适的连接依赖

### 常见关系数据库

- 开源：MySQL、PostgreSQL
- 商业：Oracle，DB2，SQL Server
- 内存数据库：Redis，VoltDB
- 图数据库：Neo4j，Nebula
- 时序数据库：InfluxDB、openTSDB
- 其他关系数据库：Access、Sqlite、H2、Derby、Sybase、Infomix等
- NoSQL数据库：MongoDB、Hbase、Cassandra、CouchDB
- NewSQL/分布式数据库：TiDB、CockroachDB、NuoDB、OpenGauss、OB、TDSQL



## 安装

MySQL 是有免安装包的，直接下载 Zip 包，进行解压，然后配置一下配置文件，再通过命令行启动即可。

https://www.itread01.com/content/1554448941.html