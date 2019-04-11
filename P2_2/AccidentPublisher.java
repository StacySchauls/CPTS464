

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
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

// ===========================================================================

public class AccidentPublisher implements Runnable{
	private Bus passedBus;
	private Route passedRoute;
	private int domain;
	private int sampleCount;
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

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

        
        
        List<Route> routes = new ArrayList<Route>();
    	int numRoutes = 0, numVehicles = 0, numInitialBackup = 0, currentRoute = 0,
    			currentVehicle = 0;
    	List<String> routeNames = new ArrayList<String>();
    	File file = new File("pub.properties");
		Scanner scanner = new Scanner(file);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		Regex routeReg = new Regex("^[A-z]*[0-9]$");
		Pattern p = Pattern.compile("^[A-z]*[0-9]$");
		
		
		
		while((line = br.readLine()) != null) {
			if(line == null)
				continue;
			if(line.charAt(0) != '#') {
				String prop = line.split("=")[0];
				String value = line.split("=")[1];
				
				if(prop.equalsIgnoreCase("numRoutes")) {
					numRoutes = Integer.parseInt(value);
					System.out.println("NumRoutes: "+numRoutes);
					continue;
				}
				if(prop.equalsIgnoreCase("numVehicles")) {
					numVehicles = Integer.parseInt(value);
					System.out.println("NumVehicles: "+numVehicles);
					continue;
				}
				if(prop.equalsIgnoreCase("numinitialbackupvehicles")) {
					numInitialBackup = 1;
					//System.out.println(value);
					//numInitialBackup = Integer.parseInt(value.split("#")[0]);
					continue;
				}
				
				Matcher matcher = p.matcher(prop);
				
				if(matcher.find()) {
					System.out.println("Adding new route "+value.split("#")[0]);
					routes.add(new Route(value.split("#")[0]));
					
					continue;
				}
				if(prop.contains("numStops")) {
					System.out.println("NumStops "+value);
					routes.get(currentRoute).numStops = Integer.parseInt(value);
					continue;
				}
				if(prop.contains("TimeBetweenStops")) {
					System.out.println("timeBW "+value);
					routes.get(currentRoute).timeBetweenStops = Integer.parseInt(value);
					continue;
				}
				//Name busses now
				if(prop.contains("Vehicle") && currentVehicle < numVehicles && !value.contains("#")) {
					if(value.contains("#")) {
						//System.out.println("contains #");
					}
					//System.out.println("currentroute is "+currentRoute+" Current vehicle is "+value);
					
					Route ro = routes.get(currentRoute);
					ro.busses.add(new Bus(value, 0));
					currentVehicle++;
					if(currentVehicle == numVehicles) {
						currentRoute++;
						currentVehicle = 0;
					}
					continue;
				}
			}
		}//end while
		Thread ths[] = new Thread[numRoutes * numVehicles];
		int i = 0;
		for(Route r : routes) {
			Route passedRoute = r;
			for(Bus b : r.busses) {
				Bus passedBus = b;
				new Thread(new AccidentPublisher(domainId, sampleCount, passedRoute, passedBus )).start();
				i++;
			}
		}
		i = 0;
		for(Route r : routes) {
			Route passedRoute = r;
			for(Bus b : r.busses) {
				//ths[i].start();
				
			}
		}
		
		
        
        
        
        
        
        
        
        
        /* Uncomment this to turn on additional logging
        Logger.get_instance().set_verbosity_by_category(
            LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
            LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
        */
        
