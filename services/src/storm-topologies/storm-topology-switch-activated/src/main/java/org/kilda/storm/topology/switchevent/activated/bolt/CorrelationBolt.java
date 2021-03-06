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

package org.bitbucket.kilda.storm.topology.switchevent.activated.bolt;

import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.bitbucket.kilda.storm.topology.switchevent.activated.model.ActivatedSwitchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Correlate existence of the switch with the topology engine.
 * 
 * @author d772392
 *
 */
public class CorrelationBolt extends BaseBasicBolt {

	private static final Logger logger = LoggerFactory.getLogger(CorrelationBolt.class);
		
	private static final String FIELD_SWITCH_ID = ActivatedSwitchEvent.OUTPUT_FIELD_SWITCH_ID;
	
	private final ICorrelator correlator;
		
	public CorrelationBolt(ICorrelator correlator) {
		this.correlator = correlator;
	}
	
	@Override
    public void prepare(Map stormConf, TopologyContext context) {
		correlator.prepare();
    }

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		if (input.contains(FIELD_SWITCH_ID)) {
			String switchId = (String)input.getValueByField(FIELD_SWITCH_ID);
		    logger.debug("switchId=" + input.getValueByField(FIELD_SWITCH_ID));
		    
		    if (isSwitchCorrelated(switchId)) {
		    	collector.emit(new Values(switchId));
		    } else {
		    	logger.warn("could not correlate switch with id " + switchId);
		    	// TODO - any action here?
		    }
	    } else {
	    	logger.error(FIELD_SWITCH_ID + " not found in tuple " + input);
	    }
	}
	
	private boolean isSwitchCorrelated(String switchId) {
		return correlator.correlate(switchId);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(FIELD_SWITCH_ID));	
	}

}
