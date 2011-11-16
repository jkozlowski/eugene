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

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.content.schema.ConceptSchema;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.jademx.util.MBeanUtil;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREInitiator;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

import javax.management.*;

/** 
 * sample agent to demonstrate building agent using jademx.
 * @author David Bernstein, <a href="http://www.caboodlenetworks.com"
 *  >Caboodle Networks, Inc.</a>
 */
public class JademxPingAgent extends JademxAgent {

    /**
     * make a Jademx ping agent 
     */
    public JademxPingAgent() {
        super();
        mBeanInfoThisLevel = constructMBeanInfo();
        logger.log(Logger.FINE,"made MBeanInfo "+mBeanInfoThisLevel);
        mBeanInfoSuper = super.getMBeanInfo();
        mBeanInfoMerged = MBeanUtil.mergeMBeanInfo(
                getClass().getName(), DESCRIPTION,
                mBeanInfoThisLevel, mBeanInfoSuper);
        logger.log(Logger.FINE,"made merged MBeanInfo "+mBeanInfoMerged);
        //setMBeanInfo( constructMBeanInfo() );
    }

    /** default answer */
    public final static String DEFAULT_ANSWER = "pong";
    /**
     * response to a ping.
     * access must be synchronized because it's accessed both by the
     * MBeanServer thread and by the JADE agent thread.  
     */
    private String response = DEFAULT_ANSWER;
    /** my logger */
    private final Logger logger =
        Logger.getMyLogger(JademxPingAgent.class.getName());
    /** my codec */
    private Codec codec = new SLCodec();
    /** my ontology */
    private Ontology ontology = PingOntology.getInstance();
    /** my content manager */
    private ContentManager contentManager = null;
    
    // MBEAN FEATURE INFO
    
    // attributes
    
    /** response attribute name */
    public final static String ATTR_RESPONSE_NAME = "Response";
    /** response attribute description */
    private final static String ATTR_RESPONSE_DESC = "ping response";
    /** response attribute type */
    private final static String ATTR_RESPONSE_TYPE = String.class.getName();
    
    // operations
    
    /** ping operation name */
    public final static String OPER_PING_NAME = "ping";
    /** ping operation description */
    private final static String OPER_PING_DESC = "ping another agent";
    /** ping agentFullName parameter name */
    private final static String OPER_PING_AGENT_FULL_NAME_PARM_NAME = 
        "agentFullName";
    /** ping agentFullName parameter type */
    private final static String OPER_PING_AGENT_FULL_NAME_PARM_TYPE = 
        String.class.getName();
    /** ping agentFullName parameter description */
    private final static String OPER_PING_AGENT_FULL_NAME_PARM_DESC = 
        "full JADE name of agent that pinged this agent";
    /** signature for a ping operation */
    public final static String OPER_PING_SIGNATURE[] = { 
            OPER_PING_AGENT_FULL_NAME_PARM_TYPE 
    };
    /** return type for ping operation */
    public final static String OPER_PING_TYPE = String.class.getName();
    
    // nofifications
    
    /** notification that have been pinged */
    public final static String NOTIF_PINGED_NAME = "pinged";

    
    // END MBEAN FEATURE INFO

    /** description for MBeanInfo */
    private final static String DESCRIPTION = 
        "sample ping agent under Jademx control";
    
    
    /** MBeanInfo for this class and superclass(es) */
    private MBeanInfo mBeanInfoMerged = null;
    /** MBeanInfo for superclass(es) */
    private MBeanInfo mBeanInfoSuper = null;
    /** MBeanInfo for for this class but not superclass(es) */
    private MBeanInfo mBeanInfoThisLevel = null;
    
