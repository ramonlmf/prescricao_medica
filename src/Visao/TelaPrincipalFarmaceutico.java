/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;

import Controle.Bd_Conexao;
import Modelo.Cidade;
import Modelo.Estado;
import Modelo.LocalizacaoLoader;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ramon
 */
public class TelaPrincipalFarmaceutico extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    int idReceitaSelecionada;
    
    static int ultimoIdFarmaceuticoGerado;
    
    
    static int idUsuario_tb_usuario;    
    static int IdFarmaceutico_tb_usuario;
    
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
    public TelaPrincipalFarmaceutico() 
    {
        initComponents();
        carregarCidadesEstados();
        
         // Configurações do frame sempre quando abrir a janela
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowOpened(WindowEvent e) 
            {
                System.out.println("A janela foi aberta!");
                
                
                
                //significa que ja existe cadastro se nao vai ter que preencher para gerar um novo Farmaceutico
                if(IdFarmaceutico_tb_usuario != 0)
                {    
                    carregarDadosFarmaceutico();
                    adicionarReceitaTabela();
                    btCadastrar.setEnabled(false);                    
                }
                else
                {
                  btAtualizarFarmaceutico.setEnabled(false);
                  btPesquisarReceita.setEnabled(false);
                }    
                
                
                System.out.println("Último ID inserido usuario novo: " + idUsuario_tb_usuario);
            }
        });
    }
    
    // vai carregar o idFarmaceutico salvo na tabela tb_usuarios
    public void SetIdFarmaceutico(int id)
    {
        IdFarmaceutico_tb_usuario = id; 
    } 
    
    //vai carregar o idUsuario da tabela tb_usuario
    public void SetIdUsuario(int id)
    {
        idUsuario_tb_usuario = id;
    }
    
    public boolean jtableVazia(JTable table) {
        TableModel model = table.getModel();
        return model.getRowCount() == 0;
    }
    
    
     /**
     * Método responsável pela adicionar as receitas na tabela de visualizaçao     
     */
    private void adicionarReceitaTabela() {
        String sql = "select " +
                 "idReceita as Numero, " +
                 "nomepaciente as Paciente, " +
                 "cpf as CPF, " +
                 "nomemedico as Médico, " +
                 "crm as CRM, " +
                 "datareceita as Data, " +
                 "CASE WHEN idFarmaceutico IS NOT NULL THEN 'Retirado' ELSE 'Aguardando Retirada' END as \"Status do Medicamento\" " +
                 "from tb_receitas";

        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
          //  pst.setString(1, txtNome.getText() + "%");
            rs = pst.executeQuery();
            tblReceita.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        
        // Verifica se a tabela de receitas esta vazia
        if (jtableVazia(tblReceita) == true)
        {
            btPesquisarReceita.setEnabled(false);
        }
    }
    
    
    
    /**
     * Método responsável por cadastrar um novo Farmaceutico
     */
    
    
    private void cadastrarFarmaceutico() {
        String sql = "insert into tb_farmaceutico(nome, farmacia, endereco, telefone, email, estado, cidade, bairro, cep, cpf) values(?,?,?,?,?,?,?,?,?,?)";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pst.setString(1, txtNome.getText()); 
            pst.setString(2, txtFarmacia.getText());
            pst.setString(3, txtEndereco.getText());
            pst.setString(4, txtTelefone.getText());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtEstado.getSelectedItem().toString());
            pst.setString(7, txtCidade.getSelectedItem().toString()); 
            pst.setString(8, txtBairro.getText());
            pst.setString(9, txtCep.getText());
            pst.setString(10, txtCpf.getText());
            
            if ( txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtFarmacia.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtEndereco.getText().trim().isEmpty()  || txtBairro.getText().trim().isEmpty()
               || txtEstado.getSelectedItem().toString().trim().isEmpty() || txtCidade.getSelectedItem().toString().trim().isEmpty() ) 
            {   
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
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
    }
    
    
     /**
     * Método responsável por atualizar o idPaciente na tabela tb_usuario 
     */
    private void atualizarUsuario() {
        
       
       //String sql = "update tb_usuarios set idPaciente=? where idUsuario=?";
       String sql = "UPDATE tb_usuarios SET idFarmaceutico = ? WHERE idUsuario = ?";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setInt(1, ultimoIdFarmaceuticoGerado);// define o novo valor da coluna
            pst.setInt(2,idUsuario_tb_usuario);// define qual registro atualizar
            
                  System.out.println("Último ID inserido: " + ultimoIdFarmaceuticoGerado);
                  System.out.println("Último ID inserido usuario novo: " + idUsuario_tb_usuario);
            
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    // JOptionPane.showMessageDialog(null, "IdPaciente adicionado com sucesso na tb_usuario");  
                    SetIdFarmaceutico(ultimoIdFarmaceuticoGerado); //tem que setar pois quando habilita o botao atualizar o idFarmaceutico é zero inicialmente
                    btAtualizarFarmaceutico.setEnabled(true);
                    btCadastrar.setEnabled(false);
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
    }
    
    
    
     /**
     * Método responsável por atualizar o cadastro de um paciente
     */
    private void atualizarCadastroFarmaceutico() {
        
        //String sql = "insert into tb_pacientes(nome,datadenascimento,cpf,telefone,email,estado,cidade,estadocivil,sexo,rg,cep,endereco,numero,bairro) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
       String sql = "update tb_farmaceutico set nome=?,farmacia=?,endereco=?,telefone=?,email=?,estado=?,cidade=?,bairro=?,cep=?,cpf=? where idFarmaceutico=?";
        
               
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setString(1, txtNome.getText());
            pst.setString(2, txtFarmacia.getText());
            pst.setString(3, txtEndereco.getText());
            pst.setString(4, txtTelefone.getText());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtEstado.getSelectedItem().toString());
            pst.setString(7, txtCidade.getSelectedItem().toString()); 
            pst.setString(8, txtBairro.getText());
            pst.setString(9, txtCep.getText());
            pst.setString(10, txtCpf.getText());
            pst.setString(11, Integer.toString(IdFarmaceutico_tb_usuario)); //atualiza o indice do paciente acho que naso precisa na verdade nao acontece anada
            
            if ( txtNome.getText().trim().isEmpty() || txtFarmacia.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() 
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtCep.getText().trim().isEmpty()
               || txtEndereco.getText().trim().isEmpty()  || txtBairro.getText().trim().isEmpty()
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
    }
    
     /**
     * método usado para setar os campos de edicao com o conteúdo da tabela do banco de dados
     */
    private void carregarDadosFarmaceutico() {   
                
        String sql = "select idFarmaceutico, nome, farmacia, endereco, telefone, email, estado, cidade, bairro, cep, cpf from tb_farmaceutico where idFarmaceutico like ?";
          
        
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, IdFarmaceutico_tb_usuario + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
                        
            txtNome.setText(rs.getString(2));            
            txtFarmacia.setText(rs.getString(3));
            txtEndereco.setText(rs.getString(4));
            txtTelefone.setText(rs.getString(5));
            txtEmail.setText(rs.getString(6));
            //txtEstado.setText(rs.getString(7));
           // txtCidade.setText(rs.getString(8));
            setarCidadeEstado(rs.getString(7),rs.getString(8)) ;
            txtBairro.setText(rs.getString(9)); 
            
            txtCep.setText(rs.getString(10));
            txtCpf.setText(rs.getString(11));
            
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
    
    
    
    
    private void impressaoReceita()  {
        
        DefaultTableModel tableModel = (DefaultTableModel) tblReceita.getModel();
        int row = tblReceita.getSelectedRow();
               
        conexao = Bd_Conexao.conectar();
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta Receita?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) 
        {
             /*
            // aqui gera um pdf e abre
            try {
                HashMap filtro = new HashMap();
                filtro.put("idreceita", Integer.parseInt(tableModel.getValueAt(row, 0).toString()));

                // Obtendo o InputStream do relatório Jasper
                InputStream inputStream = getClass().getResourceAsStream("/Visao/report/receita_medica.jasper");
                if (inputStream != null) {
                    // Preenche o relatório com os dados
                    JasperPrint print = JasperFillManager.fillReport(inputStream, filtro, conexao);
                    inputStream.close();
                    conexao.close();

                    // Exporta para PDF
                    String pdfPath = "relatorio_receita_medica.pdf";
                    JasperExportManager.exportReportToPdfFile(print, pdfPath);

                    // Abre o PDF automaticamente
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(new File(pdfPath));
                    } else {
                        JOptionPane.showMessageDialog(null, "O recurso de abrir arquivos não é suportado neste sistema.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Recurso não encontrado!");
                }
            } catch (NumberFormatException | SQLException | JRException | IOException e) {
                JOptionPane.showMessageDialog(null, e);
                e.printStackTrace();
            }
            */
            
            
          
            try {
                HashMap filtro = new HashMap();
                filtro.put("idreceita", Integer.parseInt(tableModel.getValueAt(row, 0).toString()));

                // Obtendo o InputStream do recurso
                InputStream inputStream = getClass().getResourceAsStream("/Visao/report/receita_medica.jasper");
                if (inputStream != null) {
                   //  JOptionPane.showMessageDialog(null, "Recurso ok");
                    // Preencha o relatório com os dados, usando o filtro e a conexão
                    JasperPrint print = JasperFillManager.fillReport(inputStream, filtro, conexao);
                    JasperViewer.viewReport(print, false);
                    inputStream.close();
                    conexao.close();
                } else {
                    JOptionPane.showMessageDialog(null, "Recurso não encontrado!");
                }
            } catch (NumberFormatException | SQLException | JRException | IOException e) {
                JOptionPane.showMessageDialog(null, e);
                e.printStackTrace();
            }
            
        }
        
                
               // InputStream inputStream = getClass().getResourceAsStream("/Visao/report/receita_medica.jasper");
                //System.out.println("Caminho do InputStream: " + getClass().getResource("/Visao/report/receita_medica.jasper").getPath());
                //JasperPrint print = JasperFillManager.fillReport(inputStream, filtro, conexao);
       
    }
    
    private void atualizarStatusMedicamentoReceita(int idDoFarmaceuticoAlterouStatus) {
        
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a retirada do medicamento?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) 
        {    
        
            String sql = "UPDATE tb_receitas SET idFarmaceutico = ? WHERE idReceita = ?"; 

            try {
                conexao = Bd_Conexao.conectar();
                PreparedStatement pst = conexao.prepareStatement(sql);
                pst.setInt(1, idDoFarmaceuticoAlterouStatus);

                pst.setInt(2, idReceitaSelecionada); 

                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Retirada de Medicamento confirmada com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhuma receita encontrada com esse ID.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar: " + e.getMessage());
            } finally {
                try {
                    if (conexao != null) conexao.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + ex.getMessage());
                }
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

        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btAtualizarFarmaceutico = new javax.swing.JButton();
        btSair = new javax.swing.JButton();
        btPesquisarReceita = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtCpf = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtFarmacia = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtCep = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblReceita = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        btCadastrar = new javax.swing.JButton();
        btVisualizaReceita1 = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox();
        txtCidade = new javax.swing.JComboBox();
        brRetirarMedicamento = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Portal do Farmaceutico");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Receitas dos pacientes");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 380, 190, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setText("Portal do Farmacêutico");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 440, -1));

        jLabel8.setText("E-mail");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 240, -1, -1));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 260, 310, -1));

        jLabel7.setText("Telefone");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 240, -1, -1));

        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });
        getContentPane().add(txtTelefone, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 260, 180, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 190, 150));

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 790, -1));

        jLabel9.setText("Cidade");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 300, -1, -1));

        jLabel10.setText("Estado");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 300, -1, -1));

        btAtualizarFarmaceutico.setText("Atualizar");
        btAtualizarFarmaceutico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAtualizarFarmaceuticoActionPerformed(evt);
            }
        });
        getContentPane().add(btAtualizarFarmaceutico, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, 110, -1));

        btSair.setText("Sair");
        btSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSairActionPerformed(evt);
            }
        });
        getContentPane().add(btSair, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 620, 120, -1));

        btPesquisarReceita.setText("Pesquisar Receita");
        btPesquisarReceita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarReceitaActionPerformed(evt);
            }
        });
        getContentPane().add(btPesquisarReceita, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 470, 140, 50));

        jLabel11.setText("CPF");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 190, -1, -1));

        txtCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCpfActionPerformed(evt);
            }
        });
        getContentPane().add(txtCpf, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 210, 190, -1));

        jLabel13.setText("Nome Farmacia");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 240, 230, -1));

        txtFarmacia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFarmaciaActionPerformed(evt);
            }
        });
        getContentPane().add(txtFarmacia, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 260, 470, -1));

        jLabel15.setText("Endereço da Farmácia");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 300, 170, -1));

        txtEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoActionPerformed(evt);
            }
        });
        getContentPane().add(txtEndereco, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 320, 320, -1));

        jLabel16.setText("Cep");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 300, -1, -1));

        txtCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCepActionPerformed(evt);
            }
        });
        getContentPane().add(txtCep, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 320, 90, -1));

        jLabel17.setText("Bairro");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 300, -1, 20));

        txtBairro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBairroActionPerformed(evt);
            }
        });
        getContentPane().add(txtBairro, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 320, 140, -1));

        tblReceita.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Numero", "Paciente", "CPF", "Medico", "CRM", "Data"
            }
        ));
        tblReceita.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReceitaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblReceita);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 400, 1000, 250));

        jLabel18.setText("Nome completo");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, -1, -1));

        btCadastrar.setText("Cadastrar");
        btCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarActionPerformed(evt);
            }
        });
        getContentPane().add(btCadastrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 210, 110, -1));

        btVisualizaReceita1.setText("Visualizar Receita");
        btVisualizaReceita1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btVisualizaReceita1ActionPerformed(evt);
            }
        });
        getContentPane().add(btVisualizaReceita1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, 140, 50));

        getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 320, 180, -1));

        getContentPane().add(txtCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 320, 200, -1));

        brRetirarMedicamento.setText("Confirmar Retirada");
        brRetirarMedicamento.setEnabled(false);
        brRetirarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brRetirarMedicamentoActionPerformed(evt);
            }
        });
        getContentPane().add(brRetirarMedicamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 370, 150, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, -1, -1));
        jLabel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCpfActionPerformed

    private void txtFarmaciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFarmaciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFarmaciaActionPerformed

    private void txtEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoActionPerformed

    private void txtCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCepActionPerformed

    private void txtBairroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBairroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBairroActionPerformed

    private void btAtualizarFarmaceuticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAtualizarFarmaceuticoActionPerformed
        // TODO add your handling code here:
        atualizarCadastroFarmaceutico();
    }//GEN-LAST:event_btAtualizarFarmaceuticoActionPerformed

    private void btSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSairActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btSairActionPerformed

    private void btCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarActionPerformed
        // TODO add your handling code here:
        cadastrarFarmaceutico();
        atualizarUsuario();       
        
    }//GEN-LAST:event_btCadastrarActionPerformed

    private void btVisualizaReceita1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVisualizaReceita1ActionPerformed
        // TODO add your handling code here:
        impressaoReceita();
    }//GEN-LAST:event_btVisualizaReceita1ActionPerformed

    private void btPesquisarReceitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarReceitaActionPerformed
        // TODO add your handling code here:
        PesquisaReceitas formPesquisaReceitas = new PesquisaReceitas();
        formPesquisaReceitas.setVisible(true);
        
    }//GEN-LAST:event_btPesquisarReceitaActionPerformed

    private void brRetirarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brRetirarMedicamentoActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tableModel = (DefaultTableModel) tblReceita.getModel();
        int row = tblReceita.getSelectedRow();        
        idReceitaSelecionada =  Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        atualizarStatusMedicamentoReceita(IdFarmaceutico_tb_usuario);
        adicionarReceitaTabela();       
        
    }//GEN-LAST:event_brRetirarMedicamentoActionPerformed

    private void tblReceitaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReceitaMouseClicked
        // TODO add your handling code here:
        DefaultTableModel tableModel = (DefaultTableModel) tblReceita.getModel();
        int row = tblReceita.getSelectedRow();        
        String status =  tableModel.getValueAt(row, 6).toString().trim();
        
        if(status.equals("Aguardando Retirada"))
        {
           brRetirarMedicamento.setEnabled(true);
        }
        else
        {
            brRetirarMedicamento.setEnabled(false);
        }
        
    }//GEN-LAST:event_tblReceitaMouseClicked

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
            java.util.logging.Logger.getLogger(TelaPrincipalFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalFarmaceutico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                new TelaPrincipalFarmaceutico().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brRetirarMedicamento;
    private javax.swing.JButton btAtualizarFarmaceutico;
    private javax.swing.JButton btCadastrar;
    private javax.swing.JButton btPesquisarReceita;
    private javax.swing.JButton btSair;
    private javax.swing.JButton btVisualizaReceita1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblReceita;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtCep;
    private javax.swing.JComboBox txtCidade;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JComboBox txtEstado;
    private javax.swing.JTextField txtFarmacia;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
