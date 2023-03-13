package com.kedacom.avcs.dispatch.device.bind;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author liuyue
 * @date 2022/4/13 9:15
 */
@Configuration
@ComponentScan(basePackages = {"com.kedacom.avcs.dispatch.device.bind"})
@EnableJpaAuditing
@EnableJpaRepositories
@EntityScan
public class DeviceBindDataConfiguration {
}
