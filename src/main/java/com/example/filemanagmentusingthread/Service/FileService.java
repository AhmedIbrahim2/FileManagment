package com.example.filemanagmentusingthread.Service;


import com.example.filemanagmentusingthread.Model.FileRecord;
import com.example.filemanagmentusingthread.Repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;

    public final BlockingQueue<FileRecord> blockingQueue = new LinkedBlockingQueue<>();
    public final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final static String filePath1 = "aboali1.txt";
    Resource resource = new ClassPathResource("aboali1.txt"); // File in src/main/resources

    public void saveDataFromFile(String filePath){

        executorService.submit(()->{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
                String line ;
                while ((line = reader.readLine()) != null){
                    String[] data =line.split(",");
                    FileRecord fileRecord = new FileRecord();
                    fileRecord.setASub(data[0]);
                    fileRecord.setBSub(data[1]);
                    fileRecord.setDuration(Integer.parseInt(data[2]));
                    blockingQueue.put(fileRecord);
                }
                // Add a marker to signal the end of the file
                FileRecord markerFile = new FileRecord("0","0",0);
                blockingQueue.put(markerFile);


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        executorService.submit(()->{
            try {


                while (true) {
                    FileRecord fileRecord = blockingQueue.take();
                    if (fileRecord.getASub().equals("0") && fileRecord.getBSub().equals("0") && fileRecord.getDuration() == 0) {
                        break;
                    }
                    fileRepository.save(fileRecord);
                }
                System.out.println("Data Has Been Saved Successfully");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
