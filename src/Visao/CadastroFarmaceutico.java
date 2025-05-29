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
public class CadastroFarmaceutico extends javax.swing.JFrame {
    
    
    
    private JFrame formQueAbriu;
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    static int id_farmaceutico_selecionado;
    
    static int ultimoIdFarmaceuticoGerado;
    
    
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
     
    
     // Método para definir qual formulário abriu o FormCadastroPAciente
    public void setFormQueAbriu(JFrame form) 
    {
        this.formQueAbriu = form;
    }    
   
    /**
     * Creates new form CadastroFarmaceutico
     */
    public CadastroFarmaceutico() {
        initComponents();
        
         ((DefaultTableModel) tblFarmaceuticos.getModel()).setRowCount(0);
        btNovoCadastro.setEnabled(false);
        btAlterar.setEnabled(false);
        btExcluir.setEnabled(false);
        
        carregarTabelaFarmaceuticos();
        
        carregarCidadesEstados();
    }
    
     private void limpar()
    {
        txtNome.setText("");
        txtEnderecoFarmacia.setText("");
        txtFarmacia.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEstado.addItem("");
        txtCidade.addItem("");
        txtCep.setText("");
        txtBairro.setText("");
        txtCpf.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        
        ((DefaultTableModel) tblFarmaceuticos.getModel()).setRowCount(0);
    }
    
