package com.saic.uicds.clients.util;

import static org.junit.Assert.assertNotNull;
import gov.niem.niem.niemCore.x20.ActivityDateDocument;
import gov.niem.niem.niemCore.x20.AddressFullTextDocument;
import gov.niem.niem.niemCore.x20.DateTimeDocument;
import gov.niem.niem.niemCore.x20.DateType;
import gov.ucore.ucore.x20.ContentMetadataType;
import gov.ucore.ucore.x20.DigestType;
import gov.ucore.ucore.x20.EventType;
import gov.ucore.ucore.x20.IdentifierType;
import gov.ucore.ucore.x20.SimplePropertyType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.xmlbeans.XmlObject;
import org.uicds.incident.IncidentDocument;
import org.uicds.incidentManagementService.ArchiveIncidentResponseDocument;
import org.uicds.incidentManagementService.CloseIncidentResponseDocument;
import org.uicds.incidentManagementService.CreateIncidentResponseDocument;
import org.uicds.incidentManagementService.GetIncidentListResponseDocument;
import org.uicds.incidentManagementService.UpdateIncidentResponseDocument;
import org.uicds.workProductService.GetAssociatedWorkProductListResponseDocument;
import org.uicds.workProductService.WorkProductListDocument.WorkProductList;

import com.saic.precis.x2009.x06.base.DateTimeType;
import com.saic.precis.x2009.x06.base.IdentificationType;
import com.saic.precis.x2009.x06.base.ProcessingStateType;
import com.saic.precis.x2009.x06.base.ProcessingStatusType;
import com.saic.precis.x2009.x06.base.PropertiesType;
import com.saic.precis.x2009.x06.base.StateType;
import com.saic.precis.x2009.x06.structures.WorkProductDocument;
import com.saic.precis.x2009.x06.structures.WorkProductDocument.WorkProduct;
import com.saic.precis.x2009.x06.structures.WorkProductIdentificationDocument;

public class CommonTestUtils {

    public static final String DEFAULT_ORG_PRINCIPAL_OFFICAL_NAME = "John";

    public static final String DEFAULT_ORG_PRINCIPAL_PHONE = "555-5555";

    public static final String DEFAULT_LONGITUDE = "77.0";

    public static final String DEFAULT_LATITIDUE = "30.0";

    public static final String DEFAULT_INCIDENT_ADDRESS = "123 Main St.";

    public static final String DEFAULT_ACTIVITY_NAME = "Fire on Main";

    public static final String DEFAULT_ACTIVITY_CATEGORY = "Fire";

    public static final String DEFAULT_ACTIVITY_DESCRIPTION = "Fire on Main St. at Maple in the old warehouse.";

    public static final String DEFAULT_ORG_NAME = "Organization Name";

    public static final String VALID_PROPERTIES_DATE = "2010-09-24T08:15:41.359-04:00";

    public static final String VALID_ACTIVITY_DATE = "2009-09-24T08:05:00";

    public static final String WP_VERSION_1 = "1";

    public static final String INCIDENT_WP_TYPE = "Incident";

    public static final String WP_CHECKSUM1 = "checksum";

    //public static final String IG_ID1 = "IG-K0154127-f198-402d-a1bf-721a066f497f";
    public static final String IG_ID1 = "IG-f0154127-f198-402d-a1bf-721a066f497f";

    public static final String IG_ID2 = "IG-222222222222222222222222222222222222";

    public static final String IG_ID3 = "IG-333333333333333333333333333333333333";

    public static final String WP_ID1 = "Incident-1";

    public static final String WP_ID2 = "Incident-2";

    public static final String WP_ID3 = "Incident-3";

    public static final String EVENT_ID1 = "Event-1";

    public static final String ACT1 = "act1";

    public static final String LISTENER_ID1 = "LISTENER_ID1";

    public static ProcessingStatusType getAcceptedStatus() {

        ProcessingStatusType status = ProcessingStatusType.Factory.newInstance();
        status.setStatus(com.saic.precis.x2009.x06.base.ProcessingStateType.ACCEPTED);
        return status;
    }

