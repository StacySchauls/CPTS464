

/* AccidentPublisher.java

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

On Unix: 
add $(NDDSHOME)/lib/<arch> to the 'LD_LIBRARY_PATH' environment
variable

On Windows:
add %NDDSHOME%\lib\<arch> to the 'Path' environment variable

Run the Java applications:

java -Djava.ext.dirs=$NDDSHOME/lib/java AccidentPublisher <domain_id>

java -Djava.ext.dirs=$NDDSHOME/lib/java AccidentSubscriber <domain_id>        
*/

import java.io.BufferedReader;
import java.util.Random;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;
import java.util.Scanner;
// ===========================================================================

public class AccidentPublisher implements Runnable{
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------
	public static int numRoutes = 0;
	public static int numVehicles = 0;
	public static Vehicle busses[];
	public static Route routes[];
	public static final int HEAVY = 0;
	public static final int LIGHT = 1;
	public static final int NORMAL = 2;
	private int domain;
	private int count;
	private String myRoute;
	private String myBusName;
	private int myNumStops;
	private int myTimeBetween;
	private Vehicle myBus;
	
	public static void parsePub() throws IOException {

		File file = new File("pub.properties");
		Scanner scanner = new Scanner(file);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		String pairs[];
		// get num routes, num vehicle, num backup
		//nt numRoutes = 0;
		//int numVehicles = 0idomainId, sampleCount;
		int numBackup = 0;
		int count = 0;
		
		while((st=br.readLine()) != null) {
			if(st.charAt(0) == '#') {
				System.out.println("we should ignore this line : " + st);
			}else {
				pairs = st.split("=");
				System.out.println(pairs[1]);
				System.out.println(st);
				if(count == 0) {
					numRoutes = Integer.parseInt(pairs[1]);
					
				}else if(count == 1) {
					numVehicles = Integer.parseInt(pairs[1]);				
				}else if (count == 2) {
					
				}
			}
			count ++;
			
		}
		System.out.println("Routes = " + numRoutes);
		System.out.println("Vehicles = " + numVehicles);
	}

	public static void pubLauncher() {
		//should parse pub here. get all the info we need. For now im going to use hard coded stuff
		numVehicles = 2;
		numRoutes = 2;
		//not sure if i need these two arrays or not
		busses = new Vehicle[numVehicles * numRoutes];
		routes = new Route[numRoutes];
		//routes = new Route[1];
		routes[0] = create_route("Express1",4,2,numVehicles);
		routes[1] = create_route("Express2",6,3, numVehicles);
		
		//create the vehicles. Need to know which vehicles go with which route. maybe use the name? idk
		Vehicle bus11 = create_bus(routes[0], "bus11", 4);
		Vehicle bus12 = create_bus(routes[0], "bus12", 4);

		
		Vehicle bus21 = create_bus(routes[1], "bus21", 2);
		Vehicle bus22 = create_bus(routes[1], "bus22", 2);
		
		//add the busses to the bus array
		busses[0] = bus11;
		busses[1] = bus12;
		busses[2] = bus21;
		busses[3] = bus22;

		Thread threads[] = new Thread[numVehicles * numRoutes];
		//for each vehicle on each route, start a pub thread
		int i;
		for(i = 0; i<numVehicles * numRoutes; i++) {
			System.out.println("Creating thread "+ i);
			threads[i] = new Thread(new AccidentPublisher(i,0,busses[i]));
		}
		
		for(i = 0; i<numVehicles * numRoutes; i++) {
			System.out.println("Starting thread " + i);
			threads[i].start();
		}
		
	}
	public static Vehicle create_bus(Route route, String name, int numThruRoute) {
		Vehicle bus = new Vehicle();
		bus.Accident = false;
		bus.breakdownDelayTime = 10;
		bus.route = route.name;
		bus.name = name;
		bus.NumTimesThruRoute = numThruRoute;
		bus.TimeBewteen = route.TimeBetween;
		bus.stopCount = 0;
		bus.timesAlreadyThru = 0;
		return bus;
	}
	
	public static Route create_route(String name, int NumStops, int TimeBetween,int numVehicles) {
		Route route = new Route();
		route.name = name;
		route.NumStops = NumStops;
		route.TimeBetween = TimeBetween;
		route.numVehicles = numVehicles;
		return route;
		
	}
	
	public static Boolean hasAccident() {
		Random rand = new Random();
		//10 % accident rate
		if(rand.nextInt(10) == 1) {
			return true;
		}
		return false;
	}
	
	public static int traffic() {
		Random rand = new Random();
		int num = rand.nextInt(100);
		// 25% light
		if(num < 25) {
			return LIGHT;
		}else if( num < 35 ){ // 10% heavy
			return HEAVY;
		}
		//last 65%
		return NORMAL;
	}
	
