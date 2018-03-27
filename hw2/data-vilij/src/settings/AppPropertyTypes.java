package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    GUI_RESOURCE_PATH,
    ICONS_RESOURCE_PATH,
    CSS_STYLE_SHEET_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    ERROR_LABEL,
    FORMAT_ERROR_MESSAGE,
    PARSEFORMAT_ERROR_MESSAGE,
    DATALABEL_ERROR_MESSAGE,
    FORMAT_REMINDER,
    REMAINING_ERRORS,
    MISSING_DATA_MESSAGE,
    INVALID_DATA_MESSAGE,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,
    LONG_LOAD_FILE_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,
    LONG_LOAD_FILE,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    BUTTON_LABEL_DISPLAY,
    TITLE_LABEL,
    READ_ONLY_LABEL,
    PICTURE_FILE_EXT_DESC,
    PICTURE_FILE_EXT,
    
    /* symbols */
    COLON,
    AT_SIGN,
    COMMA,
    
    /* CSS Stuff */
    TEXT_AREA_CSS
}
