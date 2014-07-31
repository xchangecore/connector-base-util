package com.saic.uicds.clients.util;

import gov.niem.niem.niemCore.x20.ActivityType;
import gov.niem.niem.niemCore.x20.AddressFullTextDocument;
import gov.niem.niem.niemCore.x20.AddressType;
import gov.niem.niem.niemCore.x20.CircularRegionType;
import gov.niem.niem.niemCore.x20.LatitudeCoordinateType;
import gov.niem.niem.niemCore.x20.LengthMeasureType;
import gov.niem.niem.niemCore.x20.LocationTwoDimensionalGeographicCoordinateDocument;
import gov.niem.niem.niemCore.x20.LongitudeCoordinateType;
import gov.niem.niem.niemCore.x20.MeasurePointValueDocument;
import gov.niem.niem.niemCore.x20.StatusType;
import gov.niem.niem.niemCore.x20.TextType;
import gov.niem.niem.niemCore.x20.TwoDimensionalGeographicCoordinateType;
import gov.ucore.ucore.x20.DigestDocument;
import gov.ucore.ucore.x20.DigestType;
import gov.ucore.ucore.x20.EntityLocationExtendedRelationshipType;
import gov.ucore.ucore.x20.EntityType;
import gov.ucore.ucore.x20.EventType;
import gov.ucore.ucore.x20.GeoLocationType;
import gov.ucore.ucore.x20.LocationType;
import gov.ucore.ucore.x20.PointType;
import gov.ucore.ucore.x20.ThingType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.oasisOpen.docs.wsn.b2.NotificationMessageHolderType;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.client.core.WebServiceOperations;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.uicds.incident.IncidentDocument;
import org.uicds.incident.UICDSIncidentType;
import org.uicds.notificationService.GetMessagesResponseDocument;
import org.uicds.resourceProfileService.CreateProfileRequestDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import x0Msg.oasisNamesTcEmergencyEDXLRM1.CommitResourceDocument;
import x0Msg.oasisNamesTcEmergencyEDXLRM1.RequestResourceDocument;

import com.saic.precis.x2009.x06.base.ChecksumType;
import com.saic.precis.x2009.x06.base.CodespaceValueType;
import com.saic.precis.x2009.x06.base.IdentificationType;
import com.saic.precis.x2009.x06.base.IdentifierType;
import com.saic.precis.x2009.x06.base.PropertiesType;
import com.saic.precis.x2009.x06.base.StateType;
import com.saic.precis.x2009.x06.base.VersionType;
import com.saic.precis.x2009.x06.structures.WorkProductDocument.WorkProduct;

public class Common {

    private static final String ZERO_POINT_ZERO = "0.0";
    private static final String ZERO = "0";
    public static final String PRECISS_NS = "http://www.saic.com/precis/2009/06/structures";
    public static final String PRECISB_NS = "http://www.saic.com/precis/2009/06/base";
    public static final String UCORE_NS = "http://ucore.gov/ucore/2.0";
    public static final String GML_NS = "http://www.opengis.net/gml/3.2";
    public static final String NIEM_NS = "http://niem.gov/niem/niem-core/2.0";
    public static final String INCIDENT_SERVICE_NS = "http://uicds.org/incident";

    public static final String INCIDENT_ELEMENT_NAME = "Incident";

    public static final String WORKPRODUCT_IDENTIFICATION = "WorkProductIdentification";
    public static final String WORKPRODUCT_PROPERTIES = "WorkProductProperties";
    public static final String WORKPRODUCT_DIGEST = "Digest";
    public static final String WORKPRODUCT_LOCATEDAT = "LocatedAt";
    public static final String WORKPRODUCT_ENTITY = "Entity";
    public static final String WORKPRODUCT_LOCATION = "Location";
    public static final String WORKPRODUCT_GEOLOCATION = "GeoLocation";
    public static final String WORKPRODUCT_POINT = "Point";
    public static final String WORKPRODUCT = "WorkProduct";
    public static final String WORKPRODUCT_ACTIVE = "Active";
    public static final String WORKPRODUCT_INACTIVE = "Inactive";

    public static final String ACTIVITY_DATE = "ActivityDate";

