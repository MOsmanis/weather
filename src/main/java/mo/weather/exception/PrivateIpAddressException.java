package mo.weather.exception;

public class PrivateIpAddressException extends Exception {
    public final static String template = "Cannot find location for a private IP address (%s)";

    public PrivateIpAddressException(String ip)
    {
        super(String.format(template, ip));
    }
}
