/**
 * 
 */
package log;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Classe que inicializa o Log e configura o arquivo .log
 * 
 * @since 01/10/2016
 */

public class GenericLog {

	private Logger log = Logger.getLogger("Inicializando");
	private static Logger logStatico = Logger.getLogger("Inicializando");

	public static void setParametrosIniciaisLog4j() {
		try {
			DOMConfigurator.configure("./src/main/java/util/log4j.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void infor(String message) {
		logStatico.info(message);
	}
	
	public void info(String message) {
		log.info(message);
	}

	public void warn(String message) {
		log.warn(message);
	}

	public void error(String message) {
		log.error(message);
	}

	public void fatal(String message) {
		log.fatal(message);
	}

	public void debug(String message) {
		log.debug(message);
	}
}

