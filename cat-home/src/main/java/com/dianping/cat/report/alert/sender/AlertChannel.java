package com.dianping.cat.report.alert.sender;

public enum AlertChannel {

	MAIL("mail"),

//	SMTPMAIL("smtpmail"),  // gaikuo 加入mail server的smtp配置

	SMS("sms"),

	WEIXIN("weixin");

	private String m_name;

	public static AlertChannel findByName(String name) {
		for (AlertChannel channel : values()) {
			if (channel.getName().equals(name)) {
				return channel;
			}
		}
		return null;
	}

	private AlertChannel(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}
}
