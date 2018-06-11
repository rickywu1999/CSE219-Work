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
    CLASSIFICATION_SOURCE_PATH,
    CLUSTERING_SOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,
    RUN_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    RUN_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    ERROR_LABEL,
    EMPTY_ERROR_MESSAGE,
    FORMAT_ERROR_MESSAGE,
    PARSEFORMAT_ERROR_MESSAGE,
    DATALABEL_ERROR_MESSAGE,
    FORMAT_REMINDER,
    REMAINING_ERRORS,
    MISSING_DATA_MESSAGE,
    INVALID_DATA_MESSAGE,
    NO_ALGORITHM_ERROR_MESSAGE,
    ALGORITHM_CLASS_EXIST_ERROR_MESSAGE,
    ACCESS_ERROR_MESSAGE,
    INSTANTIATE_ERROR_MESSAGE,
    EXIT_WHILE_RUNNING_TITLE,
    EXIT_WHILE_RUNNING_WARNING,

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
    CLUSTERING_BUTTON_LABEL_DISPLAY,
    CLASSIFICATION_BUTTON_LABEL_DISPLAY,
    CONFIG_BUTTON_LABEL_DISPLAY,
    CONTINUOUS_BUTTON_LABEL_DISPLAY,
    ITERATIONS_LABEL_DISPLAY,
    INTERVALS_LABEL_DISPLAY,
    CLUSTERNUM_LABEL_DISPLAY,
    BACK_BUTTON_LABEL_DISPLAY,
    RUN_BUTTON_LABEL_DISPLAY,
    CLUSTERING_BUTTON_LABEL,
    CLASSIFICATION_BUTTON_LABEL,
    TITLE_LABEL,
    ALGORITHM_TYPE_LABEL,
    ALGORITHM_CHOOSE_LABEL,
    READ_ONLY_LABEL,
    PICTURE_FILE_EXT_DESC,
    PICTURE_FILE_EXT,
    INSTANCE_NUMBER_TEXT,
    LABEL_NUMBER_TEXT,
    LABEL_LIST_TEXT,
    NO_FILE_TEXT,
    OK,
    CLASSIFICATION_INSTANCE_1,
    CLASSIFICATION_INSTANCE_2,
    CLASSIFICATION_LABEL_1,
    CLASSIFICATION_LABEL_2,
    CLASSIFICATION_CSS_LOOKUP,
    CLASSIFICATION_CSS_STYLE,
    
    /* symbols */
    COLON,
    AT_SIGN,
    COMMA,
    
    /* CSS Stuff */
    TEXT_AREA_CSS,
    
    /* Algorithm Stuff*/
    CLUSTERING_ALGORITHM_NAMES,
    CLASSIFICATION_ALGORITHM_NAMES
}
