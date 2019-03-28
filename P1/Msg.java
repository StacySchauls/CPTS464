

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

public class Msg   implements Copyable, Serializable{

    public int messageNumber= 0;
    public String sender=  "" ; /* maximum length = ((MSG_LEN.VALUE)) */
    public String message=  "" ; /* maximum length = ((MSG_LEN.VALUE)) */

    public Msg() {

    }
    public Msg (Msg other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Msg self;
        self = new  Msg();
        self.clear();
        return self;

    }

    public void clear() {

        messageNumber= 0;
        sender=  ""; 
        message=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Msg otherObj = (Msg)o;

        if(messageNumber != otherObj.messageNumber) {
            return false;
        }
        if(!sender.equals(otherObj.sender)) {
            return false;
        }
        if(!message.equals(otherObj.message)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)messageNumber;
        __result += sender.hashCode(); 
        __result += message.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MsgTypeSupport</code>
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

        Msg typedSrc = (Msg) src;
        Msg typedDst = this;

        typedDst.messageNumber = typedSrc.messageNumber;
        typedDst.sender = typedSrc.sender;
        typedDst.message = typedSrc.message;

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
        strBuffer.append("messageNumber: ").append(messageNumber).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("sender: ").append(sender).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("message: ").append(message).append("\n");  

        return strBuffer.toString();
    }

}