    /**
     * create a new MBeanInfo object to describe this agent.
     * @return new MBeanInfo object to describe this agent.
     */
    private MBeanInfo constructMBeanInfo() {
        
        // attributes

        MBeanAttributeInfo aI[] = new MBeanAttributeInfo[] {
                new MBeanAttributeInfo( 
                        ATTR_RESPONSE_NAME, 
                        ATTR_RESPONSE_TYPE, 
                        ATTR_RESPONSE_DESC,
                        true, true, false )
        };
        
        // constructors

        MBeanConstructorInfo cI[] = new MBeanConstructorInfo[0];
        
        // operations

        MBeanParameterInfo pIPing[] = { 
                new MBeanParameterInfo( 
                        OPER_PING_AGENT_FULL_NAME_PARM_NAME, 
                        OPER_PING_AGENT_FULL_NAME_PARM_TYPE, 
                        OPER_PING_AGENT_FULL_NAME_PARM_DESC) 
        };
        MBeanOperationInfo oI[] = new MBeanOperationInfo[] {
                new MBeanOperationInfo( 
                        OPER_PING_NAME, 
                        OPER_PING_DESC, 
                        pIPing, 
                        OPER_PING_TYPE, 
                        MBeanOperationInfo.INFO )
        };
        
        // notifications
        
        String notifications[] = {
                NOTIF_PINGED_NAME 
        };
        String NOTIF_INFO_DESCRIPTION = 
            "notification set for " + getClass().getName();
        MBeanNotificationInfo nI[] = new MBeanNotificationInfo[] {
                new MBeanNotificationInfo(
                        notifications,
                        Notification.class.getName(),
                        NOTIF_INFO_DESCRIPTION )
        };
        
        // now, MBeanInfo for this level of class hierarchy
        MBeanInfo mBeanInfo = new MBeanInfo( getClass().getName(), 
                DESCRIPTION,
                aI, cI, oI, nI );
        
        return mBeanInfo;
        
    }

    
    /**
     * this is a merge of the agent, this class, and this class's superclass.
     * @return MBean information for ping agent including JademxAgent
     */
    public MBeanInfo getMBeanInfo() {
        return mBeanInfoMerged;
    }
    
    
    
