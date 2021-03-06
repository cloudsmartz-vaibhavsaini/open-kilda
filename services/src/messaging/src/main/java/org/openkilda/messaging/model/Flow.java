/* Copyright 2017 Telstra Open Source
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.openkilda.messaging.model;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.openkilda.messaging.Utils;
import org.openkilda.messaging.info.event.PathInfoData;
import org.openkilda.messaging.payload.flow.FlowState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents flow entity.
 */
@JsonSerialize
public class Flow implements Serializable {
    /**
     * Serialization version number constant.
     */
    private static final long serialVersionUID = 1L;

    private static long MASK_COOKIE_FLAGS = 0x0000_0000_FFFF_FFFFL;

    /**
     * Flow id.
     */
    @JsonProperty(Utils.FLOW_ID)
    private String flowId;

    /**
     * FLow bandwidth.
     */
    @JsonProperty("bandwidth")
    private int bandwidth;

    /**
     * Should flow ignore bandwidth in path computation?
     */
    @JsonProperty("ignore_bandwidth")
    private boolean ignoreBandwidth;

    /**
     * Flow cookie.
     */
    @JsonProperty("cookie")
    private long cookie;

    /**
     * Flow description.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Flow last updated timestamp.
     */
    @JsonProperty("last_updated")
    private String lastUpdated;

    /**
     * Flow source switch.
     */
    @JsonProperty("src_switch")
    private String sourceSwitch;

    /**
     * Flow destination switch.
     */
    @JsonProperty("dst_switch")
    private String destinationSwitch;

    /**
     * Flow source port.
     */
    @JsonProperty("src_port")
    private int sourcePort;

    /**
     * Flow destination port.
     */
    @JsonProperty("dst_port")
    private int destinationPort;

    /**
     * Flow source vlan id.
     */
    @JsonProperty("src_vlan")
    private int sourceVlan;

    /**
     * Flow destination vlan id.
     */
    @JsonProperty("dst_vlan")
    private int destinationVlan;

    /**
     * Flow source meter id.
     */
    @JsonProperty("meter_id")
    private int meterId;

    /**
     * Flow transit vlan id.
     */
    @JsonProperty("transit_vlan")
    private int transitVlan;

    /**
     * Flow switch path.
     */
    @JsonProperty(Utils.FLOW_PATH)
    private PathInfoData flowPath;
    // TODO: why is PathInfoData an event, vs a model (ie package name)?

    /**
     * Flow state.
     */
    @JsonProperty("state")
    private FlowState state;

    /**
     * Default constructor.
     */
    public Flow() {
    }

    /**
     * Copy constructor.
     *
     * @param flow flow
     */
    public Flow(Flow flow) {
        this.flowId = flow.getFlowId();
        this.bandwidth = flow.getBandwidth();
        this.ignoreBandwidth = flow.isIgnoreBandwidth();
        this.cookie = flow.getCookie();
        this.description = flow.getDescription();
        this.lastUpdated = flow.getLastUpdated();
        this.sourceSwitch = flow.getSourceSwitch();
        this.destinationSwitch = flow.getDestinationSwitch();
        this.sourcePort = flow.getSourcePort();
        this.destinationPort = flow.getDestinationPort();
        this.sourceVlan = flow.getSourceVlan();
        this.destinationVlan = flow.getDestinationVlan();
        this.transitVlan = flow.getTransitVlan();
        this.meterId = flow.getMeterId();
        this.flowPath = flow.getFlowPath();
        this.state = flow.getState();
    }

    /**
     * Instance constructor.
     *
     * @param flowId            flow id
     * @param bandwidth         bandwidth
     * @param ignoreBandwidth   ignore bandwidth flag
     * @param cookie            cookie
     * @param description       description
     * @param lastUpdated       last updated timestamp
     * @param sourceSwitch      source switch
     * @param destinationSwitch destination switch
     * @param sourcePort        source port
     * @param destinationPort   destination port
     * @param sourceVlan        source vlan id
     * @param destinationVlan   destination vlan id
     * @param meterId           meter id
     * @param transitVlan       transit vlan id
     * @param flowPath          flow switch path
     * @param state             flow state
     */
    @JsonCreator
    public Flow(@JsonProperty(Utils.FLOW_ID) final String flowId,
                @JsonProperty("bandwidth") final int bandwidth,
                @JsonProperty("ignore_bandwidth") Boolean ignoreBandwidth,
                @JsonProperty("cookie") final long cookie,
                @JsonProperty("description") final String description,
                @JsonProperty("last_updated") final String lastUpdated,
                @JsonProperty("src_switch") final String sourceSwitch,
                @JsonProperty("dst_switch") final String destinationSwitch,
                @JsonProperty("src_port") final int sourcePort,
                @JsonProperty("dst_port") final int destinationPort,
                @JsonProperty("src_vlan") final int sourceVlan,
                @JsonProperty("dst_vlan") final int destinationVlan,
                @JsonProperty("meter_id") final int meterId,
                @JsonProperty("transit_vlan") final int transitVlan,
                @JsonProperty(Utils.FLOW_PATH) final PathInfoData flowPath,
                @JsonProperty("state") FlowState state) {
        this.flowId = flowId;
        this.bandwidth = bandwidth;
        setIgnoreBandwidth(ignoreBandwidth);
        this.cookie = cookie;
        this.description = description;
        this.lastUpdated = lastUpdated;
        this.sourceSwitch = sourceSwitch;
        this.destinationSwitch = destinationSwitch;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.sourceVlan = sourceVlan;
        this.destinationVlan = destinationVlan;
        this.transitVlan = transitVlan;
        this.meterId = meterId;
        this.flowPath = flowPath;
        this.state = state;
    }

