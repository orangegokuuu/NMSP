//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2017.03.20 於 12:49:34 PM CST 
//


package com.ws.httpapi.pojo;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SysId" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="Message"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Target" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                   &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                   &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                   &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                   &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sysId",
    "message"
})
@XmlRootElement(name = "DeliverSM")
public class DeliverSM {

    @XmlElement(name = "SysId", required = true)
    protected String sysId;
    @XmlElement(name = "Message", required = true)
    protected DeliverSM.Message message;

    /**
     * 取得 sysId 特性的值.
     * 
     * @return
     *     possible String is
     *     {@link String }
     *     
     */
    public String getSysId() {
        return sysId;
    }

    /**
     * 設定 sysId 特性的值.
     * 
     * @param value
     *     allowed String is
     *     {@link String }
     *     
     */
    public void setSysId(String value) {
        this.sysId = value;
    }

    /**
     * 取得 message 特性的值.
     * 
     * @return
     *     possible String is
     *     {@link DeliverSM.Message }
     *     
     */
    public DeliverSM.Message getMessage() {
        return message;
    }

    /**
     * 設定 message 特性的值.
     * 
     * @param value
     *     allowed String is
     *     {@link DeliverSM.Message }
     *     
     */
    public void setMessage(DeliverSM.Message value) {
        this.message = value;
    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Target" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
     *         &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
     *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
     *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
     *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "target",
        "source",
        "language",
        "text",
        "timestamp"
    })
    public static class Message {

        @XmlElement(name = "Target", required = true)
        protected String target;
        @XmlElement(name = "Source", required = true)
        protected String source;
        @XmlElement(name = "Language", required = true)
        protected String language;
        @XmlElement(name = "Text", required = true)
        protected String text;
        @XmlElement(name = "Timestamp", required = true)
        protected String timestamp;

        /**
         * 取得 target 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getTarget() {
            return target;
        }

        /**
         * 設定 target 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setTarget(String value) {
            this.target = value;
        }

        /**
         * 取得 source 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getSource() {
            return source;
        }

        /**
         * 設定 source 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setSource(String value) {
            this.source = value;
        }

        /**
         * 取得 language 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getLanguage() {
            return language;
        }

        /**
         * 設定 language 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setLanguage(String value) {
            this.language = value;
        }

        /**
         * 取得 text 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getText() {
            return text;
        }

        /**
         * 設定 text 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setText(String value) {
            this.text = value;
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

    }

}
