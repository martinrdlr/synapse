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

package org.apache.synapse.samples.framework.tests.proxy;

import org.apache.synapse.samples.framework.SampleClientResult;
import org.apache.synapse.samples.framework.SynapseTestCase;
import org.apache.synapse.samples.framework.clients.StockQuoteSampleClient;

public class Sample153 extends SynapseTestCase {

    private StockQuoteSampleClient client;

    public Sample153() {
        super(153);
        client = getStockQuoteClient();
    }


    public void testsRoutingWithoutProcessingSecurityHeaders() {
        String url = "http://localhost:8280/services/StockQuoteProxy";
        String policy = "./repository/conf/sample/resources/policy/client_policy_3.xml";
        log.info("Running test: Routing the messages arrived to a proxy service without " +
                "processing the security headers");
        SampleClientResult  result = client.requestStandardQuote(null, url, null, "IBM", policy);
        assertTrue("Client did not a response with https ", result.responseReceived());
    }

}
