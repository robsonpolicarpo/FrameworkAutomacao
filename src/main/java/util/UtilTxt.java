package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class UtilTxt {
	
	public static String caminhoTxt = "./log/Log.log";

	public static String LerLog() {

		StringBuilder varLog = new StringBuilder();

		try {
			FileReader arq = new FileReader(caminhoTxt);
			BufferedReader lerArq = new BufferedReader(arq);

			String linha = lerArq.readLine(); // lé a primeira linha a variável "Linha" receber o valor "null" 
			//quando o processo de repetição atingir o final do arquivo texto

			while (linha != null) {
				varLog.append("\n");
				varLog.append(linha);
				linha = lerArq.readLine(); // lé da segunda até a Última linha
			}
			arq.close();
		}catch (IOException e) {
			System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
		return varLog.toString();
	}

	public static void limparLog(){  
	    Writer clean;
		try {
			clean = new BufferedWriter(new FileWriter(caminhoTxt));
			clean.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}  
	
	public static void printLog(){

		try {
			FileReader arq = new FileReader(caminhoTxt);
			BufferedReader lerArq = new BufferedReader(arq);

			String linha = lerArq.readLine(); // lé a primeira linha a variável "Linha" receber o valor "null" 
			//quando o processo de repetição atingir o final do arquivo texto

			while (linha != null) {
				System.out.println(linha);
				linha = lerArq.readLine(); // lé da segunda até a Última linha
			}
			arq.close();
		}catch (IOException e) {
			System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
		}
	}

}
