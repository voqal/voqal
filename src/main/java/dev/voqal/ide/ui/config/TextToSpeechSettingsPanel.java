package dev.voqal.ide.ui.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.*;
import com.intellij.uiDesigner.core.*;
import dev.voqal.config.settings.TextToSpeechSettings;
import dev.voqal.config.settings.TextToSpeechSettings.TTSProvider;
import dev.voqal.provider.clients.deepgram.DeepgramClient;
import dev.voqal.provider.clients.openai.OpenAiClient;
import dev.voqal.provider.clients.picovoice.PicovoiceOrcaClient;
import dev.voqal.services.VoqalVoiceService;

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
        var vSpacer1 = new Spacer();

        //======== this ========
        setLayout(new GridLayoutManager(12, 3, new Insets(0, 0, 0, 0), 5, -1));

        //---- label6 ----
        label6.setText("Provider:");
        add(label6, new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- providerComboBox ----
        providerComboBox.setModel(new DefaultComboBoxModel<>(TTSProvider.getEntries()
                .stream().map(TTSProvider::getDisplayName).toArray(String[]::new)));
        add(providerComboBox, new GridConstraints(0, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label7 ----
        label7.setText("Key:");
        add(label7, new GridConstraints(1, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(providerPasswordField, new GridConstraints(1, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label9 ----
        label9.setText("Organization Id:");
        add(label9, new GridConstraints(2, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(orgIdTextField, new GridConstraints(2, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label8 ----
        label8.setText("Model:");
        add(label8, new GridConstraints(3, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- modelNameComboBox ----
        modelNameComboBox.setEditable(true);
        modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
            "tts-1",
            "tts-1-hd"
        }));
        add(modelNameComboBox, new GridConstraints(3, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label2 ----
        label2.setText("Voice:");
        add(label2, new GridConstraints(4, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- voiceComboBox ----
        voiceComboBox.setModel(new DefaultComboBoxModel<>(OpenAiClient.getVOICES()));
        add(voiceComboBox, new GridConstraints(4, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(separator1, new GridConstraints(5, 0, 1, 3,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label1 ----
        label1.setText("Speed:");
        add(label1, new GridConstraints(6, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- speedSlider ----
        speedSlider.setMaximum(200);
        speedSlider.setValue(100);
        add(speedSlider, new GridConstraints(6, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label3 ----
        label3.setText("Pitch:");
        add(label3, new GridConstraints(7, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- pitchSlider ----
        pitchSlider.setMaximum(200);
        pitchSlider.setValue(100);
        add(pitchSlider, new GridConstraints(7, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label4 ----
        label4.setText("Rate:");
        add(label4, new GridConstraints(8, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- rateSlider ----
        rateSlider.setMaximum(200);
        rateSlider.setValue(100);
        add(rateSlider, new GridConstraints(8, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label5 ----
        label5.setText("Volume:");
        add(label5, new GridConstraints(9, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- volumeSlider ----
        volumeSlider.setMaximum(200);
        volumeSlider.setValue(100);
        add(volumeSlider, new GridConstraints(9, 1, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- resetButton ----
        resetButton.setText("Reset");
        add(resetButton, new GridConstraints(10, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- listenButton ----
        listenButton.setText("Play Voice");
        add(listenButton, new GridConstraints(10, 2, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(vSpacer1, new GridConstraints(11, 0, 1, 3,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JBLabel label6;
    private JComboBox<String> providerComboBox;
    private JBLabel label7;
    private JBPasswordField providerPasswordField;
    private JBLabel label9;
    private JBTextField orgIdTextField;
    private JBLabel label8;
    private JComboBox<String> modelNameComboBox;
    private JBLabel label2;
    private JComboBox<String> voiceComboBox;
    private JSeparator separator1;
    private JBLabel label1;
    private JBSlider speedSlider;
    private JBLabel label3;
    private JBSlider pitchSlider;
    private JBLabel label4;
    private JBSlider rateSlider;
    private JBLabel label5;
    private JBSlider volumeSlider;
    private JButton resetButton;
    private JButton listenButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
