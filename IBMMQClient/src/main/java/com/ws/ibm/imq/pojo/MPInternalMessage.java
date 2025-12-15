package com.ws.ibm.imq.pojo;

import org.apache.commons.codec.binary.Hex;

public class MPInternalMessage {
  private byte[] mqMsgId;
  private byte[] mqCorrelationId;
  private String mqReplyToQ;
  private byte[] mqMsgBody;

  /**
      @roseuid 3EDFF803004B
   */
  public MPInternalMessage() {

  }

  public MPInternalMessage(byte[] mqMsgId, byte[] mqCorrelationId,
                           String mqReplyToQ,
                           byte[] mqMsgBody) {
    this.mqMsgId = mqMsgId;
    this.mqCorrelationId = mqCorrelationId;
    this.mqReplyToQ = mqReplyToQ;
    this.mqMsgBody = mqMsgBody;
  }

  public byte[] getMqCorrelationId() {
    return mqCorrelationId;
  }

  public byte[] getMqMsgBody() {
    return mqMsgBody;
  }

  public byte[] getMqMsgId() {
    return mqMsgId;
  }

  public String getMqReplyToQ() {
    return mqReplyToQ;
  }

  public void setMqCorrelationId(byte[] mqCorrelationId) {
    this.mqCorrelationId = mqCorrelationId;
  }

  public void setMqMsgBody(byte[] mqMsgBody) {
    this.mqMsgBody = mqMsgBody;
  }

  public void setMqMsgId(byte[] mqMsgId) {
    this.mqMsgId = mqMsgId;
  }

  public void setMqReplyToQ(String mqReplyToQ) {
    this.mqReplyToQ = mqReplyToQ;
  }

  public String getMqMsgIdAsString(){
    return (mqMsgId != null) ? String.valueOf(Hex.encodeHex(mqMsgId)) : null;
  }

  public String getMqCorrelationIdAsString(){
    return (mqCorrelationId != null) ? String.valueOf(Hex.encodeHex(mqMsgId)) : null;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer("MPInternalMessage: \n");
    buf.append((mqMsgId != null) ? String.valueOf(Hex.encodeHex(mqMsgId)) : null);
    buf.append("\n");
    buf.append((mqCorrelationId != null) ? String.valueOf(Hex.encodeHex(mqCorrelationId)) : null);
    buf.append("\n");
    buf.append(mqReplyToQ);
    buf.append("\n");
    char[] tmp = (mqMsgBody != null) ? Hex.encodeHex(mqMsgBody) : "".toCharArray();
    int i;
    for(i = 0; i+80 < tmp.length; i+=80){
      buf.append(tmp, i, 80);
      buf.append("\n");
    }
    buf.append(tmp, i, tmp.length - i);
    return  buf.toString();
  }

  public void reset(){
    mqMsgId = null;
    mqCorrelationId = null;
    mqReplyToQ = null;
    mqMsgBody = null;
  }
}