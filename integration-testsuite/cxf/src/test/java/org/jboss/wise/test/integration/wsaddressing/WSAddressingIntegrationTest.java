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
package org.jboss.wise.test.integration.wsaddressing;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.handlers.LoggingHandler;
import org.jboss.wise.core.test.WiseTest;
import org.jboss.wise.core.wsextensions.impl.WSAddressingEnabler;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;

/**
 * Tests WS-Addressing extension in Wise
 *
 * @author alessio.soldano@jboss.com
 * @since 23-Dic-2008
 */
@RunWith(Arquillian.class)
public class WSAddressingIntegrationTest extends WiseTest {

    private static final String WAR = "wsa";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WAR + ".war");
        archive
                .addClass(org.jboss.wise.test.integration.wsaddressing.HelloImpl.class)
                .addClass(org.jboss.wise.test.integration.wsaddressing.Hello.class)
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/wsa/Hello.wsdl"))
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/wsa/test.wsdl"))
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/wsa/web.xml"));
        return archive;
    }


    @Test
    @RunAsClient
    @SuppressWarnings("unchecked")
    public void shouldRunWithoutMK() throws Exception {
        URL wsdlURL = new URL(getServerHostAndPort() + "/wsa/Hello?wsdl");

        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
        WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlURL
                .toString()).build();
        WSMethod method = client.getWSMethod("HelloService", "HelloImplPort", "echoUserType");
        WSEndpoint wsEndpoint = method.getEndpoint();

        wsEndpoint.addWSExtension(new WSAddressingEnabler(client));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wsEndpoint.addHandler(new LoggingHandler(new PrintStream(baos)));

        Map<String, Object> args = new java.util.HashMap<String, Object>();
        args.put("user", "test");
        InvocationResult result = method.invoke(args, null);
        Map<String, Object> results = (Map<String, Object>) result.getMapRequestAndResult(null, null).get("results");
        client.close();
        Assert.assertEquals("Hello WSAddressing", results.get("result"));
        Assert.assertTrue("Could not find WS-A headers in exchanged messages!", baos.toString().contains("http://www.w3.org/2005/08/addressing"));
    }
}
