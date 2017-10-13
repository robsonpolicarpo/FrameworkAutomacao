package report;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import driver.AbstractCenario;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import parametrizacao.EvidenciaUtil;
import parametrizacao.ParametroUtil;
import util.UtilTxt;
import verificacao.Verify;

public class Report extends GenericReport {

	public void criarDocumentosDeEvidencia(String status, boolean erroInesperado) throws Exception {
		if(ParametroUtil.isColetarEvidenciaDaAutomacao() || ParametroUtil.isColetarEvidenciaTesteComoDocumento()){
			incluirImagensParametrosNaListaDeDocumentos(status);
			if(verificarMomentoDeGerarRelatorio(erroInesperado)){
				gerarMapaComParametrosIreport();
				gerarRelatorios(getParametros());
			}
		}
	}

	private boolean verificarMomentoDeGerarRelatorio(boolean erroInesperado) {
		return AbstractCenario.getCasoDeTesteAtualNomeSimples().equals(
					AbstractCenario.getCasoDeTestePrincipalNomeSimples())
					|| erroInesperado;
	}

	private boolean isCasoDeTesteAtualContemLogin() {
		return AbstractCenario.getCasoDeTesteAtualNomeSimples().contains("Login");
	}

	private void incluirImagensParametrosNaListaDeDocumentos(String status) throws Exception {
		if(isCasoDeTesteAtualContemLogin()){
			setStatus(status);
			setDescricaoCasoDeTeste("Caso de Teste Login - Solicitar login");
			setPassoApasso("Realizar login no sistema.");
			setDescricaoDaFalha(gerarDescricaoDaFalha());
			addListaEvidenciaAutomacao();
			UtilTxt.limparLog();
		}
		else if(!AbstractCenario.getCasoDeTesteAtualNomeSimples().contains("Suite")
				&& !AbstractCenario.getCasoDeTesteAtualNomeSimples().contains("Auxiliar")){
			setStatus(status);
			setDescricaoCasoDeTeste(gerarDescricaoCasoDeTeste());
			setDescricaoDaFalha(gerarDescricaoDaFalha());
			if(ParametroUtil.isColetarEvidenciaDaAutomacao()){
				addListaEvidenciaAutomacao();
			}
			if(ParametroUtil.isColetarEvidenciaTesteComoDocumento()){
				addListaEvidenciaTeste();
			}
			Verify.limparListaDeErro();
			UtilTxt.limparLog();
		}
	}

	private String gerarDescricaoDaFalha() {
		if(getStatus().equals("FALHA")){
			return Verify.getErro();
		}else{
			return "";
		}
	}

	private String gerarDescricaoCasoDeTeste() throws Exception {
		StringBuilder descricaoCasoDeTeste = new StringBuilder();
		String desc = AbstractCenario.getCasoDeTesteAtualNomeSimples();
		desc = desc.replace("_", " ");
		desc = desc.replace("Caso", "Caso de");
		descricaoCasoDeTeste.append(desc);
		descricaoCasoDeTeste.append(" - ");
		descricaoCasoDeTeste.append(getDescricaoCasoDeTeste());
		return descricaoCasoDeTeste.toString();
	}

	private void addListaEvidenciaAutomacao() throws IOException {
		coletarPrintAutomacao();
		EvidenciaBean evidencia = new EvidenciaBean(
				getPassoApasso(),
				getDescricaoCasoDeTeste(), 
				getStatus(), 
				getListaImgEvidenciaAutomacao(), 
				getDescricaoDaFalha());
		evidencia.setLog(UtilTxt.LerLog());
		if(evidenciaAutomacao == null){
			evidenciaAutomacao = new ArrayList<EvidenciaBean>();
		}
		evidenciaAutomacao.add(evidencia);
		listaImgEvidenciaAutomacao = new ArrayList<ImagemBean>();
	}

	private void addListaEvidenciaTeste() {
		EvidenciaBean evidencia = new EvidenciaBean(
				getPassoApasso(),
				getDescricaoCasoDeTeste(), 
				getStatus(), 
				getListaImgEvidenciaTeste(), 
				getDescricaoDaFalha());
		setItemNaListaEvidenciaTeste(evidencia);
		limparListaImgEvidenciaTeste();
	}

	private void gerarMapaComParametrosIreport() throws Exception {
		String nomeArquivo = getNomeArquivoSaida();
		if (nomeArquivo == null || nomeArquivo.equals("")){
			throw new Exception("Arquivo de evidências não gerado pois seu nome não foi informado!");
		}else{
			String nomeArquivoSaidaAutomacao = nomeArquivo.replace("Evidencia", "LogAutomacao");
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			String sistema = EvidenciaUtil.getValueAsString("sistema"); 
	        String subSistema = EvidenciaUtil.getValueAsString("subSistema"); 
	        String modulo = EvidenciaUtil.getValueAsString("modulo"); 
	        String projeto = EvidenciaUtil.getValueAsString("projeto"); 
	        String requisito = EvidenciaUtil.getValueAsString("requisito"); 
	        String elemento = EvidenciaUtil.getValueAsString("elemento"); 
	        String elaborado = EvidenciaUtil.getValueAsString("elaborado");
			parametros.put("SISTEMA", sistema);
			parametros.put("SUBSISTEMA", subSistema);
			parametros.put("MODULO", modulo);
			parametros.put("PROJETO", projeto);
			parametros.put("REQUISITO_UC", requisito);
			parametros.put("ELEMENTO_SOB_TESTE", elemento);
			parametros.put("ELABORADO_POR", elaborado);
			parametros.put("ARQUIVO", nomeArquivoSaidaAutomacao + ".pdf");
			try {
				BufferedImage bi = ImageIO.read( new File(EvidenciaUtil.getValueAsString("logoEmpresa")));
				parametros.put("LOGO_CLIENTE", bi);
			} catch (IOException e) {
				throw new Exception("Imagem do logo do relatório não encontrada!");
			}
			JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(20, "C:\\temp");
			parametros.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);
			setParametros(parametros);
		}
	}

}
