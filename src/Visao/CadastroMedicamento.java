/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;

import Controle.Bd_Conexao;
import Controle.BuscaValorBancoDados;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;




/**
 *
 * @author ramon
 */
public class CadastroMedicamento extends javax.swing.JFrame {
    
    private JFrame formQueAbriu;
     
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    String nome_atual_medicamento_bd;
    
    int id_medicamento_selecionado;
    
    
     // Método para definir qual formulário abriu o FormCadastroPAciente
    public void setFormQueAbriu(JFrame form) 
    {
        this.formQueAbriu = form;
    }   

    /**
     * Creates new form CadastroMedico
     */
    public CadastroMedicamento() {
        initComponents();
       // carregarTabelaMedicamento();
        ((DefaultTableModel) tblMedicamento.getModel()).setRowCount(0);
        btNovoCadastro.setEnabled(false);
        btAlterar.setEnabled(false);
        btExcluir.setEnabled(false);
    }

    /**
     * Método responsável para prencher a tabela com medicamentos na inicializacao
     */
    private void carregarTabelaMedicamento() {
        String sql = "select idMedicamento as Id, nomemedicamento as Medicamento, laboratorio as Laboratório, apresentacao as Apresentação from tb_medicamento";
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
           // pst.setString(1, txtMedicamento.getText() + "%");
            rs = pst.executeQuery();
            tblMedicamento.setModel(DbUtils.resultSetToTableModel(rs));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
     private void limpar()
    {
        txtMedicamento.setText("");
        txtLaboratorio.setText("");
        txtApresentacao.setText("");        
        ((DefaultTableModel) tblMedicamento.getModel()).setRowCount(0);
    }
    
    
    /**
     * método usado para setar os campos de texto com o conteúdo da tabela
     */
    private void setarCampos() 
    {
        int setar = tblMedicamento.getSelectedRow(); // pega a linha selecionada da tabela
        txtMedicamento.setText(tblMedicamento.getModel().getValueAt(setar, 1).toString());
        txtLaboratorio.setText(tblMedicamento.getModel().getValueAt(setar, 2).toString());
        txtApresentacao.setText(tblMedicamento.getModel().getValueAt(setar, 3).toString()); 
        
        nome_atual_medicamento_bd = txtMedicamento.getText().trim();
        
    }
    
    private boolean verificaCamposSeEstaoTodosPreenchidos()
    {
        if ( txtMedicamento.getText().trim().isEmpty() || txtLaboratorio.getText().trim().isEmpty() || txtApresentacao.getText().trim().isEmpty())               
        {   
            JOptionPane.showMessageDialog(null, "Preencha todos os campos");
            return false;
        } 
        else 
        {
            return true;
        }
    }
    
      /**
     * Método responsável por cadastrar um novo medicamento
     */    
    private boolean cadastrarMedicamento() {
        
        BuscaValorBancoDados busca = new  BuscaValorBancoDados();
        boolean tudoOk = false;
        //boolean existe = busca.valorExiste("nome_da_tabela", "nome_do_campo", "valor_a_verificar");        
        boolean existe = busca.valorExiste("tb_medicamento", "nomemedicamento", txtMedicamento.getText().trim()); 
        
        if(existe == true)
        {
            JOptionPane.showMessageDialog(null, "Medicamento já existente.\nEscolha outro nome.");
        }
        else
        {    
            
            String sql = "insert into tb_medicamento(nomemedicamento, laboratorio, apresentacao) values(?,?,?)";

            try {
                conexao = Bd_Conexao.conectar();            
                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtMedicamento.getText());
                pst.setString(2, txtLaboratorio.getText());
                pst.setString(3, txtApresentacao.getText());


                int adicionado = pst.executeUpdate();
                if (adicionado > 0) 
                {
                    JOptionPane.showMessageDialog(null, "Medicamento cadastrado com sucesso");
                }

            } 
            catch (SQLIntegrityConstraintViolationException e1) 
            {
                JOptionPane.showMessageDialog(null, "Medicamento já existente.\nEscolha outro nome.");
                
            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    conexao.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }

            
            tudoOk = true;
            
        }
        return tudoOk;
    }
    
