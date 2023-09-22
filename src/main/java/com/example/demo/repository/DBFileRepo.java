package com.example.demo.repository;

import com.example.demo.model.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBFileRepo extends JpaRepository<DBFile, String> {
}
