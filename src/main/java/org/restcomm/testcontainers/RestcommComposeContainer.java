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

package org.restcomm.testcontainers;

import com.jayway.awaitility.Awaitility;
import org.apache.http.client.fluent.Request;
import org.junit.runner.Description;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author oleg.agafonov@telestax.com (Oleg Agafonov)
 */
public class RestcommComposeContainer extends DockerComposeContainer {

    public RestcommComposeContainer(File... composeFiles) {
        super(composeFiles);
        withLocalCompose(true);
    }

    @Override
    public void starting(Description description) {
        super.starting(description);
        Awaitility.await().atMost(60, TimeUnit.SECONDS).pollInterval(2, TimeUnit.SECONDS).until(() -> {
                    try {
                        return Request.Get("http://127.0.0.1:8080/olympus").execute()
                                .returnResponse()
                                .getStatusLine()
                                .getStatusCode() == 200;
                    } catch (Exception e) {
                        return false;
                    }
                }
        );
    }
}
