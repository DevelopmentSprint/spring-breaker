/**
 * Copyright 2014 Development Sprint, LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.developmentsprint.spring.breaker.hystrix;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
@ActiveProfiles({ "hystrixManager" })
public class HystrixPropertyReloadingTest {

    @Autowired
    private HystrixCircuitManager circuitManager;

    private static File configFile;

    private static String configContent;

    static {
        System.setProperty("archaius.fixedDelayPollingScheduler.initialDelayMills", "1000");
        System.setProperty("archaius.fixedDelayPollingScheduler.delayMills", "1000");
    }

    @BeforeClass
    public static void setup() throws Exception {
        Resource res = new DefaultResourceLoader().getResource("classpath:config.properties");
        configFile = res.getFile();
        configContent = FileUtils.readFileToString(configFile);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.writeStringToFile(configFile, configContent);
    }

    @Test
    public void test() throws Exception {
        String key = "hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds";
        int timeout = Integer.valueOf(circuitManager.getConfiguration().getProperty(key).toString());

        assertThat(timeout).isEqualTo(500);

        FileUtils.writeStringToFile(configFile, key + "=1000");

        Thread.sleep(1500);

        timeout = Integer.valueOf(circuitManager.getConfiguration().getProperty(key).toString());
        assertThat(timeout).isEqualTo(1000);
    }

}
