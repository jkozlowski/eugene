// jademx - JADE management using JMX
// Copyright 2005 Caboodle Networks, Inc.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA

package jade.jademx.agent;

import jade.jademx.config.jademx.onto.ConfigAgentSpecifier;
import jade.jademx.config.jademx.onto.ConfigPlatform;
import jade.jademx.config.jademx.onto.ConfigRuntime;
import jade.jademx.config.jademx.onto.JademxConfig;
import jade.jademx.mbean.JadeFactory;
import jade.jademx.mbean.JadeRuntimeMBean;
import jade.jademx.server.JadeMXServer;
import jade.jademx.server.JadeMXServerFactory;
import jade.jademx.util.ThrowableUtil;
import jade.util.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * JademxAgentProxy test
 *
 * @author David Bernstein, <a href="http://www.caboodlenetworks.com"
 *         >Caboodle Networks, Inc.</a>
 */
@RunWith(JUnit4TestRunner.class)
public class JademxAgentProxyTest {

    @Configuration
    public static Option[] configure() {
        return options(
                junitBundles(),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.log").version("1.0.0"),
                mavenBundle().groupId("jade").artifactId("jade-osgi").versionAsInProject(),
                mavenBundle().groupId("jade").artifactId("jademx").versionAsInProject()
        );
    }

    /**
     * my logger
     */
    private final Logger logger =
            Logger.getMyLogger(JademxAgentProxyTest.class.getName());

    // original inject message before AIDs localized
    //  "(REQUEST"+
    //  " :sender  ( agent-identifier :name pinger@diamond:1099/JADE )"+
    //  " :receiver  (set ( agent-identifier :name pingee@diamond:1099/JADE ) )"+
    //  " :content  \"((action (agent-identifier :name pingee@diamond:1099/JADE) (ping)))\" "+
    //  " :reply-with  ping1  :language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
    //  " :conversation-id  ping-conv-1127942689278-18131271 )";

//    /** reply-id used in message to inject */
//    private static final String INJECT_REPLY_ID = "pinger@diamond:1099/JADE1127942689278";
//    /** AID for pingee agent in message to inject */
//    private static final String PINGEE_INJECT_AID = "pingee@diamond:1099/JADE";
//    /** AID for pinger agent in message to inject */
//    private static final String PINGER_INJECT_AID = "pinger@diamond:1099/JADE";

    /**
     * local name for pingee agent
     */
    private static final String PINGEE_LOCAL_NAME = "pingee";

//    /** message to inject */
//    private final static String injectMsg = 
//        "(REQUEST"+
//        " :sender  ( agent-identifier :name pinger )"+
//        " :receiver  (set ( agent-identifier :name pingee ) )"+
//        " :reply-to (set ( agent-identifier :name pinger ) )"+
//        " :content  \"((action (agent-identifier :name pingee@diamond:1099/JADE) (ping)))\" "+
//        " :reply-with  ping1  :language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
//        " :conversation-id  ping-conv-1127942689278-18131271 )";
//    
//    /** message to expect */
//    private final static String expectMsg = 
//        "(INFORM"+
//        " :sender  ( agent-identifier :name pingee@diamond:1099/JADE )"+
//        " :receiver  (set ( agent-identifier :name pinger@diamond:1099/JADE ) )"+
//        " :content  \"((result (action (agent-identifier :name pingee@diamond:1099/JADE) (ping)) pong))\" "+
//        " :reply-with  pinger@diamond:1099/JADE1127942689278  :in-reply-to  ping1  "+
//        ":language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
//        " :conversation-id  ping-conv-1127942689278-18131271 )";

    // SETUP/TEARDOWN

    /**
     * MBeanServer being used
     */
    private MBeanServer mBeanServer = null;

    /**
     * created JadeRuntimeMBean
     */
    private JadeRuntimeMBean runtime = null;

    /**
     * ObjectName for JadeRuntimeMBean
     */
    private ObjectName runtimeON = null;


    /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
    @Before
    public void setUp() throws Exception {
        // create a jademx config of one ping agent
        JademxConfig jademxConfig = buildPingerPingeeAgentCfg();
        // start the configuration
        // first get JadeMXServer
        JadeMXServer jadeMXServer = null;
        try {
            jadeMXServer = JadeMXServerFactory.jadeMXServerBySysProp();
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("problem getting jademx server", e));
        }
        mBeanServer = jadeMXServer.getMBeanServer();
        // now get a factory to use
        JadeFactory jadeFactory = new JadeFactory(jadeMXServer);
        // instantiate the configuration
        try {
            runtime = jadeFactory.runtimeInstance();
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("problem instantiating configuration", e));
        }
        // get ObjectName for runtime
        runtimeON = runtime.getObjectName();
        logger.log(Logger.FINE, "runtimeON:" + runtimeON);
    }

    /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
    @After
    public void tearDown() throws Exception {
        // shutdown the configuration
        try {
            mBeanServer.invoke(runtimeON,
                               JadeRuntimeMBean.OPER_SHUTDOWN,
                               new Object[]{},
                               JadeRuntimeMBean.OPER_SHUTDOWN_SIGNATURE);
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("problem shutting down JADE runtime", e));
        }
    }

    /**
     * make a configuration for testing use programmatically
     */
    private JademxConfig buildPingerPingeeAgentCfg() {
        JademxConfig jademxConfig = new JademxConfig();
        ConfigRuntime runtime = new ConfigRuntime();
        jademxConfig.addRuntime(runtime);
        ConfigPlatform platform = new ConfigPlatform();
        runtime.addPlatform(platform);
        platform.addOption("port=1917");
        platform.addOption("nomtp=true");
        ConfigAgentSpecifier agentSpecifier;
        agentSpecifier =
                new ConfigAgentSpecifier(
                        "pinger",
                        "jade.jademx.agent.JademxNopAgent");
        platform.addAgentSpecifier(agentSpecifier);
        agentSpecifier =
                new ConfigAgentSpecifier(
                        PINGEE_LOCAL_NAME,
                        "jade.jademx.agent.JademxPingAgent");
        platform.addAgentSpecifier(agentSpecifier);
        logger.log(Logger.FINE, "jademxconfig:" + jademxConfig);
        return jademxConfig;
    }

    // TESTS

    /**
     * test null ObjectName to constructor
     */
//    @Test
//    public void testConstructorNullObjectName() {
//        System.out.println("******************************************************");
//        try {
//            new JademxPingAgentProxy(null, mBeanServer);
//            fail("missed null object name to proxy constructor");
//        }
//        catch (IllegalArgumentException iae) {
//            assertTrue(true);
//        }
//    }


    /**
     * test null MBeanServer to constructor
     */
    @Test
    public void testConstructorNullMBeanServer() {
        ObjectName on = null;
        try {
            on = new ObjectName("x:foo=bar");
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("failed to create ObjectName", e));
        }
        try {
            new JademxPingAgentProxy(on, null);
            fail("missed null MBean server to proxy constructor");
        }
        catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }
}
