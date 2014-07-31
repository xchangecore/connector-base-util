package com.saic.uicds.clients.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.uicds.notificationService.GetMessagesResponseDocument;

import com.saic.precis.x2009.x06.base.IdentificationType;


public class CommonTest {
	
	static final String GET_MSGS_RESPONSE_1 = "src/test/resources/messages/GetMessagesResponse-1.xml";

	@Test
	public void testGetIdentityList() {
		File file = new File(GET_MSGS_RESPONSE_1);
		assertTrue(file.exists());
		try {
			GetMessagesResponseDocument response = GetMessagesResponseDocument.Factory.parse(file);
			List<IdentificationType> list = Common.getWorkProductIdentificationList(response);
			assertEquals(3,list.size());
		} catch (XmlException e) {
			fail("XML error paring "+GET_MSGS_RESPONSE_1+" "+e.getMessage());
		} catch (IOException e) {
			fail("IO error paring "+GET_MSGS_RESPONSE_1+" "+e.getMessage());
		}
	}
}
