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

package eu.secse.reds;

import java.io.Serializable;
import java.util.logging.Logger;

import polimi.reds.Filter;
import polimi.reds.Message;
import polimi.reds.NodeDescriptor;
import polimi.reds.Reply;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.routing.ReplyManager;
import polimi.reds.broker.routing.ReplyTable;
import polimi.reds.broker.routing.Router;
import polimi.reds.broker.routing.RoutingStrategy;
import polimi.reds.broker.routing.SubscriptionTable;

public class LoggingRouter implements Router {

	private Router base;
	private Logger logger;

	public LoggingRouter(Logger logger, Router base) {
		this.logger = logger;
		this.base = base;
	}

	public void forwardReply(Reply reply) {
		logger.fine("Router.forwardReply " + reply);
		base.forwardReply(reply);
	}

	public int getDebugLevel() {
		return base.getDebugLevel();
	}

	public NodeDescriptor getID() {
		return base.getID();
	}

	public Overlay getOverlay() {
		return base.getOverlay();
	}

	public ReplyManager getReplyManager() {
		return base.getReplyManager();
	}

	public ReplyTable getReplyTable() {
		return base.getReplyTable();
	}

	public SubscriptionTable getSubscriptionTable() {
		return base.getSubscriptionTable();
	}

	public void publish(NodeDescriptor neighborID, Message message) {
		logger.fine("Router.publish (from " + neighborID + "): " + message);
		base.publish(neighborID, message);
	}

	public void setDebugLevel(int debugLevel) {
		base.setDebugLevel(debugLevel);
	}

	public void setOverlay(Overlay o) {
		base.setOverlay(o);
	}

	public void setReplyManager(ReplyManager replyManager) {
		base.setReplyManager(replyManager);
	}

	public void setReplyTable(ReplyTable replyTable) {
		base.setReplyTable(replyTable);
	}

	public void setRoutingStrategy(RoutingStrategy routingStrategy) {
		base.setRoutingStrategy(routingStrategy);
	}

	public void setSubscriptionTable(SubscriptionTable subscriptionTable) {
		base.setSubscriptionTable(subscriptionTable);
	}

	public void signalPacket(String subject, NodeDescriptor senderID, Serializable payload) {
		base.signalPacket(subject, senderID, payload);
	}

	public void subscribe(NodeDescriptor neighborID, Filter filter) {
		logger.fine("Router.subscribe (from: " + neighborID + "): " + filter);
		base.subscribe(neighborID, filter);
	}

	public void unsubscribe(NodeDescriptor neighborID, Filter filter) {
		logger.fine("Router.unsubscribe (from: " + neighborID + "): " + filter);
		base.unsubscribe(neighborID, filter);
	}

	public void unsubscribeAll(NodeDescriptor neighborID) {
		logger.fine("Router.unsubscribeAll (from: " + neighborID + ")");
		base.unsubscribeAll(neighborID);
	}
}
