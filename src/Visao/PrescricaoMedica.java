/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Visao;

import Controle.Bd_Conexao;
import static Modelo.AssinaturaReceitaMedica.gerarAssinatura;
import static Modelo.GerarQrCodeAssinatura.gerarQrCode;
import java.awt.HeadlessException; 
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JComboBox;




/**
 *
 * @author ramon
 */
public class PrescricaoMedica extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private List<String> listaMedicamentos = new ArrayList<>();
    
    static int idMedicoLogado;
    
     private TelaPrincipalMedico telaPrincipal; //btvoltar

    /**
     * Creates new form Principal
     */
    public PrescricaoMedica( TelaPrincipalMedico telaPrincipal ) {//btvoltar
        
        this.telaPrincipal = telaPrincipal; //btvoltar
        initComponents();   
        
         // Configurações do frame sempre quando abrir a janela
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowOpened(WindowEvent e) 
            {
                System.out.println("A janela foi aberta!");
                setaLabelsMedico();
                carregarMedicamentosLista();
                limpar();
        
                ckAdd1.setEnabled(false);
                //teclaLiberada();
                configurarFiltroComboBox(txtMedicamento, listaMedicamentos);
                configurarFiltroComboBox(txtMedicamento1, listaMedicamentos);
                configurarFiltroComboBox(txtMedicamento2, listaMedicamentos);
                configurarFiltroComboBox(txtMedicamento3, listaMedicamentos);
               
                
                System.out.println("ID do medico logado: " + idMedicoLogado);
            }
        });         
        
        
        
               
    }
    
      // vai carregar o idMedico salvo na tabela tb_usuarios
    public void SetIdMedicoLogado(int id)
    {
        idMedicoLogado = id; 
    } 
    
    // metodo carrega na inicializacao todos os medicamentos para a lista
    private void carregarMedicamentosLista()
    {
        // Carregar todos os medicamentos uma única vez
        String sql = "select nomemedicamento from tb_medicamento";
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            listaMedicamentos.clear(); // Limpa a lista antes de carregar
            while (rs.next()) {
                listaMedicamentos.add(rs.getString("nomemedicamento"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                rs.close();
                pst.close();
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        } 
       }
    
    
    
    
    
    private void atualizarComboBox(JComboBox<String> comboBox, List<String> listaItens, String textoDigitado) {
        List<String> filtrados = new ArrayList<>();
        for (String item : listaItens) {
            if (item.toLowerCase().startsWith(textoDigitado.toLowerCase())) {
                filtrados.add(item);
            }
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(filtrados.toArray(new String[0]));
        comboBox.setModel(model);
        comboBox.setSelectedItem(textoDigitado);
    }
    
    /*
    //método de escuta para cada vez que a tecla for liberada pode ser usar outras listas independentes
    chamadas na inicializacao
    configurarFiltroComboBox(txtMedicamento, listaMedicamentos);
    configurarFiltroComboBox(txtOutraComboBox, listaOutroItens);
    configurarFiltroComboBox(txtMaisUmaComboBox, listaMaisItens);
    */     
    private void configurarFiltroComboBox(JComboBox<String> comboBox, List<String> listaItens) {
        comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            String textoDigitado = (String) comboBox.getEditor().getItem();
            atualizarComboBox(comboBox, listaItens, textoDigitado);
            comboBox.showPopup();
        }
        });
    }   
    
    
    
     // Pega a data atual do sitema
    private String getDateTime() {
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	Date date = new Date();
	return dateFormat.format(date);
    }
    
    
    // Metodo que converte uma data string para uma do tipo Date
   public Date setDataFormatada(String dataNascimento){
	try {
		SimpleDateFormat formatado = new SimpleDateFormat("dd/MM/yyyy");
		Date dataFormat = formatado.parse(dataNascimento);
		return dataFormat;
			
	} catch (ParseException e) {
		e.printStackTrace();
	}
	return null;
    }
	
       
    
    //Calcula a Idade baseado em java.util.Date recebe como parametro uma variavel do tipo Date

    public static int calculaIdade(java.util.Date dataNasc) {
        
        Calendar dateOfBirth = new GregorianCalendar();

        dateOfBirth.setTime(dataNasc);

        // Cria um objeto calendar com a data atual
        Calendar today = Calendar.getInstance();

        // Obtém a idade baseado no ano
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        dateOfBirth.add(Calendar.YEAR, age);

        //se a data de hoje é antes da data de Nascimento, então diminui 1(um)
        if (today.before(dateOfBirth)) {
            age--;
        }

        return age;
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
                 "from tb_receitas where nomepaciente like ?";

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
    
     /**
     * Método responsável por limpar os campos e gerenciar os componentes
     */
    private void limpar() {
        txtMedicamento.setSelectedIndex(-1);
        txtDosagem.setSelectedIndex(-1);
        txtPeriodo.setSelectedIndex(-1);
        txtTempodeuso.setSelectedIndex(-1);
                
        txtMedicamento1.setSelectedIndex(-1);
        txtDosagem1.setSelectedIndex(-1);
        txtPeriodo1.setSelectedIndex(-1);
        txtTempodeuso1.setSelectedIndex(-1);
        
        txtMedicamento2.setSelectedIndex(-1);
        txtDosagem2.setSelectedIndex(-1);
        txtPeriodo2.setSelectedIndex(-1);
        txtTempodeuso2.setSelectedIndex(-1);
        
        txtMedicamento3.setSelectedIndex(-1);
        txtDosagem3.setSelectedIndex(-1);
        txtPeriodo3.setSelectedIndex(-1);
        txtTempodeuso3.setSelectedIndex(-1);
        
        txtObservacoes.setText(null); 
        
        ckAdd1.setEnabled(true);
        ckAdd2.setEnabled(true);
        ckAdd3.setEnabled(true);
        ckAdd4.setEnabled(true);
        
        ((DefaultTableModel) tblReceita.getModel()).setRowCount(0);
       
    }
    
    /**
     * Método responsável para por os dados medico nas labels     
     */
    public void setaLabelsMedico()  {
        String sql = "select idMedico, nomemedico, crm, clinica, especialidade, enderecoclinica, cidade, estado from tb_medico where idMedico like ?";
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, idMedicoLogado + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();   
            
            //lbIdMedico.setText(rs.getString(1));  
            lbNomemedico.setText(rs.getString(2));
            lbCrm.setText(rs.getString(3));
            lbClinica.setText(rs.getString(4));
            lbEspecialidadeMedico.setText(rs.getString(5));            
            lbEnderecoClinica.setText(rs.getString(6) + " - " + rs.getString(7) + ", " + rs.getString(8));
            
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
     * Método responsável pela pesquisa de pacientes pelo nome com filtro     
     */
    public void pesquisarPacientes()  {
        String sql = "select idPaciente, nome, datanascimento, cpf, rg from tb_pacientes where idPaciente like ?";
        try {
            conexao = Bd_Conexao.conectar();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtId.getText() + "%");
            rs = pst.executeQuery();
            
            boolean registro_vf = rs.next();            
                      
            txtNome.setText(rs.getString(2));
            
            String datadenascimento = rs.getString(3);
            int idade = calculaIdade(setDataFormatada(datadenascimento));             
            txtIdade.setText(Integer.toString(idade));
            
            txtCpf.setText(rs.getString(4));
            txtRg.setText(rs.getString(5));
            
            
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
    
    private void impressaoReceita()  {
        
        DefaultTableModel tableModel = (DefaultTableModel) tblReceita.getModel();
        int row = tblReceita.getSelectedRow();
        
       
        conexao = Bd_Conexao.conectar();
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta Receita?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                HashMap filtro = new HashMap();
                filtro.put("idreceita", Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
                
                //filtro.put("idreceita", 2);
                
                //System.out.println(tableModel.getValueAt(row, 0).toString());
                //System.out.println(getClass().getResourceAsStream("/Visao/report/receita_medica.jasper"));
                   //JasperPrint print = JasperFillManager.fillReport("C:\\Receita\\report\\receita_medica.jasper", filtro, conexao);
                
               // String basePath = System.getProperty("user.dir") + "/Visao/report/";
        
               // JasperPrint print = JasperFillManager.fillReport(basePath + "receita_medica.jasper", filtro, conexao);
                
        
        
               JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/Visao/report/receita_medica.jasper"), filtro, conexao);
                JasperViewer.viewReport(print, false);
                conexao.close();
            } catch (NumberFormatException | SQLException | JRException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        
        }            
      
       
    }
    
    
    private void adicionarReceita() {
        
        String concatenar = null;
        boolean campoembranco = false;
        
        System.out.println(ckAdd1.isSelected());
        System.out.println(ckAdd2.isSelected());
        System.out.println(ckAdd3.isSelected());
        System.out.println(ckAdd4.isSelected());
        
        
        
        //String sql = "insert into tb_receitas(nomepaciente, idade, rg, cpf, medicamento1, medicamento2, medicamento3, medicamento4, dpt1, dpt2, dpt3, dpt4, observacoes, nomemedico, crm, clinica, datareceita, especialidademedico, enderecoclinica, assinaturaqrcode, idMedico) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        String sql = "insert into tb_receitas(nomepaciente, idade, rg, cpf, medicamento1, medicamento2, medicamento3, medicamento4, dpt1, dpt2, dpt3, dpt4, observacoes, nomemedico, crm, clinica, datareceita, especialidademedico, enderecoclinica, assinaturaqrcode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
       
        if (ckAdd1.isSelected() && 
        (txtMedicamento.getSelectedItem() == null || txtMedicamento.getSelectedItem().toString().trim().isEmpty() ||
        txtDosagem.getSelectedItem() == null || txtDosagem.getSelectedItem().toString().trim().isEmpty() ||
        txtPeriodo.getSelectedItem() == null || txtPeriodo.getSelectedItem().toString().trim().isEmpty() ||
        txtTempodeuso.getSelectedItem() == null || txtTempodeuso.getSelectedItem().toString().trim().isEmpty())) {
               campoembranco = true;
        }
        if (ckAdd2.isSelected() && 
        (txtMedicamento1.getSelectedItem() == null || txtMedicamento1.getSelectedItem().toString().trim().isEmpty() ||
        txtDosagem1.getSelectedItem() == null || txtDosagem1.getSelectedItem().toString().trim().isEmpty() ||
        txtPeriodo1.getSelectedItem() == null || txtPeriodo1.getSelectedItem().toString().trim().isEmpty() ||
        txtTempodeuso1.getSelectedItem() == null || txtTempodeuso1.getSelectedItem().toString().trim().isEmpty())) {
            campoembranco = true;
        }
        if (ckAdd3.isSelected() && 
        (txtMedicamento2.getSelectedItem() == null || txtMedicamento2.getSelectedItem().toString().trim().isEmpty() ||
        txtDosagem2.getSelectedItem() == null || txtDosagem2.getSelectedItem().toString().trim().isEmpty() ||
        txtPeriodo2.getSelectedItem() == null || txtPeriodo2.getSelectedItem().toString().trim().isEmpty() ||
        txtTempodeuso2.getSelectedItem() == null || txtTempodeuso2.getSelectedItem().toString().trim().isEmpty())) {
               campoembranco = true;
        }
        if (ckAdd4.isSelected() && 
        (txtMedicamento3.getSelectedItem() == null || txtMedicamento3.getSelectedItem().toString().trim().isEmpty() ||
        txtDosagem3.getSelectedItem() == null || txtDosagem3.getSelectedItem().toString().trim().isEmpty() ||
        txtPeriodo3.getSelectedItem() == null || txtPeriodo3.getSelectedItem().toString().trim().isEmpty() ||
        txtTempodeuso3.getSelectedItem() == null || txtTempodeuso3.getSelectedItem().toString().trim().isEmpty())) {
            campoembranco = true;
        }
        
        try {
            
            if ((txtNome.getText().isEmpty()) || (txtIdade.getText().isEmpty() || (txtRg.getText().isEmpty()) || (txtCpf.getText().isEmpty())
            || (txtId.getText().isEmpty())  || campoembranco )) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } 
            else 
            {
                
                conexao = Bd_Conexao.conectar();
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtNome.getText());
                pst.setString(2, txtIdade.getText());
                pst.setString(3, txtRg.getText());
                pst.setString(4, txtCpf.getText());

                pst.setString(5, "");
                pst.setString(6, "");
                pst.setString(7, "");
                pst.setString(8, "");
                pst.setString(9, "");
                pst.setString(10, "");
                pst.setString(11, "");
                pst.setString(12, "");
            
                if (ckAdd1.isSelected())
                {
                    // Tomar "1 comprimido" de "8 em 8h" durante "15" dias.
                    concatenar = ("Tomar " + txtDosagem.getSelectedItem().toString() + " de " + txtPeriodo.getSelectedItem().toString() + " durante " + txtTempodeuso.getSelectedItem().toString() + " dias.") ;
                    pst.setString(9, concatenar);
                    pst.setString(5, txtMedicamento.getSelectedItem().toString()); 
                }
                
                if (ckAdd2.isSelected())
                {
                    concatenar = ("Tomar " + txtDosagem1.getSelectedItem().toString() + " de " + txtPeriodo1.getSelectedItem().toString() + " durante " + txtTempodeuso1.getSelectedItem().toString() + " dias.") ;
                    pst.setString(10, concatenar);
                    pst.setString(6, txtMedicamento1.getSelectedItem().toString());
                }
                    
                if (ckAdd3.isSelected())
                {    
                    concatenar = ("Tomar " + txtDosagem2.getSelectedItem().toString() + " de " + txtPeriodo2.getSelectedItem().toString() + " durante " + txtTempodeuso2.getSelectedItem().toString() + " dias.") ;
                    pst.setString(11, concatenar);
                    pst.setString(7, txtMedicamento2.getSelectedItem().toString());
                }    
                    
                if (ckAdd4.isSelected())
                {
                    concatenar = ("Tomar " + txtDosagem3.getSelectedItem().toString() + " de " + txtPeriodo3.getSelectedItem().toString() + " durante " + txtTempodeuso3.getSelectedItem().toString() + " dias.") ;
                    pst.setString(12, concatenar);
                    pst.setString(8, txtMedicamento3.getSelectedItem().toString());
                }    
                
                System.out.println(txtObservacoes.getText());
                    
                if (txtObservacoes.getText().trim().isEmpty()) //Está vazio (Não considera espaços como valor válido)
                {                
                   pst.setString(13, txtObservacoes.getText()); 
                }
                else
                {
                    pst.setString(13, "Observções: " + txtObservacoes.getText()); 
                }     
                           
                pst.setString(14, lbNomemedico.getText());
                pst.setString(15, lbCrm.getText());
                pst.setString(16, lbClinica.getText());            
                pst.setString(17, getDateTime());
                   
            
                
                pst.setString(18, lbEspecialidadeMedico.getText());            
                pst.setString(19, lbEnderecoClinica.getText());

                String temp = gerarAssinatura();                    
                gerarQrCode(temp);            
                pst.setString(20, temp);
                //pst.setInt(21, idMedicoLogado);
                                
                
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Receita criada com sucesso");
                    limpar();
                }
            }
            
        } catch (SQLIntegrityConstraintViolationException e1) {
            JOptionPane.showMessageDialog(null, "Email já existente.\nEscolha outro email.");
            //txtCliEmail.setText(null);
            //txtCliEmail.requestFocus();
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

        jLabel7 = new javax.swing.JLabel();
        txtIdade = new javax.swing.JTextField();
        btBuscar = new javax.swing.JButton();
        txtRg = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtObservacoes = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lbNomemedico = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lbCrm = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        ckAdd1 = new javax.swing.JCheckBox();
        jLabel32 = new javax.swing.JLabel();
        ckAdd2 = new javax.swing.JCheckBox();
        ckAdd3 = new javax.swing.JCheckBox();
        ckAdd4 = new javax.swing.JCheckBox();
        jLabel36 = new javax.swing.JLabel();
        lbEspecialidadeMedico = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblReceita = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        txtDosagem2 = new javax.swing.JComboBox();
        txtDosagem3 = new javax.swing.JComboBox();
        txtDosagem1 = new javax.swing.JComboBox();
        txtPeriodo = new javax.swing.JComboBox();
        txtTempodeuso1 = new javax.swing.JComboBox();
        txtDosagem = new javax.swing.JComboBox();
        txtPeriodo2 = new javax.swing.JComboBox();
        txtPeriodo3 = new javax.swing.JComboBox();
        txtPeriodo1 = new javax.swing.JComboBox();
        txtTempodeuso2 = new javax.swing.JComboBox();
        txtMedicamento3 = new javax.swing.JComboBox();
        txtTempodeuso3 = new javax.swing.JComboBox();
        txtTempodeuso = new javax.swing.JComboBox();
        txtMedicamento1 = new javax.swing.JComboBox();
        txtMedicamento2 = new javax.swing.JComboBox();
        txtMedicamento = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        lbClinica = new javax.swing.JLabel();
        txtCpf = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        lbEnderecoClinica = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btSalvarReceita = new javax.swing.JButton();
        btEnviarPorEmail = new javax.swing.JButton();
        btVisualizaReceita = new javax.swing.JButton();
        Voltar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtIdmedico = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Prescrição Médica");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Idade");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 120, -1, -1));
        getContentPane().add(txtIdade, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 140, 70, -1));

        btBuscar.setText("Buscar");
        btBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(btBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 140, 80, -1));
        getContentPane().add(txtRg, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 140, 160, -1));

        jLabel12.setText("Dosagem");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 200, -1, -1));

        jLabel13.setText("Periodo");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 200, -1, -1));

        jLabel14.setText("Observações");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 440, -1, -1));
        getContentPane().add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 140, 60, -1));
        getContentPane().add(txtObservacoes, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 460, 930, 30));

        jLabel15.setText("Tempo de uso");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 200, -1, -1));
        getContentPane().add(txtNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, 340, -1));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Médico:");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 50, -1, -1));

        jLabel9.setText("Registro Geral");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 120, -1, -1));

        jLabel10.setText("ID");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 120, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("CRM:");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 70, -1, -1));

        lbNomemedico.setText("medico");
        getContentPane().add(lbNomemedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 50, 380, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Clinica:");
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 660, -1, -1));

        lbCrm.setText("crm");
        getContentPane().add(lbCrm, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 70, 140, -1));

        jLabel8.setText("Nome do paciente");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 120, -1, -1));

        jLabel24.setText("Medicamento");
        getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 260, -1, -1));

        jLabel25.setText("Dosagem");
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 260, -1, -1));

        jLabel26.setText("Periodo");
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 260, -1, -1));

        jLabel27.setText("Tempo de uso");
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 260, -1, -1));

        jLabel28.setText("Medicamento");
        getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 320, -1, -1));

        jLabel29.setText("Dosagem");
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 320, -1, -1));

        jLabel30.setText("Periodo");
        getContentPane().add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 320, -1, -1));

        jLabel31.setText("Tempo de uso");
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 320, -1, -1));

        ckAdd1.setSelected(true);
        ckAdd1.setText("Adicionar");
        ckAdd1.setEnabled(false);
        getContentPane().add(ckAdd1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 220, -1, -1));

        jLabel32.setText("Medicamento");
        getContentPane().add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, -1, -1));

        ckAdd2.setSelected(true);
        ckAdd2.setText("Adicionar");
        getContentPane().add(ckAdd2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 280, -1, -1));

        ckAdd3.setSelected(true);
        ckAdd3.setText("Adicionar");
        getContentPane().add(ckAdd3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 340, -1, -1));

        ckAdd4.setSelected(true);
        ckAdd4.setText("Adicionar");
        getContentPane().add(ckAdd4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 400, -1, -1));

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Especialidade:");
        getContentPane().add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 90, 80, -1));

        lbEspecialidadeMedico.setText("crm");
        getContentPane().add(lbEspecialidadeMedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 90, 140, -1));

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

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 500, 930, 150));

        jLabel33.setText("Dosagem");
        getContentPane().add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 380, -1, -1));

        txtDosagem2.setEditable(true);
        txtDosagem2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 comprimido", "1 gota", "2 comprimidos", "2 gotas" }));
        getContentPane().add(txtDosagem2, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 340, 160, -1));

        txtDosagem3.setEditable(true);
        txtDosagem3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 comprimido", "1 gota", "2 comprimidos", "2 gotas" }));
        getContentPane().add(txtDosagem3, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 400, 160, -1));

        txtDosagem1.setEditable(true);
        txtDosagem1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 comprimido", "1 gota", "2 comprimidos", "2 gotas" }));
        getContentPane().add(txtDosagem1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 280, 160, -1));

        txtPeriodo.setEditable(true);
        txtPeriodo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24 em 24h", "12 em 12h", "8 em 8h", "4 em 4h" }));
        getContentPane().add(txtPeriodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 220, 120, -1));

        txtTempodeuso1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10 dias", "9 dias", "8 dias", "7 dias", "6 dias", "5 dias", "4 dias", "3 dias", "2 dias", "1 dia" }));
        getContentPane().add(txtTempodeuso1, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 280, 150, -1));

        txtDosagem.setEditable(true);
        txtDosagem.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 comprimido", "1 gota", "2 comprimidos", "2 gotas" }));
        getContentPane().add(txtDosagem, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 220, 160, -1));

        txtPeriodo2.setEditable(true);
        txtPeriodo2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24 em 24h", "12 em 12h", "8 em 8h", "4 em 4h" }));
        getContentPane().add(txtPeriodo2, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 340, 120, -1));

        txtPeriodo3.setEditable(true);
        txtPeriodo3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24 em 24h", "12 em 12h", "8 em 8h", "4 em 4h" }));
        getContentPane().add(txtPeriodo3, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 400, 120, -1));

        txtPeriodo1.setEditable(true);
        txtPeriodo1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24 em 24h", "12 em 12h", "8 em 8h", "4 em 4h" }));
        getContentPane().add(txtPeriodo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 280, 120, -1));

        txtTempodeuso2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10 dias", "9 dias", "8 dias", "7 dias", "6 dias", "5 dias", "4 dias", "3 dias", "2 dias", "1 dia" }));
        getContentPane().add(txtTempodeuso2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 340, 150, -1));

        txtMedicamento3.setEditable(true);
        txtMedicamento3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMedicamento3ActionPerformed(evt);
            }
        });
        getContentPane().add(txtMedicamento3, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 400, 330, -1));

        txtTempodeuso3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10 dias", "9 dias", "8 dias", "7 dias", "6 dias", "5 dias", "4 dias", "3 dias", "2 dias", "1 dia" }));
        getContentPane().add(txtTempodeuso3, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 400, 150, -1));

        txtTempodeuso.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10 dias", "9 dias", "8 dias", "7 dias", "6 dias", "5 dias", "4 dias", "3 dias", "2 dias", "1 dia" }));
        getContentPane().add(txtTempodeuso, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 220, 150, -1));

        txtMedicamento1.setEditable(true);
        txtMedicamento1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMedicamento1ActionPerformed(evt);
            }
        });
        getContentPane().add(txtMedicamento1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 280, 330, -1));

        txtMedicamento2.setEditable(true);
        txtMedicamento2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMedicamento2ActionPerformed(evt);
            }
        });
        getContentPane().add(txtMedicamento2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 340, 330, -1));

        txtMedicamento.setEditable(true);
        txtMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMedicamentoActionPerformed(evt);
            }
        });
        getContentPane().add(txtMedicamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 220, 330, -1));

        jLabel34.setText("Periodo");
        getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 380, -1, -1));

        jLabel16.setText("Medicamento");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 200, -1, -1));

        jLabel35.setText("Tempo de uso");
        getContentPane().add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 380, -1, -1));

        lbClinica.setText("cl");
        getContentPane().add(lbClinica, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 660, 170, -1));
        getContentPane().add(txtCpf, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 140, 130, -1));

        jLabel18.setText("CPF");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 120, -1, -1));

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setText("Endereço:");
        getContentPane().add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 660, -1, -1));

        lbEnderecoClinica.setText("cl");
        getContentPane().add(lbEnderecoClinica, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 660, 390, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel6.setText("Prescrição Médica");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 350, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/logo5.png"))); // NOI18N

        btSalvarReceita.setText("Finaliza Receita");
        btSalvarReceita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalvarReceitaActionPerformed(evt);
            }
        });

        btEnviarPorEmail.setText("Enviar por Email");
        btEnviarPorEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarPorEmailActionPerformed(evt);
            }
        });

        btVisualizaReceita.setText("Visualiza Receita");
        btVisualizaReceita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btVisualizaReceitaActionPerformed(evt);
            }
        });

        Voltar.setText("Voltar");
        Voltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VoltarActionPerformed(evt);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btVisualizaReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btEnviarPorEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSalvarReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btSalvarReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btEnviarPorEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btVisualizaReceita, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 265, Short.MAX_VALUE)
                .addComponent(Voltar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 200, 620));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Visao/figuras/back_blue.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        txtIdmedico.setText("1");
        getContentPane().add(txtIdmedico, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 50, 40, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscarActionPerformed
        // TODO add your handling code here:
        pesquisarPacientes();
        adicionarReceitaTabela();
        
    }//GEN-LAST:event_btBuscarActionPerformed

    private void btSalvarReceitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalvarReceitaActionPerformed
        // TODO add your handling code here:
        adicionarReceita();
        adicionarReceitaTabela();       
    }//GEN-LAST:event_btSalvarReceitaActionPerformed

    private void btVisualizaReceitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btVisualizaReceitaActionPerformed
        // TODO add your handling code here:
        impressaoReceita();
    }//GEN-LAST:event_btVisualizaReceitaActionPerformed

    private void txtMedicamento3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMedicamento3ActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_txtMedicamento3ActionPerformed

    private void txtMedicamento1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMedicamento1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMedicamento1ActionPerformed

    private void txtMedicamento2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMedicamento2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMedicamento2ActionPerformed

    private void txtMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMedicamentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMedicamentoActionPerformed

    private void VoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VoltarActionPerformed
        // TODO add your handling code here:
        if (telaPrincipal != null) //btvoltar
        {
            telaPrincipal.adicionarReceitaTabela();
        }
        this.dispose();
        
    }//GEN-LAST:event_VoltarActionPerformed

    private void btEnviarPorEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEnviarPorEmailActionPerformed
        // TODO add your handling code here:
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma o envio da receita para o e-mail do Paciente?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) 
        {
             JOptionPane.showMessageDialog(null, "E-mail enviado com sucesso!");
        }
        
    }//GEN-LAST:event_btEnviarPorEmailActionPerformed

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
            java.util.logging.Logger.getLogger(PrescricaoMedica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrescricaoMedica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrescricaoMedica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrescricaoMedica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

      
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Voltar;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btEnviarPorEmail;
    private javax.swing.JButton btSalvarReceita;
    private javax.swing.JButton btVisualizaReceita;
    private javax.swing.JCheckBox ckAdd1;
    private javax.swing.JCheckBox ckAdd2;
    private javax.swing.JCheckBox ckAdd3;
    private javax.swing.JCheckBox ckAdd4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbClinica;
    private javax.swing.JLabel lbCrm;
    private javax.swing.JLabel lbEnderecoClinica;
    private javax.swing.JLabel lbEspecialidadeMedico;
    private javax.swing.JLabel lbNomemedico;
    private javax.swing.JTable tblReceita;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JComboBox txtDosagem;
    private javax.swing.JComboBox txtDosagem1;
    private javax.swing.JComboBox txtDosagem2;
    private javax.swing.JComboBox txtDosagem3;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtIdade;
    private javax.swing.JTextField txtIdmedico;
    private javax.swing.JComboBox txtMedicamento;
    private javax.swing.JComboBox txtMedicamento1;
    private javax.swing.JComboBox txtMedicamento2;
    private javax.swing.JComboBox txtMedicamento3;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtObservacoes;
    private javax.swing.JComboBox txtPeriodo;
    private javax.swing.JComboBox txtPeriodo1;
    private javax.swing.JComboBox txtPeriodo2;
    private javax.swing.JComboBox txtPeriodo3;
    private javax.swing.JTextField txtRg;
    private javax.swing.JComboBox txtTempodeuso;
    private javax.swing.JComboBox txtTempodeuso1;
    private javax.swing.JComboBox txtTempodeuso2;
    private javax.swing.JComboBox txtTempodeuso3;
    // End of variables declaration//GEN-END:variables
}
