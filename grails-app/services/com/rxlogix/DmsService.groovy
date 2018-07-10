package com.rxlogix

import com.rxlogix.cmis.AdapterFactory
import com.rxlogix.cmis.AdapterInterface
import com.rxlogix.config.ApplicationSettings
import grails.converters.JSON

class DmsService {

    def dynamicReportService
    AdapterInterface adapter
//    String json = '{ \n    "dmsName":"alfresco",\n    "login":"admin",\n    "password":"admin",\n    "cmisUrl":"http://10.100.22.143:7080/alfresco/api/-default-/public/cmis/versions/1.1/browser",\n    "repositoryId":"-default-",\n    "rootFolder":"/Published",\n    "documentTypeId":"cmis:document",\n    "nameId":"cmis:name",\n    "descriptionId":"cmis:description"\n}'
    String json = '{ \n    "dmsName":"alfresco",\n    "login":"admin",\n    "password":"admin",\n    "cmisUrl":"http://127.0.0.1:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser",\n    "repositoryId":"-default-",\n    "rootFolder":"/Published",\n    "documentTypeId":"cmis:document",\n    "nameId":"cmis:name",\n    "descriptionId":"cmis:description"\n}'

    def upload(File reportFile, String subfolder, String name, String description, String tag, String sensitivity, String author) {
        if (!adapter) {
            def settings = JSON.parse(json)
            adapter = AdapterFactory.getAdapter(settings);
        }
        adapter.load(reportFile, subfolder, name, description, tag, sensitivity, author)
    }

    def clear() {
        adapter = null
    }

    List<String> getFolderList(String folder) {
        if (!adapter) {
            def settings = JSON.parse(json)
            adapter = AdapterFactory.getAdapter(settings);
        }
        adapter.getFolderList(folder)
    }

    List<String> getFileList(String folder) {
        if (!adapter) {
            def settings = JSON.parse(json)
            adapter = AdapterFactory.getAdapter(settings);
        }
        adapter.getFileList(folder)
    }
}
