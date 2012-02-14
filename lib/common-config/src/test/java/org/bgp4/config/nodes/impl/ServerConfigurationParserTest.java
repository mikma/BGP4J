/**
 *  Copyright 2012 Rainer Bieniek (Rainer.Bieniek@web.de)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * File: org.bgp4.config.nodes.impl.ServerConfigurationParserTest.java 
 */
package org.bgp4.config.nodes.impl;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.bgp4.config.ConfigTestBase;
import org.bgp4.config.nodes.ServerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Rainer Bieniek (Rainer.Bieniek@web.de)
 *
 */
public class ServerConfigurationParserTest extends ConfigTestBase {

	@Before
	public void before() throws Exception {
		this.config = loadConfiguration("config/nodes/ServerConfig.xml");
		this.parser = obtainInstance(ServerConfigurationParser.class);
	}
	
	@After
	public void after() {
		this.config = null;
		this.parser = null;
	}
	
	private XMLConfiguration config;
	private ServerConfigurationParser parser;
	
	@Test
	public void testDefaultServerConfig() throws ConfigurationException {
		ServerConfiguration serverConfig = parser.parseConfig(config.configurationAt("Server(0)"));
		
		Assert.assertEquals(0, serverConfig.getListenAddress().getPort());
		Assert.assertEquals("0.0.0.0", serverConfig.getListenAddress().getHostName());
	}
	
	@Test
	public void testPortOnlyServerConfig() throws ConfigurationException {
		ServerConfiguration serverConfig = parser.parseConfig(config.configurationAt("Server(1)"));
		
		Assert.assertEquals(17179, serverConfig.getListenAddress().getPort());
		Assert.assertEquals("0.0.0.0", serverConfig.getListenAddress().getHostName());
	}
	
	@Test
	public void testAddressOnlyServerConfig() throws ConfigurationException {
		ServerConfiguration serverConfig = parser.parseConfig(config.configurationAt("Server(2)"));
		
		Assert.assertEquals(0, serverConfig.getListenAddress().getPort());
		Assert.assertEquals("192.168.4.1", serverConfig.getListenAddress().getHostName());
	}
	
	@Test
	public void testAddressPortServerConfig() throws ConfigurationException {
		ServerConfiguration serverConfig = parser.parseConfig(config.configurationAt("Server(3)"));
		
		Assert.assertEquals(17179, serverConfig.getListenAddress().getPort());
		Assert.assertEquals("192.168.4.1", serverConfig.getListenAddress().getHostName());
	}
}
