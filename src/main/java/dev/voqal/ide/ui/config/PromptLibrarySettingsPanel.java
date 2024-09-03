package dev.voqal.ide.ui.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import dev.voqal.config.settings.LanguageModelSettings;
import dev.voqal.config.settings.PromptLibrarySettings;
import dev.voqal.config.settings.PromptSettings;
import dev.voqal.config.settings.PromptSettings.PProvider;
import dev.voqal.ide.ui.VoqalUI;
import dev.voqal.services.VoqalConfigService;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class PromptLibrarySettingsPanel extends JBPanel<PromptLibrarySettingsPanel> implements Disposable {

    private final Project project;
    private final HashMap<String, PromptSettings> promptLibrary;
    private boolean setupMode = false;

    public PromptLibrarySettingsPanel(Project project) {
        this.project = project;
        this.promptLibrary = new HashMap<>();
        initComponents();

        promptComboBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            PProvider.lenientValueOf(promptComboBox.getSelectedItem().toString()),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            prompt.getModelName(),
                            prompt.getShowPartialResults(),
                            prompt.getDecomposeDirectives(),
                            prompt.getCodeSmellCorrection(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            prompt.getEditFormat(),
                            prompt.getStreamCompletions()
                    )
            );

            selectedPromptChanged(project);
        });

        list1.addListSelectionListener(e -> {
            selectedPromptChanged(project);
        });

        promptFileTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                promptFileTextField.getText(),
                                "",
                                "",
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                prompt.getVectorStoreId(),
                                prompt.getAssistantId(),
                                prompt.getAssistantThreadId(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
                selectedPromptChanged(project);
            }
        });
        urlTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                "",
                                "",
                                urlTextField.getText(),
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                prompt.getVectorStoreId(),
                                prompt.getAssistantId(),
                                prompt.getAssistantThreadId(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
                selectedPromptChanged(project);
            }
        });

        languageModelComboBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            var languageModelName = Objects.toString(languageModelComboBox.getSelectedItem(), "");
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            prompt.getProvider(),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            languageModelName,
                            prompt.getShowPartialResults(),
                            prompt.getDecomposeDirectives(),
                            prompt.getCodeSmellCorrection(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            prompt.getEditFormat(),
                            prompt.getStreamCompletions()
                    )
            );
        });

        decompCheckBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            prompt.getProvider(),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            prompt.getModelName(),
                            prompt.getShowPartialResults(),
                            decompCheckBox.isSelected(),
                            prompt.getCodeSmellCorrection(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            prompt.getEditFormat(),
                            prompt.getStreamCompletions()
                    )
            );
        });

        streamCompletionsCheckBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            prompt.getProvider(),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            prompt.getModelName(),
                            prompt.getShowPartialResults(),
                            prompt.getDecomposeDirectives(),
                            prompt.getCodeSmellCorrection(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            prompt.getEditFormat(),
                            streamCompletionsCheckBox.isSelected()
                    )
            );
        });

        codeSmellCheckBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            prompt.getProvider(),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            prompt.getModelName(),
                            prompt.getShowPartialResults(),
                            prompt.getDecomposeDirectives(),
                            codeSmellCheckBox.isSelected(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            prompt.getEditFormat(),
                            prompt.getStreamCompletions()
                    )
            );
        });

        vectorStoreIdTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                prompt.getPromptFile(),
                                prompt.getPromptText(),
                                prompt.getPromptUrl(),
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                vectorStoreIdTextField.getText(),
                                prompt.getAssistantId(),
                                prompt.getAssistantThreadId(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
            }
        });
        assistantIdTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                prompt.getPromptFile(),
                                prompt.getPromptText(),
                                prompt.getPromptUrl(),
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                prompt.getVectorStoreId(),
                                assistantIdTextField.getText(),
                                prompt.getAssistantThreadId(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
            }
        });
        assistantThreadIdTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                prompt.getPromptFile(),
                                prompt.getPromptText(),
                                prompt.getPromptUrl(),
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                prompt.getVectorStoreId(),
                                prompt.getAssistantId(),
                                assistantThreadIdTextField.getText(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
            }
        });

        editFormatComboBox.addActionListener(e -> {
            var promptName = list1.getSelectedValue();
            var editFormat = PromptSettings.EditFormat.lenientValueOf(editFormatComboBox.getSelectedItem().toString());
            promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                            prompt.getProvider(),
                            prompt.getPromptName(),
                            prompt.getPromptFile(),
                            prompt.getPromptText(),
                            prompt.getPromptUrl(),
                            prompt.getModelName(),
                            prompt.getShowPartialResults(),
                            prompt.getDecomposeDirectives(),
                            prompt.getCodeSmellCorrection(),
                            prompt.getVectorStoreId(),
                            prompt.getAssistantId(),
                            prompt.getAssistantThreadId(),
                            editFormat,
                            prompt.getStreamCompletions()
                    )
            );
        });
    }

    private void selectedPromptChanged(Project project) {
        if (setupMode) return;
        var promptName = list1.getSelectedValue();
        var prompt = promptLibrary.get(promptName);
        var promptProvider = prompt.getProvider();
        if (promptProvider == PProvider.CUSTOM_FILE) {
            promptFileLabel.setVisible(true);
            promptFileTextField.setVisible(true);
        } else {
            promptFileLabel.setVisible(false);
            promptFileTextField.setVisible(false);
        }
        if (promptProvider == PProvider.CUSTOM_URL) {
            urlLabel.setVisible(true);
            urlTextField.setVisible(true);
        } else {
            urlLabel.setVisible(false);
            urlTextField.setVisible(false);
        }
        promptFileTextField.setText(prompt.getPromptFile());
        urlTextField.setText(prompt.getPromptUrl());
        promptComboBox.setSelectedItem(prompt.getProvider().getDisplayName());
        decompCheckBox.setSelected(prompt.getDecomposeDirectives());
        codeSmellCheckBox.setSelected(prompt.getCodeSmellCorrection());
        vectorStoreIdTextField.setText(prompt.getVectorStoreId());
        assistantIdTextField.setText(prompt.getAssistantId());
        assistantThreadIdTextField.setText(prompt.getAssistantThreadId());
        editFormatComboBox.setSelectedItem(prompt.getEditFormat().getDisplayName());

        if (prompt.getPromptName().equals("Idle Mode")) {
            decompCheckBox.setVisible(true);
            streamCompletionsCheckBox.setVisible(false);
            codeSmellCheckBox.setVisible(false);

            label4.setVisible(false);
            editFormatComboBox.setVisible(false);
            label3.setVisible(false);
            vectorStoreIdTextField.setVisible(false);
            label1.setVisible(false);
            assistantIdTextField.setVisible(false);
            label2.setVisible(false);
            assistantThreadIdTextField.setVisible(false);
        } else if (prompt.getPromptName().equals("Edit Mode")) {
            decompCheckBox.setVisible(false);
            streamCompletionsCheckBox.setVisible(true);
            codeSmellCheckBox.setVisible(true);

            //todo: support edit format for edit mode
            label4.setVisible(false);
            editFormatComboBox.setVisible(false);

            label3.setVisible(false);
            vectorStoreIdTextField.setVisible(false);
            label1.setVisible(false);
            assistantIdTextField.setVisible(false);
            label2.setVisible(false);
            assistantThreadIdTextField.setVisible(false);
        } else if (prompt.getPromptName().equals("Search Mode")) {
            decompCheckBox.setVisible(false);
            streamCompletionsCheckBox.setVisible(false);
            codeSmellCheckBox.setVisible(false);

            label4.setVisible(false);
            editFormatComboBox.setVisible(false);
            label3.setVisible(true);
            vectorStoreIdTextField.setVisible(true);
            label1.setVisible(true);
            assistantIdTextField.setVisible(true);
            label2.setVisible(true);
            assistantThreadIdTextField.setVisible(true);
        }

        var languageModelNames = project.getService(VoqalConfigService.class).getConfig()
                .getLanguageModelsSettings().getModels()
                .stream().map(LanguageModelSettings::getName).toArray(String[]::new);
        languageModelComboBox.setModel(new DefaultComboBoxModel<>(languageModelNames));
        if (languageModelNames.length > 0) {
            languageModelComboBox.setSelectedIndex(0);
        }
        if (!prompt.getModelName().isEmpty() && Set.of(languageModelNames).contains(prompt.getModelName())) {
            languageModelComboBox.setSelectedItem(prompt.getModelName());
        }

        //reset other fields
        if (promptProvider == PProvider.VOQAL) {
            promptLibrary.computeIfPresent(promptName, (k, p) -> p.copy(
                            p.getProvider(),
                            p.getPromptName(),
                            "",
                            "",
                            "",
                            p.getModelName(),
                            p.getShowPartialResults(),
                            p.getDecomposeDirectives(),
                            p.getCodeSmellCorrection(),
                            p.getVectorStoreId(),
                            p.getAssistantId(),
                            p.getAssistantThreadId(),
                            p.getEditFormat(),
                            p.getStreamCompletions()
                    )
            );
        } else if (promptProvider == PProvider.CUSTOM_TEXT) {
            promptLibrary.computeIfPresent(promptName, (k, p) -> p.copy(
                            p.getProvider(),
                            p.getPromptName(),
                            "",
                            p.getPromptText(),
                            "",
                            p.getModelName(),
                            p.getShowPartialResults(),
                            p.getDecomposeDirectives(),
                            p.getCodeSmellCorrection(),
                            p.getVectorStoreId(),
                            p.getAssistantId(),
                            p.getAssistantThreadId(),
                            p.getEditFormat(),
                            p.getStreamCompletions()
                    )
            );
        } else if (promptProvider == PProvider.CUSTOM_FILE) {
            promptLibrary.computeIfPresent(promptName, (k, p) -> p.copy(
                            p.getProvider(),
                            p.getPromptName(),
                            p.getPromptFile(),
                            "",
                            "",
                            p.getModelName(),
                            p.getShowPartialResults(),
                            p.getDecomposeDirectives(),
                            p.getCodeSmellCorrection(),
                            p.getVectorStoreId(),
                            p.getAssistantId(),
                            p.getAssistantThreadId(),
                            p.getEditFormat(),
                            p.getStreamCompletions()
                    )
            );
        } else if (promptProvider == PProvider.CUSTOM_URL) {
            promptLibrary.computeIfPresent(promptName, (k, p) -> p.copy(
                            p.getProvider(),
                            p.getPromptName(),
                            "",
                            "",
                            p.getPromptUrl(),
                            p.getModelName(),
                            p.getShowPartialResults(),
                            p.getDecomposeDirectives(),
                            p.getCodeSmellCorrection(),
                            p.getVectorStoreId(),
                            p.getAssistantId(),
                            p.getAssistantThreadId(),
                            p.getEditFormat(),
                            p.getStreamCompletions()
                    )
            );
        } else {
            throw new IllegalStateException("Unknown prompt provider: " + promptProvider);
        }

        var configService = project.getService(VoqalConfigService.class);
        if (promptProvider == PProvider.VOQAL) {
            var promptTemplate = configService.getPromptTemplate(promptName);
            resetPromptPreview(project, promptTemplate);
        } else if (promptProvider == PProvider.CUSTOM_FILE) {
            var promptFile = promptFileTextField.getText();
            var promptText = "Unable to load prompt file";
            try {
                if (!promptFile.isBlank()) {
                    promptText = IOUtils.toString(new FileInputStream(promptFile), StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                promptText = "Unable to load prompt file. Error: " + e.getMessage();
            }
            resetPromptPreview(project, promptText);
        } else if (promptProvider == PProvider.CUSTOM_URL) {
            var promptText = "Unable to load prompt URL";
            try {
                if (!prompt.getPromptUrl().isBlank()) {
                    promptText = IOUtils.toString(new URL(prompt.getPromptUrl()), StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                promptText = "Unable to load prompt URL. Error: " + e.getMessage();
            }
            resetPromptPreview(project, promptText);

        } else {
            resetPromptPreview(project, prompt.getPromptText());
        }
    }

    private void resetPromptPreview(Project project, String promptTemplate) {
        var promptProvider = PProvider.lenientValueOf(promptComboBox.getSelectedItem().toString());
        if (markdownPanel != null) {
            EditorFactory.getInstance().releaseEditor(markdownPanel);
        }
        markdownPanel = VoqalUI.createPreviewComponent(
                project,
                promptTemplate,
                promptProvider == PProvider.CUSTOM_TEXT,
                this
        );
        markdownPanel.getComponent().setPreferredSize(scrollPane2.getPreferredSize());
        scrollPane2.setViewportView(markdownPanel.getComponent());

        markdownPanel.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                var promptName = list1.getSelectedValue();
                promptLibrary.computeIfPresent(promptName, (k, prompt) -> prompt.copy(
                                prompt.getProvider(),
                                prompt.getPromptName(),
                                prompt.getPromptFile(),
                                markdownPanel.getDocument().getText(),
                                prompt.getPromptUrl(),
                                prompt.getModelName(),
                                prompt.getShowPartialResults(),
                                prompt.getDecomposeDirectives(),
                                prompt.getCodeSmellCorrection(),
                                prompt.getVectorStoreId(),
                                prompt.getAssistantId(),
                                prompt.getAssistantThreadId(),
                                prompt.getEditFormat(),
                                prompt.getStreamCompletions()
                        )
                );
            }
        }, this);
    }

    public boolean isModified(PromptLibrarySettings config) {
        var savedPrompts = config.getPrompts();
        if (savedPrompts.size() != list1.getModel().getSize()) {
            return true;
        }
        if (savedPrompts.stream().anyMatch(prompt -> !promptLibrary.containsKey(prompt.getPromptName()))) {
            return true;
        }
        for (var prompt : savedPrompts) {
            var promptName = prompt.getPromptName();
            var savedPrompt = promptLibrary.get(promptName);
            if (!prompt.equals(savedPrompt)) {
                return true;
            }
        }
        return false;
    }

    public PromptLibrarySettings getConfig() {
        var prompts = new ArrayList<PromptSettings>();
        for (var i = 0; i < list1.getModel().getSize(); i++) {
            var promptName = list1.getModel().getElementAt(i);
            var prompt = promptLibrary.get(promptName);
            prompts.add(prompt);
        }
        return new PromptLibrarySettings(prompts);
    }

    public void applyConfig(PromptLibrarySettings config) {
        setupMode = true;
        promptLibrary.clear();
        DefaultListModel<String> model = new DefaultListModel<>();
        var defaultPrompts = PromptLibrarySettings.getDEFAULT_PROMPTS();
        config.getPrompts().forEach(prompt -> {
            //todo: remove conditional when custom prompts are supported
            if (defaultPrompts.stream().anyMatch(p -> p.getPromptName().equals(prompt.getPromptName()))) {
                promptLibrary.put(prompt.getPromptName(), prompt);
                model.addElement(prompt.getPromptName());
            }
        });
        //make sure we have all the default prompts
        defaultPrompts.forEach(prompt -> {
            if (!promptLibrary.containsKey(prompt.getPromptName())) {
                promptLibrary.put(prompt.getPromptName(), prompt);
                model.addElement(prompt.getPromptName());
            }
        });
        list1.setModel(model);
        setupMode = false;

        list1.setSelectedIndex(0);
        var promptName = list1.getSelectedValue();
        var prompt = promptLibrary.get(promptName);
        promptComboBox.setSelectedItem(prompt.getProvider().getDisplayName());

        var languageModelNames = project.getService(VoqalConfigService.class).getConfig()
                .getLanguageModelsSettings().getModels()
                .stream().map(LanguageModelSettings::getName).toArray(String[]::new);
        languageModelComboBox.setModel(new DefaultComboBoxModel<>(languageModelNames));
        if (languageModelNames.length > 0) {
            languageModelComboBox.setSelectedIndex(0);
        }
        if (!prompt.getModelName().isEmpty() && Set.of(languageModelNames).contains(prompt.getModelName())) {
            languageModelComboBox.setSelectedItem(prompt.getModelName());
        }
    }

    @Override
    public void dispose() {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - unknown
        panel1 = new JBPanel();
        promptComboBox = new JComboBox<>();
        label9 = new JBLabel();
        promptFileLabel = new JBLabel();
        promptFileTextField = new JBTextField();
        urlLabel = new JBLabel();
        urlTextField = new JBTextField();
        languageModelComboBox = new JComboBox();
        label5 = new JBLabel();
        label4 = new JBLabel();
        editFormatComboBox = new JComboBox<>();
        label3 = new JBLabel();
        vectorStoreIdTextField = new JBTextField();
        label1 = new JBLabel();
        assistantIdTextField = new JBTextField();
        label2 = new JBLabel();
        assistantThreadIdTextField = new JBTextField();
        decompCheckBox = new JCheckBox();
        streamCompletionsCheckBox = new JCheckBox();
        codeSmellCheckBox = new JCheckBox();
        scrollPane1 = new JBScrollPane();
        list1 = new JList<>();
        scrollPane2 = new JBScrollPane();

        //======== this ========
        setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]" +
            "[grow,fill]",
            // rows
            "[]" +
            "[grow]"));

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
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

            //---- promptComboBox ----
            promptComboBox.setModel(new DefaultComboBoxModel<>(PromptSettings.PProvider.getEntries()
                    .stream().map(PromptSettings.PProvider::getDisplayName).toArray(String[]::new)));
            panel1.add(promptComboBox, "cell 1 0");

            //---- label9 ----
            label9.setText("Provider");
            panel1.add(label9, "cell 0 0");

            //---- promptFileLabel ----
            promptFileLabel.setText("File");
            panel1.add(promptFileLabel, "cell 0 1");
            panel1.add(promptFileTextField, "cell 1 1");

            //---- urlLabel ----
            urlLabel.setText("Url");
            panel1.add(urlLabel, "cell 0 2");
            panel1.add(urlTextField, "cell 1 2");

            //---- languageModelComboBox ----
            languageModelComboBox.setEditable(true);
            panel1.add(languageModelComboBox, "cell 1 3");

            //---- label5 ----
            label5.setText("Language Model");
            panel1.add(label5, "cell 0 3");

            //---- label4 ----
            label4.setText("Edit Format");
            panel1.add(label4, "cell 0 4");

            //---- editFormatComboBox ----
            editFormatComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "Full Text",
                "Diff"
            }));
            panel1.add(editFormatComboBox, "cell 1 4");

            //---- label3 ----
            label3.setText("Vector Store Id");
            panel1.add(label3, "cell 0 5");
            panel1.add(vectorStoreIdTextField, "cell 1 5");

            //---- label1 ----
            label1.setText("Assistant Id");
            panel1.add(label1, "cell 0 6");
            panel1.add(assistantIdTextField, "cell 1 6");

            //---- label2 ----
            label2.setText("Assistant Thread Id");
            panel1.add(label2, "cell 0 7");
            panel1.add(assistantThreadIdTextField, "cell 1 7");

            //---- decompCheckBox ----
            decompCheckBox.setText("Enable Directive Decomposition");
            decompCheckBox.setSelected(true);
            panel1.add(decompCheckBox, "cell 0 8 2 1");

            //---- streamCompletionsCheckBox ----
            streamCompletionsCheckBox.setText("Enable Streaming Completions");
            streamCompletionsCheckBox.setSelected(true);
            panel1.add(streamCompletionsCheckBox, "cell 0 9 2 1");

            //---- codeSmellCheckBox ----
            codeSmellCheckBox.setText("Enable Code Smell Correction");
            codeSmellCheckBox.setSelected(true);
            panel1.add(codeSmellCheckBox, "cell 0 10 2 1");
        }
        add(panel1, "cell 1 0");

        //======== scrollPane1 ========
        {

            //---- list1 ----
            list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list1.setModel(new DefaultListModel<>() {});
            scrollPane1.setViewportView(list1);
        }
        add(scrollPane1, "cell 0 0 1 2,growy");

        //======== scrollPane2 ========
        {

            //---- markdownPanel ----
            markdownPanel = VoqalUI.createPreviewComponent(project, "", false,this);
            markdownPanel.getComponent().setPreferredSize(scrollPane2.getPreferredSize());
            scrollPane2.setViewportView(markdownPanel.getComponent());
        }
        add(scrollPane2, "cell 1 1,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - unknown
    private Editor markdownPanel;
    private JBPanel panel1;
    private JComboBox<String> promptComboBox;
    private JBLabel label9;
    private JBLabel promptFileLabel;
    private JBTextField promptFileTextField;
    private JBLabel urlLabel;
    private JBTextField urlTextField;
    private JComboBox languageModelComboBox;
    private JBLabel label5;
    private JBLabel label4;
    private JComboBox<String> editFormatComboBox;
    private JBLabel label3;
    private JBTextField vectorStoreIdTextField;
    private JBLabel label1;
    private JBTextField assistantIdTextField;
    private JBLabel label2;
    private JBTextField assistantThreadIdTextField;
    private JCheckBox decompCheckBox;
    private JCheckBox streamCompletionsCheckBox;
    private JCheckBox codeSmellCheckBox;
    private JBScrollPane scrollPane1;
    private JList<String> list1;
    private JBScrollPane scrollPane2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
