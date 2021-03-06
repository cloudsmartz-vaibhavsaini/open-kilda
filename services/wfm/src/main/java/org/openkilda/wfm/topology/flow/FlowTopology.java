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

package org.openkilda.wfm.topology.flow;

import org.apache.storm.Config;
import org.openkilda.messaging.ServiceType;
import org.openkilda.messaging.Utils;
import org.openkilda.pce.provider.Auth;
import org.openkilda.pce.provider.AuthNeo4j;
import org.openkilda.wfm.ConfigurationException;
import org.openkilda.wfm.CtrlBoltRef;
import org.openkilda.wfm.LaunchEnvironment;
import org.openkilda.wfm.StreamNameCollisionException;
import org.openkilda.wfm.topology.AbstractTopology;
import org.openkilda.wfm.topology.flow.bolts.CrudBolt;
import org.openkilda.wfm.topology.flow.bolts.ErrorBolt;
import org.openkilda.wfm.topology.flow.bolts.LcmFlowCacheSyncBolt;
import org.openkilda.wfm.topology.flow.bolts.NorthboundReplyBolt;
import org.openkilda.wfm.topology.flow.bolts.SpeakerBolt;
import org.openkilda.wfm.topology.flow.bolts.SplitterBolt;
import org.openkilda.wfm.topology.flow.bolts.TopologyEngineBolt;
import org.openkilda.wfm.topology.flow.bolts.TransactionBolt;
import org.openkilda.wfm.topology.utils.LcmKafkaSpout;

import org.apache.storm.generated.ComponentObject;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow topology.
 */
public class FlowTopology extends AbstractTopology {
    public static final String SWITCH_ID_FIELD = "switch-id";
    public static final String STATUS_FIELD = "status";
    public static final String ERROR_TYPE_FIELD = "error-type";
    public static final Fields fieldFlowId = new Fields(Utils.FLOW_ID);
    public static final Fields fieldSwitchId = new Fields(SWITCH_ID_FIELD);
    public static final Fields fieldsFlowIdStatus = new Fields(Utils.FLOW_ID, STATUS_FIELD);
    public static final Fields fieldsMessageFlowId = new Fields(MESSAGE_FIELD, Utils.FLOW_ID);
    public static final Fields fieldsMessageErrorType = new Fields(MESSAGE_FIELD, ERROR_TYPE_FIELD);
    public static final Fields fieldsMessageSwitchIdFlowIdTransactionId =
            new Fields(MESSAGE_FIELD, SWITCH_ID_FIELD, Utils.FLOW_ID, Utils.TRANSACTION_ID);

    private static final Logger logger = LoggerFactory.getLogger(FlowTopology.class);

    private final Auth pathComputerAuth;

    public FlowTopology(LaunchEnvironment env) throws ConfigurationException {
        super(env);
        pathComputerAuth = new AuthNeo4j(config.getNeo4jHost(), config.getNeo4jLogin(), config.getNeo4jPassword());

        logger.debug("Topology built {}: zookeeper={}, kafka={}, parallelism={}, workers={}" +
                ", neo4j_url{}, neo4j_user{}, neo4j_pswd{}",
                getTopologyName(), config.getZookeeperHosts(), config.getKafkaHosts(), config.getParallelism(),
                config.getWorkers(), config.getNeo4jHost(), config.getNeo4jLogin(), config.getNeo4jPassword());
    }

    public FlowTopology(LaunchEnvironment env, Auth pathComputerAuth) throws ConfigurationException {
        super(env);
        this.pathComputerAuth = pathComputerAuth;

        logger.debug("Topology built {}: zookeeper={}, kafka={}, parallelism={}, workers={}",
                getTopologyName(), config.getZookeeperHosts(), config.getKafkaHosts(), config.getParallelism(),
                config.getWorkers());
    }

