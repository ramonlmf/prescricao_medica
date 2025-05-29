/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author ramon
 */
import javax.swing.JComboBox;
import java.util.List;
import java.util.stream.Collectors;

public class LocalizacaoLoader {
    private List<Estado> estados;
    private List<Cidade> cidades;

    public void carregarEstados(String arquivoEstados) {
        estados = DataLoader.carregarEstados(arquivoEstados);
    }

    public void carregarCidades(String arquivoCidades) {
        cidades = DataLoader.carregarCidades(arquivoCidades);
    }

    public void preencherComboEstados(JComboBox<Estado> comboEstados) {
        comboEstados.removeAllItems();
        if (estados != null) {
            for (Estado estado : estados) {
                comboEstados.addItem(estado);
            }
        }
    }

    public void atualizarCidades(JComboBox<Cidade> comboCidades, Estado estadoSelecionado) {
        comboCidades.removeAllItems();
        if (cidades != null && estadoSelecionado != null) {
            List<Cidade> cidadesDoEstado = cidades.stream()
                .filter(c -> c.getEstadoId().equals(estadoSelecionado.getId()))
                .collect(Collectors.toList());
            for (Cidade cidade : cidadesDoEstado) {
                comboCidades.addItem(cidade);
            }
        }
    }
}
