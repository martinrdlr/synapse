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

package org.apache.synapse.transport.testkit.listener;

import org.apache.synapse.transport.testkit.TestEnvironment;
import org.apache.synapse.transport.testkit.client.AsyncTestClient;
import org.apache.synapse.transport.testkit.client.ClientOptions;
import org.apache.synapse.transport.testkit.server.AsyncEndpoint;
import org.apache.synapse.transport.testkit.server.AsyncEndpointFactory;
import org.apache.synapse.transport.testkit.tests.TransportTestCase;

public abstract class AsyncMessageTestCase<E extends TestEnvironment,C extends AsyncChannel<? super E>,M,N> extends TransportTestCase<E,C,AsyncTestClient<? super E,? super C,M>> {
    private final String charset;
    private final AsyncEndpointFactory<? super E,? super C,N> endpointFactory;
    
    public AsyncMessageTestCase(E env, C channel, AsyncTestClient<? super E,? super C,M> client, AsyncEndpointFactory<? super E,? super C,N> endpointFactory, ContentTypeMode contentTypeMode, String contentType, String charset) {
        super(env, channel, client, endpointFactory.getServer(), contentTypeMode, contentType);
        this.endpointFactory = endpointFactory;
        this.charset = charset;
    }

    @Override
    protected void runTest() throws Throwable {
        AsyncEndpoint<N> endpoint = endpointFactory.createAsyncEndpoint(env, channel, contentTypeMode == ContentTypeMode.SERVICE ? contentType : null);
        
        M message = prepareMessage();
        
        // Run the test.
        N messageData;
        try {
            ClientOptions options = new ClientOptions(endpoint.getEPR(), charset);
//                    contentTypeMode == ContentTypeMode.TRANSPORT ? contentType : null);
            client.sendMessage(channel, options, message);
            messageData = endpoint.waitForMessage(8000);
            if (messageData == null) {
                fail("Failed to get message");
            }
        }
        finally {
            endpoint.remove();
        }
        
        checkMessageData(message, messageData);
    }
    
    protected abstract M prepareMessage() throws Exception;
    protected abstract void checkMessageData(M message, N messageData) throws Exception;
}