        // --- Run --- //
       
    }

    // -----------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------

    private AccidentPublisher(int domain, int sample, Route passedR, Bus passedB) {
    	super();
    	this.domain = domain;
    	this.sampleCount = sample;
    	this.passedRoute = passedR;
    	this.passedBus = passedB;
    	System.out.println("Passed in routeInname as "+passedR.name);
        
    }

    // -----------------------------------------------------------------------

    private static void publisherMain(int domainId, int sampleCount, Route routeIn, Bus busIn) throws IOException  {
    	
    	

        DomainParticipant participant = null;
        Publisher publisher = null;
        Topic topic = null, topic1 = null;
        AccidentDataWriter writer = null;
        PositionDataWriter writer1 = null;
        
        Random random = new Random();
        int randomNum = random.nextInt(101);

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
            
            
            String typeName1 = PositionTypeSupport.get_type_name();
            //String typeName1 = AccidentTypeSupport.get_type_name();
            PositionTypeSupport.register_type(participant, typeName1);

            /* To customize topic QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            topic = participant.create_topic(
                "CPTS464 Schauls: T0",
                typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (topic == null) {
                System.err.println("create_topic error\n");
                return;
            }      
            
            topic1 = participant.create_topic(
                    "CPTS464 Schauls: T1",
                    typeName1, DomainParticipant.TOPIC_QOS_DEFAULT,
                    null /* listener */, StatusKind.STATUS_MASK_NONE);
                if (topic1 == null) {
                    System.err.println("create_topic error\n");
                    return;
                }   

            // --- Create writer --- //

            /* To customize data writer QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            //accident writer
                
                
            writer = 
             (AccidentDataWriter) publisher.create_datawriter(
                topic, Publisher.DATAWRITER_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (writer == null) {
                System.err.println("create_datawriter error\n");
                return;
            }          
            
            //position writer
            writer1 = 
                     (PositionDataWriter) publisher.create_datawriter(
                        topic1, Publisher.DATAWRITER_QOS_DEFAULT,
                        null /* listener */, StatusKind.STATUS_MASK_NONE);
                    if (writer1 == null) {
                        System.err.println("create_datawriter error\n");
                        return;
                    }      

             AccidentDataWriter accident_writer = (AccidentDataWriter) writer;
             PositionDataWriter position_writer = (PositionDataWriter) writer1;
            // --- Write --- //

            /* Create data sample for writing *
             * 
             */
            Accident instance = new Accident();
            Position posIn = new Position();

            InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
            /* For a data type that has a key, if the same instance is going to be
            written multiple times, initialize the key here
            and register the keyed instance prior to writing */
            //instance_handle = writer.register_instance(instance);
            int passes = 0;
            long sendPeriodMillis = 4 * 1000; // 4 seconds

            for (int count = 0;
            (sampleCount == 0) || (count < sampleCount);
            ++count) {
            	
            	System.out.println("Count: "+count+ " numumStops "+routeIn.numStops);
            	if(count % routeIn.numStops == 0) passes++;
            	if(passes>3) break;
            	
            	
                System.out.println("Writing Accident, count " + count);
                
                int traffic = random.nextInt(101);
                if(traffic <= 25)//light 25%
                {
                	posIn.trafficConditions = "light";
                	posIn.timeBetweenStops = (int)(Math.ceil(routeIn.timeBetweenStops *.75 ));
                	
                }else if(traffic <=50) {
                	//heavy
                	posIn.trafficConditions = "heavy";
                	posIn.timeBetweenStops = (int)(Math.ceil(routeIn.timeBetweenStops *.25 ));
                }else {
                	//normal
                	posIn.trafficConditions = "normal";
                	posIn.timeBetweenStops = (int)(Math.ceil(routeIn.timeBetweenStops *1));
                }
                
                LocalDateTime now = LocalDateTime.now();
        		
        		int hour = now.getHour();
        		int min = now.getMinute();
        		int sec = now.getSecond();
        		
        		Format formatter = new SimpleDateFormat("HH.mm.ss");
                
                instance.timestamp = formatter.format(new Date());;
                instance.route = routeIn.name;
                instance.vehicle = busIn.id;
                instance.stopNumber = busIn.stop;
                
                
                posIn.timestamp = formatter.format(new Date());
                System.out.println("name is "+routeIn.name);
                posIn.route = routeIn.name;
                posIn.vehicle = busIn.id;
                posIn.stopNumber = busIn.stop;
                posIn.numStops = routeIn.numStops;
                posIn.fillInRatio = random.nextInt(101);
                
                //detect accident
                int acc = random.nextInt(101);
                if(acc <= 10) {
                	accident_writer.write(instance, instance_handle);
                   
                    posIn.timeBetweenStops += 10;
                    
                }
                sendPeriodMillis =  posIn.timeBetweenStops * 1000;
                busIn.stop = (busIn.stop % routeIn.numStops + 1); //mod so we wrap around. goes back to 0
                
                position_writer.write_untyped(posIn, instance_handle);

                //Go nighty night thread
                try {
                    Thread.sleep(sendPeriodMillis);
                } catch (InterruptedException ix) {
                    System.err.println("INTERRUPTED");
                    break;
                }
                
                
                /* Modify the instance to be written here */

                /* Write data */
                
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
		System.out.println("Starting thread\n");
		
		 try {
			publisherMain(this.domain,this.sampleCount, this.passedRoute, this.passedBus);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

