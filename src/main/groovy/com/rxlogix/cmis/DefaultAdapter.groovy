package com.rxlogix.cmis

import org.apache.chemistry.opencmis.client.api.*
import org.apache.chemistry.opencmis.client.runtime.DocumentImpl
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl
import org.apache.chemistry.opencmis.commons.PropertyIds
import org.apache.chemistry.opencmis.commons.SessionParameter
import org.apache.chemistry.opencmis.commons.data.ContentStream
import org.apache.chemistry.opencmis.commons.enums.BindingType
import org.apache.chemistry.opencmis.commons.enums.VersioningState

class DefaultAdapter implements AdapterInterface {

    Session session
    def settings

    def mimetypes = [
            'txt' : 'text/plain; charset=UTF-8',
            'pdf' : 'application/pdf',
            'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
            'rtf' : 'application/rtf']


    @Override
    void init(def _settings) {
        settings = _settings
        createSession()
    }

    @Override
    void load(File reportFile, String subfolder, String name, String description, String tag, String sensitivity, String author) {
        if (!session)
            createSession()

        String path = settings.rootFolder + (subfolder ? ("/" + subfolder) : "");
        CmisObject cmisObject = session.getObjectByPath(path)

        Folder folder = (Folder) cmisObject;
        Document document = null
        String extension = reportFile.name.substring(reportFile.name.indexOf(".") + 1)
        String docName=name+"."+extension
        for (CmisObject child : folder.getChildren()) {
            if (child.getName() == docName && child instanceof Document) document = (Document) child
        }

        String mimetype = mimetypes[extension];
        byte[] contentBytes = reportFile.getBytes()
        ByteArrayInputStream stream = new ByteArrayInputStream(contentBytes);

        ContentStream contentStream = session.getObjectFactory().createContentStream(reportFile.name, contentBytes.length, mimetype, stream);

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(PropertyIds.OBJECT_TYPE_ID, settings.documentTypeId);

        properties.put(settings.descriptionId, description);
        properties.put(settings.nameId, docName);
        if (settings.authorId)
            properties.put(settings.authorId, author);
        if (settings.tagId && tag)
            properties.put(settings.tagId, tag.split(",") as List);
        if (settings.sensitivityId)
            properties.put(settings.sensitivityId, sensitivity);

        if (!document) {
            document = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
        } else {
            ObjectId pwcObjectId = document.checkOut();
            Document pwc = (Document) session.getObject(pwcObjectId);
            pwc.setContentStream(contentStream, true);
            ObjectId idOfNewLastVersion = pwc.checkIn(true, properties, null, null);
        }
    }

    @Override
    List<String> getFolderList(String folder) {
        if (!session)
            createSession()
        String folderPath = settings.rootFolder + (folder ? ("/" + folder) : "");
        CmisObject cmisObject = session.getObjectByPath(folderPath)
        List<String> folders = []
        Folder dmsFolder = (Folder) cmisObject;
        dmsFolder.getChildren().each {
            if (it instanceof Folder)
                folders << ((Folder) it).getName();
        }
        return folders.sort()
    }

    @Override
    List<String> getFileList(String folder) {
        if (!session)
            createSession()
        String folderPath = settings.rootFolder + (folder ? ("/" + folder) : "");
        CmisObject cmisObject = session.getObjectByPath(folderPath)
        List<String> folders = []
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${cmisObject}"
        Folder dmsFolder = (Folder) cmisObject;
        println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>${dmsFolder.name}"
        dmsFolder.getChildren().each {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>it>>>>>${it.name}"
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>it>>>>>${it.class}"
            if (it instanceof DocumentImpl)
                folders << ((DocumentImpl) it).getName();
        }
        return folders.sort()
    }

    def createSession() {
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put(SessionParameter.USER, settings.login);
        parameters.put(SessionParameter.PASSWORD, settings.password);

        parameters.put(SessionParameter.BROWSER_URL, settings.cmisUrl);
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
        parameters.put(SessionParameter.REPOSITORY_ID, settings.repositoryId);

        session = factory.createSession(parameters);
    }
}