     /**
     * Método responsável por atualizar o cadastro de um medicamento
     */
    private boolean atualizarCadastroMedicamento() {
        
        BuscaValorBancoDados busca = new  BuscaValorBancoDados();
        boolean tudoOk = false;
        //boolean existe = busca.valorExiste("nome_da_tabela", "nome_do_campo", "valor_a_verificar");        
        boolean existe = busca.valorExiste("tb_medicamento", "nomemedicamento", txtMedicamento.getText().trim()); 
        
        if(nome_atual_medicamento_bd.equals(txtMedicamento.getText()))
        {
            existe = false; //pois este nome ja e o do bd entao nao vai ter repeticao so vai alterar os outrso campos
        }
        
        if(existe == true)
        {
            JOptionPane.showMessageDialog(null, "Medicamento já existente.\nEscolha outro nome.");
        }
        else
        {    
                
            String sql = "update tb_medicamento set nomemedicamento=?, laboratorio=?, apresentacao=? where idMedicamento=?";

            try 
            {
                conexao = Bd_Conexao.conectar();            
                pst = conexao.prepareStatement(sql);

                pst.setString(1, txtMedicamento.getText());
                pst.setString(2, txtLaboratorio.getText());
                pst.setString(3, txtApresentacao.getText());                      
                pst.setInt(4, id_medicamento_selecionado); //atualiza o indice do farmaceutico

                 if ( txtMedicamento.getText().trim().isEmpty() || txtLaboratorio.getText().trim().isEmpty() || txtApresentacao.getText().trim().isEmpty())               
                {   
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
                } 
                else 
                {
                    int adicionado = pst.executeUpdate();
                    if (adicionado > 0) 
                    {
                        JOptionPane.showMessageDialog(null, "Medicamento atualizado com sucesso"); 
                    }
                }
            } 
            catch (SQLIntegrityConstraintViolationException e1) {
                JOptionPane.showMessageDialog(null, "Medicamento já existente.\nEscolha outro email.");

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    conexao.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
    
            tudoOk = true;
            
        }
     return tudoOk;
 }
    
     /**
     * Método responsável pela exclusão de medicamento 
     */
    private void excluirMedicamento() 
    {
        DefaultTableModel tableModel = (DefaultTableModel) tblMedicamento.getModel();
        int row = tblMedicamento.getSelectedRow();
        int id_medicamento_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString());       
        
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este Medicamento", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            //Exclui da tabela o medicamento
            conexao = Bd_Conexao.conectar();
            String sql = "delete from tb_medicamento where idMedicamento=?";
            try 
            {
                pst = conexao.prepareStatement(sql);
                pst.setInt(1, id_medicamento_selecionado);

                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    limpar();
                    JOptionPane.showMessageDialog(null, "Medicamento excluído com sucesso");
                }
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
    }
    
