/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Certification;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

/**
 *
 * @author Mario
 */
public class CertificationAuthority {

    private String name;
    private KeyPair CAkeys;
    private X509Certificate CAcertificate;
    private X509V2CRLGenerator crlGenerator;
    private BigInteger crlNumber;
    private String urlOfCRLDistPoint;

    public CertificationAuthority(String name, String urlOfCRLDistPoint) {
        this.name = name;
        this.urlOfCRLDistPoint = urlOfCRLDistPoint;
        //Generating a pair of random keys for the CA
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Setting keys lenght and random seed
        keyPairGenerator.initialize(1024, new SecureRandom());
        CAkeys = keyPairGenerator.generateKeyPair();

        //Generating a self-signed certificate
        //Setting up the generator
        X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
        generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        generator.setIssuerDN(new X500Principal("CN=" + name));
        Calendar date = Calendar.getInstance();
        generator.setNotBefore(date.getTime());
        date.add(Calendar.DAY_OF_YEAR, 365);
        generator.setNotAfter(date.getTime());
        generator.setSubjectDN(new X500Principal("CN=" + name));
        generator.setPublicKey(CAkeys.getPublic());
        generator.setSignatureAlgorithm("SHA1withRSA");
        //Adding extensions
        try {
            generator.addExtension(X509Extensions.BasicConstraints, false, new BasicConstraints(true));//Posso fare fino 0 passi per trovare la rootCA

            generator.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(CAkeys.getPublic()));
        } catch (CertificateParsingException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Generating the certificate of the CA
        CAcertificate = null;
        try {
            CAcertificate = generator.generate(CAkeys.getPrivate(), "BC");
        } catch (CertificateEncodingException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Set-up of CRL generator
        crlNumber = BigInteger.ZERO;
        crlGenerator = new X509V2CRLGenerator();
        crlGenerator.setIssuerDN(new X500Principal("CN=" + name));
        crlGenerator.setSignatureAlgorithm("SHA1withRSA");
    }

    public X509Certificate generateCertificate(String distinguishedName, PublicKey key, int validityDays, boolean intermediate) {
        //Setting up the generator
        X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
        generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        generator.setIssuerDN(new X500Principal("CN=" + name));
        Calendar date = Calendar.getInstance();
        generator.setNotBefore(date.getTime());
        date.add(Calendar.DAY_OF_YEAR, validityDays);
        generator.setNotAfter(date.getTime());
        generator.setSubjectDN(new X500Principal("CN=" + distinguishedName));
        generator.setPublicKey(key);
        generator.setSignatureAlgorithm("SHA1withRSA");
        
        try{
        //Adding extensions
        if (intermediate) {
            generator.addExtension(X509Extensions.BasicConstraints, false, new BasicConstraints(true));

        }
        generator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(CAcertificate));
        generator.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(key));
        } catch (CertificateParsingException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //CRL Dist Point
        GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String(urlOfCRLDistPoint));
        GeneralNames gns = new GeneralNames(new DERSequence(gn));
        DistributionPointName dpn = new DistributionPointName(0, gns);
        DistributionPoint distp = new DistributionPoint(dpn, null, null);
        generator.addExtension(X509Extensions.CRLDistributionPoints, false, new DERSequence(distp));

        //Generating the certificate
        X509Certificate certificate = null;
        try {
            certificate = generator.generate(CAkeys.getPrivate(), "BC");
        } catch (CertificateEncodingException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
        return certificate;
    }

    public PublicKey getPublicKey() {
        return CAkeys.getPublic();
    }

    public String getName() {
        return name;
    }

    public X509Certificate getCertificate() {
        return CAcertificate;
    }

    public void revokeCetrificate(BigInteger serial) {
        try {
            crlGenerator.addCRLEntry(serial, Calendar.getInstance().getTime(), CRLReason.privilegeWithdrawn);
            crlGenerator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(getCertificate()));
            crlGenerator.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(crlNumber));
        } catch (CertificateParsingException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public X509CRL generateCRL() {
        //Finishing to set up the generator
        Calendar date = Calendar.getInstance();
        crlGenerator.setThisUpdate(date.getTime());
        date.add(Calendar.DAY_OF_YEAR, 5);
        crlGenerator.setNextUpdate(date.getTime());
        //Generating CRL         
        X509CRL crl = null;
        try {
            crl = crlGenerator.generate(CAkeys.getPrivate());
        } catch (CRLException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(CertificationAuthority.class.getName()).log(Level.SEVERE, null, ex);
        }
        crlNumber.add(BigInteger.ONE);
        return crl;
    }
}
