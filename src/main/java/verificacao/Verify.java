/**
 * 
 */
package verificacao;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import parametrizacao.ParametroUtil;
import util.UtilTxt;

/**
 * Classe que verifica e compara as mensagens esperadas e não esperadas do sistema.
 * 
 * @since 04/09/2014
 * 
 */
@SuppressWarnings("deprecation")
public class Verify extends Assert {

	private static final String QUEBRA_DE_LINHA = "\r\n";
	private static int quantidadeDeErros;
	private static StringBuffer listaDeErros = new StringBuffer();
	private static StringBuffer listaDeErrosParaPDF = new StringBuffer();
	private static boolean habilitarVerificacao = ParametroUtil
			.getValueAsBoolean("verify");

	public static void assertEquals(boolean expected, boolean actual) {
		if (habilitarVerificacao) {
			try {
				Assert.assertEquals(expected, actual);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertEquals(expected, actual);
		}
	}

	public static void assertEquals(String mensagem, boolean expected,
			boolean actual) {
		if (habilitarVerificacao) {
			try {
				Assert.assertEquals(mensagem, expected, actual);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertEquals(mensagem, expected, actual);
		}
	}

	public static void assertEquals(String mensagem, int expected, int actual) {
		if (habilitarVerificacao) {
			try {
				Assert.assertEquals(mensagem, expected, actual);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertEquals(mensagem, expected, actual);
		}
	}

	public static void assertEquals(String mensagem, String expected,
			String actual) {
		if (habilitarVerificacao) {
			try {
				Assert.assertEquals(mensagem, expected, actual);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertEquals(mensagem, expected, actual);
		}

	}

	public static void assertEquals(String expected, String actual) {
		String obtido = actual;
		if (obtido == null) {
			obtido = "";
		}
		String esperado = expected;
		if (esperado == null) {
			esperado = "";
		}

		if (habilitarVerificacao) {
			try {
				Assert.assertEquals(esperado, obtido);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertEquals(esperado, obtido);
		}
	}

	public static void assertTrue(String mensagem, boolean actual) {
		if (habilitarVerificacao) {
			try {
				Assert.assertTrue(mensagem, actual);
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			Assert.assertTrue(mensagem, actual);
		}
	}

	public static void assertContains(String expected, String actual) {
		if (habilitarVerificacao) {
			try {
				if (!actual.contains(expected)) {
					throw new ComparisonFailure(null, expected, actual);
				}
			} catch (AssertionFailedError afe) {
				adicionarErro(afe.getMessage());
			}
		} else {
			if (!actual.contains(expected)) {
				throw new ComparisonFailure(null, expected, actual);
			}
		}
	}

	private static void adicionarErro(String erro) {
		synchronized (Verify.class) {
			if (listaDeErros == null) {
				listaDeErros = new StringBuffer();
			}
		}
		quantidadeDeErros += 1;
		listaDeErros.append(erro);
		listaDeErros.append(QUEBRA_DE_LINHA);
	}

	public static void fail(String mensagem) {
		if (habilitarVerificacao) {
			adicionarErro(mensagem);
			listaDeErrosParaPDF.append(mensagem);
		} else {
			Assert.fail(mensagem);
			listaDeErrosParaPDF.append(mensagem);
		}
	}

	public static void finalizaExecucao() {
		synchronized (Verify.class) {
			if (listaDeErros != null && listaDeErros.length() > 0) {
				String quantidade = "Quantidade de Erros:" + quantidadeDeErros
						+ QUEBRA_DE_LINHA;
				String resultado = listaDeErros.toString();
				listaDeErrosParaPDF.append(listaDeErros.toString());
				listaDeErros = null;
				quantidadeDeErros = 0;
				throw new AssertionFailedError(quantidade + resultado);
			}
		}
	}
	
	public static String getErro() {
		return listaDeErrosParaPDF.toString();
	}
	
	public static void limparListaDeErro() {
		listaDeErrosParaPDF = new StringBuffer();
		listaDeErros = null;
		UtilTxt.limparLog();
	}
}
