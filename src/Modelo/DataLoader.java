/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static List<Estado> carregarEstados(String arquivoJson) {
        List<Estado> estados = new ArrayList<>();
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(arquivoJson)));
            JSONObject jsonObj = new JSONObject(jsonText);
            JSONArray estadosArray = jsonObj.getJSONArray("estados");
            for (int i = 0; i < estadosArray.length(); i++) {
                JSONObject obj = estadosArray.getJSONObject(i);
                String id = obj.getString("id");
                String nome = obj.getString("estado");
                estados.add(new Estado(id, nome));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return estados;
    }

    public static List<Cidade> carregarCidades(String arquivoJson) {
        List<Cidade> cidades = new ArrayList<>();
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(arquivoJson)));
            JSONObject jsonObj = new JSONObject(jsonText);
            JSONArray cidadesArray = jsonObj.getJSONArray("cidades");
            for (int i = 0; i < cidadesArray.length(); i++) {
                JSONObject obj = cidadesArray.getJSONObject(i);
                String estadoId = obj.getString("estadoId");
                String nomeCidade = obj.getString("cidade");
                cidades.add(new Cidade(estadoId, nomeCidade));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cidades;
    }
}
