package com.collmall.document;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SpringBootTest
//@RunWith(SpringRunner.class)
public class ScrewApplicationTests {

//    @Autowired
//    ApplicationContext applicationContext;

//    @Test
public static void main(String[] args) {

        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/";
        String userName = "root";
        String password = "root";

        try {
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, userName, password);
            Statement statement = connection.createStatement();

            // 执行 SHOW DATABASES 命令
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES;");


            // 处理结果集
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                System.out.println("Database Name: " + databaseName);
                // 生成文档
                generateDocument(url,databaseName,userName,password);
            }

            // 关闭资源
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("执行完成");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常信息：" + e.getMessage());
            if (e instanceof SQLException) {
                System.out.println("SQL 状态码：" + ((SQLException) e).getSQLState());
                System.out.println("错误代码：" + ((SQLException) e).getErrorCode());
            }
        }
    }


    /**
     * 配置想要生成的表+ 配置想要忽略的表
     *
     * @return 生成表配置
     */
    public static ProcessConfig getProcessConfig() {
        // 忽略表名
        List<String> ignoreTableName = Arrays.asList("a", "test_group");
        // 忽略表前缀，如忽略a开头的数据库表
        List<String> ignorePrefix = Arrays.asList("a", "t");
        // 忽略表后缀
        List<String> ignoreSuffix = Arrays.asList("_test", "czb_");
        return ProcessConfig.builder()
                //根据名称指定表生成
             //   .designatedTableName(Arrays.asList("fire_user"))
                //根据表前缀生成
                .designatedTablePrefix(new ArrayList<>())
                //根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
    }

    /**
     *  生成文档
     * @param url
     * @param databaseName
     * @param userName
     * @param password
     */

    public  static void  generateDocument(String url ,String databaseName,String userName,String password) {

            // 创建数据源
            //数据源
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            hikariConfig.setJdbcUrl(url + databaseName + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&autoReconnect=true");
            hikariConfig.setUsername(userName);
            hikariConfig.setPassword(password);


            //设置可以获取tables remarks信息
            hikariConfig.addDataSourceProperty("useInformationSchema", "true");
            // 设置连接池参数
            hikariConfig.setMinimumIdle(2);
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setIdleTimeout(300000); // 空闲连接超时时间（毫秒）
            hikariConfig.setMaxLifetime(1800000); // 连接最大生命周期（毫秒）
            hikariConfig.setConnectionTestQuery("SELECT 1"); // 测试连接是否有效
            DataSource dataSource = new HikariDataSource(hikariConfig);


            //    DataSource dataSourceMysql = applicationContext.getBean(DataSource.class);
            // 生成文件配置
            EngineConfig engineConfig = EngineConfig.builder()
                    // 生成文件路径，自己mac本地的地址，这里需要自己更换下路径
                    .fileOutputDir("D:/")
                    // 打开目录
                    .openOutputDir(false)
                    // 文件类型
                    .fileType(EngineFileType.HTML)
                    // 生成模板实现
                    .produceType(EngineTemplateType.freemarker).build();
            // 生成文档配置（包含以下自定义版本号、描述等配置连接）
            Configuration config = Configuration.builder()
                    .version("1.0.0")
                    .description("生成文档信息描述")
                    //数据源
                    .dataSource(dataSource)
                    //  .dataSource(dataSourceMysql)
                    .engineConfig(engineConfig)
                    .produceConfig(getProcessConfig())
                    .build();
            // 执行生成
            new DocumentationExecute(config).execute();

    }
}