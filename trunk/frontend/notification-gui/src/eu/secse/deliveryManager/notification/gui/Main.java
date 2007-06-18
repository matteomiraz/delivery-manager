/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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

package eu.secse.deliveryManager.notification.gui;

import it.s2.registry.Credentials;
import it.s2.registry.FacetSpecificationXML;
import it.s2.registry.SeCSERegistry;
import it.s2.registry.SeCSERegistryServiceLocator;
import it.s2.registry.Service;
import it.s2.registry.ServiceProviderDescription;

import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import eu.secse.deliveryManager.notify.jaws.Interest;
import eu.secse.deliveryManager.notify.jaws.LoginFailedException;
import eu.secse.deliveryManager.notify.jaws.NotFoundException;
import eu.secse.deliveryManager.notify.jaws.NotificationBean;
import eu.secse.deliveryManager.notify.jaws.NotificationBeanServiceLocator;

public class Main {

	private NotificationBean notification;
	private SeCSERegistry registry;
	
	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="16,11"
	private TabFolder tabFolder = null;
	private Composite composite = null;
	private Composite composite1 = null;
	private Label label = null;
	private Text regEmail = null;
	private Label label1 = null;
	private Text regPwd = null;
	private Button button2 = null;
	private Label label2 = null;
	private Text regNewPwd = null;
	private Button button3 = null;
	private Composite composite2 = null;
	private Button button4 = null;
	private Button button1 = null;
	private Group group = null;
	private Label label4 = null;
	private Text email = null;
	private Label label5 = null;
	private Text password = null;
	private Button button = null;
	private Label label6 = null;
	private Group group1 = null;
	private Table table = null;
	private Group group2 = null;
	private Label label3 = null;
	private Text serviceId = null;
	private Label label7 = null;
	private Text serviceName = null;
	private Label label8 = null;
	private Text providerId = null;
	private Button notifySchemas = null;
	private Button notifyXmls = null;
	private Button notifyServices = null;
	private Button button5 = null;
	private Button allServices = null;
	private Button allNames = null;
	private Button allProviders = null;
	private Button button6 = null;
	private Label regStatus = null;
	private Label status = null;

	private Label label9 = null;

	private Button allBase = null;

	private Text baseId = null;

	private Label label10 = null;

	private Composite composite3 = null;

	private Label label11 = null;

	private Text webServiceUrl = null;

	private Button button7 = null;

	private Label label12 = null;

	private Text regWebServiceUrl = null;

	private Button button8 = null;

	private Composite composite4 = null;

	private Composite composite5 = null;

	private Label label13 = null;

	private Text registryWebService = null;

	private Group group3 = null;

	private Group group4 = null;

	private Button button9 = null;

	private Label label14 = null;

	private Text newServiceName = null;

	private Label label15 = null;

	private Text provServId = null;

	private Text provServProvider = null;

	private Label label16 = null;

	private Button button10 = null;

	private Button button11 = null;
	private Label label17 = null;
	private Label label18 = null;
	private Text regLogin = null;
	private Text regPassword = null;

