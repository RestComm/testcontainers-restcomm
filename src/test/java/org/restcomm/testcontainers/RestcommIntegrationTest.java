/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2018, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.testcontainers;

import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.restcomm.testcontainers.webdriver.DesiredCapabilities;
import org.restcomm.testcontainers.webdriver.Olympus;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.net.URL;

/**
 * @author oleg.agafonov@telestax.com (Oleg Agafonov)
 */
public abstract class RestcommIntegrationTest {

    public static final String OLYMPUS = "https://127.0.0.1:8443/olympus";

    public static final String ALICE = "alice";

    public static final String BOB = "bob";

    public static final String PASSWORD = "1234";

    @ClassRule
    public static RestcommComposeContainer restcomm =
            new RestcommComposeContainer(new File("src/test/resources/restcomm-docker-compose.yml"));

    @ClassRule
    public static GenericContainer chrome =
            new GenericContainer("selenium/standalone-chrome-debug:3.8.1").withNetworkMode("host");

    protected static Olympus olympus;

    @BeforeClass
    public static void init() throws Exception {
        // Due to '--net host' issue on Mac OS we have to use local chromedriver
        // Make sure that you have it installed or execute 'brew install chromedriver'
        if (!SystemUtils.IS_OS_MAC) {
            olympus = new Olympus(OLYMPUS, new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), DesiredCapabilities.chrome()));
        } else {
            olympus = new Olympus(OLYMPUS, new ChromeDriver(DesiredCapabilities.chromeOptions()));
        }
    }

    @AfterClass
    public static void close() throws Exception {
        olympus.close();
    }
}
