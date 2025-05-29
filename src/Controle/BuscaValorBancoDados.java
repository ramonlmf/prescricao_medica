/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ramon
 */
public class BuscaValorBancoDados {
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/db_receita_eletronica?characterEncoding=utf-8";
    private static final String USER = "dba";
    private static final String PASSWORD = "teste1234";

    // Método que verifica se o valor existe na tabela com campo especificados
    public boolean valorExiste(String tabela, String campo, String valor) {
        boolean existe = false;
        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Carrega o driver do MySQL
            Class.forName(DRIVER);
            // Estabelece a conexão
            conexao = DriverManager.getConnection(URL, USER, PASSWORD);

            // Prepara a consulta SQL
            String sql = "SELECT 1 FROM " + tabela + " WHERE " + campo + " = ? LIMIT 1";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, valor);

            // Executa a consulta
            rs = stmt.executeQuery();

            // Verifica se encontrou algum resultado
            if (rs.next()) {
                existe = true;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro na conexão ou na consulta: " + e.getMessage());
        } finally {
            // Fecha os recursos para evitar vazamentos
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conexao != null) conexao.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return existe;
    }
    
}
