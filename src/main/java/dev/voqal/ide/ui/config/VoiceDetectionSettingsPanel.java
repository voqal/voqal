package dev.voqal.ide.ui.config;

import java.awt.*;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.uiDesigner.core.*;
import dev.voqal.config.settings.VoiceDetectionSettings;
import dev.voqal.config.settings.VoiceDetectionSettings.VoiceDetectionProvider;
import dev.voqal.ide.logging.LoggerFactory;
import dev.voqal.provider.VadProvider;
import dev.voqal.provider.clients.picovoice.PicovoiceCobraClient;
import dev.voqal.provider.clients.voqal.VoqalVadClient;
import dev.voqal.utils.SharedAudioCapture;
import io.github.givimad.libfvadjni.VoiceActivityDetector;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceDetectionSettingsPanel extends JBPanel<VoiceDetectionSettingsPanel>
        implements SharedAudioCapture.AudioDataListener, Disposable {

    private final Project project;
    private final SharedAudioCapture audioCapture;
    private VadProvider vadProvider;
    private Thread readThread;
    private boolean setupMode = true;

    public VoiceDetectionSettingsPanel(Project project, SharedAudioCapture audioCapture) {
        var log = LoggerFactory.getLogger(project, VoiceDetectionSettingsPanel.class);
        this.project = project;
        this.audioCapture = audioCapture;
        initComponents();
        audioCapture.registerListener(this);

        providerComboBox.addActionListener(e -> {
            providerPasswordField.setText("");

            var provider = VoiceDetectionProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
            if (provider.isKeyRequired()) {
                label2.setVisible(true);
                providerPasswordField.setVisible(true);
            } else {
                label2.setVisible(false);
                providerPasswordField.setVisible(false);
            }

            if (provider == VoiceDetectionProvider.Voqal) {
                sensitivitySpinner.setModel(new SpinnerListModel(VoiceActivityDetector.Mode.values()));
                sensitivitySpinner.setValue(VoiceActivityDetector.Mode.values()[0]);
                ((JSpinner.DefaultEditor) sensitivitySpinner.getEditor()).getTextField().setEditable(false);
            } else if (provider == VoiceDetectionProvider.Picovoice) {
                sensitivitySpinner.setModel(new SpinnerNumberModel(20, 0, 100, 1));
                sensitivitySpinner.setValue(20);
                ((JSpinner.DefaultEditor) sensitivitySpinner.getEditor()).getTextField().setEditable(true);
            }

            try {
                updateVadProvider();
            } catch (IOException ex) {
                log.error("Error setting provider", ex);
            }
        });

        sensitivitySpinner.addChangeListener(e -> {
            try {
                updateVadProvider();
            } catch (IOException ex) {
                log.error("Error setting sensitivity", ex);
            }
        });
        voiceSilenceSpinner.addChangeListener(e -> {
            try {
                updateVadProvider();
            } catch (IOException ex) {
                log.error("Error setting voice silence", ex);
            }
        });
        speechSilenceSpinner.addChangeListener(e -> {
            try {
                updateVadProvider();
            } catch (IOException ex) {
                log.error("Error setting speech silence", ex);
            }
        });
        sustainDurationSpinner.addChangeListener(e -> {
            try {
                updateVadProvider();
            } catch (IOException ex) {
                log.error("Error setting sustain duration", ex);
            }
        });
        providerPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try {
                    if (vadProvider != null) {
                        Disposer.dispose(vadProvider);
                        vadProvider = null;
                    }
                    updateVadProvider();
                } catch (IOException ex) {
                    log.error("Error setting provider key", ex);
                }
            }
        });

        restartReadThread();
    }

    @Override
    public boolean isTestListener() {
        return true;
    }

    @Override
    public boolean isLiveDataListener() {
        return true;
    }

    private void restartReadThread() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }

        AtomicBoolean isVoiceDetected = new AtomicBoolean(false);
        AtomicBoolean isSpeechDetected = new AtomicBoolean(false);
        AtomicDouble voiceProbability = new AtomicDouble(0.0);
        readThread = new Thread(() -> {
            var readThreadLocal = readThread;
            while (readThreadLocal != null && !readThreadLocal.isInterrupted()) {
                var vadProvider = this.vadProvider;
                var voiceFlag = vadProvider != null && vadProvider.isVoiceDetected();
                var speechFlag = vadProvider != null && vadProvider.isSpeechDetected();
                var voiceProb = vadProvider != null ? vadProvider.getVoiceProbability() : 0.0;
                boolean hasVoiceProbability = vadProvider instanceof PicovoiceCobraClient;
                if (isVoiceDetected.get() == voiceFlag
                        && isSpeechDetected.get() == speechFlag
                        && voiceProb == voiceProbability.get()) {
                    continue; //no change
                } else if (hasVoiceProbability && Math.abs(voiceProb - voiceProbability.get()) <= 0.1) {
                    continue; //insignificant change
                } else {
                    var sb = new StringBuilder();
                    sb.append("<html>");
                    if (hasVoiceProbability) {
                        var sensitivity = 0;
                        var spinnerValue = sensitivitySpinner.getValue();
                        if (spinnerValue instanceof Number) {
                            sensitivity = ((Number) spinnerValue).intValue();
                        } else if (spinnerValue instanceof VoiceActivityDetector.Mode) {
                            sensitivity = ((VoiceActivityDetector.Mode) spinnerValue).ordinal();
                        }
                        var voiceProbStr = String.format("%04.1f", voiceProb);
                        if (voiceProb >= sensitivity) {
                            sb.append("<font color='#589df6'>Voice Probability: ").append(voiceProbStr).append("</font>");
                        } else {
                            sb.append("<font color='#ff6464'>Voice Probability: ").append(voiceProbStr).append("</font>");
                        }
                        sb.append(" - ");
                    }
                    if (voiceFlag) {
                        sb.append("<font color='#589df6'>Voice Detected: Y</font>");
                    } else {
                        sb.append("<font color='#ff6464'>Voice Detected: N</font>");
                    }
                    sb.append(" - ");
                    if (speechFlag) {
                        sb.append("<font color='#589df6'>Speech Detected: Y</font>");
                    } else {
                        sb.append("<font color='#ff6464'>Speech Detected: N</font>");
                    }
                    sb.append("</html>");
                    try {
                        SwingUtilities.invokeAndWait(() -> label4.setText(sb.toString()));
                    } catch (InterruptedException ignore) {
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    isVoiceDetected.set(voiceFlag);
                    isSpeechDetected.set(speechFlag);
                    voiceProbability.set(voiceProb);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }

            //reset
            var sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<font color='#ff6464'>Voice Detected: N</font>");
            sb.append(" - ");
            sb.append("<font color='#ff6464'>Speech Detected: N</font>");
            sb.append("</html>");
            try {
                SwingUtilities.invokeAndWait(() -> label4.setText(sb.toString()));
            } catch (InterruptedException ignore) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    @Override
    public void onAudioData(byte @NotNull [] data, @NotNull SharedAudioCapture.AudioDetection detection) {
        if (vadProvider != null) {
            vadProvider.onAudioData(data, detection);
        }
    }

    private void updateVadProvider() throws IOException {
        var log = LoggerFactory.getLogger(project, VoiceDetectionSettingsPanel.class);
        if (setupMode) return;
        log.debug("Updating VAD provider");
        var provider = VoiceDetectionProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        if (provider == VoiceDetectionProvider.Voqal) {
            label3.setText("Mode:");
            var sensitivity = ((VoiceActivityDetector.Mode) sensitivitySpinner.getValue()).ordinal();
            var vad = VoqalVadClient.getVad();
            if (vad == null) {
                log.warn("Voqal VAD is not initialized");
                label4.setText("<html><font color='#ff6464'>Error: Voqal VAD is not initialized</font></html>");
            } else {
                vad.setMode(VoiceActivityDetector.Mode.values()[sensitivity]);
            }
            if (vadProvider instanceof VoqalVadClient vadClient) {
                //update
                log.debug("Updating Voqal provider");
                var voiceSilenceTime = ((Number) voiceSilenceSpinner.getValue()).intValue();
                var speechSilenceTime = ((Number) speechSilenceSpinner.getValue()).intValue();
                var sustainDuration = ((Number) sustainDurationSpinner.getValue()).intValue();
                vadClient.setVoiceSilenceThreshold(voiceSilenceTime);
                vadClient.setSpeechSilenceThreshold(speechSilenceTime);
                vadClient.setSustainedDurationMillis(sustainDuration);
                vadClient.setAmnestyPeriodMillis(speechSilenceTime * 2L);
            } else if (vad != null) {
                //create new
                if (vadProvider != null) {
                    Disposer.dispose(vadProvider);
                    vadProvider = null;
                }

                var sb = new StringBuilder();
                sb.append("<html>");
                sb.append("<font color='#ff6464'>Voice Detected: N</font>");
                sb.append(" - ");
                sb.append("<font color='#ff6464'>Speech Detected: N</font>");
                sb.append("</html>");
                label4.setText(sb.toString());
                restartReadThread();

                log.debug("Creating Voqal provider");
                var voiceSilenceTime = ((Number) voiceSilenceSpinner.getValue()).intValue();
                var speechSilenceTime = ((Number) speechSilenceSpinner.getValue()).intValue();
                var sustainDuration = ((Number) sustainDurationSpinner.getValue()).intValue();
                vadProvider = new VoqalVadClient(
                        project,
                        sensitivity,
                        voiceSilenceTime,
                        speechSilenceTime,
                        sustainDuration,
                        speechSilenceTime * 2L,
                        true
                );
            }
        } else if (provider == VoiceDetectionProvider.Picovoice) {
            label3.setText("Probability (%):");
            if (vadProvider instanceof PicovoiceCobraClient picovoice) {
                //update
                log.debug("Updating Picovoice provider");
                var sensitivity = ((Number) sensitivitySpinner.getValue()).intValue();
                picovoice.setVoiceDetectionThreshold(sensitivity);
                var voiceSilenceTime = ((Number) voiceSilenceSpinner.getValue()).intValue();
                var speechSilenceTime = ((Number) speechSilenceSpinner.getValue()).intValue();
                var sustainDuration = ((Number) sustainDurationSpinner.getValue()).intValue();
                picovoice.setVoiceSilenceThreshold(voiceSilenceTime);
                picovoice.setSpeechSilenceThreshold(speechSilenceTime);
                picovoice.setSustainedDurationMillis(sustainDuration);
                picovoice.setAmnestyPeriodMillis(speechSilenceTime * 2L);
                log.debug("Updated Cobra settings: %d,%d,%d,%d,%d,%s".formatted(
                        sensitivity, voiceSilenceTime, speechSilenceTime,
                        sustainDuration, speechSilenceTime * 2L, true)
                );
            } else {
                //create new
                if (vadProvider != null) {
                    Disposer.dispose(vadProvider);
                    vadProvider = null;
                }

                try {
                    var sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("<font color='#ff6464'>Voice Detected: N</font>");
                    sb.append(" - ");
                    sb.append("<font color='#ff6464'>Speech Detected: N</font>");
                    sb.append("</html>");
                    label4.setText(sb.toString());
                    restartReadThread();

                    log.debug("Creating Picovoice provider");
                    var sensitivity = ((Number) sensitivitySpinner.getValue()).intValue();
                    var voiceSilenceTime = ((Number) voiceSilenceSpinner.getValue()).intValue();
                    var speechSilenceTime = ((Number) speechSilenceSpinner.getValue()).intValue();
                    var sustainDuration = ((Number) sustainDurationSpinner.getValue()).intValue();
                    vadProvider = new PicovoiceCobraClient(
                            project,
                            new String(this.providerPasswordField.getPassword()),
                            sensitivity,
                            voiceSilenceTime,
                            speechSilenceTime,
                            sustainDuration,
                            speechSilenceTime * 2L,
                            true
                    );
                } catch (IllegalStateException e) {
                    log.warn("Error creating Picovoice provider. Message: " + e.getMessage());
                    if (e.getMessage().contains("Failed to parse AccessKey")) {
                        label4.setText("<html><font color='#ff6464'>Error: Invalid AccessKey</font></html>");
                    } else {
                        label4.setText("<html><font color='#ff6464'>Error: " + e.getMessage() + "</font></html>");
                    }
                }
            }
        }
    }

    public boolean isModified(VoiceDetectionSettings config) {
        if (!Objects.equals(config.getProvider().name(), providerComboBox.getSelectedItem())) {
            return true;
        }
        if (VoiceDetectionProvider.lenientValueOf(providerComboBox.getSelectedItem().toString()).isKeyRequired()) {
            if (!Objects.equals(config.getProviderKey(), new String(this.providerPasswordField.getPassword()))) {
                return true;
            }
        }
        var sensitivity = 0;
        var spinnerValue = sensitivitySpinner.getValue();
        if (spinnerValue instanceof Number) {
            sensitivity = ((Number) spinnerValue).intValue();
        } else if (spinnerValue instanceof VoiceActivityDetector.Mode) {
            sensitivity = ((VoiceActivityDetector.Mode) spinnerValue).ordinal();
        }
        if (config.getSensitivity() != sensitivity) {
            return true;
        }
        if (config.getSustainDuration() != ((Number) sustainDurationSpinner.getValue()).intValue()) {
            return true;
        }
        if (config.getVoiceSilenceThreshold() != ((Number) voiceSilenceSpinner.getValue()).intValue()) {
            return true;
        }
        if (config.getSpeechSilenceThreshold() != ((Number) speechSilenceSpinner.getValue()).intValue()) {
            return true;
        }
        return false;
    }

    public VoiceDetectionSettings getConfig() {
        VoiceDetectionProvider provider = VoiceDetectionProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        String providerKey = new String(this.providerPasswordField.getPassword());
        if (!provider.isKeyRequired()) { //reset
            providerKey = "";
        }
        var sensitivity = 0;
        var spinnerValue = sensitivitySpinner.getValue();
        if (spinnerValue instanceof Number) {
            sensitivity = ((Number) spinnerValue).intValue();
        } else if (spinnerValue instanceof VoiceActivityDetector.Mode) {
            sensitivity = ((VoiceActivityDetector.Mode) spinnerValue).ordinal();
        }
        return new VoiceDetectionSettings(
                provider,
                providerKey,
                sensitivity,
                ((Number) sustainDurationSpinner.getValue()).intValue(),
                ((Number) voiceSilenceSpinner.getValue()).intValue(),
                ((Number) speechSilenceSpinner.getValue()).intValue()
        );
    }

    public void applyConfig(VoiceDetectionSettings config) {
        var log = LoggerFactory.getLogger(project, VoiceDetectionSettingsPanel.class);
        setupMode = true;
        var sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<font color='#ff6464'>Voice Detected: N</font>");
        sb.append(" - ");
        sb.append("<font color='#ff6464'>Speech Detected: N</font>");
        sb.append("</html>");
        label4.setText(sb.toString());
        providerComboBox.setSelectedItem(config.getProvider().name());
        providerPasswordField.setText(config.getProviderKey());
        voiceSilenceSpinner.setValue(config.getVoiceSilenceThreshold());
        speechSilenceSpinner.setValue(config.getSpeechSilenceThreshold());
        sustainDurationSpinner.setValue(config.getSustainDuration());

        var provider = VoiceDetectionProvider.lenientValueOf(providerComboBox.getSelectedItem().toString());
        if (provider.isKeyRequired()) {
            label2.setVisible(true);
            providerPasswordField.setVisible(true);
        } else {
            label2.setVisible(false);
            providerPasswordField.setVisible(false);
        }

        if (provider == VoiceDetectionProvider.Voqal) {
            sensitivitySpinner.setModel(new SpinnerListModel(VoiceActivityDetector.Mode.values()));
            sensitivitySpinner.setValue(VoiceActivityDetector.Mode.values()[config.getSensitivity()]);
            ((JSpinner.DefaultEditor) sensitivitySpinner.getEditor()).getTextField().setEditable(false);
        } else if (provider == VoiceDetectionProvider.Picovoice) {
            sensitivitySpinner.setModel(new SpinnerNumberModel(20, 0, 100, 1));
            sensitivitySpinner.setValue(config.getSensitivity());
            ((JSpinner.DefaultEditor) sensitivitySpinner.getEditor()).getTextField().setEditable(true);
        }
        setupMode = false;

        try {
            if (provider == VoiceDetectionProvider.Voqal) {
                var vad = VoqalVadClient.getVad();
                if (vad != null) {
                    vad.setMode(VoiceActivityDetector.Mode.values()[config.getSensitivity()]);
                } else {
                    log.warn("Voqal VAD is not initialized");
                    label4.setText("<html><font color='#ff6464'>Error: Voqal VAD is not initialized</font></html>");
                }
            }
            updateVadProvider();
        } catch (IOException e) {
            log.error("Error setting sensitivity", e);
        }
    }

    @Override
    public void dispose() {
        var log = LoggerFactory.getLogger(project, VoiceDetectionSettingsPanel.class);
        log.debug("Disposing VoiceDetectionSettingsPanel");
        audioCapture.removeListener(this);
        readThread.interrupt();
        if (vadProvider != null) {
            Disposer.dispose(vadProvider);
            vadProvider = null;
        }
        log.debug("Disposed VoiceDetectionSettingsPanel");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - unknown
        label1 = new JBLabel();
        providerComboBox = new JComboBox<>();
        label2 = new JBLabel();
        providerPasswordField = new JBPasswordField();
        label3 = new JBLabel();
        sensitivitySpinner = new JSpinner();
        label7 = new JBLabel();
        sustainDurationSpinner = new JSpinner();
        label5 = new JBLabel();
        voiceSilenceSpinner = new JSpinner();
        label6 = new JBLabel();
        speechSilenceSpinner = new JSpinner();
        separator1 = new JSeparator();
        label4 = new JBLabel();
        var vSpacer1 = new Spacer();

        //======== this ========
        setBorder (IdeBorderFactory.createTitledBorder("Voice Detection Settings"));
        setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), 5, -1));

        //---- label1 ----
        label1.setText("Provider:");
        add(label1, new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- providerComboBox ----
        providerComboBox.setModel(new DefaultComboBoxModel<>(VoiceDetectionSettings.VoiceDetectionProvider.getEntries()
                .stream().map(VoiceDetectionSettings.VoiceDetectionProvider::getDisplayName).toArray(String[]::new)));
        add(providerComboBox, new GridConstraints(0, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label2 ----
        label2.setText("Key:");
        add(label2, new GridConstraints(1, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(providerPasswordField, new GridConstraints(1, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label3 ----
        label3.setText("Probability (%):");
        add(label3, new GridConstraints(2, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(sensitivitySpinner, new GridConstraints(2, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label7 ----
        label7.setText("Sustain duration (ms):");
        add(label7, new GridConstraints(3, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- sustainDurationSpinner ----
        sustainDurationSpinner.setModel(new SpinnerNumberModel(100, 0, 500, 1));
        add(sustainDurationSpinner, new GridConstraints(3, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label5 ----
        label5.setText("Voice silence (ms):");
        add(label5, new GridConstraints(4, 0, 1, 2,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- voiceSilenceSpinner ----
        voiceSilenceSpinner.setModel(new SpinnerNumberModel(75, 1, 500, 1));
        add(voiceSilenceSpinner, new GridConstraints(4, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- label6 ----
        label6.setText("Speech silence (ms):");
        add(label6, new GridConstraints(5, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));

        //---- speechSilenceSpinner ----
        speechSilenceSpinner.setModel(new SpinnerNumberModel(2000, 500, null, 1));
        add(speechSilenceSpinner, new GridConstraints(5, 1, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(separator1, new GridConstraints(6, 0, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null, null, null));

        //---- label4 ----
        label4.setText("Voice Detected: N - Speech Detected: N");
        add(label4, new GridConstraints(7, 0, 1, 2,
            GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null));
        add(vSpacer1, new GridConstraints(8, 0, 1, 2,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK,
            GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - unknown
    private JBLabel label1;
    private JComboBox<String> providerComboBox;
    private JBLabel label2;
    private JBPasswordField providerPasswordField;
    private JBLabel label3;
    private JSpinner sensitivitySpinner;
    private JBLabel label7;
    private JSpinner sustainDurationSpinner;
    private JBLabel label5;
    private JSpinner voiceSilenceSpinner;
    private JBLabel label6;
    private JSpinner speechSilenceSpinner;
    private JSeparator separator1;
    private JBLabel label4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
