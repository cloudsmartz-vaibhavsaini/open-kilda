package org.openkilda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * The Class Switchrelation.
 *
 * @author Gaurav Chugh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"src_port", "latency", "source_switch", "available_bandwidth", "dst_port",
        "target_switch", "speed", "state"})
public class IslLinkInfo implements Serializable {

    private final static long serialVersionUID = 8274573430947748236L;

    @JsonProperty("src_port")
    private int srcPort;

    @JsonProperty("latency")
    private int latency;

    @JsonProperty("source_switch")
    private String srcSwitch;

    @JsonProperty("available_bandwidth")
    private int availableBandwidth;

    @JsonProperty("dst_port")
    private int dstPort;

    @JsonProperty("target_switch")
    private String dstSwitch;

    @JsonProperty("speed")
    private int speed;

    @JsonProperty("state")
    private String state;

    /**
     * Gets the src port.
     *
     * @return the src port
     */
    
    public int getSrcPort() {
        return srcPort;
    }

    /**
     * Sets the src port.
     *
     * @param srcPort the new src port
     */
    
    public void setSrcPort(final int srcPort) {
        this.srcPort = srcPort;
    }

    /**
     * Gets the latency.
     *
     * @return the latency
     */
    
    public int getLatency() {
        return latency;
    }

    /**
     * Sets the latency.
     *
     * @param latency the new latency
     */
    
    public void setLatency(final int latency) {
        this.latency = latency;
    }

    /**
     * Gets the src switch.
     *
     * @return the src switch
     */
    public String getSrcSwitch() {
        return srcSwitch;
    }

    /**
     * Sets the src switch.
     *
     * @param srcSwitch the new src switch
     */
    
    public void setSrcSwitch(final String srcSwitch) {
        this.srcSwitch = srcSwitch;
    }

    /**
     * Gets the available bandwidth.
     *
     * @return the available bandwidth
     */

    public int getAvailableBandwidth() {
        return availableBandwidth;
    }

    /**
     * Sets the available bandwidth.
     *
     * @param availableBandwidth the new available bandwidth
     */

    public void setAvailableBandwidth(final int availableBandwidth) {
        this.availableBandwidth = availableBandwidth;
    }

    /**
     * Gets the dst port.
     *
     * @return the dst port
     */
    
    public int getDstPort() {
        return dstPort;
    }

    /**
     * Sets the dst port.
     *
     * @param dstPort the new dst port
     */
    
    public void setDstPort(final int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * Gets the dst switch.
     *
     * @return the dst switch
     */
    
    public String getDstSwitch() {
        return dstSwitch;
    }

    /**
     * Sets the dst switch.
     *
     * @param dstSwitch the new dst switch
     */
    
    public void setDstSwitch(final String dstSwitch) {
        this.dstSwitch = dstSwitch;
    }

    /**
     * Gets the speed.
     *
     * @return the speed
     */
    
    public int getSpeed() {
        return speed;
    }

    /**
     * Sets the speed.
     *
     * @param speed the new speed
     */
    
    public void setSpeed(final int speed) {
        this.speed = speed;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    
    public void setState(final String state) {
        this.state = state;
    }

}
