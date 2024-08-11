package dev.voqal.ide.ui.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import dev.voqal.config.settings.PluginSettings;
import dev.voqal.ide.ui.WaveformVisualizer;
import dev.voqal.utils.SharedAudioCapture;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.util.Objects;

public class PluginSettingsPanel extends JBPanel<PluginSettingsPanel> {

    public PluginSettingsPanel(Project project, SharedAudioCapture audioCapture) {
        initComponents();

        comboBox1.setModel(new DefaultComboBoxModel<>(
                audioCapture.getAvailableMicrophoneNames().toArray(new String[0]))
        );
        comboBox1.addActionListener(e -> {
            audioCapture.setMicrophone(project, comboBox1.getSelectedItem().toString());
        });

        WaveformVisualizer visualizer = new WaveformVisualizer(audioCapture);
        visualizer.start();
        panel1.add(visualizer);
    }

    public boolean isModified(PluginSettings config) {
        if (!Objects.equals(config.getEnabled(), this.enabledCheckBox.isSelected())) {
            return true;
        }
        //microphone name not checked as it's applied on change
        if (!Objects.equals(config.getPauseOnFocusLost(), this.focusLostPauseCheckBox.isSelected())) {
            return true;
        }
        return false;
    }

    public PluginSettings getConfig() {
        return new PluginSettings(
                enabledCheckBox.isSelected(),
                comboBox1.getSelectedItem().toString(),
                focusLostPauseCheckBox.isSelected()
        );
    }

    public void applyConfig(PluginSettings config) {
        enabledCheckBox.setSelected(config.getEnabled());
        comboBox1.setSelectedItem(config.getMicrophoneName());
        focusLostPauseCheckBox.setSelected(config.getPauseOnFocusLost());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - unknown
        enabledCheckBox = new JBCheckBox();
        label1 = new JBLabel();
        comboBox1 = new JComboBox<>();
        focusLostPauseCheckBox = new JCheckBox();
        panel1 = new JBPanel<>();

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
            "[]"));

        //---- enabledCheckBox ----
        enabledCheckBox.setText("Enabled");
        add(enabledCheckBox, "cell 0 0");

        //---- label1 ----
        label1.setText("Microphone");
        add(label1, "cell 0 1");
        add(comboBox1, "cell 1 1 2 1");

        //---- focusLostPauseCheckBox ----
        focusLostPauseCheckBox.setText("Pause on IDE focus lost");
        focusLostPauseCheckBox.setSelected(true);
        add(focusLostPauseCheckBox, "cell 0 2 3 1");

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder(new EtchedBorder(), "Signal Preview"));
            panel1.setLayout(new MigLayout(
                "hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[]"));
        }
        add(panel1, "cell 0 3 3 1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - unknown
    private JBCheckBox enabledCheckBox;
    private JBLabel label1;
    private JComboBox comboBox1;
    private JCheckBox focusLostPauseCheckBox;
    private JBPanel panel1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
