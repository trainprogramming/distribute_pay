package com.appchemist.distribute_pay;

import com.appchemist.distribute_pay.common.ComponentObject;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ComponentObject
@ConfigurationProperties(prefix = "distributepay")
public class DistributePayConfigurationProperties {
    private String redisHost;
    private int redisPort;
    private String mongodbUrl;
    private String mongodbName;
}
