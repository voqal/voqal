package example

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JSpinner

class MakeAllTheseFieldsPrivate {
    var myWholePanel: JPanel? = null
    var myGlobalSettingsPanel: JPanel? = null
    var autoResolveEndpointNamesCheckBox: JCheckBox? = null
    var myServiceSettingsPanel: JPanel? = null
    var serviceHostTextField: JBTextField? = null
    var authorizationCodeTextField: JBTextField? = null
    var testPanel: JPanel? = null
    var serviceComboBox: ComboBox<String>? = null
    var verifyHostCheckBox: JBCheckBox? = null
    var verifyHostLabel: JBLabel? = null
    var hostLabel: JBLabel? = null
    var authorizationCodeLabel: JBLabel? = null
    var certificatePinsLabel: JBLabel? = null
    var serviceLabel: JBLabel? = null
    var myPortalSettingsPanel: JPanel? = null
    var portalZoomSpinner: JSpinner? = null
}