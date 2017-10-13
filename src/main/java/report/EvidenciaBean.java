package report;


import java.util.List;

public class EvidenciaBean {

	private String passoApasso;
	private String casoDeTeste;
	private String status;
	private String falha;
	private String log;
	private List<ImagemBean> imagens;
	
	public EvidenciaBean(String passoApasso, String casoDeTeste,
			String status, List<ImagemBean> imagens, String falha) {
		this.passoApasso = passoApasso;
		this.casoDeTeste = casoDeTeste;
		this.status = status;
		this.imagens = imagens;
		this.falha = falha;
	}
	
	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getFalha() {
		return falha;
	}

	public void setFalha(String falha) {
		this.falha = falha;
	}

	public List<ImagemBean> getImagens() {
		return imagens;
	}

	public void setImagens(List<ImagemBean> imagens) {
		this.imagens = imagens;
	}

	public String getPassoApasso() {
		return passoApasso;
	}

	public void setPassoApasso(String passoApasso) {
		this.passoApasso = passoApasso;
	}

	public String getCasoDeTeste() {
		return casoDeTeste;
	}

	public void setCasoDeTeste(String casoDeTeste) {
		this.casoDeTeste = casoDeTeste;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
