//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2017.06.19 於 11:45:04 AM CST 
//


package com.ws.httpapi.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="BNumber" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="DeliveryReport">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="SubmitDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DRDetail" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Detail">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SeqNo" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                             &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
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
    "messageId",
    "status",
    "bNumber",
    "timestamp",
    "deliveryReport",
    "drDetail"
})
@XmlRootElement(name = "PushDR")
public class PushDR {

    @XmlElement(name = "MessageId", required = true)
    protected String messageId;
    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "BNumber", required = true)
    protected String bNumber;
    @XmlElement(name = "Timestamp", required = true)
    protected String timestamp;
    @XmlElement(name = "DeliveryReport", required = true)
    protected PushDR.DeliveryReport deliveryReport;
    @XmlElement(name = "DRDetail")
    protected PushDR.DRDetail drDetail;

    /**
     * 取得 messageId 特性的值.
     * 
     * @return
     *     possible String is
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
     *     allowed String is
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
     *     possible String is
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
     *     allowed String is
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
     *     possible String is
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
     *     allowed String is
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
     *     possible String is
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
     *     allowed String is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * 取得 deliveryReport 特性的值.
     * 
     * @return
     *     possible String is
     *     {@link PushDR.DeliveryReport }
     *     
     */
    public PushDR.DeliveryReport getDeliveryReport() {
        return deliveryReport;
    }

    /**
     * 設定 deliveryReport 特性的值.
     * 
     * @param value
     *     allowed String is
     *     {@link PushDR.DeliveryReport }
     *     
     */
    public void setDeliveryReport(PushDR.DeliveryReport value) {
        this.deliveryReport = value;
    }

    /**
     * 取得 drDetail 特性的值.
     * 
     * @return
     *     possible String is
     *     {@link PushDR.DRDetail }
     *     
     */
    public PushDR.DRDetail getDRDetail() {
        return drDetail;
    }

    /**
     * 設定 drDetail 特性的值.
     * 
     * @param value
     *     allowed String is
     *     {@link PushDR.DRDetail }
     *     
     */
    public void setDRDetail(PushDR.DRDetail value) {
        this.drDetail = value;
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
     *         &lt;element name="Detail">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SeqNo" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                   &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                 &lt;/sequence>
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
        "detail"
    })
    public static class DRDetail {

    	@XmlElement(name = "Detail", required = true)
        protected List<PushDR.DRDetail.Detail> detail;

        /**
         * Gets the value of the detail property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detail property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetail().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PushDR.DRDetail.Detail }
         * 
         * 
         */
        public List<PushDR.DRDetail.Detail> getDetail() {
            if (detail == null) {
                detail = new ArrayList<PushDR.DRDetail.Detail>();
            }
            return this.detail;
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
         *         &lt;element name="SeqNo" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
         *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
            "seqNo",
            "doneDate",
            "state"
        })
        public static class Detail {

            @XmlElement(name = "SeqNo", required = true)
            protected String seqNo;
            @XmlElement(name = "DoneDate", required = true)
            protected String doneDate;
            @XmlElement(name = "State", required = true)
            protected String state;

            /**
             * 取得 seqNo 特性的值.
             * 
             * @return
             *     possible String is
             *     {@link String }
             *     
             */
            public String getSeqNo() {
                return seqNo;
            }

            /**
             * 設定 seqNo 特性的值.
             * 
             * @param value
             *     allowed String is
             *     {@link String }
             *     
             */
            public void setSeqNo(String value) {
                this.seqNo = value;
            }

            /**
             * 取得 doneDate 特性的值.
             * 
             * @return
             *     possible String is
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
             *     allowed String is
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
             *     possible String is
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
             *     allowed String is
             *     {@link String }
             *     
             */
            public void setState(String value) {
                this.state = value;
            }

        }

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
     *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="SubmitDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="DoneDate" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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

        /**
         * 取得 id 特性的值.
         * 
         * @return
         *     possible String is
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
         *     allowed String is
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
         *     possible String is
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
         *     allowed String is
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
         *     possible String is
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
         *     allowed String is
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
         *     possible String is
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
         *     allowed String is
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
         *     possible String is
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
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setError(String value) {
            this.error = value;
        }

    }

}
