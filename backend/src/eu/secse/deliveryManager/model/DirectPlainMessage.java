/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.model;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Mario
 */
public class DirectPlainMessage implements DirectMessage{
    private String receiverID;
    private Serializable message;

    DirectPlainMessage(String receiverID, Serializable message) {
        this.receiverID = receiverID;
        this.message = message;
    }

    public String getType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getReceiverID() {
        return receiverID;
    }

    public Serializable getMessage() {
        return message;
    }
    
    public DirectEncryptedMessage encrypt(PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        return new DirectEncryptedMessage(publicKey, message);        
    }
}
