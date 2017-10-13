package util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.runners.model.FrameworkMethod;

import driver.AbstractCenario;
import parametrizacao.ParametroUtil;

public class Relogio implements ActionListener {

	private javax.swing.Timer timer;
	private JLabel labelHoras=new JLabel();
	private JLabel labelData=new JLabel();
	private JLabel labelCasosTeste=new JLabel();
	private static JFrame frame=new JFrame();
	private static int qtdeTotalCasosTeste;
	private static int numeroCasosTesteAtual = 0;
	private static List<FrameworkMethod> preCondicoes;
	
	public static void main(String[] args) {
		System.out.println("Main para realizar o Runnable JAR");
		executarRelogio();
	}
	
	public static List<FrameworkMethod> getPreCondicoes() {
		return preCondicoes;
	}
	
	public static void setPreCondicoes(List<FrameworkMethod> preCondicoes) {
		Relogio.preCondicoes = preCondicoes;
		setQtdeTotalCasosTeste(preCondicoes.size());
	}
	
	public static int getNumeroCasosTesteAtual() {
		return numeroCasosTesteAtual;
	}
	
	public static void incremetarNumeroCasosTesteAtual() {
		if(AbstractCenario.getCasoDeTestePrincipalNomeSimples().contains("Suite")){
			if(AbstractCenario.getCasoDeTesteAtualNomeSimples().contains("Login")){
				++Relogio.numeroCasosTesteAtual;
			}
			if(getNumeroCasosTesteAtual() < getPreCondicoes().size()){
				if(getPreCondicoes().get(Relogio.numeroCasosTesteAtual).
						getMethod().getDeclaringClass().getSimpleName().equals(
								(AbstractCenario.getCasoDeTesteAtualNomeSimples()))){
					++Relogio.numeroCasosTesteAtual;
				}
			}
		}else{
			Relogio.numeroCasosTesteAtual = 1;
			setQtdeTotalCasosTeste(1);
		}
	}

	public static int getQtdeTotalCasosTeste() {
		return qtdeTotalCasosTeste;
	}
	
	public static void setQtdeTotalCasosTeste(int qtdeTotalCasosTeste) {
		Relogio.qtdeTotalCasosTeste = qtdeTotalCasosTeste;
	}
	
	public Relogio(){
		montaTela();
		date();
		setLabelCasoTeste();
		disparaRelogio();
	}
	
	private void setLabelCasoTeste() {
		StringBuilder casoTeste = new StringBuilder();
		casoTeste.append("CT ");
		casoTeste.append(getNumeroCasosTesteAtual());
		casoTeste.append(" de ");
		casoTeste.append(getQtdeTotalCasosTeste());
		labelCasosTeste.setText(casoTeste.toString());
	} 
	
	public void montaTela(){
		frame.dispose();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		labelCasosTeste.setFont(new Font("Arial", Font.PLAIN, 23));
		labelHoras.setFont(new Font("Arial", Font.PLAIN, 21));
		labelData.setFont(new Font("Arial", Font.PLAIN, 19));
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 7));
//		panel.add(labelCasosTeste);
		panel.add(labelHoras);
		panel.add(labelData);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		frame.getContentPane().add(panel);
		frame.setResizable(false);
		frame.setBounds(150, 200, 160, 105);
		frame.setLocation(10, 610);
		frame.setVisible(true);
	}   

	public void disparaRelogio() {
		if (timer == null) {
			timer = new javax.swing.Timer(1000, this);
			timer.setInitialDelay(0);
			timer.start();
		} else if (!timer.isRunning()) {
			timer.restart();
		}
	}
	
	public void date() {
		UtilData dt = new UtilData();
		String data = dt.getDate();
		labelData.setText(data);  
	}

	public void actionPerformed(ActionEvent ae) {
		GregorianCalendar calendario = new GregorianCalendar();
		int hora = calendario.get(GregorianCalendar.HOUR_OF_DAY);
		int minuto = calendario.get(GregorianCalendar.MINUTE); 
		int segundos = calendario.get(GregorianCalendar.SECOND);

		String horas =   
				((hora < 10) ? "0" : "")   
				+ hora   
				+ ":"   
				+ ((minuto < 10) ? "0" : "")   
				+ minuto   
				+ ":"   
				+ ((segundos < 10) ? "0" : "")   
				+ segundos;   

		labelHoras.setText(horas); 
	}
	
	public static void executarRelogio() {
		try{
			if(ParametroUtil.getValueAsBoolean("relogio")){
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Relogio();}
				});
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("ERRO interno de execução!");
		}
	}
	
}