package com.example.filemanagmentusingthread.Model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String  aSub;
    private String bSub;
    private int Duration ;

    public FileRecord(String i, String i1, int i2) {
        this.aSub = i ;
        this.bSub = i1;
        this.Duration = i2;
    }
}
