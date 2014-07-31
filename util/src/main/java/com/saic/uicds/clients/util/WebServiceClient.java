package com.saic.uicds.clients.util;

import org.apache.xmlbeans.XmlObject;

public interface WebServiceClient {

	public String getURI();
	
	public void setURI(String URI);
	
	public XmlObject sendRequest(XmlObject request);
}
