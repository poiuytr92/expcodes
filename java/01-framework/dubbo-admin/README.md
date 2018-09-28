# Dubbo admin （Eclipse EE版: Fork官方库改）

> dubbo admin front end and back end
![index](https://raw.githubusercontent.com/apache/incubator-dubbo-ops/develop/dubbo-admin-frontend/src/assets/index.png)
### front end
* Vue.js and Vuetify
* dubbo-admin-frontend/README.md for more detail

## Build setup 

``` bash
# build （打包&编译：在incubator-dubbo-ops目录下召唤dos，确保当前JDK环境为1.8+，输入以下命令）
mvn clean install

# run （运行：在incubator-dubbo-ops目录下召唤dos，输入以下命令）
mvn --projects dubbo-admin-backend spring-boot:run

# visit （打开界面：在浏览器打开下面地址）
localhost:8080 

```
