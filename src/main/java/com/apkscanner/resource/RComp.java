package com.apkscanner.resource;

import com.apkspectrum.resource.DefaultResComp;
import com.apkspectrum.resource.ResComp;
import com.apkspectrum.resource.ResImage;
import com.apkspectrum.resource.ResString;

public enum RComp implements ResComp<Object> {
    BTN_TOOLBAR_OPEN                (RStr.BTN_OPEN, RImg.TOOLBAR_OPEN, RStr.BTN_OPEN_LAB),
    BTN_TOOLBAR_OPEN_PACKAGE        (RStr.BTN_OPEN_PACKAGE, RImg.TOOLBAR_PACKAGETREE, RStr.BTN_OPEN_PACKAGE_LAB),
    BTN_TOOLBAR_MANIFEST            (RStr.BTN_MANIFEST, RImg.TOOLBAR_MANIFEST, RStr.BTN_MANIFEST_LAB),
    BTN_TOOLBAR_EXPLORER            (RStr.BTN_EXPLORER, RImg.TOOLBAR_EXPLORER, RStr.BTN_EXPLORER_LAB),
    BTN_TOOLBAR_OPEN_CODE           (RStr.BTN_OPENCODE, RImg.TOOLBAR_OPENCODE, RStr.BTN_OPENCODE_LAB),
    BTN_TOOLBAR_OPEN_CODE_LODING    (RStr.BTN_OPENING_CODE, RImg.TOOLBAR_LOADING_OPEN_JD, RStr.BTN_OPENING_CODE_LAB),
    BTN_TOOLBAR_SEARCH              (RStr.BTN_SEARCH, RImg.TOOLBAR_SEARCH, RStr.BTN_SEARCH_LAB),
    BTN_TOOLBAR_PLUGIN_EXTEND       (RStr.BTN_MORE, RImg.TOOLBAR_OPEN_ARROW, RStr.BTN_MORE_LAB),
    BTN_TOOLBAR_INSTALL             (RStr.BTN_INSTALL, RImg.TOOLBAR_INSTALL, RStr.BTN_INSTALL_LAB),
    BTN_TOOLBAR_INSTALL_UPDATE      (RStr.BTN_INSTALL_UPDATE, RImg.TOOLBAR_INSTALL, RStr.BTN_INSTALL_UPDATE_LAB),
    BTN_TOOLBAR_INSTALL_DOWNGRADE   (RStr.BTN_INSTALL_DOWNGRAD, RImg.TOOLBAR_INSTALL, RStr.BTN_INSTALL_DOWNGRAD_LAB),
    BTN_TOOLBAR_LAUNCH              (RStr.BTN_LAUNCH, RImg.TOOLBAR_LAUNCH, RStr.BTN_LAUNCH_LAB),
    BTN_TOOLBAR_SIGN                (RStr.BTN_SIGN, RImg.TOOLBAR_SIGNNING, RStr.BTN_SIGN_LAB),
    BTN_TOOLBAR_INSTALL_EXTEND      (RStr.BTN_MORE, RImg.TOOLBAR_OPEN_ARROW, RStr.BTN_MORE_LAB),
    BTN_TOOLBAR_SETTING             (RStr.BTN_SETTING, RImg.TOOLBAR_SETTING, RStr.BTN_SETTING_LAB),
    BTN_TOOLBAR_ABOUT               (RStr.BTN_ABOUT, RImg.TOOLBAR_ABOUT, RStr.BTN_ABOUT_LAB),

    BTN_OPEN_WITH_SYSTEM_SET        (RStr.LABEL_OPEN_WITH_SYSTEM, RImg.RESOURCE_TREE_OPEN_ICON, RStr.LABEL_OPEN_WITH_SYSTEM),
    BTN_OPEN_WITH_JD_GUI            (RStr.LABEL_OPEN_WITH_JDGUI, RImg.RESOURCE_TREE_JD_ICON, RStr.LABEL_OPEN_WITH_JDGUI),
    BTN_OPEN_WITH_JADX_GUI          (RStr.LABEL_OPEN_WITH_JADXGUI, RImg.RESOURCE_TREE_JADX_ICON, RStr.LABEL_OPEN_WITH_JADXGUI),
    BTN_OPEN_WITH_BYTECODE_VIEWER   (RStr.LABEL_OPEN_WITH_BYTECODE, RImg.RESOURCE_TREE_BCV_ICON, RStr.LABEL_OPEN_WITH_BYTECODE),
    BTN_OPEN_WITH_APK_SCANNER       (RStr.LABEL_OPEN_WITH_SCANNER, RImg.APP_ICON, RStr.LABEL_OPEN_WITH_SCANNER),
    BTN_OPEN_WITH_EXPLORER          (RStr.LABEL_OPEN_WITH_EXPLORER, RImg.TOOLBAR_EXPLORER, RStr.LABEL_OPEN_WITH_EXPLORER),
    BTN_OPEN_WITH_TEXTVIEWER        (RStr.LABEL_OPEN_TO_TEXTVIEWER, RImg.RESOURCE_TREE_OPEN_TO_TEXT, RStr.LABEL_OPEN_TO_TEXTVIEWER),
    BTN_OPEN_WITH_CHOOSER           (RStr.LABEL_OPEN_WITH_CHOOSE, RImg.RESOURCE_TREE_OPEN_OTHERAPPLICATION_ICON, RStr.LABEL_OPEN_WITH_CHOOSE),
    BTN_OPEN_WITH_LOADING           (RStr.BTN_OPENING_CODE, RImg.RESOURCE_TREE_OPEN_JD_LOADING, RStr.BTN_OPENING_CODE),

