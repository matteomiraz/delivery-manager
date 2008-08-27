/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Support;

import eu.secse.deliveryManager.model.MetaDataSignature;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mario
 */
public class Support {
    public static byte[] objectToBytes(Object o){
        //Getting a byte array form a object
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(data);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            objectOutputStream.close();
            data.close();
        } catch (IOException ex) {
            Logger.getLogger(MetaDataSignature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data.toByteArray();
    }
    
    public static Serializable bytesToObject(byte[] bytes){
        ObjectInputStream os=null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            os = new ObjectInputStream(is);
            return (Serializable) os.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
