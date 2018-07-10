package com.rxlogix.cmis;


import java.io.File;
import java.util.List;

public interface AdapterInterface {
    void init(Object settings);
    void load(File reportFile, String subfolder, String name, String description, String tag, String sensitivity, String author);
    List<String> getFolderList(String folder);
    List<String> getFileList(String folder);

}
