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
package org.jboss.wise.test.integration.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.test.WiseTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RunWith(Arquillian.class)
public class BasicMethodStressTest extends WiseTest {

    private static final int THREADS = Integer.valueOf(System.getProperty("wise.stress.threads"));

    private static final int THREAD_POOL_SIZE = Integer.valueOf(System.getProperty("wise.stress.threadPoolSize"));

    private static final String WAR = "basic";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WAR + ".war");
        archive
                .addClass(org.jboss.wise.test.integration.basic.HelloWorldInterface.class)
                .addClass(org.jboss.wise.test.integration.basic.HelloWorldBean.class)
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/basic/web.xml"));
        return archive;
    }

    @Test
    @RunAsClient
    public void shouldRunWithoutMKNoCacheStressTest() throws Exception {
        System.out.println("running NOMK with THREADS=" + THREADS);

        URL wsdlURL = new URL(getServerHostAndPort() + "/basic/HelloWorld?wsdl");

        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();

        // Note Wise do not provide any Cache, client is expected to take
        // care of using a single client instance or
        // other caching mechanism. Initializing client is very expensive!!
        // You have a proof of that in BasicNoCacheNoClientCacheStressTest.java
        WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlURL
                .toString()).maxThreadPoolSize(30).build();
        WSMethod method = client.getWSMethod("HelloService", "HelloWorldBeanPort", "echo");

        ExecutorService es = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        FutureTask<String>[] tasks = new FutureTask[THREADS];
        for (int i = 0; i < THREADS; i++) {
            tasks[i] = new FutureTask<String>(new NoMKNoCacheCallableTest(method, i));
            es.submit(tasks[i]);

        }
        for (int i = 0; i < THREADS; i++) {
            String result = tasks[i].get();
            System.out.println(i + ") " + result);
            Assert.assertEquals("from-wise-client thread #" + i, result);

        }
        es.shutdown();
        client.close();

    }

    public class NoMKNoCacheCallableTest implements Callable<String> {

        private final WSMethod method;

        private final int count;

        public NoMKNoCacheCallableTest(WSMethod method, int count) {
            this.method = method;
            this.count = count;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.util.concurrent.Callable#call()
         */
        public String call() throws Exception {
            Map<String, Object> args = new java.util.HashMap<String, Object>();
            args.put("arg0", "from-wise-client thread #" + count);
            InvocationResult result = method.invoke(args, null);
            Map<String, Object> res = result.getMapRequestAndResult(null, null);
            Map<String, Object> test = (Map<String, Object>) res.get("results");
            return (String) test.get("result");

        }
    }

}
