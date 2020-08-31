package domain;

public class GraphVO {
	private String date; //날짜
	private String bias; //매매기준율
	private String persent; //전일 대비
	
	public GraphVO(String date, String bias, String persent) {
		this.date = date;
		this.bias = bias;
		this.persent = persent;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBias() {
		return bias;
	}
	public void setBias(String bias) {
		this.bias = bias;
	}
	public String getPersent() {
		return persent;
	}
	public void setPersent(String persent) {
		this.persent = persent;
	}
	
	@Override
	public String toString() {
		return "graphVO [date=" + date + ", bias=" + bias + ", persent=" + persent + "]";
	}
}
