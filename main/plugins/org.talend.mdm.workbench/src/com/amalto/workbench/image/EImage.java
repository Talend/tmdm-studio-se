// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.image;

import java.util.HashMap;
import java.util.Map;

/**
 * Using enum to list all image resources in workbench
 *
 * @author liyanmei
 *
 */
public enum EImage {
    DEFAULT("icons/appli_16x16.gif"), //$NON-NLS-1$

    ACTIVITY_CATEGORY("icons/activity_category.gif"), //$NON-NLS-1$
    ACTIVITY("icons/activity.gif"), //$NON-NLS-1$
    ADD_NEWXPATH("icons/add_newXPath.png"), //$NON-NLS-1$
    SELECT_NEWXPATH("icons/select_newXPath.png"), //$NON-NLS-1$
    ADD_OBJ("icons/add_obj.gif"), //$NON-NLS-1$
    ADDMULTI_OBJ("icons/add.gif"), //$NON-NLS-1$
    ADDTSK_TSK("icons/addtsk_tsk.gif"), //$NON-NLS-1$
    ANNOTATION("icons/annotation.png"), //$NON-NLS-1$
    APPINFO("icons/appinfo.gif"), //$NON-NLS-1$
    APPLET("icons/applet.gif"), //$NON-NLS-1$
    ATTRIBUTE_GROUP("icons/attribute_group.gif"), //$NON-NLS-1$
    ATTRIBUTE_MANDATORY("icons/attribute_mandatory.gif"), //$NON-NLS-1$
    ATTRIBUTE("icons/attribute.gif"), //$NON-NLS-1$
    BACKWARD_NAV("icons/backward_nav.gif"), //$NON-NLS-1$
    BKMRK_NAV("icons/bkmrk_nav.gif"), //$NON-NLS-1$
    BKMRK_TSK("icons/bkmrk_tsk.gif"), //$NON-NLS-1$
    BLANK("icons/blank.gif"), //$NON-NLS-1$
    SYNCH("icons/synch.gif"), //$NON-NLS-1$
    CATCHUP_RLS("icons/catchup_rls.gif"), //$NON-NLS-1$
    CATCHUPRELEASE_RLS("icons/catchuprelease_rls.gif"), //$NON-NLS-1$
    CHANGE_OBJ("icons/change_obj.gif"), //$NON-NLS-1$
    CHANGE_TO_COMPLEX("icons/change_to_complex.gif"), //$NON-NLS-1$
    CHANGE_TO_SIMPLE("icons/change_to_simple.gif"), //$NON-NLS-1$
    CHANGESET_OBJ("icons/changeset_obj.gif"), //$NON-NLS-1$
    CHECKEDOUT_OV("icons/checkedout_ov.gif"), //$NON-NLS-1$
    CHECKIN_ACTION("icons/checkin_action.gif"), //$NON-NLS-1$
    CHECKOUT_ACTION("icons/checkout_action.gif"), //$NON-NLS-1$
    CLEAR_CO("icons/clear_co.gif"), //$NON-NLS-1$
    CLOSE_VIEW("icons/close_view.gif"), //$NON-NLS-1$
    CLOSELOGFILEACTION("icons/closelogfileaction.gif"), //$NON-NLS-1$
    COlLAPSEALL("icons/collapseall.gif"), //$NON-NLS-1$
    COMPLETE_TSK("icons/complete_tsk.gif"), //$NON-NLS-1$
    COMPLEX_ALL("icons/complex_all.png"), //$NON-NLS-1$
    COMPLEX_CHOICE("icons/complex_choice.png"), //$NON-NLS-1$
    COMPLEX_SEQUENCE("icons/complex_sequence.png"), //$NON-NLS-1$
    COMPRESSED_FOLDER_OBJ("icons/compressed_folder_obj.gif"), //$NON-NLS-1$
    CONCEPT("icons/concept.png"), //$NON-NLS-1$
    CONFCHG_OV("icons/confchg_ov.gif"), //$NON-NLS-1$
    CONFIGURATIONACTION("icons/configurationaction.gif"), //$NON-NLS-1$
    CONFLICT_SYNCH("icons/conflict_synch.gif"), //$NON-NLS-1$
    CONSOLE_VIEW("icons/console_view.gif"), //$NON-NLS-1$
    COPY_EDIT("icons/copy_edit.gif"), //$NON-NLS-1$
    COPY("icons/copy.gif"), //$NON-NLS-1$
    CUSTOM_FORM("icons/customform.png"), //$NON-NLS-1$
    CUSTOM_FORM_WHITE("icons/customform_white.png"), //$NON-NLS-1$
    CUT_EDIT("icons/cut_edit.gif"), //$NON-NLS-1$
    DATA_CLUSTER("icons/datacluster.png"), //$NON-NLS-1$
    DATA_CLUSTER_WHITE("icons/datacluster_white.png"), //$NON-NLS-1$
    DATA_MODEL("icons/datamodel.png"), //$NON-NLS-1$
    DATA_MODEL_WHITE("icons/datamodel_white.png"), //$NON-NLS-1$
    DEFAULT_PERSP("icons/default_persp.gif"), //$NON-NLS-1$
    DEFAULTS_PS("icons/defaults_ps.gif"), //$NON-NLS-1$
    DEFAULTVIEW_MISC("icons/defaultview_misc.gif"), //$NON-NLS-1$
    DELETE_EDIT("icons/delete_edit.gif"), //$NON-NLS-1$
    DELETE_OBJ("icons/delete_obj.gif"), //$NON-NLS-1$
    DIRTY_OV("icons/dirty_ov.gif"), //$NON-NLS-1$
    DOCUMENTATION("icons/documentation.gif"), //$NON-NLS-1$
    DOCUMENTS("icons/documents.gif"), //$NON-NLS-1$
    DUPLICATE("icons/duplicate.png"), //$NON-NLS-1$
    EDIT_OBJ("icons/edit_obj.gif"), //$NON-NLS-1$
    EDIT_TASKID("icons/default.gif"), //$NON-NLS-1$
    EDIT("icons/edit.gif"), //$NON-NLS-1$
    ELEMENT_ONLY("icons/element_only.gif"), //$NON-NLS-1$
    ELEMENT_ONLY_SKIP("icons/element_only_skip.png"), //$NON-NLS-1$
    ELEMENTS_ONLE_ADD("icons/elements_obj_+.gif"), //$NON-NLS-1$
    ELEMENTS_OBJ_CHOICE("icons/elements_obj_choice.gif"), //$NON-NLS-1$
    ELEMENTS_OBJ_SEQUENCE("icons/elements_obj_sequence.gif"), //$NON-NLS-1$
    ELEMENTS_OBJ("icons/elements_obj.gif"), //$NON-NLS-1$
    ERROR_CO("icons/error_co.gif"), //$NON-NLS-1$
    ERROR_TSK("icons/error_tsk.gif"), //$NON-NLS-1$
    ERROR("icons/error.gif"), //$NON-NLS-1$
    ERRORSTATE("icons/errorstate.gif"), //$NON-NLS-1$
    EXECUTE("icons/execute.gif"), //$NON-NLS-1$
    EXPORT("icons/export.gif"), //$NON-NLS-1$
    EXPORT_PROJECTSET("icons/export_projectset.gif"), //$NON-NLS-1$
    EXPORT_WIZ("icons/export_wiz.gif"), //$NON-NLS-1$
    EXPORTDIR_WIZ("icons/exportdir_wiz.gif"), //$NON-NLS-1$
    EXPORTPREF_WIZ("icons/exportpref_wiz.gif"), //$NON-NLS-1$
    EXPORTZIP_WIZ("icons/exportzip_wiz.gif"), //$NON-NLS-1$
    FACET("icons/facet.gif"), //$NON-NLS-1$
    FIELD("icons/field.gif"), //$NON-NLS-1$
    FILE_OBJ("icons/file_obj.gif"), //$NON-NLS-1$
    FILENAV_NAV("icons/filenav_nav.gif"), //$NON-NLS-1$
    FILTER_CHANGE("icons/filter_change.gif"), //$NON-NLS-1$
    FILTER_HISTORY("icons/filter_history.gif"), //$NON-NLS-1$
    FILTER_PS("icons/filter_ps.gif"), //$NON-NLS-1$
    FLATLAYOUT("icons/flatLayout.gif"), //$NON-NLS-1$
    FLDR_OBJ("icons/fldr_obj.gif"), //$NON-NLS-1$
    FK_OBJ("icons/fk.png"), //$NON-NLS-1$
    FONT("icons/font.gif"), //$NON-NLS-1$
    FORWARD_NAV("icons/forward_nav.gif"), //$NON-NLS-1$
    FORDER("icons/folder.gif"), //$NON-NLS-1$
    FORDER_OPEN("icons/folder_open.gif"), //$NON-NLS-1$
    GOTOOBJ_TSK("icons/gotoobj_tsk.gif"), //$NON-NLS-1$
    HEADER_COMPLETE("icons/header_complete.gif"), //$NON-NLS-1$
    HEADER_PRIORITY("icons/header_priority.gif"), //$NON-NLS-1$
    HELP_CONTENTS("icons/help_contents.gif"), //$NON-NLS-1$
    HIDEABLE("icons/hideable.gif"), //$NON-NLS-1$
    HIERARCHICALLayout("icons/hierarchicalLayout.gif"), //$NON-NLS-1$
    HOME_NAV("icons/home_nav.gif"), //$NON-NLS-1$
    HPRIO_TSK("icons/hprio_tsk.gif"), //$NON-NLS-1$
    IGNOREFILES("icons/ignorefiles.gif"), //$NON-NLS-1$
    IGNOREWS_EDIT("icons/ignorews_edit.gif"), //$NON-NLS-1$
    IMPORT("icons/import.gif"), //$NON-NLS-1$
    EDIT_PROPERTY("icons/write_obj.gif"), //$NON-NLS-1$
    IMPORT_PROJECTSET("icons/import_projectset.gif"), //$NON-NLS-1$
    IMPORT_WIZ("icons/import_wiz.gif"), //$NON-NLS-1$
    IMPORTDIR_WIZ("icons/importdir_wiz.gif"), //$NON-NLS-1$
    IMPORTPREF_WIZ("icons/importpref_wiz.gif"), //$NON-NLS-1$
    IMPORTZIP_WIZ("icons/importzip_wiz.gif"), //$NON-NLS-1$
    INCOM_SYNCH("icons/incom_synch.gif"), //$NON-NLS-1$
    INCOMPLETE_TSK("icons/incomplete_tsk.gif"), //$NON-NLS-1$
    INFO_TSK("icons/info_tsk.gif"), //$NON-NLS-1$
    INFO("icons/info.gif"), //$NON-NLS-1$
    JBOSSHOMETAB("icons/jbosshometab.gif"), //$NON-NLS-1$
    KEY("icons/key.gif"), //$NON-NLS-1$
    KEYS("icons/keys.png"), //$NON-NLS-1$
    LABEL("icons/label.png"), //$NON-NLS-1$
    LAYOUT_CO("icons/layout_co.gif"), //$NON-NLS-1$
    LINKTO_HELP("icons/linkto_help.gif"), //$NON-NLS-1$
    LOCK_OVR("icons/lock_ovr.gif"), //$NON-NLS-1$
    LOCKEDSTATE("icons/lockedstate.gif"), //$NON-NLS-1$
    LOGFILE("icons/logfile.gif"), //$NON-NLS-1$
    LOGFILETAB("icons/logfiletab.gif"), //$NON-NLS-1$
    LOGFILEVIEWER("icons/logfileviewer.gif"), //$NON-NLS-1$
    LOGOUT("icons/logout.gif"), //$NON-NLS-1$
    LPRIO_TSK("icons/lprio_tsk.gif"), //$NON-NLS-1$
    MENU("icons/menu.png"), //$NON-NLS-1$
    MENU_WHITE("icons/menu_white.png"), //$NON-NLS-1$
    MIN_VIEW("icons/min_view.gif"), //$NON-NLS-1$
    NEW_PERSP("icons/new_persp.gif"), //$NON-NLS-1$
    NEW_WIZ("icons/new_wiz.gif"), //$NON-NLS-1$
    NEWFILE_WIZ("icons/newfile_wiz.gif"), //$NON-NLS-1$
    NEWFOLDER_WIZ("icons/newfolder_wiz.gif"), //$NON-NLS-1$
    NEWPRJ_WIZ("icons/newprj_wiz.gif"), //$NON-NLS-1$
    NEWSTREAM_WIZ("icons/newstream_wiz.gif"), //$NON-NLS-1$
    NEXT_NAV("icons/next_nav.gif"), //$NON-NLS-1$
    O_LOGO_16("icons/o-logo-16.gif"), //$NON-NLS-1$
    O_LOGO_32("icons/o-logo-32.gif"), //$NON-NLS-1$
    OPENLOGFILEACTION("icons/openlogfileaction.gif"), //$NON-NLS-1$
    OUTGO_SYNCH("icons/outgo_synch.gif"), //$NON-NLS-1$
    OUTLINE_CO("icons/outline_co.gif"), //$NON-NLS-1$
    PARTICIPANT_REM("icons/participant_rem.gif"), //$NON-NLS-1$
    PARTICIPANT_REMALL("icons/participant_remall.gif"), //$NON-NLS-1$
    PASTE_EDIT("icons/paste_edit.gif"), //$NON-NLS-1$
    PASTE("icons/paste.gif"), //$NON-NLS-1$
    PHASED_OUT("icons/phased_out.gif"), //$NON-NLS-1$
    PIN_EDITOR("icons/pin_editor.gif"), //$NON-NLS-1$
    PIN_VIEW("icons/pin_view.gif"), //$NON-NLS-1$
    PIN("icons/pin.gif"), //$NON-NLS-1$
    PINNED_OVR("icons/pinned_ovr.gif"), //$NON-NLS-1$
    PREV_NAV("icons/prev_nav.gif"), //$NON-NLS-1$
    PRINT_EDIT("icons/print_edit.gif"), //$NON-NLS-1$
    PROBLEMS_VIEW("icons/problems_view.gif"), //$NON-NLS-1$
    PROGRESS_ERROR("icons/progress_error.gif"), //$NON-NLS-1$
    PROGRESS_NONE("icons/progress_none.gif"), //$NON-NLS-1$
    PROGRESS_OK("icons/progress_ok.gif"), //$NON-NLS-1$
    PROGRESS_REM("icons/progress_rem.gif"), //$NON-NLS-1$
    PROGRESS_REMALL("icons/progress_remall.gif"), //$NON-NLS-1$
    PROGRESS_STOP("icons/progress_stop.gif"), //$NON-NLS-1$
    PROGRESS_TASK("icons/progress_task.gif"), //$NON-NLS-1$
    PROP_PS("icons/prop_ps.gif"), //$NON-NLS-1$
    PVIEW("icons/pview.gif"), //$NON-NLS-1$
    REDO_EDIT("icons/redo_edit.gif"), //$NON-NLS-1$
    REFRESH_NAV("icons/refresh_nav.gif"), //$NON-NLS-1$
    REFRESH_REMOTE("icons/refresh_remote.gif"), //$NON-NLS-1$
    REFRESH("icons/refresh.gif"), //$NON-NLS-1$
    REFRESHLOGFILEACTION("icons/refreshlogfileaction.gif"), //$NON-NLS-1$
    RELEASE_RLS("icons/release_rls.gif"), //$NON-NLS-1$
    REM_CO("icons/rem_co.gif"), //$NON-NLS-1$
    ROLE("icons/role.png"), //$NON-NLS-1$
    ROLE_WHITE("icons/role_white.png"), //$NON-NLS-1$
    ROUTING_RULE("icons/routing_rule.png"), //$NON-NLS-1$
    ROUTING_RULE_WHITE("icons/routing_rule_white.png"), //$NON-NLS-1$
    SAMPLE("icons/sample.gif"), //$NON-NLS-1$
    SANDGLASS("icons/sandglass.gif"), //$NON-NLS-1$
    SAVE_EDIT("icons/save_edit.gif"), //$NON-NLS-1$
    SAVEALL_EDIT("icons/saveall_edit.gif"), //$NON-NLS-1$
    SAVEAS_EDIT("icons/saveas_edit.gif"), //$NON-NLS-1$
    SEARCH("icons/search.gif"), //$NON-NLS-1$
    BROWSE("icons/browse.png"), //$NON-NLS-1$
    SELECTED_MODE("icons/selected_mode.gif"), //$NON-NLS-1$
    SELECTOR("icons/selector.gif"), //$NON-NLS-1$
    SERVER("icons/server.gif"), //$NON-NLS-1$
    SERVERNAVIGATOR("icons/servernavigator.gif"), //$NON-NLS-1$
    SERVERNOTRUNNING("icons/servernotrunning.gif"), //$NON-NLS-1$
    SERVERRUNNING("icons/serverrunning.gif"), //$NON-NLS-1$
    SHARE_PROJECT("icons/share_project.gif"), //$NON-NLS-1$
    SHOWCHILD_MODE("icons/showchild_mode.gif"), //$NON-NLS-1$
    SHOWCOMPLETE_TSK("icons/showcomplete_tsk.gif"), //$NON-NLS-1$
    SHOWERR_TSK("icons/showerr_tsk.gif"), //$NON-NLS-1$
    SHOWTSK_TSK("icons/showtsk_tsk.gif"), //$NON-NLS-1$
    SHOWWARN_TSK("icons/showwarn_tsk.gif"), //$NON-NLS-1$
    SHUTDOWNSERVERACTION("icons/shutdownserveraction.gif"), //$NON-NLS-1$
    SITE_ELEMENT("icons/site_element.gif"), //$NON-NLS-1$
    SLEEPING("icons/sleeping.gif"), //$NON-NLS-1$
    SMALL_WARN("icons/small_warn.gif"), //$NON-NLS-1$
    SOURCESYSTEM("icons/sourcesystem.gif"), //$NON-NLS-1$
    STARTSERVERACTION("icons/startserveraction.gif"), //$NON-NLS-1$
    STEP_CURRENT("icons/step_current.gif"), //$NON-NLS-1$
    STEP_DONE("icons/step_done.gif"), //$NON-NLS-1$
    STEP_INTO("icons/stepinto.gif"), //$NON-NLS-1$
    STORED_PROCEDURE("icons/stored_procedure.png"), //$NON-NLS-1$
    STORED_PROCEDURE_WHITE("icons/stored_procedure_white.png"), //$NON-NLS-1$
    SUBMENU("icons/submenu.gif"), //$NON-NLS-1$
    SUBSCRIPTION_ENGINE("icons/event_management.png"), //$NON-NLS-1$
    SUBSCRIPTION_ENGINE_WHITE("icons/event_management_white.png"), //$NON-NLS-1$
    EVENTM_ANAGEMENT("icons/Events_management.png"), //$NON-NLS-1$
    SYNCED("icons/synced.gif"), //$NON-NLS-1$
    SYNCH_PARTICIPANTS("icons/synch_participants.gif"), //$NON-NLS-1$
    TALEND_PICTO_SMALL("icons/appli_16x16.gif"), //$NON-NLS-1$
    TALEND_PICTO("icons/appli_16x16.gif"), //$NON-NLS-1$
    TARGETSYSTEM("icons/targetsystem.gif"), //$NON-NLS-1$
    TASKMRK_TSK("icons/taskmrk_tsk.gif"), //$NON-NLS-1$
    TASKS_TSK("icons/tasks_tsk.gif"), //$NON-NLS-1$
    TERMINATESERVERACTION("icons/terminateserveraction.gif"), //$NON-NLS-1$
    THEME_CATEGORY("icons/theme_category.gif"), //$NON-NLS-1$
    THIN_CLOSE_VIEW("icons/thin_close_view.gif"), //$NON-NLS-1$
    THIN_HIDE_TOOLBAR("icons/thin_hide_toolbar.GIF"), //$NON-NLS-1$
    THIN_MAX_VIEW("icons/thin_max_view.gif"), //$NON-NLS-1$
    THIN_MIN_VIEW("icons/thin_min_view.gif"), //$NON-NLS-1$
    THIN_RESTORE_VIEW("icons/thin_restore_view.GIF"), //$NON-NLS-1$
    THIN_SHOW_TOOLBAR("icons/thin_show_toolbar.GIF"), //$NON-NLS-1$
    THIN_VIEW_MENU("icons/thin_view_menu.GIF"), //$NON-NLS-1$
    TOOLBAR("icons/toolbar.gif"), //$NON-NLS-1$
    TRANSFORMER("icons/transformer.png"), //$NON-NLS-1$
    TRANSFORMER_WHITE("icons/transformer_white.png"), //$NON-NLS-1$
    // TRASH("icons/trash.gif"),//$NON-NLS-1$
    TREE_MODE("icons/tree_mode.gif"), //$NON-NLS-1$
    TREE_OBJECT("icons/tree_object.gif"), //$NON-NLS-1$
    TYPE_DEFINITION("icons/type_definition.gif"), //$NON-NLS-1$
    UNDO_EDIT("icons/undo_edit.gif"), //$NON-NLS-1$
    UNIQUE("icons/unique.gif"), //$NON-NLS-1$
    UNPROPERTY_OBJ_AUTRE_COPIE("icons/unproperty_obj (autre copie).gif"), //$NON-NLS-1$
    UNPROPERTY_OBJ("icons/unproperty_obj.gif"), //$NON-NLS-1$
    UP_NAV("icons/up_nav.gif"), //$NON-NLS-1$
    VERSION_CONTROLLED("icons/version_controlled.gif"), //$NON-NLS-1$
    VERSIONING("icons/versioning.gif"), //$NON-NLS-1$
    VIEW_MENU("icons/view_menu.gif"), //$NON-NLS-1$
    VIEW("icons/view.png"), //$NON-NLS-1$
    VIEW_WHITE("icons/view_white.png"), //$NON-NLS-1$
    WAITING_OVR("icons/waiting_ovr.gif"), //$NON-NLS-1$
    WAITING("icons/waiting.gif"), //$NON-NLS-1$
    WARN_TSK("icons/warn_tsk.gif"), //$NON-NLS-1$
    WARNING_CO("icons/warning_co.gif"), //$NON-NLS-1$
    WILDCARD("icons/wildcard.gif"), //$NON-NLS-1$
    WORKSET_WIZ("icons/workset_wiz.gif"), //$NON-NLS-1$
    WRITABLE("icons/writable.gif"), //$NON-NLS-1$
    XPATH("icons/xpath.gif"), //$NON-NLS-1$
    XTENTIS_SERVER("icons/xtentis_server.gif"), //$NON-NLS-1$
    RUN_EXC("icons/run_exc.gif"), //$NON-NLS-1$
    RUNLAST_CO("icons/runlast_co.gif"), //$NON-NLS-1$
    STOP("icons/stop.gif"), //$NON-NLS-1$
    SUSPEND("icons/suspend_co.gif"), //$NON-NLS-1$
    DOTS_BUTTON("icons/dots_button.gif"), //$NON-NLS-1$
    ZAP("icons/zap.gif"), //$NON-NLS-1$
    ROUTINE("icons/routine.gif"), //$NON-NLS-1$
    PROCESS("icons/process.png"), //$NON-NLS-1$
    JOB("icons/process_icon.gif"), //$NON-NLS-1$
    SERVICE_CONFIGURATION("icons/service_config.png"), //$NON-NLS-1$
    SERVICE_CONFIGURATION_WHITE("icons/service_config_white.png"), //$NON-NLS-1$
    WORKFLOW_PROCESS("icons/workflow_process.png"), //$NON-NLS-1$
    WORKFLOW_PROCESS_WHITE("icons/workflow_process_white.png"), //$NON-NLS-1$
    RENAME("icons/rename.png"), //$NON-NLS-1$
    RESOURCES("icons/resources.png"), //$NON-NLS-1$
    RESOURCES_WHITE("icons/resources_white.png"), //$NON-NLS-1$
    PRIMARYKEY("icons/primarykey.png"), //$NON-NLS-1$
    SECURITYANNOTATION("icons/securityannotation.png"), //$NON-NLS-1$
    SIMPLETYPE("icons/simple_type.png"), //$NON-NLS-1$
    KEYINFO("icons/info.gif"), //$NON-NLS-1$
    SCHEMAELEMENT("icons/schema_element.png"), //$NON-NLS-1$
    EXPAND("icons/expand.png"), //$NON-NLS-1$
    COLLAPSE("icons/collapse.png"), //$NON-NLS-1$
    REGISTER_WIZ("icons/register_wiz.png"), //$NON-NLS-1$
    BROWSE_MENU("icons/browse_menu.png"), //$NON-NLS-1$
    CROSSREF("icons/crossref.png"), //$NON-NLS-1$
    HIER_VIEW("icons/hier_view.gif"), //$NON-NLS-1$
    MANAGE_USERS("icons/manage_users.png"), //$NON-NLS-1$
    REPORTING("icons/reporting.png"), //$NON-NLS-1$
    TRASH("icons/trash.png"), //$NON-NLS-1$
    UPDATEREPORT("icons/updatereport.png"), //$NON-NLS-1$
    WORKFLOWTASKS("icons/workflowtasks.png"), //$NON-NLS-1$
    SORT_ASC("icons/hmenu-asc.gif"), //$NON-NLS-1$
    SORT_DESC("icons/hmenu-desc.gif"), //$NON-NLS-1$
    INTENT("icons/indent.png");//$NON-NLS-1$

    EImage(String path) {
        this.path = path;

    }

    EImage() {

    }

    String path;

    public String getPath() {
        return path;
    }

    public static Map<String, EImage> getAlllEimages() {
        Map<String, EImage> map = new HashMap<String, EImage>();
        for (int i = 0; i < values().length; i++) {
            map.put(values()[i].getPath(), values()[i]);
        }
        return map;
    }
}
