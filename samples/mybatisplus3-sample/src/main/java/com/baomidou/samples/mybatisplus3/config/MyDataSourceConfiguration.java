package com.baomidou.samples.mybatisplus3.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据源配置，支持多数据源，shardingsphere，普通数据源
 * https://github.com/baomidou/dynamic-datasource-spring-boot-starter
 *
 *  * 动态数据源配置：
 *  *
 *  * 使用{@link com.baomidou.dynamic.datasource.annotation.DS}注解，切换数据源
 *  *
 *  * <code>@DS(DataSourceConfiguration.SHARDING_DATA_SOURCE_NAME)</code>
 *
 * @description:
 * @author: haochencheng
 * @create: 2020-09-09 18:51
 **/
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class,
        SpringBootConfiguration.class})
public class MyDataSourceConfiguration {

    /**
     * 分表数据源名称
     */
    private static final String SHARDING_DATA_SOURCE_NAME = "search_item";

    /**
     * 动态数据源配置项
     */
    @Resource
    private DynamicDataSourceProperties properties;

    /**
     * shardingjdbc有四种数据源，需要根据业务注入不同的数据源
     *
     * <p>1. 未使用分片, 脱敏的名称(默认): shardingDataSource;
     * <p>2. 主从数据源: masterSlaveDataSource;
     * <p>3. 脱敏数据源：encryptDataSource;
     * <p>4. 影子数据源：shadowDataSource
     *
     */
    @Lazy
    @Resource(name = "masterSlaveDataSource")
    private DataSource masterSlaveDataSource;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
                /**
                 * 将 shardingjdbc 管理的数据源也交给动态数据源管理
                 */
                dataSourceMap.put(SHARDING_DATA_SOURCE_NAME, masterSlaveDataSource);
                return dataSourceMap;
            }
        };
    }


}