	public static int fill_in_ratio_generator() {
		Random rand = new Random();
		//number between 0 and 100
		return rand.nextInt(101);
	}
	
	
	public static Position reached_stop(Vehicle bus, int stopNum) {
		/* 1. Detect Accident
		 * 2. Detect Heavy (10%) or light (25%) or normal (65%) traffic
		 * 3. Get Fill in Ratio for bus -> random number between 0 and 100 inclusive
		 */
		
		//Need to publish position
		//Timestamp time = new Timestamp(System.currentTimeMillis());
		LocalDateTime now = LocalDateTime.now();
		
		int hour = now.getHour();
		int min = now.getMinute();
		int sec = now.getSecond();
		
		Format formatter = new SimpleDateFormat("HH.mm.ss");
		String TimeStamp = formatter.format(new Date());
		
		
		//String TimeStamp = "";
		
		//long new_time = time.getTime();
		Position pos = new Position();
		
		
		Boolean Acc = hasAccident();
		
		//traffic type
		int traffic_type = traffic();
	
		if(traffic_type == HEAVY) {
			bus.heavy = true;
			bus.light = false;
			bus.normal = false;
			pos.trafficConditions = "Heavy";
			//System.out.println("Heavy");
			}
		if(traffic_type == LIGHT) {
			bus.light = true;
			bus.heavy = false;
			bus.normal = false;
			pos.trafficConditions = "Light";
			//System.out.println("Light");
		}
		else {
			bus.normal = true;
			bus.heavy = false;
			bus.light = false;
			pos.trafficConditions = "Normal";
			//System.out.println("Normal");
		}
		
		//fill in ratio
		bus.VFR = fill_in_ratio_generator();
		
		
		
		pos.fillInRatio = bus.VFR;
		pos.stopNumber = stopNum;
		pos.route = bus.route;
		pos.timeBetweenStops = bus.TimeBewteen;
		pos.timestamp = TimeStamp;
		pos.vehicle = bus.name;
		pos.numStops = bus.numStops;
		
		return pos;
		
		
		
	}
	
	public static void displayPos(Position pos, Vehicle bus) {
		System.out.println("Bus "+bus.name+" Has published position at stop "+bus.stopCount+ "at time "+pos.timestamp+". Traffic conditions are "+
				pos.trafficConditions+"\nThe bus has a Fill-In-Ratio of "+bus.VFR );
		/*System.out.println(pos.timestamp);
		System.out.println("Fill in ratio: "+pos.fillInRatio);
		System.out.println("Traffic: "+pos.trafficConditions);*/
	}
	
    public static void main(String[] args) throws IOException {
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

        /* Uncomment this to turn on additional logging
        Logger.get_instance().set_verbosity_by_category(
            LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
            LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
        */

        // --- Run --- //
        System.out.println("trying parse pub");
        System.out.println("Creating new thread 1");
        pubLauncher();
        /* FOR TESTING */
        //create two busses
        
        //parsePub();
        
    }

    // -----------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------

    private AccidentPublisher(int domain, int count, Vehicle bus) {
    	super();
    	this.domain = domain;
    	this.count = count;
    	//this.myBusName = bus.name;
    	this.myNumStops = bus.numStops;
    	this.myRoute = bus.route;
    	this.myTimeBetween = bus.TimeBewteen;
    	//this.myBus.stopCount = bus.stopCount;
    	this.myBus = bus;
    	System.out.println("my name is "+this.myBus.name);
    	
    }

    // -----------------------------------------------------------------------

