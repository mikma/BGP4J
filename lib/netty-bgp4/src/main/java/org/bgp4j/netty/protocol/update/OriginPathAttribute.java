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
 */
package org.bgp4j.netty.protocol.update;

import org.bgp4j.netty.BGPv4Constants;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * ORIGIN (type code 1) BGPv4 path attribute
 * 
 * @author Rainer Bieniek (Rainer.Bieniek@web.de)
 *
 */
public class OriginPathAttribute extends Attribute {

	/**
	 * Discrete origin types as specified in RFC 4271
	 * 
	 * @author rainer
	 *
	 */
	public enum Origin {
		
		/** NLRI is interior to the originating AS (RFC 4271) */
		IGP,

		/** NLRI learned via EGP protocol (RFC 4271, RFC 904) */
		EGP,
		
		/** NLRI learned by some other means (RFC 4271)*/
		INCOMPLETE;
		
		int toCode() {
			switch(this) {
				case IGP:
					return 0;
				case EGP:
					return 1;
				case INCOMPLETE:
					return 2;
				default:
					throw new IllegalArgumentException("unknown origin code: " +this);
			}
		}
		
		static Origin fromCode(int code) {
			switch(code) {
			case 0:
				return IGP;
			case 1:
				return EGP;
			case 2:
				return INCOMPLETE;
			default:
				throw new IllegalArgumentException("unknown origin code: " + code);
			}
		}
	}

	private Origin origin;
	
	public OriginPathAttribute() {
		super(Category.WELL_KNOWN_MANDATORY);
		
		origin = Origin.INCOMPLETE;
	}
	
	public OriginPathAttribute(Origin origin) {
		super(Category.WELL_KNOWN_MANDATORY);
		
		this.origin = origin;
	}
	
	/* (non-Javadoc)
	 * @see org.bgp4j.netty.protocol.PathAttribute#getTypeCode()
	 */
	@Override
	protected int getTypeCode() {
		return BGPv4Constants.BGP_PATH_ATTRIBUTE_TYPE_ORIGIN;
	}

	/* (non-Javadoc)
	 * @see org.bgp4j.netty.protocol.PathAttribute#getValueLength()
	 */
	@Override
	protected int getValueLength() {
		return 1; // fixed one byte
	}

	/* (non-Javadoc)
	 * @see org.bgp4j.netty.protocol.PathAttribute#encodeValue()
	 */
	@Override
	protected ChannelBuffer encodeValue() {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		
		buffer.writeByte(getOrigin().toCode());
		
		return buffer;
	}

	/**
	 * @return the origin
	 */
	public Origin getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

}
