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
 * but OUT ANY WARRANTY; out even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along  this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.testcontainers.webdriver;

import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author oleg.agafonov@telestax.com (Oleg Agafonov)
 */
public class DesiredCapabilities {

    public static org.openqa.selenium.remote.DesiredCapabilities chrome() {
        return org.openqa.selenium.remote.DesiredCapabilities.chrome()
                .merge(chromeOptions());
    }

    public static ChromeOptions chromeOptions() {
        return new ChromeOptions()
                .addArguments("--disable-user-media-security")
                .addArguments("--use-fake-ui-for-media-stream")
                .addArguments("--use-fake-device-for-media-stream")
                .addArguments("--disable-web-security")
                .addArguments("--reduce-security-for-testing");
    }
}
