/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dolar_exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mcan
 */
public class Exchange {
    //api url e istek yapan fonksiyon
    public static void updateCurrency(JLabel[] jLabel) {
        try {
            URL url = new URL("https://www.doviz.com/api/v1/currencies/USD/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String parts[];
            System.out.println("Output from Server .... \n");
            if ((output = br.readLine()) != null) {
                    System.out.println(output);
                    parts = output.split(",");
                     
                    // Satis Degeri
                    jLabel[0].setText(parts[0].split(":")[1]);
                    // Alis Degeri
                    jLabel[1].setText(parts[3].split(":")[1]);
                    // Günlük Deiğişim (virgülden sonra 2 basamaklı)
                    jLabel[2].setText("%" + parts[4].split(":")[1].substring(0, 5));
                    // Güncelleme Tarihi
                    long unixTimeStamp = Long.parseLong(parts[1].split(":")[1]);
                    Date time = new Date(unixTimeStamp * 1000);
                    jLabel[3].setText(time.toLocaleString());
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
              e.printStackTrace();
        } catch (IOException e) {
              e.printStackTrace();
        }
    }
    //alarmları kontrol eden ve uyarı veren, verdiği uyarıları silen fonksiyon
    public static void checkAlarms(DefaultTableModel tableModel, float sellingCurrency, float buyingCurrency) {
        // uyarı verecek alarm sayısı belli olmadığı için array list kullanıldı
        ArrayList<Integer> shouldRemovedAlarms = new ArrayList<Integer>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) { //satırlardaki her alarm kontrol eder
            float currency = (float) tableModel.getValueAt(i, 0); 
            String sellingOrBuying = (String) tableModel.getValueAt(i, 1);
                        
            switch(sellingOrBuying) {
                case "Satış":
                    if(sellingCurrency <= currency)
                    {
                        JOptionPane.showMessageDialog(new JFrame(),
                        "Dolar Kuru Banka Satış Değeri Belirlediğiniz Değerin Altına İndi!\nDolar Alabilirsiniz...",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
                        
                        shouldRemovedAlarms.add(i); //uyarı verilen alarmları array liste ekler
                    }
                    break;
                case "Alış":
                    if(buyingCurrency >= currency)
                    {
                        JOptionPane.showMessageDialog(new JFrame(),
                        "Dolar Kuru Banka Alış Değeri Belirlediğiniz Değerin Üstüne Çıktı!\nDolar Satabilirsiniz...",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
                        
                        shouldRemovedAlarms.add(i); //uyarı verilen alarmları array liste ekler
                    }
                    break;
            }
        }
        // alarm verilenleri silen döngü
        for (int j = 0; j < shouldRemovedAlarms.size(); j++) {
            tableModel.removeRow(shouldRemovedAlarms.get(j));
        }
        
    }
    
}
