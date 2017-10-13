/**
 * 
 */
package driver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import log.GenericLog;
import parametrizacao.ParametroUtil;
import report.Report;
import report.ReportXls;

/**
 * Classe que inicializa da automação e define qual browser será usado de acordo com o informado no properties.
 * 
 * @since 04/09/2014
 */

public abstract class AbstractCenario{

	protected static WebDriver webDriver;
	private static final Log LOG = LogFactory.getLog(AbstractCenario.class.getName());
	private static Method casoDeTesteAtual = null;
	private static Method casoDeTestePrincipal = null;
	static Report report = new Report();
	static ReportXls reportXls = new ReportXls();

	@BeforeClass
	public static void setUpClass() {
		synchronized (AbstractCenario.class) {
			WebDriver driver = webDriver;
			if (driver == null) {
				driver = criarWebDriver();
				GenericWebDriverSingleton.getInstance(driver);
			}
			webDriver = GenericWebDriverSingleton.getDriver();
		}
	}
	
	public boolean naox(String valorString){
		if(valorString.equalsIgnoreCase("x"))
			return false;
		else
			return true;            
	}
	
	public static void setDescricaoCasoDeTeste(String descricaoCasoDeTeste) {
		report.setDescricaoCasoDeTeste(descricaoCasoDeTeste);
	}
	
	public static void setPassoPasso(String passoApasso) {
		report.setPassoApasso(passoApasso);
	}
	
	public static void setNomeArquivo(String nomeArquivo) {
		report.setNomeArquivoSaida(nomeArquivo);
	}

	public static Method getCasoDeTestePrincipal() {
		return casoDeTestePrincipal;
	}
	
	public static String getCasoDeTestePrincipalNomeSimples() {
		return casoDeTestePrincipal.getDeclaringClass().getSimpleName();
	}

	public static void setCasoDeTestePrincipal(Method casoDeTestePrincipal) {
		AbstractCenario.casoDeTestePrincipal = casoDeTestePrincipal;
	}

	public static Method getCasoDeTesteAtual() {
		return casoDeTesteAtual;
	}
	
	public static String getCasoDeTesteAtualNomeSimples() {
		if(casoDeTesteAtual == null){
			return "";
		}else{
			return casoDeTesteAtual.getDeclaringClass().getSimpleName();
		}
	}

	public static void setCasoDeTesteAtual(Method casoDeTesteAtual) {
		AbstractCenario.casoDeTesteAtual = casoDeTesteAtual;
	}
	
	public static void addImagemAoRelatorioDeEvidencias(BufferedImage imgEvidencias) {
		if(ParametroUtil.isUtilizaPlanilhaExcel()){
			reportXls.addImagemAoRelatorioDeEvidencias(imgEvidencias);
		}else
			report.addImagemAoRelatorioDeEvidencias(imgEvidencias);
	}

	public static void gerarDocumentosDeEvidencia(String status, boolean erroInesperado) throws Exception {
		if(ParametroUtil.isUtilizaPlanilhaExcel()){
			reportXls.criarDocumentosDeEvidencia(status, erroInesperado);
		}else
			report.criarDocumentosDeEvidencia(status, erroInesperado);
	}

	protected static WebDriver criarWebDriver() {
		try {
			GenericLog.setParametrosIniciaisLog4j();
			if ("chrome".equals(ParametroUtil.getValueAsString("browser"))) {
//				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//				Proxy proxy = new Proxy();
//		        proxy.setHttpProxy("proxyweb.capes.gov.br:8080");
//		        proxy.setSocksUsername("jhonatanp");
//		        proxy.setSocksPassword("123456");
//		        capabilities.setCapability("proxy", proxy);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("chrome.switches","--disable-extensions");
//				System.setProperty("webdriver.chrome.driver",(System.getProperty("user.dir") + 
//					"//src//test//resources//chromedriver_new.exe"));
				File file = new File ("C:/chromedriver.exe");
				System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
				WebDriver driver = new ChromeDriver(options);
				return driver;
			} else if ("ie".equals(ParametroUtil.getValueAsString("browser"))) {
//				String PROXY = "proxyweb.capes.gov.br";
//				org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
//				proxy.setHttpProxy(PROXY)
//				     .setFtpProxy(PROXY)
//				     .setSslProxy(PROXY);
//				proxy.setSocksUsername("rpolicarpo");
//				proxy.setSocksPassword("@indra2015");
				
				DesiredCapabilities capabilities = new DesiredCapabilities();
//				capabilities.setCapability(CapabilityType.PROXY, proxy);
				capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
//				capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
				
				File file = new File ("C:/IEDriverServer_Win32.exe");
				System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
				WebDriver driver = new InternetExplorerDriver(capabilities);
				return driver;
			} else {
				FirefoxProfile prof = new FirefoxProfile();

				//Case:1 - Use this case to set download this code to your browser's default location
				prof.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");

				//Case:2 - Download file to Desktop
				//prof.setPreference("browser.download.folderList", 0);
				//prof.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip");

				//Case:3 - Download to custom folder path. Replace d:\\selenium with your Download Location 
//				prof.setPreference("browser.download.dir","D:\\selenium\\");
//				prof.setPreference("browser.download.folderList", 2);
//				prof.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip");

				WebDriver driver = new FirefoxDriver(prof);
				
				return driver;  //new GenericFirefoxDriver();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void delay() {
		delay(ParametroUtil.getValueAsInteger("esperaImplicita") * 1000);
	}

	public static void delay(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			LOG.error("falha no delay;", e);
		}
	}

}
