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
package org.bgp4j.netty.protocol;


import javax.inject.Inject;

import org.bgp4j.netty.BGPv4Constants;
import org.bgp4j.netty.protocol.open.OpenPacketDecoder;
import org.bgp4j.netty.protocol.update.UpdatePacketDecoder;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author Rainer Bieniek (Rainer.Bieniek@web.de)
 *
 */
public class BGPv4PacketDecoder {
	private @Inject OpenPacketDecoder openPacketDecoder;
	private @Inject UpdatePacketDecoder updatePacketDecoder;
	
	BGPv4Packet decodePacket(ChannelBuffer buffer) {
		int type = buffer.readUnsignedByte();
		BGPv4Packet packet = null;
		
		switch (type) {
		case BGPv4Constants.BGP_PACKET_TYPE_OPEN:
			packet = openPacketDecoder.decodeOpenPacket(buffer);
			break;
		case BGPv4Constants.BGP_PACKET_TYPE_UPDATE:
			packet = updatePacketDecoder.decodeUpdatePacket(buffer);
			break;
		case BGPv4Constants.BGP_PACKET_TYPE_NOTIFICATION:
			packet = decodeNotificationPacket(buffer);
			break;
		case BGPv4Constants.BGP_PACKET_TYPE_KEEPALIVE:
			packet = decodeKeepalivePacket(buffer);
			break;
		case BGPv4Constants.BGP_PACKET_TYPE_ROUTE_REFRESH:
			break;
		default:
			throw new BadMessageTypeException(type);
		}
		
		return packet;
	}
	
	/**
	 * decode the NOTIFICATION network packet. The NOTIFICATION packet must be at least 2 octets large at this point.
	 * 
	 * @param buffer the buffer containing the data. 
	 * @return
	 */
	private BGPv4Packet decodeNotificationPacket(ChannelBuffer buffer) {
		NotificationPacket packet = null;
		
		ProtocolPacketUtils.verifyPacketSize(buffer, BGPv4Constants.BGP_PACKET_MIN_SIZE_NOTIFICATION, -1);
		
		int errorCode = buffer.readUnsignedByte();
		int errorSubcode = buffer.readUnsignedByte();
		
		switch(errorCode) {
		case BGPv4Constants.BGP_ERROR_CODE_MESSAGE_HEADER:
			packet = decodeMessageHeaderNotificationPacket(buffer, errorSubcode);
			break;
		case BGPv4Constants.BGP_ERROR_CODE_OPEN:
			packet = openPacketDecoder.decodeOpenNotificationPacket(buffer, errorSubcode);
			break;
		case BGPv4Constants.BGP_ERROR_CODE_UPDATE:
			packet = updatePacketDecoder.decodeUpdateNotification(buffer, errorSubcode);
			break;
		case BGPv4Constants.BGP_ERROR_CODE_HOLD_TIMER_EXPIRED:
			packet = new HoldTimerExpiredNotificationPacket();
			break;
		case BGPv4Constants.BGP_ERROR_CODE_FINITE_STATE_MACHINE_ERROR:
			packet = new FiniteStateMachineErrorNotificationPacket();
			break;
		case BGPv4Constants.BGP_ERROR_CODE_CEASE:
			packet = new CeaseNotificationPacket();
			break;
		}
		
		return packet;
	}

	/**
	 * decode the NOTIFICATION network packet for error code "Message Header Error". 
	 * 
	 * @param buffer the buffer containing the data. 
	 * @return
	 */
	private NotificationPacket decodeMessageHeaderNotificationPacket(ChannelBuffer buffer, int errorSubcode) {
		NotificationPacket packet = null;
		
		switch(errorSubcode) {
		case MessageHeaderErrorNotificationPacket.SUBCODE_CONNECTION_NOT_SYNCHRONIZED:
			packet = new ConnectionNotSynchronizedNotificationPacket();
			break;
		case MessageHeaderErrorNotificationPacket.SUBCODE_BAD_MESSAGE_LENGTH:
			packet = new BadMessageLengthNotificationPacket(buffer.readUnsignedShort());
			break;
		case MessageHeaderErrorNotificationPacket.SUBCODE_BAD_MESSAGE_TYPE:
			packet = new BadMessageTypeNotificationPacket(buffer.readUnsignedByte());
			break;
		}
		
		return packet;
	}

	/**
	 * decode the KEEPALIVE network packet. The OPEN packet must be exactly 0 octets large at this point.
	 * 
	 * @param buffer the buffer containing the data. 
	 * @return
	 */
	private KeepalivePacket decodeKeepalivePacket(ChannelBuffer buffer) {
		KeepalivePacket packet = new KeepalivePacket();
		
		ProtocolPacketUtils.verifyPacketSize(buffer, BGPv4Constants.BGP_PACKET_SIZE_KEEPALIVE, BGPv4Constants.BGP_PACKET_SIZE_KEEPALIVE);
		
		return packet;
	}	
}