package jade.jademx.agent;

import jade.jademx.config.jademx.onto.ConfigAgentSpecifier;
import jade.jademx.config.jademx.onto.ConfigPlatform;
import jade.jademx.config.jademx.onto.ConfigRuntime;
import jade.jademx.config.jademx.onto.JademxConfig;
import jade.jademx.mbean.JadeFactory;
import jade.jademx.mbean.JadePlatformMBean;
import jade.jademx.mbean.JadeRuntimeMBean;
import jade.jademx.server.JadeMXServer;
import jade.jademx.server.JadeMXServerFactory;
import jade.jademx.util.AclMsgCmp;
import jade.jademx.util.ThrowableUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import static org.junit.Assert.fail;

/**
 * @author Jakub D Kozlowski
 */
public class UnitTestingTest {

    // original inject message before AIDs localized
    //  "(REQUEST"+
    //  " :sender  ( agent-identifier :name pinger@diamond:1099/JADE )"+
    //  " :receiver  (set ( agent-identifier :name pingee@diamond:1099/JADE ) )"+
    //  " :content  \"((action (agent-identifier :name pingee@diamond:1099/JADE) (ping)))\" "+
    //  " :reply-with  ping1  :language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
    //  " :conversation-id  ping-conv-1127942689278-18131271 )";

    /** reply-id used in message to inject */
    private static final String INJECT_REPLY_ID = "pinger@diamond:1099/JADE1127942689278";
    /** AID for pingee agent in message to inject */
    private static final String PINGEE_INJECT_AID = "pingee@diamond:1099/JADE";
    /** AID for pinger agent in message to inject */
    private static final String PINGER_INJECT_AID = "pinger@diamond:1099/JADE";
    /** local name for pingee agent */
    private static final String PINGEE_LOCAL_NAME = "pingee";

    /** message to inject */
    private final static String injectMsg =
        "(REQUEST"+
        " :sender  ( agent-identifier :name pinger )"+
        " :receiver  (set ( agent-identifier :name pingee ) )"+
        " :content  \"((action (agent-identifier :name pingee@diamond:1099/JADE) (ping)))\" "+
        " :reply-with  ping1  :language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
        " :conversation-id  ping-conv-1127942689278-18131271 )";

    /** message to expect */
    private final static String expectMsg =
        "(INFORM"+
        " :sender  ( agent-identifier :name pingee@diamond:1099/JADE )"+
        " :receiver  (set ( agent-identifier :name pinger@diamond:1099/JADE ) )"+
        " :content  \"((result (action (agent-identifier :name pingee@diamond:1099/JADE) (ping)) pong))\" "+
        " :reply-with  pinger@diamond:1099/JADE1127942689278  :in-reply-to  ping1  "+
        ":language  fipa-sl  :ontology  ping  :protocol  fipa-request"+
        " :conversation-id  ping-conv-1127942689278-18131271 )";

    // SETUP/TEARDOWN

    /** MBeanServer being used */
    private static MBeanServer mBeanServer = null;
    /** created JadeRuntimeMBean */
    private static JadeRuntimeMBean runtime = null;
    /** ObjectName for JadeRuntimeMBean */
    private static ObjectName runtimeON = null;

    @BeforeClass
    public static void setUp() throws Exception {
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
        JadeFactory jadeFactory = new JadeFactory( jadeMXServer );
        // instantiate the configuration
        try {
            runtime = jadeFactory.instantiateRuntime( jademxConfig );
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("problem instantiating configuration",e));
        }
        // get ObjectName for runtime
        runtimeON = runtime.getObjectName();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // shutdown the configuration
        try {
            mBeanServer.invoke( runtimeON,
                    JadeRuntimeMBean.OPER_SHUTDOWN,
                    new Object[]{},
                    JadeRuntimeMBean.OPER_SHUTDOWN_SIGNATURE );
        }
        catch ( Exception e ) {
            fail(ThrowableUtil.errMsg("problem shutting down JADE runtime", e));
        }
    }

    /**
     * make a configuration for testing use programmatically
     */
    private static JademxConfig buildPingerPingeeAgentCfg() {
        JademxConfig jademxConfig = new JademxConfig();
        ConfigRuntime runtime = new ConfigRuntime();
        jademxConfig.addRuntime( runtime );
        ConfigPlatform platform = new ConfigPlatform();
        runtime.addPlatform( platform );
        platform.addOption( "port=1917" );
        platform.addOption( "nomtp=true" );
        ConfigAgentSpecifier agentSpecifier;
        agentSpecifier =
            new ConfigAgentSpecifier(
                    "pinger",
                    "jade.jademx.agent.JademxNopAgent" );
        platform.addAgentSpecifier( agentSpecifier );
        agentSpecifier =
            new ConfigAgentSpecifier(
                    PINGEE_LOCAL_NAME,
                    "jade.jademx.agent.JademxPingAgent" );
        platform.addAgentSpecifier( agentSpecifier );
        return jademxConfig;
    }

    // TESTS

    /** test unit testing with calls via MBeanServer */
    @Test
    public void testUnitTest() {
        System.out.println("***********************************************************************");
        // find the agent MBean
        ObjectName agentON = null;
        JademxPingAgentProxy pingAgentProxy = null;
        try {
            ObjectName platformONs[] =
                (ObjectName[])mBeanServer.getAttribute(
                        runtimeON, JadeRuntimeMBean.ATTR_PLATFORM_OBJECT_NAMES);
            ObjectName platformON = platformONs[0];
            agentON = (ObjectName)mBeanServer.invoke( platformON,
                    JadePlatformMBean.OPER_GET_AGENT_OBJECT_NAME,
                    new Object[]{PINGEE_LOCAL_NAME},
                    JadePlatformMBean.OPER_GET_AGENT_OBJECT_NAME_SIGNATURE );
            if ( null == agentON ) {
                throw new Exception("null agent ObjectName");
            }
            pingAgentProxy = new JademxPingAgentProxy( agentON, mBeanServer );
        }
        catch (Exception e) {
            fail(ThrowableUtil.errMsg("problem finding agent MBean", e));
        }

        // make sure agent MBean bound to JademxAgent
        JademxAgent.assertIsJademxAgent( agentON, mBeanServer );

        // set unit testing attributes on agent
        try {
            // set the message to inject
            pingAgentProxy.setUnitTestInjectMessage( injectMsg );

            // set the expected message
            pingAgentProxy.setUnitTestExpectedMessage( expectMsg );

            // set message comparison variable properties
            pingAgentProxy.unitTestAddVariable( PINGER_INJECT_AID, AclMsgCmp.AID_MAP );
            pingAgentProxy.unitTestAddVariable( PINGEE_INJECT_AID, AclMsgCmp.AID_MAP );
            pingAgentProxy.unitTestAddVariable( INJECT_REPLY_ID, AclMsgCmp.REPLY_ID_MAP );
        }
        catch ( Exception e ) {
            fail(ThrowableUtil.errMsg("problem setting agent test setup", e));
        }

        // assert the expected message
        JademxAgent.assertExpectedMessage( agentON, mBeanServer );

    }
}