    public static WorkProduct getIncidentWorkProduct(IdentificationType wpid, PropertiesType props,
        DigestType digest, IncidentDocument incident) {

        WorkProductDocument wpDoc = WorkProductDocument.Factory.newInstance();
        WorkProduct wp = wpDoc.addNewWorkProduct();

        Common.setIdentifierElement(
            wp.addNewPackageMetadata().addNewPackageMetadataExtensionAbstract(), wpid);
        Common.setPropertiesElement(
            wp.getPackageMetadata().addNewPackageMetadataExtensionAbstract(), props);
        if (digest != null) {
            Common.setDigestElement(wp.addNewDigestAbstract(), digest);
        }

        if (incident == null) {
            incident = IncidentDocument.Factory.newInstance();
            incident.addNewIncident();
            wp.addNewStructuredPayload().set(incident);
        } else {
            wp.addNewStructuredPayload().set(incident);
        }

        return wp;
    }

    public static WorkProduct getDefaultIncidentWorkProduct() {

        IdentificationType wpid = Common.createWorkProductIdentification(WP_CHECKSUM1, WP_ID1,
            INCIDENT_WP_TYPE, WP_VERSION_1, Common.WORKPRODUCT_ACTIVE);
        PropertiesType props = Common.createWorkProductProperties(IG_ID1, VALID_PROPERTIES_DATE,
            "user", "1", VALID_PROPERTIES_DATE, "user", "codespace", "value");
        DigestType digest = createDefaultIncidentDigest();

        return getIncidentWorkProduct(wpid, props, digest, getDefaultIncident());
    }

    public static WorkProduct getClosedWorkProduct(WorkProduct workProduct) {

        IdentificationType wpid = Common.getIdentificationElement(workProduct);
        wpid.setState(StateType.INACTIVE);
        IncidentDocument incident = Common.getIncidentDocumentFromWorkProduct(workProduct);
        assertNotNull(incident);
        PropertiesType props = Common.getPropertiesElement(workProduct);
        DigestType digest = createDefaultIncidentDigest();
        WorkProduct closedProduct = CommonTestUtils.getIncidentWorkProduct(wpid, props, digest,
            incident);
        return closedProduct;
    }

    public static WorkProduct getEmptyIncidentWorkProduct() {

        IdentificationType wpid = Common.createWorkProductIdentification(WP_CHECKSUM1, WP_ID1,
            INCIDENT_WP_TYPE, WP_VERSION_1, Common.WORKPRODUCT_ACTIVE);
        PropertiesType props = Common.createWorkProductProperties(IG_ID1, VALID_PROPERTIES_DATE,
            "user", "1", VALID_PROPERTIES_DATE, "user", "codespace", "value");
        DigestType digest = createDefaultIncidentDigest();

        return getIncidentWorkProduct(wpid, props, digest, null);
    }

    public static DigestType createDefaultIncidentDigest() {

        DigestType digest = DigestType.Factory.newInstance();
        CommonTestUtils.setEvent(digest, EVENT_ID1, DEFAULT_ACTIVITY_DESCRIPTION,
            DEFAULT_ACTIVITY_NAME, null, null);
        return digest;
    }

    public static void setEvent(DigestType digest, String eventId, String descriptor,
        String identifier, ContentMetadataType metadata, SimplePropertyType property) {

        EventType event = EventType.Factory.newInstance();
        event.setId(eventId);

        // set the Identifier for the Event
        if (identifier != null) {
            IdentifierType id = event.addNewIdentifier();
            id.setStringValue(identifier);
            id.setCodespace("http://niem.gov/niem/niem-core/2.0");
            id.setCode("ActivityName");
        }

        if (descriptor != null) {
            event.addNewDescriptor().setStringValue(descriptor);
        }
        if (metadata != null) {
            event.setMetadata(metadata);
        }
        if (property != null) {
            int n = event.sizeOfSimplePropertyArray();
            event.setSimplePropertyArray(n, property);
        }

        Common.setEvent(event, digest);
    }