    /**
     * Instance constructor.
     *
     * @param flowId            flow id
     * @param bandwidth         bandwidth
     * @param ignoreBandwidth   ignore bandwidth flag
     * @param description       description
     * @param sourceSwitch      source switch
     * @param sourcePort        source port
     * @param sourceVlan        source vlan id
     * @param destinationSwitch destination switch
     * @param destinationPort   destination port
     * @param destinationVlan   destination vlan id
     */
    public Flow(String flowId, int bandwidth, boolean ignoreBandwidth, String description,
            String sourceSwitch, int sourcePort, int sourceVlan,
            String destinationSwitch, int destinationPort, int destinationVlan) {
        this.flowId = flowId;
        this.bandwidth = bandwidth;
        this.ignoreBandwidth = ignoreBandwidth;
        this.description = description;
        this.sourceSwitch = sourceSwitch;
        this.destinationSwitch = destinationSwitch;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.sourceVlan = sourceVlan;
        this.destinationVlan = destinationVlan;
    }

    /**
     * Gets flow id.
     *
     * @return flow id
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * Sets flow id.
     *
     * @param flowId flow id
     */
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    /**
     * Gets flow state.
     *
     * @return flow state
     */
    public FlowState getState() {
        return state;
    }

    /**
     * Sets flow state.
     *
     * @param state flow state
     */
    public void setState(FlowState state) {
        this.state = state;
    }

    /**
     * Gets flow bandwidth.
     *
     * @return flow bandwidth
     */
    public int getBandwidth() {
        return bandwidth;
    }

    /**
     *
     * @return ignore bandwidth flag
     */
    public boolean isIgnoreBandwidth() {
        return ignoreBandwidth;
    }

    /**
     *
     * @param ignoreBandwidth ignore bandwidth flag
     */
    public void setIgnoreBandwidth(Boolean ignoreBandwidth) {
        if (ignoreBandwidth == null) {
            ignoreBandwidth = false;
        }
        this.ignoreBandwidth = ignoreBandwidth;
    }

    /**
     * Sets flow bandwidth.
     *
     * @param bandwidth flow bandwidth
     */
    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * Gets flow cookie.
     *
     * @return flow cookie
     */
    public long getCookie() {
        return cookie;
    }

    /**
     * Sets flow cookie.
     *
     * @param cookie flow cookie
     */
    public void setCookie(long cookie) {
        this.cookie = cookie;
    }

    /**
     * Gets flow description.
     *
     * @return flow description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets flow description.
     *
     * @param description flow description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets flow last updated timestamp.
     *
     * @return flow last updated timestamp
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets flow last updated timestamp.
     *
     * @param lastUpdated flow last updated timestamp
     */
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Gets flow source switch.
     *
     * @return flow source switch
     */
    public String getSourceSwitch() {
        return sourceSwitch;
    }

    /**
     * Sets flow source switch.
     *
     * @param sourceSwitch flow source switch
     */
    public void setSourceSwitch(String sourceSwitch) {
        this.sourceSwitch = sourceSwitch;
    }

    /**
     * Gets flow destination switch.
     *
     * @return flow destination switch
     */
    public String getDestinationSwitch() {
        return destinationSwitch;
    }

    /**
     * Sets flow destination switch.
     *
     * @param destinationSwitch flow destination switch
     */
    public void setDestinationSwitch(String destinationSwitch) {
        this.destinationSwitch = destinationSwitch;
    }

    /**
     * Gets flow source port.
     *
     * @return flow source port
     */
    public int getSourcePort() {
        return sourcePort;
    }

    /**
     * Sets flow source port.
     *
     * @param sourcePort flow source port
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * Gets flow destination port.
     *
     * @return flow destination port
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /**
     * Sets flow destination port.
     *
     * @param destinationPort flow destination port
     */
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * Gets flow source vlan id.
     *
     * @return flow source vlan id
     */
    public int getSourceVlan() {
        return sourceVlan;
    }

    /**
     * Sets flow source vlan id.
     *
     * @param sourceVlan flow source vlan id
     */
    public void setSourceVlan(int sourceVlan) {
        this.sourceVlan = sourceVlan;
    }

