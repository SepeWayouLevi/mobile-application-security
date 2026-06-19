package com.example.forms.models;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Demand implements Serializable {
    @SerializedName("idDemand")
    private Long id;
    @SerializedName("typeOfReference")
    private String typeOfReference;
    @SerializedName("requesterName")
    private String requesterName;


    @SerializedName("productLine")
    private String productLine;

    @SerializedName("typeOfArticle")
    private String typeOfArticle;

    @SerializedName("productClassificationDescription")
    private String productClassificationDescription;

    @SerializedName("catalogPrice")
    private float priceCatalog;


    @SerializedName("pricingValidation")
    private String pricingValidation;

    @SerializedName("purchaseValidation")
    private String purchaseValidation;

    @SerializedName("regulatoryAffairsValidation")
    private String affRegValidation;

    @SerializedName("marking")
    private boolean marking;

    @SerializedName("typeOfMarking")
    private String typeOfMarking;
    @SerializedName("idMarking")
    private Integer idMarking;
    private boolean isSend =  false;

    private String email;

    @SerializedName("statusId")
    private String statusID;

    public Demand(){}

    public Demand(String type_de_reference,
                  String requesterName,
                  String productLine,
                  String typeOfArticle,
                  String productClassificationDescription,
                  float priceCatalog,
                  boolean marking,
                  String typeOfMarking,
                  String email,
                  String statusID
    ) {
        this.typeOfReference = type_de_reference;
        this.requesterName = requesterName;
        this.productLine = productLine;
        this.typeOfArticle = typeOfArticle;
        this.productClassificationDescription = productClassificationDescription;
        this.priceCatalog = priceCatalog;
        this.marking = marking;
        this.typeOfMarking = typeOfMarking;
        this.email = email;
        this.statusID =  statusID;
    }

    // Getters

    public String getTypeOfReference() {
        return typeOfReference != null ? typeOfReference : "Référence non disponible";
    }

    public String getRequesterName() {
       return requesterName != null ? requesterName : "Demandeur non disponible";
    }

    public String getProductLine() {
        return productLine != null ? productLine : "Gamme non disponible";
    }

    public String getTypeOfArticle() {
        return typeOfArticle != null ? typeOfArticle : "Article non disponible";
    }


    public void setTypeOfReference(String typeOfReference) {
        this.typeOfReference = typeOfReference;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    //setters
    public void setTypeOfArticle(String typeOfArticle) {
        this.typeOfArticle = typeOfArticle;
    }



    public float getPriceCatalog() {
        return priceCatalog;
    }

    public void setPriceCatalog(float priceCatalog) {
        this.priceCatalog = priceCatalog;
    }

    public String getProductClassificationDescription() {
        return productClassificationDescription;
    }

    public void setProductClassificationDescription(String productClassificationDescription) {
        this.productClassificationDescription = productClassificationDescription;
    }




    public String getPricingValidation() {
        return pricingValidation;
    }

    public void setPricingValidation(String pricingValidation) {
        this.pricingValidation = pricingValidation;
    }


    public boolean isMarking() {
        return marking;
    }

    public void setMarking(boolean marking) {
        this.marking = marking;
    }

    public String getTypeOfMarking() {
        return typeOfMarking;
    }

    public String getPurchaseValidation() {
        return purchaseValidation;
    }

    public void setPurchaseValidation(String purchaseValidation) {
        this.purchaseValidation = purchaseValidation;
    }

    public void setTypeOfMarking(String typeOfMarking) {
        this.typeOfMarking = typeOfMarking;

    }

    public String setMarkingType(String typeOfMarking){
        this.typeOfMarking = typeOfMarking;
        return typeOfMarking;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdMarking() {
        return idMarking;
    }

    public void setIdMarking(Integer idMarking) {
        this.idMarking = idMarking;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getAffRegValidation() {
        return affRegValidation;
    }

    public void setAffRegValidation(String affRegValidation) {
        this.affRegValidation = affRegValidation;
    }
}