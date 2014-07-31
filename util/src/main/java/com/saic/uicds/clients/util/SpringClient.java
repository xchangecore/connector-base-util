package com.saic.uicds.clients.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;
import org.uicds.incident.IncidentDocument;
import org.uicds.incident.UICDSIncidentType;
import org.uicds.notificationService.GetMessagesRequestDocument;
import org.uicds.notificationService.GetMessagesResponseDocument;
import org.uicds.resourceInstanceService.GetResourceInstanceListRequestDocument;
import org.uicds.resourceInstanceService.GetResourceInstanceListResponseDocument;
import org.uicds.resourceInstanceService.GetResourceInstanceRequestDocument;
import org.uicds.resourceInstanceService.GetResourceInstanceResponseDocument;
import org.uicds.resourceInstanceService.RegisterRequestDocument;
import org.uicds.resourceInstanceService.RegisterResponseDocument;
import org.uicds.resourceInstanceService.ResourceInstance;
import org.uicds.resourceInstanceService.GetResourceInstanceListResponseDocument.GetResourceInstanceListResponse;
import org.uicds.resourceInstanceService.ResourceInstance.Endpoints;
import org.uicds.resourceProfileService.GetProfileRequestDocument;
import org.uicds.resourceProfileService.GetProfileResponseDocument;
import org.uicds.resourceProfileService.ResourceProfile;
import org.uicds.workProductService.GetProductRequestDocument;
import org.uicds.workProductService.GetProductResponseDocument;
import org.w3.x2005.x08.addressing.ActionDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import x0.messageStructure1.StructuredPayloadType;

import com.saic.precis.x2009.x06.base.IdentificationType;
import com.saic.precis.x2009.x06.base.IdentifierType;
import com.saic.precis.x2009.x06.structures.WorkProductDocument.WorkProduct;

public class SpringClient implements WebServiceClient {

    private static final String INCIDENT_SERVICE_NS = "http://uicds.org/incident";
    private static final String INCIDENT_ELEMENT_NAME = "Incident";

    protected WebServiceOperations webServiceTemplate;

