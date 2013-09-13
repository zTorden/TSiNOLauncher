package net.minecraft.hopper;

public class PublishRequest {
	public String token;
	public int report_id;

	public PublishRequest(Report report) {
		this.report_id = report.getId();
		this.token = report.getToken();
	}
}
