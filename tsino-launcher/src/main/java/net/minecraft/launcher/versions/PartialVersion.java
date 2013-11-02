package net.minecraft.launcher.versions;

import java.util.Date;

public class PartialVersion implements Version {
	private String id;
	private Date time;
	private ReleaseType type;

	public PartialVersion() {
	}

	public PartialVersion(String id, Date updateTime,
			ReleaseType type) {
		if ((id == null) || (id.length() == 0))
			throw new IllegalArgumentException("ID cannot be null or empty");
		if (updateTime == null)
			throw new IllegalArgumentException("Update time cannot be null");
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		this.id = id;
		this.time = updateTime;
		this.type = type;
	}

	public PartialVersion(Version version) {
		this(version.getId(), version
				.getUpdatedTime(), version.getType());
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public ReleaseType getType() {
		return this.type;
	}

	@Override
	public Date getUpdatedTime() {
		return this.time;
	}

	@Override
	public void setType(ReleaseType type) {
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		this.type = type;
	}

	@Override
	public void setUpdatedTime(Date time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");
		this.time = time;
	}

	@Override
	public String toString() {
		return "PartialVersion{id='" + this.id + '\'' + ", updateTime="
				+ this.time + ", type=" + this.type + '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.versions.PartialVersion JD-Core Version: 0.6.2
 */