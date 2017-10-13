package report;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import parametrizacao.EvidenciaUtil;
import parametrizacao.ParametroUtil;

import com.aspose.words.jasperreports.AWDocExporter;

import driver.AbstractCenario;

@SuppressWarnings("deprecation")
public class GenericReport {
	protected ArrayList<EvidenciaBean> evidenciaTeste;
	protected ArrayList<ImagemBean> listaImgEvidenciaTeste;
	protected ArrayList<ImagemBean> listaImgEvidenciaAutomacao;
	protected ArrayList<EvidenciaBean> evidenciaAutomacao;
	private HashMap<String, Object> parametros;
	private String passoApasso = "";
	private String descricaoCasoDeTeste;
	private String status;
	private String nomeArquivoSaida;
	private String descricaoDaFalha;
	private String sistema;
	private String modulo;
	private String codProjeto;
	private String casoDeUso;

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getCodProjeto() {
		return codProjeto;
	}

	public void setCodProjeto(String codProjeto) {
		this.codProjeto = codProjeto;
	}

	public String getCasoDeUso() {
		return casoDeUso;
	}

	public void setCasoDeUso(String casoDeUso) {
		this.casoDeUso = casoDeUso;
	}

	public HashMap<String, Object> getParametros() {
		return parametros;
	}

	public void setParametros(HashMap<String, Object> parametros) {
		this.parametros = parametros;
	}

	public String getDescricaoDaFalha() {
		return descricaoDaFalha;
	}

	public void setDescricaoDaFalha(String descricaoDaFalha) {
		this.descricaoDaFalha = descricaoDaFalha;
	}

	public String getNomeArquivoSaida() {
		return nomeArquivoSaida;
	}

