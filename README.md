# aliyun-ddns-auto-refresh
定时刷新阿里云DNS配置

1. `mvn assembly:assembly` 打包得到 `oobss-ddns-1.0-SNAPSHOT-jar-with-dependencies.jar`

2. 在jar包所在目录下添加配置文件`application.properties`
``` bash
accessKeyId=yourAccessKeyId
secret=yourSecret
refreshIntervalInSeconds=600
```

3. 启动命令
``` base
java -jar oobss-ddns-1.0-SNAPSHOT-jar-with-dependencies.jar
```