    @Override
    public StormTopology createTopology() throws StreamNameCollisionException {
        logger.info("Creating Topology: {}", topologyName);

        TopologyBuilder builder = new TopologyBuilder();
        List<CtrlBoltRef> ctrlTargets = new ArrayList<>();
        BoltDeclarer boltSetup;
        Integer parallelism = config.getParallelism();

        KafkaSpoutConfig<String, String> kafkaSpoutConfig;
        KafkaSpout<String, String> kafkaSpout;

//        builder.setSpout(
//                ComponentType.LCM_SPOUT.toString(),
//                createKafkaSpout(config.getKafkaFlowTopic(), ComponentType.LCM_SPOUT.toString()), 1);
//        builder.setBolt(
//                ComponentType.LCM_FLOW_SYNC_BOLT.toString(),
//                new LcmFlowCacheSyncBolt(ComponentType.NORTHBOUND_KAFKA_SPOUT.toString()),
//                1)
//                .shuffleGrouping(ComponentType.NORTHBOUND_KAFKA_SPOUT.toString(), LcmKafkaSpout.STREAM_ID_LCM)
//                .shuffleGrouping(ComponentType.LCM_SPOUT.toString());

        /*
         * Spout receives all Northbound requests.
         */
        kafkaSpoutConfig = makeKafkaSpoutConfigBuilder(
                ComponentType.NORTHBOUND_KAFKA_SPOUT.toString(), config.getKafkaFlowTopic()).build();
        // (crimi) - commenting out LcmKafkaSpout here due to dying worker
        //kafkaSpout = new LcmKafkaSpout<>(kafkaSpoutConfig);
        kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);
        builder.setSpout(ComponentType.NORTHBOUND_KAFKA_SPOUT.toString(), kafkaSpout, parallelism);

        /*
         * Bolt splits requests on streams.
         * It groups requests by flow-id.
         */
        SplitterBolt splitterBolt = new SplitterBolt();
        builder.setBolt(ComponentType.SPLITTER_BOLT.toString(), splitterBolt, parallelism)
                .shuffleGrouping(ComponentType.NORTHBOUND_KAFKA_SPOUT.toString());

        /*
         * Bolt handles flow CRUD operations.
         * It groups requests by flow-id.
         */
        CrudBolt crudBolt = new CrudBolt(pathComputerAuth);
        ComponentObject.serialized_java(org.apache.storm.utils.Utils.javaSerialize(pathComputerAuth));

        boltSetup = builder.setBolt(ComponentType.CRUD_BOLT.toString(), crudBolt, parallelism)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.CREATE.toString(), fieldFlowId)
                // TODO: this READ is used for single and for all flows. But all flows shouldn't be fieldsGrouping.
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.READ.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.UPDATE.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.DELETE.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.PUSH.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.UNPUSH.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.PATH.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.RESTORE.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.REROUTE.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.STATUS.toString(), fieldFlowId)
                // TODO: this CACHE_SYNC shouldn't be fields-grouping - there is no field - it should be all - but tackle during multi instance testing
                .fieldsGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.CACHE_SYNC.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.TRANSACTION_BOLT.toString(), StreamType.STATUS.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.SPEAKER_BOLT.toString(), StreamType.STATUS.toString(), fieldFlowId)
                .fieldsGrouping(ComponentType.TOPOLOGY_ENGINE_BOLT.toString(), StreamType.STATUS.toString(), fieldFlowId);
//                .shuffleGrouping(
//                        ComponentType.LCM_FLOW_SYNC_BOLT.toString(), LcmFlowCacheSyncBolt.STREAM_ID_SYNC_FLOW_CACHE);
        ctrlTargets.add(new CtrlBoltRef(ComponentType.CRUD_BOLT.toString(), crudBolt, boltSetup));

        /*
         * Bolt sends cache updates.
         */
        KafkaBolt cacheKafkaBolt = createKafkaBolt(config.getKafkaTopoCacheTopic());
        builder.setBolt(ComponentType.CACHE_KAFKA_BOLT.toString(), cacheKafkaBolt, parallelism)
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.CREATE.toString())
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.UPDATE.toString())
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.DELETE.toString())
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.STATUS.toString());

        /*
         * Spout receives Topology Engine response
         */
        KafkaSpout topologyKafkaSpout = createKafkaSpout(
                config.getKafkaFlowTopic(), ComponentType.TOPOLOGY_ENGINE_KAFKA_SPOUT.toString());
        builder.setSpout(ComponentType.TOPOLOGY_ENGINE_KAFKA_SPOUT.toString(), topologyKafkaSpout, parallelism);

        /*
         * Bolt processes Topology Engine responses, groups by flow-id field
         */
        TopologyEngineBolt topologyEngineBolt = new TopologyEngineBolt();
        builder.setBolt(ComponentType.TOPOLOGY_ENGINE_BOLT.toString(), topologyEngineBolt, parallelism)
                .shuffleGrouping(ComponentType.TOPOLOGY_ENGINE_KAFKA_SPOUT.toString());

        /*
         * Bolt sends Speaker requests
         */
        KafkaBolt speakerKafkaBolt = createKafkaBolt(config.getKafkaSpeakerTopic());
        builder.setBolt(ComponentType.SPEAKER_KAFKA_BOLT.toString(), speakerKafkaBolt, parallelism)
                .shuffleGrouping(ComponentType.TRANSACTION_BOLT.toString(), StreamType.CREATE.toString())
                .shuffleGrouping(ComponentType.TRANSACTION_BOLT.toString(), StreamType.DELETE.toString());

        /*
         * Spout receives Speaker responses
         */
        KafkaSpout speakerKafkaSpout = createKafkaSpout(
                config.getKafkaFlowTopic(), ComponentType.SPEAKER_KAFKA_SPOUT.toString());
        builder.setSpout(ComponentType.SPEAKER_KAFKA_SPOUT.toString(), speakerKafkaSpout, parallelism);

        /*
         * Bolt processes Speaker responses, groups by flow-id field
         */
        SpeakerBolt speakerBolt = new SpeakerBolt();
        builder.setBolt(ComponentType.SPEAKER_BOLT.toString(), speakerBolt, parallelism)
                .shuffleGrouping(ComponentType.SPEAKER_KAFKA_SPOUT.toString());

        /*
         * Transaction bolt.
         */
        TransactionBolt transactionBolt = new TransactionBolt();
        boltSetup = builder.setBolt(ComponentType.TRANSACTION_BOLT.toString(), transactionBolt, parallelism)
                .fieldsGrouping(ComponentType.TOPOLOGY_ENGINE_BOLT.toString(), StreamType.CREATE.toString(), fieldSwitchId)
                .fieldsGrouping(ComponentType.TOPOLOGY_ENGINE_BOLT.toString(), StreamType.DELETE.toString(), fieldSwitchId)
