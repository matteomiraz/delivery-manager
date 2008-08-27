/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.model;

/**
 *
 * @author Mario
 */
public abstract class DFederation implements Deliverable {

    public String federationId;

    public DFederation(String federationId) {
        this.federationId = federationId;
    }

    public String getFederationId() {
        return this.federationId;
    }
}