    /**
     * Gets flow destination vlan id.
     *
     * @return flow destination vlan id
     */
    public int getDestinationVlan() {
        return destinationVlan;
    }

    /**
     * Sets flow destination vlan id.
     *
     * @param destinationVlan flow destination vlan id
     */
    public void setDestinationVlan(int destinationVlan) {
        this.destinationVlan = destinationVlan;
    }

    /**
     * Gets flow transit vlan id.
     *
     * @return flow transit vlan id
     */
    public int getTransitVlan() {
        return transitVlan;
    }

    /**
     * Sets flow transit vlan id.
     *
     * @param transitVlan flow transit vlan id
     */
    public void setTransitVlan(int transitVlan) {
        this.transitVlan = transitVlan;
    }

    /**
     * Gets flow meter id.
     *
     * @return flow meter id
     */
    public int getMeterId() {
        return meterId;
    }

    /**
     * Sets flow meter id.
     *
     * @param meterId flow meter id
     */
    public void setMeterId(int meterId) {
        this.meterId = meterId;
    }

    /**
     * Gets flow switch path.
     *
     * @return flow switch path
     */
    public PathInfoData getFlowPath() {
        return flowPath;
    }

    /**
     * Sets flow switch path.
     *
     * @param flowPath flow switch path
     */
    public void setFlowPath(PathInfoData flowPath) {
        this.flowPath = flowPath;
    }

    /**
     *
     * @return is this is single switch flow
     */
    @JsonIgnore
    public boolean isOneSwitchFlow() {
        // TODO(surabujin): there is no decision how it should react on null values in source/dest switches
        if (sourceSwitch == null || destinationSwitch == null) {
            return sourceSwitch == null && destinationSwitch == null;
        }
        return sourceSwitch.equals(destinationSwitch);
    }

    @JsonIgnore
    public long getFlagglessCookie() {
        return cookie & MASK_COOKIE_FLAGS;
    }

    @JsonIgnore
    public boolean isForward() {
        boolean isForward = cookieMarkedAsFroward();
        boolean isReversed = cookieMarkedAsReversed();

        if (isForward && isReversed) {
            throw new IllegalArgumentException(
                    "Invalid cookie flags combinations - it mark as forward and reverse flow at same time.");
        }

        return isForward;
    }

    @JsonIgnore
    public boolean isReverse() {
        return !isForward();
    }

    private boolean cookieMarkedAsFroward() {
        boolean isMatch;

        if ((cookie & 0xE000000000000000L) != 0) {
            isMatch = (cookie & 0x4000000000000000L) != 0;
        } else {
            isMatch = (cookie & 0x0080000000000000L) == 0;
        }
        return isMatch;

    }

    private boolean cookieMarkedAsReversed() {
        boolean isMatch;
        if ((cookie & 0xE000000000000000L) != 0) {
            isMatch = (cookie & 0x2000000000000000L) != 0;
        } else {
            isMatch = (cookie & 0x0080000000000000L) != 0;
        }
        return isMatch;
    }

    /**
     * Checks if flow path contains specified switch.
     *
     * @param switchId switch id
     * @return true if flow path contains specified switch
     */
    public boolean containsSwitchInPath(String switchId) {
        return flowPath.getPath().stream()
                .anyMatch(node -> node.getSwitchId().equals(switchId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Flow flow = (Flow) object;
        return Objects.equals(getFlowId(), flow.getFlowId())
                && getBandwidth() == flow.getBandwidth()
                && Objects.equals(getDescription(), flow.getDescription())
                && getState() == flow.getState()
                && Objects.equals(getSourceSwitch(), flow.getSourceSwitch())
                && getSourcePort() == flow.getSourcePort()
                && getSourceVlan() == flow.getSourceVlan()
                && Objects.equals(getDestinationSwitch(), flow.getDestinationSwitch())
                && getDestinationPort() == flow.getDestinationPort()
                && getDestinationVlan() == flow.getDestinationVlan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(flowId, bandwidth, description, state,
                sourceSwitch, sourcePort, sourceVlan, destinationSwitch, destinationPort, destinationVlan);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add(Utils.FLOW_ID, flowId)
                .add("bandwidth", bandwidth)
                .add("ignore_bandwidth", ignoreBandwidth)
                .add("description", description)
                .add("state", state)
                .add("src_switch", sourceSwitch)
                .add("src_port", sourcePort)
                .add("src_vlan", sourceVlan)
                .add("dst_switch", destinationSwitch)
                .add("dst_port", destinationPort)
                .add("dst_vlan", destinationVlan)
                .add("cookie", cookie)
                .add("transit_vlan", transitVlan)
                .add("meter_id", meterId)
                .add("last_updated", lastUpdated)
                .add(Utils.FLOW_PATH, flowPath)
                .toString();
    }
}
