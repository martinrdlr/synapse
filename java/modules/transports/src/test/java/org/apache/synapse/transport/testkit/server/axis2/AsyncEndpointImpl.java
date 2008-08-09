/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.transport.testkit.server.axis2;

import java.util.concurrent.TimeUnit;

import org.apache.axis2.description.AxisService;
import org.apache.synapse.transport.testkit.listener.MessageData;
import org.apache.synapse.transport.testkit.listener.MockMessageReceiver;
import org.apache.synapse.transport.testkit.server.AsyncEndpoint;

public class AsyncEndpointImpl extends EndpointImpl implements AsyncEndpoint {
    private final MockMessageReceiver messageReceiver;
    
    public AsyncEndpointImpl(AxisServer<?> server, AxisService service, MockMessageReceiver messageReceiver) {
        super(server, service);
        this.messageReceiver = messageReceiver;
    }

    public MessageData waitForMessage(long timeout, TimeUnit unit) throws Throwable {
        return messageReceiver.waitForMessage(timeout, unit);
    }
}