    /**
     * Método responsável para prencher a tabela com farmaceuticos na inicializacao
     */
    private void carregarTabelaFarmaceuticos() {
         
        String sql = "select idFarmaceutico as Id, nome as Nome, farmacia as Farmacia, endereco as Endereço, telefone as Telefone, email as Email, estado as Estado, cidade as Cidade from tb_farmaceutico";
                
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblFarmaceuticos.setModel(DbUtils.resultSetToTableModel(rs));
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
        if ( txtNome.getText().trim().isEmpty() || txtEnderecoFarmacia.getText().trim().isEmpty() || txtFarmacia.getText().trim().isEmpty()               
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtBairro.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
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
     * Método responsável por cadastrar um novo farmaceutico
     */    
    private boolean cadastrarFarmaceutico() {
        
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
            existe = busca.valorExiste("tb_farmaceutico", "email", txtEmail.getText().trim());
            if(existe == true)
            {
                JOptionPane.showMessageDialog(null, "E-mail já existente.\nEscolha outro nome.");
            }
            else
            {
            
                String sql = "insert into tb_farmaceutico(nome, farmacia, endereco, telefone, email,estado,cidade,bairro,cep,cpf) values(?,?,?,?,?,?,?,?,?,?)";
        
                    try {
                        conexao = Bd_Conexao.conectar();            
                        pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                        pst.setString(1, txtNome.getText());
                        pst.setString(2, txtFarmacia.getText());
                        pst.setString(3, txtEnderecoFarmacia.getText());
                        pst.setString(4, txtTelefone.getText());
                        pst.setString(5, txtEmail.getText());
                        pst.setString(6, txtEstado.getSelectedItem().toString());
                        pst.setString(7, txtCidade.getSelectedItem().toString());
                        pst.setString(8, txtBairro.getText());
                        pst.setString(9, txtCep.getText());
                        pst.setString(10, txtCpf.getText());

                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Cadastrado com sucesso"); 

                                // Obtém o último ID gerado
                                rs = pst.getGeneratedKeys();
                                if (rs.next()) {
                                    ultimoIdFarmaceuticoGerado = rs.getInt(1);
                                    System.out.println("Último ID inserido: " + ultimoIdFarmaceuticoGerado);
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
        String sql = "insert into tb_usuarios(login, senha, perfil, idFarmaceutico) values(?,?,?,?)";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtLogin.getText());
            pst.setString(2, txtSenha.getText());
            pst.setString(3, "Farmaceutico");
            pst.setInt(4, ultimoIdFarmaceuticoGerado);
            
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
    
      
    //método usado para setar os campos de texto com o conteúdo da tabela 
     
    private void setarCampos() {
        int setar = tblFarmaceuticos.getSelectedRow(); // pega a linha selecionada da tabela
        txtNome.setText(tblFarmaceuticos.getModel().getValueAt(setar, 1).toString());
        txtFarmacia.setText(tblFarmaceuticos.getModel().getValueAt(setar, 2).toString());
        txtEnderecoFarmacia.setText(tblFarmaceuticos.getModel().getValueAt(setar, 3).toString());
        txtTelefone.setText(tblFarmaceuticos.getModel().getValueAt(setar, 4).toString());
        txtEmail.setText(tblFarmaceuticos.getModel().getValueAt(setar, 5).toString());
        
        setarCidadeEstado(tblFarmaceuticos.getModel().getValueAt(setar, 6).toString(),tblFarmaceuticos.getModel().getValueAt(setar, 7).toString());
        //txtEstado.setSelectedItem(tblFarmaceuticos.getModel().getValueAt(setar, 6).toString());
        //txtCidade.setSelectedItem(tblFarmaceuticos.getModel().getValueAt(setar, 7).toString());   
        
        
        System.out.print(tblFarmaceuticos.getModel().getValueAt(setar, 6).toString());
        
        String sql = "select idFarmaceutico, nome, bairro, cep, cpf from tb_farmaceutico where nome like ?";
                
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNome.getText() + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            txtBairro.setText(rs.getString(3));            
            txtCep.setText(rs.getString(4));
            txtCpf.setText(rs.getString(5));
                         
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
    private boolean verificarLoginFarmaceutico(String login, String senha) {        
       
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
    private void excluirFarmaceutico() {
        
        
        DefaultTableModel tableModel = (DefaultTableModel) tblFarmaceuticos.getModel();
        int row = tblFarmaceuticos.getSelectedRow();
        int id_paciente_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString());       
        
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este Farmaceutico", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            
            String login = JOptionPane.showInputDialog(null, "Digite o nome de usuario a ser excluidio:");
            String senha = JOptionPane.showInputDialog(null, "Digite a senha do usuario a ser excluido:");
            
            if(verificarLoginFarmaceutico(login, senha))
            {
                //Exclui o login do paciente primeiro nesta ordem para tirar a chave estrangeira...
                //Ou ajustar a restrição de chave estrangeira para permitir ações em cascata, assim ao deletar um paciente, os registros 
                //relacionados na tabela tb_usuarios também seriam removidos automaticamente
                conexao = Bd_Conexao.conectar();
                String sql = "delete from tb_usuarios where idFarmaceutico=?";
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
                sql = "delete from tb_farmaceutico where idFarmaceutico=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, id_paciente_selecionado);

                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        limpar();
                        JOptionPane.showMessageDialog(null, "Farmaceutico excluído com sucesso");
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
     * Método responsável por atualizar o cadastro de um farmaceutico
     */
    private void atualizarCadastroFarmaceutico() {
        
        
        String sql = "update tb_farmaceutico set nome=?, farmacia=?, endereco=?, telefone=?, email=?,estado=?,cidade=?,bairro=?,cep=?,cpf=? where idFarmaceutico=?";
        
                
        try 
        {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtFarmacia.getText());
            pst.setString(3, txtEnderecoFarmacia.getText());
            pst.setString(4, txtTelefone.getText());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtEstado.getSelectedItem().toString());
            pst.setString(7, txtCidade.getSelectedItem().toString());
            pst.setString(8, txtBairro.getText());
            pst.setString(9, txtCep.getText());
            pst.setString(10, txtCpf.getText());           
            pst.setInt(11, id_farmaceutico_selecionado); //atualiza o indice do farmaceutico
            
            if ( txtNome.getText().trim().isEmpty() || txtEnderecoFarmacia.getText().trim().isEmpty() || txtFarmacia.getText().trim().isEmpty()               
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtBairro.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
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
     * método usado para setar os campos de texto para fazer a alterarcao vinda do form de PesquisaFarmaceutico
     */
    public void setarCamposDaPesquisaFarmaceutico(int id_farmaceutico) {
          
        id_farmaceutico_selecionado = id_farmaceutico;
        btNovoCadastro.setEnabled(false);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        lbLogin.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        
        String sql = "select nome, farmacia, endereco, telefone, email,estado,cidade,bairro,cep,cpf from tb_farmaceutico where idFarmaceutico like ?";
                    
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, id_farmaceutico + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
                        
            txtNome.setText(rs.getString(1));
            txtFarmacia.setText(rs.getString(2));
            txtEnderecoFarmacia.setText(rs.getString(3));            
            txtTelefone.setText(rs.getString(4));
            txtEmail.setText(rs.getString(5));
            
            
            //txtEstado.setSelectedItem(rs.getString(6));
            //txtCidade.setSelectedItem(rs.getString(7));  
            setarCidadeEstado(rs.getString(6),rs.getString(7));
        
            
            txtBairro.setText(rs.getString(8));
            txtCep.setText(rs.getString(9));            
            txtCpf.setText(rs.getString(10));
                       
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
        txtFarmacia = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEnderecoFarmacia = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btCadastrar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btVoltar = new javax.swing.JButton();
        btAlterar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFarmaceuticos = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtCep = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        btNovoCadastro = new javax.swing.JButton();
        txtLogin = new javax.swing.JTextField();
        lbLogin = new javax.swing.JLabel();
        lbSenha = new javax.swing.JLabel();
        txtSenha = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtCpf = new javax.swing.JTextField();
        txtEstado = new javax.swing.JComboBox();
        txtCidade = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cadastro de Farmacêutico");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("Farmácia");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 190, -1, -1));

        jLabel5.setText("Nome completo");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        txtFarmacia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFarmaciaActionPerformed(evt);
            }
        });
        getContentPane().add(txtFarmacia, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 210, 250, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setText("Cadastramento do farmacêutico");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, -1, -1));

        jLabel6.setText("Endereço da farmácia");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 190, -1, -1));

        txtEnderecoFarmacia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoFarmaciaActionPerformed(evt);
            }
        });
        getContentPane().add(txtEnderecoFarmacia, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 210, 210, -1));