    public static IncidentDocument getDefaultIncident() {

        IncidentDocument incident = IncidentDocument.Factory.newInstance();

        incident.addNewIncident();
        incident.getIncident().addNewActivityCategoryText().setStringValue(
            DEFAULT_ACTIVITY_CATEGORY);
        incident.getIncident().addNewActivityName().setStringValue(DEFAULT_ACTIVITY_NAME);
        incident.getIncident().addNewActivityDescriptionText().setStringValue(
            DEFAULT_ACTIVITY_DESCRIPTION);
        incident.getIncident().addNewActivityIdentification().addNewIdentificationID().setStringValue(
            IG_ID1);

        DateTimeDocument dateDoc = DateTimeDocument.Factory.newInstance();
        dateDoc.addNewDateTime().setStringValue(VALID_ACTIVITY_DATE);

        ActivityDateDocument activityDate = ActivityDateDocument.Factory.newInstance();
        activityDate.addNewActivityDate().set(dateDoc);

        Common.substitute(incident.getIncident().addNewActivityDateRepresentation(),
            Common.NIEM_NS, Common.ACTIVITY_DATE, DateType.type, activityDate.getActivityDate());

        AddressFullTextDocument fullAddress = AddressFullTextDocument.Factory.newInstance();
        fullAddress.addNewAddressFullText().setStringValue(DEFAULT_INCIDENT_ADDRESS);
        incident.getIncident().addNewIncidentLocation().addNewLocationAddress().set(fullAddress);

        incident.getIncident().getIncidentLocationArray(0).addNewLocationArea().addNewAreaCircularRegion().set(
            Common.createCircle(DEFAULT_LATITIDUE, DEFAULT_LONGITUDE));

        incident.getIncident().addNewIncidentJurisdictionalOrganization().addNewOrganizationName().setStringValue(
            DEFAULT_ORG_NAME);
        incident.getIncident().getIncidentJurisdictionalOrganizationArray(0).addNewOrganizationPrincipalOfficial().addNewPersonName().addNewPersonFullName().setStringValue(
            DEFAULT_ORG_PRINCIPAL_OFFICAL_NAME);
        return incident;
    }

    public static WorkProduct addIncidentEventToIncidentAndUpdateModifedByAndTime(
        WorkProduct workProduct, String reason, String category, String id, String description,
        String user) {

        IncidentDocument incident = Common.getIncidentDocumentFromWorkProduct(workProduct);
        if (incident == null) {
            incident = IncidentDocument.Factory.newInstance();
        }

        if (incident.getIncident() == null) {
            incident.addNewIncident();
        }

        Common.addIncidentEvent(incident.getIncident(), reason, category, id, description);

        // ActivityType event = incident.getIncident().addNewIncidentEvent();
        // event.addNewActivityIdentification().addNewIdentificationCategoryDescriptionText().setStringValue(
        // IDCategoryDescription);
        // event.addNewActivityReasonText().setStringValue(reason);

        workProduct.getStructuredPayloadArray(0).set(incident);

        // Set the created by and last updated by
        workProduct = setCreatedByAndLastUpdatedBy(workProduct, user);

        return workProduct;
    }

    // public static WorkProduct addWebEOCReceivedIncident(WorkProduct workProduct, String
    // boardname, String dataid) {
    //
    // // Add the specific Activity for WebEOC creating the incident
    // IncidentDocument incident = Common.getIncidentDocumentFromWorkProduct(workProduct);
    // if (incident == null) {
    // incident = IncidentDocument.Factory.newInstance();
    // }
    // if (incident.getIncident() == null) {
    // incident.addNewIncident();
    // }
    // ActivityType event = incident.getIncident().addNewIncidentEvent();
    // event.addNewActivityCategoryText().setStringValue(Constants.WEBEOC_OWNER_NAME);
    //
    // gov.niem.niem.niemCore.x20.IdentificationType eventID = event.addNewActivityIdentification();
    // eventID.addNewIdentificationID().setStringValue(dataid);
    // eventID.addNewIdentificationCategoryDescriptionText().setStringValue(boardname);
    //
    // event.addNewActivityReasonText().setStringValue(Constants.WEBEOC_RECEIVED_REASON);
    // workProduct.getStructuredPayloadArray(0).set(incident);
    //
    // return workProduct;
    // }

