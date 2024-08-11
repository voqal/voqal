package dev.voqal.ide.ui.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.*;
import dev.voqal.config.settings.LanguageModelSettings;
import dev.voqal.config.settings.LanguageModelSettings.LMProvider;
import dev.voqal.config.settings.LanguageModelSettings.OProvider;
import dev.voqal.provider.clients.anthropic.AnthropicClient;
import dev.voqal.provider.clients.deepseek.DeepSeekClient;
import dev.voqal.provider.clients.fireworks.FireworksClient;
import dev.voqal.provider.clients.googleapi.GoogleApiClient;
import dev.voqal.provider.clients.groq.GroqClient;
import dev.voqal.provider.clients.mistralai.MistralAiClient;
import dev.voqal.provider.clients.ollama.OllamaClient;
import dev.voqal.provider.clients.openai.OpenAiClient;
import dev.voqal.provider.clients.togetherai.TogetherAiClient;
import dev.voqal.provider.clients.vertexai.VertexAiClient;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.Objects;

public class LanguageModelSettingsPanel extends JBPanel<LanguageModelSettingsPanel> {

    private final Project project;
    private final LanguageModelSettings settings;

    public LanguageModelSettingsPanel(Project project, LanguageModelSettings settings) {
        this.project = project;
        this.settings = settings;
        initComponents();

        var lmProvider = settings.getProvider();
        modelNameComboBox.addActionListener(e -> {
            if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.OPENAI) {
                tokenLimitSpinner.setValue(OpenAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.TOGETHER_AI) {
                tokenLimitSpinner.setValue(TogetherAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.VERTEX_AI) {
                tokenLimitSpinner.setValue(VertexAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.GOOGLE_API) {
                tokenLimitSpinner.setValue(GoogleApiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.ANTHROPIC) {
                tokenLimitSpinner.setValue(AnthropicClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.DEEPSEEK) {
                tokenLimitSpinner.setValue(DeepSeekClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.GROQ) {
                tokenLimitSpinner.setValue(GroqClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.FIREWORKS_AI) {
                tokenLimitSpinner.setValue(FireworksClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            } else if (settings.getTokenLimit() == -1 && lmProvider == LMProvider.MISTRAL_AI) {
                tokenLimitSpinner.setValue(MistralAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
            }
        });

        observabilityComboBox.addActionListener(e -> {
            if (Objects.equals(observabilityComboBox.getSelectedItem(), "Helicone")) {
                label8.setVisible(true);
                observabilityPasswordField.setVisible(true);
                label2.setVisible(true);
                observabilityUserTextField.setVisible(true);
            } else {
                label8.setVisible(false);
                observabilityPasswordField.setVisible(false);
                label2.setVisible(false);
                observabilityUserTextField.setVisible(false);
            }
        });
    }

    public boolean isModified() {
        var config = settings;
        var lmProvider = settings.getProvider();
        if (lmProvider.isKeyRequired()) {
            if (!Objects.equals(config.getProviderKey(), new String(this.providerPasswordField.getPassword()))) {
                return true;
            }
        }
        if (lmProvider.isOrgIdAvailable()) {
            if (!Objects.equals(config.getOrgId(), this.orgIdTextField.getText())) {
                return true;
            }
        }
        if (lmProvider.isApiUrlRequired()) {
            if (!Objects.equals(config.getApiUrl(), this.apiUrlTextField.getText())) {
                return true;
            }
        }
        if (!Objects.equals(config.getModelName(), modelNameComboBox.getSelectedItem())) {
            return true;
        }

        var observabilityProvider = OProvider.valueOf(observabilityComboBox.getSelectedItem().toString());
        if (!Objects.equals(config.getObservabilityProvider(), observabilityProvider)) {
            return true;
        }
        if (observabilityProvider.isKeyRequired()) {
            if (!Objects.equals(config.getObservabilityKey(), new String(this.observabilityPasswordField.getPassword()))) {
                return true;
            }
        }
        if (observabilityProvider != OProvider.None && !Objects.equals(config.getObservabilityUserId(), observabilityUserTextField.getText())) {
            return true;
        }

        if (!Objects.equals(config.getTokenLimit(), tokenLimitSpinner.getValue())) {
            return true;
        }
        if (!Objects.equals(config.getTemperature(), temperatureSpinner.getValue())) {
            if (config.getTemperature() != null || (double) temperatureSpinner.getValue() >= 0.0) {
                return true;
            }
        }
        if (!Objects.equals(config.getApiHeaders(), headerParamsTextField.getText())) {
            return true;
        }
        if (lmProvider.isProjectIdRequired()) {
            if (!Objects.equals(config.getProjectId(), projectIdTextField.getText())) {
                return true;
            }
        }
        if (lmProvider.isLocationRequired()) {
            if (!Objects.equals(config.getLocation(), locationTextField.getText())) {
                return true;
            }
        }
        if (!Objects.equals(config.getName(), nameTextField.getText())) {
            return true;
        }
        if (lmProvider.isAudioModalityAvailable()) {
            if (!Objects.equals(config.getAudioModality(), audioModalityCheckBox.isSelected())) {
                return true;
            }
        }
        return false;
    }

    public LanguageModelSettings getConfig() {
        var providerKey = new String(this.providerPasswordField.getPassword());
        var lmProvider = settings.getProvider();
        if (!lmProvider.isKeyRequired()) {
            providerKey = ""; //reset
        }

        var apiUrl = this.apiUrlTextField.getText();
        if (!lmProvider.isApiUrlRequired()) {
            apiUrl = "";
        }

        var observabilityUser = observabilityUserTextField.getText();
        var observabilityProviderKey = new String(this.observabilityPasswordField.getPassword());
        var observabilityProvider = OProvider.valueOf(observabilityComboBox.getSelectedItem().toString());
        if (observabilityProvider == OProvider.None) {
            observabilityUser = ""; //reset
            observabilityProviderKey = ""; //reset
        }
        Double temperature = (double) temperatureSpinner.getValue();
        if (temperature == -1.0) {
            temperature = null;
        } else if (temperature < 0.0) {
            temperature = null;
        }

        var projectId = projectIdTextField.getText();
        if (!lmProvider.isProjectIdRequired()) {
            projectId = ""; //reset
        }
        var location = locationTextField.getText();
        if (!lmProvider.isLocationRequired()) {
            location = ""; //reset
        }

        var name = nameTextField.getText();
        if (name.isBlank()) {
            name = lmProvider.name();
        }

        var audioModality = audioModalityCheckBox.isSelected();
        if (!lmProvider.isAudioModalityAvailable()) {
            audioModality = false;
        }

        return new LanguageModelSettings(
                lmProvider,
                providerKey,
                orgIdTextField.getText(),
                modelNameComboBox.getSelectedItem().toString(),
                null,
                temperature,
                observabilityProvider,
                observabilityProviderKey,
                observabilityUser,
                apiUrl,
                (int) tokenLimitSpinner.getValue(),
                headerParamsTextField.getText(),
                projectId,
                location,
                name,
                audioModality
        );
    }

    public void applyConfig(LanguageModelSettings config) {
        providerPasswordField.setText(config.getProviderKey());
        orgIdTextField.setText(config.getOrgId());
        apiUrlTextField.setText(config.getApiUrl());
        headerParamsTextField.setText(config.getApiHeaders());
        projectIdTextField.setText(config.getProjectId());
        locationTextField.setText(config.getLocation());
        nameTextField.setText(config.getName());
        audioModalityCheckBox.setSelected(config.getAudioModality());
        observabilityUserTextField.setText(config.getObservabilityUserId());

        var lmProvider = settings.getProvider();
        if (lmProvider == LMProvider.TOGETHER_AI) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    TogetherAiClient.getMODELS().stream().sorted().toArray(String[]::new))
            );
        } else if (lmProvider == LMProvider.OPENAI) {
            var providerKey = new String(this.providerPasswordField.getPassword());
            OpenAiClient.getModels(project, providerKey).onSuccess(models -> {
                modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                        models.stream().sorted().toArray(String[]::new))
                );
                modelNameComboBox.setSelectedItem(config.getModelName());
            });
        } else if (lmProvider == LMProvider.HUGGING_FACE) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    config.getModelName()
            }));
        } else if (lmProvider == LMProvider.MISTRAL_AI) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    MistralAiClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.GROQ) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    GroqClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.OLLAMA) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    OllamaClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.VERTEX_AI) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    VertexAiClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.GOOGLE_API) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    GoogleApiClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.ANTHROPIC) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    AnthropicClient.getMODELS().toArray(new String[0]))
            );
        } else if (lmProvider == LMProvider.FIREWORKS_AI) {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                    FireworksClient.getMODELS().toArray(new String[0]))
            );
        } else {
            modelNameComboBox.setModel(new DefaultComboBoxModel<>(new String[] {""}));
        }
        modelNameComboBox.setSelectedItem(config.getModelName());
        tokenLimitSpinner.setValue(config.getTokenLimit());
        if (config.getTokenLimit() == -1 && lmProvider == LMProvider.TOGETHER_AI) {
            tokenLimitSpinner.setValue(TogetherAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.OPENAI) {
            tokenLimitSpinner.setValue(OpenAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.VERTEX_AI) {
            tokenLimitSpinner.setValue(VertexAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.GOOGLE_API) {
            tokenLimitSpinner.setValue(GoogleApiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.ANTHROPIC) {
            tokenLimitSpinner.setValue(AnthropicClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.DEEPSEEK) {
            tokenLimitSpinner.setValue(DeepSeekClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.GROQ) {
            tokenLimitSpinner.setValue(GroqClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.FIREWORKS_AI) {
            tokenLimitSpinner.setValue(FireworksClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        } else if (config.getTokenLimit() == -1 && lmProvider == LMProvider.MISTRAL_AI) {
            tokenLimitSpinner.setValue(MistralAiClient.getTokenLimit(modelNameComboBox.getSelectedItem().toString()));
        }

        if (config.getTemperature() == null) {
            temperatureSpinner.setValue(-1.0);
        } else {
            temperatureSpinner.setValue(config.getTemperature());
        }

        if (config.getProvider() == LMProvider.OLLAMA) {
            apiUrlTextField.getEmptyText().setText("http://localhost:11434/api/generate");
        } else {
            apiUrlTextField.getEmptyText().setText("https://api.openai.com/v1/");
        }

        observabilityComboBox.setSelectedItem(config.getObservabilityProvider().name());
        observabilityPasswordField.setText(config.getObservabilityKey());
        if (Objects.equals(observabilityComboBox.getSelectedItem(), OProvider.Helicone.name())) {
            label8.setVisible(true);
            observabilityPasswordField.setVisible(true);
        } else {
            label8.setVisible(false);
            observabilityPasswordField.setVisible(false);
        }

        if (lmProvider.isModelNameRequired()) {
            label5.setVisible(true);
            modelNameComboBox.setVisible(true);
        } else {
            label5.setVisible(false);
            modelNameComboBox.setVisible(false);
        }
        if (lmProvider.isApiUrlRequired()) {
            apiUrlLabel.setVisible(true);
            apiUrlTextField.setVisible(true);
        } else {
            apiUrlLabel.setVisible(false);
            apiUrlTextField.setVisible(false);
        }
        if (lmProvider.isOrgIdAvailable()) {
            label11.setVisible(true);
            orgIdTextField.setVisible(true);
        } else {
            label11.setVisible(false);
            orgIdTextField.setVisible(false);
        }
        if (lmProvider.isProjectIdRequired()) {
            projectIdLabel.setVisible(true);
            projectIdTextField.setVisible(true);
        } else {
            projectIdLabel.setVisible(false);
            projectIdTextField.setVisible(false);
        }
        if (lmProvider.isLocationRequired()) {
            locationLabel.setVisible(true);
            locationTextField.setVisible(true);
        } else {
            locationLabel.setVisible(false);
            locationTextField.setVisible(false);
        }
        if (lmProvider.isKeyRequired()) {
            label6.setVisible(true);
            providerPasswordField.setVisible(true);
        } else {
            label6.setVisible(false);
            providerPasswordField.setVisible(false);
        }
        if (lmProvider.isAudioModalityAvailable()) {
            audioModalityCheckBox.setEnabled(true);
        } else {
            audioModalityCheckBox.setEnabled(false);
        }
        if (lmProvider.isHeaderParamsAvailable()) {
            headerParamsLabel.setVisible(true);
            headerParamsTextField.setVisible(true);
        } else {
            headerParamsLabel.setVisible(false);
            headerParamsTextField.setVisible(false);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        nameLabel = new JBLabel();
        nameTextField = new JBTextField();
        label6 = new JBLabel();
        providerPasswordField = new JBPasswordField();
        providerPasswordField.getEmptyText().setText("(required)");
        projectIdLabel = new JBLabel();
        projectIdTextField = new JBTextField();
        locationLabel = new JBLabel();
        locationTextField = new JBTextField();
        label11 = new JBLabel();
        orgIdTextField = new JBTextField();
        orgIdTextField.getEmptyText().setText("(optional)");
        apiUrlLabel = new JBLabel();
        apiUrlTextField = new JBTextField();
        headerParamsLabel = new JBLabel();
        headerParamsTextField = new JBTextField();
        headerParamsTextField.getEmptyText().setText("(key:value,key:value,...)");
        label5 = new JBLabel();
        modelNameComboBox = new JComboBox<>();
        audioModalityCheckBox = new JBCheckBox();
        panel3 = new JBPanel<>();
        label12 = new JBLabel();
        tokenLimitSpinner = new JSpinner();
        temperatureLabel = new JBLabel();
        temperatureSpinner = new JSpinner(new SpinnerNumberModel(-1.0, -1.0, 1.0, 0.1));
        panel2 = new JBPanel<>();
        label7 = new JBLabel();
        observabilityComboBox = new JComboBox<>();
        label8 = new JBLabel();
        observabilityPasswordField = new JBPasswordField();
        label2 = new JBLabel();
        observabilityUserTextField = new JBTextField();

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
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- nameLabel ----
        nameLabel.setText("Name");
        add(nameLabel, "cell 0 0");
        add(nameTextField, "cell 1 0 2 1");

        //---- label6 ----
        label6.setText("Key");
        add(label6, "cell 0 1");
        add(providerPasswordField, "cell 1 1 2 1");

        //---- projectIdLabel ----
        projectIdLabel.setText("Project Id");
        add(projectIdLabel, "cell 0 2");
        add(projectIdTextField, "cell 1 2 2 1");

        //---- locationLabel ----
        locationLabel.setText("Location");
        add(locationLabel, "cell 0 3");
        add(locationTextField, "cell 1 3 2 1");

        //---- label11 ----
        label11.setText("Organization Id");
        add(label11, "cell 0 4");
        add(orgIdTextField, "cell 1 4 2 1");

        //---- apiUrlLabel ----
        apiUrlLabel.setText("API URL");
        add(apiUrlLabel, "cell 0 5");
        add(apiUrlTextField, "cell 1 5 2 1");

        //---- headerParamsLabel ----
        headerParamsLabel.setText("Header Params");
        add(headerParamsLabel, "cell 0 6");
        add(headerParamsTextField, "cell 1 6 2 1");

        //---- label5 ----
        label5.setText("Model Name");
        add(label5, "cell 0 7");

        //---- modelNameComboBox ----
        modelNameComboBox.setEditable(true);
        modelNameComboBox.setModel(new DefaultComboBoxModel<>(
                OpenAiClient.getMODELS().toArray(new String[0]))
        );
        add(modelNameComboBox, "cell 1 7 2 1");

        //---- audioModalityCheckBox ----
        audioModalityCheckBox.setText("Enable Audio Modality");
        add(audioModalityCheckBox, "cell 0 8 3 1");

        //======== panel3 ========
        {
            panel3.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[grow,fill]",
                // rows
                "[]"));

            //---- label12 ----
            label12.setText("Token Limit");
            label12.setHorizontalAlignment(SwingConstants.RIGHT);
            panel3.add(label12, "cell 0 0");

            //---- tokenLimitSpinner ----
            tokenLimitSpinner.setModel(new SpinnerNumberModel(-1, -1, null, 1));
            panel3.add(tokenLimitSpinner, "cell 1 0 2 1");

            //---- temperatureLabel ----
            temperatureLabel.setText("Temperature");
            temperatureLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel3.add(temperatureLabel, "cell 1 0 2 1");
            panel3.add(temperatureSpinner, "cell 1 0 2 1");
        }
        add(panel3, "cell 0 9 3 1");

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(new EtchedBorder(), "Observability"));
            panel2.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[fill]" +
                "[fill]" +
                "[grow,fill]",
                // rows
                "[]" +
                "[]" +
                "[]"));

            //---- label7 ----
            label7.setText("Provider");
            panel2.add(label7, "cell 0 0");

            //---- observabilityComboBox ----
            observabilityComboBox.setModel(new DefaultComboBoxModel<>(OProvider.getEntries()
                    .stream().map(OProvider::name).toArray(String[]::new)));
            panel2.add(observabilityComboBox, "cell 1 0 2 1");

            //---- label8 ----
            label8.setText("Key");
            panel2.add(label8, "cell 0 1");
            panel2.add(observabilityPasswordField, "cell 1 1 2 1");

            //---- label2 ----
            label2.setText("User");
            panel2.add(label2, "cell 0 2");

            //---- observabilityUserTextField ----
            observabilityUserTextField.setText("");
            panel2.add(observabilityUserTextField, "cell 1 2 2 1");
        }
        add(panel2, "cell 0 10 3 1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JBLabel nameLabel;
    private JBTextField nameTextField;
    private JBLabel label6;
    private JBPasswordField providerPasswordField;
    private JBLabel projectIdLabel;
    private JBTextField projectIdTextField;
    private JBLabel locationLabel;
    private JBTextField locationTextField;
    private JBLabel label11;
    private JBTextField orgIdTextField;
    private JBLabel apiUrlLabel;
    private JBTextField apiUrlTextField;
    private JBLabel headerParamsLabel;
    private JBTextField headerParamsTextField;
    private JBLabel label5;
    private JComboBox<String> modelNameComboBox;
    private JBCheckBox audioModalityCheckBox;
    private JBPanel panel3;
    private JBLabel label12;
    private JSpinner tokenLimitSpinner;
    private JBLabel temperatureLabel;
    private JSpinner temperatureSpinner;
    private JBPanel panel2;
    private JBLabel label7;
    private JComboBox<String> observabilityComboBox;
    private JBLabel label8;
    private JBPasswordField observabilityPasswordField;
    private JBLabel label2;
    private JBTextField observabilityUserTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
