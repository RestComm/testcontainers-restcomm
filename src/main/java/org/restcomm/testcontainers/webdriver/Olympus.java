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

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author oleg.agafonov@telestax.com (Oleg Agafonov)
 */
public class Olympus implements AutoCloseable {

    private static final long DEFAULT_TIMEOUT = 30;

    private final String url;

    private final long timeout;

    private final WebDriver driver;

    private final Map<String, String> windows = new HashMap<>();

    public Olympus(String url, WebDriver driver) {
        this(url, DEFAULT_TIMEOUT, driver);
    }

    public Olympus(String url, long timeout, WebDriver driver) {
        this.url = url;
        this.timeout = timeout;
        this.driver = driver;
    }

    public Olympus login(String login, String password) {
        if (windows.isEmpty()) {
            driver.get(url);
        } else {
            ((JavascriptExecutor) driver).executeScript("$(window.open('" + url + "'))");
            switchToNewWindow();
        }
        getElement(By.xpath("//*[@name='username']")).sendKeys(login);
        getElement(By.xpath("//*[@name='password']")).sendKeys(password);
        getElement(By.xpath("//button[@type='submit']")).click();
        getElement(By.xpath("//*[@id='user-name']"));
        String window = driver.getWindowHandle();
        windows.put(login, window);
        return this;
    }

    public Olympus logout(String login) {
        String window = windows.remove(login);
        if (window != null) {
            driver.switchTo().window(window);
            getElement(By.xpath("//a[contains(@ng-click, 'signOut()')]")).click();
            driver.close();
        }
        return this;
    }

    public Olympus call(String login) {
        if (findElement(By.xpath("//*[@id='contact-space-" + login + "']")) == null) {
            addContact(login);
        }
        getElement(By.xpath("//*[@id='contact-space-" + login + "']")).click();
        getElement(By.xpath("//a[contains(@ng-click, 'callContact')]")).click();
        return this;
    }

    public Olympus awaitCallInProgress(String login) {
        getElement(By.xpath("//*[@id='contact-space-" + login + "']")).click();
        return awaitCallInProgress();
    }

    public Olympus awaitCallInProgress() {
        getElement(By.xpath("//*[@id='incoming-call']"));
        return this;
    }

    public Olympus awaitCallFinished(String login) {
        getElement(By.xpath("//*[@id='contact-space-" + login + "']")).click();
        return awaitCallFinished();
    }

    public Olympus awaitCallFinished() {
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='incoming-call']")));
        return this;
    }

    public Olympus hangupCall() {
        getElement(By.xpath("//div[@id='big-video']//a[contains(@ng-click, 'callHangup')]")).click();
        return this;
    }

    public Olympus rejectCall() {
        getElement(By.xpath("//a[contains(@ng-click, 'rejectCall()')]")).click();
        return this;
    }

    public Olympus answerCall() {
        getElement(By.xpath("//a[contains(@ng-click, 'acceptCall(false)')]")).click();
        return this;
    }

    public Olympus switchToWindow(String login) {
        String window = windows.get(login);
        if (window != null) {
            driver.switchTo().window(window);
        }
        return this;
    }

    public Olympus addContact(String login) {
        getElement(By.xpath("//a[@title='Add Contact']")).click();
        getElement(By.xpath("//input[@placeholder='Contact Name']")).sendKeys(login);
        getElement(By.xpath("//input[@placeholder='Contact Address']")).sendKeys(login);
        getElement(By.xpath("//button[text()='Add Contact']")).click();
        return this;
    }

    private void switchToNewWindow() {
        Iterator<String> i = driver.getWindowHandles().iterator();
        String window = null;
        while (i.hasNext()) {
            window = i.next();
        }
        driver.switchTo().window(window);
    }

    private WebElement findElement(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() ? elements.get(0) : null;
    }

    private WebElement getElement(By locator) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public void close() throws Exception {
        windows.forEach((login, window) -> driver.switchTo().window(window).close());
    }
}
