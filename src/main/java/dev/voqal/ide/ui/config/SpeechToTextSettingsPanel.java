package dev.voqal.ide.ui.config;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import dev.voqal.config.settings.SpeechToTextSettings;
import dev.voqal.config.settings.SpeechToTextSettings.STTProvider;
import dev.voqal.utils.Iso639Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SpeechToTextSettingsPanel extends JBPanel<SpeechToTextSettingsPanel> {

    public SpeechToTextSettingsPanel() {
        initComponents();

        providerComboBox.addActionListener(e -> {
            providerPasswordField.setText("");
            orgIdTextField.setText("");
            urlTextField.setText("");
            queryParamsTextField.setText("");

            var provider = STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
            label3.setVisible(provider.isKeyRequired());
            providerPasswordField.setVisible(provider.isKeyRequired());
            label7.setVisible(provider.isOrgIdAvailable());
            orgIdTextField.setVisible(provider.isOrgIdAvailable());
            label5.setVisible(provider.isUrlRequired());
            urlTextField.setVisible(provider.isUrlRequired());
            label4.setVisible(provider.isModelNameRequired());
            modelNameComboBox.setVisible(provider.isModelNameRequired());
            queryParamsLabel.setVisible(provider.isQueryParamsSupported());
            queryParamsTextField.setVisible(provider.isQueryParamsSupported());
            label2.setVisible(provider.isLanguageCodeSupported());
            languageComboBox.setVisible(provider.isLanguageCodeSupported());
            revalidate();
            repaint();

            if (provider == STTProvider.OPENAI) {
                modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                        "whisper-1"
                }));
            } else if (provider == STTProvider.DEEPGRAM) {
                modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                        "nova-2",
                        "nova",
                        "enhanced",
                        "base"
                }));
            } else if (provider == STTProvider.GROQ) {
                modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                        "whisper-large-v3",
                        "distil-whisper-large-v3-en" //todo: disable lang combobox
                }));
            } else {
                modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{""}));
            }
        });
    }

    public boolean isModified(SpeechToTextSettings config) {
        if (!Objects.equals(config.getProvider().getDisplayName(), providerComboBox.getSelectedItem())) {
            return true;
        }
        if (STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isKeyRequired()) {
            if (!Objects.equals(config.getProviderKey(), new String(this.providerPasswordField.getPassword()))) {
                return true;
            }
        }
        if (STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isOrgIdAvailable()) {
            if (!Objects.equals(config.getOrgId(), orgIdTextField.getText())) {
                return true;
            }
        }
        if (!Objects.equals(config.getProviderUrl(), urlTextField.getText())) {
            return true;
        }
        if (STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isModelNameRequired()) {
            if (!Objects.equals(config.getModelName(), modelNameComboBox.getSelectedItem())) {
                return true;
            }
        }
        if (STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isQueryParamsSupported()) {
            if (!Objects.equals(config.getQueryParams(), queryParamsTextField.getText())) {
                return true;
            }
        }
        if (STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isLanguageCodeSupported()) {
            if (!Objects.equals(config.getLanguage(), Iso639Language.findByName(languageComboBox.getSelectedItem().toString()))) {
                return true;
            }
        }
        return false;
    }

    public SpeechToTextSettings getConfig() {
        String providerKey = new String(this.providerPasswordField.getPassword());
        STTProvider sttProvider = STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        if (!sttProvider.isKeyRequired()) {
            providerKey = ""; //reset
        }
        String providerUrl = urlTextField.getText();
        if (!sttProvider.isUrlRequired()) {
            providerUrl = ""; //reset
        }
        String queryParams = queryParamsTextField.getText();
        if (!sttProvider.isQueryParamsSupported()) {
            queryParams = ""; //reset
        }
        return new SpeechToTextSettings(
                sttProvider,
                providerKey,
                orgIdTextField.getText(),
                providerUrl,
                modelNameComboBox.getSelectedItem().toString(),
                queryParams,
                Iso639Language.findByName(languageComboBox.getSelectedItem().toString())
        );
    }

    public void applyConfig(SpeechToTextSettings config) {
        providerComboBox.setSelectedItem(config.getProvider().getDisplayName());
        providerPasswordField.setText(config.getProviderKey());
        orgIdTextField.setText(config.getOrgId());
        urlTextField.setText(config.getProviderUrl());
        modelNameComboBox.setSelectedItem(config.getModelName());
        queryParamsTextField.setText(config.getQueryParams());
        languageComboBox.setSelectedItem(config.getLanguage().getDisplayName());

        var provider = STTProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        label3.setVisible(provider.isKeyRequired());
        providerPasswordField.setVisible(provider.isKeyRequired());
        label7.setVisible(provider.isOrgIdAvailable());
        orgIdTextField.setVisible(provider.isOrgIdAvailable());
        label5.setVisible(provider.isUrlRequired());
        urlTextField.setVisible(provider.isUrlRequired());
        label4.setVisible(provider.isModelNameRequired());
        modelNameComboBox.setVisible(provider.isModelNameRequired());
        queryParamsLabel.setVisible(provider.isQueryParamsSupported());
        queryParamsTextField.setVisible(provider.isQueryParamsSupported());
        label2.setVisible(provider.isLanguageCodeSupported());
        languageComboBox.setVisible(provider.isLanguageCodeSupported());
        revalidate();
        repaint();

        if (provider == STTProvider.OPENAI) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "whisper-1"
            }));
        } else if (provider == STTProvider.DEEPGRAM) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "nova-2",
                    "nova",
                    "enhanced",
                    "base"
            }));
        } else if (provider == STTProvider.GROQ) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "whisper-large-v3",
                    "distil-whisper-large-v3-en" //todo: disable lang combobox
            }));
        } else {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        label1 = new JBLabel();
        providerComboBox = new JComboBox<>();
        label3 = new JBLabel();
        providerPasswordField = new JBPasswordField();
        providerPasswordField.getEmptyText().setText("(required)");
        label7 = new JBLabel();
        orgIdTextField = new JBTextField();
        orgIdTextField.getEmptyText().setText("(optional)");
        label5 = new JBLabel();
        urlTextField = new JBTextField();
        urlTextField.getEmptyText().setText("http://localhost:9000/asr?output=json");
        label4 = new JBLabel();
        modelNameComboBox = new JComboBox<>();
        label2 = new JBLabel();
        languageComboBox = new JComboBox<>();
        queryParamsLabel = new JBLabel();
        queryParamsTextField = new JBTextField();
        queryParamsTextField.getEmptyText().setText("query=param&query2=param2");

        //======== this ========
        setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]" +
            "[fill]" +
            "[grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label1 ----
        label1.setText("Provider");
        add(label1, "cell 0 0");

        //---- providerComboBox ----
        providerComboBox.setModel(new DefaultComboBoxModel<>(SpeechToTextSettings.STTProvider.getEntries()
                .stream().map(SpeechToTextSettings.STTProvider::getDisplayName).toArray(String[]::new)));
        add(providerComboBox, "cell 1 0 2 1");

        //---- label3 ----
        label3.setText("Key");
        add(label3, "cell 0 1");

        //---- providerPasswordField ----
        providerPasswordField.setPreferredSize(new Dimension(0, 0));
        add(providerPasswordField, "cell 1 1 2 1");

        //---- label7 ----
        label7.setText("Organization Id");
        add(label7, "cell 0 2");

        //---- orgIdTextField ----
        orgIdTextField.setPreferredSize(new Dimension(0, 0));
        add(orgIdTextField, "cell 1 2 2 1");

        //---- label5 ----
        label5.setText("URL");
        add(label5, "cell 0 3");

        //---- urlTextField ----
        urlTextField.setPreferredSize(new Dimension(0, 0));
        add(urlTextField, "cell 1 3 2 1");

        //---- label4 ----
        label4.setText("Model Name");
        add(label4, "cell 0 4");

        //---- modelNameComboBox ----
        modelNameComboBox.setEditable(true);
        modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
            "whisper-1"
        }));
        add(modelNameComboBox, "cell 1 4 2 1");

        //---- label2 ----
        label2.setText("Language");
        add(label2, "cell 0 5");

        //---- languageComboBox ----
        languageComboBox.setEditable(true);
        languageComboBox.setModel(new DefaultComboBoxModel<>(Iso639Language.getEntries()
                .stream().map(Iso639Language::getDisplayName).toArray(String[]::new)));
        languageComboBox.setSelectedItem(Iso639Language.ENGLISH.getDisplayName());
        add(languageComboBox, "cell 1 5 2 1");

        //---- queryParamsLabel ----
        queryParamsLabel.setText("Query Params");
        add(queryParamsLabel, "cell 0 6");
        add(queryParamsTextField, "cell 1 6 2 1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JBLabel label1;
    private JComboBox<String> providerComboBox;
    private JBLabel label3;
    private JBPasswordField providerPasswordField;
    private JBLabel label7;
    private JBTextField orgIdTextField;
    private JBLabel label5;
    private JBTextField urlTextField;
    private JBLabel label4;
    private JComboBox<String> modelNameComboBox;
    private JBLabel label2;
    private JComboBox<String> languageComboBox;
    private JBLabel queryParamsLabel;
    private JBTextField queryParamsTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
