package com.example.filemanagmentusingthread.Repository;

import com.example.filemanagmentusingthread.Model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileRecord,Long> {
}
