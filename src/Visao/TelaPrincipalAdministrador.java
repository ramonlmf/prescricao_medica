/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;

import Controle.Bd_Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import net.proteanit.sql.DbUtils;





/**
 *
 * @author ramon
 */
public class TelaPrincipalAdministrador extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    
    /**
     * Creates new form Principal
     */
    public TelaPrincipalAdministrador() {
        initComponents();  
        adicionarPacientesTabela();
        adicionarMedicosTabela();
        adicionarFarmaceuticosTabela();
    }
    
    /*
      Método responsável por adicionar todos pacientes do banco de dados na tabela
    */
    public void adicionarPacientesTabela() {
        
        String sql = "select idPaciente as Id, nome as Nome, datanascimento as DataNascimento, cpf as CPF, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_pacientes";
         
        try 
        {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);          
            rs = pst.executeQuery();
            tblPaciente.setModel(DbUtils.resultSetToTableModel(rs));
        } 
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, e);
        } 
        finally 
        {
            try 
            {
                conexao.close();
            } 
            catch (SQLException ex) 
            {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
    /*
      Método responsável por adicionar todos os médicos do banco de dados na tabela
    */
    public void adicionarMedicosTabela() 
    {  
        String sql = "select idMedico as Id, nomemedico as Nome, especialidade as Especialidade, crm as CRM, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_medico";
         
        try 
        {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);          
            rs = pst.executeQuery();
            tblMedicos.setModel(DbUtils.resultSetToTableModel(rs));
        } 
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, e);
        } 
        finally 
        {
            try 
            {
                conexao.close();
            } 
            catch (SQLException ex) 
            {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
    /*
      Método responsável por adicionar todos os farmacêuticos do banco de dados na tabela
    */
    public void adicionarFarmaceuticosTabela() 
    {  
        String sql = "select idFarmaceutico as Id, nome as Nome, farmacia as Farmacia, endereco as Endereço, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_farmaceutico";
        
        try 
        {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);          
            rs = pst.executeQuery();
            tblFarmaceuticos.setModel(DbUtils.resultSetToTableModel(rs));
        } 
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(null, e);
        } 
        finally 
        {
            try 
            {
                conexao.close();
            } 
            catch (SQLException ex) 
            {
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

        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPaciente = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMedicos = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblFarmaceuticos = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btPesquisarMedico = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btCadastroMedico = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btCadastrarFarmaceutico = new javax.swing.JButton();
        btPesquisarFarmaceutico = new javax.swing.JButton();
        btPesquisarPaciente = new javax.swing.JButton();
        btCadastrarPaciente = new javax.swing.JButton();
        btReceitas = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        btCadastrarMedicamento = new javax.swing.JButton();
        btPesquisarMedicamento = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtIdmedico = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Portal do Administrador");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setText("Farmacêuticos");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 490, 90, -1));

        jLabel7.setText("Médicos");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 310, 70, -1));

        jLabel8.setText("Pacientes");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 70, -1));

        tblPaciente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Nome", "Data Nascimento", "CPF", "Telefone", "E-mail", "Estado", "Cidade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPaciente.getTableHeader().setReorderingAllowed(false);
        tblPaciente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPacienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPaciente);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 150, 960, 140));

        tblMedicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Nome", "Especialidade", "CRM", "Telefone", "E-mail", "Estado", "Cidade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMedicos.getTableHeader().setReorderingAllowed(false);
        tblMedicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMedicosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMedicos);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 330, 960, 140));

        tblFarmaceuticos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Farmacêutico", "Farmácia", "Endereço", "Telefone", "E-mail", "Estado", "Cidade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblFarmaceuticos.getTableHeader().setReorderingAllowed(false);
        tblFarmaceuticos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFarmaceuticosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblFarmaceuticos);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 510, 960, 140));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setText("Portal do Administrador");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 430, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Paciente");

        btPesquisarMedico.setText("Pesquisar/Alterar");
        btPesquisarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarMedicoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Médico");

        btCadastroMedico.setText("Cadastrar");
        btCadastroMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastroMedicoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Farmaceutico");

        btCadastrarFarmaceutico.setText("Cadastrar");
        btCadastrarFarmaceutico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarFarmaceuticoActionPerformed(evt);
            }
        });

        btPesquisarFarmaceutico.setText("Pesquisar/Alterar");
        btPesquisarFarmaceutico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarFarmaceuticoActionPerformed(evt);
            }
        });

        btPesquisarPaciente.setText("Pesquisar/Alterar");
        btPesquisarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarPacienteActionPerformed(evt);
            }
        });

        btCadastrarPaciente.setText("Cadastrar");
        btCadastrarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarPacienteActionPerformed(evt);
            }
        });

        btReceitas.setText("Receitas");
        btReceitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btReceitasActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Medicamento");

        btCadastrarMedicamento.setText("Cadastrar");
        btCadastrarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarMedicamentoActionPerformed(evt);
            }
        });

        btPesquisarMedicamento.setText("Pesquisar/Alterar");
        btPesquisarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarMedicamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btPesquisarMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btCadastroMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btCadastrarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btPesquisarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btCadastrarMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btPesquisarMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btCadastrarFarmaceutico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btPesquisarFarmaceutico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(2, 2, 2)))
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(55, 55, 55))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(57, 57, 57))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastroMedico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarMedico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(1, 1, 1)
                .addComponent(btReceitas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastrarPaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarPaciente)
                .addGap(11, 11, 11)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btCadastrarFarmaceutico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarFarmaceutico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastrarMedicamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarMedicamento)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 200, 620));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtIdmedico.setText("1");
        getContentPane().add(txtIdmedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 50, 40, -1));

        jLabel19.setText("id do medico");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 50, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCadastroMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastroMedicoActionPerformed
        // TODO add your handling code here:
        CadastroMedico formCadastroMedico = new CadastroMedico();
        formCadastroMedico.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formCadastroMedico.setVisible(true);
    }//GEN-LAST:event_btCadastroMedicoActionPerformed

    private void btCadastrarFarmaceuticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarFarmaceuticoActionPerformed
        // TODO add your handling code here:
        CadastroFarmaceutico formCadastroFarmaceutico = new CadastroFarmaceutico();
        formCadastroFarmaceutico.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formCadastroFarmaceutico.setVisible(true);
    }//GEN-LAST:event_btCadastrarFarmaceuticoActionPerformed

    private void btCadastrarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarPacienteActionPerformed
        // TODO add your handling code here:
        CadastroPaciente formCadastroPaciente = new CadastroPaciente();
        formCadastroPaciente.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formCadastroPaciente.setVisible(true);
    }//GEN-LAST:event_btCadastrarPacienteActionPerformed

    private void btCadastrarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarMedicamentoActionPerformed
        // TODO add your handling code here:
        CadastroMedicamento formCadastroMedicamento = new CadastroMedicamento();
        formCadastroMedicamento.setVisible(true);
    }//GEN-LAST:event_btCadastrarMedicamentoActionPerformed

    private void tblPacienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacienteMouseClicked

    }//GEN-LAST:event_tblPacienteMouseClicked

    private void tblMedicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMedicosMouseClicked

    }//GEN-LAST:event_tblMedicosMouseClicked

    private void tblFarmaceuticosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFarmaceuticosMouseClicked

    }//GEN-LAST:event_tblFarmaceuticosMouseClicked

    private void btPesquisarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarMedicamentoActionPerformed
        // TODO add your handling code here:
        PesquisarMedicamento formPesquisarMedicamento = new PesquisarMedicamento();
        formPesquisarMedicamento.setVisible(true);
    }//GEN-LAST:event_btPesquisarMedicamentoActionPerformed

    private void btPesquisarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarPacienteActionPerformed
        // TODO add your handling code here:
        PesquisaPaciente formPesquisaPaciente = new PesquisaPaciente();
        formPesquisaPaciente.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formPesquisaPaciente.setVisible(true);
    }//GEN-LAST:event_btPesquisarPacienteActionPerformed

    private void btReceitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btReceitasActionPerformed
        // TODO add your handling code here:
        PesquisaReceitas formPesquisaReceitas = new PesquisaReceitas(); 
        formPesquisaReceitas.setVisible(true);
    }//GEN-LAST:event_btReceitasActionPerformed

    private void btPesquisarFarmaceuticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarFarmaceuticoActionPerformed
        // TODO add your handling code here:
        PesquisarFarmaceutico formPesquisarFarmaceutico = new PesquisarFarmaceutico();
        formPesquisarFarmaceutico.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formPesquisarFarmaceutico.setVisible(true);
    }//GEN-LAST:event_btPesquisarFarmaceuticoActionPerformed

    private void btPesquisarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarMedicoActionPerformed
        // TODO add your handling code here:
        PesquisarMedico formPesquisarMedico = new PesquisarMedico();
        formPesquisarMedico.setFormQueAbriu(this); // 'this' é o formulário que está abrindo o geral
        formPesquisarMedico.setVisible(true);
    }//GEN-LAST:event_btPesquisarMedicoActionPerformed

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
            java.util.logging.Logger.getLogger(TelaPrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalAdministrador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new TelaPrincipalAdministrador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCadastrarFarmaceutico;
    private javax.swing.JButton btCadastrarMedicamento;
    private javax.swing.JButton btCadastrarPaciente;
    private javax.swing.JButton btCadastroMedico;
    private javax.swing.JButton btPesquisarFarmaceutico;
    private javax.swing.JButton btPesquisarMedicamento;
    private javax.swing.JButton btPesquisarMedico;
    private javax.swing.JButton btPesquisarPaciente;
    private javax.swing.JButton btReceitas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblFarmaceuticos;
    private javax.swing.JTable tblMedicos;
    private javax.swing.JTable tblPaciente;
    private javax.swing.JTextField txtIdmedico;
    // End of variables declaration//GEN-END:variables
}
