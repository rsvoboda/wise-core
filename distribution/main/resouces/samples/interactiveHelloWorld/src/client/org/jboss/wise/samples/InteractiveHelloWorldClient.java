/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.samples;

import org.jboss.wise.core.client.*;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefano.maestri@javalinux.it
 */
public class InteractiveHelloWorldClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
            WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
                    .wsdlURL("http://127.0.0.1:8080/InteractiveHelloWorld/InteractiveHelloWorldWS?wsdl").build();
            Map<String, WSService> services = client.processServices();
            System.out.println("Available services are:");
            for (String key : services.keySet()) {
                System.out.println(key);
            }
            System.out.println("Selectting the first one");
            Map<String, WSEndpoint> endpoints = services.values().iterator().next().processEndpoints();
            System.out.println("Available endpoints are:");
            for (String key : endpoints.keySet()) {
                System.out.println(key);
            }
            System.out.println("Selectting the first one");
            Map<String, WSMethod> methods = endpoints.values().iterator().next().getWSMethods();
            System.out.println("Available methods are:");
            for (String key : methods.keySet()) {
                System.out.println(key);
            }
            System.out.println("Selectting the first one");
            WSMethod method = methods.values().iterator().next();
            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("toWhom", "SpiderMan");
            InvocationResult result = method.invoke(requestMap, null);
            System.out.println(result.getMapRequestAndResult(null, null));
            System.out.println(result.getMapRequestAndResult(null, requestMap));
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
