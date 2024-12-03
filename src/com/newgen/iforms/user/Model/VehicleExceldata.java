package com.newgen.iforms.user.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VehicleExceldata {

	public VehicleExceldata(String brand, String product, String fullVariant, String modelYear, int tenure,
			int gracePeriod, String description, String trim, double retailPrice, double cashback,
			double purchaseDiscount, double formulaPrice, double financeCharge, double insurance, double totalAmount,
			double downPayment, double financeAmount, double mi, double rv, double rvPercentage, double adminFee,
			double financePercentage, double insur, double totalTermCostRatio, double adminFeeDiscountAmount,
			double freeFirstInstallment, int freeSecondInstallment, int freeThirdInstallment, int freeFourthInstallment,
			int freeFifthInstallment, int freeSixthInstallment, double total, double totalRebateDiscount,
			double difference, double rebatePercentage, double rebateAmount) {
		this.brand = brand;
		this.product = product;
		this.fullVariant = fullVariant;
		this.modelYear = modelYear;
		this.tenure = tenure;
		this.gracePeriod = gracePeriod;
		this.description = description;
		this.trim = trim;
		this.retailPrice = retailPrice;
		this.cashback = cashback;
		this.purchaseDiscount = purchaseDiscount;
		this.formulaPrice = formulaPrice;
		this.financeCharge = financeCharge;
		this.insurance = insurance;
		this.totalAmount = totalAmount;
		this.downPayment = downPayment;
		this.financeAmount = financeAmount;
		this.mi = mi;
		this.rv = rv;
		this.rvPercentage = rvPercentage;
		this.adminFee = adminFee;
		this.financePercentage = financePercentage;
		this.insur = insur;
		this.totalTermCostRatio = totalTermCostRatio;
		this.adminFeeDiscountAmount = adminFeeDiscountAmount;
		this.freeFirstInstallment = freeFirstInstallment;
		this.freeSecondInstallment = freeSecondInstallment;
		this.freeThirdInstallment = freeThirdInstallment;
		this.freeFourthInstallment = freeFourthInstallment;
		this.freeFifthInstallment = freeFifthInstallment;
		this.freeSixthInstallment = freeSixthInstallment;
		this.total = total;
		this.totalRebateDiscount = totalRebateDiscount;
		this.difference = difference;
		this.rebatePercentage = rebatePercentage;
		this.rebateAmount = rebateAmount;
	}

	@JsonProperty("Brand")
	private String brand;

	@JsonProperty("Product")
	private String product;

	@JsonProperty("Model")
	private String model;

	@JsonProperty("Full Variant")
	private String fullVariant;

	@JsonProperty("Model Year")
	private String modelYear;

	@JsonProperty("Tenure")
	private int tenure;

	@JsonProperty("Grace Period")
	private int gracePeriod;

	@JsonProperty("Description")
	private String description;

	@JsonProperty("Trim")
	private String trim;

	@JsonProperty("Retail Price")
	private double retailPrice;

	@JsonProperty("Cashback")
	private double cashback;

	@JsonProperty("Purchase Discount")
	private double purchaseDiscount;

	@JsonProperty("Formula Price")
	private double formulaPrice;

	@JsonProperty("Finance Charge")
	private double financeCharge;

	@JsonProperty("Insurance")
	private double insurance;

	@JsonProperty("Total Amount")
	private double totalAmount;

	@JsonProperty("Down payment")
	private double downPayment;

	@JsonProperty("Finance amount")
	private double financeAmount;

	@JsonProperty("MI")
	private double mi;

	@JsonProperty("RV")
	private double rv;

	@JsonProperty("RV Percentage")
	private double rvPercentage;

	@JsonProperty("Admin Fee")
	private double adminFee;

	@JsonProperty("Finance Percentage")
	private double financePercentage;

	@JsonProperty("%Insur")
	private double insur;

	@JsonProperty("Total term cost ratio")
	private double totalTermCostRatio;

	@JsonProperty("Admin Fee Discount Amount")
	private double adminFeeDiscountAmount;

	@JsonProperty("Free first installment")
	private double freeFirstInstallment;

	@JsonProperty("Free second installment")
	private int freeSecondInstallment;

	@JsonProperty("Free third installment")
	private int freeThirdInstallment;

	@JsonProperty("Free fourth installment")
	private int freeFourthInstallment;

	@JsonProperty("Free fifth installment")
	private int freeFifthInstallment;

	@JsonProperty("Free sixth installment")
	private int freeSixthInstallment;

	@JsonProperty("Total")
	private double total;

	@JsonProperty("Total Rebate Discount")
	private double totalRebateDiscount;

	@JsonProperty("Difference")
	private double difference;

	@JsonProperty("Rebate Percentage")
	private double rebatePercentage;

	@JsonProperty("Rebate Amount")
	private double rebateAmount;

}