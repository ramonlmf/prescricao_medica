/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;
import java.sql.*;
import Controle.Bd_Conexao;
import java.awt.HeadlessException;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author ramon
 */
public class Cadastro extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
        
    
    /**
     * Creates new form Login
     */
    public Cadastro() {
        initComponents();
          status();     
    }
    
    public void verificaDados()
    {
        if ( txtLogin.getText().trim().isEmpty() || txtSenha1.getText().trim().isEmpty()|| txtSenha2.getText().trim().isEmpty()|| txtChave.getSelectedItem().toString().trim().isEmpty()  ) 
        {   
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
        } 
        else 
        {                
            if( Arrays.equals(txtSenha1.getPassword(), txtSenha2.getPassword()))
            {
                cadastrarUsuario();
                
            }
            else
            {
                JOptionPane.showMessageDialog(null, "As senhas não conferem. Por favor, tente novamente");
                
                    
            }
                
        }
    }
    
    
    /**
     * Método responsável por cadastrar um novo usuario
     */
    private void cadastrarUsuario() {
        String sql = "insert into tb_usuarios(login, senha, perfil) values(?,?,?)";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, txtLogin.getText());
            pst.setString(2, txtSenha1.getText());
            pst.setString(3, txtChave.getSelectedItem().toString().trim());
            
            pst.executeUpdate();            
            JOptionPane.showMessageDialog(null, "Cadastrado com sucesso"); 
            
            /*
            // Obtém o último ID gerado
            rs = pst.getGeneratedKeys();
            if (rs.next()) {

                int usert = rs.getInt(1);

                TelaPrincipalPaciente form = new TelaPrincipalPaciente(); // Crie uma instância do Form2
                form.SetIdUsuario(usert); // Chame o método SetText
                System.out.println("Último ID inserido no cadsatro: " + usert);
            }  
            */
            
             // Obtém o último ID gerado
            rs = pst.getGeneratedKeys();
            int usert = 0;
            if (rs.next()) 
            {
                usert = rs.getInt(1);   
                System.out.println("Último ID inserido no cadsatro: " + usert);
            }  
            
            
            String perfil_user =  txtChave.getSelectedItem().toString().trim();
                
            if(perfil_user == "Paciente")
            {
                TelaPrincipalPaciente principal = new TelaPrincipalPaciente();                
                principal.setVisible(true);   
                principal.SetIdUsuario(usert); // Chame o método SetText
                principal.SetIdPaciente(0); //seta zero pq é um novo usuario do tipo paciente
                this.dispose();
            } 
            else if(perfil_user == "Farmaceutico")
            {
                TelaPrincipalFarmaceutico principal = new TelaPrincipalFarmaceutico();                
                principal.setVisible(true);   
                principal.SetIdUsuario(usert); // Chame o método SetText
                principal.SetIdFarmaceutico(0); //seta zero pq é um novo usuario do tipo paciente
                this.dispose();

            }
            else if(perfil_user == "Medico")
            {
                TelaPrincipalMedico principal = new TelaPrincipalMedico();
                principal.setVisible(true);
                principal.SetIdUsuario(usert); // Chame o método SetText
                principal.SetIdMedico(0); //seta zero pq é um novo usuario do tipo paciente
                this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Perfil inexistente");
            }
                
            
        } catch (SQLIntegrityConstraintViolationException e1) {
            JOptionPane.showMessageDialog(null, "Usuário já existente.\nEscolha outro nome.");
            txtLogin.setText(null);
            txtLogin.requestFocus();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
    
     /**
     * Método responsável por exibir o ícone de status da conexão
     */
    private void status() {
        try {
            conexao = Bd_Conexao.conectar();
            if (conexao != null) {
                jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/dbok.png")));
            } else {
                jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/dberror.png")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }

 
       
        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLabel4 = new javax.swing.JLabel();
        txtSenha2 = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        btCadastrar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtSenha1 = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        txtChave = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        jInternalFrame1.setBackground(new java.awt.Color(255, 255, 255));
        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cadastro de Usuario");
        setBackground(new java.awt.Color(255, 255, 255));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setName(""); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, -1, -1));
        getContentPane().add(txtSenha2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 240, 188, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Nome de usuário");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 120, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Repetir Senha");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, -1, -1));

        txtLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginActionPerformed(evt);
            }
        });
        getContentPane().add(txtLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 188, -1));

        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 340, 140, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Senha");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, -1, -1));
        getContentPane().add(txtSenha1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 188, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Chave Registro");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 270, 120, -1));

        txtChave.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Paciente", "Farmaceutico", "Medico" }));
        getContentPane().add(txtChave, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 290, 190, -1));

        jLabel1.setForeground(new java.awt.Color(0, 204, 51));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_verde_login.jpg"))); // NOI18N
        jLabel1.setName(""); // NOI18N
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed

        verificaDados();
        
    }//GEN-LAST:event_btCadastrarActionPerformed

    private void txtLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cadastro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cadastro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cadastro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cadastro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cadastro().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCadastrar;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JComboBox txtChave;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JPasswordField txtSenha1;
    private javax.swing.JPasswordField txtSenha2;
    // End of variables declaration//GEN-END:variables
}