    public void setWebServiceTemplate(WebServiceOperations webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public WebServiceOperations getWebServiceTemplate() {
		return webServiceTemplate;
	}

	private final String URISuffix = "uicds/core/ws/services";
    private String URI = "http://localhost/" + URISuffix;
    private String outputFile;

    public String getURI() {
    	URI = ((WebServiceTemplate) webServiceTemplate).getDefaultUri();
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
        setDefaultUri(URI);
    }

    public void setOutputFile(String name) {
        outputFile = name;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getDefaultUri() {
        return ((WebServiceTemplate) webServiceTemplate).getDefaultUri();
    }

    public void setDefaultUri(String defaultUri) {
        ((WebServiceTemplate) webServiceTemplate).setDefaultUri(defaultUri);
        URI = defaultUri;
    }

    HashMap<String, ClientCommand> commands;

    public void setCommands(Map<String, ClientCommand> cmds) {
        this.commands = (HashMap<String, ClientCommand>) cmds;
    }

    private String runCommand = null;

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    private String runCommandFileName = null;

    public String getRunCommandFileName() {
        return runCommandFileName;
    }

    public void setRunCommandFileName(String runCommandFileName) {
        this.runCommandFileName = runCommandFileName;
    }

    private String runCommandFileNameList = null;

    public String getRunCommandFileNameList() {
        return runCommandFileNameList;
    }

    public void setRunCommandFileNameList(String runCommandFileNameList) {
        this.runCommandFileNameList = runCommandFileNameList;
    }

    // PullPointAddress Endpoint for subscribed notifications
    private String pullPointAddress;

    public void setPullPointAddress(String ppa) {
        this.pullPointAddress = ppa;
    }

    public String getPullPointAddress() {
        return this.pullPointAddress;
    }

    /*
     * looks up the bean identified by the command and runs the execute method
     */
    public void runClientCommand(String command) {
        System.err.println("\nProcessing command " + command + "\n");
        ClientCommand c = commands.get(command);
        c.execute();
    }

    /*
     * runs a command from a string
     */
    public void runCommand(String command) {
        if (webServiceTemplate == null) {
            System.err.println("webServiceTemplate is null");
            return;
        }

        if (command == null) {
            System.err.println("runCommand failed : runCommand is null");
            return;
        }
        
        System.out.println("Sending to URI: " + ((WebServiceTemplate) webServiceTemplate).getDefaultUri());

        // Unpack the command from a SOAP wrapper if necessary
        command = Common.unpackFromSOAP(command);

        StringWriter s = new StringWriter();
        StreamSource source = new StreamSource(new StringReader(command));
        StreamResult result = new StreamResult(s);
        webServiceTemplate.sendSourceAndReceiveToResult(source, result);
        try {
            XmlObject obj = XmlObject.Factory.parse(s.toString());
            System.out.println(obj);
            if (outputFile != null) {
                writeOutputFile(obj);
            }
            validate(obj);
        } catch (XmlException e) {
            System.err.println("Error parsing result");
        }
    }

    private void writeOutputFile(XmlObject obj) {
        try {
            XmlOptions xo = new XmlOptions();
            // xo.setSavePrettyPrintIndent(3);
            xo.setSavePrettyPrint();
            obj.save(new File(outputFile), xo);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    /*
     * runs a command from an XmlObject
     */
    public XmlObject sendRequest(XmlObject request) {
        if (webServiceTemplate == null) {
            System.err.println("webServiceTemplate is null");
            return null;
        }

        if (request == null) {
            System.err.println("sendRequest failed : sendRequest is null");
            return null;
        }

        XmlObject response = (XmlObject) webServiceTemplate.marshalSendAndReceive(request);

        return response;
    }

    /*
     * validates an XmlObject
     */
    private boolean validate(XmlObject object) {
        boolean valid = false;
        // Set up the validation error listener.
        ArrayList<?> validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        if (!object.validate(validationOptions)) {
            System.err.println("\nSERVICE RESPONSE FAILS VALIDATION\n");
            Iterator<?> iter = validationErrors.iterator();
            while (iter.hasNext()) {
                System.err.println(">> " + iter.next() + "\n");
            }
        } else {
            valid = true;
        }
        return valid;
    }

    /*
     * reads a command from file and executes it
     */
    public void runCommandFileName(String fileName) {
        System.err.println("\nProcessing command file " + fileName);
        String command = Common.getCommandFromFile(fileName);
        if (command != null) {
            System.out.println(fileName + " request:\n" + command);
            runCommand(command);
        }
    }

    /*
     * reads a list files that contain commands and calls runFileCommand for each one
     */
    public void runCommandFileNameList(String requestListFileName) {
        Document xmlFileList = Common.getXmlDocByFile(requestListFileName);
        NodeList nodeList = xmlFileList.getElementsByTagName("requestFileName");
        if (nodeList == null) {
            System.out.println("File " + requestListFileName + " has no requestFileName elements");
        } else {
            int size = nodeList.getLength();
            for (int i = 0; i < size; i++) {
                String requestFileName = nodeList.item(i).getTextContent();
                runCommandFileName(requestFileName);
            }
        }
    }

    /*
     * execute 1) a command, 2) a command from file or 3) commands from a list of files
     */
    public void run() {
        String runCommand = getRunCommand();
        if (runCommand != null) {
            runClientCommand(runCommand);
        } else {
            String runCommandFileName = getRunCommandFileName();
            if (runCommandFileName != null) {
                runCommandFileName(runCommandFileName);
            } else {
                String runCommandFileNameList = getRunCommandFileNameList();
                if (runCommandFileNameList != null) {
                    runCommandFileNameList(runCommandFileNameList);
                } else {
                    System.err.println("Nothing to do");
                }
            }
        }
    }

    public void printCommands() {
        Set<String> cmds = commands.keySet();
        for (String cmd : cmds) {
            System.out.println(cmd);
        }
    }

    /**
     * Get a particular resourceProfile from the core
     * 
     * @param resourceProfileID
     * @return
     */
    public ResourceProfile getResourceProfile(String resourceProfileId) {
        IdentifierType resourceProfileIdentifier = IdentifierType.Factory.newInstance();
        resourceProfileIdentifier.setStringValue(resourceProfileId);
        GetProfileRequestDocument request = GetProfileRequestDocument.Factory.newInstance();
        request.addNewGetProfileRequest().setID(resourceProfileIdentifier);
        GetProfileResponseDocument response = (GetProfileResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        return response.getGetProfileResponse().getProfile();
    }

    /**
     * Get a particular resourceProfile from the core
     * 
     * @param resourceProfileID
     * @return
     */
    public GetProfileResponseDocument getResourceProfileResponseDocument(String resourceProfileId) {
        IdentifierType resourceProfileIdentifier = IdentifierType.Factory.newInstance();
        resourceProfileIdentifier.setStringValue(resourceProfileId);
        GetProfileRequestDocument request = GetProfileRequestDocument.Factory.newInstance();
        request.addNewGetProfileRequest().setID(resourceProfileIdentifier);
        GetProfileResponseDocument response = (GetProfileResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        return response;
    }

    public IdentifierType registerApplication(String applicationID, String localID,
        String resourceProfileID) {

        IdentifierType resourceInstanceIdentifier = null;

        ResourceInstance resourceInstance = getExistingResourceInstance(applicationID);

        if (resourceInstance == null) {
            RegisterRequestDocument request = RegisterRequestDocument.Factory.newInstance();
            request.addNewRegisterRequest().addNewID().setStringValue(applicationID);
            request.getRegisterRequest().addNewLocalResourceID().setStringValue(localID);
            request.getRegisterRequest().addNewResourceProfileID().setStringValue(resourceProfileID);

            RegisterResponseDocument response = (RegisterResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
            resourceInstance = response.getRegisterResponse().getResourceInstance();
            resourceInstanceIdentifier = resourceInstance.getID();
        } else {
            resourceInstanceIdentifier = resourceInstance.getID();
        }

        return resourceInstanceIdentifier;
    }

    public ResourceInstance getExistingResourceInstance(String resourceInstanceId) {
        ResourceInstance resourceInstance = null;

        GetResourceInstanceListResponse list = getResourceInstanceList();
        if (list != null && list.getResourceInstanceList() != null
            && list.getResourceInstanceList().sizeOfResourceInstanceArray() > 0) {
            for (ResourceInstance resource : list.getResourceInstanceList().getResourceInstanceArray()) {
                if (resource.getID().getStringValue().equals(resourceInstanceId)) {
                    resourceInstance = resource;
                }
            }
        }

        return resourceInstance;
    }

    public ResourceInstance getExistingResourceInstance(IdentifierType resourceInstanceIdentifier) {
        ResourceInstance resourceInstance = null;

        GetResourceInstanceRequestDocument request = GetResourceInstanceRequestDocument.Factory.newInstance();
        request.addNewGetResourceInstanceRequest().setID(resourceInstanceIdentifier);
        GetResourceInstanceResponseDocument response = (GetResourceInstanceResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        if (response != null) {
            resourceInstance = response.getGetResourceInstanceResponse().getResourceInstance();
        }

        return resourceInstance;
    }

    public GetResourceInstanceListResponse getResourceInstanceList() {
        GetResourceInstanceListRequestDocument request = GetResourceInstanceListRequestDocument.Factory.newInstance();
        request.addNewGetResourceInstanceListRequest().setQueryString("");
        GetResourceInstanceListResponseDocument response = (GetResourceInstanceListResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        return response.getGetResourceInstanceListResponse();
    }

    /*
     * - removed by ddh for the pilot merge. public void getIncidentList() {
     * GetIncidentListRequestDocument request =
     * GetIncidentListRequestDocument.Factory.newInstance(); request.addNewGetIncidentListRequest();
     * GetIncidentListResponseDocument response = (GetIncidentListResponseDocument)
     * webServiceTemplate.marshalSendAndReceive(request); System.out.println("response:\n" +
     * response); }
     */

    public GetMessagesResponseDocument getMessagesResponseDocument(ResourceInstance resourceInstance) {

        GetMessagesRequestDocument request = GetMessagesRequestDocument.Factory.newInstance();
        BigInteger max = BigInteger.ONE;
        request.addNewGetMessagesRequest().setMaximumNumber(max);
        XmlCursor xc = request.getGetMessagesRequest().newCursor();
        xc.toNextToken();
        QName to = new QName("http://www.w3.org/2005/08/addressing", "To");
        EndpointReferenceType endpoint = getEndpoint(resourceInstance);
        String endpointAddressString = endpoint.getAddress().getStringValue();
        System.out.println("endpointAddressString=" + endpointAddressString);
        xc.insertElementWithText(to, endpointAddressString);
        xc.dispose();
        GetMessagesResponseDocument response = (GetMessagesResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        return response;
    }

    public EndpointReferenceType getEndpoint(ResourceInstance resourceInstance) {
        Endpoints endpoints = resourceInstance.getEndpoints();
        if (endpoints.sizeOfEndpointArray() > 0) {
            return resourceInstance.getEndpoints().getEndpointArray(0);
        } else {
            return null;
        }
    }

    public GetMessagesResponseDocument getMessages(String entityID) {
        GetMessagesRequestDocument request = GetMessagesRequestDocument.Factory.newInstance();
        BigInteger max = BigInteger.ONE;
        request.addNewGetMessagesRequest().setMaximumNumber(max);
        // <wsa:To>http://localhost:8080/uicds/ws/joe@core1.saic.com</wsa:To>
        XmlCursor xc = request.getGetMessagesRequest().newCursor();
        xc.toNextToken();
        QName to = new QName("http://www.w3.org/2005/08/addressing", "To");
        xc.insertElementWithText(to, getURI() + "/" + URISuffix + entityID);
        xc.dispose();
        System.out.println(request);

        GetMessagesResponseDocument response = (GetMessagesResponseDocument) webServiceTemplate.marshalSendAndReceive(
            request, new WebServiceMessageCallback() {

                @Override
                public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException,
                    TransformerException {
                    try {
                        SoapMessage soapMessage = (SoapMessage) webServiceMessage;
                        SoapHeader soapHeader = soapMessage.getSoapHeader();
                        StringSource headerSource = new StringSource(getMessagesHeader());
                        System.out.println("H: " + headerSource);
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.transform(headerSource, soapHeader.getResult());
                        System.out.println("soapHeader: " + soapHeader);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        System.out.println("response:\n" + response);
        return response;
    }

    public GetMessagesResponseDocument getMessagesPullpoint(String pullpoint) {
        GetMessagesRequestDocument request = GetMessagesRequestDocument.Factory.newInstance();
        BigInteger max = BigInteger.ONE;
        request.addNewGetMessagesRequest().setMaximumNumber(max);
        // <wsa:To>http://localhost:8080/uicds/ws/joe@core1.saic.com</wsa:To>
        XmlCursor xc = request.getGetMessagesRequest().newCursor();
        xc.toNextToken();
        QName to = new QName("http://www.w3.org/2005/08/addressing", "To");
        xc.insertElementWithText(to, pullpoint);
        xc.dispose();
        // System.out.println(request);

        GetMessagesResponseDocument response = (GetMessagesResponseDocument) webServiceTemplate.marshalSendAndReceive(
            request, new WebServiceMessageCallback() {

                @Override
                public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException,
                    TransformerException {
                    try {
                        SoapMessage soapMessage = (SoapMessage) webServiceMessage;
                        SoapHeader soapHeader = soapMessage.getSoapHeader();
                        StringSource headerSource = new StringSource(getMessagesHeader());
                        System.out.println("H: " + headerSource);
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.transform(headerSource, soapHeader.getResult());
                        System.out.println("soapHeader: " + soapHeader);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        return response;
    }

    public String getMessagesHeader() {
        ActionDocument action = ActionDocument.Factory.newInstance();
        action.addNewAction().setStringValue(
            "http://docs.oasis-open.org/wsn/bw-2/PullPoint/GetMessagesResponse");

        // Use xmlText to make sure to prefix all the namespaces
        return action.xmlText();
    }

    /**
     * Get an individual work product from the core
     * 
     * @param workProductID
     * @return
     */
    public WorkProduct getWorkProduct(IdentificationType workProductIdentification) {
        GetProductRequestDocument request = GetProductRequestDocument.Factory.newInstance();
        request.addNewGetProductRequest().setWorkProductIdentification(workProductIdentification);
        GetProductResponseDocument response = (GetProductResponseDocument) webServiceTemplate.marshalSendAndReceive(request);
        return response.getGetProductResponse().getWorkProduct();
    }

    /**
     * Get the incident id from a WorkProduct
     * 
     * @param wp
     * @return
     */
    public String getIncidentIDFromWorkProduct(WorkProduct wp) {
        String id = null;
        IncidentDocument incidentDoc = getIncidentDocumentFromWorkProduct(wp);
        if (incidentDoc != null) {
            UICDSIncidentType incident = incidentDoc.getIncident();
            if (incident != null && incident.sizeOfActivityIdentificationArray() > 0) {
                if (incident.getActivityIdentificationArray(0).sizeOfIdentificationIDArray() > 0) {
                    id = incident.getActivityIdentificationArray(0).getIdentificationIDArray(0).getStringValue();
                }
            }
        }
        return id;
    }

    /**
     * Get the IncidentDocument from the structured payload of a WorkProduct
     * 
     * @param wp
     * @return
     */
    public IncidentDocument getIncidentDocumentFromWorkProduct(WorkProduct wp) {
        IncidentDocument incidentDocument = null;
        StructuredPayloadType structuredPayload = wp.getStructuredPayloadArray(0);
        XmlObject[] objects = structuredPayload.selectChildren(new QName(INCIDENT_SERVICE_NS,
            INCIDENT_ELEMENT_NAME));
        if (objects.length > 0) {
            try {
                incidentDocument = IncidentDocument.Factory.parse(objects[0].getDomNode());
            } catch (XmlException e) {
                System.err.println("Error parsing IncidentDocument from payload: " + e.getMessage());
            }
        }
        return incidentDocument;
    }

}
