/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;

import Controle.Bd_Conexao;
import Controle.BuscaValorBancoDados;
import Modelo.Cidade;
import Modelo.Estado;
import Modelo.LocalizacaoLoader;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author ramon
 */
public class CadastroMedico extends javax.swing.JFrame {
    
    private JFrame formQueAbriu;
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    static int id_medico_selecionado;
    
         
    static int ultimoIdMedicoGerado;
    
    
       // Método para definir qual formulário abriu o FormCadastroPAciente
    public void setFormQueAbriu(JFrame form) 
    {
        this.formQueAbriu = form;
    }   
    
     //*******************************************************************************************************
    private LocalizacaoLoader localizacaoLoader = new LocalizacaoLoader();
    private void carregarCidadesEstados()
    {
        // Carregue os dados uma única vez
        
        String basePath = System.getProperty("user.dir") + "/estcid/";
        localizacaoLoader.carregarEstados(basePath + "estados.json");
        localizacaoLoader.carregarCidades(basePath + "cidades.json");     
        
        // Preencha o combo de estados
        localizacaoLoader.preencherComboEstados(txtEstado);
        // Atualize as cidades inicialmente
        updateCidades();
        
        // Adicione listener para mudança de estado
        txtEstado.addActionListener(e -> updateCidades());
    }
    
    private void updateCidades() {
        Estado estadoSelecionado = (Estado) txtEstado.getSelectedItem();
        localizacaoLoader.atualizarCidades(txtCidade, estadoSelecionado);
    }
    
     private void setarCidadeEstado( String estadoDesejado, String cidadeDesejada )
    {      

        // Encontrar o estado na combobox
        for (int i = 0; i < txtEstado.getItemCount(); i++) {
            Estado estadoItem = (Estado) txtEstado.getItemAt(i);
            if (estadoItem.getEstado().equals(estadoDesejado)) {
                txtEstado.setSelectedIndex(i);
                break;
            }
        }

        // Como a atualização das cidades depende do estado selecionado, podemos forçar a atualização
        updateCidades();

        // Agora, selecionar a cidade desejada
        for (int i = 0; i < txtCidade.getItemCount(); i++) {
            Cidade cidadeItem = (Cidade) txtCidade.getItemAt(i);
            if (cidadeItem.getCidade().equals(cidadeDesejada)) {
                txtCidade.setSelectedIndex(i);
                break;
            }
        }     
    }     
    //*******************************************************************************************************
    
    /**
     * Creates new form CadastroMedico
     */
    public CadastroMedico() {
        initComponents();
        carregarCidadesEstados();
        carregarTabelaMedicos();
        
        
        ((DefaultTableModel) tblMedicos.getModel()).setRowCount(0);
        btNovoCadastro.setEnabled(false);
        btAlterar.setEnabled(false);
        btExcluir.setEnabled(false);
    }
    
    private void limpar()
    {
        txtNome.setText("");
        txtEspecialidade.setText("");
        txtCrm.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        //txtEstado.setText("");
        //txtCidade.setText("");
        setarCidadeEstado("","");
        txtClinica.setText("");
        txtEndereco.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        ((DefaultTableModel) tblMedicos.getModel()).setRowCount(0);
    }
    
