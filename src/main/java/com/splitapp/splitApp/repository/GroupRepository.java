package com.splitapp.splitApp.repository;

import com.splitapp.splitApp.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByMembersId(Long userId);
    List<Group> findByOwnerId(Long ownerId);
}