package net.floodlightcontroller.flowstatistics;


import net.floodlightcontroller.packet.*;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

/**
 * 报文摘要格式
 * ——————————————————————————————
 * | IPV6Header                 |
 * _____________________________
 * | tcpheader                  |
 * _____________________________
 * |timestamp                  |
 * _____________________________
 * |input | padding           |
 * ____________________________
 *
 * Created by zhensheng on 2016/5/26.
 */
public class PktSummary extends BasePacket {
    //public static final  long serialVersionID = 1L;
    private static final int HEADER_LENGTH = 65 ;

    /**
     * ipv6 header
     */
    protected byte version;
    protected byte trafficClass;
    protected int flowLabel;
    protected short payloadLength;
    protected IpProtocol nextHeader;
    protected byte hopLimit;
    protected IPv6Address sourceAddress;
    protected IPv6Address destinationAddress;

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getTrafficClass() {
        return trafficClass;
    }

    public void setTrafficClass(byte trafficClass) {
        this.trafficClass = trafficClass;
    }

    public int getFlowLabel() {
        return flowLabel;
    }

    public void setFlowLabel(int flowLabel) {
        this.flowLabel = flowLabel;
    }

    public short getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(short payloadLength) {
        this.payloadLength = payloadLength;
    }

    public IpProtocol getNextHeader() {
        return nextHeader;
    }

    public void setNextHeader(IpProtocol nextHeader) {
        this.nextHeader = nextHeader;
    }

    public byte getHopLimit() {
        return hopLimit;
    }

    public void setHopLimit(byte hopLimit) {
        this.hopLimit = hopLimit;
    }

    public IPv6Address getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(IPv6Address sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public IPv6Address getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(IPv6Address destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public TransportPort getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(TransportPort sourcePort) {
        this.sourcePort = sourcePort;
    }

    public TransportPort getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(TransportPort destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getAcknowledge() {
        return acknowledge;
    }

    public void setAcknowledge(int acknowledge) {
        this.acknowledge = acknowledge;
    }

    public byte getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(byte dataOffset) {
        this.dataOffset = dataOffset;
    }

    public short getFlags() {
        return flags;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public short getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(short windowSize) {
        this.windowSize = windowSize;
    }

    public short getChecksum() {
        return checksum;
    }

    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }

    public short getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(short urgentPointer) {
        this.urgentPointer = urgentPointer;
    }



    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    /**
     * tcp header
     */
    protected TransportPort sourcePort;
    protected TransportPort destinationPort;
    protected int sequence;
    protected int acknowledge;
    protected byte dataOffset;
    protected short flags;
    protected short windowSize;
    protected short checksum;
    protected short urgentPointer;

    /**
     * timestamp & input interface
     */
    protected int timeStamp ;
    protected int input ;
    //protected byte padding1;
    //protected short padding2;

    @Override
    public byte[] serialize() {
        byte[] payloadData = null;
        if (this.payload != null) {
            this.payload.setParent(this);
            payloadData = this.payload.serialize();


        }
        // Update our internal payload length.
        this.payloadLength = (short) ((payloadData != null) ? payloadData.length : 0);
        // Create a byte buffer to hold the IPv6 packet structure.

        byte[] data = new byte[HEADER_LENGTH+ this.payloadLength];
        ByteBuffer bb = ByteBuffer.wrap(data);
        // Add header fields to the byte buffer in the correct order.
        // Fear not the bit magic that must occur.
        bb.put((byte) (((this.version & 0xF) << 4) |
                ((this.trafficClass & 0xF0) >>> 4)));
        bb.put((byte) (((this.trafficClass & 0xF) << 4) |
                ((this.flowLabel & 0xF0000) >>> 16)));
        bb.putShort((short) (this.flowLabel & 0xFFFF));
        bb.putShort(this.payloadLength);
        bb.put((byte) this.nextHeader.getIpProtocolNumber());
        bb.put(this.hopLimit);
        bb.put(this.sourceAddress.getBytes());
        bb.put(this.destinationAddress.getBytes());
        // Add the payload to the byte buffer, if necessary.
        /**
         *
         */
        bb.putShort((short)this.sourcePort.getPort()); //TCP ports are defined to be 16 bits
        bb.putShort((short)this.destinationPort.getPort());
        bb.putInt(this.sequence);
        bb.putInt(this.acknowledge);
        bb.putShort((short) (this.flags | (dataOffset << 12)));
        bb.putShort(this.windowSize);
        bb.putShort(this.checksum);
        bb.putShort(this.urgentPointer);

        if (payloadData != null)
            bb.put(payloadData);
        // We're done! Return the data.
        return data;

    }

    @Override
    public IPacket deserialize(byte[] data, int offset, int length) throws PacketParsingException {
        ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
        // Retrieve values from IPv6 header.
        byte firstByte = bb.get();
        byte secondByte = bb.get();
        this.version = (byte) ((firstByte & 0xF0) >>> 4);
        /*
        if (this.version != 6) {
            throw new PacketParsingException(
                    "Invalid version for IPv6 packet: " +
                            this.version);
        }*/

        this.trafficClass = (byte) (((firstByte & 0xF) << 4) |
                ((secondByte & 0xF0) >>> 4));
        this.flowLabel = ((secondByte & 0xF) << 16) |
                (bb.getShort() & 0xFFFF);
        this.payloadLength = bb.getShort();
        this.nextHeader = IpProtocol.of(bb.get());
        this.hopLimit = bb.get();
        byte[] sourceAddress = new byte[16];
        bb.get(sourceAddress, 0, 16);
        byte[] destinationAddress = new byte[16];
        bb.get(destinationAddress, 0, 16);
        this.sourceAddress = IPv6Address.of(sourceAddress);
        this.destinationAddress = IPv6Address.of(destinationAddress);
        /**
         * TCP header
         */
        this.sourcePort = TransportPort.of((int) (bb.getShort() & 0xffff)); // short will be signed, pos or neg
        this.destinationPort = TransportPort.of((int) (bb.getShort() & 0xffff)); // convert range 0 to 65534, not -32768 to 32767
        this.sequence = bb.getInt();
        this.acknowledge = bb.getInt();
        this.flags = bb.getShort();
        this.dataOffset = (byte) ((this.flags >> 12) & 0xf);
        /* dont care dataOffset
        if (this.dataOffset < 5) {
            throw new PacketParsingException("Invalid tcp header length < 20");
        }*/
        this.flags = (short) (this.flags & 0x1ff);
        this.windowSize = bb.getShort();
        this.checksum = bb.getShort();
        this.urgentPointer = bb.getShort();
        this.timeStamp = bb.getInt();
        /**
         * todo:
         */
        int i = bb.get();
        this.input = (i < 0)? i+256 :i  ;
        IPacket payload = new Data() ;

        // Deserialize as much of the payload as we can (hopefully all of it).
        this.payload = payload.deserialize(data, bb.position(), bb.limit() - bb.position());

        return this;

    }
}
