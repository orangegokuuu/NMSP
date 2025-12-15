
welcome = {	->
	String hostName;
	try {
		hostName = java.net.InetAddress.getLocalHost().getHostName();
	} catch (java.net.UnknownHostException ignore) {
		hostName = 'localhost';
	}

	String banner = this.class.getResourceAsStream('/banner.txt').text;

	return """
${banner}
Logged into $hostName @ ${new Date()}
""";
}

prompt = { -> return "% "; }