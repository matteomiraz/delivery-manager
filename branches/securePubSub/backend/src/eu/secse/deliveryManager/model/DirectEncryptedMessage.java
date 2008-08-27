/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.model;

import Support.Support;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Mario
 */
public class DirectEncryptedMessage implements DirectMessage {

    private PublicKey receiverID;
    private byte[] encryptedMessage;

    public DirectEncryptedMessage(PublicKey publicKey, Serializable message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptedMessage = cipher.doFinal(Support.objectToBytes(message));
    }
    
    public DirectPlainMessage decrypt(PrivateKey privateKey) throws InvalidKeyException{
        Serializable message = null;
        try {
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            message = Support.bytesToObject(cipher.doFinal(encryptedMessage));
            return new DirectPlainMessage(receiverID.toString(), message);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DirectEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DirectEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DirectEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DirectEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new DirectPlainMessage(receiverID.toString(), message);
    }

    public String getType() {
        return "Encrypted direct message";
    }

    public String getReceiverID() {
        return receiverID.toString();
    }

    public Object getMessage() {
        return encryptedMessage;
    }
}
