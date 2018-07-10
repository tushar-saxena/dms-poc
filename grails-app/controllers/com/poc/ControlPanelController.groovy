package com.poc

import com.rxlogix.cmis.AdapterFactory
import com.rxlogix.config.ApplicationSettings
import com.rxlogix.dto.AjaxResponseDTO
import com.rxlogix.dto.ResponseDTO
import grails.converters.JSON
import grails.validation.ValidationException
import org.apache.commons.io.FileUtils

class ControlPanelController {
    static final String TEMP_FILE_NAME = "test.txt"
//    static final String TEMP_FILE_NAME = "j1_1.docx"

    def dmsService
    def applicationSettingsService

    def saveDmsSettings() {
        AjaxResponseDTO responseDTO = new AjaxResponseDTO()
        String val=params.dmsSettings?.trim()
        try {
            if (!val) {
                storeDmsConfig("")
                responseDTO.setSuccessResponse("Success")
            } else {
                def settings = JSON.parse(val)
                if (!settings) {
                    responseDTO.setFailureResponse("Test")
                } else {
                    dmsService.adapter = AdapterFactory.getAdapter(settings);
                    if (sendTestDocument(responseDTO)) {
                        storeDmsConfig(val)
                        responseDTO.setSuccessResponse("Test")
                    }
                }
            }

        } catch (Exception e) {
            responseDTO.setFailureResponse(e, 'error')
        }
        render(responseDTO.toAjaxResponse())
    }

    private storeDmsConfig(String val) {
        ApplicationSettings applicationSettings = ApplicationSettings.first()
        applicationSettings.dmsIntegration = val
        applicationSettings.save()
        dmsService.clear()
        applicationSettingsService.reload()
    }

    private boolean sendTestDocument(AjaxResponseDTO responseDTO ){
        File testFile
        try {
            String TEMP_FILE_NAME = "a.txt";
            testFile = new File(System.getProperty("user.home") + "/upload/" + TEMP_FILE_NAME);
//            testFile.createNewFile();
//            FileUtils.writeStringToFile(testFile, "This is test content.");
            dmsService.upload(testFile, "riskmanager", "Validated Signal", "Test Description", "Test", "Sensitive", "Tushar")
            responseDTO.setSuccessResponse(null, "Success")
        } catch (Exception e) {
            responseDTO.setFailureResponse(e, "error" as String)
            return false
        }
        finally {
            testFile?.delete()
        }
        return true
    }
    def testDmsSettings() {
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>i called"
        AjaxResponseDTO responseDTO = new AjaxResponseDTO()
        sendTestDocument(responseDTO)
        render(responseDTO.toAjaxResponse())
    }

    def listFiles() {
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>i called"
        List list = dmsService.getFileList("riskmanager")
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${list}"
        render(list)
    }

    String defaultDmsSettings = """
    {
    "dmsName":"alfresco",
    "login":"admin",
    "password":"admin",
    "cmisUrl":"http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser",
    "repositoryId":"-default-",
    "rootFolder":"/Published",
    "authorId":"cm:author",
    "documentTypeId":"D:d3:d3",
    "nameId":"cmis:name",
    "descriptionId":"cmis:description",
    "sensitivityId":"d3:priv2",
    "tagId":"d3:tag2"
    }
    """
}
