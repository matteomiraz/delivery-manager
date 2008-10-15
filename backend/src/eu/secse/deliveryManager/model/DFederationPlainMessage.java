/* This file is part of Delivery Manager.
 * (c) 2007 Matteo Miraz et al., Politecnico di Milano
 *
 * Delivery Manager is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * Delivery Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Delivery Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.secse.deliveryManager.model;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DFederationPlainMessage extends DFederation {

    private static final long serialVersionUID = 2527411809270793372L;
    protected Deliverable object;
    private Collection<MetaData> metadata;
    
    public DFederationPlainMessage(String federationId, Deliverable elem,
            Collection<MetaData> metadata) {
        super(federationId);
        this.object = elem;
        this.metadata = metadata;
    }
    
    public DFederationPlainMessage(String federationId, Deliverable elem){
        super(federationId);
        this.object = elem;
        metadata = new Vector<MetaData>();
    }

    public DFederationPlainMessage(String federationId, Deliverable elem, MetaData metadata) {
        this(federationId,elem);
        this.metadata.add(metadata);
    }
    
    public DFederationEncryptedMessage encrypt(Key federationKey,long keyVersion){
        DFederationEncryptedMessage message = null;
        try {
            message = new DFederationEncryptedMessage(federationId, object, metadata, federationKey, keyVersion);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DFederationPlainMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DFederationPlainMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(DFederationPlainMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DFederationPlainMessage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DFederationPlainMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

    public Deliverable getObject() {
        return this.object;
    }

    public Collection<MetaData> getMetadata() {
        return metadata;
    }

    public String getType() {
        return "FED-" + object.getType() + "@" + federationId;
    }

    @Override
    public String toString() {
        return "federation:" + this.federationId + "; obj:" + this.object;
    }
}
