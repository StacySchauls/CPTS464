
/* AccidentSubscriber.java

A publication of data of type Accident

This file is derived from code automatically generated by the rtiddsgen 
command:

rtiddsgen -language java -example <arch> .idl

Example publication of type Accident automatically generated by 
'rtiddsgen' To test them follow these steps:

(1) Compile this file and the example subscription.

(2) Start the subscription on the same domain used for RTI Data Distribution
Service with the command
java AccidentSubscriber <domain_id> <sample_count>

(3) Start the publication on the same domain used for RTI Data Distribution
Service with the command
java AccidentPublisher <domain_id> <sample_count>

(4) [Optional] Specify the list of discovery initial peers and 
multicast receive addresses via an environment variable or a file 
(in the current working directory) called NDDS_DISCOVERY_PEERS. 

You can run any number of publishers and subscribers programs, and can 
add and remove them dynamically from the domain.

Example:

To run the example application on domain <domain_id>:

Ensure that $(NDDSHOME)/lib/<arch> is on the dynamic library path for
Java.                       

On UNIX systems: 
add $(NDDSHOME)/lib/<arch> to the 'LD_LIBRARY_PATH' environment
variable

On Windows systems:
add %NDDSHOME%\lib\<arch> to the 'Path' environment variable

Run the Java applications:

java -Djava.ext.dirs=$NDDSHOME/class AccidentPublisher <domain_id>

java -Djava.ext.dirs=$NDDSHOME/class AccidentSubscriber <domain_id>  
*/

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;

// ===========================================================================

public class AccidentSubscriber {
	// -----------------------------------------------------------------------
	// Public Methods
	// -----------------------------------------------------------------------

	public static void main(String[] args) {
		// --- Get domain ID --- //
		int domainId = 0;
		if (args.length >= 1) {
			domainId = Integer.valueOf(args[0]).intValue();
		}

		// -- Get max loop count; 0 means infinite loop --- //
		int sampleCount = 0;
		if (args.length >= 2) {
			sampleCount = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Uncomment this to turn on additional logging
		 * Logger.get_instance().set_verbosity_by_category(
		 * LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
		 * LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
		 */

		// --- Run --- //
		subscriberMain(domainId, sampleCount);
	}

	// -----------------------------------------------------------------------
	// Private Methods
	// -----------------------------------------------------------------------

	// --- Constructors: -----------------------------------------------------

	private AccidentSubscriber() {
		super();
	}

	// -----------------------------------------------------------------------

	private static void subscriberMain(int domainId, int sampleCount) {

		DomainParticipant participant = null;
		Subscriber subscriber = null;
		Topic topic = null;
		Topic topic1 = null;
		DataReaderListener listener = null;
		AccidentDataReader reader = null;
		PositionDataReader reader1 = null;

		try {

			// --- Create participant --- //

			/*
			 * To customize participant QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			participant = DomainParticipantFactory.TheParticipantFactory.create_participant(domainId,
					DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}

			// --- Create subscriber --- //

			/*
			 * To customize subscriber QoS, use the configuration file USER_QOS_PROFILES.xml
			 */

			subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (subscriber == null) {
				System.err.println("create_subscriber error\n");
				return;
			}

			// --- Create topic --- //

			/* Register type before creating topic */
			String typeName = AccidentTypeSupport.get_type_name();
			AccidentTypeSupport.register_type(participant, typeName);

			String typeName1 = PositionTypeSupport.get_type_name();
			PositionTypeSupport.register_type(participant, typeName1);

			/*
			 * To customize topic QoS, use the configuration file USER_QOS_PROFILES.xml
			 */

			topic = participant.create_topic("CPTS464 Schauls: T0", typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
					null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			topic1 = participant.create_topic("CPTS464 Schauls: T1", typeName1, DomainParticipant.TOPIC_QOS_DEFAULT,
					null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (topic1 == null) {
				System.err.println("create_topic error\n");
				return;
			}

			// --- Create reader --- //

			listener = new AccidentListener();

			/*
			 * To customize data reader QoS, use the configuration file
			 * USER_QOS_PROFILES.xml
			 */

			reader = (AccidentDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT,
					listener, StatusKind.STATUS_MASK_ALL);
			if (reader == null) {
				System.err.println("create_datareader error\n");
				return;
			}

			reader1 = (PositionDataReader) subscriber.create_datareader(topic1, Subscriber.DATAREADER_QOS_DEFAULT,
					listener, StatusKind.STATUS_MASK_ALL);
			if (reader1 == null) {
				System.err.println("create_datareader error\n");
				return;
			}

			// --- Wait for data --- //

			final long receivePeriodSec = 4;

			for (int count = 0; (sampleCount == 0) || (count < sampleCount); ++count) {

				try {
					Thread.sleep(receivePeriodSec * 1000); // in millisec
				} catch (InterruptedException ix) {
					System.err.println("INTERRUPTED");
					break;
				}
			}
		} finally {

			// --- Shutdown --- //

			if (participant != null) {
				participant.delete_contained_entities();

				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
			/*
			 * RTI Data Distribution Service provides the finalize_instance() method for
			 * users who want to release memory used by the participant factory singleton.
			 * Uncomment the following block of code for clean destruction of the
			 * participant factory singleton.
			 */
			// DomainParticipantFactory.finalize_instance();
		}
	}

	// -----------------------------------------------------------------------
	// Private Types
	// -----------------------------------------------------------------------

	// =======================================================================

	private static class AccidentListener extends DataReaderAdapter {

		AccidentSeq _dataSeq = new AccidentSeq();
		SampleInfoSeq _infoSeq = new SampleInfoSeq();
		PositionSeq pdata = new PositionSeq();
		int flag = 0;

		public void on_data_available(DataReader reader) {
			AccidentDataReader AccidentReader = null;
			PositionDataReader Position_reader = null;
			if (reader.getClass() == PositionDataReader.class) {
				System.out.println("ITS POSITION");
				try {
					Position_reader = (PositionDataReader) reader;
					Position_reader.take(pdata, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
							SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
							InstanceStateKind.ANY_INSTANCE_STATE);

					for (int i = 0; i < pdata.size(); ++i) {
						SampleInfo info = (SampleInfo) _infoSeq.get(i);

						if (info.valid_data) {
							Position p = pdata.get(i);
							System.out
									.println(MessageFormat.format("Position\t{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t",
											p.route, p.vehicle, p.trafficConditions, p.stopNumber, p.numStops,
											p.timeBetweenStops, p.fillInRatio, p.timestamp));
							flag = 1;

						}
					}
				} catch (RETCODE_NO_DATA noData) {
					// No data to process
					System.out.println("Error");
				}
			} else {
				System.out.println("ITS ACCIDENT");
				AccidentReader = (AccidentDataReader) reader;

				try {
					AccidentReader.take(_dataSeq, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
							SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
							InstanceStateKind.ANY_INSTANCE_STATE);

					for (int i = 0; i < _dataSeq.size(); ++i) {
						SampleInfo info = (SampleInfo) _infoSeq.get(i);

						if (info.valid_data) {

							Accident p = _dataSeq.get(i);
							System.out
									.println(MessageFormat.format("Accident\t{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t",
											p.route, p.vehicle, "", p.stopNumber, "", "", "", p.timestamp));
							flag = 0;

						}
					}
				} catch (RETCODE_NO_DATA noData) {
					// No data to process
				}
			}

			if (flag == 1) {
				Position_reader.return_loan(pdata, _infoSeq);
			} else {
				AccidentReader.return_loan(_dataSeq, _infoSeq);
			}
		}
	}
}
