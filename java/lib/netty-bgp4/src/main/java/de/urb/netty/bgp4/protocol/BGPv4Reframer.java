/**
 * 
 */
package de.urb.netty.bgp4.protocol;

import javax.inject.Inject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;

import de.urb.netty.bgp4.BGPv4Constants;

/**
 * Reframing decoder to ensure that a complete BGPv4 packet is processed in the subsequent decoder.
 * 
 * @author rainer
 *
 */
public class BGPv4Reframer extends FrameDecoder {
	private @Inject Logger log;

	/**
	 * reframe the received packet to completely contain the next BGPv4 packet. It peeks into the first four bytes of the 
	 * TCP stream which contain a 16-bit marker and a 16-bit length field. 
	 * The marker must be all one's and the length value must be between 19 and 4096 according to RFC 4271. The marker and length
	 * constraints are verified and if either is violated the connection is closed early.
	 *  
	 *  @param ctx the context
	 *  @param channel the channel from which the data is consumed
	 *  @param buffer the buffer to read from
	 *  @return a complete BGPv4 protocol packet in a channel buffer or null. If a packet is returned it starts on the type byte.
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if (buffer.readableBytes() < (BGPv4Constants.BGP_PACKET_MIN_LENGTH-1))
			return null;

		buffer.markReaderIndex();

		byte[] marker = new byte[BGPv4Constants.BGP_PACKET_MARKER_LENGTH];
		buffer.readBytes(marker);
		
		for(int i=0; i<marker.length; i++) {
			if(marker[i] != (byte)0xff) {
				log.error("received invalid marker {}, closing connection", marker);
				
				channel.close();
				return null;				
			}
		}		
		int length = buffer.readUnsignedShort();

		if(length < BGPv4Constants.BGP_PACKET_MIN_LENGTH || length > BGPv4Constants.BGP_PACKET_MAX_LENGTH) {
			log.error("received illegal packet size {}, must be between {} and {}. closing connection", 
					new Object[] { length, BGPv4Constants.BGP_PACKET_MIN_LENGTH, BGPv4Constants.BGP_PACKET_MAX_LENGTH });
			
			channel.close();
			return null;
		}
		
		if (buffer.readableBytes() < (length - 4)) {
			buffer.resetReaderIndex();
			return null;
		}

		return buffer.readBytes(length);
	}

}
