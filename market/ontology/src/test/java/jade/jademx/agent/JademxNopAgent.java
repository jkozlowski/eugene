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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanException;
import javax.management.ReflectionException;

//import jade.util.Logger;


/** 
 * sample agent to demonstrate building agent using jademx.
 * @author David Bernstein, <a href="http://www.caboodlenetworks.com"
 *  >Caboodle Networks, Inc.</a>
 */
public class JademxNopAgent extends JademxAgent {

    /**
     * make a Jademx ping agent 
     */
    public JademxNopAgent() {
    }

    ///** my logger */
    //private final Logger logger = 
    //    Logger.getMyLogger(JademxNopAgent.class.getName());
    
    /* (non-Javadoc)
     * @see jade.core.Agent#setup()
     */
    protected void setup() {
        // make sure set up as a JademxAgent
        super.setup();
        
        //MBeanInfo mbi = getMBeanInfo();
        
        boolean reachedUnreachable = false;
        AttributeList aList = null;
        
        try {
            reachedUnreachable = false;
            aList = getAttributes( new String[] { "FuBar "});
            reachedUnreachable = true;
        }
        catch ( RuntimeException re ) {
            // expected
        }
        if ( reachedUnreachable ) {
            throw new RuntimeException(
            "shouldn't have gotten past getAttributes() call");
        }
        
        aList = new AttributeList(1);
        aList.add( new Attribute("foo","bar") );
        try {
            reachedUnreachable = false;
            setAttributes( aList );
            reachedUnreachable = true;
        }
        catch ( RuntimeException re ) {
            // expected
        }
        if ( reachedUnreachable ) {
            throw new RuntimeException(
            "shouldn't have gotten past setAttributes() call");
        }
        
        try {
            invoke( "foo", new Object[0], new String[0]);
        }
        catch ( MBeanException e ) {
            throw new RuntimeException("unexpected MBeanException", e );
        }
        catch (ReflectionException e) {
            // expected
        }
        
        
    }



    
}
