package com.gigaspaces.mq.util;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

import java.io.IOException;

public class ReadQueueMessage {

    public String receiveMsg() {

        MQQueueManager queueManager = null;
        MQMessage mqMessage = null;

        try {
            // creating queue manager
            queueManager = new MQQueueManager("PUBSRC");
            // creating queue`
            MQQueue queue = queueManager.accessQueue("ASN.PUBSRC.DATA", CMQC.MQOO_INPUT_SHARED);
            // configuring message encoding, character set, and format
            mqMessage = new MQMessage();
            queue.get(mqMessage);
            // Closing Queue after putting message
            queue.close();
            return mqMessage.readStringOfByteLength(mqMessage.getMessageLength());
        } catch (MQException | IOException je) {
            je.printStackTrace(System.err);
        } finally {
            if (queueManager != null) {
                try {
                    // Disconnecting queue manager
                    queueManager.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        return "";

    }

    public static void main(String[] args) {
        ReadQueueMessage readQueueMessage = new ReadQueueMessage();
        String msg = readQueueMessage.receiveMsg();
        System.out.println(msg);
    }

}