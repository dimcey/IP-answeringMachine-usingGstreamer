package configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Config.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
public class Config {

    //Instanciate Properties
    static Properties prop = new Properties();

    /**
     * Empty constructor.
     */
        public Config() {

    }

    /**
     * Get a specific persistent property.
     *
     * @param property is the name of the desired property.
     * @return the desired property value.
     */
    public String getProperties(String property) {
        //Local variables
        String result = "-1";
        InputStream input = null;

        try {
            //Get a glasses to read.
            input = new FileInputStream("properties.txt");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            result = prop.getProperty(property);

        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return result;
    }

    /**
     * Set a specific persistent property.
     *
     * @param property is the name of the property to change.
     * @param value is the value to give to the property.
     */
    public void setProperties(String property, String value) {

        OutputStream output = null;

        try {

            //Get a pen to write.
            output = new FileOutputStream("properties.txt");

            // set the property value
            prop.setProperty(property, value);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
                }
            }

        }
    }

    /**
     * Get all the properties.
     */
    public void getAll() {

        InputStream input = null;

        try {

            //Get a glasses to read.
            input = new FileInputStream("properties.txt");

            //Load properties
            prop.load(input);

            //Design
            System.out.println("+----------------------------------------------------------+");
            System.out.println("|         persistent set of properties available           |");
            System.out.println("+----------------------------------------------------------+");

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                System.out.println("| Key : " + String.format("%-15s", key) + ", Value : " + String.format("%-25s", value) + " |");
            }
            
            System.out.println("+----------------------------------------------------------+");

        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
