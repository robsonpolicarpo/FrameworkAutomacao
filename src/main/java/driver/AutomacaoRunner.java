package driver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import log.GenericLog;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import parametrizacao.ParametroUtil;
import poscondicoes.PosCondicao;
import precondicoes.PreCondicao;
import precondicoes.PreCondicoes;
import util.Relogio;
import util.UtilTxt;
import verificacao.Verify;
import dadosdeteste.AbstractCarga;
import dadosdeteste.Carga;
import dadosdeteste.DDT;
import dadosdeteste.DDTUtils;

public class AutomacaoRunner extends BlockJUnit4ClassRunner {

	private List<AbstractCarga> classesDeCarga;

	private List<String> casosJaExecutados;

	private List<PosCondicao> posCondicoes;

	private FrameworkMethod casoDeTesteAtual;

	private static List<String> preCondicoesQueSeraoExecutadas;
	
	private void mapearCasosDeTesteQueJaSeraoExecutadosComoPreCondicao(
			FrameworkMethod preCondicao) throws Throwable {
		List<FrameworkMethod> preCondicoes = obterPreCondicoes(preCondicao);
		for (FrameworkMethod preCondicaoDaPreCondicao : preCondicoes) {
			mapearCasosDeTesteQueJaSeraoExecutadosComoPreCondicao(preCondicaoDaPreCondicao);
		}
		String nomeCompleto = getNomeCompletoMetodo(preCondicao);
		if (!preCondicoesQueSeraoExecutadas.contains(nomeCompleto)) {
			preCondicoesQueSeraoExecutadas.add(nomeCompleto);
		}
	}

	private String getNomeCompletoMetodo(FrameworkMethod casoDeTeste) {
		return getNomeCompletoMetodo(casoDeTeste, 0);
	}

	private String getNomeCompletoMetodo(FrameworkMethod casoDeTeste, int indice) {
		String nomeCompletoMetodo = "";
		nomeCompletoMetodo += casoDeTeste.getMethod().getDeclaringClass()
				.getName();
		nomeCompletoMetodo += ".";
		nomeCompletoMetodo += casoDeTeste.getName();
		nomeCompletoMetodo += "[";
		nomeCompletoMetodo += indice;
		nomeCompletoMetodo += "]";
		return nomeCompletoMetodo;
	}

	private List<FrameworkMethod> obterPreCondicoes(FrameworkMethod casoDeTeste)
			throws Throwable {
		PreCondicoes anotacaoPreCondicoes = casoDeTeste
				.getAnnotation(PreCondicoes.class);
		List<FrameworkMethod> resultado = new ArrayList<FrameworkMethod>();
		List<PreCondicao> preCondicoes = new ArrayList<PreCondicao>();
		if (anotacaoPreCondicoes != null) {
			if (casoDeTeste.getAnnotation(PreCondicao.class) != null) {
				String nomeCompletoPreCondicao = getNomeCompletoMetodo(casoDeTeste);
				throw new IllegalArgumentException(
						"O caso de teste \""
								+ nomeCompletoPreCondicao
								+ "()\" não pode possuir a anotação @PreCondicao e a anotação @PreCondicoes simultaneamente.");
			}
			for (int i = 0; i < anotacaoPreCondicoes.preCondicoes().length; i++) {
				preCondicoes.add(anotacaoPreCondicoes.preCondicoes()[i]);
			}
		} else if (casoDeTeste.getAnnotation(PreCondicao.class) != null) {
			preCondicoes.add(casoDeTeste.getAnnotation(PreCondicao.class));
		}
		for (PreCondicao preCondicao : preCondicoes) {
			Class<?> classe;
			if (preCondicao.alvo() == Object.class) {
				classe = casoDeTeste.getMethod().getDeclaringClass();
			} else {
				classe = preCondicao.alvo();
			}
			resultado.add(new FrameworkMethod(classe.getMethod(preCondicao
					.casoDeTeste())));
		}
		return resultado;
	}

