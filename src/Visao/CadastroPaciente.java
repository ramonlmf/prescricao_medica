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
public class CadastroPaciente extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    static int id_paciente_selecionado;
    static int ultimoIdPacienteGerado;
    
        
    private JFrame formQueAbriu;

    
    // Método para definir qual formulário abriu o FormCadastroPAciente
    public void setFormQueAbriu(JFrame form) {
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
    public CadastroPaciente() {
        
       
        
        initComponents();
         // carregarTabelaPacientes();
        carregarCidadesEstados();
        ((DefaultTableModel) tblPaciente.getModel()).setRowCount(0);
        btNovoCadastro.setEnabled(false);
        btAlterar.setEnabled(false);
        btExcluir.setEnabled(false);
    }
    
    
    private void limpar()
    {
        txtNome.setText("");
        txtDataNascimento.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        //txtEstado.setText("");
        //txtCidade.setText("");
        setarCidadeEstado("","");
        txtEstadocivil.setText("");
        txtSexo.setText("");
        txtRg.setText("");
        txtCep.setText("");
        txtEndereco.setText("");
        txtNumero.setText("");
        txtBairro.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        ((DefaultTableModel) tblPaciente.getModel()).setRowCount(0);
    }
    
    /**
     * Método responsável para prencher a tabela com pacientes na inicializacao
     */
    private void carregarTabelaPacientes() {
         String sql = "select idPaciente as Id, nome as Nome, datanascimento as DataNascimento, cpf as CPF, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_pacientes";
                     
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
           // pst.setString(1, txtMedicamento.getText() + "%");
            rs = pst.executeQuery();
            tblPaciente.setModel(DbUtils.resultSetToTableModel(rs));
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
    
    private boolean verificaCamposSeEstaoTodosPreenchidos()
    {
        if ( txtNome.getText().trim().isEmpty() || txtDataNascimento.getText().trim().isEmpty() || txtEstadocivil.getText().trim().isEmpty()
               || txtSexo.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtRg.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtEndereco.getText().trim().isEmpty() || txtNumero.getText().trim().isEmpty() || txtBairro.getText().trim().isEmpty()
               || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
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
     * Método responsável por cadastrar um novo paciente
     */    
    private boolean cadastrarPaciente() {
        
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
            
                String sql = "insert into tb_pacientes(nome,datanascimento,cpf,telefone,email,estado,cidade,estadocivil,sexo,rg,cep,endereco,numero,bairro) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
                    try {
                        conexao = Bd_Conexao.conectar();            
                        pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                        pst.setString(1, txtNome.getText());
                        pst.setString(2, txtDataNascimento.getText());
                        pst.setString(3, txtCpf.getText());
                        pst.setString(4, txtTelefone.getText());
                        pst.setString(5, txtEmail.getText());
                        pst.setString(6, txtEstado.getSelectedItem().toString());
                        pst.setString(7, txtCidade.getSelectedItem().toString());
                        pst.setString(8, txtEstadocivil.getText());
                        pst.setString(9, txtSexo.getText());
                        pst.setString(10, txtRg.getText());
                        pst.setString(11, txtCep.getText());
                        pst.setString(12, txtEndereco.getText());
                        pst.setString(13, txtNumero.getText());
                        pst.setString(14, txtBairro.getText());

                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Cadastrado com sucesso"); 

                                // Obtém o último ID gerado
                                rs = pst.getGeneratedKeys();
                                if (rs.next()) {
                                    ultimoIdPacienteGerado = rs.getInt(1);
                                    System.out.println("Último ID inserido: " + ultimoIdPacienteGerado);
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
        String sql = "insert into tb_usuarios(login, senha, perfil, idPaciente) values(?,?,?,?)";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtLogin.getText());
            pst.setString(2, txtSenha.getText());
            pst.setString(3, "Paciente");
            pst.setInt(4, ultimoIdPacienteGerado);
            
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
     * método usado para setar os campos de texto com o conteúdo da tabela e complementa com o do banco de dados
     */
    private void setarCampos() {
        int setar = tblPaciente.getSelectedRow(); // pega a linha selecionada da tabela
        txtNome.setText(tblPaciente.getModel().getValueAt(setar, 1).toString());
        txtDataNascimento.setText(tblPaciente.getModel().getValueAt(setar, 2).toString());
        txtCpf.setText(tblPaciente.getModel().getValueAt(setar, 3).toString());
        txtTelefone.setText(tblPaciente.getModel().getValueAt(setar, 4).toString());
        txtEmail.setText(tblPaciente.getModel().getValueAt(setar, 5).toString());
        //txtEstado.setText(tblPaciente.getModel().getValueAt(setar, 6).toString());
        //txtCidade.setText(tblPaciente.getModel().getValueAt(setar, 7).toString());
        setarCidadeEstado(tblPaciente.getModel().getValueAt(setar, 6).toString(),tblPaciente.getModel().getValueAt(setar, 7).toString());
          
        
        String sql = "select idPaciente, nome, rg, estadocivil, sexo, cep, endereco, numero, bairro from tb_pacientes where nome like ?";
                
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNome.getText() + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            txtRg.setText(rs.getString(3));
            txtEstadocivil.setText(rs.getString(4));
            txtSexo.setText(rs.getString(5));
            txtCep.setText(rs.getString(6));
            txtEndereco.setText(rs.getString(7));
            txtNumero.setText(rs.getString(8));
            txtBairro.setText(rs.getString(9));  
                       
            //tblPaciente.setModel(DbUtils.resultSetToTableModel(rs));
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
     * Método responsável pela autenticação do usuário a ser excluido     
     */
    private boolean verificarLoginPaciente(String login, String senha) {        
       
        boolean login_correto = false;
        String sql = "SELECT * FROM tb_usuarios WHERE login=? AND senha=?";
        
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, login);            
            pst.setString(2, senha);            
            rs = pst.executeQuery();            
            boolean registro_vf = rs.next();
            if (registro_vf) 
            {
               login_correto = true;
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Usuário e/ou senha inválido(s)");
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
        return login_correto;
    } 
    
    
    /**
     * Método responsável pela exclusão do paciente incluindo login 
     */
    private void excluirPaciente() {
        
        
        DefaultTableModel tableModel = (DefaultTableModel) tblPaciente.getModel();
        int row = tblPaciente.getSelectedRow();
        int id_paciente_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString());       
        
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este paciente", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            
            String login = JOptionPane.showInputDialog(null, "Digite o nome de usuario a ser excluidio:");
            String senha = JOptionPane.showInputDialog(null, "Digite a senha do usuario a ser excluido:");
            
            if(verificarLoginPaciente(login, senha))
            {
                //Exclui o login do paciente primeiro nesta ordem para tirar a chave estrangeira...
                //Ou ajustar a restrição de chave estrangeira para permitir ações em cascata, assim ao deletar um paciente, os registros 
                //relacionados na tabela tb_usuarios também seriam removidos automaticamente
                conexao = Bd_Conexao.conectar();
                String sql = "delete from tb_usuarios where idPaciente=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, id_paciente_selecionado);

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
                
                //Exclui da tabela paciente o Paciente para depois tirar a chave primaria
                conexao = Bd_Conexao.conectar();
                sql = "delete from tb_pacientes where idPaciente=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, id_paciente_selecionado);

                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        limpar();
                        JOptionPane.showMessageDialog(null, "Paciente excluído com sucesso");
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
    }
    
    /**
     * Método responsável por atualizar o cadastro de um paciente
     */
    private void atualizarCadastroPaciente() {
        
        
        String sql = "update tb_pacientes set nome=?,datanascimento=?,cpf=?,telefone=?,email=?,estado=?,cidade=?,estadocivil=?,sexo=?,rg=?,cep=?,endereco=?,numero=?,bairro=? where idPaciente=?";
        
        try 
        {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtDataNascimento.getText());
            pst.setString(3, txtCpf.getText());
            pst.setString(4, txtTelefone.getText());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtEstado.getSelectedItem().toString());
            pst.setString(7, txtCidade.getSelectedItem().toString());            
            pst.setString(8, txtEstadocivil.getText());
            pst.setString(9, txtSexo.getText());
            pst.setString(10, txtRg.getText());
            pst.setString(11, txtCep.getText());
            pst.setString(12, txtEndereco.getText());
            pst.setString(13, txtNumero.getText());
            pst.setString(14, txtBairro.getText());
            pst.setInt(15, id_paciente_selecionado); //atualiza o indice do paciente
            
            if ( txtNome.getText().trim().isEmpty() || txtDataNascimento.getText().trim().isEmpty() || txtEstadocivil.getText().trim().isEmpty()
               || txtSexo.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtRg.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtEndereco.getText().trim().isEmpty() || txtNumero.getText().trim().isEmpty() || txtBairro.getText().trim().isEmpty()
               || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
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
     * método usado para setar os campos de texto para fazer a alterarcao vinda do form de PesquisaPaciente
     */
    public void setarCamposDaPesquisaPaciente(int id_paciente) {
          
        id_paciente_selecionado = id_paciente;
        btNovoCadastro.setEnabled(false);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        lbSenha.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        
        String sql = "select nome,datanascimento,cpf,telefone,email,estado,cidade,estadocivil,sexo,rg,cep,endereco,numero,bairro from tb_pacientes where idPaciente like ?";
                         
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, id_paciente + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            txtNome.setText(rs.getString(1));
            txtDataNascimento.setText(rs.getString(2));
            txtCpf.setText(rs.getString(3));
            txtTelefone.setText(rs.getString(4));
            txtEmail.setText(rs.getString(5));
            //txtEstado.setText(rs.getString(6));
            //txtCidade.setText(rs.getString(7));
            setarCidadeEstado(rs.getString(6),rs.getString(7));
            txtEstadocivil.setText(rs.getString(8));
            txtSexo.setText(rs.getString(9));
            txtRg.setText(rs.getString(10));
            txtCep.setText(rs.getString(11));
            txtEndereco.setText(rs.getString(12));
            txtNumero.setText(rs.getString(13));
            txtBairro.setText(rs.getString(14));  
                       
            //tblPaciente.setModel(DbUtils.resultSetToTableModel(rs));
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
        txtDataNascimento = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEstadocivil = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btCadastrar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btVoltar = new javax.swing.JButton();
        btAlterar = new javax.swing.JButton();
        txtSexo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPaciente = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtCpf = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtRg = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCep = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        txtLogin = new javax.swing.JTextField();
        lbLogin = new javax.swing.JLabel();
        lbSenha = new javax.swing.JLabel();
        txtSenha = new javax.swing.JTextField();
        btNovoCadastro = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox();
        txtCidade = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cadastro de Paciente");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Data de Nascimento");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 190, -1, -1));

        jLabel5.setText("Nome completo");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        txtDataNascimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDataNascimentoActionPerformed(evt);
            }
        });
        getContentPane().add(txtDataNascimento, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 210, 130, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setText("Cadastramento de paciente");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        jLabel6.setText("Estado Civil");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 190, -1, -1));

        txtEstadocivil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadocivilActionPerformed(evt);
            }
        });
        getContentPane().add(txtEstadocivil, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 210, 130, -1));

        jLabel8.setText("E-mail");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 240, -1, -1));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 260, 410, -1));

        jLabel7.setText("Telefone");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 240, -1, -1));

        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });
        getContentPane().add(txtTelefone, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 260, 180, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 190, 150));

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 480, -1));

        jLabel9.setText("Cidade");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 290, -1, -1));

        jLabel10.setText("Estado");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 290, -1, -1));

        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 350, 110, -1));

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

        txtSexo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSexoActionPerformed(evt);
            }
        });
        getContentPane().add(txtSexo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 210, 180, -1));

        jLabel12.setText("Sexo");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 190, -1, -1));

        tblPaciente.setModel(new javax.swing.table.DefaultTableModel(
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
        if (tblPaciente.getColumnModel().getColumnCount() > 0) {
            tblPaciente.getColumnModel().getColumn(0).setMinWidth(1);
            tblPaciente.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblPaciente.getColumnModel().getColumn(0).setMaxWidth(50);
            tblPaciente.getColumnModel().getColumn(7).setPreferredWidth(20);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 390, 1000, 270));

        jLabel11.setText("CPF");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 240, -1, -1));

        txtCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCpfActionPerformed(evt);
            }
        });
        getContentPane().add(txtCpf, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 260, 170, -1));

        jLabel13.setText("RG");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 240, -1, -1));

        txtRg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRgActionPerformed(evt);
            }
        });
        getContentPane().add(txtRg, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 260, 170, -1));

        jLabel14.setText("Numero");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 290, -1, -1));

        txtNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroActionPerformed(evt);
            }
        });
        getContentPane().add(txtNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 310, 60, -1));

        jLabel15.setText("Endereço");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 290, -1, -1));

        txtEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoActionPerformed(evt);
            }
        });
        getContentPane().add(txtEndereco, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 310, 300, -1));

        jLabel16.setText("Cep");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, -1, -1));

        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });
        getContentPane().add(txtCep, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, 90, -1));

        jLabel17.setText("Bairro");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 290, -1, 20));

        txtBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBairroActionPerformed(evt);
            }
        });
        getContentPane().add(txtBairro, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 310, 140, -1));

        txtLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginActionPerformed(evt);
            }
        });
        getContentPane().add(txtLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 360, 170, -1));

        lbLogin.setText("Nome de usuário");
        getContentPane().add(lbLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 340, -1, -1));

        lbSenha.setText("Senha provisória");
        getContentPane().add(lbSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 340, 120, -1));

        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });
        getContentPane().add(txtSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, 170, -1));

        btNovoCadastro.setText("Novo Cadastro");
        btNovoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoCadastroActionPerformed(evt);
            }
        });
        getContentPane().add(btNovoCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 350, 110, -1));

        getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 310, 160, -1));

        getContentPane().add(txtCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 310, 170, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtDataNascimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDataNascimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDataNascimentoActionPerformed

    private void txtEstadocivilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadocivilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEstadocivilActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void tblPacienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacienteMouseClicked
       setarCampos();
        btNovoCadastro.setEnabled(true);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        btExcluir.setEnabled(true);
        lbSenha.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        DefaultTableModel tableModel = (DefaultTableModel) tblPaciente.getModel();
        int row = tblPaciente.getSelectedRow();
        id_paciente_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString()); 
    }//GEN-LAST:event_tblPacienteMouseClicked

    private void txtCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCpfActionPerformed

    private void txtSexoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSexoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSexoActionPerformed

    private void txtRgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRgActionPerformed

    private void txtNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroActionPerformed

    private void txtEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoActionPerformed

    private void txtCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCepActionPerformed

    private void txtBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroActionPerformed

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        // TODO add your handling code here:
        if(verificaCamposSeEstaoTodosPreenchidos())
        {
            if(cadastrarPaciente())
            {    
                // criarLoginUsuario();
                carregarTabelaPacientes();
                btCadastrar.setEnabled(false); 
                btNovoCadastro.setEnabled(true);
                lbSenha.setVisible(false);
                lbLogin.setVisible(false);
                txtSenha.setVisible(false);
                txtLogin.setVisible(false);
                
            }
        }
    }//GEN-LAST:event_btCadastrarActionPerformed

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        // TODO add your handling code here:
        
        if (formQueAbriu != null) {
            // Aqui você pode chamar algum método do formulário que abriu
            // Por exemplo, se for uma classe específica, pode fazer um cast
            if (formQueAbriu instanceof PesquisaPaciente) 
            {
                ((PesquisaPaciente) formQueAbriu).carregarTabelaPacientes();
            } 
            else if(formQueAbriu instanceof TelaPrincipalAdministrador)
            {
                ((TelaPrincipalAdministrador) formQueAbriu).adicionarPacientesTabela();
            }
            
        }              
        this.dispose(); //Fechar o form atual
    }//GEN-LAST:event_btVoltarActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void txtLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginActionPerformed

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
        
        
        // int confirma = JOptionPane.showConfirmDialog(null, "Confirma o envio da receita para o e-mail do Paciente?", "Atenção", JOptionPane.YES_NO_OPTION);
       
    }//GEN-LAST:event_btNovoCadastroActionPerformed

    private void btAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar os dados deste paciente", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            atualizarCadastroPaciente();
            carregarTabelaPacientes();
        }
        
    }//GEN-LAST:event_btAlterarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        // TODO add your handling code here:
        
        excluirPaciente();
        carregarTabelaPacientes();
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
            java.util.logging.Logger.getLogger(CadastroPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroPaciente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        
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
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JTable tblPaciente;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtCep;
    private javax.swing.JComboBox txtCidade;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtDataNascimento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JComboBox txtEstado;
    private javax.swing.JTextField txtEstadocivil;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtRg;
    private javax.swing.JTextField txtSenha;
    private javax.swing.JTextField txtSexo;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
