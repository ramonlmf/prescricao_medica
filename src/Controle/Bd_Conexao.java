/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;
import java.sql.*;

/**
 *
 * @author ramon
 */
public class Bd_Conexao {
     /**
     * Método responsável pela conexão com o banco
     *
     * @return conexao
     */
    public static Connection conectar() {
        Connection conexao = null;
        String driver = "com.mysql.cj.jdbc.Driver";      
        String url = "jdbc:mysql://localhost:3306/db_receita_eletronica?characterEncoding=utf-8";        
        String user = "dba";   
        String password = "teste1234"; 
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
            return null;
        }
    }
    
}
