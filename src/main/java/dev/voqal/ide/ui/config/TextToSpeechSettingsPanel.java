package dev.voqal.ide.ui.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.*;
import dev.voqal.config.settings.TextToSpeechSettings;
import dev.voqal.config.settings.TextToSpeechSettings.TTSProvider;
import dev.voqal.provider.clients.deepgram.DeepgramClient;
import dev.voqal.provider.clients.openai.OpenAiClient;
import dev.voqal.provider.clients.picovoice.PicovoiceOrcaClient;
import dev.voqal.services.VoqalVoiceService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class TextToSpeechSettingsPanel extends JBPanel<TextToSpeechSettingsPanel> {

    public TextToSpeechSettingsPanel(Project project) {
        initComponents();

        resetButton.addActionListener(e -> {
            speedSlider.setValue(100);
            pitchSlider.setValue(100);
            rateSlider.setValue(100);
            volumeSlider.setValue(100);
        });
        listenButton.addActionListener(e -> {
            Object voice = voiceComboBox.getSelectedItem();
            project.getService(VoqalVoiceService.class).playSound("speech/hello_" + voice, getConfig());
        });
        providerComboBox.addActionListener(e -> {
            providerPasswordField.setText("");
            orgIdTextField.setText("");

            TTSProvider ttsProvider = TTSProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
            if (ttsProvider.isKeyRequired()) {
                label7.setVisible(true);
                providerPasswordField.setVisible(true);
            } else {
                label7.setVisible(false);
                providerPasswordField.setVisible(false);
            }
            if (ttsProvider.isOrgIdAvailable()) {
                label9.setVisible(true);
                orgIdTextField.setVisible(true);
            } else {
                label9.setVisible(false);
                orgIdTextField.setVisible(false);
            }
            if (ttsProvider.isModelRequired()) {
                label8.setVisible(true);
                modelNameComboBox.setVisible(true);
            } else {
                label8.setVisible(false);
                modelNameComboBox.setVisible(false);
            }

            if (ttsProvider == TTSProvider.OPENAI) {
                voiceComboBox.setModel(new DefaultComboBoxModel<>(OpenAiClient.getVOICES()));
            } else if (ttsProvider == TTSProvider.DEEPGRAM) {
                voiceComboBox.setModel(new DefaultComboBoxModel<>(DeepgramClient.getVOICES()));
            } else if (ttsProvider == TTSProvider.PICOVOICE) {
                voiceComboBox.setModel(new DefaultComboBoxModel<>(PicovoiceOrcaClient.getVOICES()));
            } else {
                voiceComboBox.setModel(new DefaultComboBoxModel<>(new String[]{""}));
            }

            label2.setVisible(ttsProvider.isVoiceNameRequired());
            voiceComboBox.setVisible(ttsProvider.isVoiceNameRequired());

            separator1.setVisible(ttsProvider.isSonicSupported());
            label1.setVisible(ttsProvider.isSonicSupported());
            speedSlider.setVisible(ttsProvider.isSonicSupported());
            label3.setVisible(ttsProvider.isSonicSupported());
            pitchSlider.setVisible(ttsProvider.isSonicSupported());
            label4.setVisible(ttsProvider.isSonicSupported());
            rateSlider.setVisible(ttsProvider.isSonicSupported());
            label5.setVisible(ttsProvider.isSonicSupported());
            volumeSlider.setVisible(ttsProvider.isSonicSupported());
            resetButton.setVisible(ttsProvider.isSonicSupported());
            listenButton.setVisible(ttsProvider.isSonicSupported());
        });
    }

    public boolean isModified(TextToSpeechSettings config) {
        if (TTSProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isVoiceNameRequired()) {
            if (!Objects.equals(config.getVoice(), voiceComboBox.getSelectedItem().toString())) {
                return true;
            }
        }
        if (!Objects.equals(config.getSpeed(), speedSlider.getValue())) {
            return true;
        }
        if (!Objects.equals(config.getPitch(), pitchSlider.getValue())) {
            return true;
        }
        if (!Objects.equals(config.getRate(), rateSlider.getValue())) {
            return true;
        }
        if (!Objects.equals(config.getVolume(), volumeSlider.getValue())) {
            return true;
        }
        if (!Objects.equals(config.getProvider().getDisplayName(), providerComboBox.getSelectedItem())) {
            return true;
        }
        if (TTSProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isKeyRequired()) {
            if (!Objects.equals(config.getProviderKey(), new String(this.providerPasswordField.getPassword()))) {
                return true;
            }
        }
        if (TTSProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isOrgIdAvailable()) {
            if (!Objects.equals(config.getOrgId(), orgIdTextField.getText())) {
                return true;
            }
        }
        if (!Objects.equals(config.getModelName(), modelNameComboBox.getSelectedItem())) {
            return true;
        }
        return false;
    }

    public TextToSpeechSettings getConfig() {
        String providerKey = new String(this.providerPasswordField.getPassword());
        TTSProvider ttsProvider = TTSProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        if (!ttsProvider.isKeyRequired()) {
            providerKey = ""; //reset
        }
        return new TextToSpeechSettings(
                voiceComboBox.getSelectedItem().toString(),
                speedSlider.getValue(),
                pitchSlider.getValue(),
                rateSlider.getValue(),
                volumeSlider.getValue(),
                false,
                0,
                ttsProvider,
                providerKey,
                orgIdTextField.getText(),
                modelNameComboBox.getSelectedItem().toString()
        );
    }

    public void applyConfig(TextToSpeechSettings config) {
        speedSlider.setValue(config.getSpeed());
        pitchSlider.setValue(config.getPitch());
        rateSlider.setValue(config.getRate());
        volumeSlider.setValue(config.getVolume());
        providerComboBox.setSelectedItem(config.getProvider().getDisplayName());
        providerPasswordField.setText(config.getProviderKey());
        orgIdTextField.setText(config.getOrgId());
        modelNameComboBox.setSelectedItem(config.getModelName());

        var ttsProvider = config.getProvider();
        if (ttsProvider.isKeyRequired()) {
            label7.setVisible(true);
            providerPasswordField.setVisible(true);
        } else {
            label7.setVisible(false);
            providerPasswordField.setVisible(false);
        }
        if (ttsProvider.isOrgIdAvailable()) {
            label9.setVisible(true);
            orgIdTextField.setVisible(true);
        } else {
            label9.setVisible(false);
            orgIdTextField.setVisible(false);
        }
        if (ttsProvider.isModelRequired()) {
            label8.setVisible(true);
            modelNameComboBox.setVisible(true);
        } else {
            label8.setVisible(false);
            modelNameComboBox.setVisible(false);
        }

        if (ttsProvider == TTSProvider.OPENAI) {
            voiceComboBox.setModel(new DefaultComboBoxModel<>(OpenAiClient.getVOICES()));
        } else if (ttsProvider == TTSProvider.DEEPGRAM) {
            voiceComboBox.setModel(new DefaultComboBoxModel<>(DeepgramClient.getVOICES()));
        } else if (ttsProvider == TTSProvider.PICOVOICE) {
            voiceComboBox.setModel(new DefaultComboBoxModel<>(PicovoiceOrcaClient.getVOICES()));
        } else {
            voiceComboBox.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        }
        voiceComboBox.setSelectedItem(config.getVoice());

        label2.setVisible(ttsProvider.isVoiceNameRequired());
        voiceComboBox.setVisible(ttsProvider.isVoiceNameRequired());

        separator1.setVisible(ttsProvider.isSonicSupported());
        label1.setVisible(ttsProvider.isSonicSupported());
        speedSlider.setVisible(ttsProvider.isSonicSupported());
        label3.setVisible(ttsProvider.isSonicSupported());
        pitchSlider.setVisible(ttsProvider.isSonicSupported());
        label4.setVisible(ttsProvider.isSonicSupported());
        rateSlider.setVisible(ttsProvider.isSonicSupported());
        label5.setVisible(ttsProvider.isSonicSupported());
        volumeSlider.setVisible(ttsProvider.isSonicSupported());
        resetButton.setVisible(ttsProvider.isSonicSupported());
        listenButton.setVisible(ttsProvider.isSonicSupported());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        panel1 = new JBPanel<>();
        label6 = new JBLabel();
        providerComboBox = new JComboBox<>();
        label7 = new JBLabel();
        providerPasswordField = new JBPasswordField();
        providerPasswordField.getEmptyText().setText("(required)");
        label9 = new JBLabel();
        orgIdTextField = new JBTextField();
        orgIdTextField.getEmptyText().setText("(optional)");
        label8 = new JBLabel();
        modelNameComboBox = new JComboBox<>();
        label2 = new JBLabel();
        voiceComboBox = new JComboBox<>();
        separator1 = new JSeparator();
        label1 = new JBLabel();
        speedSlider = new JBSlider();
        label3 = new JBLabel();
        pitchSlider = new JBSlider();
        label4 = new JBLabel();
        rateSlider = new JBSlider();
        label5 = new JBLabel();
        volumeSlider = new JBSlider();
        resetButton = new JButton();
        listenButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[84,grow,fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- label6 ----
            label6.setText("Provider");
            panel1.add(label6, "cell 0 0");

            //---- providerComboBox ----
            providerComboBox.setModel(new DefaultComboBoxModel<>(TTSProvider.getEntries()
                    .stream().map(TTSProvider::getDisplayName).toArray(String[]::new)));
            panel1.add(providerComboBox, "cell 1 0 2 1");

            //---- label7 ----
            label7.setText("Key");
            panel1.add(label7, "cell 0 1");
            panel1.add(providerPasswordField, "cell 1 1 2 1");

            //---- label9 ----
            label9.setText("Organization Id");
            panel1.add(label9, "cell 0 2");
            panel1.add(orgIdTextField, "cell 1 2 2 1");

            //---- label8 ----
            label8.setText("Model");
            panel1.add(label8, "cell 0 3");

            //---- modelNameComboBox ----
            modelNameComboBox.setEditable(true);
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "tts-1",
                "tts-1-hd"
            }));
            panel1.add(modelNameComboBox, "cell 1 3 2 1");

            //---- label2 ----
            label2.setText("Voice");
            panel1.add(label2, "cell 0 4");

            //---- voiceComboBox ----
            voiceComboBox.setModel(new DefaultComboBoxModel<>(OpenAiClient.getVOICES()));
            panel1.add(voiceComboBox, "cell 1 4 2 1");
            panel1.add(separator1, "cell 0 5 3 1");

            //---- label1 ----
            label1.setText("Speed");
            panel1.add(label1, "cell 0 6");

            //---- speedSlider ----
            speedSlider.setMaximum(200);
            speedSlider.setValue(100);
            panel1.add(speedSlider, "cell 1 6 2 1");

            //---- label3 ----
            label3.setText("Pitch");
            panel1.add(label3, "cell 0 7");

            //---- pitchSlider ----
            pitchSlider.setMaximum(200);
            pitchSlider.setValue(100);
            panel1.add(pitchSlider, "cell 1 7 2 1");

            //---- label4 ----
            label4.setText("Rate");
            panel1.add(label4, "cell 0 8");

            //---- rateSlider ----
            rateSlider.setMaximum(200);
            rateSlider.setValue(100);
            panel1.add(rateSlider, "cell 1 8 2 1");

            //---- label5 ----
            label5.setText("Volume");
            panel1.add(label5, "cell 0 9");

            //---- volumeSlider ----
            volumeSlider.setMaximum(200);
            volumeSlider.setValue(100);
            panel1.add(volumeSlider, "cell 1 9 2 1");

            //---- resetButton ----
            resetButton.setText("Reset");
            panel1.add(resetButton, "cell 2 10");

            //---- listenButton ----
            listenButton.setText("Play Voice");
            panel1.add(listenButton, "cell 2 10");
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel panel1;
    private JLabel label6;
    private JComboBox<String> providerComboBox;
    private JLabel label7;
    private JBPasswordField providerPasswordField;
    private JLabel label9;
    private JBTextField orgIdTextField;
    private JLabel label8;
    private JComboBox<String> modelNameComboBox;
    private JLabel label2;
    private JComboBox<String> voiceComboBox;
    private JSeparator separator1;
    private JLabel label1;
    private JSlider speedSlider;
    private JLabel label3;
    private JSlider pitchSlider;
    private JLabel label4;
    private JSlider rateSlider;
    private JLabel label5;
    private JSlider volumeSlider;
    private JButton resetButton;
    private JButton listenButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
