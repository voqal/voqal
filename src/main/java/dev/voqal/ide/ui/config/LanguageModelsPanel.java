package dev.voqal.ide.ui.config;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ConcurrentFactoryMap;
import dev.voqal.config.settings.LanguageModelSettings;
import dev.voqal.config.settings.LanguageModelsSettings;
import kotlin.text.Regex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.*;

public class LanguageModelsPanel {

  private static final String EMPTY_PANEL = "empty.panel";

  private final JBList<LanguageModelSettings> myLanguageModelsList;
  private final List<LanguageModelSettings> myLanguageModels = new ArrayList<>();
  private final List<LanguageModelSettingsPanel> myEditors = new ArrayList<>();
  private final Project myProject;

  private int count;
  private final Map<LanguageModelSettings, String> myRepoNames = ConcurrentFactoryMap.createMap(settings -> Integer.toString(count++));

  public LanguageModelsPanel(final Project project) {
    initComponents();
    myProject = project;

    myLanguageModelsList = new JBList<>(new CollectionListModel<>());
    myLanguageModelsList.getEmptyText().setText("No language models configured");

    myLanguageModelsLabel.setLabelFor(myLanguageModelsList);

    myLanguageModelsPanel.setMinimumSize(new Dimension(-1, 100));

    final List<AnAction> createActions = new ArrayList<>();
    for (final LanguageModelSettings.LMProvider provider : LanguageModelSettings.LMProvider.getEntries()) {
      createActions.add(new AddLanguageModelAction(provider) {
        @Override
        protected LanguageModelSettings getSettings() {
          var defaultSettings = LanguageModelSettings.asDefault(provider);
            if (hasExistingName(defaultSettings.getName())) {
                return LanguageModelSettings.duplicate(defaultSettings, getUniqueName(defaultSettings));
            } else {
                return defaultSettings;
            }
        }
      });
    }

    ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(myLanguageModelsList)
      .disableUpDownActions()
      .setAddIcon(LayeredIcon.ADD_WITH_DROPDOWN);

    toolbarDecorator.addExtraAction(new AnActionButton("Duplicate", AllIcons.General.InlineCopy) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            LanguageModelSettings settings = getSelectedSettings();
            if (settings != null) {
                var nextName = getUniqueName(settings);
                addSettings(LanguageModelSettings.duplicate(settings, nextName));
            }
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.BGT;
        }
    });

    toolbarDecorator.setAddAction(anActionButton -> {
        DefaultActionGroup group = new DefaultActionGroup();
        for (AnAction aMyAdditional : createActions) {
            group.add(aMyAdditional);
        }
        Set<LanguageModelSettings> languageModelsSettings = new HashSet<>();
        myLanguageModels.forEach(languageModelsSettings::remove);
        if (!languageModelsSettings.isEmpty()) {
            group.add(Separator.getInstance());
            for (final LanguageModelSettings settings : languageModelsSettings) {
                group.add(new AddLanguageModelAction(settings) {
                    @Override
                    protected LanguageModelSettings getSettings() {
                        return settings;
                    }
                });
            }
        }

        JBPopupFactory.getInstance()
                .createActionGroupPopup("Add Language Model", group, DataManager.getInstance().getDataContext(anActionButton.getContextComponent()),
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true).show(
                        anActionButton.getPreferredPopupPoint());
    });

    toolbarDecorator.setRemoveAction(anActionButton -> {
        LanguageModelSettings settings = getSelectedSettings();
        if (settings != null) {
            CollectionListModel<LanguageModelSettings> model = getListModel();
            model.remove(settings);
            myLanguageModels.remove(settings);
            myEditors.removeIf(e -> e.getConfig().equals(settings));

            if (model.getSize() > 0) {
                myLanguageModelsList.setSelectedValue(model.getElementAt(0), true);
            } else {
                myRepositoryEditor.removeAll();
                myRepositoryEditor.repaint();
            }
        }
    });

    myLanguageModelsPanel.add(toolbarDecorator.createPanel(), BorderLayout.CENTER);

    myLanguageModelsList.getSelectionModel().addListSelectionListener(e -> {
      LanguageModelSettings settings = getSelectedSettings();
      if (settings != null) {
        String name = myRepoNames.get(settings);
        assert name != null;
        ((CardLayout)myRepositoryEditor.getLayout()).show(myRepositoryEditor, name);
        mySplitter.doLayout();
        mySplitter.repaint();
      }
    });

    myLanguageModelsList.setCellRenderer(SimpleListCellRenderer.create((label, value, index) -> {
      label.setIcon(value.getProvider().getIcon());
      label.setText(value.getName());
    }));
  }

  private CollectionListModel<LanguageModelSettings> getListModel() {
    return (CollectionListModel<LanguageModelSettings>) myLanguageModelsList.getModel();
  }

  private void addSettings(LanguageModelSettings settings) {
    myLanguageModels.add(settings);
    getListModel().add(settings);
    addSettingsPanel(settings);
    myLanguageModelsList.setSelectedIndex(myLanguageModelsList.getModel().getSize() - 1);
  }

  private void addSettingsPanel(LanguageModelSettings settings) {
    LanguageModelSettingsPanel editor = new LanguageModelSettingsPanel(myProject, settings);
    myEditors.add(editor);
    editor.applyConfig(settings);
    String name = myRepoNames.get(settings);
    myRepositoryEditor.add(editor, name);
    myRepositoryEditor.doLayout();
  }

  @Nullable
  private LanguageModelSettings getSelectedSettings() {
    return myLanguageModelsList.getSelectedValue();
  }

  public JComponent createComponent() {
    return myPanel;
  }

  public boolean isModified(LanguageModelsSettings settings) {
    if (myEditors.size() != settings.getModels().size()) {
      return true;
    }
    return !myEditors.isEmpty() && myEditors.stream().anyMatch(LanguageModelSettingsPanel::isModified);
  }

  public void reset(LanguageModelsSettings settings) {
    myEditors.clear();//todo: dispose?
    myRepoNames.clear();
    myRepositoryEditor.removeAll();
    myRepositoryEditor.add(myEmptyPanel, EMPTY_PANEL);
    myLanguageModels.clear();

    CollectionListModel<LanguageModelSettings> listModel = new CollectionListModel<>(new ArrayList<>());
    for (LanguageModelSettings repository : settings.getModels()) {
      myLanguageModels.add(repository);
      listModel.add(repository);
    }

    myLanguageModelsList.setModel(listModel);

    for (LanguageModelSettings clone : myLanguageModels) {
      addSettingsPanel(clone);
    }

    if (!myLanguageModels.isEmpty()) {
      myLanguageModelsList.setSelectedValue(myLanguageModels.get(0), true);
    }
  }

    private @NotNull String getUniqueName(LanguageModelSettings settings) {
        var originalName = settings.getName();
        var index = 1;
        var regex = new Regex("(.*)-(\\d+)");
        var match = regex.matchEntire(originalName);
        if (match != null) {
            originalName = match.getGroupValues().get(1);
            index = Integer.parseInt(match.getGroupValues().get(2));
        }
        var nextName = originalName + "-" + index;
        while (hasExistingName(nextName)) {
            nextName = originalName + "-" + ++index;
        }
        return nextName;
    }

  private boolean hasExistingName(String name) {
    return myLanguageModels.stream().anyMatch(s -> s.getName().equals(name));
  }

  public LanguageModelsSettings getConfig() {
    return new LanguageModelsSettings(Arrays.asList(
            myEditors.stream().map(LanguageModelSettingsPanel::getConfig).toArray(LanguageModelSettings[]::new)
    ));
  }

  public void applyConfig(LanguageModelsSettings settings) {
    reset(settings);
  }

  private abstract class AddLanguageModelAction extends IconWithTextAction implements DumbAware {

    AddLanguageModelAction(LanguageModelSettings.LMProvider provider) {
      super(provider.getDisplayName(), provider.getDisplayName(), provider.getIcon());
    }

    AddLanguageModelAction(LanguageModelSettings settings) {
      this(settings.getProvider());
    }

    protected abstract LanguageModelSettings getSettings();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      addSettings(getSettings());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
      return ActionUpdateThread.BGT;
    }
  }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - unknown
        myPanel = new JPanel();
        myLanguageModelsLabel = new JBLabel();
        myLanguageModelsPanel = new JPanel();
        myRepositoryEditor = new JPanel();
        myEmptyPanel = new JPanel();
        var bLabel1 = new JBLabel();
        mySplitter = new Splitter();

        //======== myPanel ========
        {
            myPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));

            //---- myLanguageModelsLabel ----
            myLanguageModelsLabel.setText("Configured language models:");
            myPanel.add(myLanguageModelsLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //======== myLanguageModelsPanel ========
            {
                myLanguageModelsPanel.setLayout(new BorderLayout());
            }
            myPanel.add(myLanguageModelsPanel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, new Dimension(0, 50), null));

            //======== myRepositoryEditor ========
            {
                myRepositoryEditor.setLayout(new CardLayout());

                //======== myEmptyPanel ========
                {
                    myEmptyPanel.setBorder(new TitledBorder(new EmptyBorder(100, 220, 100, 220), ""));
                    myEmptyPanel.setLayout(new BorderLayout());

                    //---- bLabel1 ----
                    bLabel1.setHorizontalAlignment(SwingConstants.CENTER);
                    bLabel1.setText("No language model selected");
                    myEmptyPanel.add(bLabel1, BorderLayout.CENTER);
                }
                myRepositoryEditor.add(myEmptyPanel, "Card1");
            }
            myPanel.add(myRepositoryEditor, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));

            //---- mySplitter ----
            mySplitter.setFirstComponent(myLanguageModelsPanel);
            mySplitter.setOrientation(true);
            mySplitter.setSecondComponent(myRepositoryEditor);
            myPanel.add(mySplitter, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel myPanel;
    private JBLabel myLanguageModelsLabel;
    private JPanel myLanguageModelsPanel;
    private JPanel myRepositoryEditor;
    private JPanel myEmptyPanel;
    private Splitter mySplitter;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