    public static final WorkProduct setCreatedByAndLastUpdatedBy(WorkProduct workProduct,
        String user) {

        if (workProduct != null && workProduct.getPackageMetadata() != null
            && workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray() > 0) {

            for (int i = 0; i < workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray(); i++) {
                XmlObject extension = workProduct.getPackageMetadata().getPackageMetadataExtensionAbstractArray(
                    i);
                if (extension instanceof PropertiesType) {
                    PropertiesType props = (PropertiesType) extension;
                    props.getCreatedBy().setStringValue(user);
                    props.getLastUpdatedBy().setStringValue(user);
                    break;
                }
            }
        }
        return workProduct;
    }

    public static final WorkProduct setLastUpdatedTime(WorkProduct workProduct, Calendar calendar) {

        if (workProduct != null && workProduct.getPackageMetadata() != null
            && workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray() > 0) {

            for (int i = 0; i < workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray(); i++) {
                XmlObject extension = workProduct.getPackageMetadata().getPackageMetadataExtensionAbstractArray(
                    i);
                if (extension instanceof PropertiesType) {
                    PropertiesType props = (PropertiesType) extension;
                    DateTimeType dtt = DateTimeType.Factory.newInstance();
                    dtt.setCalendarValue(calendar);
                    props.setLastUpdated(dtt);
                    break;
                }
            }
        }
        return workProduct;
    }

    public static final WorkProduct setIncidentGroupID(WorkProduct workProduct, String id) {

        if (workProduct != null && workProduct.getPackageMetadata() != null
            && workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray() > 0) {

            for (int i = 0; i < workProduct.getPackageMetadata().sizeOfPackageMetadataExtensionAbstractArray(); i++) {
                XmlObject extension = workProduct.getPackageMetadata().getPackageMetadataExtensionAbstractArray(
                    i);
                if (extension instanceof PropertiesType) {
                    PropertiesType props = (PropertiesType) extension;
                    if (props.getAssociatedGroups() == null) {
                        props.addNewAssociatedGroups();
                    }
                    if (props.getAssociatedGroups().sizeOfIdentifierArray() == 0) {
                        props.getAssociatedGroups().addNewIdentifier();
                    }
                    props.getAssociatedGroups().getIdentifierArray(0).setStringValue(id);
                    break;
                }
            }
        }
        return workProduct;
    }

    public static GetIncidentListResponseDocument createEmptyIncidentListResponse() {

        GetIncidentListResponseDocument incidentList = GetIncidentListResponseDocument.Factory.newInstance();
        incidentList.addNewGetIncidentListResponse().addNewWorkProductList();
        return incidentList;
    }

    public static GetIncidentListResponseDocument createIncidentListResponse() {

        GetIncidentListResponseDocument incidentList = GetIncidentListResponseDocument.Factory.newInstance();
        WorkProduct workProduct = incidentList.addNewGetIncidentListResponse().addNewWorkProductList().addNewWorkProduct();
        WorkProductIdentificationDocument wpid = WorkProductIdentificationDocument.Factory.newInstance();
        wpid.addNewWorkProductIdentification().addNewIdentifier().setStringValue(WP_ID1);
        Common.setIdentifierElement(
            workProduct.addNewPackageMetadata().addNewPackageMetadataExtensionAbstract(),
            wpid.getWorkProductIdentification());
        PropertiesType wpprop = PropertiesType.Factory.newInstance();
        wpprop.addNewAssociatedGroups().addNewIdentifier().setStringValue(IG_ID1);
        Common.setPropertiesElement(
            workProduct.getPackageMetadata().addNewPackageMetadataExtensionAbstract(), wpprop);
        return incidentList;
    }

    public static GetAssociatedWorkProductListResponseDocument createAssociatedWorkProductListResponse() {

        WorkProduct incidentWorkProduct = CommonTestUtils.getDefaultIncidentWorkProduct();
        ArrayList<WorkProduct> wpList = new ArrayList<WorkProduct>();
        wpList.add(incidentWorkProduct);
        return createAssociatedWorkProductListResponse(wpList);
    }

