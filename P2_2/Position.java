

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class Position   implements Copyable, Serializable{

    public String timestamp=  "" ; /* maximum length = (255) */
    public String route=  "" ; /* maximum length = (255) */
    public String vehicle=  "" ; /* maximum length = (255) */
    public int stopNumber= 0;
    public int numStops= 0;
    public int timeBetweenStops= 0;
    public String trafficConditions=  "" ; /* maximum length = (255) */
    public int fillInRatio= 0;

    public Position() {

    }
    public Position (Position other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Position self;
        self = new  Position();
        self.clear();
        return self;

    }

    public void clear() {

        timestamp=  ""; 
        route=  ""; 
        vehicle=  ""; 
        stopNumber= 0;
        numStops= 0;
        timeBetweenStops= 0;
        trafficConditions=  ""; 
        fillInRatio= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Position otherObj = (Position)o;

        if(!timestamp.equals(otherObj.timestamp)) {
            return false;
        }
        if(!route.equals(otherObj.route)) {
            return false;
        }
        if(!vehicle.equals(otherObj.vehicle)) {
            return false;
        }
        if(stopNumber != otherObj.stopNumber) {
            return false;
        }
        if(numStops != otherObj.numStops) {
            return false;
        }
        if(timeBetweenStops != otherObj.timeBetweenStops) {
            return false;
        }
        if(!trafficConditions.equals(otherObj.trafficConditions)) {
            return false;
        }
        if(fillInRatio != otherObj.fillInRatio) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += timestamp.hashCode(); 
        __result += route.hashCode(); 
        __result += vehicle.hashCode(); 
        __result += (int)stopNumber;
        __result += (int)numStops;
        __result += (int)timeBetweenStops;
        __result += trafficConditions.hashCode(); 
        __result += (int)fillInRatio;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PositionTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        Position typedSrc = (Position) src;
        Position typedDst = this;

        typedDst.timestamp = typedSrc.timestamp;
        typedDst.route = typedSrc.route;
        typedDst.vehicle = typedSrc.vehicle;
        typedDst.stopNumber = typedSrc.stopNumber;
        typedDst.numStops = typedSrc.numStops;
        typedDst.timeBetweenStops = typedSrc.timeBetweenStops;
        typedDst.trafficConditions = typedSrc.trafficConditions;
        typedDst.fillInRatio = typedSrc.fillInRatio;

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("timestamp: ").append(timestamp).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("route: ").append(route).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("vehicle: ").append(vehicle).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("stopNumber: ").append(stopNumber).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numStops: ").append(numStops).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("timeBetweenStops: ").append(timeBetweenStops).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("trafficConditions: ").append(trafficConditions).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("fillInRatio: ").append(fillInRatio).append("\n");  

        return strBuffer.toString();
    }

}
