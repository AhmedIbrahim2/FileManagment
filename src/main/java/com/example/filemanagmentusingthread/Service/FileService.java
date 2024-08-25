package com.example.filemanagmentusingthread.Service;


import com.example.filemanagmentusingthread.Model.FileRecord;
import com.example.filemanagmentusingthread.Repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;

    public final BlockingQueue<FileRecord> blockingQueue = new LinkedBlockingQueue<>();
    public final ExecutorService executorService = Executors.newFixedThreadPool(2);

    final static String filePath1 = "aboali1.txt";
    Resource resource = new ClassPathResource("aboali1.txt"); // File in src/main/resources

    private Path path1 = Paths.get("D:\\workspace\\aboali\\aboali1.txt");


    private long lastKnownFileSize = 0; // To keep track of the last known file size

    // Method to check file size and save data if it has changed
//    @Scheduled(fixedRate = 60000) // Schedule the job to run every 1 minute (60000 ms)
//    public void checkFileSizeAndSaveData() {
//        try {
//            Path path = Paths.get(resource.getURI());
//            long currentFileSize = Files.size(path);
//
//            if (currentFileSize != lastKnownFileSize) {
//                System.out.println("File size has changed, saving data...");
//                lastKnownFileSize = currentFileSize;
//                saveDataFromFile(filePath1);
//            } else {
//                System.out.println("File size has not changed.");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error checking file size", e);
//        }
//    }
    public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public FileService() {
        // Schedule the task to check the file size every 1 minute
        scheduler.scheduleAtFixedRate(this::checkFileSizeAndSaveData, 0, 1, TimeUnit.MINUTES);
    }
    public void checkFileSizeAndSaveData() {
        try {
       //     Path path = Paths.get(resource.getURI());
            long currentFileSize = Files.size(path1);

            long lineCount;
            try (BufferedReader reader = Files.newBufferedReader(path1)) {
                lineCount = reader.lines().count();
            }
           System.out.println("Current file size: " + currentFileSize);
           System.out.println("Last known file size: " + lastKnownFileSize);
            System.out.println("Number of lines in the file: " + lineCount);

            System.out.println("Check performed at: " + java.time.LocalTime.now());


            if (currentFileSize != lastKnownFileSize) {
                System.out.println("File size has changed, saving data...");
                lastKnownFileSize = currentFileSize;
                saveDataFromFile(path1.toString());
            } else {
                System.out.println("File size has not changed.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error checking file size", e);
        }
    }
    public void saveDataFromFile(String filePath){

        executorService.submit(()->{
            try (BufferedReader reader = Files.newBufferedReader(path1)){
                String line ;
                while ((line = reader.readLine()) != null){
                    String[] data =line.split(",");
                    FileRecord fileRecord = new FileRecord();
                    fileRecord.setASub(data[0]);
                    fileRecord.setBSub(data[1]);
                    fileRecord.setDuration(Integer.parseInt(data[2]));
                    blockingQueue.put(fileRecord);
                }
                if (blockingQueue.size() >= 200) {
                        processBatch();
                    }

                // Process remaining records
                processBatch();
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

    private void processBatch() {
        List<FileRecord> batch = new ArrayList<>();
        blockingQueue.drainTo(batch, 200); // Drain up to 100 records from the queue
        if (!batch.isEmpty()) {
            fileRepository.saveAll(batch); // Bulk save
        }
    }
}
