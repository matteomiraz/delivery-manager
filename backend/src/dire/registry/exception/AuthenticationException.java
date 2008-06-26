package dire.registry.exception;

import java.rmi.RemoteException;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=false)
public class AuthenticationException extends RemoteException {

	private static final long serialVersionUID = -5185482755928989867L;

}