    public static GetAssociatedWorkProductListResponseDocument createAssociatedWorkProductListResponse(
        ArrayList<WorkProduct> wpList) {

        GetAssociatedWorkProductListResponseDocument response = GetAssociatedWorkProductListResponseDocument.Factory.newInstance();
        response.addNewGetAssociatedWorkProductListResponse();
        if (wpList.size() > 0) {
            WorkProductList workProductList = response.getGetAssociatedWorkProductListResponse().addNewWorkProductList();
            for (WorkProduct workProduct : wpList) {
                workProductList.addNewWorkProduct().set(workProduct);
            }
        }
        return response;
    }

    public static UpdateIncidentResponseDocument createUpdateIncidentResponse() {

        UpdateIncidentResponseDocument response = UpdateIncidentResponseDocument.Factory.newInstance();
        ProcessingStatusType status = response.addNewUpdateIncidentResponse().addNewWorkProductPublicationResponse().addNewWorkProductProcessingStatus();
        status.setStatus(ProcessingStateType.ACCEPTED);
        WorkProduct workProduct = response.getUpdateIncidentResponse().getWorkProductPublicationResponse().addNewWorkProduct();
        PropertiesType workProductProperties = PropertiesType.Factory.newInstance();
        workProductProperties.addNewAssociatedGroups().addNewIdentifier().setStringValue(IG_ID1);
        Common.setPropertiesElement(
            workProduct.addNewPackageMetadata().addNewPackageMetadataExtensionAbstract(),
            workProductProperties);
        return response;
    }

    public static UpdateIncidentResponseDocument createPendingUpdateIncidentResponse() {

        UpdateIncidentResponseDocument response = UpdateIncidentResponseDocument.Factory.newInstance();
        ProcessingStatusType status = response.addNewUpdateIncidentResponse().addNewWorkProductPublicationResponse().addNewWorkProductProcessingStatus();
        status.setStatus(ProcessingStateType.PENDING);
        status.addNewACT().setStringValue(ACT1);
        return response;
    }

    public static CreateIncidentResponseDocument createCreateIncidentResponse() {

        CreateIncidentResponseDocument response = CreateIncidentResponseDocument.Factory.newInstance();
        ProcessingStatusType status = response.addNewCreateIncidentResponse().addNewWorkProductPublicationResponse().addNewWorkProductProcessingStatus();
        status.setStatus(ProcessingStateType.ACCEPTED);
        WorkProduct workProduct = response.getCreateIncidentResponse().getWorkProductPublicationResponse().addNewWorkProduct();
        PropertiesType workProductProperties = PropertiesType.Factory.newInstance();
        workProductProperties.addNewAssociatedGroups().addNewIdentifier().setStringValue(IG_ID1);
        Common.setPropertiesElement(
            workProduct.addNewPackageMetadata().addNewPackageMetadataExtensionAbstract(),
            workProductProperties);
        return response;
    }

    public static CloseIncidentResponseDocument createCloseIncidentResponse() {

        CloseIncidentResponseDocument response = CloseIncidentResponseDocument.Factory.newInstance();
        ProcessingStatusType status = response.addNewCloseIncidentResponse().addNewWorkProductProcessingStatus();
        status.setStatus(ProcessingStateType.ACCEPTED);
        return response;
    }

    public static ArchiveIncidentResponseDocument createArchiveIncidentResponse() {

        ArchiveIncidentResponseDocument response = ArchiveIncidentResponseDocument.Factory.newInstance();
        ProcessingStatusType status = response.addNewArchiveIncidentResponse().addNewWorkProductProcessingStatus();
        status.setStatus(ProcessingStateType.ACCEPTED);
        return response;
    }

    public static String getDateString(GregorianCalendar cal) {

        if (cal.get(Calendar.SECOND) == 0) {
            cal.set(Calendar.SECOND, 0);
        }
        SimpleDateFormat ISO8601Local = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        TimeZone timeZone = TimeZone.getDefault();
        ISO8601Local.setTimeZone(timeZone);
        return ISO8601Local.format(cal.getTime());
    }

}
