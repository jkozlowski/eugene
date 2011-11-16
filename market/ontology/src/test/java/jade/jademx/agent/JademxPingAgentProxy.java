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

import jade.jademx.mbean.JademxException;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/** 
 * proxy agent corresponding to JademxPingAgent.
 * The purpose of this class is to have access to the dynamic JMX interface
 * presented by a JademxAgent without having to use the cumbersome dynamic
 * syntax for interacting with that interface.
 * @author David Bernstein, <a href="http://www.caboodlenetworks.com"
 *  >Caboodle Networks, Inc.</a>
 */
public class JademxPingAgentProxy extends JademxAgentProxy {
    
    /**
     * make a JademxPingAgentProxy
     * @param objectName agent's ObjectName
     * @param mBeanServer agent's MBeanServer
     */
    public JademxPingAgentProxy(ObjectName objectName, MBeanServer mBeanServer) {
        super( objectName, mBeanServer );
    }
    
    //
    // PROXIED METHODS
    //
    
    // attribute Response
    
    /**
     * proxy for @see JademxPingAgent#setResponse(String)
     * @param response response to set
     * @throws JademxException on problem
     */
    public void setResponse( String response ) throws JademxException {
        setAttribute( JademxPingAgent.ATTR_RESPONSE_NAME, response );
    }
    
    /**
     * proxy for @see JademxPingAgent#getResponse()
     * @return response
     * @throws JademxException on problem
     */
    public String getResponse() throws JademxException {
        return (String)getAttribute( JademxPingAgent.ATTR_RESPONSE_NAME );
    }
    
    // operation ping
    
    /**
     * proxy for @see JademxPingAgent#ping(String)
     * @return current response
     * @throws JademxException on problem
     */
    public String ping( String pingeeAgentFullName ) throws JademxException {
        return (String)invoke( 
                JademxPingAgent.OPER_PING_NAME,
                new Object[]{ pingeeAgentFullName },
                JademxPingAgent.OPER_PING_SIGNATURE );
    }
    
}
