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

/**
 * a class to show usage of construction with a string argument
 * @author David Bernstein, <a href="http://www.caboodlenetworks.com"
 *  >Caboodle Networks, Inc.</a>
 */
public class Arg {
    
    /** keep copy of constructor argument string */
    private String val;

    /**
     * construct a new Arg
     * @param val the string for construction
     */
    public Arg(String val) {
        this.val = val;
    }
    
    /**
     * return the constructor parameter
     * @return the constructor parameter
     */
    public String getVal() {
        return val;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object o ) {
        boolean eq = o instanceof Arg;
        if ( eq ) {
             String oVal = ((Arg)o).getVal();
             if ( ( null == oVal ) && ( null == val ) ) {
                 // yes, they're equal
             }
             else if ( ( null != oVal) && ( null != val ) ) {
                 eq = val.equals( oVal );
             }
             else {
                 eq = false;
             }
        }
        return eq;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass()+"[val="+val+"]";
    }
    
}
