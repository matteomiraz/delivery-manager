/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.model;

import Support.Support;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
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
public class DFederationEncryptedMessage extends DFederation{
    private byte[] encryptedMessage;
    private long keyVersion;
    
    public DFederationEncryptedMessage(String federationId, Deliverable elem,
            Collection<MetaData> metadata, Key federationKey, long keyVersion) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        super(federationId);
        this.keyVersion = keyVersion;
        DFederationPlainMessage message =
                new DFederationPlainMessage(federationId, elem, metadata);
        Cipher cipher = Cipher.getInstance(federationKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, federationKey);
        encryptedMessage = cipher.doFinal(Support.objectToBytes(message));
    }
    
    public DFederationPlainMessage decrypt(Key federationKey) throws InvalidKeyException{
        try {
            Cipher cipher = Cipher.getInstance(federationKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, federationKey);
            return (DFederationPlainMessage) Support.bytesToObject(cipher.doFinal(encryptedMessage));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DFederationEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DFederationEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DFederationEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DFederationEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public long getKeyVersion(){
        return keyVersion;
    }

    public String getType() {
        return "FED-encrypted message@" + federationId;
    }
}