    public static String getArgURI(String[] args) {

        // set the uri from the system property
        String uRI = System.getProperty("target.uri");

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-u")) {
                // if there's a -u switch then there should be a target uri
                i++;
                if (args[i] == null) {
                    System.out.println("Switch -u must be followed with a target URI");
                    return null;
                }
                uRI = args[i];
            }
        }
        return uRI;
    }

    public static String getArgParameterFileName(String[] args) {

        String parameterFileName = null;

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-i")) {
                // if there's a -i switch then there should be a parameterFileName
                i++;
                if (args[i] == null) {
                    System.out.println("Switch -i must be followed with a target ParameterFileName");
                    return null;
                }
                parameterFileName = args[i];
            }
        }

        if (parameterFileName != null) {
            statPathFile(parameterFileName);
        }

        return parameterFileName;
    }

    // get the base url (up to the third slash in http://.../)
    public static String getBaseURL(String uri) {

        int slashIndex = 0;
        for (int i = 0; i < 3; i++) {
            if ((uri.substring(slashIndex).length()) > 0) {
                slashIndex = uri.indexOf("/", slashIndex) + 1;
            }
        }
        return uri.substring(0, slashIndex - 1);
    }

    public static HttpsURLConnection getHttpsURLConnection(String url) {

        HttpsURLConnection httpsUrlConn = null;

        String javaHome = System.getProperty("java.home");
        String certPathFileName = javaHome + "\\lib\\security\\jssecacerts";
        statPathFile(certPathFileName);
        System.out.println("certPathFileName=" + certPathFileName);
        System.setProperty("javax.net.ssl.trustStore", certPathFileName);
        System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");

        try {
            URL connectUrl = new URL(url);
            URLConnection urlConn = connectUrl.openConnection();
            httpsUrlConn = (HttpsURLConnection) urlConn;
            httpsUrlConn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {

                    return true;
                }
            });
            System.out.println("connected to " + httpsUrlConn.getURL());
        } catch (Exception ex) {
            System.err.println("https connection failed");
            ex.printStackTrace();
        }
        return httpsUrlConn;
    }

    public static String unpackFromSOAP(String command) {

        String unpackedCommand = "";

        try {
            XmlObject object = XmlObject.Factory.parse(command);
            XmlObject[] envelopes = object.selectChildren(
                "http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
            if (envelopes.length > 0) {
                XmlObject[] bodies = envelopes[0].selectChildren(
                    "http://schemas.xmlsoap.org/soap/envelope/", "Body");
                if (bodies.length > 0) {
                    XmlCursor xc = bodies[0].newCursor();
                    xc.toFirstChild();
                    XmlOptions xo = new XmlOptions();
                    xo.setSaveOuter();
                    unpackedCommand = xc.getObject().xmlText(xo);
                    xc.dispose();
                }
            } else {
                unpackedCommand = command;
            }
        } catch (XmlException e) {
            System.err.println("Error unpacking message from SOAP wrapper");
            ;
        }

        return unpackedCommand;
    }

    public static XmlObject unpackXmlObjectFromSOAP(String command) {

        XmlObject unpackedXmlObjectCommand = null;

        XmlObject object = null;
        ;
        try {
            object = XmlObject.Factory.parse(command);
        } catch (XmlException e1) {
            e1.printStackTrace();
        }

        XmlObject[] envelopes = object.selectChildren("http://schemas.xmlsoap.org/soap/envelope/",
            "Envelope");
        if (envelopes.length > 0) {
            XmlObject[] bodies = envelopes[0].selectChildren(
                "http://schemas.xmlsoap.org/soap/envelope/", "Body");
            if (bodies.length > 0) {
                XmlCursor xc = bodies[0].newCursor();
                xc.toFirstChild();
                XmlOptions xo = new XmlOptions();
                xo.setSaveOuter();
                String unpackedCommand = xc.getObject().xmlText(xo);
                try {
                    unpackedXmlObjectCommand = XmlObject.Factory.parse(unpackedCommand);
                } catch (XmlException e) {
                    e.printStackTrace();
                }
                xc.dispose();
            }
        } else {
            unpackedXmlObjectCommand = object;
        }

        return unpackedXmlObjectCommand;
    }

    public static String getTextFromAny(XmlObject object) {

        XmlCursor c = object.newCursor();
        String text = c.getTextValue();
        c.dispose();
        return text;
    }

    public static String getAttributeTextFromAny(XmlObject object, QName attributeName) {

        XmlCursor c = object.newCursor();
        String text = c.getAttributeText(attributeName);
        c.dispose();
        return text;
    }

    public static String getCommandFromFile(String pathFileName) {

        String text = null;
        try {
            File pathFile = new File(pathFileName);
            long pathFileLengthLong = pathFile.length();
            int pathFileLength = 0;

            if (pathFileLengthLong > java.lang.Integer.MAX_VALUE) {
                pathFileLength = java.lang.Integer.MAX_VALUE;
            } else {
                pathFileLength = (int) pathFileLengthLong;
            }

            FileInputStream fis = new FileInputStream(pathFile);
            byte buff[] = new byte[pathFileLength];

            fis.read(buff);
            text = new String(buff);

        } catch (IOException tex) {
            System.err.println("File not found: " + pathFileName);
        }
        return (text);
    }

    public static Document getXmlDocByFile(String xmlPathFileName) {

        Document xmlDoc = null;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

            docFactory.setNamespaceAware(true); // "never forget this!" why?
            docFactory.setValidating(false); // turn off dtd validation
            docFactory.setExpandEntityReferences(false); // Prevent expansion of entity references

            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            xmlDoc = docBuilder.parse(xmlPathFileName);
        } catch (IOException tex) {
            System.err.println("File IO exception:" + xmlPathFileName);
            System.exit(1);
        } catch (ParserConfigurationException tex) {
            System.err.println("The parser was not configured correctly");
            System.exit(1);
        } catch (SAXException tex) {
            System.err.println("Parser exception:" + xmlPathFileName);
            System.exit(1);
        }
        return xmlDoc;
    }

    /*
     * extracts the parameter value for the parameter name in the parameters document
     */
    public static String getDocumentParameter(Document parametersDocument, String parameterName) {

        String parameterValue = null;
        NodeList nodeList = parametersDocument.getElementsByTagName(parameterName);
        if (nodeList == null) {
            System.out.println("Parameter not found: " + parameterName);
        } else {
            int size = nodeList.getLength();
            for (int i = 0; i < size; i++) {
                parameterValue = nodeList.item(i).getTextContent();
            }
        }
        return parameterValue;
    }

    public static String statPathFile(String pathFileName) {

        if (!(new File(pathFileName)).isFile()) {
            System.err.println("pathFileName does not exist:" + pathFileName);
        }
        return (pathFileName);
    }

    public static Boolean createPathDir(String pathDirName) {

        // create the directory
        File pathDir = new File(pathDirName);

        if (!pathDir.exists()) {
            if (!pathDir.mkdirs()) {
                System.err.println("failed to create:" + pathDirName);
                return false;
            }
        } else if (!pathDir.isDirectory()) {
            System.err.println("not a directory:" + pathDirName);
            return false;
        }
        return true;
    }

    public static String getFaultDetailedDescription(WebServiceOperations webServiceTemplate,
        SoapFaultClientException wsTransEx, org.slf4j.Logger logger) {

        String detailDescription = null;
        @SuppressWarnings("unchecked")
        Iterator<SoapFaultDetailElement> it = wsTransEx.getSoapFault().getFaultDetail().getDetailEntries();
        while (it.hasNext()) {
            SoapFaultDetailElement e = (SoapFaultDetailElement) it.next();
            if (e.getName().getLocalPart().equalsIgnoreCase("description")) {
                Source detailSource = e.getSource();
                try {
                    Object detail = ((WebServiceTemplate) webServiceTemplate).getUnmarshaller().unmarshal(
                        detailSource);
                    if (detail != null) {
                        detailDescription = Common.getTextFromAny((XmlObject) detail);
                    }
                } catch (XmlMappingException e1) {
                    logger.error("====> ERROR: caught exception from unmarshalling exception message, XmlMappingException="
                        + e1.getMessage());
                    return e1.getMessage();
                } catch (IOException e1) {
                    logger.error("====> ERROR: caught exception from unmarshalling exception message="
                        + e1.getMessage());
                    return e1.getMessage();
                }
            }
        }
        return detailDescription;

    }

    public static String getFullyQualifiedHostName() {

        String localhost = null;
        try {
            localhost = InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            localhost = "Unknown Host";
        }
        return localhost;
    }

    public static String getEndpointPullpointAddress(EndpointReferenceType[] endpoints) {

        String pullPointAddress = null;

        for (EndpointReferenceType endpoint : endpoints) {
            if ((pullPointAddress = endpoint.getAddress().getStringValue()) != null)
                break;
        }
        return pullPointAddress;
    }

    public static IdentificationType createWorkProductIdentification(ChecksumType wpChecksum,
        IdentifierType wpIdentifier, CodespaceValueType wpType, VersionType wpVersion) {

        IdentificationType workProductIdentification = IdentificationType.Factory.newInstance();

        workProductIdentification.setChecksum(wpChecksum);
        workProductIdentification.setIdentifier(wpIdentifier);
        workProductIdentification.setType(wpType);
        workProductIdentification.setVersion(wpVersion);

        return workProductIdentification;
    }

    public static IdentificationType createWorkProductIdentification(String checksum,
        String identifier, String type, String version, String state) {

        IdentificationType workProductIdentification = IdentificationType.Factory.newInstance();

        ChecksumType wpChecksum = ChecksumType.Factory.newInstance();
        wpChecksum.setStringValue(checksum);
        workProductIdentification.setChecksum(wpChecksum);

        IdentifierType wpIdentifier = IdentifierType.Factory.newInstance();
        wpIdentifier.setStringValue(identifier);
        workProductIdentification.setIdentifier(wpIdentifier);

        CodespaceValueType wpType = CodespaceValueType.Factory.newInstance();
        wpType.setStringValue(type);
        workProductIdentification.setType(wpType);

        VersionType wpVersion = VersionType.Factory.newInstance();
        wpVersion.setStringValue(version);
        workProductIdentification.setVersion(wpVersion);

        if (state.equalsIgnoreCase(WORKPRODUCT_ACTIVE)) {
            workProductIdentification.setState(StateType.ACTIVE);
        } else {
            workProductIdentification.setState(StateType.INACTIVE);
        }

        return workProductIdentification;
    }

    public static PropertiesType createWorkProductProperties(String igid, String createdDate,
        String createdBy, String size, String lastUpdatedDate, String lastUpdatedBy,
        String mimeCodespace, String codeValue) {

        PropertiesType props = PropertiesType.Factory.newInstance();

        props.addNewAssociatedGroups().addNewIdentifier().setStringValue(igid);
        props.addNewCreated().setStringValue(createdDate);
        props.addNewCreatedBy().setStringValue(createdBy);
        props.addNewKilobytes().setStringValue(size);
        props.addNewLastUpdated().setStringValue(lastUpdatedDate);
        props.addNewLastUpdatedBy().setStringValue(lastUpdatedBy);
        props.addNewMimeType().setCodespace(mimeCodespace);
        props.getMimeType().setStringValue(codeValue);
        return props;
    }

    /**
     * Set the Identifier element in the given abstract package metadata
     * 
     * @param packageMetadataExtensionAbstract
     * @param workProductIdentification
     */
    public static final void setIdentifierElement(XmlObject packageMetadataExtensionAbstract,
        XmlObject workProductIdentification) {

        substitute(packageMetadataExtensionAbstract, PRECISS_NS, WORKPRODUCT_IDENTIFICATION,
            IdentificationType.type, workProductIdentification);
    }

    /**
     * Set the Properties element in the given abstract package metadata
     * 
     * @param packageMetadataExtensionAbstract
     * @param workProductProperties
     */
    public static final void setPropertiesElement(XmlObject packageMetadataExtensionAbstract,
        XmlObject workProductProperties) {

        substitute(packageMetadataExtensionAbstract, PRECISS_NS, WORKPRODUCT_PROPERTIES,
            PropertiesType.type, workProductProperties);
    }

    /**
     * Set the Digest element in the given abstract digest
     * 
     * @param digestAbstract
     * @param workProductDigest
     */
    public static final void setDigestElement(XmlObject digestAbstract, XmlObject workProductDigest) {

        substitute(digestAbstract, UCORE_NS, WORKPRODUCT_DIGEST, DigestType.type, workProductDigest);
    }

    public static void setEvent(EventType event, DigestType digest) {

        // add an Event
        substitute(digest.addNewThingAbstract(), UCORE_NS, "Event", EventType.type, event);
    }

    /**
     * Set the Entity element in the given abstract digest abstract Entity
     * 
     * @param thingAbstract
     * @param workProductEntity
     */
    public static final void setEntityElement(XmlObject thingAbstract, XmlObject workProductEntity) {

        substitute(thingAbstract, UCORE_NS, WORKPRODUCT_ENTITY, EntityType.type, workProductEntity);
    }

    /**
     * Set the locatedAt element in the given abstract digest abstract locatedAt
     * 
     * @param thingAbstract
     * @param workProductLocatedAt
     */
    public static final void setLocatedAtElement(XmlObject thingAbstract,
        XmlObject workProductLocatedAt) {

        substitute(thingAbstract, UCORE_NS, WORKPRODUCT_LOCATEDAT,
            EntityLocationExtendedRelationshipType.type, workProductLocatedAt);
    }

    /**
     * Set the Location element in the given abstract digest abstract location
     * 
     * @param digestAbstract
     * @param workProductLocation
     */
    public static final void setLocationElement(XmlObject thingAbstract,
        XmlObject workProductLocation) {

        substitute(thingAbstract, UCORE_NS, WORKPRODUCT_LOCATION, LocationType.type,
            workProductLocation);
    }

    /**
     * Get a list of all the Location elements in the digest.
     * 
     * @param digest
     * @return
     */
    public static List<LocationType> getLocationElements(DigestType digest) {

        ArrayList<LocationType> list = new ArrayList<LocationType>();
        XmlObject[] locations = digest.selectChildren(
            gov.ucore.ucore.x20.LocationType.type.getName().getNamespaceURI(), "Location");
        if (locations.length > 0) {
            for (XmlObject object : locations) {
                LocationType location = (LocationType) object;
                list.add(location);
            }
        }
        return list;
    }

    public static gov.ucore.ucore.x20.PointType getPointFromLocationType(LocationType location) {

        XmlObject[] geoLocations = location.selectChildren(
            gov.ucore.ucore.x20.GeoLocationType.type.getName().getNamespaceURI(), "GeoLocation");
        if (geoLocations != null && geoLocations.length > 0) {
            for (XmlObject object : geoLocations) {
                GeoLocationType geo = (GeoLocationType) object;
                XmlObject[] UcorePoint = geo.selectChildren(
                    gov.ucore.ucore.x20.PointType.type.getName().getNamespaceURI(), "Point");
                if (UcorePoint.length > 0) {
                    gov.ucore.ucore.x20.PointType point = (gov.ucore.ucore.x20.PointType) UcorePoint[0];
                    return point;
                }
            }
        }
        return null;
    }

    public static gov.ucore.ucore.x20.CircleByCenterPointDocument getCircleByCenterPointFromLocationType(
        LocationType location) {

        XmlObject[] geoLocations = location.selectChildren(
            gov.ucore.ucore.x20.GeoLocationType.type.getName().getNamespaceURI(), "GeoLocation");
        if (geoLocations != null && geoLocations.length > 0) {
            for (XmlObject object : geoLocations) {
                GeoLocationType geo = (GeoLocationType) object;
                XmlObject[] ucoreCircles = geo.selectChildren(
                    gov.ucore.ucore.x20.CircleByCenterPointType.type.getName().getNamespaceURI(),
                    "CircleByCenterPoint");
                if (ucoreCircles.length > 0) {
                    gov.ucore.ucore.x20.CircleByCenterPointType ucoreCircle = (gov.ucore.ucore.x20.CircleByCenterPointType) ucoreCircles[0];
                    gov.ucore.ucore.x20.CircleByCenterPointDocument circle = gov.ucore.ucore.x20.CircleByCenterPointDocument.Factory.newInstance();
                    circle.setCircleByCenterPoint(ucoreCircle);
                    return circle;
                }
            }
        }
        return null;
    }

    /**
     * /** Set the Location element in the given abstract digest abstract abstract location abstract
     * geolocation
     * 
     * @param geoLocationAbstract
     * @param workProductGeoLocation
     */
    public static final void setGeoLocationElement(XmlObject geoLocationAbstract,
        XmlObject workProductGeoLocation) {

        // substitute(geoLocationAbstract, GML_NS, WORKPRODUCT_POINT,
        // net.opengis.gml.x32.PointType.type, workProductGeoLocation);
        substitute(geoLocationAbstract, UCORE_NS, WORKPRODUCT_POINT, PointType.type,
            workProductGeoLocation);
    }

    public static LocationTwoDimensionalGeographicCoordinateDocument toCoordinate(String latLon)
        throws NumberFormatException {

        String[] points = latLon.split(",");

        if (points.length != 2) {
            return null;
        }

        LocationTwoDimensionalGeographicCoordinateDocument loc = LocationTwoDimensionalGeographicCoordinateDocument.Factory.newInstance();

        try {
            String[] values = toDegMinSec(points[0]);
            loc.addNewLocationTwoDimensionalGeographicCoordinate().addNewGeographicCoordinateLatitude().addNewLatitudeDegreeValue().setStringValue(
                values[0]);
            loc.getLocationTwoDimensionalGeographicCoordinate().getGeographicCoordinateLatitude().addNewLatitudeMinuteValue().setStringValue(
                values[1]);
            loc.getLocationTwoDimensionalGeographicCoordinate().getGeographicCoordinateLatitude().addNewLatitudeSecondValue().setStringValue(
                values[2]);
            values = toDegMinSec(points[1]);
            loc.getLocationTwoDimensionalGeographicCoordinate().addNewGeographicCoordinateLongitude().addNewLongitudeDegreeValue().setStringValue(
                values[0]);
            loc.getLocationTwoDimensionalGeographicCoordinate().getGeographicCoordinateLongitude().addNewLongitudeMinuteValue().setStringValue(
                values[1]);
            loc.getLocationTwoDimensionalGeographicCoordinate().getGeographicCoordinateLongitude().addNewLongitudeSecondValue().setStringValue(
                values[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        return loc;
    }

    public static CircularRegionType createCircle(String latitude, String longitude) {

        CircularRegionType circle = CircularRegionType.Factory.newInstance();

        circle.addNewCircularRegionCenterCoordinate().set(getCircleCenter(latitude, longitude));

        LengthMeasureType radius = circle.addNewCircularRegionRadiusLengthMeasure();
        MeasurePointValueDocument value = MeasurePointValueDocument.Factory.newInstance();
        value.addNewMeasurePointValue().setStringValue(ZERO_POINT_ZERO);
        radius.set(value);
        return circle;
    }

    public static TwoDimensionalGeographicCoordinateType getCircleCenter(String latitude,
        String longitude) {

        TwoDimensionalGeographicCoordinateType center = TwoDimensionalGeographicCoordinateType.Factory.newInstance();

        LatitudeCoordinateType latCoord = LatitudeCoordinateType.Factory.newInstance();
        try {
            String[] values = toDegMinSec(latitude);
            latCoord.addNewLatitudeDegreeValue().setStringValue(values[0]);
            latCoord.addNewLatitudeMinuteValue().setStringValue(values[1]);
            latCoord.addNewLatitudeSecondValue().setStringValue(values[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing latitude: " + e.getMessage());
            latCoord.addNewLatitudeDegreeValue().setStringValue(ZERO);
            latCoord.addNewLatitudeMinuteValue().setStringValue(ZERO);
            latCoord.addNewLatitudeSecondValue().setStringValue(ZERO_POINT_ZERO);
        }
        center.setGeographicCoordinateLatitude(latCoord);

        LongitudeCoordinateType lonCoord = LongitudeCoordinateType.Factory.newInstance();
        try {
            String[] values = toDegMinSec(longitude);
            lonCoord.addNewLongitudeDegreeValue().setStringValue(values[0]);
            lonCoord.addNewLongitudeMinuteValue().setStringValue(values[1]);
            lonCoord.addNewLongitudeSecondValue().setStringValue(values[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing latitude: " + e.getMessage());
            lonCoord.addNewLongitudeDegreeValue().setStringValue(ZERO);
            lonCoord.addNewLongitudeMinuteValue().setStringValue(ZERO);
            lonCoord.addNewLongitudeSecondValue().setStringValue(ZERO_POINT_ZERO);
        }
        center.setGeographicCoordinateLongitude(lonCoord);

        return center;
    }

    public static String[] toDegMinSec(String decimal) {

        double d = Double.parseDouble(decimal);
        int degrees = (int) d;
        d = Math.abs(d - degrees) * 60;
        int minutes = (int) d;
        double seconds = ((d - minutes) * 60) + 0.005;
        String[] ret = new String[3];
        ret[0] = String.valueOf(degrees);
        ret[1] = String.valueOf(minutes);
        ret[2] = String.valueOf(seconds).substring(0, 5);
        return ret;
    }

    // convert deg/min/sec to decimal degrees
    public static String fromDegMinSec(LongitudeCoordinateType longitude) {

        String deg = "0.0";
        String min = "0.0";
        String sec = "0.0";
        String decimalValue = "0.0";

        if (longitude != null) {
            if (longitude.sizeOfLongitudeDegreeValueArray() > 0) {
                deg = longitude.getLongitudeDegreeValueArray(0).getStringValue();
            }
            if (longitude.sizeOfLongitudeMinuteValueArray() > 0) {
                min = longitude.getLongitudeMinuteValueArray(0).getStringValue();
            }
            if (longitude.sizeOfLongitudeSecondValueArray() > 0) {
                sec = longitude.getLongitudeSecondValueArray(0).getStringValue();
            }
            // Calculate decimal from degree minute second
            decimalValue = fromDegMinSec(deg, min, sec);

        } else {
            System.out.println("LatitudeCoordinateType is null");
        }

        return String.valueOf(decimalValue);
    }

    // convert deg/min/sec to decimal degrees
    public static String fromDegMinSec(LatitudeCoordinateType latitude) {

        String deg = "0.0";
        String min = "0.0";
        String sec = "0.0";
        String decimalValue = "0.0";

        if (latitude != null) {
            if (latitude.sizeOfLatitudeDegreeValueArray() > 0) {
                deg = latitude.getLatitudeDegreeValueArray(0).getStringValue();
            }
            if (latitude.sizeOfLatitudeMinuteValueArray() > 0) {
                min = latitude.getLatitudeMinuteValueArray(0).getStringValue();
            }
            if (latitude.sizeOfLatitudeSecondValueArray() > 0) {
                sec = latitude.getLatitudeSecondValueArray(0).getStringValue();
            }
            // Calculate decimal from degree minute second
            decimalValue = fromDegMinSec(deg, min, sec);

        } else {
            System.out.println("LatitudeCoordinateType is null");
        }

        return String.valueOf(decimalValue);
    }

    protected static String fromDegMinSec(String degrees, String minutes, String seconds) {

        Double dDegree = toDouble(degrees, null);
        Double dMinute = toDouble(minutes, 0.0);
        Double dSecond = toDouble(seconds, 0.0);

        int sign = (int) (dDegree / Math.abs(dDegree));
        dDegree = sign * (Math.abs(dDegree) + (dMinute / 60.0) + (dSecond / 3600.0));

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(5);
        nf.setMinimumFractionDigits(5);

        return nf.format(dDegree);
    }

    protected static Double toDouble(String doubleString, Double defaultValue)
        throws NumberFormatException {

        try {
            double d = Double.parseDouble(doubleString);
            return d;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /*
        public static void setPoint(LocationType location, net.opengis.gml.x32.PointType point) {

            PointType uPoint = PointType.Factory.newInstance();
            uPoint.addNewPoint().set(point);
            substitute(location.addNewGeoLocation().addNewGeoLocationAbstract(), UCORE_NS, WORKPRODUCT_POINT, PointType.type, uPoint);
            
    		GeoLocationType geoLocation = GeoLocationType.Factory.newInstance();
    		GeoLocationType[] geoLocationArray = new GeoLocationType[1];
    		geoLocationArray[0] = geoLocation;
    		XmlObject geoLocationAbstract = geoLocation.addNewGeoLocationAbstract();
    		location.setGeoLocationArray(geoLocationArray);
    		setGeoLocationElement(geoLocationAbstract,uPoint);

        }
    */

    /**
     * Helper class for handling substitution elements
     * 
     * @param parentObject
     * @param subNamespace
     * @param subTypeName
     * @param subSchemaType
     * @param theObject
     */
    public static final void substitute(XmlObject parentObject, String subNamespace,
        String subTypeName, SchemaType subSchemaType, XmlObject theObject) {

        XmlObject subObject = parentObject.substitute(new QName(subNamespace, subTypeName),
            subSchemaType);
        if (subObject == parentObject) {
            System.out.println("cannot change to " + subTypeName);
        } else {
            subObject.set(theObject);
        }
    }

    /**
     * validate an XmlBeans object.
     * 
     * @param object
     * @param printValidationErrors
     * @return
     */
    public static boolean validate(XmlObject object, boolean printValidationErrors) {

        boolean valid = false;
        // Set up the validation error listener.
        ArrayList<String> validationErrors = new ArrayList<String>();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        if (!object.validate(validationOptions) && printValidationErrors) {
            System.out.println("VALIDATION FAILED");
            Iterator<?> iter = validationErrors.iterator();
            while (iter.hasNext()) {
                System.out.println(">> " + iter.next() + "\n");
            }
        } else {
            valid = true;
        }
        return valid;
    }

    /**
     * Get the Identification of the given WorkProduct
     * 
     * @param workProduct
     * @return
     */
    public static final IdentificationType getIdentificationElement(WorkProduct workProduct) {

        IdentificationType id = null;
        if (workProduct == null) {
            System.err.println("Trying to get an identification element from a null work product");
        }
        if (workProduct != null && workProduct.getPackageMetadata() != null) {
            XmlObject[] objects = workProduct.getPackageMetadata().selectChildren(
                new QName(PRECISS_NS, WORKPRODUCT_IDENTIFICATION));
            if (objects.length > 0) {
                id = (IdentificationType) objects[0];
            }
        }
        return id;
    }

    /**
     * Get the Properties element from the given WorkProduct
     * 
     * @param workProduct
     * @return
     */
    public static final PropertiesType getPropertiesElement(WorkProduct workProduct) {

        PropertiesType properties = null;
        if (workProduct != null && workProduct.getPackageMetadata() != null) {
            XmlObject[] objects = workProduct.getPackageMetadata().selectChildren(
                new QName(PRECISS_NS, WORKPRODUCT_PROPERTIES));
            if (objects.length > 0) {
                properties = (PropertiesType) objects[0];
            }
        }
        return properties;
    }

    public static IdentifierType getFirstAssociatedInterestGroup(WorkProduct workProduct) {

        PropertiesType properties = Common.getPropertiesElement(workProduct);
        return getFirstAssociatedInterestGroup(properties);
    }

    public static IdentifierType getFirstAssociatedInterestGroup(PropertiesType properties) {

        if (properties == null || properties.getAssociatedGroups() == null
            || properties.getAssociatedGroups().sizeOfIdentifierArray() == 0) {
            return null;
        }

        return properties.getAssociatedGroups().getIdentifierArray(0);
    }

    public static AddressFullTextDocument getFullTextAddressFromLocationAddressArray(
        AddressType address) {

        AddressFullTextDocument fullText = null;
        try {
            fullText = AddressFullTextDocument.Factory.parse(address.xmlText());
        } catch (XmlException e) {
            System.err.println("error paring full test address: " + e.getMessage());
        }
        return fullText;
    }

    public static Date getISO8601LocalDateFromActivityDate(XmlObject activityDateRepresentation,
        SimpleDateFormat dateFormat) {

        XmlCursor cursor = activityDateRepresentation.newCursor();
        String calendarString = cursor.getTextValue();
        cursor.dispose();
        if (calendarString != null && calendarString.length() > 0) {
            try {
                return (Date) dateFormat.parse(calendarString.trim());
            } catch (ParseException e) {
                System.err.println("Error parsing date string should be yyyy-MM-dd'T'HH:mm:ss format: "
                    + e.getMessage());
            }
        }
        return null;
        // XmlCursor cursor =
        // incident.getIncident().getActivityDateRepresentationArray(0).newCursor();
        // String calendarString = cursor.getTextValue();
        // cursor.dispose();
        // if (calendarString != null && calendarString.length() > 0) {
        // SimpleDateFormat ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // TimeZone timeZone = TimeZone.getDefault();
        // ISO8601Local.setTimeZone(timeZone);
        // try {
        // Date dateTime = (Date) ISO8601Local.parse(calendarString.trim());
        // fieldValueMap.put(UICDSPositionLogBoard.DATA_TIME_FIELD,
        // ISO8601Local.format(dateTime));
        // } catch (ParseException e) {
        // System.err.println("Error parsing date string should be yyyy-MM-dd'T'HH:mm:ss format: "
        // + e.getMessage());
        // }
        // }
    }

    public static DigestType getDigest(WorkProduct workProduct) {

        DigestType digest = null;
        if (workProduct != null) {
            XmlObject[] objects = workProduct.selectChildren(DigestDocument.type.getDocumentElementName());
            if (objects.length > 0) {
                digest = (DigestType) objects[0];
            }
        }
        return digest;
    }

    public static EventType getEvent(DigestType digest, String eventId) {

        ThingType[] things = digest.getThingAbstractArray();
        for (ThingType thing : things) {
            if (thing.getId().equalsIgnoreCase(eventId.trim()) && (thing instanceof EventType)) {
                return (EventType) thing;
            }
        }
        return null;
    }

    public static ArrayList<EventType> getEvents(DigestType digest) {

        ArrayList<EventType> events = new ArrayList<EventType>();
        ThingType[] things = digest.getThingAbstractArray();
        for (ThingType thing : things) {
            if (thing instanceof EventType) {
                events.add((EventType) thing);
            }
        }
        return events;
    }

    public static List<IdentificationType> getWorkProductIdentificationList(
        GetMessagesResponseDocument response) {

        NotificationMessageHolderType[] messages = response.getGetMessagesResponse().getNotificationMessageArray();
        List<IdentificationType> identificationList = new ArrayList<IdentificationType>();
        for (NotificationMessageHolderType notificationMessage : messages) {
            NotificationMessageHolderType.Message message = notificationMessage.getMessage();

            // Only process WorkProductSummary notifications
            XmlObject[] summaries = message.selectChildren(new QName(PRECISS_NS, WORKPRODUCT));
            if (summaries.length > 0) {
                WorkProduct summary = (WorkProduct) summaries[0];
                identificationList.add(getIdentificationElement(summary));
            }
        }
        return identificationList;
    }

    public static IncidentDocument getIncidentDocumentFromWorkProduct(WorkProduct workProduct) {

        IncidentDocument incidentDocument = null;
        XmlObject[] objects = workProduct.getStructuredPayloadArray(0).selectChildren(
            new QName(INCIDENT_SERVICE_NS, INCIDENT_ELEMENT_NAME));
        if (objects.length > 0) {
            try {
                incidentDocument = IncidentDocument.Factory.parse(objects[0].getDomNode());
            } catch (XmlException e) {
                System.err.println("Error parsing IncidentDocument from payload: " + e.getMessage());
            }
        }
        return incidentDocument;
    }

    public static WorkProduct updateIncidentDocumentInWorkProduct(WorkProduct workProduct,
        IncidentDocument incident) {

        workProduct.getStructuredPayloadArray(0).set(incident);

        return workProduct;
    }

    public static WorkProduct updateInidentInWorkProduct(WorkProduct workProduct,
        UICDSIncidentType incident) {

        IncidentDocument doc = IncidentDocument.Factory.newInstance();
        doc.addNewIncident().set(incident);
        return updateIncidentDocumentInWorkProduct(workProduct, doc);
    }

    public static CreateProfileRequestDocument getCreateProfileRequestDocumentFromFile(
        String fileName) {

        try {
            InputStream in = new FileInputStream(fileName);
            CreateProfileRequestDocument createProfileRequestDocument = CreateProfileRequestDocument.Factory.parse(in);
            return createProfileRequestDocument;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        } catch (XmlException e) {
            System.err.println("error parsing files " + " " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        }
        return null;
    }

    public static IncidentDocument getIncidentDocumentFromFile(String fileName) {

        try {
            InputStream in = new FileInputStream(fileName);
            IncidentDocument incidentDocument = IncidentDocument.Factory.parse(in);
            return incidentDocument;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        } catch (XmlException e) {
            System.err.println("error parsing files " + " " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        }
        return null;
    }

    public static RequestResourceDocument getRequestResourceDocumentFromFile(String fileName) {

        try {
            InputStream in = new FileInputStream(fileName);
            RequestResourceDocument requestResourceDocument = RequestResourceDocument.Factory.parse(in);
            return requestResourceDocument;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        } catch (XmlException e) {
            System.err.println("error parsing files " + " " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        }
        return null;
    }

    public static CommitResourceDocument getCommitResourceDocumentFromFile(String fileName) {

        try {
            InputStream in = new FileInputStream(fileName);
            CommitResourceDocument commitResourceDocument = CommitResourceDocument.Factory.parse(in);
            return commitResourceDocument;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        } catch (XmlException e) {
            System.err.println("error parsing files " + " " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File not found: " + fileName + " " + e.getMessage());
        }
        return null;
    }

    public static UICDSIncidentType addIncidentEvent(UICDSIncidentType incident, String reason,
        String category, String id, String description) {

        ActivityType event = incident.addNewIncidentEvent();
        event.addNewActivityReasonText().setStringValue(reason);

        event.addNewActivityCategoryText().setStringValue(category);

        gov.niem.niem.niemCore.x20.IdentificationType eventID = event.addNewActivityIdentification();
        eventID.addNewIdentificationID().setStringValue(id);
        eventID.addNewIdentificationCategoryDescriptionText().setStringValue(description);

        return incident;
    }

    public static UICDSIncidentType addIncidentEventMema(UICDSIncidentType incident, String reason,
            String category, String id, String description, String incidentName) {

            ActivityType event = incident.addNewIncidentEvent();
            event.addNewActivityReasonText().setStringValue(reason);
            event.addNewActivityName().setStringValue(incidentName);
            event.addNewActivityCategoryText().setStringValue(category);

            gov.niem.niem.niemCore.x20.IdentificationType eventID = event.addNewActivityIdentification();
            eventID.addNewIdentificationID().setStringValue(id);
            eventID.addNewIdentificationCategoryDescriptionText().setStringValue(description);

            return incident;
        }
    
    public static ActivityType getIncidentEventByCategoryAndReason(UICDSIncidentType incident,
        String category, String reason) {

        boolean foundDC = false;
        boolean foundCreated = false;

        if (incident != null && incident.sizeOfIncidentEventArray() > 0) {
            for (ActivityType event : incident.getIncidentEventArray()) {
                if (event.sizeOfActivityCategoryTextArray() > 0) {
                    for (TextType categoryText : event.getActivityCategoryTextArray()) {
                        if (categoryText.getStringValue().equalsIgnoreCase(category)) {
                            foundDC = true;
                        }
                    }
                }
                if (event.sizeOfActivityReasonTextArray() > 0) {
                    for (TextType reasonText : event.getActivityReasonTextArray()) {
                        if (reasonText.getStringValue().equalsIgnoreCase(reason)) {
                            foundCreated = true;
                        }
                    }
                }
                if (foundDC && foundCreated) {
                    return event;
                }
            }
        }

        return null;
    }

    public static boolean activityStatusExists(UICDSIncidentType incident, String category,
        String categoryDescription) {

        boolean exists = false;

        // xpath might simplify this esearch
        if (incident.sizeOfActivityStatusArray() > 0) {
            for (StatusType status : incident.getActivityStatusArray()) {
                if (status.sizeOfStatusIssuerIdentificationArray() > 0) {
                    for (gov.niem.niem.niemCore.x20.IdentificationType issuer : status.getStatusIssuerIdentificationArray()) {
                        if (issuer.sizeOfIdentificationCategoryArray() > 0) {
                            for (XmlObject idCategory : issuer.getIdentificationCategoryArray()) {
                                String text = Common.getTextFromAny(idCategory);
                                if (text != null && text.equals(category)) {
                                    if (issuer.getIdentificationCategoryDescriptionTextArray(0) != null) {
                                        if (issuer.getIdentificationCategoryDescriptionTextArray(0).getStringValue().equals(
                                            categoryDescription)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return exists;
    }

}