    private static void publisherMain(int domainId, int sampleCount,Vehicle bus) {

        DomainParticipant participant = null;
        Publisher publisher = null;
        Topic topic = null;
        Topic busTopic = null;
        Topic stopTopic = null;
        AccidentDataWriter writer = null;
        AccidentDataWriter busWriter = null;
        AccidentDataWriter stopWriter = null;
        Publisher vehicles[] = new Publisher[numVehicles];
        
        try {
            // --- Create participant --- //

            /* To customize participant QoS, use
            the configuration file
            USER_QOS_PROFILES.xml */

            participant = DomainParticipantFactory.TheParticipantFactory.
            create_participant(
                domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (participant == null) {
                System.err.println("create_participant error\n");
                return;
            }        

            // --- Create publisher --- //

            /* To customize publisher QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            publisher = participant.create_publisher(
                DomainParticipant.PUBLISHER_QOS_DEFAULT, null /* listener */,
                StatusKind.STATUS_MASK_NONE);
            if (publisher == null) {
                System.err.println("create_publisher error\n");
                return;
            }                   

            // --- Create topic --- //

            /* Register type before creating topic */
            String typeName = AccidentTypeSupport.get_type_name();
            AccidentTypeSupport.register_type(participant, typeName);

            /* To customize topic QoS, use
            the configuration file USER_QOS_PROFILES.xml */
            System.out.println("topic is " + bus.name);
            
            //Need to change topics/add topics?
            
            
            
            topic = participant.create_topic(
                bus.route,
                typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            
            stopTopic = participant.create_topic(
                    Integer.toString(bus.stopCount),
                    typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                    null /* listener */, StatusKind.STATUS_MASK_NONE);
            
            busTopic = participant.create_topic(
                    bus.name,
                    typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                    null /* listener */, StatusKind.STATUS_MASK_NONE);
            
            
            if (topic == null) {
                System.err.println("create_topic error\n");
                return;
            }           

            // --- Create writer --- //

            /* To customize data writer QoS, use
            the configuration file USER_QOS_PROFILES.xml */
            
            //make a writer for each topic
            
            
            writer = (AccidentDataWriter)
            publisher.create_datawriter(
                topic, Publisher.DATAWRITER_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            
            busWriter = (AccidentDataWriter)
                    publisher.create_datawriter(
                        busTopic, Publisher.DATAWRITER_QOS_DEFAULT,
                        null /* listener */, StatusKind.STATUS_MASK_NONE);
            
            stopWriter = (AccidentDataWriter)
                    publisher.create_datawriter(
                        stopTopic, Publisher.DATAWRITER_QOS_DEFAULT,
                        null /* listener */, StatusKind.STATUS_MASK_NONE);

            
            if (writer == null) {
                System.err.println("create_datawriter error\n");
                return;
            }           

            // --- Write --- //

            /* Create data sample for writing */
            Accident instance = new Accident();
            Accident routeInstance = new Accident();
            Accident stopInstance = new Accident();
            
            
            
            
            
            
            
            
            
            

            InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
            /* For a data type that has a key, if the same instance is going to be
            written multiple times, initialize the key here
            and register the keyed instance prior to writing */
            //instance_handle = writer.register_instance(instance);

            final long sendPeriodMillis = 0; // 4 seconds
            long start = System.nanoTime();
            
            /* TODO: FIX THE BREAK OUT OF THIS FOR LOOP FR THE BUS STOP COUNT AND SHIT */
            for (int count = 0;(sampleCount == 0) || (count < sampleCount); ++count ) {
            	

            
            	
            	//checks the count to know when to break out of the loop
            	if(bus.stopCount == bus.NumTimesThruRoute) {
            		
            		System.out.println("Bus "+bus.name+" has reached the end of the route.");
            		if(bus.timesAlreadyThru == 3) {
            			System.out.println("Bus "+bus.name+" has gone through 3 times. breaking.");
            			break;
            		}
            		bus.timesAlreadyThru++;
            		bus.stopCount = 0;
            	}
            		
                
                

                /* Modify the instance to be written here */
            	
            	//TIMING
            	long now = System.nanoTime();
            	//System.out.println((double)(start - now) /1000000000.0);
            	if( (double)(now - start) /1000000000.0 >= bus.TimeBewteen ) {
            		 System.out.println("This is bus " + bus.name +" on route " + bus.route + " stop count is "+bus.stopCount);
                 	instance.route = bus.route;
             		instance.vehicle = bus.name;
             		instance.stopNumber = bus.stopCount;
             		
             		stopInstance.stopNumber = bus.stopCount;
            		 bus.stopCount++;
            		 start = System.nanoTime();
            		 
            		if(hasAccident()) {
                    	System.out.println("Bus "+bus.name+" has had an accident!\n");
                    	bus.TimeBewteen += 10;
                    }
            		 Position pos = reached_stop(bus, bus.stopCount);
            		 
            		 //display the position
            		 displayPos(pos, bus);
            		 instance.timestamp = pos.timestamp;
            		 
            		 // write all the writers
            		 
            		 //writer.write(instance, instance_handle);
            		 //instance.message = "sent from bus "+bus.name;
            		// busWriter.write(instance, instance_handle);
            		 //instance.message = "sent from stop " + bus.stopCount;
            		 stopWriter.write(stopInstance, instance_handle);
            	}
               

                
                /* Write data */

                
                
                
                try {
                    Thread.sleep(sendPeriodMillis);
                } catch (InterruptedException ix) {
                    System.err.println("INTERRUPTED");
                    break;
                }
            }

            //writer.unregister_instance(instance, instance_handle);

        } finally {

            // --- Shutdown --- //

            if(participant != null) {
                participant.delete_contained_entities();

                DomainParticipantFactory.TheParticipantFactory.
                delete_participant(participant);
            }
            /* RTI Data Distribution Service provides finalize_instance()
            method for people who want to release memory used by the
            participant factory singleton. Uncomment the following block of
            code for clean destruction of the participant factory
            singleton. */
            //DomainParticipantFactory.finalize_instance();
        }
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		publisherMain(this.domain, this.count, this.myBus);
	}
}

