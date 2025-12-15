//
// 此檔案是由 JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 所產生 
// 請參閱 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 一旦重新編譯來源綱要, 對此檔案所做的任何修改都將會遺失. 
// 產生時間: 2017.03.20 於 12:49:34 PM CST 
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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SysId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Message"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Target" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/&gt;
 *                   &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="DrFlag" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="ValidType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="LongSmsFlag" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="IsMQ" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlRootElement(name = "SMS")
public class SMS {

    @XmlElement(name = "SysId", required = true)
    protected String sysId;
    @XmlElement(name = "Message", required = true)
    protected SMS.Message message;

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
     *     {@link SMS.Message }
     *     
     */
    public SMS.Message getMessage() {
        return message;
    }

    /**
     * 設定 message 特性的值.
     * 
     * @param value
     *     allowed String is
     *     {@link SMS.Message }
     *     
     */
    public void setMessage(SMS.Message value) {
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
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Target" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/&gt;
     *         &lt;element name="Source" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="DrFlag" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="ValidType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="LongSmsFlag" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;element name="IsMQ" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
        "text",
        "language",
        "drFlag",
        "validType",
        "longSmsFlag",
        "isMQ"
    })
    public static class Message {

        @XmlElement(name = "Target", required = true)
        protected List<String> target;
        @XmlElement(name = "Source", required = true)
        protected String source;
        @XmlElement(name = "Text", required = true)
        protected String text;
        @XmlElement(name = "Language", required = true)
        protected String language;
        @XmlElement(name = "DrFlag", required = true)
        protected String drFlag;
        @XmlElement(name = "ValidType", required = true)
        protected String validType;
        @XmlElement(name = "LongSmsFlag", required = true)
        protected String longSmsFlag;
        @XmlElement(name = "IsMQ", required = true)
        protected String isMQ = "false";

        /**
         * Gets the value of the target property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB String.
         * This is why there is not a <CODE>set</CODE> method for the target property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTarget().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Strings of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getTarget() {
            if (target == null) {
                target = new ArrayList<String>();
            }
            return this.target;
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
         * 取得 drFlag 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getDrFlag() {
            return drFlag;
        }

        /**
         * 設定 drFlag 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setDrFlag(String value) {
            this.drFlag = value;
        }

        /**
         * 取得 validType 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getValidType() {
            return validType;
        }

        /**
         * 設定 validType 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setValidType(String value) {
            this.validType = value;
        }
        
        /**
         * 取得 longSmsFlag 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getLongSmsFlag() {
            return longSmsFlag;
        }

        /**
         * 設定 longSmsFlag 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setLongSmsFlag(String value) {
            this.longSmsFlag = value;
        }
        
        /**
         * 取得 isMQ 特性的值.
         * 
         * @return
         *     possible String is
         *     {@link String }
         *     
         */
        public String getIsMQ() {
            return isMQ;
        }

        /**
         * 設定 isMQ 特性的值.
         * 
         * @param value
         *     allowed String is
         *     {@link String }
         *     
         */
        public void setIsMQ(String value) {
            this.isMQ = value;
        }

    }

}
