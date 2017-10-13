/**
 * 
 */
package report;

import java.io.IOException;
import java.util.ArrayList;

import driver.AbstractCenario;
import parametrizacao.ParametroUtil;
import util.UtilTxt;
import verificacao.Verify;

public class ReportXls extends GenericReport {

//	private UtilExcel xls = new UtilExcel();

	public void criarDocumentosDeEvidencia(String status, boolean erroInesperado) throws Exception {
		if(ParametroUtil.isColetarEvidenciaDaAutomacao() || ParametroUtil.isColetarEvidenciaTesteComoDocumento()){
			incluirImagensParametrosNaListaDeDocumentos(status);
			if(verificarMomentoDeGerarRelatorio(erroInesperado)){
//				gerarMapaComParametrosIreport();
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
			setPassoApasso("Passo a passo"); //gerarPassoAPasso()
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

//	private String gerarPassoAPasso() throws Exception {
//		StringBuilder passoApasso = new StringBuilder();
//		passoApasso.append(xls.lerPlanilha("Str_Item_Menu"));
//		passoApasso.append(" > ");
//		passoApasso.append(xls.lerPlanilha("Str_Sub_Item_Menu"));
//		return passoApasso.toString();
//	}

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
//		descricaoCasoDeTeste.append(xls.getProcedimentoTestePlanilha(
//				AbstractCenario.getCasoDeTesteAtualNomeSimples()));
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

//	private void gerarMapaComParametrosIreport() throws Exception, IOException {
//		HashMap<String, Object> parametros = new HashMap<String, Object>();
//		String[] parametros1 = xls.getSistemaModuloRequisitoDaPlanilha();
//		String sistema = parametros1[0];
//		String subSistema = parametros1[1];
//		String modulo = parametros1[2];
//		String requisito = parametros1[3];
//		String projeto = xls.getCodigoProjetoDaPlanilha();
//		String elemento = xls.getElementoSobTesteDaPlanilha();
//		String[] parametros2 = xls.getArquivoEelaboradoPorDaPlanilha();
//		String elaborado = parametros2[0];
//		String arquivoRTMO = parametros2[1];
//		String nomeArquivoSaidaAutomacao = arquivoRTMO.replace("RTMO", "LogAutomacao");
//		nomeArquivoSaidaAutomacao = nomeArquivoSaidaAutomacao.replace("xls", "pdf");
//		parametros.put("SISTEMA", sistema);
//		parametros.put("SUBSISTEMA", subSistema);
//		parametros.put("MODULO", modulo);
//		parametros.put("PROJETO", projeto);
//		parametros.put("REQUISITO_UC", requisito);
//		parametros.put("ELEMENTO_SOB_TESTE", elemento);
//		parametros.put("ELABORADO_POR", elaborado);
//		parametros.put("ARQUIVO", nomeArquivoSaidaAutomacao);
//		BufferedImage bi = ImageIO.read( new File(EvidenciaUtil.getValueAsString("logoEmpresa")));
//		parametros.put("LOGO_CLIENTE", bi);
//		JRFileVirtualizer fileVirtualizer = new JRFileVirtualizer(20, "c:\\temp");
//		parametros.put(JRParameter.REPORT_VIRTUALIZER, fileVirtualizer);
//		nomeArquivoSaidaAutomacao = nomeArquivoSaidaAutomacao.replace(".pdf", "");
//		nomeArquivoSaidaAutomacao = nomeArquivoSaidaAutomacao.replace("Arquivo: ", "");
//		setNomeArquivoSaida(nomeArquivoSaidaAutomacao);
//		setParametros(parametros);
//	}

}
