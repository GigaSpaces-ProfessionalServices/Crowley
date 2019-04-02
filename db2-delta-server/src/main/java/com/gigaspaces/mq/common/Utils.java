package com.gigaspaces.mq.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.*;

public class Utils {

    FileReader fr;
    Properties properties;
    Logger logger;

    public Utils() {

        try {
            initProps();
        } catch (FileNotFoundException e) {
            initPropsUsingDefaults();
        }

    }

    private void initPropsUsingDefaults() {
        InputStream input = null;
        try {
            String filename = "qparam.properties";
            input = this.getClass().getClassLoader().getResourceAsStream(filename);
            //load the properties from the default qparam.properties
            properties = new Properties();
            properties.load(input);
            System.out.println("Read parameter values from the default 'qparam.properties'.");
        } catch (IOException ex) {
            System.out.println("Reading values from the default 'qparam.properties' failed.");
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initProps() throws FileNotFoundException {
        try {
            fr = new FileReader("qparam.properties");
        } catch (FileNotFoundException e) {
            System.out.println("File 'qparam.properties' not found ");
            throw e;
        }
        properties = new Properties();
        try {
            properties.load(fr);
        } catch (IOException e) {
            System.out.println("Reading values from 'qparam.properties' Failed. ");
            e.printStackTrace();
        }
    }


    public Properties getProps() throws FileNotFoundException {

        if (properties == null) {
            this.initProps();
        }
        return properties;
    }

    public Logger getLogger() {


        if (logger == null) {
            logger = Logger.getLogger(Utils.class.getName());
        }
        return logger;

    }


}
