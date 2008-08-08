package dire.registry;

import java.io.Serializable;
import java.security.cert.CertPath;

public class Signature implements Serializable {

	private static final long serialVersionUID = -3957006900326873074L;

	private byte[] signature;
	private CertPath certificationPath;
	private String secureAlgorithm;
	
	Signature() { }

	public Signature(CertPath certificationPath, String secureAlgorithm, byte[] signature) {
		this.certificationPath = certificationPath;
		this.secureAlgorithm = secureAlgorithm;
		this.signature = signature;
	}

	public byte[] getSignature() {
		return signature;
	}

	public CertPath getCertificationPath() {
		return certificationPath;
	}

	public String getSecureAlgorithm() {
		return secureAlgorithm;
	}
}
