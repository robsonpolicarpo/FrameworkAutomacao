/**
 * 
 */
package parametrizacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe que lé o arquivo properties.
 * 
 * @since 04/09/2014
 *
 */
public class ParametroUtil {

	private static final String FILE_NAME = "parametros.properties";

	public static boolean isUtilizaPlanilhaExcel(){
		if(ParametroUtil.getValueAsBoolean("utilizaXls"))
			return true;
		else
			return false;
	}

	public static boolean isGerarPDF(){
		if(ParametroUtil.getValueAsBoolean("gerarPDF"))
			return true;
		else
			return false;
	}

	public static boolean isColetarEvidenciaTesteComoDocumento(){
		if(ParametroUtil.getValueAsString("coletaEvidenciasTeste").equals("documento"))
			return true;
		else
			return false;
	}

	public static boolean isColetarEvidenciaTesteComoPrint(){
		if(ParametroUtil.getValueAsString("coletaEvidenciasTeste").equals("print"))
			return true;
		else
			return false;
	}

	public static boolean isColetarEvidenciaDaAutomacao(){
		return ParametroUtil.getValueAsBoolean("coletaEvidenciaAutomacao");
	}

	public static boolean getValueAsBoolean(String chave) {
		Properties properties = getPropertiesFile();
		String valor = properties.getProperty(chave).toLowerCase().trim();
		if (!"true".equals(valor) && !"false".equals(valor)) {
			throw new IllegalArgumentException("The key \"" + chave
					+ "\" is not boolean.");
		}
		return valor.equals("true");
	}

	public static String getValueAsString(String chave) {
		Properties properties = getPropertiesFile();
		return properties.getProperty(chave).toLowerCase().trim();
	}

	public static int getValueAsInteger(String chave) {
		Properties properties = getPropertiesFile();
		String valor = properties.getProperty(chave).toLowerCase().trim();
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("The key \"" + chave
					+ "\" is not integer.");
		}
	}

	private static Properties getPropertiesFile() {
		Properties propriedade = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(
					new File("./src/main/java/resource").getCanonicalPath() + "/"
							+ FILE_NAME);
			propriedade.load(fis);
			return propriedade;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
