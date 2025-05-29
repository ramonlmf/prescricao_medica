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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;




/**
 *
 * @author ramon
 */
public class TelaPrincipalMedico extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
   
    static int ultimoIdMedicoGerado;   
    
    static int idUsuario_tb_usuario;    
    static int IdMedico_tb_usuario;
    
    
    
    public interface Atualizavel 
    {
        void atualizarReceitas();
    }
    
    
     // vai carregar o idMedico salvo na tabela tb_usuarios
    public void SetIdMedico(int id)
    {
        IdMedico_tb_usuario = id; 
    } 
    
    //vai carregar o idUsuario da tabela tb_usuario
    public void SetIdUsuario(int id)
    {
        idUsuario_tb_usuario = id;
    }
    
    
    

    /**
     * Creates new form Principal
     */
    public TelaPrincipalMedico() {
        initComponents();
        carregarCidadesEstados();
        
         // Configurações do frame sempre quando abrir a janela
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowOpened(WindowEvent e) 
            {
                System.out.println("A janela foi aberta!");
                
                //significa que ja existe cadastro se nao vai ter que preencher para gerar um novo paciente
                if(IdMedico_tb_usuario != 0)
                {    
                    carregarDadosMedico();
                    adicionarReceitaTabela();
                    btCadastrarMedico.setEnabled(false);                    
                }
                else
                {
                  btAtualizarMedico.setEnabled(false);
                  btVisualizaReceita.setEnabled(false);
                } 
                
                System.out.println("Último ID inserido usuario novo: " + idUsuario_tb_usuario);
            }
        });                
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
     * método usado para setar os campos de edicao com o conteúdo da tabela do banco de dados
     */
    private void carregarDadosMedico() 
    {   
                
        String sql = "select idMedico, nomemedico, especialidade, crm, telefone, email, estado, cidade, clinica, enderecoclinica from tb_medico where idMedico like?";
                
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, IdMedico_tb_usuario + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();
            
            txtNome.setText(rs.getString(2));            
            txtEspecialidade.setText(rs.getString(3));
            txtCrm.setText(rs.getString(4));
            txtTelefone.setText(rs.getString(5));
            txtEmail.setText(rs.getString(6));
            //txtEstado.setText(rs.getString(7));
           // txtCidade.setText(rs.getString(8));
            
            setarCidadeEstado(rs.getString(7),rs.getString(8)) ;
            txtClinica.setText(rs.getString(9)); 
            txtEndereco.setText(rs.getString(10));
            
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
     * Método responsável por cadastrar um novo medico
     */    
    private void cadastrarMedico() {
        String sql = "insert into tb_medico(nomemedico, especialidade, crm, telefone, email, estado, cidade, clinica, enderecoclinica) values(?,?,?,?,?,?,?,?,?)";
        
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
            
            
            if ( txtNome.getText().trim().isEmpty() || txtEspecialidade.getText().trim().isEmpty() || txtCrm.getText().trim().isEmpty()
               || txtTelefone.getText().trim().isEmpty() || txtClinica.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty() 
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
                            ultimoIdMedicoGerado = rs.getInt(1);
                            System.out.println("Último ID inserido: " + ultimoIdMedicoGerado);
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
     * Método responsável por atualizar o idMedico na tabela tb_usuario 
     */
    private void atualizarUsuario() {
        
       
       //String sql = "update tb_usuarios set idPaciente=? where idUsuario=?";
       String sql = "UPDATE tb_usuarios SET idMedico = ? WHERE idUsuario = ?";
        
        try {
            conexao = Bd_Conexao.conectar();            
            pst = conexao.prepareStatement(sql);
            
            pst.setInt(1, ultimoIdMedicoGerado);// define o novo valor da coluna
            pst.setInt(2,idUsuario_tb_usuario);// define qual registro atualizar
            
                  System.out.println("Último ID inserido: " + ultimoIdMedicoGerado);
                  System.out.println("Último ID inserido usuario novo: " + idUsuario_tb_usuario);
            
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    // JOptionPane.showMessageDialog(null, "IdPaciente adicionado com sucesso na tb_usuario");  
                    SetIdMedico(ultimoIdMedicoGerado); //tem que setar pois quando habilita o botao atualizar o idPaciente é zero inicialmente
                    btAtualizarMedico.setEnabled(true);
                    btCadastrarMedico.setEnabled(false);
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
    private void atualizarCadastroMedico() {
        
        //String sql = "insert into tb_pacientes(nome,datadenascimento,cpf,telefone,email,estado,cidade,estadocivil,sexo,rg,cep,endereco,numero,bairro) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
       String sql = "update tb_medico set nomemedico=?, especialidade=?, crm=?, telefone=?, email=?, estado=?, cidade=?, clinica=?, enderecoclinica=? where idMedico=?";
        
       
        
        try {
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
            pst.setString(10, Integer.toString(IdMedico_tb_usuario)); //atualiza o indice do medico
            
            if ( txtNome.getText().trim().isEmpty() || txtEspecialidade.getText().trim().isEmpty() || txtCrm.getText().trim().isEmpty()
               || txtTelefone.getText().trim().isEmpty() || txtClinica.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty()
               || txtEmail.getText().trim().isEmpty() || txtTelefone.getText().trim().isEmpty() || txtEndereco.getText().trim().isEmpty() 
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
     * Método responsável pela adicionar as receitas na tabela de visualizaçao     
     */
    public void adicionarReceitaTabela() {
       String sql = "select " +
                 "idReceita as Numero, " +
                 "nomepaciente as Paciente, " +
                 "cpf as CPF, " +
                 "nomemedico as Médico, " +
                 "crm as CRM, " +
                 "datareceita as Data, " +
                 "CASE WHEN idFarmaceutico IS NOT NULL THEN 'Retirado' ELSE 'Aguardando Retirada' END as \"Status do Medicamento\" " +
                 "from tb_receitas where nomemedico like ?";
       
        
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNome.getText() + "%");
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
    }
    
    
    
  
    
    private void impressaoReceita()  
    {
        
        DefaultTableModel tableModel = (DefaultTableModel) tblReceita.getModel();
        int row = tblReceita.getSelectedRow();
        
       
        conexao = Bd_Conexao.conectar();
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta Receita?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                HashMap filtro = new HashMap();
                filtro.put("idreceita", Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/Visao/report/receita_medica.jasper"), filtro, conexao);
                JasperViewer.viewReport(print, false);
                conexao.close();
            } catch (NumberFormatException | SQLException | JRException e) {
                JOptionPane.showMessageDialog(null, e);
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

        btVisualizaReceita = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblReceita = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtEspecialidade = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTelefone = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtClinica = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtEndereco = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCrm = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btAtualizarMedico = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btCadastrarMedico = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btCadastrarFarmaceutico = new javax.swing.JButton();
        btPesquisarFarmaceutico = new javax.swing.JButton();
        btPesquisarPaciente = new javax.swing.JButton();
        btCadastrarPaciente = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        btCadastrarMedicamento = new javax.swing.JButton();
        btPesquisarMedicamento = new javax.swing.JButton();
        btPrescreverMedicamento = new javax.swing.JButton();
        txtEstado = new javax.swing.JComboBox();
        txtCidade = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Portal do Médico");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btVisualizaReceita.setText("Visualiza Receita");
        btVisualizaReceita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btVisualizaReceitaActionPerformed(evt);
            }
        });
        getContentPane().add(btVisualizaReceita, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 630, 130, 30));

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
        jScrollPane3.setViewportView(tblReceita);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 340, 930, 270));

        jLabel11.setText("Nome completo");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 130, -1, -1));

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 150, 390, -1));

        jLabel39.setText("Receitas ");
        getContentPane().add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 320, 130, -1));

        jLabel19.setText("Especialidade");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 130, -1, -1));

        txtEspecialidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEspecialidadeActionPerformed(evt);
            }
        });
        getContentPane().add(txtEspecialidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 150, 390, -1));

        jLabel7.setText("Telefone");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 190, -1, -1));

        txtTelefone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefoneActionPerformed(evt);
            }
        });
        getContentPane().add(txtTelefone, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 210, 170, -1));

        jLabel23.setText("Clinica");
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 240, -1, -1));

        txtClinica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClinicaActionPerformed(evt);
            }
        });
        getContentPane().add(txtClinica, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 260, 170, -1));

        jLabel8.setText("E-mail");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 190, -1, -1));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 210, 280, -1));

        txtEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEnderecoActionPerformed(evt);
            }
        });
        getContentPane().add(txtEndereco, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 260, 280, -1));

        jLabel12.setText("Endereço");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 240, -1, -1));

        jLabel10.setText("Estado");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 190, -1, -1));

        jLabel9.setText("Cidade");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 190, -1, -1));

        txtCrm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCrmActionPerformed(evt);
            }
        });
        getContentPane().add(txtCrm, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 150, 70, -1));

        jLabel38.setText("CRM");
        getContentPane().add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 130, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setText("Portal do Médico");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 350, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Paciente");

        btAtualizarMedico.setText("Alterar");
        btAtualizarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAtualizarMedicoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Médico");

        btCadastrarMedico.setText("Cadastrar");
        btCadastrarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarMedicoActionPerformed(evt);
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

        btPesquisarFarmaceutico.setText("Pesquisar");
        btPesquisarFarmaceutico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarFarmaceuticoActionPerformed(evt);
            }
        });

        btPesquisarPaciente.setText("Pesquisar");
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

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Medicamento");

        btCadastrarMedicamento.setText("Cadastrar");
        btCadastrarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCadastrarMedicamentoActionPerformed(evt);
            }
        });

        btPesquisarMedicamento.setText("Pesquisar");
        btPesquisarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarMedicamentoActionPerformed(evt);
            }
        });

        btPrescreverMedicamento.setText("Prescrição");
        btPrescreverMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrescreverMedicamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 21, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btAtualizarMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btCadastrarMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(btPrescreverMedicamento, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btCadastrarFarmaceutico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btPesquisarFarmaceutico, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(2, 2, 2)))
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(57, 57, 57))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(58, 58, 58))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastrarMedico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAtualizarMedico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPrescreverMedicamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastrarPaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarPaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btCadastrarFarmaceutico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPesquisarFarmaceutico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCadastrarMedicamento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btPesquisarMedicamento)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 200, 620));

        getContentPane().add(txtEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 210, 180, -1));

        getContentPane().add(txtCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 210, 200, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCadastrarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarMedicoActionPerformed
        // TODO add your handling code here:
        cadastrarMedico();
        atualizarUsuario();
    }//GEN-LAST:event_btCadastrarMedicoActionPerformed

    private void btCadastrarFarmaceuticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarFarmaceuticoActionPerformed
        // TODO add your handling code here:
        CadastroFarmaceutico formCadastroFarmaceutico = new CadastroFarmaceutico();
        formCadastroFarmaceutico.setVisible(true);
    }//GEN-LAST:event_btCadastrarFarmaceuticoActionPerformed

    private void btCadastrarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarPacienteActionPerformed
        // TODO add your handling code here:
        CadastroPaciente formCadastroPaciente = new CadastroPaciente();  
        formCadastroPaciente.setVisible(true);
    }//GEN-LAST:event_btCadastrarPacienteActionPerformed

    private void btCadastrarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCadastrarMedicamentoActionPerformed
        // TODO add your handling code here:
        CadastroMedicamento formCadastroMedicamento = new CadastroMedicamento();
        formCadastroMedicamento.setVisible(true);
    }//GEN-LAST:event_btCadastrarMedicamentoActionPerformed

    private void btVisualizaReceitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVisualizaReceitaActionPerformed
        // TODO add your handling code here:
        impressaoReceita();
    }//GEN-LAST:event_btVisualizaReceitaActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtEspecialidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEspecialidadeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEspecialidadeActionPerformed

    private void txtTelefoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefoneActionPerformed

    private void txtClinicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClinicaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClinicaActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEnderecoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEnderecoActionPerformed

    private void txtCrmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCrmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCrmActionPerformed

    private void btAtualizarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAtualizarMedicoActionPerformed
        // TODO add your handling code here:
        atualizarCadastroMedico();
    }//GEN-LAST:event_btAtualizarMedicoActionPerformed

    private void btPrescreverMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrescreverMedicamentoActionPerformed
        // TODO add your handling code here:
        PrescricaoMedica formPrescricaoMedica = new PrescricaoMedica(this);  ////btvoltar this
        formPrescricaoMedica.SetIdMedicoLogado(IdMedico_tb_usuario);                    
        formPrescricaoMedica.setVisible(true);                   
        System.out.println("Medico");
        
                           
        
    }//GEN-LAST:event_btPrescreverMedicamentoActionPerformed

    private void btPesquisarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarPacienteActionPerformed
        // TODO add your handling code here:
        PesquisaPaciente formPesquisaPaciente = new PesquisaPaciente();
        formPesquisaPaciente.setVisible(true);
    }//GEN-LAST:event_btPesquisarPacienteActionPerformed

    private void btPesquisarFarmaceuticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarFarmaceuticoActionPerformed
        // TODO add your handling code here:
        
        PesquisarFarmaceutico formPesquisarFarmaceutico = new PesquisarFarmaceutico();
        formPesquisarFarmaceutico.setVisible(true);
    }//GEN-LAST:event_btPesquisarFarmaceuticoActionPerformed

    private void btPesquisarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarMedicamentoActionPerformed
        // TODO add your handling code here:
        PesquisarMedicamento formPesquisarMedicamento = new PesquisarMedicamento();
        formPesquisarMedicamento.setVisible(true);
    }//GEN-LAST:event_btPesquisarMedicamentoActionPerformed

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
            java.util.logging.Logger.getLogger(TelaPrincipalMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipalMedico.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new TelaPrincipalMedico().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAtualizarMedico;
    private javax.swing.JButton btCadastrarFarmaceutico;
    private javax.swing.JButton btCadastrarMedicamento;
    private javax.swing.JButton btCadastrarMedico;
    private javax.swing.JButton btCadastrarPaciente;
    private javax.swing.JButton btPesquisarFarmaceutico;
    private javax.swing.JButton btPesquisarMedicamento;
    private javax.swing.JButton btPesquisarPaciente;
    private javax.swing.JButton btPrescreverMedicamento;
    private javax.swing.JButton btVisualizaReceita;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblReceita;
    private javax.swing.JComboBox txtCidade;
    private javax.swing.JTextField txtClinica;
    private javax.swing.JTextField txtCrm;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtEspecialidade;
    private javax.swing.JComboBox txtEstado;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtTelefone;
    // End of variables declaration//GEN-END:variables
}