     /**
     * método usado para setar os campos de texto para fazer a alterarcao vinda do form de PesquisaFarmaceutico
     */
    public void setarCamposDaPesquisaMedicamento(int id_medicamento) {
          
        id_medicamento_selecionado = id_medicamento;
        btNovoCadastro.setEnabled(false);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
                
        String sql = "select nomemedicamento, laboratorio, apresentacao from tb_medicamento where idMedicamento like ?";
                    
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, id_medicamento_selecionado + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            nome_atual_medicamento_bd = rs.getString(1); //pega o nome atual do medicamento no banco de dados pra nao dar conflito na atualizacao do medicamento
            txtMedicamento.setText(rs.getString(1));
            txtLaboratorio.setText(rs.getString(2));
            txtApresentacao.setText(rs.getString(3)); 
            
        } catch (SQLException e) {
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

        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtLaboratorio = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtApresentacao = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMedicamento = new javax.swing.JTextField();
        btCadastrar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btVoltar = new javax.swing.JButton();
        btAlterar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicamento = new javax.swing.JTable();
        btNovoCadastro = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cadastro de Medicamento");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Laboratório");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 190, -1, -1));

        jLabel5.setText("Medicamento");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        txtLaboratorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLaboratorioActionPerformed(evt);
            }
        });
        getContentPane().add(txtLaboratorio, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, 250, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setText("Cadastramento de medicamento");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        jLabel6.setText("Apresentação");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 190, -1, -1));

        txtApresentacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApresentacaoActionPerformed(evt);
            }
        });
        getContentPane().add(txtApresentacao, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 210, 360, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 190, 150));

        txtMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMedicamentoActionPerformed(evt);
            }
        });
        getContentPane().add(txtMedicamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 310, -1));

        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, 110, -1));

        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });
        getContentPane().add(btExcluir, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 590, 110, -1));

        btVoltar.setText("Voltar");
        btVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btVoltarActionPerformed(evt);
            }
        });
        getContentPane().add(btVoltar, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 630, 110, -1));

        btAlterar.setText("Alterar");
        btAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAlterarActionPerformed(evt);
            }
        });
        getContentPane().add(btAlterar, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 550, 110, -1));

        tblMedicamento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Medicamento", "Laboratório", "Apresentação"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMedicamento.getTableHeader().setReorderingAllowed(false);
        tblMedicamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMedicamentoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMedicamento);
        if (tblMedicamento.getColumnModel().getColumnCount() > 0) {
            tblMedicamento.getColumnModel().getColumn(0).setMinWidth(1);
            tblMedicamento.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblMedicamento.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, 960, 400));

        btNovoCadastro.setText("Novo Cadastro");
        btNovoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoCadastroActionPerformed(evt);
            }
        });
        getContentPane().add(btNovoCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 290, 110, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMedicamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMedicamentoActionPerformed

    private void txtLaboratorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLaboratorioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLaboratorioActionPerformed

    private void txtApresentacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApresentacaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApresentacaoActionPerformed

    private void tblMedicamentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMedicamentoMouseClicked
       
        setarCampos();
        btNovoCadastro.setEnabled(true);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        btExcluir.setEnabled(true);
        DefaultTableModel tableModel = (DefaultTableModel) tblMedicamento.getModel();
        int row = tblMedicamento.getSelectedRow();
        id_medicamento_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString()); 
        
       
    }//GEN-LAST:event_tblMedicamentoMouseClicked

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        // TODO add your handling code here:
        
        if(verificaCamposSeEstaoTodosPreenchidos())
        {
            if(cadastrarMedicamento())
            {    
                limpar();
                carregarTabelaMedicamento();
                btCadastrar.setEnabled(false); 
                btAlterar.setEnabled(false);
                btExcluir.setEnabled(false);
                btNovoCadastro.setEnabled(true);
                
            }
        }    
               
    }//GEN-LAST:event_btCadastrarActionPerformed

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        // TODO add your handling code here:
         if (formQueAbriu != null) {
            // Aqui você pode chamar algum método do formulário que abriu
            // Por exemplo, se for uma classe específica, pode fazer um cast
            if (formQueAbriu instanceof PesquisarMedicamento) 
            {
                ((PesquisarMedicamento) formQueAbriu).carregarTabelaMedicamento();
            } 
        }              
        this.dispose(); //Fechar o form atual
    }//GEN-LAST:event_btVoltarActionPerformed

    private void btNovoCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNovoCadastroActionPerformed
        // TODO add your handling code here:
         limpar();
        btCadastrar.setEnabled(true);
        btNovoCadastro.setEnabled(false);         
        btAlterar.setEnabled(false);
        btExcluir.setEnabled(false);
        nome_atual_medicamento_bd = "";
    }//GEN-LAST:event_btNovoCadastroActionPerformed

    private void btAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarActionPerformed
        // TODO add your handling code here:
         int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar os dados deste medicamento", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            if(atualizarCadastroMedicamento())
            {
                carregarTabelaMedicamento();
            }
        }
    }//GEN-LAST:event_btAlterarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        // TODO add your handling code here:
        excluirMedicamento();
        carregarTabelaMedicamento();
    }//GEN-LAST:event_btExcluirActionPerformed

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
            java.util.logging.Logger.getLogger(CadastroMedicamento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroMedicamento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroMedicamento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroMedicamento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadastroMedicamento().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAlterar;
    private javax.swing.JButton btCadastrar;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btNovoCadastro;
    private javax.swing.JButton btVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblMedicamento;
    private javax.swing.JTextField txtApresentacao;
    private javax.swing.JTextField txtLaboratorio;
    private javax.swing.JTextField txtMedicamento;
    // End of variables declaration//GEN-END:variables
}
