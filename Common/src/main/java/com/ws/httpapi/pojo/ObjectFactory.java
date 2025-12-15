//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2017.03.20 於 12:49:34 PM CST 
//


package com.ws.httpapi.pojo;

import javax.xml.bind.annotation.XmlRegistry;

import com.ws.pojo.GenericBean;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory extends GenericBean {


    /**
	 * 
	 */
	private static final long serialVersionUID = 3546335047189709363L;

	/**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SMS }
     * 
     */
    public SMS createSMS() {
        return new SMS();
    }

    /**
     * Create an instance of {@link RetrieveDRResp }
     * 
     */
    public RetrieveDRResp createRetrieveDRResp() {
        return new RetrieveDRResp();
    }

    /**
     * Create an instance of {@link QueryDRResp }
     * 
     */
    public QueryDRResp createQueryDRResp() {
        return new QueryDRResp();
    }

    /**
     * Create an instance of {@link PushDR }
     * 
     */
    public PushDR createPushDR() {
        return new PushDR();
    }

    /**
     * Create an instance of {@link DeliverSM }
     * 
     */
    public DeliverSM createDeliverSM() {
        return new DeliverSM();
    }

    /**
     * Create an instance of {@link QueryDRResp.Message }
     * 
     */
    public QueryDRResp.Message createQueryDRRespMessage() {
        return new QueryDRResp.Message();
    }

    /**
     * Create an instance of {@link SMS.Message }
     * 
     */
    public SMS.Message createSMSMessage() {
        return new SMS.Message();
    }

    /**
     * Create an instance of {@link SMSResp }
     * 
     */
    public SMSResp createSMSResp() {
        return new SMSResp();
    }

    /**
     * Create an instance of {@link RetrieveDR }
     * 
     */
    public RetrieveDR createRetrieveDR() {
        return new RetrieveDR();
    }

    /**
     * Create an instance of {@link RetrieveDRResp.DeliveryReport }
     * 
     */
    public RetrieveDRResp.DeliveryReport createRetrieveDRRespDeliveryReport() {
        return new RetrieveDRResp.DeliveryReport();
    }

    /**
     * Create an instance of {@link QueryDR }
     * 
     */
    public QueryDR createQueryDR() {
        return new QueryDR();
    }

    /**
     * Create an instance of {@link PushDR.DeliveryReport }
     * 
     */
    public PushDR.DeliveryReport createPushDRDeliveryReport() {
        return new PushDR.DeliveryReport();
    }

    /**
     * Create an instance of {@link PushDRResp }
     * 
     */
    public PushDRResp createPushDRResp() {
        return new PushDRResp();
    }

    /**
     * Create an instance of {@link DeliverSM.Message }
     * 
     */
    public DeliverSM.Message createDeliverSMMessage() {
        return new DeliverSM.Message();
    }

    /**
     * Create an instance of {@link DeliverSMResp }
     * 
     */
    public DeliverSMResp createDeliverSMResp() {
        return new DeliverSMResp();
    }

    /**
     * Create an instance of {@link QueryDRResp.Message.DeliveryReport }
     * 
     */
    public QueryDRResp.Message.DeliveryReport createQueryDRRespMessageDeliveryReport() {
        return new QueryDRResp.Message.DeliveryReport();
    }

    /**
     * Create an instance of {@link BatchRetrieveDRResp }
     * 
     */
    public BatchRetrieveDRResp createBatchRetrieveDRResp() {
        return new BatchRetrieveDRResp();
    }

    /**
     * Create an instance of {@link QueryDRResp.Message.DRDetail }
     * 
     */
    public QueryDRResp.Message.DRDetail createQueryDRRespMessageDRDetail() {
        return new QueryDRResp.Message.DRDetail();
    }

    /**
     * Create an instance of {@link PushDR.DRDetail }
     * 
     */
    public PushDR.DRDetail createPushDRDRDetail() {
        return new PushDR.DRDetail();
    }

    /**
     * Create an instance of {@link BatchRetrieveDR }
     * 
     */
    public BatchRetrieveDR createBatchRetrieveDR() {
        return new BatchRetrieveDR();
    }

    /**
     * Create an instance of {@link BatchRetrieveDRResp.DeliveryReport }
     * 
     */
    public BatchRetrieveDRResp.DeliveryReport createBatchRetrieveDRRespDeliveryReport() {
        return new BatchRetrieveDRResp.DeliveryReport();
    }

    /**
     * Create an instance of {@link QueryDRResp.Message.DRDetail.Detail }
     * 
     */
    public QueryDRResp.Message.DRDetail.Detail createQueryDRRespMessageDRDetailDetail() {
        return new QueryDRResp.Message.DRDetail.Detail();
    }

    /**
     * Create an instance of {@link PushDR.DRDetail.Detail }
     * 
     */
    public PushDR.DRDetail.Detail createPushDRDRDetailDetail() {
        return new PushDR.DRDetail.Detail();
    }

}