    MENU_TOOLBAR_NEW_WINDOW         (RStr.MENU_NEW),
    MENU_TOOLBAR_NEW_EMPTY          (RStr.MENU_NEW_WINDOW, RImg.TOOLBAR_MANIFEST.getImageIcon(16,16)),
    MENU_TOOLBAR_NEW_APK            (RStr.MENU_NEW_APK_FILE, RImg.TOOLBAR_OPEN.getImageIcon(16,16)),
    MENU_TOOLBAR_NEW_PACKAGE        (RStr.MENU_NEW_PACKAGE, RImg.TOOLBAR_PACKAGETREE.getImageIcon(16,16)),
    MENU_TOOLBAR_OPEN_APK           (RStr.MENU_APK_FILE, RImg.TOOLBAR_OPEN.getImageIcon(16,16)),
    MENU_TOOLBAR_OPEN_PACKAGE       (RStr.MENU_PACKAGE, RImg.TOOLBAR_PACKAGETREE.getImageIcon(16,16)),
    MENU_TOOLBAR_INSTALL_APK        (RStr.MENU_INSTALL, RImg.TOOLBAR_INSTALL.getImageIcon(16,16)),
    MENU_TOOLBAR_UNINSTALL_APK      (RStr.MENU_UNINSTALL, RImg.TOOLBAR_UNINSTALL.getImageIcon(16,16)),
    MENU_TOOLBAR_CLEAR_DATA         (RStr.MENU_CLEAR_DATA, RImg.TOOLBAR_CLEAR.getImageIcon(16,16)),
    MENU_TOOLBAR_INSTALLED_CHECK    (RStr.MENU_CHECK_INSTALLED, RImg.TOOLBAR_PACKAGETREE.getImageIcon(16,16)),
    MENU_TOOLBAR_DECODER_JD_GUI     (RStr.MENU_DECODER_JD_GUI),
    MENU_TOOLBAR_DECODER_JADX_GUI   (RStr.MENU_DECODER_JADX_GUI),
    MENU_TOOLBAR_DECODER_BYTECODE   (RStr.MENU_DECODER_BYTECODE),
    MENU_TOOLBAR_SEARCH_RESOURCE    (RStr.MENU_SEARCH_RESOURCE),
    MENU_TOOLBAR_EXPLORER_ARCHIVE   (RStr.MENU_EXPLORER_ARCHIVE),
    MENU_TOOLBAR_EXPLORER_FOLDER    (RStr.MENU_EXPLORER_FOLDER),
    MENU_TOOLBAR_LAUNCH_LAUNCHER    (RStr.MENU_LAUNCH_LAUNCHER),
    MENU_TOOLBAR_LAUNCH_SELECT      (RStr.MENU_LAUNCH_SELECT),
    MENU_TOOLBAR_SELECT_DEFAULT     (RStr.MENU_SELECT_DEFAULT),
    MENU_TOOLBAR_SEARCH_BY_PACKAGE  (RStr.LABEL_BY_PACKAGE_NAME),
    MENU_TOOLBAR_SEARCH_BY_NAME     (RStr.LABEL_BY_APP_LABEL),
    MENU_TOOLBAR_TO_BASIC_INFO      (RStr.MENU_VISIBLE_TO_BASIC_EACH),

    TABBED_BASIC_INFO               (RStr.TAB_BASIC_INFO, RStr.TAB_BASIC_INFO),
    TABBED_APEX_INFO                (RStr.TAB_APEX_INFO, RStr.TAB_APEX_INFO),
    TABBED_WIDGET                   (RStr.TAB_WIDGETS, RStr.TAB_WIDGETS),
    TABBED_LIBRARIES                (RStr.TAB_LIBRARIES, RStr.TAB_LIBRARIES),
    TABBED_RESOURCES                (RStr.TAB_RESOURCES, RStr.TAB_RESOURCES),
    TABBED_COMPONENTS               (RStr.TAB_COMPONENTS, RStr.TAB_COMPONENTS),
    TABBED_SIGNATURES               (RStr.TAB_SIGNATURES, RStr.TAB_SIGNATURES),

    LABEL_XML_CONSTRUCTION          (RStr.COMPONENT_LABEL_XML),
    COMPONENT_FILTER_PROMPT_XML     (RStr.COMPONENT_FILTER_PROMPT_XML),
    COMPONENT_FILTER_PROMPT_NAME    (RStr.COMPONENT_FILTER_PROMPT_NAME),
    ; // ENUM END

    DefaultResComp res;

    private RComp(ResString<?> text) {
        res = new DefaultResComp(text);
    }

    private RComp(ResString<?> text, ResString<?> toolTipText) {
        res = new DefaultResComp(text, toolTipText);
    }

    private RComp(ResString<?> text, ResImage<?> image) {
        res = new DefaultResComp(text, image);
    }

    private RComp(ResString<?> text, javax.swing.Icon icon) {
        res = new DefaultResComp(text, icon);
    }

    private RComp(ResString<?> text, ResImage<?> image, ResString<?> toolTipText) {
        res = new DefaultResComp(text, image, toolTipText);
    }

    private RComp(ResString<?> text, javax.swing.Icon icon, ResString<?> toolTipText) {
        res = new DefaultResComp(text, icon, toolTipText);
    }

    @Override
    public ResComp<?> get() {
        return res.get();
    }

    @Override
    public String getText() {
        return res.getText();
    }

    @Override
    public javax.swing.Icon getIcon() {
        return res.getIcon();
    }

    @Override
    public String getToolTipText() {
        return res.getToolTipText();
    }

    @Override
    public void setIconSize(java.awt.Dimension iconSize) {
        res.setIconSize(iconSize);
    }

    @Override
    public void set(java.awt.Component c) {
        res.set(c);
    }
}