// (crimi) - whereas this doesn't belong here per se (Response from TE), it looks as though
// nobody receives this message
//                .fieldsGrouping(ComponentType.TOPOLOGY_ENGINE_BOLT.toString(), StreamType.RESPONSE.toString(), fieldSwitchId)
//
                .fieldsGrouping(ComponentType.SPEAKER_BOLT.toString(), StreamType.CREATE.toString(), fieldSwitchId)
                .fieldsGrouping(ComponentType.SPEAKER_BOLT.toString(), StreamType.DELETE.toString(), fieldSwitchId);
        ctrlTargets.add(new CtrlBoltRef(ComponentType.TRANSACTION_BOLT.toString(), transactionBolt, boltSetup));

        /*
         * Error processing bolt
         */
        ErrorBolt errorProcessingBolt = new ErrorBolt();
        builder.setBolt(ComponentType.ERROR_BOLT.toString(), errorProcessingBolt, parallelism)
                .shuffleGrouping(ComponentType.SPLITTER_BOLT.toString(), StreamType.ERROR.toString())
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.ERROR.toString());

        /*
         * Bolt forms Northbound responses
         */
        NorthboundReplyBolt northboundReplyBolt = new NorthboundReplyBolt();
        builder.setBolt(ComponentType.NORTHBOUND_REPLY_BOLT.toString(), northboundReplyBolt, parallelism)
                .shuffleGrouping(ComponentType.CRUD_BOLT.toString(), StreamType.RESPONSE.toString())
                .shuffleGrouping(ComponentType.ERROR_BOLT.toString(), StreamType.RESPONSE.toString());

        /*
         * Bolt sends Northbound responses
         */
        KafkaBolt northboundKafkaBolt = createKafkaBolt(config.getKafkaNorthboundTopic());
        builder.setBolt(ComponentType.NORTHBOUND_KAFKA_BOLT.toString(), northboundKafkaBolt, parallelism)
                .shuffleGrouping(ComponentType.NORTHBOUND_REPLY_BOLT.toString(), StreamType.RESPONSE.toString());

        createCtrlBranch(builder, ctrlTargets);
        createHealthCheckHandler(builder, ServiceType.FLOW_TOPOLOGY.getId());

//        builder.setBolt(
//                ComponentType.TOPOLOGY_ENGINE_OUTPUT.toString(), createKafkaBolt(config.getKafkaTopoEngTopic()), 1)
//                .shuffleGrouping(ComponentType.LCM_FLOW_SYNC_BOLT.toString(), LcmFlowCacheSyncBolt.STREAM_ID_TPE);

        return builder.createTopology();
    }

    public static void main(String[] args) {
        try {
            LaunchEnvironment env = new LaunchEnvironment(args);
            (new FlowTopology(env)).setup();
        } catch (Exception e) {
            System.exit(handleLaunchException(e));
        }
    }
}
