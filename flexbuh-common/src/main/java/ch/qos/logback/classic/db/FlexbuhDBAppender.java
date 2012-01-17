package ch.qos.logback.classic.db;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.complitex.flexbuh.common.logging.EventProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 15:52
 */
public class FlexbuhDBAppender extends DBAppender {

	private static final Logger log = LoggerFactory.getLogger(FlexbuhDBAppender.class);

	private static final String ROOT_PACKAGE = "org.complitex.flexbuh";

	@Override
	Map<String, String> mergePropertyMaps(ILoggingEvent event) {

		Map<String, String> result = super.mergePropertyMaps(event);
		if (event.getArgumentArray() != null) {
			for (Object argument : event.getArgumentArray()) {
				if (argument instanceof EventProperty) {
					EventProperty eventProperty = (EventProperty)argument;
					result.put(eventProperty.getName(), eventProperty.getValue());
				}
			}
		}

		addModuleName(event.getCallerData()[0], result);
		addLogin(result);
		return result;
	}

	private void addModuleName(StackTraceElement callerData, Map<String, String> result) {

		if (callerData == null) {
			return;
		}

		String className = callerData.getClassName();
		if (StringUtils.startsWith(className, ROOT_PACKAGE)) {
			String rest = StringUtils.remove(className, ROOT_PACKAGE);
			String[] packages = StringUtils.split(rest, '.');
			if (packages.length > 0) {
				result.put("module", ROOT_PACKAGE + "." + packages[0]);
			}
		}
	}

	private void addLogin(Map<String, String> result) {
		try {

			if (RequestCycle.get() == null) {
				return;
			}

			WebRequest servletWebRequest = (WebRequest) RequestCycle.get().getRequest();

			if (servletWebRequest == null) {
				return;
			}

			HttpServletRequest servletRequest = (HttpServletRequest) servletWebRequest.getContainerRequest();

			if (servletRequest == null) {
				return;
			}

			if (servletRequest.getUserPrincipal() != null) {
				result.put("login", servletRequest.getUserPrincipal().getName());
			} else {
				result.put("login", "ANONYMOUS");
			}
		} catch (Throwable e) {
			System.out.println("Can not add login to log: " + e.toString());
		}
	}
}