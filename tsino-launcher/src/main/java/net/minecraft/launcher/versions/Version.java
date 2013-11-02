package net.minecraft.launcher.versions;

import java.util.Date;

public abstract interface Version {
	public abstract String getId();

	public abstract ReleaseType getType();

	public abstract Date getUpdatedTime();

	public abstract void setType(ReleaseType paramReleaseType);

	public abstract void setUpdatedTime(Date paramDate);
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.versions.Version JD-Core Version: 0.6.2
 */