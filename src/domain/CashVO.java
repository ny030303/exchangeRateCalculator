package domain;

public class CashVO {
	private String name; //통화명
	private String bias; //매매기준율
	private String salePrice; //팔때 가격
	private String buyPrice; //구매 가격
	private String urlLink; // 링크

	public CashVO(String name, String bias, String salePrice, String buyPrice, String urlLink) {
		this.name = name;
		this.bias = bias;
		this.salePrice = salePrice;
		this.buyPrice = buyPrice;
		this.urlLink = urlLink;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBias() {
		return bias;
	}
	public void setBias(String bias) {
		this.bias = bias;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(String buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
	}

	@Override
	public String toString() {
		return "CashVO [name=" + name + ", bias=" + bias + ", salePrice=" + salePrice + ", buyPrice=" + buyPrice
				+ ", urlLink=" + urlLink + "]";
	}
	
	
}
