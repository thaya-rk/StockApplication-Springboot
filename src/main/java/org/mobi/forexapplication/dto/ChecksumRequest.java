package org.mobi.forexapplication.dto;

public class ChecksumRequest {
    private String amount;
    private String sellerOrderNo;
    private String subMID;
    private String mid; // now used as param1
    private String tid; // now used as param2

    // Getters and setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getSellerOrderNo() { return sellerOrderNo; }
    public void setSellerOrderNo(String sellerOrderNo) { this.sellerOrderNo = sellerOrderNo; }

    public String getSubMID() { return subMID; }
    public void setSubMID(String subMID) { this.subMID = subMID; }

    public String getMid() { return mid; }
    public void setMid(String mid) { this.mid = mid; }

    public String getTid() { return tid; }
    public void setTid(String tid) { this.tid = tid; }
}
