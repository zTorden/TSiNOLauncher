package net.minecraft.hopper;

import java.util.Map;

public class SubmitRequest {
	public String report;
	public String version;
	public String product;
	public Map<String, String> environment;

	public SubmitRequest(String report, String product, String version,
			Map<String, String> environment) {
		this.report = report;
		this.version = version;
		this.product = product;
		this.environment = environment;
	}
}
