package Certification;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;



public class User {
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.1C174812-A462-FF79-135E-EC8071FDB4A7]
    // </editor-fold> 

    private KeyPair keys;
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.FAE71B42-B8D7-478B-636A-55F0E8E9A9BF]
    // </editor-fold> 
    private String name;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.366D45EB-110D-79F1-9596-C62E1DCFBA40]
    // </editor-fold> 
    public User(String name, String cypherAlgorithm) {
        this.name = name;

        //Generating a pair of random keys for cyphering
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(cypherAlgorithm);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Setting keys lenght and random seed
        keyPairGenerator.initialize(1024, new SecureRandom());
        keys = keyPairGenerator.generateKeyPair();
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.737AA1C4-8F2D-7335-41A3-791EE4DD056F]
    // </editor-fold> 
    public KeyPair getKeys() {
        return keys;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.35BD0A98-C1E8-DA2C-9923-46CCBC079438]
    // </editor-fold> 
    public String getName() {
        return name;
    }
}