        jLabel8.setText("Estado");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, -1, -1));

        jLabel7.setText("Bairro");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 240, -1, -1));

        txtBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBairroActionPerformed(evt);
            }
        });
        getContentPane().add(txtBairro, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 260, 150, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 190, 150));

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 310, -1));

        jLabel9.setText("Email");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 240, -1, -1));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 260, 210, -1));

        jLabel10.setText("Cidade");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 240, -1, -1));

        btCadastrar.setText("Cadastar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 300, 110, -1));

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

        tblFarmaceuticos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblFarmaceuticos);
        if (tblFarmaceuticos.getColumnModel().getColumnCount() > 0) {
            tblFarmaceuticos.getColumnModel().getColumn(0).setMinWidth(1);
            tblFarmaceuticos.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblFarmaceuticos.getColumnModel().getColumn(0).setMaxWidth(50);
            tblFarmaceuticos.getColumnModel().getColumn(7).setPreferredWidth(20);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 340, 1000, 310));

        jLabel11.setText("Cep");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 240, -1, -1));

        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });
        getContentPane().add(txtCep, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 260, 140, -1));

        jLabel12.setText("Telefone");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 240, -1, -1));

        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });
        getContentPane().add(txtTelefone, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 260, 150, -1));

        btNovoCadastro.setText("Novo Cadastro");
        btNovoCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoCadastroActionPerformed(evt);
            }
        });
        getContentPane().add(btNovoCadastro, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 300, 110, -1));

        txtLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLoginActionPerformed(evt);
            }
        });
        getContentPane().add(txtLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, 170, -1));

        lbLogin.setText("Nome de Usuario");
        getContentPane().add(lbLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, 120, -1));

        lbSenha.setText("Senha");
        getContentPane().add(lbSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 290, 60, -1));

        txtSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSenhaActionPerformed(evt);
            }
        });
        getContentPane().add(txtSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 310, 170, -1));

        jLabel15.setText("CPF");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 190, 30, -1));
        getContentPane().add(txtCpf, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, 170, -1));

        getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 260, 150, -1));

        getContentPane().add(txtCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 260, 140, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtFarmaciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFarmaciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFarmaciaActionPerformed

    private void txtEnderecoFarmaciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoFarmaciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoFarmaciaActionPerformed

    private void txtBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void tblFarmaceuticosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFarmaceuticosMouseClicked
        setarCampos();
        btNovoCadastro.setEnabled(true);
        btCadastrar.setEnabled(false);
        btAlterar.setEnabled(true);
        btExcluir.setEnabled(true);
        lbSenha.setVisible(false);
        lbLogin.setVisible(false);
        txtSenha.setVisible(false);
        txtLogin.setVisible(false);
        DefaultTableModel tableModel = (DefaultTableModel) tblFarmaceuticos.getModel();
        int row = tblFarmaceuticos.getSelectedRow();
        id_farmaceutico_selecionado = Integer.parseInt(tableModel.getValueAt(row, 0).toString()); 
    }//GEN-LAST:event_tblFarmaceuticosMouseClicked

    private void txtCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCepActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void btVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVoltarActionPerformed
        // TODO add your handling code here:
        if (formQueAbriu != null) {
            // Aqui você pode chamar algum método do formulário que abriu
            // Por exemplo, se for uma classe específica, pode fazer um cast
            if (formQueAbriu instanceof PesquisarFarmaceutico) 
            {
                ((PesquisarFarmaceutico) formQueAbriu).carregarTabelaFarmaceuticos();
            } 
            else if(formQueAbriu instanceof TelaPrincipalAdministrador)
            {
                ((TelaPrincipalAdministrador) formQueAbriu).adicionarFarmaceuticosTabela();
            }
        }              
        this.dispose(); //Fechar o form atual
    }//GEN-LAST:event_btVoltarActionPerformed

    private void txtLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLoginActionPerformed

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSenhaActionPerformed

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        // TODO add your handling code here:
        if(verificaCamposSeEstaoTodosPreenchidos())
        {
            if(cadastrarFarmaceutico())
            {    
                // criarLoginUsuario();
                carregarTabelaFarmaceuticos();
                btCadastrar.setEnabled(false); 
                btNovoCadastro.setEnabled(true);
                lbSenha.setVisible(false);
                lbLogin.setVisible(false);
                txtSenha.setVisible(false);
                txtLogin.setVisible(false);
                
            }
        }
        
    }//GEN-LAST:event_btCadastrarActionPerformed

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

    private void btAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja alterar os dados deste paciente", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) 
        {
            atualizarCadastroFarmaceutico();
            carregarTabelaFarmaceuticos();
        }
    }//GEN-LAST:event_btAlterarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        // TODO add your handling code here:
        excluirFarmaceutico();
        carregarTabelaFarmaceuticos();
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
            java.util.logging.Logger.getLogger(CadastroFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CadastroFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CadastroFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CadastroFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CadastroFarmaceutico().setVisible(true);
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
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JTable tblFarmaceuticos;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtCep;
    private javax.swing.JComboBox txtCidade;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEnderecoFarmacia;
    private javax.swing.JComboBox txtEstado;
    private javax.swing.JTextField txtFarmacia;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtSenha;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
