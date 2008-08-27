/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.model;

import eu.secse.deliveryManager.federations.securepubsub.core.SecureFederationUser;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
public class DFederationReKey extends DFederation{
    
    private Map<PublicKey,byte[]> encryptedFederationKey;
    private long keyVersion;

    public DFederationReKey(String federationId, Key newKey, long newKeyVersion, Collection<SecureFederationUser> receivers) {
        super(federationId);
        this.keyVersion = newKeyVersion;
        encryptedFederationKey = new HashMap<PublicKey, byte[]>();
        
        for(SecureFederationUser u : receivers){
            PublicKey k = u.getUserKey();
            try {
                Cipher cipher = Cipher.getInstance(k.getAlgorithm());
                cipher.init(Cipher.ENCRYPT_MODE, k);
                encryptedFederationKey.put(k, cipher.doFinal(Support.Support.objectToBytes(newKey)));
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Key getFederationKey(PublicKey publicKey,PrivateKey privateKey){
        try {
            //Getting the encrypted federation key associated with the user public key
            byte[] encryptedKey = encryptedFederationKey.get(publicKey);
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return (Key) Support.Support.bytesToObject(cipher.doFinal(encryptedKey));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DFederationReKey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public long getKeyVersion(){
        return keyVersion;
    }    

    public String getType() {
        return "FED-rekey@"+federationId;
    }
}