    /* (non-Javadoc)
     * @see jade.core.Agent#setup()
     */
    protected void setup() {
        // make sure set up as a JademxAgent
        super.setup();
        // get content manager set up
        contentManager = getContentManager();
        contentManager.registerLanguage( codec );
        contentManager.registerOntology( ontology );
        // add the PingResponderBehaviour
        addBehaviour( 
                new PingResponder( 
                    this, 
                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST) ) );
    }
    // ACTUAL ATTRIBUTE AND OPERATION
    
    /** count pings for benefit of reply-with in messages */
    private int pingCount = 0;
    /**
     * perform ping operation
     * @param pingeeAgentFullName full JADE name of agent to ping
     * @return current response
     */
    public String ping( String pingeeAgentFullName ) throws Exception {
        
        // get the ping message ready
        
        ACLMessage request = new ACLMessage( ACLMessage.REQUEST );
        AID receiver = new AID( pingeeAgentFullName, AID.ISGUID );
        request.addReceiver( receiver );
        request.setConversationId( "ping-conv-" + 
                Long.toString( System.currentTimeMillis() ) +
                "-" + 
                Long.toString( this.hashCode() ) );
        request.setLanguage( codec.getName() );
        request.setOntology( ontology.getName() );
        request.setProtocol( FIPANames.InteractionProtocol.FIPA_REQUEST );
        request.setReplyWith( "ping" + Integer.toString( ++pingCount ) );
        request.setSender( getAID() );
        
        Ping ping = new Ping();
        Action pingAction = new Action();
        pingAction.setActor( receiver );
        pingAction.setAction( ping );
        
        contentManager.fillContent( request, pingAction );
        
        logger.log( Logger.FINE, "request: "+request);
        
        // start behaviour to perform ping and wait for response.
        // OK to use wait() because this is a thread belonging to the 
        // MBeanServer and the behaviour runs in a different thread.

        String    response = null;
        Exception exception = null;
        PingResponse pingResponse = new PingResponse();
        synchronized( pingResponse ) {
            // enqueue behaviour to perform ping
            addBehaviour( new PingInitiator( this, request, pingResponse ) );
            // block until have response
            while ( ( null == ( exception = pingResponse.getException() ) ) && 
                    ( null == ( response  = pingResponse.getResponse()  ) ) ) {
                pingResponse.wait();
            }
        }

        // if got an exception performing ping, re-throw it

        if ( null != exception ) {
            throw exception;
        }
        
        // return the response
        
        return response;
    }
    
    /**
     * set the response
     * @param response response to set
     */
    public void setResponse( String response ) {
        synchronized ( this.response ) {
            this.response = response;
        }
    }
    
    /**
     * get response
     * @return response
     */
    public String getResponse() {
        synchronized ( this.response ) {
            return response;
        }
    }
    
    // MBEAN INTERFACE TO ATTRIBUTE AND OPERATION
    
    /**
     * get an attribute value
     * @param attribute name of attribute to get
     * @return attribute value
     * @throws AttributeNotFoundException no such attribute
     * @throws MBeanException exception from getter
     * @throws ReflectionException exception invoking getter
     */
    public Object getAttribute( String attribute ) 
      throws AttributeNotFoundException,
             MBeanException, 
             ReflectionException {
        Object o;
        // attributes known about directly at this level        
        if ( ATTR_RESPONSE_NAME.equals( attribute ) ) {
            o = getResponse();
        }
        // attributes known by parent class
        else {
            o = super.getAttribute( attribute );            
        }
        return o;
    }

    /**
     * @param attribute
     * @throws AttributeNotFoundException no such attribte
     * @throws InvalidAttributeValueException bad attribute value
     * @throws MBeanException exception from setter
     * @throws ReflectionException exception invoking setter
     */
    public void setAttribute( Attribute attribute )
      throws AttributeNotFoundException,
             InvalidAttributeValueException,
             MBeanException,
             ReflectionException {
 
        String attributeName = attribute.getName();
        Object v = attribute.getValue();
        // attributes known about directly at this level
        if ( ATTR_RESPONSE_NAME.equals( attributeName ) ) {
            String s;
            try {
                s = (String) v;
            }
            catch ( ClassCastException cce ){
                throw new InvalidAttributeValueException( "for attribute "+
                        " need "+String.class.getName()+" got "+
                        v.getClass().getName() );
            }
            try {
                setResponse( s );
                // invoking directly, not indirectly, 
                // so can't throw ReflectionException
            }
            catch ( Exception e ) {
                throw new MBeanException( e,
                        "exception setting "+ATTR_RESPONSE_NAME+" attribute" );
            }
        }
        // attributes known by parent class
        else {
            super.setAttribute( attribute );
        }
    }


    /**
     * invoke an action
     * @param actionName name of action to invoke
     * @param params action parameters
     * @param signature action signature
     * @return object result
     * @exception MBeanException wrap action exception
     * @exception ReflectionException wrap action invocation exception
     */
    public Object invoke(String actionName, Object params[], String signature[])
	  throws MBeanException, ReflectionException {
        Object o = null;
        if ( OPER_PING_NAME.equals( actionName ) && 
             MBeanUtil.signaturesEqual(OPER_PING_SIGNATURE, signature) ) {
            try {
                o = ping( (String)params[0] );
            }
            catch ( Exception e ) {
                throw new MBeanException( e, 
                        "exception invoking "+OPER_PING_NAME+" operation");
            }
        }
        else {
            super.invoke( actionName, params, signature );
        }
        return o;
    }
    
    /** name of ping schema */
    public static final String PING = "ping";

    /** the ping ontology */
    private static class PingOntology extends Ontology {
        /** constant identifying the ontology name **/
        public static final String NAME = "ping";
        /** the singleton instance */
        private static Ontology theInstance = new PingOntology();
        /** return singleton instance */
        public static Ontology getInstance() {
            return theInstance;
        }
        /** make a ping ontology */
        private PingOntology() {
            super( NAME, BasicOntology.getInstance());
            try {
                ConceptSchema pingSchema = new ConceptSchema( PING );
                add( pingSchema, Ping.class );
            } 
            catch ( OntologyException oe ) {
                oe.printStackTrace();
                throw new RuntimeException( "Unrecoverable OntologyException",
                        oe );
            }
        }
    }
    
    
    /** 
     * response to ping.
     * meant as wrapper to be handled with <code>Object.wait()</code> and
     * <code>Object.notify()</code>.
     */
    private class PingResponse {
        /** hold the response */
        private String response = null;
        /** hold any Exception generated trying to get response */
        private Exception exception = null;
        /**
         * get the ping response
         * @return Returns the response.
         */
        public String getResponse() {
            return response;
        }
        /**
         * set the ping response
         * @param response The response to set.
         */
        public void setResponse(String response) {
            this.response = response;
        }
        /**
         * get any exception set
         * @return Returns the exception
         */
        public Exception  getException() {
            return exception;
        }
        /**
         * set a exception received trying to get response
         * @param exception The exception to set.
         */
        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
    
    
    /** 
     * behaviour to send a ping and get response
     */
    private class PingInitiator extends SimpleAchieveREInitiator {
        
        /** ping response object */
        private PingResponse pingResponse;

        /**
         * @param a my agent
         * @param msg message to send
         * @param pingResponse PingResponse object to set and notify()
         */
        public PingInitiator( 
                Agent a,
                ACLMessage msg,
                PingResponse pingResponse ) {
            super( a, msg );
            this.pingResponse = pingResponse;
        }
        
        /* (non-Javadoc)
         * @see jade.proto.SimpleAchieveREInitiator#handleInform(
         * jade.lang.acl.ACLMessage)
         */
        protected void handleInform( ACLMessage msg ) {
            logger.log( Logger.FINE, "ping response:"+msg);
            String response = null;
            synchronized( pingResponse ) {
                // extract content
                Result result = null;
                try {
                    result = (Result)contentManager.extractContent( msg );
                    response = (String)result.getValue();
                    // set the response in the response object
                    pingResponse.setResponse( response );
                }
                catch ( Exception e ) {
                    // let ping() know that encountered a problem
                    pingResponse.setException( e );
                }
                catch ( Throwable t ) {
                    // this way the MBeanServer won't wait unnecessarily
                    pingResponse.setException( new RuntimeException(
                            "throwable encountered performing ping", t ) );
                }
                // notify waiting thread that response has been received
                pingResponse.notify();
            }
        }
    }
    
    
    /** 
     * behaviour to respond to a ping
     */
    private class PingResponder extends SimpleAchieveREResponder {
        
        /** my content manager */
        ContentManager contentManager;
        
        /**
         * @param a my agent
         * @param mt message template to use
         */
        public PingResponder( JademxPingAgent a, MessageTemplate mt ) {
            super(a, mt);
            pingAgent = a;
            contentManager = a.getContentManager();
        }
        /** my typed agent */
        private JademxPingAgent pingAgent;
        
        /* (non-Javadoc)
         * @see jade.proto.SimpleAchieveREResponder#prepareResponse(
         * jade.lang.acl.ACLMessage)
         */
        protected ACLMessage prepareResponse( ACLMessage request )
                throws NotUnderstoodException, RefuseException {
            
            logger.log( Logger.FINE, "ping request: "+request);

            // extract content
            Action action;
            try {
                action = (Action)contentManager.extractContent( request );
                
            }
            catch ( Exception e ) {
                e.printStackTrace();
                throw new NotUnderstoodException(
                        "didn't understand "+request+":"+ e );
            } 
            
            // create reply
            ACLMessage reply = request.createReply();
            reply.setPerformative( ACLMessage.INFORM );
            Result result = new Result( action, pingAgent.getResponse() );
            try {
                contentManager.fillContent( reply, result );
            }
            catch ( Exception e ) {
                throw new RefuseException(
                        "can't create reply to "+request+":"+ e );
            }             
            
            // notify any JMX listeners that we were pinged
            String pinger = request.getSender().getName();
            pingAgent.notifyListeners( 
                    NOTIF_PINGED_NAME, 
                    "this agent was pinged by "+pinger, 
                    pinger );

            // send back reply
            logger.log( Logger.FINE, "reply: "+reply);
            return reply;
        }
    }
    
}
