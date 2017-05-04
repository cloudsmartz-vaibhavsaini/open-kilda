package org.bitbucket.openkilda.messaging.command.flow;

import static com.google.common.base.MoreObjects.toStringHelper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 * Class represents ingress flow installation info.
 * Transit vlan id is used in output action.
 * Output action is always push transit vlan tag.
 * Input vlan id is optional, because flow could be untagged on ingoing side.
 * Bandwidth and meter id are used for flow throughput limitation.
 */
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "command",
        "destination",
        "flow_name",
        "switch_id",
        "input_port",
        "output_port",
        "input_vlan_id",
        "transit_vlan_id",
        "output_vlan_type",
        "bandwidth",
        "meter_id"})
public class InstallIngressFlow extends InstallTransitFlow {
    /**
     * Serialization version number constant.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Flow bandwidth value. It is a mandatory parameter.
     */
    protected Number bandwidth;

    /**
     * Allocated meter id. It is a mandatory parameter.
     */
    protected Number meterId;

    /**
     * Input vlan id value. It is an optional parameter.
     */
    protected Number inputVlanId;

    /**
     * Output action on the vlan tag. It is a mandatory parameter.
     */
    protected OutputVlanType outputVlanType;

    /**
     * Default constructor.
     */
    public InstallIngressFlow() {
    }

    /**
     * Constructs an ingress flow installation command.
     *
     * @param flowName       Name of the flow
     * @param switchId       Switch ID for flow installation
     * @param inputPort      Input port of the flow
     * @param outputPort     Output port of the flow
     * @param inputVlanId    Input vlan id value
     * @param transitVlanId  Transit vlan id value
     * @param outputVlanType Output vlan type action
     * @param bandwidth      Flow bandwidth
     * @param meterId        Allocated meter id
     * @throws IllegalArgumentException if any of mandatory parameters is null
     */
    @JsonCreator
    public InstallIngressFlow(@JsonProperty("flow_name") String flowName,
                              @JsonProperty("switch_id") String switchId,
                              @JsonProperty("input_port") Number inputPort,
                              @JsonProperty("output_port") Number outputPort,
                              @JsonProperty("input_vlan_id") Number inputVlanId,
                              @JsonProperty("transit_vlan_id") Number transitVlanId,
                              @JsonProperty("output_vlan_type") OutputVlanType outputVlanType,
                              @JsonProperty("bandwidth") Number bandwidth,
                              @JsonProperty("meter_id") Number meterId) {
        super(flowName, switchId, inputPort, outputPort, transitVlanId);
        setInputVlanId(inputVlanId);
        setOutputVlanType(outputVlanType);
        setBandwidth(bandwidth);
        setMeterId(meterId);
    }

    /**
     * Returns output action on the vlan tag.
     *
     * @return Output action on the vlan tag
     */
    @JsonProperty("output_vlan_type")
    public OutputVlanType getOutputVlanType() {
        return outputVlanType;
    }

    /**
     * Returns flow bandwidth value.
     *
     * @return Flow bandwidth value
     */
    @JsonProperty("bandwidth")
    public Number getBandwidth() {
        return bandwidth;
    }

    /**
     * Returns meter id for the flow.
     *
     * @return Meter id for the flow
     */
    @JsonProperty("meter_id")
    public Number getMeterId() {
        return meterId;
    }

    /**
     * Returns input vlan id value.
     *
     * @return Input vlan id value
     */
    @JsonProperty("input_vlan_id")
    public Number getInputVlanId() {
        return inputVlanId;
    }

    /**
     * Sets output action on the vlan tag.
     *
     * @param outputVlanType action on the vlan tag
     */
    @JsonProperty("output_vlan_type")
    public void setOutputVlanType(OutputVlanType outputVlanType) {
        if (outputVlanType == null) {
            throw new IllegalArgumentException("need to set output_vlan_type");
        } else if (!Utils.validateInputVlanType(inputVlanId, outputVlanType)) {
            throw new IllegalArgumentException("need to set valid values for input_vlan_id and output_vlan_type");
        } else {
            this.outputVlanType = outputVlanType;
        }
    }

    /**
     * Sets flow bandwidth value.
     *
     * @param bandwidth bandwidth value
     */
    @JsonProperty("bandwidth")
    public void setBandwidth(Number bandwidth) {
        if (bandwidth == null) {
            throw new IllegalArgumentException("need to set bandwidth");
        } else if (bandwidth.intValue() < 0) {
            throw new IllegalArgumentException("need to set non negative bandwidth");
        }
        this.bandwidth = bandwidth;
    }

    /**
     * Sets meter id for the flow.
     *
     * @param meterId id for the flow
     */
    @JsonProperty("meter_id")
    public void setMeterId(Number meterId) {
        if (meterId == null) {
            throw new IllegalArgumentException("need to set meter_id");
        } else if (meterId.intValue() < 0) {
            throw new IllegalArgumentException("need to set non negative meter_id");
        }
        this.meterId = meterId;
    }

    /**
     * Sets input vlan id value.
     *
     * @param inputVlanId input vlan id value
     */
    @JsonProperty("input_vlan_id")
    public void setInputVlanId(Number inputVlanId) {
        if (inputVlanId == null) {
            this.inputVlanId = 0;
        } else if (Utils.validateVlanRange(inputVlanId.intValue())) {
            this.inputVlanId = inputVlanId;
        } else {
            throw new IllegalArgumentException("need to set valid value for input_vlan_id");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .addValue(flowName)
                .addValue(switchId)
                .addValue(inputPort)
                .addValue(outputPort)
                .addValue(inputVlanId)
                .addValue(transitVlanId)
                .addValue(outputVlanType)
                .addValue(bandwidth)
                .addValue(meterId)
                .toString();
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

        InstallIngressFlow flow = (InstallIngressFlow) object;
        return Objects.equals(flowName, flow.flowName)
                && Objects.equals(switchId, flow.switchId)
                && Objects.equals(inputPort, flow.inputPort)
                && Objects.equals(outputPort, flow.outputPort)
                && Objects.equals(inputVlanId, flow.inputVlanId)
                && Objects.equals(transitVlanId, flow.transitVlanId)
                && Objects.equals(outputVlanType, flow.outputVlanType)
                && Objects.equals(bandwidth, flow.bandwidth)
                && Objects.equals(meterId, flow.meterId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(flowName, switchId, inputPort, outputPort,
                inputVlanId, transitVlanId, outputVlanType, bandwidth, meterId);
    }
}
