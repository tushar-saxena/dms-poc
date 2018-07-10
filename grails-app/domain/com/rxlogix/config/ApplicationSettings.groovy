package com.rxlogix.config

import com.rxlogix.util.DbUtil

/**
 * Contains application settings configured via Control Panel
 */

class ApplicationSettings {

    Boolean showJapaneseReportFields = false        //show or hide Japanese ReportFields in Template/Query CRUD form
    String dmsIntegration //enables integration with Document Management System

    //Standard fields
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String createdBy = "Application"
    String modifiedBy = "Application"
    String defaultUiSettings

    static mapping = {
        table name: "APPLICATION_SETTINGS"

        showJapaneseReportFields column: "SHOW_JAPANESE_REPORT_FIELDS"
        dmsIntegration column: "DMS_INTEGRATION"
        defaultUiSettings  column: "DEFAULT_UI_SETTINGS", sqlType: DbUtil.longStringType
    }

    static constraints = {
        createdBy(nullable: false, maxSize: 20)
        dmsIntegration(nullable: true, maxSize:4000)
        modifiedBy(nullable: false, maxSize: 20)
        defaultUiSettings(nullable: true)
    }

}
