package com.amr.chatservice.service.upload;

import com.amr.chatservice.utils.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.stream.Stream;

@Log4j2
@Service
public class FileUploadService implements FilesStorageService {
    private String storagePath = null;
    private String currentFolder = null;
    private static StringBuilder builder = new StringBuilder();
    private Path root = null;

    public FileUploadService() {
        init();
    }

    private String getFolderInfo() {
        builder.setLength(0);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        builder.append(year).append(month).append(day);
        return builder.toString();
    }

    private void getFolderUpload() {
        this.storagePath = Constant.UPLOAD_PATH;
        this.root = Paths.get(this.storagePath);
    }

    private void createFolder() {
        String folderDate = getFolderInfo();
        File folder = new File(this.storagePath + File.separator + folderDate);
        if (!isExistFolder()) {
            folder.mkdirs();
            log.info("Not exist folder, create it now " + folderDate);
        }
        log.info("Folder " + folderDate + " created!");
    }

    private boolean isExistFolder() {
        String folderDate = getFolderInfo();
        File folder = new File(this.storagePath + File.separator + folderDate);
        this.currentFolder = this.storagePath + File.separator + folderDate;
        return folder.exists() && folder.isDirectory();
    }

    @Override
    public void init() {
        try {
            getFolderUpload();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        createFolder();
        String fileFinal;
        try {
            String filename = RandomStringUtils.randomAlphanumeric(20);
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            fileFinal = this.currentFolder + File.separator + filename + "." + extension;
            File source = new File(fileFinal);
            file.transferTo(source);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        return fileFinal;
    }

    @Override
    public String saveAndReturnPath(MultipartFile file) {
        createFolder();
        String filePathServer;
        try {
            String filename = RandomStringUtils.randomAlphanumeric(20);
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            filePathServer = getFolderInfo() + File.separator + filename + "." + extension;
            String fileFinal = this.currentFolder + File.separator + filename + "." + extension;
            File source = new File(fileFinal);
            file.transferTo(source);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        return filePathServer;
    }


    @Override
    public String createFilePath(String fileName, String extension) {
        String random = RandomStringUtils.randomAlphanumeric(20);
        return getFolderInfo() + File.separator + random + fileName + "." + extension;
    }

    @Override
    public String saveAndReturnPathV2(MultipartFile file) {
        createFolder();
        String filePathServer;
        try {
            String filename = RandomStringUtils.randomAlphanumeric(20);
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            filePathServer = getFolderInfo() + File.separator + filename + "." + extension;
            String fileFinal = this.storagePath + File.separator + filename + "." + extension;
            File source = new File(fileFinal);
            if(!source.exists() && !source.mkdirs()){
                log.error("Can't not create folder!");
            }
            file.transferTo(source);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        return filePathServer;
    }

    @Override
    public String copyFile(String pathFileOld, String extension) {
        String filePathServe;
        String pathServer = this.storagePath;
        String namePathFileOld = pathServer + File.separator + pathFileOld;
        File fileOld = new File(namePathFileOld);
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(20);
        String filePathFinal = this.currentFolder + File.separator + randomAlphanumeric + "." + extension;
        File newFile = new File(filePathFinal);
        filePathServe = getFolderInfo() + File.separator + randomAlphanumeric + "." + extension;
        try {
            FileUtils.copyFile(fileOld, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePathServe;
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
