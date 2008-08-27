/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.model;

import Support.Support;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object that represents a Signature metadata of a message.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public class MetaDataSignature implements MetaData {

    private byte[] signature;
    private CertPath certificationPath;
    private String signatureAlgorithm;

    /**
     * Constructor of a MetaDataSignature metadata. Form the parameters
     * computes the signature of a message.
     * @param deliverable The message to be signed.
     * @param privateKey The private key used to sign the message.
     * This parameter should be <i>DigestAlgorithm</i>With<i>CryptograpicAlgorithm</i>.
     * Check on provider documentation for the supported algorithms.
     * @param signatureAlgorithm 
     * @param certificationPath The certification path that should be used to
     * verify the identity of the author of the message.
     * @throws java.security.NoSuchAlgorithmException This exception is thrown if
     * the specified algorithm isn't supported by the provider.
     * @throws java.security.InvalidKeyException This exception is thrown if the
     * provided key doesn't match with the specified algorithm.
     */
    public MetaDataSignature(
            Deliverable deliverable, PrivateKey privateKey,
            String signatureAlgorithm,
            CertPath certificationPath)
            throws NoSuchAlgorithmException, InvalidKeyException {
        this.certificationPath = certificationPath;
        this.signatureAlgorithm = signatureAlgorithm;

        //Generating the signature
        Signature signatureProvider = null;
        try {
            signatureProvider = Signature.getInstance(signatureAlgorithm, "BC");
            signatureProvider.initSign(privateKey);
            signatureProvider.update(Support.objectToBytes(deliverable));
            signature = signatureProvider.sign();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(MetaDataSignature.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(MetaDataSignature.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] getSignature() {
        return signature;
    }

    public CertPath getCertPath() {
        return certificationPath;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getType() {
        return "Signature";
    }
}
