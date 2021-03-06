package org.openkilda.wfm.topology.flow.utils;

import org.openkilda.messaging.model.Flow;
import org.openkilda.messaging.model.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class BidirectionalFlow {
    private String flowId;
    private Flow forward = null;
    private Flow reverse = null;

    public void add(Flow flow) {
        Flow current;

        boolean isForward = flow.isForward();
        if (isForward) {
            current = this.forward;
        } else {
            current = reverse;
        }

        if (current != null) {
            throw new IllegalArgumentException(
                    String.format("The flow(%s) for %s is already set",
                            flowId, isForward ? "forward" : "reverse"));
        }

        if (isForward) {
            forward = flow;
        } else {
            reverse = flow;
        }

        flowId = flow.getFlowId();
    }

    public ImmutablePair<Flow, Flow> makeFlowPair() {
        List<String> missing = new ArrayList<>(2);
        if (forward == null) {
            missing.add("forward is missing");
        }
        if (reverse == null) {
            missing.add("reverse is missing");
        }
        if (0 < missing.size()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Flow pair is incomplete: %s",
                            String.join(" and ", missing))
            );
        }

        return new ImmutablePair<>(forward, reverse);
    }
}