	public Main() throws ServiceException {
		try {
			notification = new NotificationBeanServiceLocator().getNotificationBeanPort(new URL("http://localhost:8080/notification/NotificationBean"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}

		try {
			registry = new SeCSERegistryServiceLocator().getSeCSERegistry(new URL("http://localhost:8080/SeCSERegistry/services/SeCSERegistry"));
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	/**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(sShell, SWT.BOTTOM);
		createComposite();
		createComposite1();
		createComposite4();
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("registration");
		tabItem.setControl(composite);
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("subscription");
		tabItem1.setControl(composite1);
		TabItem tabItem9 = new TabItem(tabFolder, SWT.NONE);
		tabItem9.setText("registry");
		tabItem9.setControl(composite4);
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData81 = new GridData();
		gridData81.horizontalAlignment = GridData.END;
		gridData81.verticalAlignment = GridData.CENTER;
		GridData gridData71 = new GridData();
		gridData71.horizontalAlignment = GridData.END;
		gridData71.verticalAlignment = GridData.CENTER;
		GridData gridData61 = new GridData();
		gridData61.horizontalAlignment = GridData.END;
		gridData61.verticalAlignment = GridData.CENTER;
		GridData gridData51 = new GridData();
		gridData51.horizontalAlignment = GridData.END;
		gridData51.verticalAlignment = GridData.CENTER;
		GridData gridData41 = new GridData();
		gridData41.horizontalAlignment = GridData.CENTER;
		gridData41.verticalAlignment = GridData.CENTER;
		GridData gridData32 = new GridData();
		gridData32.horizontalAlignment = GridData.FILL;
		gridData32.grabExcessHorizontalSpace = true;
		gridData32.verticalAlignment = GridData.CENTER;
		GridData gridData31 = new GridData();
		gridData31.horizontalAlignment = GridData.FILL;
		gridData31.grabExcessHorizontalSpace = true;
		gridData31.verticalAlignment = GridData.CENTER;
		GridData gridData22 = new GridData();
		gridData22.horizontalAlignment = GridData.FILL;
		gridData22.grabExcessHorizontalSpace = true;
		gridData22.verticalAlignment = GridData.CENTER;
		GridData gridData110 = new GridData();
		gridData110.horizontalAlignment = GridData.FILL;
		gridData110.grabExcessHorizontalSpace = true;
		gridData110.verticalAlignment = GridData.CENTER;
		GridData gridData17 = new GridData();
		gridData17.horizontalAlignment = GridData.FILL;
		gridData17.horizontalSpan = 3;
		gridData17.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(gridLayout);
		label12 = new Label(composite, SWT.NONE);
		label12.setText("web service url: ");
		label12.setLayoutData(gridData51);
		regWebServiceUrl = new Text(composite, SWT.BORDER);
		regWebServiceUrl.setText("http://localhost:8080/notification/NotificationBean");
		regWebServiceUrl.setLayoutData(gridData32);
		button8 = new Button(composite, SWT.NONE);
		button8.setText("set");
		button8.setLayoutData(gridData41);
		button8.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				webServiceUrl.setText(regWebServiceUrl.getText());
				try {
					notification = new NotificationBeanServiceLocator().getNotificationBeanPort(new URL(regWebServiceUrl.getText()));
					regStatus.setText("Ok: " + regWebServiceUrl.getText());
				} catch (Exception e1) {
					regStatus.setText("ERROR: " + e1.getMessage());
				}
			}
		});
		label = new Label(composite, SWT.RIGHT);
		label.setText("email:");
		label.setLayoutData(gridData61);
		regEmail = new Text(composite, SWT.BORDER);
		regEmail.setLayoutData(gridData110);
		createComposite2();
		label1 = new Label(composite, SWT.RIGHT);
		label1.setText("password:");
		label1.setLayoutData(gridData71);
		regPwd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		regPwd.setLayoutData(gridData22);
		button2 = new Button(composite, SWT.NONE);
		button2.setText("unregister");
		button2.setLayoutData(gridData);
		button2.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					notification.unregisterNotificationUser(regEmail.getText(), regPwd.getText());
					regStatus.setText("User unregistered correctly.");
				} catch (RemoteException e1) {
					regStatus.setText("Error: " + e1.getMessage());
				}
			}
		});
		label2 = new Label(composite, SWT.RIGHT);
		label2.setText("new password:");
		label2.setLayoutData(gridData81);
		regNewPwd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		regNewPwd.setLayoutData(gridData31);
		button3 = new Button(composite, SWT.NONE);
		button3.setText("change password");
		button3.setLayoutData(gridData1);
		button3.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					notification.changePassword(regEmail.getText(), regPwd.getText(), regNewPwd.getText());
					regStatus.setText("Password changed.");
				} catch (RemoteException e1) {
					regStatus.setText("Error: " + e1.getMessage());
				}
			}
		});
		regStatus = new Label(composite, SWT.NONE);
		regStatus.setText("");
		regStatus.setLayoutData(gridData17);
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		composite1 = new Composite(tabFolder, SWT.NONE);
		composite1.setLayout(gridLayout2);
		createComposite3();
		createGroup();
		createGroup1();
		Label filler1 = new Label(composite1, SWT.NONE);
		createGroup2();
	}

	/**
	 * This method initializes composite2	
	 *
	 */
	private void createComposite2() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		composite2 = new Composite(composite, SWT.NONE);
		composite2.setLayout(gridLayout1);
		button4 = new Button(composite2, SWT.NONE);
		button4.setText("register");
		button4.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					notification.registerNotificationUser(regEmail.getText());
					regStatus.setText("User registered correctly... check your mail for the password!");
				} catch (RemoteException e1) {
					regStatus.setText("Error: " + e1.getMessage());
				}
			}
		});
		button1 = new Button(composite2, SWT.NONE);
		button1.setText("remind password");
		button1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					notification.resendPassword(regEmail.getText());
					regStatus.setText("Password sent... check your mail!");
				} catch (RemoteException e1) {
					regStatus.setText("Error: " + e1.getMessage());
				}
			}
		});
	}

	/**
	 * This method initializes group	
	 *
	 */
	private void createGroup() {
		GridData gridData18 = new GridData();
		gridData18.horizontalAlignment = GridData.FILL;
		gridData18.grabExcessHorizontalSpace = true;
		gridData18.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalSpan = 3;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 7;
		group = new Group(composite1, SWT.NONE);
		group.setText("autentication");
		group.setLayoutData(gridData4);
		group.setLayout(gridLayout3);
		label4 = new Label(group, SWT.NONE);
		label4.setText("email");
		email = new Text(group, SWT.BORDER);
		email.setText("name@domain.com");
		label6 = new Label(group, SWT.NONE);
		label6.setText("  ");
		label5 = new Label(group, SWT.NONE);
		label5.setText("password:");
		password = new Text(group, SWT.BORDER | SWT.PASSWORD);
		button = new Button(group, SWT.NONE);
		button.setText("set");
		status = new Label(group, SWT.NONE);
		status.setText("");
		status.setLayoutData(gridData18);
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if(updateTable())
					status.setText("Login Ok");
			}
		});
	}

	/**
	 * This method initializes group1	
	 *
	 */
	private void createGroup1() {
		GridData gridData16 = new GridData();
		gridData16.horizontalAlignment = GridData.CENTER;
		gridData16.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 2;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.grabExcessVerticalSpace = true;
		gridData5.verticalAlignment = GridData.FILL;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		group1 = new Group(composite1, SWT.NONE);
		group1.setLayoutData(gridData5);
		group1.setLayout(gridLayout5);
		group1.setText("current subscriptions");
		table = new Table(group1, SWT.NONE);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData2);
		table.setLinesVisible(true);
		Label filler = new Label(group1, SWT.NONE);
		button6 = new Button(group1, SWT.NONE);
		button6.setText("unsubscribe");
		button6.setLayoutData(gridData16);
		button6.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				// unsubscribe
				TableItem[] selection = table.getSelection();
				for (TableItem item : selection) {
					try {
						int n = (Integer) item.getData();
						notification.unregisterNotification(email.getText(), password.getText(), n);
						status.setText("Unsubscribed successfully");
					} catch (LoginFailedException e1) {
						status.setText("Error: " + e1.getMessage());
					} catch (NotFoundException e1) {
						status.setText("Error: " + e1.getMessage());
					} catch (RemoteException e1) {
						status.setText("Error: " + e1.getMessage());
					}
				}
				
				updateTable();
			}
		});
		TableColumn id = new TableColumn(table, SWT.CENTER);
		id.setWidth(60);
		id.setText("id");
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(60);
		tableColumn.setText("baseId");
		TableColumn name = new TableColumn(table, SWT.CENTER);
		name.setWidth(60);
		name.setText("name");
		TableColumn provider = new TableColumn(table, SWT.CENTER);
		provider.setWidth(60);
		provider.setText("provider");
		TableColumn tableColumn62 = new TableColumn(table, SWT.CENTER);
		tableColumn62.setWidth(30);
		tableColumn62.setResizable(false);
		tableColumn62.setText("srv");
		TableColumn tableColumn63 = new TableColumn(table, SWT.CENTER);
		tableColumn63.setWidth(30);
		tableColumn63.setResizable(false);
		tableColumn63.setText("sch");
		TableColumn tableColumn64 = new TableColumn(table, SWT.CENTER);
		tableColumn64.setWidth(30);
		tableColumn64.setResizable(false);
		tableColumn64.setText("xml");
	}

	/**
	 * This method initializes group2	
	 *
	 */
	private void createGroup2() {
		GridData gridData21 = new GridData();
		gridData21.verticalSpan = 3;
		gridData21.verticalAlignment = GridData.CENTER;
		gridData21.horizontalAlignment = GridData.END;
		GridData gridData20 = new GridData();
		gridData20.horizontalAlignment = GridData.FILL;
		gridData20.verticalAlignment = GridData.CENTER;
		GridData gridData19 = new GridData();
		gridData19.horizontalAlignment = GridData.END;
		gridData19.verticalAlignment = GridData.CENTER;
		GridData gridData15 = new GridData();
		gridData15.horizontalAlignment = GridData.FILL;
		gridData15.verticalAlignment = GridData.CENTER;
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.FILL;
		gridData14.verticalAlignment = GridData.CENTER;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.FILL;
		gridData13.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.END;
		gridData12.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.END;
		gridData11.verticalAlignment = GridData.CENTER;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.END;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		GridData gridData8 = new GridData();
		GridData gridData7 = new GridData();
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.CENTER;
		gridData6.horizontalSpan = 4;
		gridData6.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 4;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = false;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.CENTER;
		group2 = new Group(composite1, SWT.NONE);
		group2.setLayoutData(gridData3);
		group2.setLayout(gridLayout4);
		group2.setText("new subscription");
		label3 = new Label(group2, SWT.NONE);
		label3.setText("service ID:");
		label3.setLayoutData(gridData12);
		Label filler7 = new Label(group2, SWT.NONE);
		serviceId = new Text(group2, SWT.BORDER);
		serviceId.setLayoutData(gridData13);
		allServices = new Button(group2, SWT.CHECK);
		allServices.setText("all");
		label9 = new Label(group2, SWT.NONE);
		label9.setText("base ID:");
		label9.setLayoutData(gridData19);
		Label filler5 = new Label(group2, SWT.NONE);
		baseId = new Text(group2, SWT.BORDER);
		baseId.setLayoutData(gridData20);
		allBase = new Button(group2, SWT.CHECK);
		allBase.setText("all");
		allBase.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				baseId.setEnabled(!allBase.getSelection());
			}
		});
		allServices.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				serviceId.setEnabled(!allServices.getSelection());
			}
		});
		label7 = new Label(group2, SWT.NONE);
		label7.setText("service name:");
		label7.setLayoutData(gridData11);
		Label filler13 = new Label(group2, SWT.NONE);
		serviceName = new Text(group2, SWT.BORDER);
		serviceName.setLayoutData(gridData14);
		allNames = new Button(group2, SWT.CHECK);
		allNames.setText("all");
		allNames.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				serviceName.setEnabled(!allNames.getSelection());
			}
		});
		label8 = new Label(group2, SWT.NONE);
		label8.setText("provider:");
		label8.setLayoutData(gridData10);
		Label filler14 = new Label(group2, SWT.NONE);
		providerId = new Text(group2, SWT.BORDER);
		providerId.setLayoutData(gridData15);
		allProviders = new Button(group2, SWT.CHECK);
		allProviders.setText("all");
		allProviders.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				providerId.setEnabled(!allProviders.getSelection());
			}
		});
		label10 = new Label(group2, SWT.NONE);
		label10.setText("notify:");
		label10.setLayoutData(gridData21);
		Label filler49 = new Label(group2, SWT.NONE);
		notifyServices = new Button(group2, SWT.CHECK);
		notifyServices.setText("service");
		notifyServices.setLayoutData(gridData7);
		Label filler44 = new Label(group2, SWT.NONE);
		Label filler55 = new Label(group2, SWT.NONE);
		notifySchemas = new Button(group2, SWT.CHECK);
		notifySchemas.setText("facet schema");
		notifySchemas.setLayoutData(gridData8);
		Label filler50 = new Label(group2, SWT.NONE);
		Label filler334 = new Label(group2, SWT.NONE);
		notifyXmls = new Button(group2, SWT.CHECK);
		notifyXmls.setText("facet xml");
		notifyXmls.setLayoutData(gridData9);
		Label filler42 = new Label(group2, SWT.NONE);
		button5 = new Button(group2, SWT.NONE);
		button5.setText("subscribe");
		button5.setLayoutData(gridData6);
		button5.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					String providerName = allProviders.getSelection()?null:providerId.getText();
					String serviceNameRegex = allNames.getSelection()?null:serviceName.getText();
					String srvId = allServices.getSelection()?null:serviceId.getText();
					String srvBaseId = allBase.getSelection()?null:baseId.getText();

					notification.registerNotification(email.getText(), password.getText(), providerName, srvId , srvBaseId, serviceNameRegex, notifyServices.getSelection(), notifySchemas.getSelection(), notifyXmls.getSelection());
					updateTable();
					status.setText("Subscribed correcly");
				} catch (LoginFailedException e1) {
					status.setText("Login failed: " + e1.getMessage());
				} catch (RemoteException e1) {
					status.setText("Error: " + e1.getMessage());
				}
			}
		});
	}

	/**
	 * This method initializes composite3	
	 *
	 */
	private void createComposite3() {
		GridData gridData24 = new GridData();
		gridData24.horizontalAlignment = GridData.FILL;
		gridData24.grabExcessHorizontalSpace = true;
		gridData24.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 3;
		GridData gridData23 = new GridData();
		gridData23.horizontalSpan = 3;
		gridData23.horizontalAlignment = GridData.FILL;
		gridData23.verticalAlignment = GridData.CENTER;
		gridData23.grabExcessHorizontalSpace = true;
		composite3 = new Composite(composite1, SWT.NONE);
		composite3.setLayoutData(gridData23);
		composite3.setLayout(gridLayout6);
		label11 = new Label(composite3, SWT.NONE);
		label11.setText("web service url:");
		webServiceUrl = new Text(composite3, SWT.BORDER);
		webServiceUrl.setText("http://localhost:8080/notification/NotificationBean");
		webServiceUrl.setLayoutData(gridData24);
		button7 = new Button(composite3, SWT.NONE);
		button7.setText("set");
		button7.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				regWebServiceUrl.setText(webServiceUrl.getText());
				try {
					notification = new NotificationBeanServiceLocator().getNotificationBeanPort(new URL(webServiceUrl.getText()));
					status.setText("Ok: " + webServiceUrl.getText());
				} catch (Exception e1) {
					status.setText("ERROR: " + e1.getMessage());
				}
			}
		});
	}

	/**
	 * This method initializes composite4	
	 *
	 */
	private void createComposite4() {
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.numColumns = 3;
		composite4 = new Composite(tabFolder, SWT.NONE);
		createComposite5();
		composite4.setLayout(gridLayout8);
		createGroup3();
		createGroup4();
	}

	/**
	 * This method initializes composite5	
	 *
	 */
	private void createComposite5() {
		GridData gridData40 = new GridData();
		gridData40.horizontalAlignment = GridData.FILL;
		gridData40.horizontalSpan = 3;
		gridData40.verticalAlignment = GridData.CENTER;
		GridData gridData39 = new GridData();
		gridData39.horizontalAlignment = GridData.END;
		gridData39.verticalAlignment = GridData.CENTER;
		GridData gridData38 = new GridData();
		gridData38.horizontalAlignment = GridData.END;
		gridData38.verticalAlignment = GridData.CENTER;
		GridData gridData37 = new GridData();
		gridData37.horizontalAlignment = GridData.END;
		gridData37.verticalAlignment = GridData.CENTER;
		GridData gridData36 = new GridData();
		gridData36.verticalSpan = 3;
		GridData gridData35 = new GridData();
		gridData35.horizontalAlignment = GridData.FILL;
		gridData35.verticalAlignment = GridData.CENTER;
		GridData gridData34 = new GridData();
		gridData34.horizontalAlignment = GridData.FILL;
		gridData34.verticalAlignment = GridData.CENTER;
		GridData gridData26 = new GridData();
		gridData26.grabExcessHorizontalSpace = true;
		gridData26.verticalAlignment = GridData.CENTER;
		gridData26.horizontalAlignment = GridData.FILL;
		GridData gridData25 = new GridData();
		gridData25.horizontalSpan = 3;
		gridData25.horizontalAlignment = GridData.FILL;
		gridData25.verticalAlignment = GridData.CENTER;
		gridData25.grabExcessHorizontalSpace = true;
		GridLayout gridLayout7 = new GridLayout();
		gridLayout7.numColumns = 3;
		composite5 = new Composite(composite4, SWT.NONE);
		composite5.setLayout(gridLayout7);
		composite5.setLayoutData(gridData25);
		label13 = new Label(composite5, SWT.NONE);
		label13.setText("registry web service:");
		label13.setLayoutData(gridData39);
		registryWebService = new Text(composite5, SWT.BORDER);
		registryWebService.setLayoutData(gridData26);
		registryWebService.setText("http://localhost:8080/SeCSERegistry/services/SeCSERegistry");
		button9 = new Button(composite5, SWT.NONE);
		button9.setText("set");
		button9.setLayoutData(gridData36);
		button9.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					registry = new SeCSERegistryServiceLocator().getSeCSERegistry(new URL(registryWebService.getText()));
					try {
						long id = registry.authenticate(new Credentials(regPassword.getText(), regLogin.getText()));
						registryStatus.setText("Ok: your id is " + id);
					} catch (Throwable e1) {
						long id = registry.registerServiceProvider(new ServiceProviderDescription("Delivery Manager notification user", 0), new Credentials(regPassword.getText(), regLogin.getText()));
						registryStatus.setText("Ok: registered id " + id);
					}
				} catch (Exception e1) {
					registryStatus.setText("ERROR: " + e1.getMessage());
				}
			}
		});
		label17 = new Label(composite5, SWT.NONE);
		label17.setText("registry login: ");
		label17.setLayoutData(gridData37);
		regLogin = new Text(composite5, SWT.BORDER);
		regLogin.setLayoutData(gridData35);
		label18 = new Label(composite5, SWT.NONE);
		label18.setText("registry password: ");
		label18.setLayoutData(gridData38);
		regPassword = new Text(composite5, SWT.BORDER);
		regPassword.setLayoutData(gridData34);
		registryStatus = new Label(composite5, SWT.NONE);
		registryStatus.setText("");
		registryStatus.setLayoutData(gridData40);
	}

	/**
	 * This method initializes group3	
	 *
	 */
	private void createGroup3() {
		GridData gridData45 = new GridData();
		gridData45.horizontalSpan = 3;
		gridData45.verticalAlignment = GridData.CENTER;
		gridData45.horizontalAlignment = GridData.CENTER;
		GridData gridData44 = new GridData();
		gridData44.horizontalAlignment = GridData.END;
		gridData44.verticalAlignment = GridData.CENTER;
		GridData gridData43 = new GridData();
		gridData43.horizontalAlignment = GridData.END;
		gridData43.verticalAlignment = GridData.CENTER;
		GridData gridData42 = new GridData();
		gridData42.horizontalAlignment = GridData.END;
		gridData42.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout9 = new GridLayout();
		gridLayout9.numColumns = 3;
		GridData gridData28 = new GridData();
		gridData28.horizontalAlignment = GridData.FILL;
		gridData28.verticalAlignment = GridData.CENTER;
		group3 = new Group(composite4, SWT.NONE);
		group3.setLayoutData(gridData28);
		group3.setLayout(gridLayout9);
		group3.setText("add new service");
		label14 = new Label(group3, SWT.NONE);
		label14.setText("name: ");
		label14.setLayoutData(gridData44);
		Label filler413 = new Label(group3, SWT.NONE);
		newServiceName = new Text(group3, SWT.BORDER);
		label19 = new Label(group3, SWT.NONE);
		label19.setText("version:");
		label19.setLayoutData(gridData43);
		Label filler2 = new Label(group3, SWT.NONE);
		newServiceVersion = new Text(group3, SWT.BORDER);
		label20 = new Label(group3, SWT.NONE);
		label20.setText("previous id: ");
		label20.setLayoutData(gridData42);
		Label filler412 = new Label(group3, SWT.NONE);
		newServicePrevious = new Text(group3, SWT.BORDER);
		button11 = new Button(group3, SWT.NONE);
		button11.setText("add");
		button11.setLayoutData(gridData45);
		button11.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {   
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {    
				try {
					Service sd = new Service(true, 0, newServicePrevious.getText(), newServiceName.getText(), null, null, newServiceVersion.getText());
					String id = registry.deployService(sd , new Credentials(regPassword.getText(), regLogin.getText()), false);
					registryStatus.setText("Created service: " + id);
				} catch (Throwable e1) {
					registryStatus.setText("ERROR: " + e1.getMessage());
				}
			}
		
		});
	}

	/**
	 * This method initializes group4	
	 *
	 */
	private void createGroup4() {
		GridData gridData33 = new GridData();
		gridData33.horizontalAlignment = GridData.END;
		gridData33.verticalAlignment = GridData.CENTER;
		GridData gridData30 = new GridData();
		gridData30.horizontalAlignment = GridData.END;
		gridData30.verticalAlignment = GridData.CENTER;
		GridData gridData29 = new GridData();
		gridData29.horizontalSpan = 3;
		gridData29.verticalAlignment = GridData.CENTER;
		gridData29.horizontalAlignment = GridData.CENTER;
		GridLayout gridLayout10 = new GridLayout();
		gridLayout10.numColumns = 3;
		GridData gridData27 = new GridData();
		gridData27.horizontalAlignment = GridData.FILL;
		gridData27.verticalAlignment = GridData.CENTER;
		group4 = new Group(composite4, SWT.NONE);
		group4.setLayoutData(gridData27);
		group4.setLayout(gridLayout10);
		group4.setText("add provider facet");
		label15 = new Label(group4, SWT.NONE);
		label15.setText("service Id: ");
		label15.setLayoutData(gridData30);
		Label filler3 = new Label(group4, SWT.NONE);
		provServId = new Text(group4, SWT.BORDER);
		label16 = new Label(group4, SWT.NONE);
		label16.setText("provider:");
		label16.setLayoutData(gridData33);
		Label filler8 = new Label(group4, SWT.NONE);
		provServProvider = new Text(group4, SWT.BORDER);
		button10 = new Button(group4, SWT.NONE);
		button10.setText("add");
		button10.setLayoutData(gridData29);
		button10.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				try {
					FacetSpecificationXML commerceXml = new FacetSpecificationXML(-1, "Commerce facet", DOCUMENT1 + provServProvider.getText() + DOCUMENT2, provServId.getText(), "Commerce", null, null);
					String commId = registry.addWellKnownFacetSpecificationXML(provServId.getText(), "Commerce", commerceXml, new Credentials(regPassword.getText(), regLogin.getText()), false, false, null);
					status.setText("Added commerce facet specification XML with id " + commId);
				} catch (Throwable e1) {
					status.setText("Error: " + e1.getMessage());
				}
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		try {
			Display display = Display.getDefault();
			Main thisClass = new Main();
			thisClass.createSShell();
			thisClass.sShell.open();

			while (!thisClass.sShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (ServiceException e) {
		}
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Delivery Manager ~ Notification Client");
		sShell.setSize(new Point(686, 455));
		sShell.setLayout(new FillLayout());
		createTabFolder();
	}

	private boolean updateTable() {
		try {
			table.removeAll();

			Interest[] ints = notification.getAllInterests(email.getText(), password.getText());

			if(ints != null) {
				for (Interest i : ints) {
					TableItem ti = new TableItem(table, SWT.NULL);
					ti.setData(i.getId());
					ti.setText("subscription " + i.getId());
					ti.setText(0, i.getServiceId()==null?"**all**":i.getServiceId());
					ti.setText(1, i.getBaseServiceId()==null?"**all**":i.getBaseServiceId());
					ti.setText(2, i.getServiceNameRegex()==null?"**all**":i.getServiceNameRegex());
					ti.setText(3, i.getProviderName()==null?"**all**":i.getProviderName());
					ti.setText(4, i.isNotifyService()?"yes":"no");
					ti.setText(5, i.isNotifyFacetSpecificationSchema()?"yes":"no");
					ti.setText(6, i.isNotifyFacetSpecificationXml()?"yes":"no");
				}
			}
			return true;
		} catch (RemoteException e1) {
			status.setText("Error: " + e1.getMessage());
			e1.printStackTrace();
			return false;
		}
	}


	private static final String DOCUMENT1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
	"<LanguageSpecificSpecification xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Commerce.xsd\">" +
	"<FacetType>Commerce</FacetType>" +
	"  <ReferencedOntology>ReferencedOntology</ReferencedOntology>"+
	"  <ReferencedSIM>ReferencedSIM</ReferencedSIM>" +
	"  <FacetSpecificationLanguage>FacetSpecificationLanguage</FacetSpecificationLanguage>" +
	"  <FacetSpecificationOwner>FacetSpecificationOwner</FacetSpecificationOwner>"+
	"  <FacetSpecificationLastEdited>FacetSpecificationLastEdited</FacetSpecificationLastEdited>"+
	"  <FacetSpecificationData>" +
	"    <CommerceSpec>"+
	"      <BusinessEntity>"+
	"       <BusinessName>";
	private static final String DOCUMENT2 = "</BusinessName>"+
	"        <BusinessDescription>BusinessDescription</BusinessDescription>" +
	"        <BusinessURLS>"+
	"          <URL>URL</URL>"+
	"        </BusinessURLS>"+
	"        <contacts>"+
	"          <contact>"+
	"            <personName>personName</personName>"+
	"            <description>description</description>"+
	"            <phone>phone</phone>"+
	"            <email>email</email>"+
	"            <address>address</address>"+
	"          </contact>"+
	"        </contacts>"+
	"      </BusinessEntity>"+
	"      <Costings>"+
	"        <Cost>"+
	"          <CostName>CostName</CostName>"+
	"          <CostDescription>CostDescription</CostDescription>"+
	"          <CostType>CostType</CostType>"+
	"          <AssociatedOperation>AssociatedOperation</AssociatedOperation>"+
	"          <CostMetric>CostMetric</CostMetric>"+
	"          <CostValue>CostValue</CostValue>"+
	"        </Cost>"+
	"        <PaymentApproach>PaymentApproach</PaymentApproach>"+
	"      </Costings>"+
	"		<SLAs>" +
	"        <SLA>SLA</SLA>" +
	"      </SLAs>" +
	"      <ServicePolicies>" +
	"        <ServicePolicy>ServicePolicy</ServicePolicy>" +
	"      </ServicePolicies>" +
	"      <Miscellaneous>Miscellaneous</Miscellaneous>" +
	"    </CommerceSpec>" +
	"  </FacetSpecificationData>" +
	"</LanguageSpecificSpecification>";
	private Label registryStatus = null;
	private Label label19 = null;
	private Label label20 = null;
	private Text newServicePrevious = null;
	private Text newServiceVersion = null;

}
