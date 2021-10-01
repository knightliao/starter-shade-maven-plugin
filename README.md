# starter-shade-maven-plugin

shade maven plugin for java projects / Java项目的打包插件


## 特点

- 简单、方便
- 只生成一个jar包
- 自动生成start.sh, stop.sh
- 自动生成部署环境，并自动将部署环境打成tar.gz包

## demo

https://github.com/knightliao/starter-shade-maven-plugin/demos/starter-shade-demo

## 使用方式

    <build>
    
        <finalName>starter-shade-demo</finalName>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>

        <plugins>
            <plugin>
                <groupId>com.github.knightliao.plugin</groupId>
                <artifactId>starter-shade-maven-plugin</artifactId>
                <version>1.0.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <finalName>${project.build.finalName}</finalName>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.example.starter.DemoMain</mainClass>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.DontIncludeResourceTransformer">
                                    <resources>
                                    </resources>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

        </plugins>

    </build>

## 效果

### 打包

mvn clean package

### 结果

#### target目录
    
    ➜  target git:(master) ll
    total 52824
    drwxr-xr-x  7 knightliao  staff       238  6  3 17:23 classes
    drwxr-xr-x  3 knightliao  staff       102  6  3 17:23 maven-archiver
    -rw-r--r--  1 knightliao  staff      5219  6  3 17:23 original-starter-shade-demo.jar
    drwxr-xr-x  8 knightliao  staff       272  6  3 17:23 starter-run
    -rw-r--r--  1 knightliao  staff  14106191  6  3 17:23 starter-shade-demo.jar
    -rw-r--r--  1 knightliao  staff  12927960  6  3 17:23 starter-shade-demo.tar.gz

其中

- starter-run 是可部署的环境，包含所有可执行脚本以及jar包的文件目录 
- starter-shade-demo.tar.gz 是对 starter-run 目录的打包

#### starter-run目录

    ➜  target git:(master) cd starter-run
    ➜  starter-run git:(master) ls -l
    total 27592
    -rw-r--r--  1 knightliao  staff         9  6  3 17:23 demo.properties
    -rw-r--r--  1 knightliao  staff       412  6  3 17:23 env
    -rw-r--r--  1 knightliao  staff      1353  6  3 17:23 logback.xml
    -rw-r--r--  1 knightliao  staff      1037  6  3 17:23 start.sh
    -rw-r--r--  1 knightliao  staff  14106191  6  3 17:23 starter-shade-demo.jar
    -rw-r--r--  1 knightliao  staff       532  6  3 17:23 stop.sh

其中

- start.sh 是开始脚本, 插件自动生成的
- stop.sh 是关闭脚本, 插件自动生成的
- env 是启动前的环境变量设置

#### env文件

    ➜  starter-run git:(master) cat env
    
    BUNDLE_JAR_NAME=starter-shade-demo.jar
    
    export JAVA_OPTS="$JAVA_OPTS -server -Xms1024m -Xmx1024m -Xmn448m -Xss256K -XX:MaxPermSize=128m -XX:ReservedCodeCacheSize=64m"
    export JAVA_OPTS="$JAVA_OPTS -XX:+UseParallelGC -XX:+UseParallelOldGC -XX:ParallelGCThreads=2"
    export JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
    export JAVA_OPTS="$JAVA_OPTS -Dlogback.configurationFile=file:logback.xml"

其中

- 必须设置 BUNDLE_JAR_NAME ，否则 start.sh 无法启动
- 其它参数可配

#### 执行、查看日志和关闭

    ➜  starter-run git:(master) sh start.sh
    nohup java  -server -Xms1024m -Xmx1024m -Xmn448m -Xss256K -XX:MaxPermSize=128m -XX:ReservedCodeCacheSize=64m -XX:+UseParallelGC -XX:+UseParallelOldGC -XX:ParallelGCThreads=2 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Dlogback.configurationFile=file:logback.xml -jar starter-shade-demo.jar  >> log_1464945913.log 2>&1 &
    ➜  starter-run git:(master) tail -f log_1464945913.log
    信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7c518f42: startup date [Fri Jun 03 17:25:13 CST 2016]; root of context hierarchy
    六月 03, 2016 5:25:13 下午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
    信息: Loading XML bean definitions from class path resource [applicationContext.xml]
    2016-06-03 17:25:14,079 [main] INFO  com.example.starter.DemoMain - {key=value}
    2016-06-03 17:25:14,082 [main] INFO  com.example.starter.DemoMain - 0
    2016-06-03 17:25:14,583 [main] INFO  com.example.starter.DemoMain - 1
    2016-06-03 17:25:15,089 [main] INFO  com.example.starter.DemoMain - 2
    2016-06-03 17:25:15,593 [main] INFO  com.example.starter.DemoMain - 3
    2016-06-03 17:25:16,097 [main] INFO  com.example.starter.DemoMain - 4
    2016-06-03 17:25:16,602 [main] INFO  com.example.starter.DemoMain - 5
    2016-06-03 17:25:17,108 [main] INFO  com.example.starter.DemoMain - 6
    ^C
    ➜  starter-run git:(master) sh stop.sh
    Find process and pid=[79300]
    Kill pid=[79300] done
    
