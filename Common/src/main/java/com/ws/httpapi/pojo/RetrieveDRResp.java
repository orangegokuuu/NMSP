//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2017.10.12 於 02:42:55 PM CST 
//


package com.ws.httpapi.pojo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type 的 Java 類別.
 * 
 * <p>下列綱要片段會指定此類別中包含的預期內容.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DeliveryReport" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SubmitDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resultCode",
    "messageId",
    "status",
    "bNumber",
    "timestamp",
    "deliveryReport"
})
@XmlRootElement(name = "RetrieveDRResp")
public class RetrieveDRResp {

    @XmlElement(name = "ResultCode", required = true)
    protected String resultCode;
    @XmlElement(name = "MessageId", required = true)
    protected String messageId;
    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "BNumber", required = true)
    protected String bNumber;
    @XmlElement(name = "Timestamp", required = true)
    protected String timestamp;
    @XmlElement(name = "DeliveryReport", required = true)
    protected List<RetrieveDRResp.DeliveryReport> deliveryReport;

    /**
     * 取得 resultCode 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * 設定 resultCode 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * 取得 messageId 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * 設定 messageId 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * 取得 status 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * 設定 status 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * 取得 bNumber 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBNumber() {
        return bNumber;
    }

    /**
     * 設定 bNumber 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBNumber(String value) {
        this.bNumber = value;
    }

    /**
     * 取得 timestamp 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 設定 timestamp 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the deliveryReport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deliveryReport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeliveryReport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RetrieveDRResp.DeliveryReport }
     * 
     * 
     */
    public List<RetrieveDRResp.DeliveryReport> getDeliveryReport() {
        if (deliveryReport == null) {
            deliveryReport = new ArrayList<RetrieveDRResp.DeliveryReport>();
        }
        return this.deliveryReport;
    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SubmitDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *       &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id",
        "submitDate",
        "doneDate",
        "state",
        "error"
    })
    public static class DeliveryReport {

        @XmlElement(name = "Id", required = true)
        protected String id;
        @XmlElement(name = "SubmitDate", required = true)
        protected String submitDate;
        @XmlElement(name = "DoneDate", required = true)
        protected String doneDate;
        @XmlElement(name = "State", required = true)
        protected String state;
        @XmlElement(name = "Error", required = true)
        protected String error;
        @XmlAttribute(name = "seq")
        protected String seq;
        @XmlAttribute(name = "total")
        protected String total;

        /**
         * 取得 id 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * 設定 id 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * 取得 submitDate 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubmitDate() {
            return submitDate;
        }

        /**
         * 設定 submitDate 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubmitDate(String value) {
            this.submitDate = value;
        }

        /**
         * 取得 doneDate 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDoneDate() {
            return doneDate;
        }

        /**
         * 設定 doneDate 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDoneDate(String value) {
            this.doneDate = value;
        }

        /**
         * 取得 state 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getState() {
            return state;
        }

        /**
         * 設定 state 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setState(String value) {
            this.state = value;
        }

        /**
         * 取得 error 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getError() {
            return error;
        }

        /**
         * 設定 error 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setError(String value) {
            this.error = value;
        }

        /**
         * 取得 seq 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSeq() {
            return seq;
        }

        /**
         * 設定 seq 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSeq(String value) {
            this.seq = value;
        }

        /**
         * 取得 total 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotal() {
            return total;
        }

        /**
         * 設定 total 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotal(String value) {
            this.total = value;
        }

    }

}