	public AutomacaoRunner(Class<?> klass) throws InitializationError {
		super(klass);
		TestClass classeDeTeste = new TestClass(klass);
		if (preCondicoesQueSeraoExecutadas == null) {
			preCondicoesQueSeraoExecutadas = new ArrayList<String>();
		}
		try {
			List<FrameworkMethod> preCondicoes;
			for (FrameworkMethod casoDeTeste : classeDeTeste
					.getAnnotatedMethods(Test.class)) {
				if (casoDeTeste.getAnnotation(Ignore.class) == null) {
					preCondicoes = obterPreCondicoes(casoDeTeste);
					setQtdeCasoTesteTotal(preCondicoes, casoDeTeste);
					for (FrameworkMethod preCondicao : preCondicoes) {
						mapearCasosDeTesteQueJaSeraoExecutadosComoPreCondicao(preCondicao);
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void setQtdeCasoTesteTotal(List<FrameworkMethod> preCondicoes,
			FrameworkMethod casoDeTeste) {
		if(casoDeTeste.getMethod().getDeclaringClass().getSimpleName().contains("Suite")){
			Relogio.setPreCondicoes(preCondicoes);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		casoDeTesteAtual = method;
		AbstractCenario.setCasoDeTestePrincipal(casoDeTesteAtual.getMethod());
//		if (method.getAnnotation(Ignore.class) != null
//				|| preCondicoesQueSeraoExecutadas
//				.contains(getNomeCompletoMetodo(method))) {
//			notifier.fireTestIgnored(describeChild(method));
//		} else {
			classesDeCarga = new ArrayList<AbstractCarga>();
			casosJaExecutados = new ArrayList<String>();
			posCondicoes = new ArrayList<PosCondicao>();
			EachTestNotifier eachNotifier = new EachTestNotifier(notifier,
					describeChild(method));
			eachNotifier.fireTestStarted();
			try {
				Collection<Object[]> listaDados = new ArrayList<Object[]>();
				DDT ddt = method.getAnnotation(DDT.class);
				Method metodo;
				if (ddt != null) {
					metodo = ddt.alvo().getMethod(ddt.metodo());
					Object resultado = metodo.invoke(ddt.alvo().newInstance(),
							new Object[0]);
					listaDados = (Collection<Object[]>) resultado;
				}
				System.out
				.println("-------------------------------------------");
				System.out.println("Executando caso de teste: "
						+ method.getName());
				if (listaDados.size() == 0) {
					executarCaso(method, notifier, 0);
				} else {
					Object[] linha;
					for (int i = 0; i < listaDados.size(); i++) {
						linha = (Object[]) listaDados.toArray(new Object[0])[i];
						DDTUtils.setDados(linha);
						executarCaso(method, notifier, i);
					}
				}
				System.out
				.println("-------------------------------------------");
			} catch (Throwable e) {
				Verify.fail(e.getMessage());
				eachNotifier.addFailure(e);
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "FAIL".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						AbstractCenario.gerarDocumentosDeEvidencia("FALHA", true);
						Verify.limparListaDeErro();
						UtilTxt.limparLog();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			} finally {
				try {
					executarPosCondicoes(method, notifier);
				} catch (Throwable e) {
					eachNotifier.addFailure(e);
				}
				realizarDescarga();
			}
//		}
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm.ssaaa");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private void coletarEvidencia(final Method method, String status)
			throws Exception {
		//Esse método é utilizado na pré condição, e somente limpa o arquivo de Log.
		UtilTxt.limparLog();
	}

	private void coletarEvidencia(final FrameworkMethod method, String status) throws Exception
			 {
		if(ParametroUtil.isGerarPDF()){
			AbstractCenario.gerarDocumentosDeEvidencia(status, false);
		}
		else{
			File scrFile = ((TakesScreenshot) GenericWebDriverSingleton.getDriver())
					.getScreenshotAs(OutputType.FILE);
			String caminhoCompleto = montarPathDaImagem(method, status);
			try {
				FileUtils.copyFile(scrFile, new File(caminhoCompleto));
			} catch (IOException e) {
				throw new IOException("Falha ao salvar o arquivo!");
			}
		}
	}

	private String montarPathDaImagem(final FrameworkMethod method,
			String status) {
		StringBuffer caminhoCompleto = new StringBuffer();
		caminhoCompleto.append(ParametroUtil
				.getValueAsString("pastaEvidencias"));
		caminhoCompleto
		.append(method.getMethod().getDeclaringClass().getPackage() + "//");
		caminhoCompleto.append("[");
		caminhoCompleto.append(status);
		caminhoCompleto.append("] ");
		caminhoCompleto.append(" ");
		caminhoCompleto.append(getDateTime());
		caminhoCompleto.append(" ");
		caminhoCompleto
		.append(method.getMethod().getDeclaringClass().getSimpleName());
		caminhoCompleto.append(".");
		caminhoCompleto.append(method.getName());
		caminhoCompleto.append(".png");
		return caminhoCompleto.toString();
	}

	private void enfileirarPosCondicao(FrameworkMethod method) {
		PosCondicao anotacaoPosCondicao = method
				.getAnnotation(PosCondicao.class);
		if (anotacaoPosCondicao != null) {
			posCondicoes.add(anotacaoPosCondicao);
		}
	}

	private void executarPosCondicoes(final FrameworkMethod method, RunNotifier notifier)
			throws Throwable {
		Class<?> classe;
		Method metodo;
		Exception excecao = null;
		EachTestNotifier eachNotifier = new EachTestNotifier(notifier,
				describeChild(method));
		eachNotifier.fireTestStarted();
		for (int i = posCondicoes.size() - 1; i >= 0; i--) {
			if (posCondicoes.get(i).alvo() == Object.class) {
				classe = method.getMethod().getDeclaringClass();
			} else {
				classe = posCondicoes.get(i).alvo();
			}
			metodo = classe.getMethod(posCondicoes.get(i).metodo());
			AbstractCenario.setCasoDeTesteAtual(metodo);
			try {
				metodo.invoke(classe.newInstance());
			} catch (Exception e) {
				excecao = e;
				System.out.println(posCondicoes.get(i));
			}
			try {
				Verify.finalizaExecucao();
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "SUCCESS".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						coletarEvidencia(metodo, "SUCESSO");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (Throwable e) {
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "FAIL".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						coletarEvidencia(metodo, "FALHA");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				eachNotifier.addFailure(e);
				GenericLog.infor(e.getMessage());
			}
			eachNotifier.fireTestFinished();
		}
		if (excecao != null) {
			throw new RuntimeException(excecao);
		}
	}

	protected Object createTest() throws Exception {
		return new TestClass(casoDeTesteAtual.getMethod().getDeclaringClass())
		.getOnlyConstructor().newInstance();
	}

	protected void executarCaso(final FrameworkMethod method,
			RunNotifier notifier, int indice) throws Throwable {
		EachTestNotifier eachNotifier = new EachTestNotifier(notifier,
				describeChild(method));
		eachNotifier.fireTestStarted();
		if (validarPremissasDeExecucao(method, indice)) {
			executarPreCondicoes(method, notifier, indice);
			casoDeTesteAtual = method;
			AbstractCenario.setCasoDeTesteAtual(casoDeTesteAtual.getMethod());
			if(method.getName().equals("realizarLogin")){
				Relogio.incremetarNumeroCasosTesteAtual();
				Relogio.executarRelogio();
			}
			realizarCarga(method);
			enfileirarPosCondicao(method);
			methodBlock(method).evaluate();
			try {
				Verify.finalizaExecucao();
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "SUCCESS".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						coletarEvidencia(method, "SUCESSO");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (Throwable e) {
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "FAIL".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						coletarEvidencia(method, "FALHA");
						Verify.limparListaDeErro();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				eachNotifier.addFailure(e);
			}
			eachNotifier.fireTestFinished();
		}
	}

	private boolean validarPremissasDeExecucao(FrameworkMethod method,
			int indice) throws Throwable {
		String nomeCompletoPreCondicao = getNomeCompletoMetodo(method, indice);
		if (casosJaExecutados.contains(nomeCompletoPreCondicao)) {
			return false;
		} else {
			casosJaExecutados.add(nomeCompletoPreCondicao);
			return true;
		}
	}

	private void executarPreCondicoes(final FrameworkMethod method,
			RunNotifier notifier, int indice) throws Throwable {
		PreCondicoes anotacaoPreCondicoes = method
				.getAnnotation(PreCondicoes.class);
		List<PreCondicao> preCondicoes = new ArrayList<PreCondicao>();
		if (anotacaoPreCondicoes != null) {
			if (method.getAnnotation(PreCondicao.class) != null) {
				String nomeCompletoPreCondicao = getNomeCompletoMetodo(method);
				throw new IllegalArgumentException(
						"O caso de teste \""
								+ nomeCompletoPreCondicao
								+ "()\" não pode possuir a anotação @PreCondicao e a anotação @PreCondicoes simultaneamente.");
			}
			for (int i = 0; i < anotacaoPreCondicoes.preCondicoes().length; i++) {
				preCondicoes.add(anotacaoPreCondicoes.preCondicoes()[i]);
			}
		} else if (method.getAnnotation(PreCondicao.class) != null) {
			preCondicoes.add(method.getAnnotation(PreCondicao.class));
		}
		for (PreCondicao preCondicao : preCondicoes) {
			Class<?> classe;
			Method metodo;
			if (preCondicao.alvo() == Object.class) {
				classe = method.getMethod().getDeclaringClass();
			} else {
				classe = preCondicao.alvo();
			}
			metodo = classe.getMethod(preCondicao.casoDeTeste());
			try {
				executarCaso(new FrameworkMethod(metodo), notifier, indice);
				System.out.println("Pré-Condição executada: " + classe.getSimpleName()
				+ " -> " + metodo.getName());
			} catch (Exception e) {
				EachTestNotifier eachNotifier = new EachTestNotifier(notifier,
						describeChild(method));
				eachNotifier.fireTestStarted();
				Verify.fail(e.getMessage());
				eachNotifier.addFailure(e);
				try {
					if ("ALL".equalsIgnoreCase(ParametroUtil
							.getValueAsString("nivelEvidencias"))
							|| "FAIL".equalsIgnoreCase(ParametroUtil
									.getValueAsString("nivelEvidencias"))) {
						AbstractCenario.gerarDocumentosDeEvidencia("FALHA", true);
						Verify.limparListaDeErro();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				System.out.println("Erro na Pré-Condição: " + classe.getSimpleName()
				+ " -> " + metodo.getName());
			}
		}
	}

	private void realizarCarga(final FrameworkMethod method) throws Throwable {
		Carga anotacaoCarga = method.getAnnotation(Carga.class);
		if (anotacaoCarga != null) {
			for (int i = 0; i < anotacaoCarga.alvo().length; i++) {
				AbstractCarga abstractCarga = anotacaoCarga.alvo()[i]
						.newInstance();
				for (AbstractCarga carga : classesDeCarga) {
					if (carga.getClass() == abstractCarga.getClass()) {
						throw new IllegalArgumentException(method.getMethod()
								.toString()
								+ ":A classe de carga \""
								+ abstractCarga.getClass().getName()
								+ "\" já foi executada.");
					}
				}
				abstractCarga.carregar();
				classesDeCarga.add(abstractCarga);
			}
		}
	}

	private void realizarDescarga() {
		for (int i = (classesDeCarga.size() - 1); i >= 0; i--) {
			try {
				classesDeCarga.get(i).descarregar();
			} catch (Throwable e) {
				Verify.fail(e.getMessage());
			}
		}
	}

}