	public void setNomeArquivoSaida(String nomeArquivoSaida) {
		this.nomeArquivoSaida = nomeArquivoSaida;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescricaoCasoDeTeste() {
		return descricaoCasoDeTeste;
	}

	public void setDescricaoCasoDeTeste(String descricaoCasoDeTeste) {
		this.descricaoCasoDeTeste = descricaoCasoDeTeste;
	}

	public ArrayList<EvidenciaBean> getEvidenciaTeste() {
		return evidenciaTeste;
	}

	public void setEvidenciaTeste(ArrayList<EvidenciaBean> evidenciaTeste) {
		this.evidenciaTeste = evidenciaTeste;
	}

	public void setItemNaListaEvidenciaTeste(EvidenciaBean evidenciaTeste) {
		if(this.evidenciaTeste == null){
			this.evidenciaTeste = new ArrayList<EvidenciaBean>();
		}
		this.evidenciaTeste.add(evidenciaTeste);
	}

	public ArrayList<ImagemBean> getListaImgEvidenciaTeste() {
		return listaImgEvidenciaTeste;
	}

	public void setListaImgEvidenciaTeste(
			ArrayList<ImagemBean> listaImgEvidenciaTeste) {
		this.listaImgEvidenciaTeste = listaImgEvidenciaTeste;
	}

	public void limparListaImgEvidenciaTeste() {
		listaImgEvidenciaTeste = new ArrayList<ImagemBean>();
	}

	public ArrayList<ImagemBean> getListaImgEvidenciaAutomacao() {
		return listaImgEvidenciaAutomacao;
	}

	public void setListaImgEvidenciaAutomacao(
			ArrayList<ImagemBean> listaImgEvidenciaAutomacao) {
		this.listaImgEvidenciaAutomacao = listaImgEvidenciaAutomacao;
	}

	public ArrayList<EvidenciaBean> getEvidenciaAutomacao() {
		return evidenciaAutomacao;
	}

	public void setEvidenciaAutomacao(
			ArrayList<EvidenciaBean> evidenciaAutomacao) {
		this.evidenciaAutomacao = evidenciaAutomacao;
	}

	public String getPassoApasso() {
		return passoApasso;
	}

	public void setPassoApasso(String passoApasso) {
		this.passoApasso = passoApasso;
	}

	public void addImagemAoRelatorioDeEvidencias(BufferedImage imgEvidencias) {
		if(this.listaImgEvidenciaTeste == null){
			this.listaImgEvidenciaTeste = new ArrayList<ImagemBean>();
		}
		ImagemBean img = new ImagemBean();
		img.setImagem(imgEvidencias);
		listaImgEvidenciaTeste.add(img);
	}

	public void addImgLogEvidencias(BufferedImage imgLogPDF) {
		if(listaImgEvidenciaAutomacao == null){
			listaImgEvidenciaAutomacao = new ArrayList<ImagemBean>();
		}
		ImagemBean img = new ImagemBean();
		img.setImagem(imgLogPDF);
		listaImgEvidenciaAutomacao.add(img);
	}

	public void gerarRelatorios(HashMap<String, Object> parametros) throws Exception {
		String nomeArquivoSaidaTeste = getNomeArquivoSaida();
		if(ParametroUtil.isColetarEvidenciaDaAutomacao()){
			nomeArquivoSaidaTeste = nomeArquivoSaidaTeste.replace("Evidencia", "LogAutomacao");
			gerarEvidenciaAutomacaoPDF(evidenciaAutomacao, parametros, nomeArquivoSaidaTeste);
		}
		if(ParametroUtil.isColetarEvidenciaTesteComoDocumento()){
			String tipoRelatorioTeste =  ParametroUtil.getValueAsString("tipoRelatorio");
			nomeArquivoSaidaTeste = getNomeArquivoSaida();
			if(tipoRelatorioTeste.equalsIgnoreCase("pdf")){
				parametros.replace("ARQUIVO", nomeArquivoSaidaTeste.concat(".pdf"));
				gerarEvidenciaTestePDF(evidenciaTeste, parametros, nomeArquivoSaidaTeste);
			}else if(tipoRelatorioTeste.equalsIgnoreCase("docx")){
				parametros.replace("ARQUIVO", nomeArquivoSaidaTeste.concat(".docx"));
				gerarEvidenciaTesteDOCX(evidenciaTeste, parametros, nomeArquivoSaidaTeste);
			}
			else if(tipoRelatorioTeste.equalsIgnoreCase("doc")){
				parametros.replace("ARQUIVO", nomeArquivoSaidaTeste.concat(".doc"));
				gerarEvidenciaTesteDOC(evidenciaTeste, parametros, nomeArquivoSaidaTeste);
			}
		}
	}

	public void gerarEvidenciaAutomacaoPDF(ArrayList<EvidenciaBean> lista, 
			HashMap<String, Object> parametros, String nomeArquivoPDF){
		try {
			System.out.println("Gerando evidencia da automacao...");
			long start = System.currentTimeMillis();
			JasperReport report = JasperCompileManager
					.compileReport(EvidenciaUtil.getValueAsString("pathIReportAutomacao"));
			JasperPrint print = JasperFillManager.fillReport(report, parametros,
					new JRBeanCollectionDataSource(lista));
			JasperExportManager.exportReportToPdfFile(print,
					"evidencia/" + nomeArquivoPDF + ".pdf");
			System.out.println(nomeArquivoPDF + ".pdf" + " gerado em: " 
					+ (System.currentTimeMillis() - start)/1000 + " segundos.");
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	public void gerarEvidenciaTestePDF(List<EvidenciaBean> lista, 
			HashMap<String, Object> parametros, String nomeArquivoPDF){
		try {
			System.out.println("Gerando evidencia de teste...");
			long start = System.currentTimeMillis();
			JasperReport report = JasperCompileManager
					.compileReport(EvidenciaUtil.getValueAsString("pathIReportTestePDF"));
			JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(lista);
			JasperPrint print = JasperFillManager.
					fillReport(report, parametros, beanColDataSource);
			JasperExportManager.exportReportToPdfFile(print,
					"evidencia/" + nomeArquivoPDF);
			System.out.println(nomeArquivoPDF + ".pdf" + " gerado em: " 
					+ (System.currentTimeMillis() - start)/1000 + " segundos.");
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	public void gerarEvidenciaTesteDOCX(List<EvidenciaBean> lista, 
			HashMap<String, Object> parametros, String nomeArquivo){
		try {
			System.out.println("Gerando evidencia de teste...");
			long start = System.currentTimeMillis();
			File file = new File("evidencia/" + nomeArquivo); 
			file.createNewFile(); 
			OutputStream out = new FileOutputStream(file); 
			JasperReport report = JasperCompileManager
					.compileReport(EvidenciaUtil.getValueAsString("pathIReportTesteDOCX"));
			JasperPrint print = JasperFillManager.fillReport(report, parametros,
					new JRBeanCollectionDataSource(lista));
			JRDocxExporter exporter = new JRDocxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			System.out.println("Gerado em: " + (System.currentTimeMillis() - start)/1000 + " segundos.");
		} catch (JRException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gerarEvidenciaTesteDOC(ArrayList<EvidenciaBean> lista, 
			HashMap<String, Object> parametros, String nomeArquivoSaida){
		try {
			System.out.println("Gerando evidencia de teste...");
			long start = System.currentTimeMillis();
			File file = new File("evidencia/" + nomeArquivoSaida); 
			file.createNewFile(); 
			OutputStream out = new FileOutputStream(file); 
			JasperReport report = JasperCompileManager
					.compileReport(EvidenciaUtil.getValueAsString("pathIReportTesteDOC"));
			JasperPrint print = JasperFillManager.fillReport(report, parametros,
					new JRBeanCollectionDataSource(lista));
			AWDocExporter exporter = new AWDocExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			System.out.println("Gerado em: " + ((System.currentTimeMillis() - start)/1000) + " segundos.");
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void coletarPrintAutomacao() throws IOException {
		if(!AbstractCenario.getCasoDeTesteAtual().getDeclaringClass().getSimpleName().contains("Auxiliar")){
			try{
				BufferedImage bi = new Robot().createScreenCapture(new Rectangle(
						Toolkit.getDefaultToolkit().getScreenSize()));
				addImgLogEvidencias(bi); 
			}
			catch(Exception e ) {
				e.printStackTrace();
			}
		}
	}

}
