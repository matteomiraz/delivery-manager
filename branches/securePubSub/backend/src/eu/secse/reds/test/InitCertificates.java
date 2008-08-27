/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.reds.test;

import Certification.CertificationAuthority;
import Certification.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Mario
 */


public class InitCertificates {
    private static void toFile(String path, byte[] encoded) {
        try {
            FileOutputStream fos = null;
            fos = new FileOutputStream(path);
            fos.write(encoded);
        }catch (IOException ex) {
            Logger.getLogger(InitCertificates.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        try {
            //Chiavi utenti
            User alice = new User("Alice", "RSA");
            CertificationAuthority valery = new CertificationAuthority("Valery", "http://localhost/certificates/valery.crl");
            X509Certificate aliceCert = valery.generateCertificate(alice.getName(), alice.getKeys().getPublic(), 10, false);
            
            //Chiave di federazione
            KeyGenerator kg = null;
            try {
                kg = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(InitCertificates.class.getName()).log(Level.SEVERE, null, ex);
            }
            SecretKey federationKey = kg.generateKey();
            
            //Salvataggio su file
            //Certificato
            toFile("aliceCert.cer", aliceCert.getEncoded());
            //Chiave privata
            toFile("alicePrivateKey.key", alice.getKeys().getPrivate().getEncoded());
            //Certificato della CA
            toFile("valeryCert.cer", valery.getCertificate().getEncoded());
            //Chiave di federazione
            toFile("federationkey.key", federationKey.getEncoded());
            //CRL
            toFile("C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\certificates\\valery.crl",
                    valery.generateCRL().getEncoded());
        } catch (CRLException ex) {
            Logger.getLogger(InitCertificates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateEncodingException ex) {
            Logger.getLogger(InitCertificates.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
