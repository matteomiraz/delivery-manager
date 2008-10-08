/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.model;

import Support.Support;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class DirectEncryptedMessage implements DirectMessage {

    private PublicKey receiverID;
    private byte[] encryptedSimmetricKey;
    private String simmetricAlgorithm;
    private byte[] encryptedMessage;

    public DirectEncryptedMessage(PublicKey publicKey, String simmetricAlgorithm,int simmetricKeySize, Serializable message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
        //Setting the receiverID
        this.receiverID = publicKey;
        
        Cipher cipher;
        //Generating a simmetric key to encrypt the content of the message
        KeyGenerator keyGenerator = KeyGenerator.getInstance(simmetricAlgorithm, "BC");
        keyGenerator.init(simmetricKeySize);
        SecretKey simmetricKey = keyGenerator.generateKey();
        //Encrypting the message
        cipher = Cipher.getInstance(simmetricKey.getAlgorithm(), "BC");
        cipher.init(Cipher.ENCRYPT_MODE, simmetricKey);
        encryptedMessage = cipher.doFinal(Support.objectToBytes(message));
        //Encrypting the simmetric key
        cipher = Cipher.getInstance(publicKey.getAlgorithm(), "BC");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        cipher.wrap(simmetricKey);
        this.simmetricAlgorithm = simmetricAlgorithm;
    }

    public DirectPlainMessage decrypt(PrivateKey privateKey) throws InvalidKeyException {
        Serializable message = null;
        try {
            Cipher cipher;
            //Decrypting the simmetric key
            cipher = Cipher.getInstance(privateKey.getAlgorithm(),"BC");
            cipher.init(Cipher.UNWRAP_MODE, privateKey);
            SecretKey simmetricKey = (SecretKey) cipher.unwrap(encryptedSimmetricKey, simmetricAlgorithm, Cipher.SECRET_KEY);
            //Decrypting the message
            cipher = Cipher.getInstance(simmetricKey.getAlgorithm(),"BC");
            cipher.init(Cipher.DECRYPT_MODE, simmetricKey);
            message = Support.bytesToObject(cipher.doFinal(encryptedMessage));
            return new DirectPlainMessage(receiverID.toString(), message);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(DirectEncryptedMessage.class.getName()).log(Level.SEVERE, null, ex);
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
