/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author ramon
 */
public class AssinaturaReceitaMedica {
    
    
    
    
    public static String gerarAssinatura()
    {
        String s; //String a ser impressa
        StringBuffer sb = new StringBuffer(); //StringBuffer para o append
        int qtdChars = 1; //Qtd de chars na String       

        while(qtdChars <= 16){ //String tem que ter 16 chars
	
		int charInt = (int) (Math.random() * 71); //Random para  gerar um número aleatório até 70
							//70, porque na tabela ASCII o maior caracter que será necessário é o de nro 70 (F)
	
		if(((charInt >= 48) && (charInt <= 57)) || ((charInt >= 65) && (charInt <= 70))){ 
		//Chars de 48 a 57 são os números
		//Chars de 65 a 70 são: A, B, C, D, F
			char c = (char) charInt; //Gerando um caracter de acordo com o ASCII gerado pelo random		
			sb.append(c); //Adiciona o char gerado no StringBuffer
			
			qtdChars++; //Incremente nro de chars no String
		}		
	}
	
	s = sb.toString(); //String recebe o StringBuffer que tem a sentença de 16 bytes nesse momento		
	System.out.println(s); //Imprime o String com o resultado

    
        return s;
    }
}