    /**
     * Método responsável para prencher a tabela com medicos na inicializacao
     */
    private void carregarTabelaMedicos() {
        String sql = "select idMedico as Id, nomemedico as Nome, especialidade as Especialidade, crm as CRM, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_medico";
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
           // pst.setString(1, txtMedicamento.getText() + "%");
            rs = pst.executeQuery();
            tblMedicos.setModel(DbUtils.resultSetToTableModel(rs));
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
     * método usado para setar os campos de texto com o conteúdo da tabela 
     */
    private void setarCampos() {
        int setar = tblMedicos.getSelectedRow(); // pega a linha selecionada da tabela
        txtNome.setText(tblMedicos.getModel().getValueAt(setar, 1).toString());
        txtEspecialidade.setText(tblMedicos.getModel().getValueAt(setar, 2).toString());
        txtCrm.setText(tblMedicos.getModel().getValueAt(setar, 3).toString());
        txtTelefone.setText(tblMedicos.getModel().getValueAt(setar, 4).toString());
        txtEmail.setText(tblMedicos.getModel().getValueAt(setar, 5).toString());
       // txtEstado.setText(tblMedicos.getModel().getValueAt(setar, 6).toString());
       // txtCidade.setText(tblMedicos.getModel().getValueAt(setar, 7).toString());   
        setarCidadeEstado(tblMedicos.getModel().getValueAt(setar, 6).toString(),tblMedicos.getModel().getValueAt(setar, 7).toString());
        
         String sql = "select clinica,enderecoclinica from tb_medico where idMedico like ?";
                 
       
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, id_medico_selecionado + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            
            txtClinica.setText(rs.getString(1));            
            txtEndereco.setText(rs.getString(2));
            
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
     * método usado para setar os campos de texto para fazer a alterarcao vinda do form de PesquisaMedico
     */
    public void setarCamposDaPesquisaMedico(int id_medico) {
          
        id_medico_selecionado = id_medico;
        btNovoCadastro.setEnabled(false);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        lbSenha.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        
        String sql = "select nomemedico,especialidade,crm,telefone,email,estado,cidade,clinica,enderecoclinica from tb_medico where idMedico like ?";
                 
       
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, id_medico_selecionado + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            txtNome.setText(rs.getString(1));
            txtEspecialidade.setText(rs.getString(2));
            txtCrm.setText(rs.getString(3));
            txtTelefone.setText(rs.getString(4));
            txtEmail.setText(rs.getString(5));
            //txtEstado.setText(rs.getString(6));
            //txtCidade.setText(rs.getString(7));
            setarCidadeEstado(rs.getString(6),rs.getString(7));
            txtClinica.setText(rs.getString(8));            
            txtEndereco.setText(rs.getString(9));
            
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
     * Método responsável por atualizar o cadastro de um medico
     */
    private void atualizarCadastroMedico() {
        
        
        String sql = "update tb_medico set nomemedico=?,especialidade=?,crm=?,telefone=?,email=?,estado=?,cidade=?,clinica=?,enderecoclinica=?  where idMedico=?";
                          
        try 
        {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtEspecialidade.getText());
            pst.setString(3, txtCrm.getText());
            pst.setString(4, txtTelefone.getText());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtEstado.getSelectedItem().toString());
            pst.setString(7, txtCidade.getSelectedItem().toString());
            pst.setString(8, txtClinica.getText());
            pst.setString(9, txtEndereco.getText());            
            pst.setInt(10, id_medico_selecionado); //atualiza o indice do medico
            
            if ( txtNome.getText().trim().isEmpty() || txtEspecialidade.getText().trim().isEmpty() || txtCrm.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty() 
               || txtClinica.getText().trim().isEmpty() || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
            {   
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } 
            else 
            {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) 
                {
                    JOptionPane.showMessageDialog(null, "Dados atualizado com sucesso"); 
                }
            }
        } 
        catch (SQLIntegrityConstraintViolationException e1) {
            JOptionPane.showMessageDialog(null, "Email já existente.\nEscolha outro email.");
            txtEmail.setText(null);
            txtEmail.requestFocus();
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
     * Método responsável pela exclusão do medico incluindo login 
     */
    private void excluirMedico() {
        
        
        DefaultTableModel tableModel = (DefaultTableModel) tblMedicos.getModel();
        int row = tblMedicos.getSelectedRow();
        int id_medico_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString());       
        
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este Médico", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            
            //String login = JOptionPane.showInputDialog(null, "Digite o nome de usuario a ser excluidio:");
           // String senha = JOptionPane.showInputDialog(null, "Digite a senha do usuario a ser excluido:");
            
            //if(verificarLoginMedico(login, senha))
            //{
                //Exclui o login do medico primeiro nesta ordem para tirar a chave estrangeira...
                //Ou ajustar a restrição de chave estrangeira para permitir ações em cascata, assim ao deletar um medico, os registros 
                //relacionados na tabela tb_usuarios também seriam removidos automaticamente
                conexao = Bd_Conexao.conectar();
                String sql = "delete from tb_usuarios where idMedico=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, id_medico_selecionado);

                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        limpar();
                        JOptionPane.showMessageDialog(null, "Login excluído com sucesso");
                        
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
                
                //Exclui da tb_medico o Medico para depois tirar a chave primaria
                conexao = Bd_Conexao.conectar();
                sql = "delete from tb_medico where idMedico=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, id_medico_selecionado);

                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        limpar();
                        JOptionPane.showMessageDialog(null, "Medico excluído com sucesso");
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
            //}
        }
    }
    
    private boolean verificaCamposSeEstaoTodosPreenchidos()
    {
        if ( txtNome.getText().trim().isEmpty() || txtEspecialidade.getText().trim().isEmpty() || txtCrm.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty() 
               || txtClinica.getText().trim().isEmpty() || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
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
     * Método responsável por cadastrar um novo medico
     */    
    private boolean cadastrarMedico() {
        
        BuscaValorBancoDados busca = new  BuscaValorBancoDados();
        boolean tudoOk = false;
        //boolean existe = busca.valorExiste("nome_da_tabela", "nome_do_campo", "valor_a_verificar");        
        boolean existe = busca.valorExiste("tb_usuarios", "login", txtLogin.getText().trim()); 
        
        if(existe == true)
        {
            JOptionPane.showMessageDialog(null, "Usuário já existente.\nEscolha outro nome.");
        }
        else
        {                               
            existe = busca.valorExiste("tb_pacientes", "email", txtEmail.getText().trim());
            if(existe == true)
            {
                JOptionPane.showMessageDialog(null, "E-mail já existente.\nEscolha outro nome.");
            }
            else
            {
            
                String sql = "insert into tb_medico(nomemedico,especialidade,crm,telefone,email,estado,cidade,clinica,enderecoclinica) values(?,?,?,?,?,?,?,?,?)";
        
                    try {
                        conexao = Bd_Conexao.conectar();            
                        pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                        pst.setString(1, txtNome.getText());
                        pst.setString(2, txtEspecialidade.getText());
                        pst.setString(3, txtCrm.getText());
                        pst.setString(4, txtTelefone.getText());
                        pst.setString(5, txtEmail.getText());
                        pst.setString(6, txtEstado.getSelectedItem().toString());
                        pst.setString(7, txtCidade.getSelectedItem().toString());
                        pst.setString(8, txtClinica.getText());
                        pst.setString(9, txtEndereco.getText());
                        

                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Cadastrado com sucesso"); 

                                // Obtém o último ID gerado
                                rs = pst.getGeneratedKeys();
                                if (rs.next()) {
                                    ultimoIdMedicoGerado = rs.getInt(1);
                                    System.out.println("Último ID inserido: " + ultimoIdMedicoGerado);
                                }
                        }

                    } catch (SQLIntegrityConstraintViolationException e1) {
                        JOptionPane.showMessageDialog(null, "Email já existente.\nEscolha outro email.");
                        txtEmail.setText(null);
                        txtEmail.requestFocus();
                    } catch (HeadlessException | SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    } finally {
                        try {
                            conexao.close();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);
                        }
                    }
                    
                    criarLoginUsuario();
                    tudoOk = true;
            }
        }
        return tudoOk;
    }
    
     /**
     * Método responsável por criar um login para usuario
     */
    private void criarLoginUsuario() 
    {
        String sql = "insert into tb_usuarios(login, senha, perfil, idMedico) values(?,?,?,?)";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtLogin.getText());
            pst.setString(2, txtSenha.getText());
            pst.setString(3, "Medico");
            pst.setInt(4, ultimoIdMedicoGerado);
            
            pst.executeUpdate();            
            JOptionPane.showMessageDialog(null, "Login criado com sucesso"); 
            
            
            
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtEspecialidade = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCrm = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtClinica = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btCadastrar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btVoltar = new javax.swing.JButton();
        btAlterar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMedicos = new javax.swing.JTable();
        btNovoCadastro = new javax.swing.JButton();
        lbLogin = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        lbSenha = new javax.swing.JLabel();
        txtSenha = new javax.swing.JTextField();
        txtEstado = new javax.swing.JComboBox();
        txtCidade = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cadastro de Mèdico");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Especialidade");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 190, -1, -1));

        jLabel5.setText("Nome completo");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        txtEspecialidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEspecialidadeActionPerformed(evt);
            }
        });
        getContentPane().add(txtEspecialidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 210, 390, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setText("Cadastramento do médico");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        jLabel6.setText("CRM");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 190, -1, -1));

        txtCrm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCrmActionPerformed(evt);
            }
        });
        getContentPane().add(txtCrm, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 210, 110, -1));

        jLabel8.setText("E-mail");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 250, -1, -1));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 270, 280, -1));

        jLabel7.setText("Telefone");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, -1, -1));

        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });
        getContentPane().add(txtTelefone, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, 170, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 190, 150));

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 390, -1));

        jLabel9.setText("Cidade");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 250, -1, -1));

        jLabel11.setText("Clinica");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, -1, -1));

        txtClinica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClinicaActionPerformed(evt);
            }
        });
        getContentPane().add(txtClinica, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 320, 170, -1));

        jLabel12.setText("Endereço");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 300, -1, -1));

        txtEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoActionPerformed(evt);
            }
        });
        getContentPane().add(txtEndereco, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 320, 280, -1));

        jLabel10.setText("Estado");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 250, -1, -1));

        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 310, 110, -1));

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

        tblMedicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
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
        jScrollPane1.setViewportView(tblMedicos);
        if (tblMedicos.getColumnModel().getColumnCount() > 0) {
            tblMedicos.getColumnModel().getColumn(0).setMinWidth(1);
            tblMedicos.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblMedicos.getColumnModel().getColumn(0).setMaxWidth(50);
            tblMedicos.getColumnModel().getColumn(7).setPreferredWidth(20);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 440, 960, 210));

        btNovoCadastro.setText("Novo Cadastro");
        btNovoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoCadastroActionPerformed(evt);
            }
        });
        getContentPane().add(btNovoCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 310, 110, -1));

        lbLogin.setText("Nome de usuário");
        getContentPane().add(lbLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 370, -1, -1));

        txtLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginActionPerformed(evt);
            }
        });
        getContentPane().add(txtLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 390, 170, -1));

        lbSenha.setText("Senha provisória");
        getContentPane().add(lbSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 370, 120, -1));

        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });
        getContentPane().add(txtSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 390, 170, -1));

        getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 270, 210, -1));

        getContentPane().add(txtCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 270, 200, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtEspecialidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEspecialidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEspecialidadeActionPerformed

    private void txtCrmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCrmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCrmActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void tblMedicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMedicosMouseClicked
       
        btNovoCadastro.setEnabled(true);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        btExcluir.setEnabled(true);
        lbSenha.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        DefaultTableModel tableModel = (DefaultTableModel) tblMedicos.getModel();
        int row = tblMedicos.getSelectedRow();
        id_medico_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString()); 
        setarCampos();
    }//GEN-LAST:event_tblMedicosMouseClicked

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        // TODO add your handling code here:
         if(verificaCamposSeEstaoTodosPreenchidos())
        {
            if(cadastrarMedico())
            {    
                // criarLoginUsuario();
                carregarTabelaMedicos();
                btCadastrar.setEnabled(false); 
                btNovoCadastro.setEnabled(true);
                lbSenha.setVisible(false);
                lbLogin.setVisible(false);
                txtSenha.setVisible(false);
                txtLogin.setVisible(false);
                
            }
        }
    }//GEN-LAST:event_btCadastrarActionPerformed

    private void txtClinicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClinicaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClinicaActionPerformed

    private void txtEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoActionPerformed

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        // TODO add your handling code here:
        if (formQueAbriu != null) {
            // Aqui você pode chamar algum método do formulário que abriu
            // Por exemplo, se for uma classe específica, pode fazer um cast
            if (formQueAbriu instanceof TelaPrincipalAdministrador) 
            {
                ((TelaPrincipalAdministrador) formQueAbriu).adicionarMedicosTabela();
            } 
            else if(formQueAbriu instanceof PesquisarMedico)
            {
                ((PesquisarMedico) formQueAbriu).carregarTabelaMedicos();
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
        
        lbSenha.setVisible(true);
        lbLogin.setVisible(true);
        txtSenha.setVisible(true);
        txtLogin.setVisible(true);
    }//GEN-LAST:event_btNovoCadastroActionPerformed

    private void txtLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarActionPerformed
        // TODO add your handling code here:
         int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar os dados deste Médico", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            atualizarCadastroMedico();
            carregarTabelaMedicos();
        }
    }//GEN-LAST:event_btAlterarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        // TODO add your handling code here:
        excluirMedico();
        carregarTabelaMedicos();
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
            java.util.logging.Logger.getLogger(CadastroMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadastroMedico().setVisible(true);
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbLogin;
    private javax.swing.JLabel lbSenha;
    private javax.swing.JTable tblMedicos;
    private javax.swing.JComboBox txtCidade;
    private javax.swing.JTextField txtClinica;
    private javax.swing.JTextField txtCrm;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtEspecialidade;
    private javax.swing.JComboBox txtEstado;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtSenha;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
